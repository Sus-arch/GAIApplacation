import javax.persistence.*;

@Entity
@Table(name = "app_db.violation_type")
public class ViolationType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_type_id")
    private Integer violationTypeId;
	
	@Column(name = "violation_type_name", nullable = false, length = 120, unique = true)
	private String violationTypeName;
	
	public Integer getViolationTypeId() {
		return violationTypeId;
	}
	
	public String getViolationTypeName() {
		return violationTypeName;
	}
	
	public void setViolationTypeName(String violationTypeName) {
		this.violationTypeName = violationTypeName;
	}
}
