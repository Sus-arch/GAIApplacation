package validators;

import entities.Driver;
import exceptions.InvalidLicenseNumberException;
import exceptions.LicenseAlreadyExistsException;

import javax.persistence.EntityManager;
import java.time.LocalDate;

/**
 * Класс для валидации данных водителя.
 * Проверяет корректность имени, фамилии, отчества, даты рождения, города и номера водительского удостоверения.
 */
public class DriverValidator {

    /**
     * Валидирует данные водителя.
     * Проверяет корректность имени, фамилии, отчества, даты рождения, города и номера ВУ. Если данные не соответствуют
     * требованиям, выбрасывает исключение с описанием всех ошибок.
     *
     * @param driver            объект {@link Driver}, содержащий данные водителя.
     * @param em                объект {@link EntityManager} для проверки уникальности номера ВУ.
     * @param oldLicenseNumber  предыдущий номер ВУ для исключения проверки уникальности, если номер не изменился.
     * @throws Exception если данные водителя содержат ошибки.
     */
    public static void validateDriver(Driver driver, EntityManager em, String oldLicenseNumber) throws Exception {
        StringBuilder errors = new StringBuilder();

        // Валидация имени
        if (driver.getFirstName() == null || driver.getFirstName().trim().isEmpty()) {
            errors.append("Имя не может быть пустым.\n");
        } else if (!driver.getFirstName().matches("[а-яА-Я]+")) {
            errors.append("Имя должно содержать только русские буквы.\n");
        }

        // Валидация фамилии
        if (driver.getLastName() == null || driver.getLastName().trim().isEmpty()) {
            errors.append("Фамилия не может быть пустой.\n");
        } else if (!driver.getLastName().matches("[а-яА-Я]+")) {
            errors.append("Фамилия должна содержать только русские буквы.\n");
        }

        // Валидация отчества (если указано)
        if (driver.getMiddleName() != null && !driver.getMiddleName().trim().isEmpty() &&
            !driver.getMiddleName().matches("[а-яА-Я]+")) {
            errors.append("Отчество должно содержать только русские буквы.\n");
        }

        // Валидация даты рождения
        if (driver.getBirthday() == null) {
            errors.append("Дата рождения должна быть в формате ГГГГ-ММ-ДД.\n");
        } else if (driver.getBirthday().isAfter(LocalDate.now())) {
            errors.append("Дата рождения должна находиться в прошлом.\n");
        } else if (driver.getBirthday().plusYears(18).isAfter(LocalDate.now())) {
            errors.append("Водитель не может быть несовершеннолетним.\n");
        }

        // Валидация города
        if (driver.getCity() == null || driver.getCity().trim().isEmpty()) {
            errors.append("Город не может быть пустым.\n");
        } else if (!driver.getCity().matches("[а-яА-Я-]+")) {
            errors.append("Название города может содержать только русские буквы и тире.\n");
        }

        // Валидация номера ВУ
        if (!driver.getLicenseNumber().equals(oldLicenseNumber)) {
            try {
                // Проверка формата номера ВУ
                LicenseNumberValidator.validateLicenseNumber(driver.getLicenseNumber());
                // Проверка уникальности номера ВУ
                LicenseNumberValidator.validateLicenseUniqueness(driver.getLicenseNumber(), em);
            } catch (InvalidLicenseNumberException | LicenseAlreadyExistsException ex) {
                errors.append(ex.getMessage()).append("\n");
            }
        }

        // Если есть ошибки, выбрасываем исключение
        if (errors.length() > 0) {
            throw new Exception(errors.toString());
        }
    }
}
