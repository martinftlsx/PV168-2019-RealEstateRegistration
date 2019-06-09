package cz.muni.fi.pv168.exceptions;

/**
 * @author Martin Podhora
 */
public class ServiceFailureException extends RuntimeException {
    public ServiceFailureException() {
    }

    public ServiceFailureException(String msg) {
        super(msg);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
