package cz.muni.fi.pv168.brewerymanager.backend;

import commons.DBUtils;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 *
 * @author Adam Kral
 */
public class KegManagerImplTest {
    private KegManagerImpl manager;
    private DataSource ds;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    //--------------------------------------------------------------------------
    // Test initialization
    //--------------------------------------------------------------------------

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        // we will use in memory database
        ds.setDatabaseName("memory:kegmgr-test");
        // database is created automatically if it does not exist yet
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,KegManager.class.getResource("createTables.sql"));
        manager = new KegManagerImpl(ds);
        manager.setDataSource(ds);
    }

    @After
    public void tearDown() throws SQLException {
        // Drop tables after each test
        DBUtils.executeSqlScript(ds,KegManager.class.getResource("dropTables.sql"));
    }
    
    //--------------------------------------------------------------------------
    // Preparing test data
    //--------------------------------------------------------------------------


    private KegBuilder samplePilsnerKegBuilder() {
        return new KegBuilder()
                .id(null)
                .price(1000)
                .deposit(1000)
                .capacity(30)
                .brand("plzen");
    }

    private KegBuilder sampleRadegastKegBuilder() {
        return new KegBuilder()
                .id(null)
                .price(1200)
                .deposit(1000)
                .capacity(50)
                .brand("radegast");
    }
    
        //--------------------------------------------------------------------------
    // Tests for operations for creating and fetching kegs
    //--------------------------------------------------------------------------

    @Test
    public void createKeg() {
        Keg keg = samplePilsnerKegBuilder().build();
        manager.createKeg(keg);

        Long kegId = keg.getId();
        assertThat(kegId).isNotNull();

        assertThat(manager.getKeg(kegId))
                .isNotSameAs(keg)
                .isEqualToComparingFieldByField(keg);
    }

    @Test
    public void findAllKegs() {

        assertThat(manager.findAllKegs()).isEmpty();

        Keg g1 = samplePilsnerKegBuilder().build();
        Keg g2 = sampleRadegastKegBuilder().build();

        manager.createKeg(g1);
        manager.createKeg(g2);

        assertThat(manager.findAllKegs())
                .usingFieldByFieldElementComparator()
                .containsOnly(g1,g2);
    }
    
     @Test(expected = IllegalArgumentException.class)
    public void createNullKeg() {
        manager.createKeg(null);
    }

    @Test
    public void createKegWithExistingId() {
        Keg keg = samplePilsnerKegBuilder().id(1L).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createKeg(keg);
    }

    @Test
    public void createKegZeroCapacity() {
        Keg keg = samplePilsnerKegBuilder().capacity(0).build();
        assertThatThrownBy(() -> manager.createKeg(keg))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createKegWithNegativePrice() {
        Keg keg = samplePilsnerKegBuilder().price(-1).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createKeg(keg);
    }

    @Test
    public void createKegWithNegativeDeposit() {
        Keg keg = samplePilsnerKegBuilder().deposit(-1).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createKeg(keg);
    }

    @Test
    public void createKegWithEmptyBrand() {
        Keg keg = samplePilsnerKegBuilder().brand("").build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createKeg(keg);
    }

    @Test
    public void createKegWithZeroPrice() {
        Keg keg = samplePilsnerKegBuilder().price(0).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createKeg(keg);
    }

    @Test
    public void createKegWithNegativeCapacity() {
        Keg keg = samplePilsnerKegBuilder().capacity(0).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createKeg(keg);
    }

    @Test
    public void createKegWithNullBrand() {
        Keg keg = samplePilsnerKegBuilder().brand(null).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createKeg(keg);
    }


 
     //--------------------------------------------------------------------------
    // Tests if KegManager methods throws ServiceFailureException in case of
    // DB operation failure
    //--------------------------------------------------------------------------

    @Test
    public void createKegWithSqlExceptionThrown() throws SQLException {
        // Create sqlException, which will be thrown by our DataSource mock
        // object to simulate DB operation failure
        SQLException sqlException = new SQLException();
        // Create DataSource mock object
        DataSource failingDataSource = mock(DataSource.class);
        // Instruct our DataSource mock object to throw our sqlException when
        // DataSource.getConnection() method is called.
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        // Configure our manager to use DataSource mock object
        manager.setDataSource(failingDataSource);

        // Create Keg instance for our test
        Keg keg = samplePilsnerKegBuilder().build();

        // Try to call Manager.createKeg(Keg) method and expect that
        // exception will be thrown
        assertThatThrownBy(() -> manager.createKeg(keg))
                // Check that thrown exception is ServiceFailureException
                .isInstanceOf(ServiceFailureException.class)
                // Check if cause is properly set
                .hasCause(sqlException);
    }
    
}
