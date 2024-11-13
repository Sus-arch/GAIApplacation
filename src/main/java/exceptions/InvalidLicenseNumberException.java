package exceptions;

public class InvalidLicenseNumberException extends Exception {
    public InvalidLicenseNumberException(String message) {
        super(message);
    }
}
