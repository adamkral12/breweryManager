package cz.muni.fi.pv168.brewerymanager.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example from lecture
 * @author Adam Kral
 */
public class DatabaseConnection {
    
    private static final String JDBC_URL = "";
     private static final String JDBC_USER = "";
      private static final String JDBC_PASS = "";
    
      public void connect() throws SQLException{
          
       try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
         /* Selecting */
               Statement st = connection.createStatement();   ){
           boolean result = st.execute("SELECT * FROM tableName");
           ResultSet resultSet = st.getResultSet();
           
           while(resultSet.next()){
               Long id = resultSet.getLong("ID");
               String name = resultSet.getString("NAME");
 
           }
           
           /*INserting */
           PreparedStatement insertStatement = connection.prepareStatement
        ("INSERT INTO table (a,b,c) VLAUES (?,?,?)");
           
           insertStatement.setInt(1, 1);
           insertStatement.setString(2, "Ahoj");
           insertStatement.setBoolean(3, false);
           insertStatement.execute();
       }
       
       
      /*Get generated key */
       /* auto-incerement more simple */
      // st.execute("INSERT into TABLE (a) values ('hi')",
        //       Statement.RETURN_GENERATED_KEYS);
       //ResultSet keys = st.getGeneratedKeys();
      }
}
