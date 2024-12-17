import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import validators.LicenseNumberValidator;
import exceptions.InvalidLicenseNumberException;
import exceptions.LicenseAlreadyExistsException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LicenseNumberValidatorTest {
	@Mock
	private EntityManager em;
	
	@Mock
	private TypedQuery<Long> typedQuery;
	
	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
	
	@Test
    void testValidateLicenseNumber_ValidNumber() {
        String validLicense = "1234567890";
        assertDoesNotThrow(() -> LicenseNumberValidator.validateLicenseNumber(validLicense));
    }
	
	@Test
    void testValidateLicenseNumber_InvalidLength() {
        String invalidLicense = "12345";
        Exception exception = assertThrows(InvalidLicenseNumberException.class, () -> 
            LicenseNumberValidator.validateLicenseNumber(invalidLicense));
        assertEquals("Номер ВУ должен состоять только из 10 цифр.", exception.getMessage());
    }
	
	@Test
    void testValidateLicenseNumber_NonNumeric() {
        String invalidLicense = "1234abcd90";
        Exception exception = assertThrows(InvalidLicenseNumberException.class, () -> 
            LicenseNumberValidator.validateLicenseNumber(invalidLicense));
        assertEquals("Номер ВУ должен состоять только из 10 цифр.", exception.getMessage());
    }
	
	@Test
    void testValidateLicenseUniqueness_LicenseDoesNotExist() {
        String license = "1234567890";

        when(em.createQuery("SELECT COUNT(d) FROM Driver d WHERE d.licenseNumber = :license", Long.class))
            .thenReturn(typedQuery);
        when(typedQuery.setParameter("license", license)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);

        assertDoesNotThrow(() -> LicenseNumberValidator.validateLicenseUniqueness(license, em));

        verify(em).createQuery("SELECT COUNT(d) FROM Driver d WHERE d.licenseNumber = :license", Long.class);
        verify(typedQuery).setParameter("license", license);
        verify(typedQuery).getSingleResult();
    }
	
	 @Test
	    void testValidateLicenseUniqueness_LicenseExists() {
	        String license = "1234567890";

	        when(em.createQuery("SELECT COUNT(d) FROM Driver d WHERE d.licenseNumber = :license", Long.class))
	            .thenReturn(typedQuery);
	        when(typedQuery.setParameter("license", license)).thenReturn(typedQuery);
	        when(typedQuery.getSingleResult()).thenReturn(1L);

	        Exception exception = assertThrows(LicenseAlreadyExistsException.class, () -> 
	            LicenseNumberValidator.validateLicenseUniqueness(license, em));
	        assertEquals("Номер ВУ уже существует.", exception.getMessage());

	        verify(em).createQuery("SELECT COUNT(d) FROM Driver d WHERE d.licenseNumber = :license", Long.class);
	        verify(typedQuery).setParameter("license", license);
	        verify(typedQuery).getSingleResult();
	    }
}
