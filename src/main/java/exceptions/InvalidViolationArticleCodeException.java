package exceptions;

/**
 * Исключение, которое указывает на недопустимый код статьи нарушения.
 * Используется для обработки ошибок, связанных с некорректным форматом или содержанием кода статьи.
 */
public class InvalidViolationArticleCodeException extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, описывающее причину исключения.
     */
    public InvalidViolationArticleCodeException(String message) {
        super(message); // Передача сообщения в базовый класс Exception
    }
}
