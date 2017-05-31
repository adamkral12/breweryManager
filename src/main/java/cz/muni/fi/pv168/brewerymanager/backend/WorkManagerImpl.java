package cz.muni.fi.pv168.brewerymanager.backend;

import commons.DBUtils;
import commons.IllegalEntityException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * This class implements work manager service
 * 
 * @author Adam Kral, Petra Mikova
 */
public class WorkManagerImpl implements WorkManager {
        
    private static final Logger logger = Logger.getLogger(
            WorkManagerImpl.class.getName());  
    
    private DataSource dataSource;
 //   private final Clock clock;
    public  WorkManagerImpl(DataSource ds){
        this.dataSource = ds;
    } 

    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
        private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public Long startWorkingWithKeg(Employee employee, Keg keg) throws ServiceFailureException  {
        LocalDate now = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        checkDataSource();
        if (keg == null) {
            throw new IllegalArgumentException("keg is null");
        }        
        if (keg.getId() == null) {
            throw new IllegalEntityException("keg id is null");
        }        
        if (employee == null) {
            throw new IllegalArgumentException("employee is null");
        }        
        if (employee.getId() == null) {
            throw new IllegalEntityException("employee id is null");
        }  
          if (!employeeExistsInWorks(employee.getId())){
            throw new IllegalEntityException("employee does not exist in the database");
                } 
       if (!kegExistsInWorks(keg.getId())){
            throw new IllegalEntityException("keg does not exist in the database");
                } 
       
        Connection conn = null;
        PreparedStatement updateSt = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            updateSt = conn.prepareStatement(
                         "INSERT INTO JOB (employee_id, keg_id, start_date, start_time)"
                         +"values (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            
            updateSt.setLong(1, employee.getId());
            updateSt.setLong(2, keg.getId());
            updateSt.setDate(3, toSqlDate(now));
            updateSt.setTime(4, toSqlTime(nowTime));
            int count = updateSt.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException("Keg or employee " + keg + "," + employee + " not found or it is already being worked with now");
            }
            
            DBUtils.checkUpdatesCount(count, keg, true);   
            Long id = DBUtils.getId(updateSt.getGeneratedKeys());
            conn.commit();
            return id;
        } catch (SQLException ex) {
            String msg = "Error when creating work";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, updateSt);
        }
    }
    
       private boolean employeeExistsInWorks(Long id) throws ServiceFailureException {
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

                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving employee with id " + id, ex);
        }
    }
        
    private boolean kegExistsInWorks(Long id) throws ServiceFailureException{
                try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,brand,capacity,price,deposit FROM keg WHERE id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Keg keg = resultSetToKeg(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + keg + " and " + resultSetToKeg(rs));
                }

                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving keg with id " + id, ex);
        }
    }

        private Keg resultSetToKeg(ResultSet rs) throws SQLException {
        Keg keg = new Keg();
        keg.setId(rs.getLong("id"));
        keg.setCapacity(rs.getInt("capacity"));
        keg.setDeposit(rs.getInt("deposit"));
        keg.setPrice(rs.getInt("price"));
        keg.setBrand(rs.getString("brand"));
        return keg;
    }
    @Override
    public List<Employee> findEmployeesWorkingWithKeg(Keg keg, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate) throws ServiceFailureException {
         checkDataSource();        
        if (keg == null) {
            throw new IllegalArgumentException("keg is null");
        }        
        if (keg.getId() == null) {
            throw new IllegalEntityException("keg id is null");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("start time is null");
        } 
        if (endTime == null) {
            throw new IllegalArgumentException("end time is null");
        } 
        if (startDate == null) {
        throw new IllegalArgumentException("start date is null");
        } 
        if (endDate == null) {
            throw new IllegalArgumentException("end date is null");
        } 
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Employee.id, name, email, phoneNumber, salary, position" +
                    " FROM Employee JOIN JOB ON Employee.id = Work.employee_id " +
                    " JOIN Keg ON WORK.keg_id = keg.id" +        
                    " WHERE keg.id = ?"
                            + " AND start_time <= ? AND end_ime >= ?"
                            + " AND start_date <= ? AND end_date >= ?");
            st.setLong(1, keg.getId());
            st.setTime(2, toSqlTime(startTime));
            st.setTime(3, toSqlTime(endTime));
            st.setDate(4, toSqlDate(startDate));
            st.setDate(5, toSqlDate(endDate));
            return EmployeeManagerImpl.executeQueryForMultipleEmployees(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find work with keg " + keg;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }   
    }
    
    public  boolean isEnded(Work work){
        return work.isEnded();
    }

    @Override
    public void endWork(Work work) throws ServiceFailureException {
        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();
        checkDataSource();
        validate(work);
        
        if (work.getId() == null) {
            throw new IllegalEntityException("work id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);            
            st = conn.prepareStatement(
                    "UPDATE Job SET end_time = ?, end_date = ? WHERE id = ?");
            
            st.setTime(1, toSqlTime(nowTime));
            st.setDate(2, toSqlDate(nowDate));
            st.setLong(3, work.getId());;

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, work, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating work in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteWork(Work work) throws ServiceFailureException {
                checkDataSource();
        if (work == null) {
            throw new IllegalArgumentException("work is null");
        }        
        if (work.getId() == null) {
            throw new IllegalEntityException("work id is null");
        }        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM JOB WHERE id = ?");
            st.setLong(1, work.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, work, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting work from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Work> findAllWorks() throws ServiceFailureException {
                checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT job.id, employee_id, keg_id, start_time, end_time, start_date, end_date,name, brand FROM JOB"
                            + " LEFT JOIN EMPLOYEE ON employee.id = job.employee_id "
                            + "LEFT JOIN KEG ON keg.id = job.keg_id ");
            return executeQueryForMultipleWorks(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all works from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }  
    }
    
        private Long getKey(ResultSet keyRS, Work work) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert work " + work
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert work " + work
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert work " + work
                    + " - no key found");
        }
    }
        
            private void validate(Work work) throws IllegalArgumentException {
        if (work == null) {
            throw new IllegalArgumentException("work is null");
        }
        if (work.getEmployeeId() == null) {
            throw new IllegalArgumentException("employee id in work is null");
        }
        if (work.getKegId() == null) {
            throw new IllegalArgumentException("keg id in work is null");
        }
        if (work.getStartDate() == null) {
            throw new IllegalArgumentException("work start date is null");
        }
        
        if (work.getEndDate() != null && work.getEndDate().isBefore(work.getStartDate())){
            throw new IllegalArgumentException("work date ended before started");
        }
        
        if (work.getStartTime() == null) {
            throw new IllegalArgumentException("work start time is null");
        }
        
        if (work.getEndTime() != null && work.getEndTime().isBefore(work.getStartTime())){
            throw new IllegalArgumentException("work time ended before started");
        }
    }
    
        static Work executeQueryForSingleWork(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Work result = rowToWork(rs);                
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more works with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }
        
     @Override
        public Employee findEmployeeWithKeg(Keg keg, LocalDate date, LocalTime time) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();        
        if (keg == null) {
            throw new IllegalArgumentException("keg is null");
        }        
        if (keg.getId() == null) {
            throw new IllegalEntityException("keg id is null");
        }  
        
        if (date == null){
            throw new IllegalArgumentException("date is null");
        }
        
        if (time == null){
            throw new IllegalArgumentException("time is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT EMPLOYEE.id, name, phoneNumber, email, position,salary " +
                    "FROM EMPLOYEE JOIN JOB ON JOB.employee_id = employee.id " +
                    "JOIN KEG ON KEG.id = JOB.keg_id " +        
                    "WHERE KEG.id = ?" + 
                     "AND JOB.start_time <= ?" +
                     "AND JOB.end_time >= ?"   +
                     "AND JOB.start_date <= ?"+
                     "AND JOB.end_date >= ?"     );
            st.setLong(1, keg.getId());
            st.setTime(2, toSqlTime(time));
            st.setTime(3, toSqlTime(time));
            st.setDate(4, toSqlDate(date));
            st.setDate(5, toSqlDate(date));

            return EmployeeManagerImpl.executeQueryForSingleEmployee(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find employee with keg " + keg;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }        
    }

    
    static List<Work> executeQueryForMultipleWorks(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Work> result = new ArrayList<Work>();
        while (rs.next()) {
            result.add(rowToWork(rs));
        }
        return result;
    }

    static private Work rowToWork(ResultSet rs) throws SQLException {
        Work result = new Work();
        result.setId(rs.getLong("id"));
        result.setKegId(rs.getLong("keg_id"));
        result.setEmployeeId(rs.getLong("employee_id"));
        result.setStartDate(toLocalDate(rs.getDate("start_date")));
        result.setEndDate(toLocalDate(rs.getDate("end_date")));
        result.setStartTime(toLocalTime(rs.getTime("start_time")));
        result.setEndTime(toLocalTime(rs.getTime("end_time")));
        return result;
    }
        
    
    private static Date toSqlDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }
    
    private static Time toSqlTime(LocalTime localTime) {
        return localTime == null ? null : Time.valueOf(localTime);
    }
    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
    
        private static LocalTime toLocalTime(Time time) {
        return time == null ? null : time.toLocalTime();
    }

    @Override
    public Work getWork(Long id) throws ServiceFailureException {
        
        checkDataSource();
        
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, employee_id, keg_id, start_date, end_date, start_time, end_time FROM JOB WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleWork(st);
        } catch (SQLException ex) {
            String msg = "Error when getting work with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
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

    @Override
    public boolean employeeExists(Long id) throws ServiceFailureException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id FROM JOB WHERE employee_id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found ");
                }

                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving employee with id " + id, ex);
        }
    }

    @Override
    public boolean kegExists(Long id) throws ServiceFailureException {
           try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id FROM JOB WHERE keg_id = ?")) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found ");
                }

                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving employee with id " + id, ex);
        }    }
}
