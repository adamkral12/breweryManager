package cz.muni.fi.pv168.brewerymanager.gui;

import cz.muni.fi.pv168.brewerymanager.backend.Data;
import cz.muni.fi.pv168.brewerymanager.backend.Employee;
import cz.muni.fi.pv168.brewerymanager.backend.EmployeeManager;
import cz.muni.fi.pv168.brewerymanager.backend.Position;
import cz.muni.fi.pv168.brewerymanager.backend.WorkManager;
import static cz.muni.fi.pv168.brewerymanager.gui.KegTableModel.log;
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
public class EmployeeTableModel extends AbstractTableModel {

    private static final ResourceBundle texts = ResourceBundle.getBundle("gui/texts");
    private static List<Employee> employees = new ArrayList<>();
    final static Logger log = LoggerFactory.getLogger(EmployeeTableModel.class.getName());

    public EmployeeTableModel(EmployeeTableModel employeeModel) {
        RetrieveSwingWorker retrieveSwingWorker = new RetrieveSwingWorker(employeeModel);
        retrieveSwingWorker.execute();
    }

    private static class CreateSwingWorker extends SwingWorker<Void, Void> {

        private final Employee employee;
        private final EmployeeManager employeeManager;
        private final WeakReference<EmployeeTableModel> tableModel;
        
        
        public CreateSwingWorker(Employee employee, EmployeeTableModel employeeModel) {
            this.tableModel = new WeakReference<>(employeeModel);
            this.employeeManager = Data.getInstance().getEmployeeManager();
            this.employee = employee;
            
        }

        @Override
        protected Void doInBackground() throws Exception {
            log.info("Adding employee:" + employee);
            employeeManager.createEmployee(employee);
            employees.add(employee);

            return null;
        }

        @Override
        protected void done() {
            EmployeeTableModel employeeTableModel = tableModel.get();
            if (employeeTableModel != null) {
                int lastRow = employeeTableModel.getRowCount() - 1;
                employeeTableModel.fireTableRowsInserted(lastRow, lastRow);
            }
        }
    }

    private static class RetrieveSwingWorker extends SwingWorker<Void, Void> {

        private final EmployeeManager employeeManager;
        private final WeakReference<EmployeeTableModel> tableModel;
        
        public RetrieveSwingWorker(EmployeeTableModel employeeModel){
            this.tableModel = new WeakReference<>(employeeModel);
            employeeManager = Data.getInstance().getEmployeeManager();
        }
        @Override
        protected Void doInBackground() throws Exception {
            
            employees = employeeManager.findAllEmployees();
            log.info("Retrieving employees: " + employees);
            return null;
        }

        @Override
        protected void done() {
            EmployeeTableModel employeeTableModel = tableModel.get();
            if (employeeTableModel != null) {
            employeeTableModel.fireTableRowsInserted(0, employeeTableModel.getRowCount() - 1);
            }
        }
    }

    private static class UpdateSwingWorker extends SwingWorker<Void, Void> {

        private final Employee employee;
        private final int rowIndex;
        private final int columnIndex;
        private final EmployeeManager employeeManager;
        private final WeakReference<EmployeeTableModel> tableModel;

        public UpdateSwingWorker(Employee employee, int rowIndex, int columnIndex,EmployeeTableModel employeeModel) {
            this.tableModel = new WeakReference<>(employeeModel);
            employeeManager = Data.getInstance().getEmployeeManager();            
            this.employee = employee;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }

        @Override
        protected Void doInBackground() throws Exception {
            log.info("Updating employee:" + employee);           
            employeeManager.updateEmployee(employee);
            employees.set(rowIndex, employee);
            return null;
        }

        @Override
        protected void done() {
            EmployeeTableModel employeeTableModel = tableModel.get();
            if (employeeTableModel != null) {
            employeeTableModel.fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    private static class DeleteSwingWorker extends SwingWorker<Void, Void> {


        private final int row;
        private final EmployeeManager employeeManager;
        private final WorkManager workManager;
        private final WeakReference<EmployeeTableModel> tableModel;

        public DeleteSwingWorker(int row, EmployeeTableModel employeeModel) {
            this.row = row;
            this.tableModel = new WeakReference<>(employeeModel);
            employeeManager = Data.getInstance().getEmployeeManager();   
            workManager = Data.getInstance().getWorkManager();
        }

        @Override
        protected Void doInBackground() throws Exception {

            Long id = employees.get(row).getId();
            if (workManager.employeeExists(id)){
                log.info("Employee exists in table works, you must delete it first");
                return null;
            }
            Employee employee = employeeManager.getEmployee(id);            
            log.info("Deleting employee:" + employee);            
            employeeManager.deleteEmployee(employee);
            employees.remove(row);            
            return null;
        }

        @Override
        protected void done() {
            EmployeeTableModel employeeTableModel = tableModel.get();
            if (employeeTableModel != null) {            
            employeeTableModel.fireTableRowsDeleted(row, row);
            }
        }
    }

    @Override
    public int getRowCount() {
        return employees.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee employee = employees.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return employee.getId();
            case 1:
                return employee.getName();
            case 2:
                return employee.getEmail();
            case 3:
                return employee.getPhoneNumber();
            case 4:
                return employee.getSalary();
            case 5:
                return employee.getPosition();
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return texts.getString("ID");
            case 1:
                return texts.getString("NAME");
            case 2:
                return texts.getString("EMAIL");
            case 3:
                return texts.getString("PHONENUMBER");
            case 4:
                return texts.getString("SALARY");
            case 5:
                return texts.getString("POSITION");
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return Integer.class;
            case 5:
                return Position.class;
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Employee employee = employees.get(rowIndex);
        switch (columnIndex) {
            case 0:
                employee.setId((Long) aValue);
                break;
            case 1:
                employee.setName((String) aValue);
                break;
            case 2:
                employee.setEmail((String) aValue);
                break;
            case 3:
                employee.setPhoneNumber((String) aValue);
                break;
            case 4:
                employee.setSalary((Integer) aValue);
                break;
            case 5:
                employee.setPosition((Position) aValue);
                break;
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
   //     UpdateSwingWorker updateSwingWorker = new UpdateSwingWorker(employee, rowIndex, columnIndex, employeeModel);
     //   updateSwingWorker.execute();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                throw new IllegalArgumentException(texts.getString("COLUMNINDEX"));
        }
    }

    public void updateRow (Object aValue, int rowIndex, int columnIndex, Employee employee, EmployeeTableModel employeeModel){
        UpdateSwingWorker updateSwingWorker = new UpdateSwingWorker(employee, rowIndex, columnIndex, employeeModel);
        updateSwingWorker.execute();       
        setValueAt(aValue, rowIndex, columnIndex);
    }
    
    public void addRow(Employee employee, EmployeeTableModel employeeModel) {
        CreateSwingWorker createSwingWorker = new CreateSwingWorker(employee, employeeModel);
        createSwingWorker.execute();
    }

    public void removeRow(int row, EmployeeTableModel employeeModel) {
        DeleteSwingWorker deleteSwingWorker = new DeleteSwingWorker(row, employeeModel);
        deleteSwingWorker.execute();
    }
}
