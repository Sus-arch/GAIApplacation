package validators;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import exceptions.InvalidLicensePlateException;
import exceptions.LicensePlateAlreadyExistsExeption;

/**
 * Класс для валидации государственного регистрационного номера автомобиля.
 * Содержит методы для проверки формата номера и его уникальности.
 */
public class LicensePlateValidator {

    private static final Logger logger = Logger.getLogger(LicensePlateValidator.class);

    /**
     * Допустимые буквы для государственных регистрационных номеров.
     * Содержит только буквы русского алфавита, используемые в госномерах.
     */
    private static final String VALID_LETTERS = "АВЕКМНОРСТУХ";

    /**
     * Проверяет корректность формата государственного регистрационного номера.
     * Проверяет длину номера, используемые буквы, цифры и код региона.
     *
     * @param licensePlate государственный регистрационный номер, который необходимо проверить.
     * @throws InvalidLicensePlateException если номер не соответствует формату.
     */
    public static void validateLicensePlate(String licensePlate) throws InvalidLicensePlateException {
        logger.debug("Валидация госномера: " + licensePlate);

        // Проверка длины госномера
        if (licensePlate == null || licensePlate.length() < 8 || licensePlate.length() > 9) {
            logger.warn("Ошибка валидации: Неверная длина госномера: " + licensePlate);

            throw new InvalidLicensePlateException("Номерной знак должен содержать от 8 до 9 символов.");
        }

        // Проверка допустимых букв в определенных позициях номера
        if (!VALID_LETTERS.contains(String.valueOf(licensePlate.charAt(0))) ||
            !VALID_LETTERS.contains(String.valueOf(licensePlate.charAt(4))) ||
            !VALID_LETTERS.contains(String.valueOf(licensePlate.charAt(5)))) {
            logger.warn("Ошибка валидации: Неверные символы в госномере: " + licensePlate);

            throw new InvalidLicensePlateException("Неверные символы в номерном знаке.");
        }

        // Проверка, что позиции 1-3 содержат цифры
        for (int i = 1; i < 4; i++) {
            if (!Character.isDigit(licensePlate.charAt(i))) {
                logger.warn("Ошибка валидации: Неверные цифры в госномере: " + licensePlate);

                throw new InvalidLicensePlateException("Неверные цифры в номерном знаке.");
            }
        }

        // Проверка, что код региона (позиции 6 и далее) содержит только цифры
        for (int i = 6; i < licensePlate.length(); i++) {
            if (!Character.isDigit(licensePlate.charAt(i))) {
                logger.warn("Ошибка валидации: Неверный код региона в госномере: " + licensePlate);

                throw new InvalidLicensePlateException("Неверный код региона в номерном знаке.");
            }
        }

        logger.info("Госномер успешно прошёл валидацию: " + licensePlate);
    }

    /**
     * Проверяет уникальность государственного регистрационного номера в базе данных.
     * Использует {@link EntityManager} для выполнения запроса к базе данных.
     *
     * @param licensePlate государственный регистрационный номер, который необходимо проверить на уникальность.
     * @param em           объект {@link EntityManager} для взаимодействия с базой данных.
     * @throws LicensePlateAlreadyExistsExeption если номер уже существует в базе данных.
     */
    public static void validateLicensePlateUniqueness(String licensePlate, EntityManager em) throws LicensePlateAlreadyExistsExeption {
        logger.debug("Проверка на уникальность госномера: " + licensePlate);

        // Выполняем запрос для проверки наличия госномера в базе данных
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM Car c WHERE c.licensePlate = :licensePlate", Long.class);
        query.setParameter("licensePlate", licensePlate);
        Long count = query.getSingleResult();

        // Если госномер уже существует, выбрасываем исключение
        if (count > 0) {
            logger.warn("Ошибка валидации: Госномер уже существует: " + licensePlate);

            throw new LicensePlateAlreadyExistsExeption("Госномер уже существует.");
        }

        logger.info("Госномер успешно прошёл проверку на уникальность: " + licensePlate);
    }
}
