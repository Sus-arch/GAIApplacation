package validators;

import entities.Violation;
import exceptions.InvalidResolutionException;
import exceptions.ResolutionAlreadyExistsExeption;

import java.time.LocalDate;

import javax.persistence.EntityManager;

/**
 * Класс для валидации нарушения.
 * Содержит методы для проверки корректности данных нарушения, таких как дата нарушения, разрешение, автомобиль, тип нарушения и статья.
 */
public class ViolationValidator {

    /**
     * Валидирует данные нарушения.
     * Проверяет дату нарушения, разрешение, автомобиль, тип нарушения и статью нарушения.
     *
     * @param violation объект {@link Violation}, содержащий данные нарушения.
     * @param em        объект {@link EntityManager} для проверки уникальности разрешения в базе данных.
     * @param oldResolution предыдущее значение разрешения, чтобы избежать проверки уникальности, если оно не изменилось.
     * @throws Exception если данные нарушения содержат ошибки.
     */
    public static void validateViolation(Violation violation, EntityManager em, String oldResolution) throws Exception {
        StringBuilder errors = new StringBuilder();

        // Проверка даты нарушения
        if (violation.getViolationDate() == null) {
            errors.append("Дата нарушения должна быть в формате ГГГГ-ММ-ДД.\n");
        } else if (violation.getViolationDate().isAfter(LocalDate.now())) {
            errors.append("Дата нарушения должна находиться в прошлом.\n");
        }

        // Проверка номера постановления
        if (!violation.getViolationResolution().equals(oldResolution)) {
            try {
                // Валидация разрешения и его уникальности
                ResolutionValidator.validateResolution(violation.getViolationResolution());
                ResolutionValidator.validateResolutionUniqueness(violation.getViolationResolution(), em);
            } catch (InvalidResolutionException | ResolutionAlreadyExistsExeption ex) {
                // Добавление ошибок, если они есть
                errors.append(ex.getMessage()).append("\n");
            }
        }

        // Проверка автомобиля
        if (violation.getCar() == null) {
            errors.append("Выберите автомобиль.\n");
        }

        // Проверка типа нарушения
        if (violation.getViolationType() == null) {
            errors.append("Выберите тип нарушения.\n");
        }

        // Проверка статьи нарушения
        if (violation.getViolationArticle() == null) {
            errors.append("Выберите статью нарушения.\n");
        }

        // Если есть ошибки, выбрасываем исключение с описанием ошибок
        if (errors.length() > 0) {
            throw new Exception(errors.toString());
        }
    }
}
