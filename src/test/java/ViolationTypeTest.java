import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import entities.ViolationType;

import static org.junit.jupiter.api.Assertions.*;

public class ViolationTypeTest {
	private ViolationType violationType;
	
	@BeforeEach
	void setup() {
        MockitoAnnotations.openMocks(this);
		violationType = new ViolationType();
	}
	
	@Test
	void testSetAndGetBiolationTypeName() {
		String name = "Превышение установленной скорости движения";
		violationType.setViolationTypeName(name);
		assertEquals(name, violationType.getViolationTypeName());
	}
}
