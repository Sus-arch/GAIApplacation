import javax.persistence.*;

@Entity
@Table(name = "app_db.violation_article")
public class ViolationArticle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_article_id")
    private Integer violationArticleId;
	
	@Column(name = "violation_article_code", nullable = false, length = 20)
    private String violationArticleCode;
	
	@Column(name = "violation_article_description", length = 255)
    private String violationArticleDescription;
	
	@Column(name = "violation_article_fine", nullable = false)
    private Integer violationArticleFine;
	
	public Integer getViolationArticleId() {
        return violationArticleId;
    }
	
	public String getViolationArticleCode() {
        return violationArticleCode;
    }
	
	public String getViolationArticleDescription() {
        return violationArticleDescription;
    }
	
	public Integer getViolationArticleFine() {
        return violationArticleFine;
    }
	
	public void setViolationArticleCode(String violationArticleCode) {
        this.violationArticleCode = violationArticleCode;
    }
	
	public void setViolationArticleDescription(String violationArticleDescription) {
        this.violationArticleDescription = violationArticleDescription;
    }
	
	public void setViolationArticleFine(Integer violationArticleFine) {
        this.violationArticleFine = violationArticleFine;
    }
}
