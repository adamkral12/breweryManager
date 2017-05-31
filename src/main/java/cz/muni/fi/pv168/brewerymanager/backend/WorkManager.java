package cz.muni.fi.pv168.brewerymanager.backend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author Adam Kral, Petra Mikova
 */
public interface WorkManager {
       /**
    *  Stores a new work into a database. Id is autmatically generated and stored
    * into id atribute. Creates dependencies between keg and employee
    *
     * @param employee employee who is to be working with keg
     * @param keg keg with which employee is working
     * @return new work  
    * @throws IllegalArgumentException when employee is null or keg is null
    * @throws ServiceFailureException when db operation fails
    **/
    
    Long startWorkingWithKeg(Employee employee, Keg keg) throws ServiceFailureException;
    /**
     * Returns work with given id
     * 
     * @param id
     * @return work with given id or null if work does not exist
     * @throws ServiceFailureException  when db operation fails
     */
    Work getWork(Long id) throws ServiceFailureException;
    /**
     * Finds employee(s) who was/were working with keg between given times
     * @param keg keg being worked with
     * @param startTime start time
     * @param endTime end time
     * @param startDate
     * @param endDate
     * @return employee(s) who worked with keg
     * @throws ServiceFailureException when db operation fails
     */
    List<Employee> findEmployeesWorkingWithKeg(Keg keg, LocalTime startTime, LocalTime endTime,
            LocalDate startDate, LocalDate endDate) throws ServiceFailureException;
    /**
     * Updates end time and date of work in database
     * 
     * @param work updated work to be stored in database
     * @throws IllegalArgumentException when work is null or has null id
     * @throws ServiceFailureException when db operation fails
     */
 
    
    void endWork (Work work) throws ServiceFailureException;
    
       boolean isEnded(Work work);
    /**
     * Deletes work from database
     * 
     * @param work work to be deleted from database
     * @throws IllegalArgumentException when work is null or has null id
     * @throws ServiceFailureException when db operation fails
     */
    
    void deleteWork(Work work) throws ServiceFailureException;
   /**
    * Returns list of all works in database
    * 
    * @return list of all works from database
    * @throws ServiceFailureException when db operation fails
    */
    List<Work> findAllWorks() throws ServiceFailureException;
    /**
     * Returns employee working with keg at certain date and time
     * if not found, returns null
     * @param keg which was/is employee working with
     * @param date date when employee was working with keg
     * @param time when employee was working with keg
     * @return employee or null
     * @throws ServiceFailureException when db operation fails 
     */
    Employee findEmployeeWithKeg(Keg keg, LocalDate date, LocalTime time) throws ServiceFailureException;
    
    boolean employeeExists(Long id) throws ServiceFailureException;    
    boolean kegExists(Long id) throws ServiceFailureException;
}
