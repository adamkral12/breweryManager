package commons;

/**
 * Exception thrown when we use invalid entity for operation.
 * @author alice
 */
public class IllegalEntityException extends RuntimeException {

    /**
     * Construct an exception.
     */
    public IllegalEntityException() {
    }

    /**
     * Construct exception with a message.
     * @param msg detail message.
     */
    public IllegalEntityException(String msg) {
        super(msg);
    }

    /**
     * Constructs exception with message and cause.
     * 
     * @param msg detail message.
     * @param cause cause
     */
    public IllegalEntityException(String msg, Throwable cause) {
        super(msg, cause);
    }
        
}