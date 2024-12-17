import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import validators.LicensePlateValidator;
import exceptions.InvalidLicensePlateException;
import exceptions.LicensePlateAlreadyExistsExeption;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LicensePlateValidatorTest {
	@Mock
	private EntityManager em;
	
	@Mock
	private TypedQuery<Long> typedQuery;
	
	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
	
	@Test
	void testValidateLicensePlate_ValidLicensePlateLen8() {
		String validLicensePlate = "Е123ЕЕ78";
		assertDoesNotThrow(() -> LicensePlateValidator.validateLicensePlate(validLicensePlate));
	}
	
	@Test
	void testValidateLicensePlate_ValidLicensePlateLen9() {
		String validLicensePlate = "Е123ЕЕ178";
		assertDoesNotThrow(() -> LicensePlateValidator.validateLicensePlate(validLicensePlate));
	}
	
	@Test
	void testValidateLicensePlate_InvalidLength() {
		String invalidLicensePlate = "А987АА1";
		Exception exception = assertThrows(InvalidLicensePlateException.class, () -> 
				LicensePlateValidator.validateLicensePlate(invalidLicensePlate));
		assertEquals("Номерной знак должен содержать от 8 до 9 символов.", exception.getMessage());
	}
	
	@Test
	void testValidateLicensePlate_InvalidLetters() {
		String invalidLicensePlate = "Ф123ЯЮ178";
		Exception exception = assertThrows(InvalidLicensePlateException.class, () ->
				LicensePlateValidator.validateLicensePlate(invalidLicensePlate));
		assertEquals("Неверные символы в номерном знаке.", exception.getMessage());
	}
	
	@Test 
	void testValidateLicensePlate_NonNumeric() {
		String invalidLicensePlate = "Е1Н3ЕЕ178";
		Exception exception = assertThrows(InvalidLicensePlateException.class, () ->
				LicensePlateValidator.validateLicensePlate(invalidLicensePlate));
		assertEquals("Неверные цифры в номерном знаке.", exception.getMessage());
	}
	
	@Test
	void testValidateLicensePlate_NonNumericRegion() {
		String invalidLicensePlate = "Е123ЕЕ17Н";
		Exception exception = assertThrows(InvalidLicensePlateException.class, () ->
				LicensePlateValidator.validateLicensePlate(invalidLicensePlate));
		assertEquals("Неверный код региона в номерном знаке.", exception.getMessage());
	}
	
	@Test
    void testValidateLicensePlateUniqueness_LicensePlateDoesNotExist() {
		String licensePlate = "Е123ЕЕ78";

        when(em.createQuery("SELECT COUNT(c) FROM Car c WHERE c.licensePlate = :licensePlate", Long.class))
            .thenReturn(typedQuery);
        when(typedQuery.setParameter("licensePlate", licensePlate)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);

        assertDoesNotThrow(() -> LicensePlateValidator.validateLicensePlateUniqueness(licensePlate, em));

        verify(em).createQuery("SELECT COUNT(c) FROM Car c WHERE c.licensePlate = :licensePlate", Long.class);
        verify(typedQuery).setParameter("licensePlate", licensePlate);
        verify(typedQuery).getSingleResult();
    }
	
	@Test
    void testValidateLicensePlateUniqueness_LicensePlateExist() {
		String licensePlate = "Е123ЕЕ78";

		when(em.createQuery("SELECT COUNT(c) FROM Car c WHERE c.licensePlate = :licensePlate", Long.class))
        	.thenReturn(typedQuery);
		when(typedQuery.setParameter("licensePlate", licensePlate)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(1L);

        Exception exception = assertThrows(LicensePlateAlreadyExistsExeption.class, () -> 
            LicensePlateValidator.validateLicensePlateUniqueness(licensePlate, em));
        assertEquals("Госномер уже существует.", exception.getMessage());

        verify(em).createQuery("SELECT COUNT(c) FROM Car c WHERE c.licensePlate = :licensePlate", Long.class);
        verify(typedQuery).setParameter("licensePlate", licensePlate);
        verify(typedQuery).getSingleResult();
    }
	
}
