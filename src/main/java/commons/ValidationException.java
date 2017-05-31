package commons;

/**
 * Exception thrown when validation process fails.
 * 
 * @author alice
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs exception without further information.
     */
    public ValidationException() {
    }

    /**
     * Constructs exception with a message.
     *
     * @param msg the detail message.
     */
    public ValidationException(String msg) {
        super(msg);
    }
    
    public ValidationException(String msg, Throwable cause){
        super(msg, cause);
    }
}