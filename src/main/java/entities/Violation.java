package entities;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Сущность, представляющая нарушение правил.
 * Маппится на таблицу "violation" в базе данных.
 */
@Entity
@Table(name = "app_db.violation")
public class Violation {

    /**
     * Идентификатор нарушения.
     * Уникальный идентификатор для каждого нарушения в базе данных.
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_id")
	private Integer violationId;
	
    /**
     * Статья, по которой было зафиксировано нарушение.
     * Связь с сущностью ViolationArticle.
     */
	@OneToOne
	@JoinColumn(name = "violation_article_id", nullable = false)
	private ViolationArticle violationArticle;
	
    /**
     * Автомобиль, с которым связано нарушение.
     * Связь с сущностью Car.
     */
	@ManyToOne
	@JoinColumn(name = "car_id", nullable = false)
	private Car car;
	
    /**
     * Резолюция по нарушению (например, штраф или предписание).
     * Обязательное поле.
     */
	@Column(name = "violation_resolution", nullable = false)
    private String violationResolution;
	
    /**
     * Дата, когда было зафиксировано нарушение.
     * Обязательное поле.
     */
	@Column(name = "violation_date", nullable = false)
    private LocalDate violationDate;
	
    /**
     * Флаг, указывающий, было ли нарушение оплачено.
     * Обязательное поле.
     */
	@Column(name = "violation_paid", nullable = false)
    private Boolean violationPaid;
	
    /**
     * Тип нарушения.
     * Связь с сущностью ViolationType.
     */
	@ManyToOne
	@JoinColumn(name = "violation_type_id", nullable = false)
	private ViolationType violationType;

    /**
     * Получить идентификатор нарушения.
     * @return идентификатор нарушения.
     */
	public Integer getViolationId() {
        return violationId;
    }
	
    /**
     * Получить статью, по которой было зафиксировано нарушение.
     * @return статья нарушения.
     */
	public ViolationArticle getViolationArticle() {
        return violationArticle;
    }
	
    /**
     * Получить автомобиль, с которым связано нарушение.
     * @return автомобиль.
     */
	public Car getCar() {
        return car;
    }
	
    /**
     * Получить резолюцию по нарушению.
     * @return резолюция.
     */
	public String getViolationResolution() { 
		return violationResolution;
	}
	
    /**
     * Получить дату, когда было зафиксировано нарушение.
     * @return дата нарушения.
     */
	public LocalDate getViolationDate() {
        return violationDate;
    }
	
    /**
     * Получить флаг, указывающий, было ли нарушение оплачено.
     * @return true, если нарушение оплачено, иначе false.
     */
	public Boolean getViolationPaid() {
        return violationPaid;
    }
	
    /**
     * Получить тип нарушения.
     * @return тип нарушения.
     */
	public ViolationType getViolationType() {
		return violationType;
	}
	
    /**
     * Установить резолюцию по нарушению.
     * @param resolution резолюция нарушения.
     */
	public void setViolationResolution(String resolution) { 
		this.violationResolution = resolution;
	}
	
    /**
     * Установить статью, по которой было зафиксировано нарушение.
     * @param violationArticle статья нарушения.
     */
	public void setViolationArticle(ViolationArticle violationArticle) {
        this.violationArticle = violationArticle;
    }
	
    /**
     * Установить автомобиль, с которым связано нарушение.
     * @param car автомобиль.
     */
	public void setCar(Car car) {
        this.car = car;
    }
	
    /**
     * Установить дату нарушения.
     * @param violationDate дата нарушения.
     */
	public void setViolationDate(LocalDate violationDate) {
        this.violationDate = violationDate;
    }
	
    /**
     * Установить флаг оплаты нарушения.
     * @param violationPaid true, если нарушение оплачено, иначе false.
     */
	public void setViolationPaid(Boolean violationPaid) {
        this.violationPaid = violationPaid;
    }
	
    /**
     * Установить тип нарушения.
     * @param violationType тип нарушения.
     */
	public void setViolationType(ViolationType violationType) {
        this.violationType = violationType;
    }
}
