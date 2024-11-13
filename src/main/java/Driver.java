import javax.persistence.*;
import java.util.List;
import java.time.LocalDate;


@Entity
@Table(name = "app_db.drivers")
public class Driver {
	
	@Id
    @Column(name = "driver_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer driverId;
	
	@Column(name = "driver_first_name", nullable = false, length = 100)
    private String firstName;
	
	@Column(name = "driver_last_name", nullable = false, length = 100)
    private String lastName;
	
	@Column(name = "driver_middle_name", length = 100)
    private String middleName;
	
	@Column(name = "driver_birthday", nullable = false)
	private LocalDate birthday;
	
    @Column(name = "driver_license_number", unique = true, nullable = false, length = 20)
    private String licenseNumber;
    
    @Column(name = "driver_city", length = 75)
    private String city;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;
    
    public Integer getDriverId() {
    	return driverId;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public String getCity() {
        return city;
    }
    
    public List<Car> getCars() {
        return cars;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}
