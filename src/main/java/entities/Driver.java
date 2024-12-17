package entities;

import javax.persistence.*;
import java.util.List;
import java.time.LocalDate;

/**
 * Сущность, представляющая водителя.
 * Маппится на таблицу "drivers" в базе данных.
 */
@Entity
@Table(name = "app_db.drivers")
public class Driver {

    /**
     * Идентификатор водителя.
     * Это уникальный идентификатор для каждого водителя в базе данных.
     */
	@Id
    @Column(name = "driver_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer driverId;
	
    /**
     * Имя водителя.
     * Обязательное поле, длина строки не более 100 символов.
     */
	@Column(name = "driver_first_name", nullable = false, length = 100)
    private String firstName;
	
    /**
     * Фамилия водителя.
     * Обязательное поле, длина строки не более 100 символов.
     */
	@Column(name = "driver_last_name", nullable = false, length = 100)
    private String lastName;
	
    /**
     * Отчество водителя.
     * Может быть пустым, длина строки не более 100 символов.
     */
	@Column(name = "driver_middle_name", length = 100)
    private String middleName;
	
    /**
     * Дата рождения водителя.
     * Обязательное поле.
     */
	@Column(name = "driver_birthday", nullable = false)
	private LocalDate birthday;
    
    /**
     * Номер водительского удостоверения.
     * Обязательное поле, уникальное значение, длина строки не более 20 символов.
     */
    @Column(name = "driver_license_number", unique = true, nullable = false, length = 20)
    private String licenseNumber;
    
    /**
     * Город проживания водителя.
     * Может быть пустым, длина строки не более 75 символов.
     */
    @Column(name = "driver_city", length = 75)
    private String city;
    
    /**
     * Список автомобилей, принадлежащих водителю.
     * Связь с сущностью Car.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    /**
     * Получить идентификатор водителя.
     * @return идентификатор водителя.
     */
    public Integer getDriverId() {
    	return driverId;
    }

    /**
     * Получить имя водителя.
     * @return имя водителя.
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Получить фамилию водителя.
     * @return фамилия водителя.
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Получить отчество водителя.
     * @return отчество водителя, если оно указано.
     */
    public String getMiddleName() {
        return middleName;
    }
    
    /**
     * Получить дату рождения водителя.
     * @return дата рождения водителя.
     */
    public LocalDate getBirthday() {
        return birthday;
    }
    
    /**
     * Получить номер водительского удостоверения.
     * @return номер водительского удостоверения.
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    /**
     * Получить город проживания водителя.
     * @return город проживания водителя.
     */
    public String getCity() {
        return city;
    }
    
    /**
     * Получить список автомобилей, принадлежащих водителю.
     * @return список автомобилей.
     */
    public List<Car> getCars() {
        return cars;
    }
    
    /**
     * Получить полное имя водителя (фамилия, имя, отчество).
     * Если отчество отсутствует, оно не включается в строку.
     * @return полное имя водителя.
     */
    public String getFullName() {
        String fullName = this.lastName + " " + this.firstName.charAt(0);
        
        // Добавляем первую букву отчества, если оно указано
        if (this.middleName != null && !this.middleName.isEmpty()) {
            fullName += "." + this.middleName.charAt(0);
        }

        return fullName;
    }

    /**
     * Установить имя водителя.
     * @param firstName имя водителя.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Установить фамилию водителя.
     * @param lastName фамилия водителя.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Установить отчество водителя.
     * @param middleName отчество водителя.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    /**
     * Установить дату рождения водителя.
     * @param birthday дата рождения водителя.
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * Установить номер водительского удостоверения.
     * @param licenseNumber номер водительского удостоверения.
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * Установить город проживания водителя.
     * @param city город проживания водителя.
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /**
     * Установить список автомобилей, принадлежащих водителю.
     * @param cars список автомобилей.
     */
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }
}
