import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "app_db.violation")
public class Violation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_id")
	private Integer violationId;
	
	@OneToOne
	@JoinColumn(name = "violation_article_id", nullable = false)
	private ViolationArticle violationArticle;
	
	@ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
	
	@Column(name = "violation_date", nullable = false)
    private LocalDate violationDate;
	
	@Column(name = "violation_paid", nullable = false)
    private Boolean violationPaid;
	
	@ManyToOne
    @JoinColumn(name = "violation_type_id", nullable = false)
    private ViolationType violationType;
	
	public Integer getViolationId() {
        return violationId;
    }
	
	public ViolationArticle getViolationArticle() {
        return violationArticle;
    }
	
	public Car getCar() {
        return car;
    }
	
	public LocalDate getViolationDate() {
        return violationDate;
    }
	
	public Boolean getViolationPaid() {
        return violationPaid;
    }
	
	public ViolationType getViolationType() {
		return violationType;
	}
	
	public void setViolationArticle(ViolationArticle violationArticle) {
        this.violationArticle = violationArticle;
    }
	
	public void setCar(Car car) {
        this.car = car;
    }
	
	public void setViolationDate(LocalDate violationDate) {
        this.violationDate = violationDate;
    }
	
	public void setViolationPaid(Boolean violationPaid) {
        this.violationPaid = violationPaid;
    }
	
	public void setViolationType(ViolationType violationType) {
        this.violationType = violationType;
    }
}
