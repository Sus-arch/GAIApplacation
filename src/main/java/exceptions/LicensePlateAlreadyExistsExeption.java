package exceptions;

/**
 * Исключение, которое возникает, когда номер автомобиля уже существует в системе.
 * Используется для обработки ситуации, когда в базе данных уже имеется запись с таким номером автомобиля.
 */
public class LicensePlateAlreadyExistsExeption extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, описывающее причину исключения.
     */
    public LicensePlateAlreadyExistsExeption(String message) {
        super(message); // Передача сообщения в базовый класс Exception
    }
}
