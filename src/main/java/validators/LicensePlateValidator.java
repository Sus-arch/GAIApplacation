package validators;

import exceptions.InvalidLicensePlateException;

public class LicensePlateValidator {

    private static final String VALID_LETTERS = "АВЕКМНОРСТУХ";

    public static void validateLicensePlate(String licensePlate) throws InvalidLicensePlateException {
        if (licensePlate == null || licensePlate.length() < 8 || licensePlate.length() > 9) {
            throw new InvalidLicensePlateException("Номерной знак должен содержать от 8 до 9 символов.");
        }

        if (!VALID_LETTERS.contains(String.valueOf(licensePlate.charAt(0))) ||
            !VALID_LETTERS.contains(String.valueOf(licensePlate.charAt(4))) ||
            !VALID_LETTERS.contains(String.valueOf(licensePlate.charAt(5)))) {
            throw new InvalidLicensePlateException("Неверные символы в номерном знаке.");
        }

        for (int i = 1; i < 4; i++) {
            if (!Character.isDigit(licensePlate.charAt(i))) {
                throw new InvalidLicensePlateException("Неверные цифры в номерном знаке.");
            }
        }

        for (int i = 6; i < licensePlate.length(); i++) {
            if (!Character.isDigit(licensePlate.charAt(i))) {
                throw new InvalidLicensePlateException("Неверный код региона в номерном знаке.");
            }
        }
    }
}

