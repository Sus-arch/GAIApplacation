package services;

import entities.Driver;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

/**
 * Класс, предоставляющий услуги для работы с данными водителей.
 * Выполняет операции добавления, обновления, удаления и поиска водителей в базе данных.
 */
public class DriverService {
    private EntityManager entityManager;

    /**
     * Конструктор, инициализирует объект DriverService с переданным EntityManager.
     *
     * @param entityManager Менеджер сущностей для работы с базой данных.
     */
    public DriverService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Получает EntityManager, используемый для работы с базой данных.
     *
     * @return EntityManager для работы с сущностями.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Получает водителя по номеру водительского удостоверения.
     *
     * @param licenseNumber Номер водительского удостоверения.
     * @return Водитель, если найден, иначе null.
     */
    public Driver getDriverByLicense(String licenseNumber) {
        try {
            TypedQuery<Driver> query = entityManager.createQuery(
                    "SELECT d FROM Driver d WHERE d.licenseNumber = :license", Driver.class);
            query.setParameter("license", licenseNumber);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;  // Если водитель не найден, возвращаем null
        }
    }

    /**
     * Получает всех водителей из базы данных.
     *
     * @return Список всех водителей.
     */
    public List<Driver> getAllDrivers() {
        TypedQuery<Driver> query = entityManager.createQuery("SELECT d FROM Driver d", Driver.class);
        return query.getResultList();
    }

    /**
     * Добавляет нового водителя в базу данных.
     *
     * @param driver Водитель, которого нужно добавить.
     */
    public void addDriver(Driver driver) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();  
            entityManager.persist(driver);  // Сохраняем водителя в базе данных
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();  // Если произошла ошибка, откатываем транзакцию
            }
            throw e;
        }
    }

    /**
     * Обновляет информацию о водителе в базе данных.
     *
     * @param driver Водитель с обновленной информацией.
     */
    public void updateDriver(Driver driver) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin(); 
            entityManager.merge(driver);  // Обновляем существующего водителя
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();  // Если произошла ошибка, откатываем транзакцию
            }
            throw e;
        }
    }

    /**
     * Удаляет водителя по номеру водительского удостоверения.
     *
     * @param licenseNumber Номер водительского удостоверения.
     * @throws IllegalArgumentException Если водитель с таким номером ВУ не найден.
     */
    public void deleteDriver(String licenseNumber) {
        Driver driver = getDriverByLicense(licenseNumber);
        if (driver != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(driver);  // Удаляем водителя из базы данных
            entityManager.getTransaction().commit();
        } else {
            throw new IllegalArgumentException("Водитель с таким номером ВУ не найден.");
        }
    }

    /**
     * Производит поиск водителей по различным параметрам.
     *
     * @param firstName Имя водителя.
     * @param lastName Фамилия водителя.
     * @param middleName Отчество водителя.
     * @param licenseNumber Номер водительского удостоверения.
     * @param city Город водителя.
     * @param fromDate Дата рождения (с).
     * @param toDate Дата рождения (по).
     * @return Список водителей, удовлетворяющих заданным условиям.
     */
    public List<Driver> searchDrivers(String firstName, String lastName, String middleName, String licenseNumber, String city, String fromDate, String toDate) {
        StringBuilder queryBuilder = new StringBuilder("SELECT d FROM Driver d WHERE 1=1");

        // Добавляем условия поиска для каждого параметра, если они не пустые
        if (firstName != null && !firstName.isEmpty()) {
            queryBuilder.append(" AND d.firstName LIKE :firstName");
        }
        if (lastName != null && !lastName.isEmpty()) {
            queryBuilder.append(" AND d.lastName LIKE :lastName");
        }
        if (middleName != null && !middleName.isEmpty()) {
            queryBuilder.append(" AND d.middleName LIKE :middleName");
        }
        if (licenseNumber != null && !licenseNumber.isEmpty()) {
            queryBuilder.append(" AND d.licenseNumber LIKE :licenseNumber");
        }
        if (city != null && !city.isEmpty()) {
            queryBuilder.append(" AND d.city LIKE :city");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
	        queryBuilder.append(" AND d.birthday >= :fromDate");
	    }
	    if (toDate != null && !toDate.isEmpty()) {
	        queryBuilder.append(" AND d.birthday <= :toDate");
	    }

        TypedQuery<Driver> query = entityManager.createQuery(queryBuilder.toString(), Driver.class);

        // Устанавливаем параметры запроса, если они не пустые
        if (firstName != null && !firstName.isEmpty()) {
            query.setParameter("firstName", "%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            query.setParameter("lastName", "%" + lastName + "%");
        }
        if (middleName != null && !middleName.isEmpty()) {
	        query.setParameter("middleName", "%" + middleName + "%");
        }
        if (licenseNumber != null && !licenseNumber.isEmpty()) {
            query.setParameter("licenseNumber", "%" + licenseNumber + "%");
        }
        if (city != null && !city.isEmpty()) {
            query.setParameter("city", "%" + city + "%");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
        	LocalDate fromLocalDate = LocalDate.parse(fromDate);
	        query.setParameter("fromDate", fromLocalDate);
	    }
	    if (toDate != null && !toDate.isEmpty()) {
	    	LocalDate toLocalDate = LocalDate.parse(toDate);
	        query.setParameter("toDate", toLocalDate);
	    }

        return query.getResultList();  // Возвращаем результаты поиска
    }
}
