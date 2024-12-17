package entities;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

/**
 * Сущность, представляющая автомобиль.
 * Маппится на таблицу "car" в базе данных.
 */
@Entity
@Table(name = "app_db.car")
public class Car {

    /**
     * Идентификатор автомобиля.
     * Это уникальный идентификатор для каждого автомобиля в базе данных.
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "car_id")
	private Integer carId;
	
    /**
     * Марка автомобиля.
     * Обязательное поле, длина строки не более 100 символов.
     */
	@Column(name = "car_brand", nullable = false, length = 100)
	private String brand;
	
    /**
     * Модель автомобиля.
     * Обязательное поле, длина строки не более 100 символов.
     */
	@Column(name = "car_model", nullable = false, length = 100)
    private String model;
	
    /**
     * VIN-номер автомобиля.
     * Обязательное поле, длина строки не более 45 символов.
     */
	@Column(name = "car_vin_number", nullable = false, length = 45)
    private String vinNumber;
	
    /**
     * Госномер автомобиля.
     * Обязательное поле, длина строки не более 45 символов.
     */
	@Column(name = "car_license_plate", nullable = false, length = 45)
    private String licensePlate;
	
    /**
     * Владелец автомобиля (ссылка на сущность Driver).
     * Обязательное поле.
     */
	@ManyToOne
    @JoinColumn(name = "car_owner_id", nullable = false)
    private Driver owner;
	
    /**
     * Дата последней технической проверки автомобиля.
     * Может быть пустым.
     */
	@Column(name = "car_last_vehicle_inspection")
    private LocalDate lastVehicleInspection;
	
    /**
     * Нарушения, связанные с этим автомобилем.
     * Связь с сущностью Violation.
     */
	@OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Violation> violations;

    /**
     * Получить идентификатор автомобиля.
     * @return идентификатор автомобиля.
     */
	public Integer getCarId() {
    	return carId;
    }
	
    /**
     * Получить марку автомобиля.
     * @return марка автомобиля.
     */
	public String getBrand() {
        return brand;
    }

    /**
     * Получить модель автомобиля.
     * @return модель автомобиля.
     */
    public String getModel() {
        return model;
    }

    /**
     * Получить VIN-номер автомобиля.
     * @return VIN-номер автомобиля.
     */
    public String getVinNumber() {
        return vinNumber;
    }

    /**
     * Получить госномер автомобиля.
     * @return госномер автомобиля.
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Получить владельца автомобиля.
     * @return владелец автомобиля.
     */
    public Driver getOwner() {
        return owner;
    }

    /**
     * Получить дату последней технической проверки автомобиля.
     * @return дата последней технической проверки.
     */
    public LocalDate getLastVehicleInspection() {
        return lastVehicleInspection;
    }
    
    /**
     * Получить список нарушений, связанных с автомобилем.
     * @return список нарушений.
     */
    public List<Violation> getViolations() {
        return violations;
    }

    /**
     * Установить марку автомобиля.
     * @param brand марка автомобиля.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Установить модель автомобиля.
     * @param model модель автомобиля.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Установить VIN-номер автомобиля.
     * @param vinNumber VIN-номер автомобиля.
     */
    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    /**
     * Установить госномер автомобиля.
     * @param licensePlate госномер автомобиля.
     */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /**
     * Установить владельца автомобиля.
     * @param owner владелец автомобиля.
     */
    public void setOwner(Driver owner) {
        this.owner = owner;
    }

    /**
     * Установить дату последней технической проверки автомобиля.
     * @param lastVehicleInspection дата последней проверки.
     */
    public void setLastVehicleInspection(LocalDate lastVehicleInspection) {
        this.lastVehicleInspection = lastVehicleInspection;
    }
    
    /**
     * Установить список нарушений, связанных с автомобилем.
     * @param violations список нарушений.
     */
    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
}
