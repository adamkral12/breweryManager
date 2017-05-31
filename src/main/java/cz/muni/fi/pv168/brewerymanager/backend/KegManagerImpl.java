package cz.muni.fi.pv168.brewerymanager.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class representss KegManager service
 * 
 * @author Adam Kral, Petra Mikova
 */
public class KegManagerImpl implements KegManager {
    
    private static final Logger logger = Logger.getLogger(
            KegManagerImpl.class.getName());
    
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    

    public KegManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }    
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createKeg(Keg keg) throws ServiceFailureException {
        validate(keg);
        if (keg.getId() != null){
            throw new IllegalArgumentException("keg id is already set");
        }
        
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO KEG (capacity,price,deposit,brand) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)){
            st.setInt(1, keg.getCapacity());
            st.setInt(2, keg.getPrice());
            st.setInt(3, keg.getDeposit());
            st.setString(4, keg.getBrand());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert keg " + keg);
            }
            
            ResultSet keyRS = st.getGeneratedKeys();
            keg.setId(getKey(keyRS, keg));
            
        } catch(SQLException ex) {
             throw new ServiceFailureException("Error when inserting keg " + keg, ex);
        }
      
    }

    @Override
    public Keg getKeg(Long id) throws ServiceFailureException {
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

                return keg;
            } else {
                return null;
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
    public void updateKeg(Keg keg) throws ServiceFailureException {
          validate(keg);
        if (keg.getId() == null) {
            throw new IllegalArgumentException("keg id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                    "UPDATE keg SET capacity = ?, price = ?, deposit = ?, brand = ? WHERE id = ?")) {

            st.setInt(1, keg.getCapacity());
            st.setInt(2, keg.getPrice());
            st.setInt(3, keg.getDeposit());
            st.setString(4, keg.getBrand());
            st.setLong(5, keg.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Keg " + keg + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating keg " + keg, ex);
        }
    }

    @Override
    public void deleteKeg(Keg keg) throws ServiceFailureException {
         if (keg == null) {
            throw new IllegalArgumentException("keg is null");
        }
        if (keg.getId() == null) {
            throw new IllegalArgumentException("keg id is null");
        }
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                    "DELETE FROM keg WHERE id = ?")) {

            st.setLong(1, keg.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("keg " + keg + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting keg " + keg, ex);
        }
    }

    @Override
    public List<Keg> findAllKegs() throws ServiceFailureException {
           try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,capacity,deposit,price,brand FROM keg")) {

            ResultSet rs = st.executeQuery();

            List<Keg> result = new ArrayList<>();
            while (rs.next()) {
                result.add(resultSetToKeg(rs));
            }
            return result;

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all kegs", ex);
        }
    }
    

    
    /**
     * Method to control if atributes of keg are set properly
     * @param keg keg to be chekced
     * @throws IllegalArgumentException if keg is null, capacity is zero or less,
     * or price or deposit is negative
     */
    private void validate(Keg keg) throws IllegalArgumentException {
        if (keg == null) {
            throw new IllegalArgumentException("keg is null");
        }
        if (keg.getCapacity() <= 0) {
            throw new IllegalArgumentException("keg capacity is zero or less");
        }
        if (keg.getPrice() <= 0) {
            throw new IllegalArgumentException("keg price is negative number");
        }
        if (keg.getDeposit() <= 0) {
            throw new IllegalArgumentException("keg deposit is negative number");
        }
        
        if (keg.getBrand() == null || keg.getBrand().isEmpty()){
             throw new IllegalArgumentException("keg brand is null");           
        }
    }
    /**
     * method to generate unique ID for keg
     * @param keyRS resultset
     * @param keg
     * @return unique ID
     * @throws ServiceFailureException  when key is not unique or more or 
     * less than one key was retreived
     * @throws SQLException 
     */
    private Long getKey(ResultSet keyRS, Keg keg) throws ServiceFailureException, SQLException{
         if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert keg " + keg
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert keg " + keg
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert keg " + keg
                    + " - no key found");
        }
    }
}
