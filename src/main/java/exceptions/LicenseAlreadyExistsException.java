package exceptions;

/**
 * Исключение, которое указывает на то, что водитель с таким номером лицензии уже существует.
 * Используется для обработки ситуации, когда в базе данных уже имеется запись с таким номером лицензии.
 */
public class LicenseAlreadyExistsException extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, описывающее причину исключения.
     */
    public LicenseAlreadyExistsException(String message) {
        super(message); // Передача сообщения в базовый класс Exception
    }
}
