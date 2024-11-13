import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "app_db.car")
public class Car {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "car_id")
	private Integer carId;
	
	@Column(name = "car_brand", nullable = false, length = 100)
	private String brand;
	
	@Column(name = "car_model", nullable = false, length = 100)
    private String model;
	
	@Column(name = "car_vin_number", nullable = false, length = 45)
    private String vinNumber;
	
	@Column(name = "car_license_plate", nullable = false, length = 45)
    private String licensePlate;
	
	@ManyToOne
    @JoinColumn(name = "car_owner_id", nullable = false)
    private Driver owner;
	
	@Column(name = "car_last_vehicle_inspection")
    private LocalDate lastVehicleInspection;
	
	public Integer getCarId() {
    	return carId;
    }
	
	public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public Driver getOwner() {
        return owner;
    }

    public LocalDate getLastVehicleInspection() {
        return lastVehicleInspection;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public void setOwner(Driver owner) {
        this.owner = owner;
    }

    public void setLastVehicleInspection(LocalDate lastVehicleInspection) {
        this.lastVehicleInspection = lastVehicleInspection;
    }
}
