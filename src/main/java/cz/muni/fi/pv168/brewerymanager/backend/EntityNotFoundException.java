package cz.muni.fi.pv168.brewerymanager.backend;

/**
 * Exception thrown when performing operation on non-existing entity in the 
 * database.
 * @author alice
 */
public class EntityNotFoundException extends RuntimeException {
    
    /**
     * Constructs an exception.
     */
    public EntityNotFoundException(){
        
    }
    /**
     * Constructs an exception with message.
     * @param msg detail message
     */
    public EntityNotFoundException(String msg){
        super(msg);
    }
    /**
     * Constructs an exception with message and cause.
     * @param msg detail message
     * @param cause cause of an exception
     */
    public EntityNotFoundException(String msg, Throwable cause){
        super(msg, cause);
    }
}