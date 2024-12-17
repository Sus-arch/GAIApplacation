import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import validators.VinNumberValidator;
import exceptions.InvalidVinNumberException;
import exceptions.VinNumberAlreadyExistsExeption;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VinNumberValidatorTest {
	@Mock
	private EntityManager em;
	
	@Mock
	private TypedQuery<Long> typedQuery;
	
	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
	
	@Test
	void testValidateVin_ValidVin() {
		String validVin = "1HGBH41JXMN109186";
		assertDoesNotThrow(() -> VinNumberValidator.validateVin(validVin));
	}
	
	@Test
	void testValidateVin_InvalidLength() {
		String invalidVin = "1HGBH41JXMN1091869";
		Exception exception = assertThrows(InvalidVinNumberException.class, () ->
				VinNumberValidator.validateVin(invalidVin));
		assertEquals("VIN должен содержать 17 символов.", exception.getMessage());
	}
	
	@Test
	void testValidateVin_ForbiddenChars() {
		String invalidVin = "1HGBH41IOQN109186";
		Exception exception = assertThrows(InvalidVinNumberException.class, () ->
				VinNumberValidator.validateVin(invalidVin));
		assertEquals("VIN не должен содержать символы I, O, Q.", exception.getMessage());
	}
	
	@Test
	void testValidateVin_NonNumericOrLetter() {
		String invalidVin = "1H!BH41JX_N109)86";
		Exception exception = assertThrows(InvalidVinNumberException.class, () ->
				VinNumberValidator.validateVin(invalidVin));
		assertEquals("VIN должен содержать только буквы и цифры.", exception.getMessage());
	}
	
	@Test
    void testValidateVinNumberUniqueness_VinDoesNotExist() {
        String vin = "1HGCM82633A123456";

        when(em.createQuery("SELECT COUNT(c) FROM Car c WHERE c.vinNumber = :vinNumber", Long.class))
            .thenReturn(typedQuery);
        when(typedQuery.setParameter("vinNumber", vin)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);

        assertDoesNotThrow(() -> VinNumberValidator.validateVinNumberUniqueness(vin, em)
        );
        
        verify(em).createQuery("SELECT COUNT(c) FROM Car c WHERE c.vinNumber = :vinNumber", Long.class);
        verify(typedQuery).setParameter("vinNumber", vin);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void testValidateVinNumberUniqueness_VinExists() {
        String vin = "1HGCM82633A123456";

        when(em.createQuery("SELECT COUNT(c) FROM Car c WHERE c.vinNumber = :vinNumber", Long.class))
            .thenReturn(typedQuery);
        when(typedQuery.setParameter("vinNumber", vin)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(1L);

        Exception exception = assertThrows(VinNumberAlreadyExistsExeption.class, () ->
            VinNumberValidator.validateVinNumberUniqueness(vin, em)
        );
        assertEquals("VIN-номер уже существует.", exception.getMessage());
        
        verify(em).createQuery("SELECT COUNT(c) FROM Car c WHERE c.vinNumber = :vinNumber", Long.class);
        verify(typedQuery).setParameter("vinNumber", vin);
        verify(typedQuery).getSingleResult();
    }
}
