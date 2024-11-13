package validators;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import exceptions.InvalidLicenseNumberException;
import exceptions.LicenseAlreadyExistsException;

public class LicenseNumberValidator {
	public static void validateLicenseNumber(String licenseNumber) throws InvalidLicenseNumberException {
	    if (licenseNumber.length() != 10 || !licenseNumber.matches("\\d+")) {
	        throw new InvalidLicenseNumberException("Номер ВУ должен состоять только из 10 цифр.");
	    }
	}
	public static void validateLicenseUniqueness(String license, EntityManager em) throws LicenseAlreadyExistsException {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(d) FROM Driver d WHERE d.licenseNumber = :license", Long.class);
        query.setParameter("license", license);
        Long count = query.getSingleResult();
        if (count > 0) {
            throw new LicenseAlreadyExistsException("Номер ВУ уже существует.");
        }
    }
}
