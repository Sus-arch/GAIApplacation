package exceptions;

/**
 * Исключение, которое возникает, когда VIN-номер уже существует в системе.
 * Это исключение используется для обработки ситуации, когда в базе данных уже имеется запись с таким же VIN-номером.
 */
public class VinNumberAlreadyExistsExeption extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, которое описывает причину возникновения исключения.
     */
    public VinNumberAlreadyExistsExeption(String message) {
        super(message); // Передача сообщения в конструктор базового класса Exception
    }
}
