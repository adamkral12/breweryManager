package cz.muni.fi.pv168.brewerymanager.gui;

import cz.muni.fi.pv168.brewerymanager.backend.Employee;
import cz.muni.fi.pv168.brewerymanager.backend.EmployeeManager;
import cz.muni.fi.pv168.brewerymanager.backend.Keg;
import cz.muni.fi.pv168.brewerymanager.backend.KegManager;
import cz.muni.fi.pv168.brewerymanager.backend.Data;
import cz.muni.fi.pv168.brewerymanager.backend.Work;
import cz.muni.fi.pv168.brewerymanager.backend.WorkManager;
import static cz.muni.fi.pv168.brewerymanager.gui.EmployeeTableModel.log;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author adam
 */
public class WorkTableModel extends AbstractTableModel {

    private static final ResourceBundle texts = ResourceBundle.getBundle("gui/texts");

    private static List<Work> works = new ArrayList<>();

    public WorkTableModel(WorkTableModel workModel) {

        RetrieveSwingWorker retrieveSwingWorker = new RetrieveSwingWorker(workModel);
        retrieveSwingWorker.execute();
    }

    private static  class CreateSwingWorker extends SwingWorker<Void, Void> {

        private final Work work;
        private final Employee employee;
        private final Keg keg;
        private final WorkManager workManager;
        private final EmployeeManager employeeManager;
        private final KegManager kegManager;
        private final WeakReference<WorkTableModel> tableModel;
        
        public CreateSwingWorker(Work work, Long employeeId, Long kegId, WorkTableModel workModel) {
            this.workManager = Data.getInstance().getWorkManager();
            this.employeeManager = Data.getInstance().getEmployeeManager();
            this.kegManager = Data.getInstance().getKegManager();
            this.keg = kegManager.getKeg(kegId);
            this.employee = employeeManager.getEmployee(employeeId);
            this.tableModel = new WeakReference<>(workModel);
            this.work = work;
        }

        @Override
        protected Void doInBackground() throws Exception {
            
            Long workId = workManager.startWorkingWithKeg(employee, keg);
            Work work2 = workManager.getWork(workId);
            works.add(work2);
            log.info("Starting work:" + work2);
            return null;
        }

        @Override
        protected void done() {
            WorkTableModel workModel = tableModel.get();
            if (workModel != null){
            int lastRow = workModel.getRowCount() - 1;
            workModel.fireTableRowsInserted(lastRow, lastRow);
            }
        }
    }

    private static class RetrieveSwingWorker extends SwingWorker<Void, Void> {

        private final WorkManager workManager;
        private final WeakReference<WorkTableModel> tableModel;        
        
        public RetrieveSwingWorker(WorkTableModel workModel){
            this.tableModel = new WeakReference<>(workModel);
            workManager = Data.getInstance().getWorkManager();
        }        
        @Override
        protected Void doInBackground() throws Exception {
            works = workManager.findAllWorks();
            log.info("Retrieving all works:" + works);
            return null;
        }

        @Override
        protected void done() {
            WorkTableModel workModel = tableModel.get();
            if (workModel != null){
            workModel.fireTableRowsInserted(0, workModel.getRowCount() - 1);                
            }

        }
    }

    private static class UpdateSwingWorker extends SwingWorker<Void, Void> {

        private final Work work;
        private final int rowIndex;
        private final int columnIndex;
        private final WorkManager workManager;
        private final WeakReference<WorkTableModel> tableModel; 
        
        public UpdateSwingWorker(Work work, int rowIndex, int columnIndex, WorkTableModel workModel) {
            this.work = work;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            this.tableModel = new WeakReference<>(workModel);
            workManager = Data.getInstance().getWorkManager();           
        }

        @Override
        protected Void doInBackground() throws Exception {
            works.set(rowIndex, work);
            log.info("Updating work: " + work);
            return null;
        }

