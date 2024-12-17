package services;

import entities.ViolationArticle;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

/**
 * Сервис для работы с статьями нарушений.
 * Содержит методы для добавления, обновления, удаления и поиска статей нарушений.
 */
public class ViolationArticleService {
    private EntityManager entityManager;

    /**
     * Конструктор, инициализирует объект ViolationArticleService с переданным EntityManager.
     *
     * @param entityManager Менеджер сущностей для работы с базой данных.
     */
    public ViolationArticleService(EntityManager entityManager) {
    	this.entityManager = entityManager;
    }
    
    /**
     * Возвращает объект EntityManager, который используется для работы с базой данных.
     *
     * @return EntityManager для работы с базой данных.
     */
    public EntityManager getEntityManager() {
    	return entityManager;
    }
    
    /**
     * Находит статью нарушения по коду статьи.
     * Возвращает null, если статья не найдена.
     *
     * @param code Код статьи нарушения.
     * @return Статья нарушения с заданным кодом, или null, если не найдена.
     */
    public ViolationArticle getViolationArticleByCode(String code) {
    	try {
			TypedQuery<ViolationArticle> query = entityManager.createQuery(
					"SELECT va FROM ViolationArticle va WHERE va.violationArticleCode = :violationArticleCode", ViolationArticle.class);
			query.setParameter("violationArticleCode", code);
			return query.getSingleResult();  // Возвращаем найденную статью нарушения
		} catch (Exception e) {
			// В случае ошибки или отсутствия статьи возвращаем null
			return null;
		}
    }
    
    /**
     * Возвращает все статьи нарушений из базы данных.
     *
     * @return Список всех статей нарушений.
     */
    public List<ViolationArticle> getAllViolationArticles() {
    	TypedQuery<ViolationArticle> query = entityManager.createQuery("SELECT va FROM ViolationArticle va", ViolationArticle.class);
    	return query.getResultList();  // Возвращаем все статьи нарушений
    }
    
    /**
     * Добавляет новую статью нарушения в базу данных.
     *
     * @param violationArticle Статья нарушения, которую нужно добавить.
     */
    public void addViolationArticle(ViolationArticle violationArticle) {
    	EntityTransaction transaction = entityManager.getTransaction();
    	try {
    		transaction.begin();  // Начинаем транзакцию
            entityManager.persist(violationArticle);  // Сохраняем статью нарушения
            transaction.commit();  // Подтверждаем транзакцию
		} catch (Exception e) {
			// В случае ошибки откатываем транзакцию
			if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;  // Бросаем исключение дальше
		}
    }
    
    /**
     * Обновляет существующую статью нарушения в базе данных.
     *
     * @param violationArticle Статья нарушения с обновленными данными.
     */
    public void updateViolationArticle(ViolationArticle violationArticle) {
    	EntityTransaction transaction = entityManager.getTransaction();
    	try {
    		transaction.begin();  // Начинаем транзакцию
            entityManager.merge(violationArticle);  // Обновляем статью нарушения
            transaction.commit();  // Подтверждаем транзакцию
		} catch (Exception e) {
			// В случае ошибки откатываем транзакцию
			if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;  // Бросаем исключение дальше
		}
    }
    
    /**
     * Удаляет статью нарушения по коду статьи.
     *
     * @param code Код статьи нарушения, которую нужно удалить.
     * @throws IllegalArgumentException Если статья с указанным кодом не найдена.
     */
    public void deleteViolationArticle(String code) {
    	ViolationArticle violationArticle = getViolationArticleByCode(code);
    	if (violationArticle != null) {
    		entityManager.getTransaction().begin();  // Начинаем транзакцию
    		entityManager.remove(violationArticle);  // Удаляем статью нарушения
    		entityManager.getTransaction().commit();  // Подтверждаем транзакцию
    	} else {
            throw new IllegalArgumentException("Статья нарушения с таким кодом не найдена.");  // Бросаем исключение, если статья не найдена
    	}
    }
    
    /**
     * Выполняет поиск статей нарушений по заданным критериям.
     * Все параметры являются необязательными.
     *
     * @param articleCode  Код статьи нарушения для поиска.
     * @param description  Описание статьи нарушения для поиска.
     * @param fineFrom     Минимальная сумма штрафа для поиска.
     * @param fineTo       Максимальная сумма штрафа для поиска.
     * @return Список статей нарушений, удовлетворяющих критериям поиска.
     */
    public List<ViolationArticle> searchViolationArticles(String articleCode, String description, String fineFrom, String fineTo) {
	    StringBuilder queryBuilder = new StringBuilder("SELECT va FROM ViolationArticle va WHERE 1=1");

	    // Добавляем условия поиска по каждому параметру, если он не пуст
	    if (articleCode != null && !articleCode.isEmpty()) {
	        queryBuilder.append(" AND va.violationArticleCode LIKE :articleCode");
	    }
	    if (description != null && !description.isEmpty()) {
	        queryBuilder.append(" AND va.violationArticleDescription LIKE :description");
	    }
	    if (fineFrom != null && !fineFrom.isEmpty()) {
	    	queryBuilder.append(" AND va.violationArticleFine >= :fineFrom");
	    }
	    if (fineTo != null && !fineTo.isEmpty()) {
	    	queryBuilder.append(" AND va.violationArticleFine <= :fineTo");
	    }

	    TypedQuery<ViolationArticle> query = entityManager.createQuery(queryBuilder.toString(), ViolationArticle.class);

	    // Устанавливаем параметры для запроса
	    if (articleCode != null && !articleCode.isEmpty()) {
	        query.setParameter("articleCode", "%" + articleCode + "%");
	    }
	    if (description != null && !description.isEmpty()) {
	        query.setParameter("description", "%" + description + "%");
	    }
	    if (fineFrom != null && !fineFrom.isEmpty()) {
	    	query.setParameter("fineFrom", Integer.parseInt(fineFrom));
	    }
	    if (fineTo != null && !fineTo.isEmpty()) {
	    	query.setParameter("fineTo", Integer.parseInt(fineTo));
	    }

	    return query.getResultList();  // Возвращаем список найденных статей нарушений
	}
}
