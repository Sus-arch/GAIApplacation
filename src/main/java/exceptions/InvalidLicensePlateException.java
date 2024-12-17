package exceptions;

/**
 * Исключение, которое указывает на недопустимый номерной знак автомобиля.
 * Это исключение используется для сигнализации об ошибках, связанных с некорректным
 * форматом или содержанием государственного регистрационного номера транспортного средства.
 */
public class InvalidLicensePlateException extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, описывающее причину исключения.
     */
    public InvalidLicensePlateException(String message) {
        super(message); // Передаем сообщение в базовый класс Exception
    }
}
