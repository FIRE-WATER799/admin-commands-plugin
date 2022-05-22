package salatosik;

public class DatabaseException extends Exception {
    private String message;
    
    public String getMessage() { return message; }

    public DatabaseException(String message) {
        super(message);
    }
}
