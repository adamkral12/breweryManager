package cz.muni.fi.pv168.brewerymanager.backend;

import commons.DBUtils;
import commons.IllegalEntityException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Adam Kral, Petra Mikova
 * jak v result setu nastavovat enum? (metoda rowToEmployee)
 */
public class EmployeeManagerImpl implements EmployeeManager {
    
    private static final Logger logger = Logger.getLogger(
            EmployeeManagerImpl.class.getName());    
    
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    } 
    public EmployeeManagerImpl(DataSource ds){
        this.dataSource = ds;
    }

    @Override
    public Long createEmployee(Employee employee) throws ServiceFailureException {
        Long id;
        checkDataSource();
        validate(employee);
        if (employee.getId() != null) {
            throw new IllegalEntityException("employee id is already set");
        }       
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Employee (name,email,phoneNumber,salary, position) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            
            st.setString(1, employee.getName());
            st.setString(2, employee.getEmail());
            st.setString(3, employee.getPhoneNumber());
            st.setInt(4, employee.getSalary());
            st.setString(5, employee.getPosition().toString());
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, employee, true);

            id = DBUtils.getId(st.getGeneratedKeys());
            employee.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting employee into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
 
        }
        return id;
    }
    /**
     * Method to control if atributes of keg are set properly
     * @param keg keg to be chekced
     * @throws IllegalArgumentException if keg is null, capacity is zero or less,
     * or price or deposit is negative
     */
    private void validate(Employee employee) throws IllegalArgumentException {
        if (employee == null) {
            throw new IllegalArgumentException("employee is null");
        }
        if (employee.getPosition() == null) {
            throw new IllegalArgumentException("employee position is null");
        }
        if (employee.getName() == null) {
            throw new IllegalArgumentException("employee name is null");
        }
        if (employee.getEmail() == null) {
            throw new IllegalArgumentException("employee email is null");
        }
        if (employee.getPhoneNumber() == null) {
            throw new IllegalArgumentException("employee phone number is null");
        }
        if (employee.getSalary() <= 0) {
            throw new IllegalArgumentException("employee salary is negative number");
        }
    }
    @Override
    public Employee getEmployee(Long id) throws ServiceFailureException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name,email,salary,position,phoneNumber FROM employee WHERE id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Employee employee = rowToEmployee(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + employee + " and " + rowToEmployee(rs));
                }

                return employee;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving employee with id " + id, ex);
        }
    }

    @Override
    public void updateEmployee(Employee employee) throws ServiceFailureException {
                checkDataSource();
        validate(employee);
        
        if (employee.getId() == null) {
            throw new IllegalEntityException("employee id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);            
            st = conn.prepareStatement(
                    "UPDATE Employee SET name = ?, email = ?, phoneNumber = ?, salary = ?, position=? WHERE id = ?");
            st.setString(1, employee.getName());;

            st.setString(2, employee.getEmail());
            st.setString(3, employee.getPhoneNumber());
            st.setInt(4, employee.getSalary());
            st.setString(5, employee.getPosition().toString());
            st.setLong(6, employee.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, employee, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating employee in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteEmployee(Employee employee) throws ServiceFailureException {
                checkDataSource();
        if (employee == null) {
            throw new IllegalArgumentException("employee is null");
        }        
        if (employee.getId() == null) {
            throw new IllegalEntityException("employee id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Employee WHERE id = ?");
            st.setLong(1, employee.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, employee, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting employee from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Employee> findAllEmployees() throws ServiceFailureException {
                checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, email, salary, phoneNumber,position FROM Employee");
            return executeQueryForMultipleEmployees(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all employees from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }  
    }
    
        static Employee executeQueryForSingleEmployee(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Employee result = rowToEmployee(rs);                
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more employees with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Employee> executeQueryForMultipleEmployees(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Employee> result = new ArrayList<Employee>();
        while (rs.next()) {
            result.add(rowToEmployee(rs));
        }
        return result;
    }
    
    
        static private Employee rowToEmployee(ResultSet rs) throws SQLException {
        Employee result = new Employee();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setEmail(rs.getString("email"));
        result.setPhoneNumber(rs.getString("phoneNumber"));
        result.setSalary(rs.getInt("salary"));
        result.setPosition(Position.valueOf(rs.getString("position")));


        return result;
    }
    
            private static Date toSqlDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
    
}
