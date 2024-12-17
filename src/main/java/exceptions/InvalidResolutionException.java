package exceptions;

/**
 * Исключение, которое указывает на недопустимое постановление о нарушении.
 * Это исключение используется для сигнализации об ошибках, связанных с некорректным
 * форматом или содержанием номера постановления о нарушении.
 */
public class InvalidResolutionException extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, описывающее причину исключения.
     */
    public InvalidResolutionException(String message) {
        super(message); // Передаем сообщение в базовый класс Exception
    }
}
