import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import entities.Car;
import entities.Violation;
import entities.ViolationArticle;
import entities.ViolationType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ViolationTest {

    @Mock
    private ViolationArticle violationArticle;
    @Mock
    private Car car;
    @Mock
    private ViolationType violationType;

    private Violation violation;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        violation = new Violation();
    }

    @Test
    void testSetAndGetViolationArticle() {
        violation.setViolationArticle(violationArticle);
        assertEquals(violationArticle, violation.getViolationArticle());
    }

    @Test
    void testSetAndGetCar() {
        violation.setCar(car);
        assertEquals(car, violation.getCar()); 
    }

    @Test
    void testSetAndGetViolationDate() {
        LocalDate date = LocalDate.of(2024, 11, 17);
        violation.setViolationDate(date);
        assertEquals(date, violation.getViolationDate());
    }

    @Test
    void testSetAndGetViolationPaid() {
        violation.setViolationPaid(true);
        assertTrue(violation.getViolationPaid()); 
    }

    @Test
    void testSetAndGetViolationType() {
        violation.setViolationType(violationType);
        assertEquals(violationType, violation.getViolationType()); 
    }
}
