package dataaccess;

public class InUseException extends RuntimeException {
    public InUseException(String message) {
        super(message);
    }
}
