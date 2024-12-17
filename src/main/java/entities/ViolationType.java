package entities;

import java.util.List;
import javax.persistence.*;

/**
 * Сущность, представляющая тип нарушения.
 * Маппится на таблицу "violation_type" в базе данных.
 */
@Entity
@Table(name = "app_db.violation_type")
public class ViolationType {

    /**
     * Идентификатор типа нарушения.
     * Уникальный идентификатор для каждого типа нарушения.
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_type_id")
    private Integer violationTypeId;
	
    /**
     * Название типа нарушения.
     * Обязательное поле для указания типа нарушения, которое должно быть уникальным.
     */
	@Column(name = "violation_type_name", nullable = false, length = 120, unique = true)
	private String violationTypeName;
	
    /**
     * Список нарушений, связанных с этим типом.
     * Связь с сущностью Violation.
     */
	@OneToMany(mappedBy = "violationType", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Violation> violations;

    /**
     * Получить идентификатор типа нарушения.
     * @return идентификатор типа нарушения.
     */
	public Integer getViolationTypeId() {
		return violationTypeId;
	}
	
    /**
     * Получить название типа нарушения.
     * @return название типа нарушения.
     */
	public String getViolationTypeName() {
		return violationTypeName;
	}
	
    /**
     * Получить список нарушений, связанных с этим типом.
     * @return список нарушений.
     */
	public List<Violation> getViolations() {
        return violations;
    }
	
    /**
     * Установить название типа нарушения.
     * @param violationTypeName название типа нарушения.
     */
	public void setViolationTypeName(String violationTypeName) {
		this.violationTypeName = violationTypeName;
	}
	
    /**
     * Установить список нарушений, связанных с этим типом.
     * @param violations список нарушений.
     */
	public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
}
