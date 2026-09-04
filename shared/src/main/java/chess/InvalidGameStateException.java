package chess;

public class InvalidGameStateException extends RuntimeException {
    public InvalidGameStateException() {}
    public InvalidGameStateException(String message) {
        super(message);
    }
}
