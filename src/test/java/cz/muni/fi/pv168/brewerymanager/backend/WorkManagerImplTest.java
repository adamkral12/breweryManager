package cz.muni.fi.pv168.brewerymanager.backend;

import commons.DBUtils;
import commons.IllegalEntityException;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Adam Kral, Petra Mikova
 */
public class WorkManagerImplTest {

    private WorkManagerImpl manager;
    private KegManagerImpl kegManager;
    private EmployeeManagerImpl employeeManager;
    private DataSource ds;

    private final static LocalDate SDATE = LocalDate.of(2016, Month.JANUARY, 1);
    private final static LocalDate EDATE = LocalDate.of(2016, Month.JANUARY, 1);

    private final static LocalTime STIME = LocalTime.of(4, 0);
    private static final LocalTime ETIME = LocalTime.of(7, 0);

    private final static ZonedDateTime NOW
            = LocalDateTime.of(2016, Month.FEBRUARY, 29, 14, 00).atZone(ZoneId.of("UTC"));

    @Rule

    public ExpectedException expectedException = ExpectedException.none();

    //--------------------------------------------------------------------------
    // Test initialization
    //--------------------------------------------------------------------------
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();

        ds.setDatabaseName("memory:workmgr-test");

        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, KegManager.class.getResource("createTables.sql"));
        manager = new WorkManagerImpl(ds);
        manager.setDataSource(ds);
        kegManager = new KegManagerImpl(ds);
        kegManager.setDataSource(ds);
        employeeManager = new EmployeeManagerImpl(ds);
        employeeManager.setDataSource(ds);
        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, KegManager.class.getResource("dropTables.sql"));
    }

    //--------------------------------------------------------------------------
    // Preparing test data
    //--------------------------------------------------------------------------
    private Employee e1, e2, e3, empWithNullId, empNotInDB;
    private Keg k1, k2, k3, k4, k5, kegWithNullId, kegNotInDB;
    private Work w1, w2, w3;

    private void prepareTestData() {

        e1 = new EmployeeBuilder().name("Adam Kral").email("email@myemail").
                phoneNumber("5434").salary(300000).position(Position.BEER_TASTER).build();
        e2 = new EmployeeBuilder().name("Master Yoda").email("star@wars").
                phoneNumber("0000").salary(10000).position(Position.CLEANING_LADY).build();
        e3 = new EmployeeBuilder().name("Unknown").email("gohome@youredrunk.com").
                phoneNumber("123452311").salary(150000).position(Position.MYSTERY_SHOPPER).build();

        k1 = new KegBuilder().price(999).capacity(25).deposit(1000).brand("Pilsner").build();
        k2 = new KegBuilder().price(1900).capacity(30).deposit(1000).brand("Radegast").build();
        k3 = new KegBuilder().price(2499).capacity(50).deposit(1000).brand("Gambrinus").build();
        k4 = new KegBuilder().price(4999).capacity(100).deposit(1500).brand("Staropramen").build();
        k5 = new KegBuilder().price(19999).capacity(300).deposit(2000).brand("Krušovice").build();

        kegManager.createKeg(k1);
        kegManager.createKeg(k2);
        kegManager.createKeg(k3);
        kegManager.createKeg(k4);
        kegManager.createKeg(k5);

        employeeManager.createEmployee(e1);
        employeeManager.createEmployee(e2);
        employeeManager.createEmployee(e3);

        w3 = new WorkBuilder().employeeId(new Long(1)).kegId(new Long(1)).startTime(STIME).
                endTime(ETIME).startDate(SDATE).endDate(EDATE).build();

        empWithNullId = new EmployeeBuilder().id(null).build();
        empNotInDB = new EmployeeBuilder().id(e3.getId() + 100).build();
        assertThat(employeeManager.getEmployee(empNotInDB.getId())).isNull();

        kegWithNullId = new KegBuilder().price(999).capacity(25).deposit(1000).brand("Pilsner").id(null).build();
        kegNotInDB = new KegBuilder().price(19999).capacity(300).deposit(2000).brand("Krušovice").id(k5.getId() + 100).build();
        assertThat(kegManager.getKeg(kegNotInDB.getId())).isNull();
    }

    /**
     * WORK building
     */
    private WorkBuilder eThreeWithKegOneBuilder(LocalDate startDate, LocalTime startTime, Long id) {
        return new WorkBuilder().employeeId(new Long(3)).kegId(new Long(1)).startTime(startTime).
                endTime(null).startDate(startDate).endDate(null).id(id);
    }

    private WorkBuilder eThreeWithKegTwoBuilder(LocalDate startDate, LocalTime startTime, Long id) {
        return new WorkBuilder().employeeId(new Long(3)).kegId(new Long(2)).startTime(startTime).
                endTime(null).startDate(startDate).endDate(null).id(id);
    }
    
    private WorkBuilder endWorkBuilder(Long id, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime){
         return new WorkBuilder().employeeId(new Long(3)).kegId(new Long(1)).startTime(startTime).
                endTime(endTime).startDate(startDate).endDate(endDate).id(id);
    }

    @Test
    public void findEmployeeWithKegAndAllWorks() {

        assertThat(manager.findEmployeeWithKeg(k1, SDATE, STIME)).isNull();
        assertThat(manager.findEmployeeWithKeg(k2, SDATE, STIME)).isNull();
        assertThat(manager.findEmployeeWithKeg(k3, SDATE, STIME)).isNull();
        assertThat(manager.findEmployeeWithKeg(k3, SDATE, STIME)).isNull();
        assertThat(manager.findEmployeeWithKeg(k3, SDATE, STIME)).isNull();

        assertThat(manager.findAllWorks()).isEmpty();
        Long id = manager.startWorkingWithKeg(e3, k1);

        assertNotNull(id);

        LocalDate workDate = manager.getWork(id).getStartDate();
        LocalTime workTime = manager.getWork(id).getStartTime();

        Work work = manager.getWork(id);

        w1 = eThreeWithKegOneBuilder(workDate, workTime, id).build();


        assertThat(manager.findAllWorks()).usingFieldByFieldElementComparator()
                .containsOnly(w1);

        Long id2 = manager.startWorkingWithKeg(e3, k2);

        LocalDate work2Date = manager.getWork(id2).getStartDate();
        LocalTime work2Time = manager.getWork(id2).getStartTime();

        w2 = eThreeWithKegTwoBuilder(work2Date, work2Time, id2).build();
 

        assertThat(manager.findAllWorks()).usingFieldByFieldElementComparator()
                .containsOnly(w1,w2);
        
        assertThat(manager.getWork(id).getEndTime()).isNull();
        assertThat(manager.getWork(id).getEndDate()).isNull();  
        
        manager.endWork(work);
        
        assertThat(manager.getWork(id).getEndTime()).isNotNull();
        assertThat(manager.getWork(id).getEndDate()).isNotNull();
        
        assertThat(manager.findEmployeeWithKeg(k1, workDate, workTime))
                .isEqualToComparingFieldByField(e3);
        assertThat(manager.findEmployeeWithKeg(k2, SDATE, STIME)).isNull();
        assertThat(manager.findEmployeeWithKeg(k3, SDATE, STIME)).isNull();
        assertThat(manager.findEmployeeWithKeg(k4, SDATE, STIME)).isNull();
        assertThat(manager.findEmployeeWithKeg(k5, SDATE, STIME)).isNull();
        
        manager.deleteWork(work);
        
        
        assertThat(manager.findAllWorks()).usingFieldByFieldElementComparator()
                .containsOnly(w2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findGraveWithNullBody() {
        manager.findEmployeeWithKeg(null, SDATE, STIME);
    }

    @Test(expected = IllegalEntityException.class)
    public void findEmployeeWithKegWithNullId() {
        manager.findEmployeeWithKeg(kegWithNullId, SDATE, STIME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void workWithNullEmployee() {
        manager.startWorkingWithKeg(null, k2);
    }

    @Test(expected = IllegalEntityException.class)
    public void workWithEmployeeWithNullId() {
        manager.startWorkingWithKeg(empWithNullId, k2);
    }

    @Test(expected = IllegalEntityException.class)
    public void workWithEmployeeNotInDB() {
        manager.startWorkingWithKeg(empNotInDB, k2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void workWithNullKeg() {
        manager.startWorkingWithKeg(e2, null);
    }

    @Test(expected = IllegalEntityException.class)
    public void workWithKegWithNullID() {
        manager.startWorkingWithKeg(e2, kegWithNullId);
    }

    @Test(expected = IllegalEntityException.class)
    public void workWithKegNotInDb() {
        manager.startWorkingWithKeg(e2, kegNotInDB);
    }
  
   
}
