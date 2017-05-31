package cz.muni.fi.pv168.brewerymanager.gui;

import cz.muni.fi.pv168.brewerymanager.backend.Data;
import cz.muni.fi.pv168.brewerymanager.backend.Keg;
import cz.muni.fi.pv168.brewerymanager.backend.KegManager;
import cz.muni.fi.pv168.brewerymanager.backend.WorkManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author adam
 */
public class KegTableModel extends AbstractTableModel {
    
    private static final ResourceBundle texts = ResourceBundle.getBundle("gui/texts");
    private static List<Keg> kegs = new ArrayList<>();
    final static Logger log = LoggerFactory.getLogger(KegTableModel.class.getName());
    
    public KegTableModel(KegTableModel kegTableModel) {
        RetrieveSwingWorker retrieveSwingWorker = new RetrieveSwingWorker(kegTableModel);
        retrieveSwingWorker.execute();
    }
    
    private static class CreateSwingWorker extends SwingWorker<Void,Void> {
        private final Keg keg;
        private final KegManager kegManager;
        private final WeakReference<KegTableModel> tableModel;
        
        public CreateSwingWorker(Keg keg, KegTableModel kegModel) {
            this.keg = keg;
            this.kegManager = Data.getInstance().getKegManager();
            this.tableModel = new WeakReference<>(kegModel);
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            log.info("Adding keg:" + keg);            
            kegManager.createKeg(keg);
            kegs.add(keg);
            
            return null;
        }
        
        @Override    
        protected void done() {
            KegTableModel kegTableModel = tableModel.get();
            if (kegTableModel != null) {
            int lastRow = kegTableModel.getRowCount() - 1;
            kegTableModel.fireTableRowsInserted(lastRow, lastRow);
            }
        }
    }
    
    private static class RetrieveSwingWorker extends SwingWorker<Void,Void> {  
        private final KegManager kegManager;
        private final WeakReference<KegTableModel> tableModel;        
        
        public RetrieveSwingWorker(KegTableModel kegModel){
            this.tableModel = new WeakReference<>(kegModel);
            kegManager = Data.getInstance().getKegManager();
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            
            kegs = kegManager.findAllKegs();
            log.info("Retrieving all kegs:" + kegs);
            return null;
        }
        
        @Override    
        protected void done() {
            KegTableModel kegTableModel = tableModel.get();
            if (kegTableModel != null) {            
            kegTableModel.fireTableRowsInserted(0, kegTableModel.getRowCount() - 1);
            }
        }
    }
    
    private static class UpdateSwingWorker extends SwingWorker<Void,Void> {
        private final Keg keg;
        private final int rowIndex;
        private final int columnIndex;
        private final KegManager kegManager;
        private final WeakReference<KegTableModel> tableModel;       
        
        public UpdateSwingWorker(Keg keg, int rowIndex, int columnIndex, KegTableModel kegModel) {
           this.tableModel = new WeakReference<>(kegModel);
           kegManager = Data.getInstance().getKegManager();
            this.keg = keg;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            log.info("Updating keg" + keg);
            kegManager.updateKeg(keg);
            kegs.set(rowIndex, keg);
            return null;
        }
        
        @Override    
        protected void done() {
            KegTableModel kegModel = tableModel.get();
            if (kegModel != null){
            kegModel.fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }
    
    private static class DeleteSwingWorker extends SwingWorker<Void,Void> {        
        private final int row;
        private final KegManager kegManager;
        private final WorkManager workManager;
        private final WeakReference<KegTableModel> tableModel;
        
        public DeleteSwingWorker(int row, KegTableModel kegModel) {
            this.row = row;
           this.kegManager = Data.getInstance().getKegManager();
           this.workManager = Data.getInstance().getWorkManager();
           this.tableModel = new WeakReference<>(kegModel);
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            Long id = kegs.get(row).getId();
            if (workManager.kegExists(id)){
                log.info("Keg exists in table works, you must delete it first");
                return null;
            }
            Keg keg = kegManager.getKeg(id);
            log.info("Deleting keg: " + keg);
            kegManager.deleteKeg(keg);
            kegs.remove(row);            
            return null;
        }
        
        @Override    
        protected void done() {
            KegTableModel kegModel = tableModel.get();
            if (kegModel != null){
            kegModel.fireTableRowsDeleted(row, row);
            }
        }
    }
 
    @Override
    public int getRowCount() {
        return kegs.size();
    }
 
    @Override
    public int getColumnCount() {
        return 5;
    }
 
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Keg keg = kegs.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return keg.getBrand(); 
            case 1:
                return keg.getId();
            case 2:
                return keg.getCapacity();
            case 3:
                return keg.getPrice();
            case 4:
                return keg.getDeposit();         
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return texts.getString("BRAND");   
            case 1:
                return texts.getString("ID");
            case 2:
                return texts.getString("CAPACITY");
            case 3:
                return texts.getString("PRICE");
            case 4:
                return texts.getString("DEPOSIT");        
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Long.class;
            case 2:
                return Integer.class;
            case 3:
                return Integer.class;
            case 4:
                return Integer.class;
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Keg keg = kegs.get(rowIndex);
        switch (columnIndex) {
            case 0:
                keg.setBrand((String) aValue);
                break;
            case 1:
                keg.setId((Long) aValue);
                break;
            case 2:
                keg.setCapacity((Integer) aValue);
                break;
            case 3:
                keg.setPrice((Integer) aValue);
                break;
            case 4:
                keg.setDeposit((Integer) aValue);
                break;               
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
      //  UpdateSwingWorker updateSwingWorker = new UpdateSwingWorker(keg, rowIndex, columnIndex);
      //  updateSwingWorker.execute();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
                return false;
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }
    
    public void addRow(Keg keg, KegTableModel kegTableModel) {
        CreateSwingWorker createSwingWorker = new CreateSwingWorker(keg, kegTableModel);
        createSwingWorker.execute();
    }
    
    public void removeRow(int row, KegTableModel kegTableModel) {
        DeleteSwingWorker deleteSwingWorker = new DeleteSwingWorker(row, kegTableModel);
        deleteSwingWorker.execute();
    }
}
