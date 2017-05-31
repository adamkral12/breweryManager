package cz.muni.fi.pv168.brewerymanager.backend;

import java.util.ResourceBundle;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

public class Data {

    private static Data instance;
    
    private final BasicDataSource bds;
    private final EmployeeManager employeeManager;
    private final KegManager kegManager;
    private final WorkManager workManager;
    final static Logger log = LoggerFactory.getLogger(Data.class);
    private static final ResourceBundle login = ResourceBundle.getBundle("cz/muni/fi/pv168/brewerymanager/backend/login");


    private Data() {
        bds = new BasicDataSource();
        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl(login.getString("url"));
        bds.setUsername(login.getString("user"));
        bds.setPassword(login.getString("pass"));

        employeeManager = new EmployeeManagerImpl(bds);
        kegManager = new KegManagerImpl(bds);
        workManager = new WorkManagerImpl(bds);
    }

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    
    public static DataSource createMemoryDatabase() {
        BasicDataSource bds = new BasicDataSource();
        //set JDBC driver and URL
        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl(login.getString("url"));
        bds.setUsername(login.getString("user"));
        bds.setPassword(login.getString("pass"));

        new ResourceDatabasePopulator(
                new ClassPathResource("cz/muni/fi/pv168/brewerymanager/backend/createTables.sql"),
                new ClassPathResource("cz/muni/fi/pv168/brewerymanager/backend/testData.sql")
        ).execute(bds);
        log.info("SQL scripts executed");
        return bds;
    }

    public EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    

    public  KegManager getKegManager() {
        return kegManager;
    }

    public WorkManager getWorkManager() {
        return workManager;
    }

    public static void main(String[] args) throws ServiceFailureException {
    }
}
