package cz.muni.fi.pv168.brewerymanager.backend;

import java.util.List;

/**
 *
 * @author Adam Kral, Petra Mikova
 */
public interface EmployeeManager {
      /**
    *  Stores a new employee into a database. Id is autmatically generated and stored
    * into id atribute.
    *
    * @param employee employee to be created
    * @throws IllegalArgumentException when employee is null or id is already signed
    * @throws ServiceFailureException when db operation fails
    **/
    
    Long createEmployee(Employee employee) throws ServiceFailureException;
    /**
     * Returns employee with given id
     * 
     * @param id primary key of requested employee
     * @return employee with given id or null if employee does not exist
     * @throws ServiceFailureException  when db operation fails
     */
    
    Employee getEmployee(Long id) throws ServiceFailureException;
    /**
     * Updates employee in database
     * 
     * @param employee updated employee to be stored in database
     * @throws IllegalArgumentException when employee is null or has null id
     * @throws ServiceFailureException when db operation fails
     */
    
    void updateEmployee(Employee employee) throws ServiceFailureException;
    /**
     * Deletes employee from database
     * 
     * @param employee employee to be deleted from database
     * @throws IllegalArgumentException when employee is null or has null id
     * @throws ServiceFailureException when db operation fails
     */
    
    void deleteEmployee(Employee employee) throws ServiceFailureException;
   /**
    * Returns list of all employees in database
    * 
    * @return list of all employees from database
    * @throws ServiceFailureException when db operation fails
    */
    List<Employee> findAllEmployees() throws ServiceFailureException;
}