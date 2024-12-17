package validators;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import exceptions.InvalidVinNumberException;
import exceptions.VinNumberAlreadyExistsExeption;

/**
 * Класс для валидации VIN-номеров.
 * Содержит методы для проверки формата VIN-номера и его уникальности в базе данных.
 */
public class VinNumberValidator {

    private static final Logger logger = Logger.getLogger(VinNumberValidator.class);

    /**
     * Недопустимые символы в VIN-номере.
     * Символы I, O и Q запрещены в соответствии с международными стандартами.
     */
    private static final String FORBIDDEN_CHARS = "IOQ";

    /**
     * Проверяет корректность формата VIN-номера.
     * VIN-номер должен содержать 17 символов, не содержать запрещенных символов (I, O, Q)
     * и включать только буквы и цифры.
     *
     * @param vin VIN-номер, который необходимо проверить.
     * @throws InvalidVinNumberException если VIN-номер не соответствует формату.
     */
    public static void validateVin(String vin) throws InvalidVinNumberException {
        logger.debug("Валидация VIN-номера: " + vin);

        // Проверка длины VIN-номера
        if (vin == null || vin.length() != 17) {
            logger.warn("Ошибка валидации: Неверная длина VIN-номера: " + vin);
            throw new InvalidVinNumberException("VIN должен содержать 17 символов.");
        }

        // Проверка на наличие запрещенных символов и недопустимых символов
        for (char c : vin.toCharArray()) {
            if (FORBIDDEN_CHARS.indexOf(c) != -1) {
                logger.warn("Ошибка валидации: Неверные символы в VIN-номере: " + vin);
                throw new InvalidVinNumberException("VIN не должен содержать символы I, O, Q.");
            }

            if (!Character.isLetterOrDigit(c)) {
                logger.warn("Ошибка валидации: VIN-номер содержит недопустимые символы: " + vin);
                throw new InvalidVinNumberException("VIN должен содержать только буквы и цифры.");
            }
        }

        logger.info("VIN-номер успешно прошёл валидацию: " + vin);
    }

    /**
     * Проверяет уникальность VIN-номера в базе данных.
     * Использует {@link EntityManager} для выполнения запроса к базе данных.
     *
     * @param vinNumber VIN-номер, который необходимо проверить на уникальность.
     * @param em        объект {@link EntityManager} для взаимодействия с базой данных.
     * @throws VinNumberAlreadyExistsExeption если VIN-номер уже существует в базе данных.
     */
    public static void validateVinNumberUniqueness(String vinNumber, EntityManager em) throws VinNumberAlreadyExistsExeption {
        logger.debug("Проверка на уникальность VIN-номера: " + vinNumber);

        // Выполняем запрос для проверки уникальности VIN-номера
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Car c WHERE c.vinNumber = :vinNumber", Long.class);
        query.setParameter("vinNumber", vinNumber);
        Long count = query.getSingleResult();

        // Если VIN-номер уже существует, выбрасываем исключение
        if (count > 0) {
            logger.warn("Ошибка валидации: VIN-номер уже существует: " + vinNumber);
            throw new VinNumberAlreadyExistsExeption("VIN-номер уже существует.");
        }

        logger.info("VIN-номер успешно прошёл проверку на уникальность: " + vinNumber);
    }
}
