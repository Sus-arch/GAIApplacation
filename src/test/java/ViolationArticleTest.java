import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import entities.ViolationArticle;

import static org.junit.jupiter.api.Assertions.*;

public class ViolationArticleTest {
	private ViolationArticle violationArticle;
	
	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        violationArticle = new ViolationArticle();
    }
	
	@Test
	void testSetAndGetViolationArticleCode() {
		String code = "КоАП РФ 12.9 п.2";
		violationArticle.setViolationArticleCode(code);
		assertEquals(code, violationArticle.getViolationArticleCode());
	}
	
	@Test
	void testSetAndGetViolationArticleDescription() {
		String description = "Превышение установленной скорости движения транспортного средства на величину более 20, но не более 40 километров в час";
		violationArticle.setViolationArticleDescription(description);
		assertEquals(description, violationArticle.getViolationArticleDescription());
	}
	
	@Test
	void testSetAndGetViolationArticleFine() {
		Integer fine = 500;
		violationArticle.setViolationArticleFine(fine);
		assertEquals(fine, violationArticle.getViolationArticleFine());
		
	}

}
