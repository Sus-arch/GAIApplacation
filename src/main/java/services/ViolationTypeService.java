package services;

import entities.ViolationType;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

/**
 * Сервис для работы с типами нарушений.
 * Предоставляет методы для добавления, обновления, удаления, поиска и получения всех типов нарушений.
 */
public class ViolationTypeService {
    private EntityManager entityManager;

    /**
     * Конструктор сервиса для работы с типами нарушений.
     * @param entityManager {@link EntityManager} для работы с базой данных.
     */
    public ViolationTypeService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Получение {@link EntityManager}.
     * @return {@link EntityManager} для работы с базой данных.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Получить тип нарушения по названию.
     * @param name Название типа нарушения.
     * @return Объект типа {@link ViolationType}, если найдено, иначе {@code null}.
     */
    public ViolationType getViolationTypeByName(String name) {
        try {
            TypedQuery<ViolationType> query = entityManager.createQuery(
                    "SELECT vt FROM ViolationType vt WHERE vt.violationTypeName = :violationTypeName", ViolationType.class);
            query.setParameter("violationTypeName", name);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;  // Если тип нарушения не найден, возвращаем null
        }
    }

    /**
     * Получить все типы нарушений.
     * @return Список всех типов нарушений.
     */
    public List<ViolationType> getAllViolationTypes() {
        TypedQuery<ViolationType> query = entityManager.createQuery("SELECT vt FROM ViolationType vt ORDER BY vt.violationTypeId", ViolationType.class);
        return query.getResultList();
    }

    /**
     * Добавить новый тип нарушения.
     * @param violationType Тип нарушения для добавления.
     * @throws RuntimeException Если транзакция не может быть завершена.
     */
    public void addViolationType(ViolationType violationType) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(violationType);  // Сохраняем новый тип нарушения
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();  // Откатываем транзакцию в случае ошибки
            }
            throw e;
        }
    }

    /**
     * Обновить существующий тип нарушения.
     * @param violationType Тип нарушения для обновления.
     * @throws RuntimeException Если транзакция не может быть завершена.
     */
    public void updateViolationType(ViolationType violationType) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(violationType);  // Обновляем тип нарушения
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();  // Откатываем транзакцию в случае ошибки
            }
            throw e;
        }
    }

    /**
     * Удалить тип нарушения по названию.
     * @param name Название типа нарушения для удаления.
     * @throws IllegalArgumentException Если тип нарушения с таким названием не найден.
     */
    public void deleteViolationType(String name) {
        ViolationType violationType = getViolationTypeByName(name);
        if (violationType != null) {
            entityManager.getTransaction().begin();
            entityManager.remove(violationType);  // Удаляем тип нарушения
            entityManager.getTransaction().commit();
        } else {
            throw new IllegalArgumentException("Тип нарушения с таким названием не найден.");
        }
    }

    /**
     * Поиск типов нарушений по имени.
     * @param typeName Часть имени типа нарушения для поиска.
     * @return Список типов нарушений, имя которых содержит указанный параметр.
     */
    public List<ViolationType> searchViolationTypes(String typeName) {
        StringBuilder queryBuilder = new StringBuilder("SELECT vt FROM ViolationType vt WHERE 1=1");

        if (typeName != null && !typeName.isEmpty()) {
            queryBuilder.append(" AND vt.violationTypeName LIKE :typeName");
        }

        TypedQuery<ViolationType> query = entityManager.createQuery(queryBuilder.toString(), ViolationType.class);

        if (typeName != null && !typeName.isEmpty()) {
            query.setParameter("typeName", "%" + typeName + "%");
        }

        return query.getResultList();
    }
}
