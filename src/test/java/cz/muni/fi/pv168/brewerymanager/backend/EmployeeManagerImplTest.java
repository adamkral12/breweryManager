package cz.muni.fi.pv168.brewerymanager.backend;

import commons.DBUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static java.time.Month.FEBRUARY;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class checks the functionality of EmployeeManagerImpl.
 * @author Petra Mikova 433345
 */
public class EmployeeManagerImplTest {
    
     
    private EmployeeManagerImpl manager;
    private DataSource ds;
    private final List<Employee> employees = new ArrayList<Employee>();

    // ExpectedException is one possible mechanisms for testing if expected
    // exception is thrown. See createGraveWithExistingId() for usage example.
    @Rule
    // attribute annotated with @Rule annotation must be public :-(
    public ExpectedException expectedException = ExpectedException.none();
  
   private EmployeeBuilder sampleYodaEmployee() {
       return new EmployeeBuilder()
               .name("Yoda")
               .email("yoda@jedi.pow")
               .phoneNumber("283789727")
               .salary(120)
               .position(Position.SHOP_MASTER);
       
       
   }
   private EmployeeBuilder sampleObiWanEmployee() {
       return new EmployeeBuilder()
               .name("Obi-wan")
               .email("obi.wan@raised.vader")
               .phoneNumber("97546789876")
               .salary(170)
               .position(Position.MYSTERY_SHOPPER);
   }
   
   
   @Before
   public void setUp() throws SQLException{
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,EmployeeManager.class.getResource("createTables.sql"));
        manager = new EmployeeManagerImpl(ds);
        manager.setDataSource(ds);
   }
   
    @After
    public void tearDown() throws SQLException {
        // Drop tables after each test
        DBUtils.executeSqlScript(ds,EmployeeManager.class.getResource("dropTables.sql"));
    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        //we will use in memory database
        ds.setDatabaseName("memory:employeemgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

   @Test
   public void testGetEmployee(){
      
       Employee employee = sampleYodaEmployee().build();
       
       Long id = manager.createEmployee(employee);
       assertNotNull(id);
       // tests creating employee
       
       Employee returnEmpl = manager.getEmployee(id);
       
       assertEquals(sampleYodaEmployee().getName(), returnEmpl.getName());
       assertEquals(sampleYodaEmployee().getEmail(), returnEmpl.getEmail());
       assertEquals(sampleYodaEmployee().getPhoneNumber(), returnEmpl.getPhoneNumber());
       assertEquals(sampleYodaEmployee().getPosition(), returnEmpl.getPosition());
       assertEquals(sampleYodaEmployee().getSalary(), returnEmpl.getSalary());  
       // tries if the employee is set right
   }    
   @Test
   public void testUpdateEmployee(){
       final String name = "Master Win-du";
       final String email = "purple.saber@jedi.pow";
       final String number = "987654567";
       final int salary = 130;
       
       Employee employee = sampleYodaEmployee().build();
       
       Long id = manager.createEmployee(employee);
       assertNotNull(id);
       
       Employee returnEmpl = manager.getEmployee(id);
       
       assertEquals(sampleYodaEmployee().getName(), returnEmpl.getName());
       assertEquals(sampleYodaEmployee().getEmail(), returnEmpl.getEmail());
       assertEquals(sampleYodaEmployee().getPhoneNumber(), returnEmpl.getPhoneNumber());
       assertEquals(sampleYodaEmployee().getPosition(), returnEmpl.getPosition());
       assertEquals(sampleYodaEmployee().getSalary(), returnEmpl.getSalary());  
       // at first it tests simmilarly like 1st function, if the employee was created correctly
       employee.setName(name);
       employee.setEmail(email);
       employee.setPhoneNumber(number);
       employee.setPosition(Position.KEG_MASTER);
       employee.setSalary(salary);
       
       manager.updateEmployee(employee);
       returnEmpl = manager.getEmployee(id);
       
       assertEquals(name, returnEmpl.getName());
       assertEquals(email, returnEmpl.getEmail());
       assertEquals(number, returnEmpl.getPhoneNumber());
       assertEquals(Position.KEG_MASTER, returnEmpl.getPosition());
       assertEquals(salary, returnEmpl.getSalary());  
       // then it tests if the attributes were updated
   }
   @Test
   public void testDeleteEmployee(){
       
       Employee employee = sampleObiWanEmployee().build();
       
       Long id = manager.createEmployee(employee);
       assertNotNull(manager.getEmployee(id));
       //checks if the employee exists
       manager.deleteEmployee(employee);
       assertNull(manager.getEmployee(id));
       //checks if the employee was deleted
       
       
   }
   @Test
   public void testFindAllEmployees(){

        assertThat(manager.findAllEmployees()).isEmpty();

        Employee e1 = sampleYodaEmployee().build();
        Employee e2 = sampleObiWanEmployee().build();

        manager.createEmployee(e1);
        manager.createEmployee(e2);

        assertThat(manager.findAllEmployees())
                .usingFieldByFieldElementComparator()
                .containsOnly(e1,e2);
       
   }
   @Test
   public void testNullEmployee(){
       try{
           manager.createEmployee(null);
           fail("Employee is created");
       }
       catch(IllegalArgumentException ex){
           
       }
       // a small extra test checking if the null employee is not created and
       // exception will come up
   }
 
   
   @Test(expected=IllegalArgumentException.class)
   public void testNegativeSalary(){
       Long id = manager.createEmployee(sampleObiWanEmployee().build());
       Employee empl = manager.getEmployee(id);
       empl.setSalary(-1);
       
   }
   
     
}