package cz.muni.fi.pv168.brewerymanager.backend;

import java.util.List;

/**
 *
 * @author Adam Kral, Petra Mikova
 */
public interface KegManager {
    /**
    *  Stores a new keg into a database. Id is autmatically generated and stored
    * into id atribute.
    *
    * @param keg keg to be created
    * @throws IllegalArgumentException when keg is null or id is already signed
    * @throws ServiceFailureException when db operation fails
    **/
    
    void createKeg(Keg keg) throws ServiceFailureException;
    /**
     * Returns keg with given id
     * 
     * @param id primary key of requested keg
     * @return keg with given id or null if keg does not exist
     * @throws ServiceFailureException  when db operation fails
     */
    
    Keg getKeg(Long id) throws ServiceFailureException;
    /**
     * Updates keg in database
     * 
     * @param keg updated keg to be stored in database
     * @throws IllegalArgumentException when keg is null or has null id
     * @throws ServiceFailureException when db operation fails
     */
    
    void updateKeg(Keg keg) throws ServiceFailureException;
    /**
     * Deletes keg from database
     * 
     * @param keg keg to be deleted from database
     * @throws IllegalArgumentException when keg is null or has null id
     * @throws ServiceFailureException when db operation fails
     */
    
    void deleteKeg(Keg keg) throws ServiceFailureException;
   /**
    * Returns list of all kegs in database
    * 
    * @return list of all kegs from database
    * @throws ServiceFailureException when db operation fails
    */
    List<Keg> findAllKegs() throws ServiceFailureException;
}
