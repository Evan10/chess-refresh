package dataaccess;

public class InUseException extends DataAccessException {
    public InUseException(String message) {
        super(message);
    }
}
