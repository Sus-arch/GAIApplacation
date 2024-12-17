package validators;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import exceptions.InvalidLicenseNumberException;
import exceptions.LicenseAlreadyExistsException;

/**
 * Класс для валидации номера водительского удостоверения (ВУ).
 * Содержит методы для проверки формата номера ВУ и его уникальности.
 */
public class LicenseNumberValidator {

    private static final Logger logger = Logger.getLogger(LicenseNumberValidator.class);

    /**
     * Проверяет корректность формата номера ВУ.
     * Номер ВУ должен состоять из 10 цифр.
     *
     * @param licenseNumber номер ВУ, который необходимо проверить.
     * @throws InvalidLicenseNumberException если номер ВУ не соответствует формату.
     */
    public static void validateLicenseNumber(String licenseNumber) throws InvalidLicenseNumberException {
        logger.debug("Валидация номера ВУ: " + licenseNumber);

        // Проверка длины и формата номера ВУ
        if (licenseNumber.length() != 10 || !licenseNumber.matches("\\d+")) {
            logger.warn("Ошибка валидации: Неверный номер ВУ: " + licenseNumber);

            throw new InvalidLicenseNumberException("Номер ВУ должен состоять только из 10 цифр.");
        }

        logger.info("Номер ВУ успешно прошёл валидацию: " + licenseNumber);
    }

    /**
     * Проверяет уникальность номера ВУ в базе данных.
     * Использует {@link EntityManager} для выполнения запроса к базе данных.
     *
     * @param license номер ВУ, который необходимо проверить на уникальность.
     * @param em      объект {@link EntityManager} для взаимодействия с базой данных.
     * @throws LicenseAlreadyExistsException если номер ВУ уже существует в базе данных.
     */
    public static void validateLicenseUniqueness(String license, EntityManager em) throws LicenseAlreadyExistsException {
        logger.debug("Проверка на уникальность номера ВУ: " + license);

        // Запрос для проверки наличия номера ВУ в базе данных
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(d) FROM Driver d WHERE d.licenseNumber = :license", Long.class);
        query.setParameter("license", license);
        Long count = query.getSingleResult();

        // Если такой номер ВУ уже существует, выбрасываем исключение
        if (count > 0) {
            logger.warn("Ошибка валидации: Номер ВУ уже существует: " + license);

            throw new LicenseAlreadyExistsException("Номер ВУ уже существует.");
        }

        logger.info("Номер ВУ успешно прошёл проверку на уникальность: " + license);
    }
}
