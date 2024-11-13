package exceptions;

public class  InvalidVinNumberException extends Exception {
    public InvalidVinNumberException(String message) {
        super(message);
    }
}