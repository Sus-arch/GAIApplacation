import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import entities.Driver;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DriverTest {
	private Driver driver;
	
	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        driver = new Driver();
    }
	
	@Test
    void testSetAndGetFirstName() {
		String firstName = "Иван";
		driver.setFirstName(firstName);
		assertEquals(firstName, driver.getFirstName());
    }
	
	@Test
    void testSetAndGetLastName() {
		String lastName = "Иванов";
		driver.setLastName(lastName);
		assertEquals(lastName, driver.getLastName());
    }
	
	@Test
    void testSetAndGetMiddleName() {
		String middleName = "Иванович";
		driver.setMiddleName(middleName);
		assertEquals(middleName, driver.getMiddleName());
    }
	
	@Test
	void testSetAndGetBirthday() {
		LocalDate birthday = LocalDate.of(2024, 11, 17);
		driver.setBirthday(birthday);
		assertEquals(birthday, driver.getBirthday());
	}
	
	@Test
	void testSetAndGetLicenseNumber() {
		String licenseNumber = "1234567890";
		driver.setLicenseNumber(licenseNumber);
		assertEquals(licenseNumber, driver.getLicenseNumber());
	}
	
	@Test
	void testSetAndGetCity() {
		String city = "Киров";
		driver.setCity(city);
		assertEquals(city, driver.getCity());
	}
	
	@Test
	void testGetFullName_WithMiddleName() {
		String firstName = "Иван";
		String middleName = "Иванович";
		String lastName = "Иванов";
		String fullName = lastName + " " + firstName.charAt(0) + "." + middleName.charAt(0);
		driver.setFirstName(firstName);
		driver.setMiddleName(middleName);
		driver.setLastName(lastName);
		assertEquals(fullName, driver.getFullName());
	}
	
	@Test
	void testGetFullName_WithoutMiddleName() {
		String firstName = "Иван";
		String lastName = "Иванов";
		String fullName = lastName + " " + firstName.charAt(0);
		driver.setFirstName(firstName);
		driver.setLastName(lastName);
		assertEquals(fullName, driver.getFullName());
	}

}
