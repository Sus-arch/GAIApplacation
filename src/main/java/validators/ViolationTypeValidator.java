package validators;

import entities.ViolationType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Класс для валидации типов нарушений.
 * Проверяет корректность названия типа нарушения и его уникальность.
 */
public class ViolationTypeValidator {

    /**
     * Валидирует данные типа нарушения.
     * Проверяет корректность названия типа и его уникальность в базе данных.
     *
     * @param violationType объект {@link ViolationType}, содержащий данные типа нарушения.
     * @param em            объект {@link EntityManager} для проверки уникальности названия типа в базе данных.
     * @param oldName       предыдущее название типа, чтобы избежать проверки уникальности, если название не изменилось.
     * @throws Exception если данные типа нарушения содержат ошибки.
     */
    public static void validateViolationType(ViolationType violationType, EntityManager em, String oldName) throws Exception {
        StringBuilder errors = new StringBuilder();

        // Проверка названия типа
        if (violationType.getViolationTypeName() == null || violationType.getViolationTypeName().trim().isEmpty()) {
            errors.append("Название типа не может быть пустым.\n");
        } else if (!violationType.getViolationTypeName().matches("[а-яА-Я\\s\\p{P}]+")) {
            errors.append("Название типа может содержать только русские буквы и знаки препинания.\n");
        }

        // Проверка уникальности названия типа
        if (!violationType.getViolationTypeName().equals(oldName)) {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(vt) FROM ViolationType vt WHERE vt.violationTypeName = :violationTypeName", Long.class
            );
            query.setParameter("violationTypeName", violationType.getViolationTypeName());
            Long count = query.getSingleResult();

            if (count > 0) {
                errors.append("Тип нарушения с таким названием уже существует.\n");
            }
        }

        // Если есть ошибки, выбрасываем исключение
        if (errors.length() > 0) {
            throw new Exception(errors.toString());
        }
    }
}
