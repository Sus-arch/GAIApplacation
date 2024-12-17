package entities;

import java.util.List;
import javax.persistence.*;

/**
 * Сущность, представляющая статью нарушения.
 * Маппится на таблицу "violation_article" в базе данных.
 */
@Entity
@Table(name = "app_db.violation_article")
public class ViolationArticle {

    /**
     * Идентификатор статьи нарушения.
     * Уникальный идентификатор для каждой статьи нарушения в базе данных.
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_article_id")
    private Integer violationArticleId;
	
    /**
     * Код статьи нарушения.
     * Обязательное поле для идентификации статьи.
     */
	@Column(name = "violation_article_code", nullable = false, length = 20)
    private String violationArticleCode;
	
    /**
     * Описание статьи нарушения.
     * Обязательное поле, которое содержит описание нарушения.
     */
	@Column(name = "violation_article_description", nullable = false, length = 255)
    private String violationArticleDescription;
	
    /**
     * Размер штрафа по статье нарушения.
     * Обязательное поле для определения штрафа, связанного с нарушением.
     */
	@Column(name = "violation_article_fine", nullable = false)
    private Integer violationArticleFine;
	
    /**
     * Список нарушений, связанных с этой статьей.
     * Связь с сущностью Violation.
     */
	@OneToMany(mappedBy = "violationArticle", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Violation> violations;

    /**
     * Получить идентификатор статьи нарушения.
     * @return идентификатор статьи.
     */
	public Integer getViolationArticleId() {
        return violationArticleId;
    }
	
    /**
     * Получить код статьи нарушения.
     * @return код статьи.
     */
	public String getViolationArticleCode() {
        return violationArticleCode;
    }
	
    /**
     * Получить описание статьи нарушения.
     * @return описание статьи.
     */
	public String getViolationArticleDescription() {
        return violationArticleDescription;
    }
	
    /**
     * Получить размер штрафа по статье нарушения.
     * @return размер штрафа.
     */
	public Integer getViolationArticleFine() {
        return violationArticleFine;
    }
	
    /**
     * Получить список нарушений, связанных с этой статьей.
     * @return список нарушений.
     */
	public List<Violation> getViolations() {
        return violations;
    }
	
    /**
     * Установить код статьи нарушения.
     * @param violationArticleCode код статьи.
     */
	public void setViolationArticleCode(String violationArticleCode) {
        this.violationArticleCode = violationArticleCode;
    }
	
    /**
     * Установить описание статьи нарушения.
     * @param violationArticleDescription описание статьи.
     */
	public void setViolationArticleDescription(String violationArticleDescription) {
        this.violationArticleDescription = violationArticleDescription;
    }
	
    /**
     * Установить размер штрафа по статье нарушения.
     * @param violationArticleFine размер штрафа.
     */
	public void setViolationArticleFine(Integer violationArticleFine) {
        this.violationArticleFine = violationArticleFine;
    }
	
    /**
     * Установить список нарушений, связанных с этой статьей.
     * @param violations список нарушений.
     */
	public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
}
