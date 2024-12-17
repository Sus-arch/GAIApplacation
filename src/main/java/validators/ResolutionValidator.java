package validators;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import exceptions.InvalidResolutionException;
import exceptions.ResolutionAlreadyExistsExeption;

/**
 * Класс для валидации номера постановления.
 * Содержит методы для проверки формата номера постановления и его уникальности в базе данных.
 */
public class ResolutionValidator {

    /**
     * Проверяет корректность формата номера постановления.
     * Номер постановления должен содержать ровно 20 цифр и не содержать других символов.
     *
     * @param resolution номер постановления, который необходимо проверить.
     * @throws InvalidResolutionException если номер постановления не соответствует формату.
     */
    public static void validateResolution(String resolution) throws InvalidResolutionException {
        // Проверка на null и длину
        if (resolution == null || resolution.length() != 20) {
            throw new InvalidResolutionException("Номер постановления должен содержать 20 цифр.");
        }

        // Проверка, что строка состоит только из цифр
        if (!resolution.matches("\\d+")) {
            throw new InvalidResolutionException("Номер постановления должен содержать только числа.");
        }
    }

    /**
     * Проверяет уникальность номера постановления в базе данных.
     * Использует {@link EntityManager} для выполнения запроса к базе данных.
     *
     * @param resolution номер постановления, который необходимо проверить на уникальность.
     * @param em         объект {@link EntityManager} для взаимодействия с базой данных.
     * @throws ResolutionAlreadyExistsExeption если номер постановления уже существует в базе данных.
     */
    public static void validateResolutionUniqueness(String resolution, EntityManager em) throws ResolutionAlreadyExistsExeption {
        // Запрос для проверки количества записей с данным номером постановления
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(v) FROM Violation v WHERE v.violationResolution = :violationResolution", Long.class
        );
        query.setParameter("violationResolution", resolution);
        Long count = query.getSingleResult();

        // Если номер постановления уже существует, выбрасываем исключение
        if (count > 0) {
            throw new ResolutionAlreadyExistsExeption("Номер постановления уже существует.");
        }
    }
}
