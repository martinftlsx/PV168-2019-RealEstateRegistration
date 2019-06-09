package cz.muni.fi.pv168.exceptions;

/**
 * @author Martin Podhora
 */
public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
