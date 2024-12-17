import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import entities.Car;
import entities.Driver;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CarTest {
	@Mock
	private Driver driver;
	
	private Car car;
	
	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        car = new Car();
    }
	
	@Test
	void testSetAndGetBrand() {
		String brand = "BMW";
		car.setBrand(brand);
		assertEquals(brand, car.getBrand());
	}
	
	@Test
	void testSetAndGetModel() {
		String model = "X7";
		car.setModel(model);
		assertEquals(model, car.getModel());
	}
	
	@Test
	void testSetAndGetVinNumber() {
		String vinNumber = "1HGBH41JXMN109186";
		car.setVinNumber(vinNumber);
		assertEquals(vinNumber, car.getVinNumber());
	}
	
	@Test
	void testSetAndGetLicensePlate() {
		String licensePlate = "Е289МА178";
		car.setLicensePlate(licensePlate);
		assertEquals(licensePlate, car.getLicensePlate());
	}
	
	@Test
	void testSetAndGetOwner() {
		car.setOwner(driver);
		assertEquals(driver, car.getOwner());
	}
	
	@Test
	void testSetAndGetLastVehicleInspection() {
		LocalDate lastVehicleInspection = LocalDate.of(2024, 10, 10);
		car.setLastVehicleInspection(lastVehicleInspection);
		assertEquals(lastVehicleInspection, car.getLastVehicleInspection());
	}

}