        @Override
        protected void done() {
            WorkTableModel workModel = tableModel.get();
            if (workModel != null){
            workModel.fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    private static class EndSwingWorker extends SwingWorker<Void, Void> {

        private Work work;
        private final int rowIndex;
        private final Long id;
        private final WorkManager workManager;
        private final WeakReference<WorkTableModel> tableModel; 
        
        public EndSwingWorker(int rowIndex, WorkTableModel workModel) {
            this.workManager = Data.getInstance().getWorkManager();
            this.tableModel = new WeakReference<>(workModel);        
            this.rowIndex = rowIndex;
            id = works.get(rowIndex).getId();
            work = workManager.getWork(id);
        }

        @Override
        protected Void doInBackground() throws Exception {
            log.info("Ending work:" + work);            
            workManager.endWork(work);
            work = workManager.getWork(id);
            works.set(rowIndex, work);
            return null;
        }

        @Override
        protected void done() {
            WorkTableModel workModel = tableModel.get();
            if (workModel != null){
            workModel.fireTableCellUpdated(rowIndex, 6);
            workModel.fireTableCellUpdated(rowIndex, 7);
            }

        }
    }

    private static class DeleteSwingWorker extends SwingWorker<Void, Void> {

        private final int row;
        private final WorkManager workManager;
        private final KegManager kegManager;
        private final EmployeeManager employeeManager;        
        private final WeakReference<WorkTableModel> tableModel; 
        
        public DeleteSwingWorker(int row, WorkTableModel workModel) {
            this.row = row;
            workManager = Data.getInstance().getWorkManager();
            employeeManager = Data.getInstance().getEmployeeManager();
            kegManager = Data.getInstance().getKegManager();
            tableModel = new WeakReference<>(workModel);
        }

        @Override
        protected Void doInBackground() throws Exception {
            Long id = works.get(row).getId();
            Work work = workManager.getWork(id);
            log.info("Deleting  work: " + work);
            workManager.deleteWork(work);
            works.remove(row);            
            return null;
        }

        @Override
        protected void done() {
            WorkTableModel workModel = tableModel.get();
            if (workModel != null){
            workModel.fireTableRowsDeleted(row, row);
            }
        }
    }

    @Override
    public int getRowCount() {
        return works.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Work work = works.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return work.getId();
            case 1:
                return work.getEmployeeId();
           case 2:
                return work.getKegId();
            case 3:
                return work.getStartDate();
            case 4:
                return work.getStartTime();
            case 5:
                return work.getEndDate();
            case 6:
                return work.getEndTime();
            default:
                log.error(texts.getString("COULD NOT UPDATE WORK"));
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return texts.getString("ID");
            case 1:
                return texts.getString("EMPLOYEE_ID");
            case 2:
                return texts.getString("KEG_ID");
            case 3:
                return texts.getString("START_DATE");
            case 4:
                return texts.getString("START_TIME");
            case 5:
                return texts.getString("END_DATE");
            case 6:
                return texts.getString("END_TIME");
            default:
                throw new IllegalArgumentException("column:" + columnIndex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return Long.class;
            case 2:
                return Long.class;
            case 3:
                return LocalDate.class;
            case 4:
                return LocalTime.class;
            case 5:
                return LocalDate.class;
            case 6:
                return LocalTime.class;

            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Work work = works.get(rowIndex);
        switch (columnIndex) {
            case 0:
                work.setEmployeeId((Long) aValue);
                break;
            case 1:
                work.setKegId((Long) aValue);
                break;
            case 2:
                work.setId((Long) aValue);
                break;
            case 3:
                Date startDate = (Date) aValue;
                work.setStartDate((LocalDate) startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                break;
            case 4:
                work.setStartTime((LocalTime) aValue);
                break;
            case 5:
                Date endDate = (Date) aValue;                
                work.setEndDate((LocalDate) endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                break;
            case 6:
                work.setEndTime((LocalTime) aValue);
                break;
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
      //  UpdateSwingWorker updateSwingWorker = new UpdateSwingWorker(work, rowIndex, columnIndex);
      //  updateSwingWorker.execute();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 6:
                return false;
            case 5:
            case 3:    
                return true;
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }

    public void addRow(Work work, Long employeeId, Long kegId, WorkTableModel workModel) {
        CreateSwingWorker createSwingWorker = new CreateSwingWorker(work, employeeId, kegId, workModel);
        createSwingWorker.execute();
    }

    public void removeRow(int row, WorkTableModel workModel) {
        DeleteSwingWorker deleteSwingWorker = new DeleteSwingWorker(row, workModel);
        deleteSwingWorker.execute();
    }

    public void endWork(int row, WorkTableModel workModel) {
        EndSwingWorker endSwingWorker = new EndSwingWorker(row, workModel);
        endSwingWorker.execute();
    }
}
