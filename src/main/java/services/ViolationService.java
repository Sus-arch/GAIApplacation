package services;

import entities.Violation; 

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для работы с сущностями Нарушение (Violation).
 * Предоставляет методы для получения, добавления, обновления и удаления записей о нарушениях.
 */
public class ViolationService {
    private EntityManager entityManager;
    
    /**
     * Конструктор, инициализирует сервис с использованием переданного EntityManager.
     * 
     * @param entityManager менеджер сущностей для работы с базой данных
     */
    public ViolationService(EntityManager entityManager) {
    	this.entityManager = entityManager;
    }
    
    /**
     * Получение EntityManager.
     * 
     * @return текущий EntityManager
     */
    public EntityManager getEntityManager() {
    	return entityManager;
    }
    
    /**
     * Получение нарушения по номеру постановления.
     * 
     * @param resolution номер постановления
     * @return объект нарушения, если найдено, иначе null
     */
    public Violation getViolationByResolution(String resolution) {
    	try {
    		TypedQuery<Violation> query = entityManager.createQuery(
                    "SELECT v FROM Violation v WHERE v.violationResolution = :violationResolution", Violation.class);
            query.setParameter("violationResolution", resolution);
            return query.getSingleResult();
		} catch (Exception e) {
			// если нарушение с таким номером постановления не найдено, возвращаем null
			return null;
		}
    }
    
    /**
     * Получение списка всех нарушений.
     * 
     * @return список всех нарушений
     */
    public List<Violation> getAllViolations() {
    	TypedQuery<Violation> query = entityManager.createQuery("SELECT v FROM Violation v", Violation.class);
    	return query.getResultList();
    }
    
    /**
     * Добавление нового нарушения в базу данных.
     * 
     * @param violation объект нарушения для добавления
     */
    public void addViolation(Violation violation) {
    	EntityTransaction transaction = entityManager.getTransaction();
    	try {
    		transaction.begin();
    		entityManager.persist(violation);  // сохраняем нарушение в базе данных
    		transaction.commit();  // подтверждаем изменения
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();  // откатываем изменения в случае ошибки
			}
			throw e;
		}
    }
    
    /**
     * Обновление данных нарушения в базе.
     * 
     * @param violation объект нарушения с обновленными данными
     */
    public void updateViolation(Violation violation) {
    	EntityTransaction transaction = entityManager.getTransaction();
    	try {
    		transaction.begin();
    		entityManager.merge(violation);  // слияние обновленного объекта с текущей базой данных
    		transaction.commit();
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();  // откатываем изменения в случае ошибки
			}
			throw e;
		}
    }
    
    /**
     * Удаление нарушения из базы данных по номеру постановления.
     * 
     * @param resolution номер постановления для удаления
     */
    public void deleteViolation(String resolution) {
    	Violation violation = getViolationByResolution(resolution);
    	if (violation != null) {
    		entityManager.getTransaction().begin();
    		entityManager.remove(violation);  // удаляем нарушение из базы данных
    		entityManager.getTransaction().commit();
    	} else {
            throw new IllegalArgumentException("Нарушение с таким номером постановления не найдено.");
    	}
    }
    
    /**
     * Поиск нарушений по различным критериям: дате нарушения, номеру постановления, 
     * номеру автомобиля, типу нарушения, статье нарушения, статусу оплаты.
     * 
     * @param violationDateFrom дата начала диапазона для поиска по дате нарушения
     * @param violationDateTo дата окончания диапазона для поиска по дате нарушения
     * @param resolution номер постановления для поиска
     * @param car номер автомобиля для поиска
     * @param violationType тип нарушения для поиска
     * @param violationArticle статья нарушения для поиска
     * @param isPaid статус оплаты (true - оплачено, false - не оплачено) для поиска
     * @return список нарушений, соответствующих всем критериям
     */
    public List<Violation> searchViolations(String violationDateFrom, String violationDateTo, String resolution, String car, String violationType, String violationArticle, Boolean isPaid) {
	    StringBuilder queryBuilder = new StringBuilder("SELECT v FROM Violation v WHERE 1=1");

	    // добавляем условия поиска в запрос
	    if (violationDateFrom != null && !violationDateFrom.isEmpty()) {
	        queryBuilder.append(" AND v.violationDate >= :violationDateFrom");
	    }
	    if (violationDateTo != null && !violationDateTo.isEmpty()) {
	        queryBuilder.append(" AND v.violationDate <= :violationDateTo");
	    }
	    if (resolution != null && !resolution.isEmpty()) { 
	        queryBuilder.append(" AND v.violationResolution LIKE :resolutionNumber");
	    }
	    if (car != null && !car.isEmpty()) {
	        queryBuilder.append(" AND v.car.licensePlate = :carLicensePlate");
	    }
	    if (violationType != null && !violationType.isEmpty()) {
	        queryBuilder.append(" AND v.violationType.violationTypeName = :violationType");
	    }
	    if (violationArticle != null && !violationArticle.isEmpty()) {
	        queryBuilder.append(" AND v.violationArticle.violationArticleCode = :violationArticle");
	    }
	    if (isPaid != null) {	    	
	    	queryBuilder.append(" AND v.violationPaid = :paymentStatus");
	    }

	    TypedQuery<Violation> query = entityManager.createQuery(queryBuilder.toString(), Violation.class);

	    // устанавливаем параметры для запроса
	    if (violationDateFrom != null && !violationDateFrom.isEmpty()) {
	    	LocalDate DateFrom = LocalDate.parse(violationDateFrom);
	        query.setParameter("violationDateFrom", DateFrom);
	    }
	    if (violationDateTo != null && !violationDateTo.isEmpty()) {
	    	LocalDate DateTo = LocalDate.parse(violationDateTo);
	        query.setParameter("violationDateTo", DateTo);
	    }
	    if (resolution != null && !resolution.isEmpty()) { 
	        query.setParameter("resolutionNumber", "%" + resolution + "%");
	    }
	    if (car != null && !car.isEmpty()) {
	        String carLicensePlate = car.substring(car.indexOf('(') + 1, car.indexOf(')')); 
	        query.setParameter("carLicensePlate", carLicensePlate);
	    }
	    if (violationType != null && !violationType.isEmpty()) {
	        query.setParameter("violationType", violationType);
	    }
	    if (violationArticle != null && !violationArticle.isEmpty()) {
	        query.setParameter("violationArticle", violationArticle);
	    }
	    if (isPaid != null) {	    	
		    query.setParameter("paymentStatus", isPaid);
	    }   

	    return query.getResultList();
	}

}
