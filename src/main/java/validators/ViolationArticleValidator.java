package validators;

import entities.ViolationArticle;
import exceptions.InvalidViolationArticleCodeException;
import exceptions.ViolationArticleCodeAlreadyExistsExeption;

import javax.persistence.EntityManager;

/**
 * Класс для валидации статей нарушений.
 * Содержит метод для проверки корректности кода статьи, описания и штрафа.
 */
public class ViolationArticleValidator {

    /**
     * Валидирует данные статьи нарушения.
     * Проверяет корректность кода статьи, уникальность кода, описание и штраф.
     *
     * @param violationArticle объект {@link ViolationArticle}, содержащий данные статьи нарушения.
     * @param em               объект {@link EntityManager} для проверки уникальности кода статьи в базе данных.
     * @param oldCode          предыдущий код статьи, чтобы избежать проверки уникальности, если код не изменился.
     * @throws Exception если данные статьи содержат ошибки.
     */
    public static void validateViolationArticle(ViolationArticle violationArticle, EntityManager em, String oldCode) throws Exception {
        StringBuilder errors = new StringBuilder();

        // Проверка кода статьи
        if (!violationArticle.getViolationArticleCode().equals(oldCode)) {
            try {
                // Проверка формата кода статьи
                ViolationArticleCodeValidator.validateArticleCode(violationArticle.getViolationArticleCode());

                // Проверка уникальности кода статьи
                ViolationArticleCodeValidator.validateCodeUniqueness(violationArticle.getViolationArticleCode(), em);
            } catch (InvalidViolationArticleCodeException | ViolationArticleCodeAlreadyExistsExeption ex) {
                // Добавляем ошибки к списку
                errors.append(ex.getMessage()).append("\n");
            }
        }

        // Проверка описания статьи
        if (violationArticle.getViolationArticleDescription() == null || violationArticle.getViolationArticleDescription().trim().isEmpty()) {
            errors.append("Описание не может быть пустым.\n");
        } else if (!violationArticle.getViolationArticleDescription().matches("[а-яА-Я\\p{Punct}0-9\\s]+")) {
            errors.append("Описание может содержать только русские буквы, знаки препинания и цифры.\n");
        }

        // Проверка штрафа
        if (violationArticle.getViolationArticleFine() == null || violationArticle.getViolationArticleFine() <= 0) {
            errors.append("Штраф должен быть положительным числом.\n");
        }

        // Если есть ошибки, выбрасываем исключение
        if (errors.length() > 0) {
            throw new Exception(errors.toString());
        }
    }
}
