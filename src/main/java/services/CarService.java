package services;

import entities.Car;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

/**
 * Сервис для работы с автомобилями.
 * Предоставляет методы для добавления, обновления, удаления и поиска автомобилей.
 */
public class CarService {
    private EntityManager entityManager;
    
    /**
     * Конструктор класса CarService.
     * 
     * @param entityManager объект EntityManager для работы с базой данных.
     */
    public CarService(EntityManager entityManager) {
    	this.entityManager = entityManager;
    }
    
    /**
     * Получение текущего EntityManager.
     * 
     * @return объект EntityManager.
     */
    public EntityManager getEntityManager() {
    	return entityManager;
    }
    
    /**
     * Получение автомобиля по номеру государственного регистрационного знака.
     * 
     * @param licensePlate номер государственного регистрационного знака.
     * @return объект Car, если автомобиль найден, или null, если автомобиль не найден.
     */
    public Car getCarByLicensePlate(String licensePlate) {
    	try {
			TypedQuery<Car> query = entityManager.createQuery(
					"SELECT c FROM Car c WHERE c.licensePlate = :licensePlate", Car.class);
			query.setParameter("licensePlate", licensePlate);
			return query.getSingleResult();
		} catch (Exception e) {
			// В случае ошибки (например, автомобиль не найден), возвращается null.
			return null;
		}
    }
    
    /**
     * Получение списка всех автомобилей.
     * 
     * @return список всех автомобилей.
     */
    public List<Car> getAllCars() {
    	TypedQuery<Car> query = entityManager.createQuery("SELECT c FROM Car c", Car.class);
    	return query.getResultList();
    }
    
    /**
     * Добавление нового автомобиля в базу данных.
     * 
     * @param car объект автомобиля, который необходимо добавить.
     */
    public void addCar(Car car) {
    	EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();  
            entityManager.persist(car);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
    
    /**
     * Обновление данных автомобиля в базе данных.
     * 
     * @param car объект автомобиля с обновленными данными.
     */
    public void updateCar(Car car) {
    	EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin(); 
            entityManager.merge(car);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback(); 
            }
            throw e;
        }
    }
    
    /**
     * Удаление автомобиля по номеру государственного регистрационного знака.
     * 
     * @param licensePlate номер государственного регистрационного знака автомобиля, который нужно удалить.
     * @throws IllegalArgumentException если автомобиль с таким номером не найден.
     */
    public void deleteCar(String licensePlate) {
        Car car = getCarByLicensePlate(licensePlate);
        if (car != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(car);
            entityManager.getTransaction().commit();
        } else {
            throw new IllegalArgumentException("Автомобиль с таким госномером не найден.");
        }
    }
    
    /**
     * Поиск автомобилей по различным параметрам (марка, модель, VIN, номер госзнака, дата последней проверки, владелец).
     * 
     * @param brand марка автомобиля.
     * @param model модель автомобиля.
     * @param vin VIN-номер автомобиля.
     * @param licensePlate номер государственного регистрационного знака.
     * @param lastInspectionDateFrom дата начала интервала последней проверки.
     * @param lastInspectionDateTo дата конца интервала последней проверки.
     * @param owner владелец автомобиля.
     * @return список автомобилей, соответствующих поисковым критериям.
     */
    public List<Car> searchCars(String brand, String model, String vin, String licensePlate, String lastInspectionDateFrom, String lastInspectionDateTo, String owner) {
	    StringBuilder queryBuilder = new StringBuilder("SELECT c FROM Car c WHERE 1=1");

	    // Формирование динамического запроса с учетом непустых параметров
	    if (brand != null && !brand.isEmpty()) {
	        queryBuilder.append(" AND c.brand LIKE :brand");
	    }
	    if (model != null && !model.isEmpty()) {
	        queryBuilder.append(" AND c.model LIKE :model");
	    }
	    if (vin != null && !vin.isEmpty()) {
	        queryBuilder.append(" AND c.vinNumber LIKE :vin");
	    }
	    if (licensePlate != null && !licensePlate.isEmpty()) {
	        queryBuilder.append(" AND c.licensePlate LIKE :licensePlate");
	    }
	    if (lastInspectionDateFrom != null && !lastInspectionDateFrom.isEmpty()) {
	        queryBuilder.append(" AND c.lastVehicleInspection >= :lastInspectionDateFrom");
	    }
	    if (lastInspectionDateTo != null && !lastInspectionDateTo.isEmpty()) {
	        queryBuilder.append(" AND c.lastVehicleInspection <= :lastInspectionDateTo");
	    }
	    if (owner != null && !owner.isEmpty()) {
	        queryBuilder.append(" AND c.owner.licenseNumber = :ownerLicense");
	    }

	    TypedQuery<Car> query = entityManager.createQuery(queryBuilder.toString(), Car.class);

	    // Установка параметров запроса для каждого поля, если оно не пустое
	    if (brand != null && !brand.isEmpty()) {
	        query.setParameter("brand", "%" + brand + "%");
	    }
	    if (model != null && !model.isEmpty()) {
	        query.setParameter("model", "%" + model + "%");
	    }
	    if (vin != null && !vin.isEmpty()) {
	        query.setParameter("vin", "%" + vin + "%");
	    }
	    if (licensePlate != null && !licensePlate.isEmpty()) {
	        query.setParameter("licensePlate", "%" + licensePlate + "%");
	    }
	    if (lastInspectionDateFrom != null && !lastInspectionDateFrom.isEmpty()) {
	        LocalDate inspectionDateFrom = LocalDate.parse(lastInspectionDateFrom);
	        query.setParameter("lastInspectionDateFrom", inspectionDateFrom);
	    }
	    if (lastInspectionDateTo != null && !lastInspectionDateTo.isEmpty()) {
	        LocalDate inspectionDateTo = LocalDate.parse(lastInspectionDateTo);
	        query.setParameter("lastInspectionDateTo", inspectionDateTo);
	    }
	    if (owner != null && !owner.isEmpty()) {
	        String licenseNumber = owner.substring(owner.indexOf('(') + 1, owner.indexOf(')'));
	        query.setParameter("ownerLicense", licenseNumber);
	    }

	    return query.getResultList();
	}
}
