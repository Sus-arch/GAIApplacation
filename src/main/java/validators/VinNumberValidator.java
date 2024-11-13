package validators;

import exceptions.InvalidVinNumberException;

public class VinNumberValidator {

    private static final String FORBIDDEN_CHARS = "IOQ";

    public static void validateVin(String vin) throws InvalidVinNumberException {
        if (vin == null || vin.length() != 17) {
            throw new InvalidVinNumberException("VIN должен содержать 17 символов.");
        }

        for (char c : vin.toCharArray()) {
            if (FORBIDDEN_CHARS.indexOf(c) != -1) {
                throw new InvalidVinNumberException("VIN не должен содержать символы I, O, Q.");
            }

            if (!Character.isLetterOrDigit(c)) {
                throw new InvalidVinNumberException("VIN должен содержать только буквы и цифры.");
            }
        }
    }
}

