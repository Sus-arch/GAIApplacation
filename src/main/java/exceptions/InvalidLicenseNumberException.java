package exceptions;

/**
 * Исключение, которое указывает на недопустимый номер водительского удостоверения.
 * Это исключение используется для сигнализации об ошибках, связанных с некорректным
 * форматом или содержанием номера водительского удостоверения.
 */
public class InvalidLicenseNumberException extends Exception {

    /**
     * Конструктор с параметром сообщения.
     * 
     * @param message сообщение, описывающее причину исключения.
     */
    public InvalidLicenseNumberException(String message) {
        super(message); // Передаем сообщение в базовый класс Exception
    }
}
