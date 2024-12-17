package validators;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import exceptions.InvalidViolationArticleCodeException;
import exceptions.ViolationArticleCodeAlreadyExistsExeption;

/**
 * Класс для валидации кодов статей нарушений.
 * Содержит методы для проверки формата кодов статей, логических диапазонов и их уникальности в базе данных.
 */
public class ViolationArticleCodeValidator {

    /**
     * Регулярное выражение для проверки формата статьи нарушения.
     * Пример правильного формата: "КоАП РФ 12.9 п.2".
     */
    private static final String ARTICLE_REGEX = "КоАП РФ \\d+\\.\\d+( п\\.\\d+)?";

    /**
     * Проверяет корректность формата и логических диапазонов кода статьи нарушения.
     *
     * @param article код статьи нарушения, который необходимо проверить.
     * @throws InvalidViolationArticleCodeException если код статьи нарушения имеет неверный формат или выходит за логические границы.
     */
    public static void validateArticleCode(String article) throws InvalidViolationArticleCodeException {
        // Проверка на null или пустое значение
        if (article == null || article.trim().isEmpty()) {
            throw new InvalidViolationArticleCodeException("Код статьи нарушения не может быть пустым.");
        }

        // Проверка формата с использованием регулярного выражения
        if (!article.matches(ARTICLE_REGEX)) {
            throw new InvalidViolationArticleCodeException("Код статьи нарушения имеет неверный формат. Пример: 'КоАП РФ 12.9 п.2'.");
        }

        // Проверка логических диапазонов номера главы, параграфа и пункта
        validateLogicalRanges(article);
    }

    /**
     * Проверяет логические диапазоны главы, параграфа и пункта статьи.
     * Например, глава должна быть от 1 до 20, параграф от 1 до 50, а пункт от 1 до 10.
     *
     * @param article код статьи нарушения, который необходимо проверить.
     * @throws InvalidViolationArticleCodeException если значения главы, параграфа или пункта выходят за допустимые диапазоны.
     */
    private static void validateLogicalRanges(String article) throws InvalidViolationArticleCodeException {
        // Удаляем префикс "КоАП РФ" и разбиваем строку на части
        String[] parts = article.replace("КоАП РФ ", "").split("[\\. ]");

        // Проверка главы статьи
        int chapter = Integer.parseInt(parts[0]);
        if (chapter < 1 || chapter > 20) {
            throw new InvalidViolationArticleCodeException("Номер главы статьи должен быть от 1 до 20.");
        }

        // Проверка параграфа статьи
        int paragraph = Integer.parseInt(parts[1]);
        if (paragraph < 1 || paragraph > 50) {
            throw new InvalidViolationArticleCodeException("Номер параграфа статьи должен быть от 1 до 50.");
        }

        // Проверка пункта статьи, если он указан
        if (parts.length == 3 && parts[2].startsWith("п")) {
            int point = Integer.parseInt(parts[2].substring(2));
            if (point < 1 || point > 10) {
                throw new InvalidViolationArticleCodeException("Номер пункта статьи должен быть от 1 до 10.");
            }
        }
    }

    /**
     * Проверяет уникальность кода статьи нарушения в базе данных.
     * Использует {@link EntityManager} для выполнения запроса к базе данных.
     *
     * @param code код статьи нарушения, который необходимо проверить на уникальность.
     * @param em   объект {@link EntityManager} для взаимодействия с базой данных.
     * @throws ViolationArticleCodeAlreadyExistsExeption если статья с таким кодом уже существует.
     */
    public static void validateCodeUniqueness(String code, EntityManager em) throws ViolationArticleCodeAlreadyExistsExeption {
        // Запрос для проверки количества записей с данным кодом статьи
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(va) FROM ViolationArticle va WHERE va.violationArticleCode = :violationArticleCode", Long.class
        );
        query.setParameter("violationArticleCode", code);
        Long count = query.getSingleResult();

        // Если статья с таким кодом уже существует, выбрасываем исключение
        if (count > 0) {
            throw new ViolationArticleCodeAlreadyExistsExeption("Статья с таким кодом уже существует.");
        }
    }
}
