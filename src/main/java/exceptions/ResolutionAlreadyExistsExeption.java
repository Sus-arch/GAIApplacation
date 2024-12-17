package exceptions;

/**
 * Исключение, которое возникает, когда постановление уже существует в системе.
 * Это исключение используется для обработки ситуации, когда в базе данных уже имеется запись с таким же номером постановления.
 */
public class ResolutionAlreadyExistsExeption extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, которое описывает причину возникновения исключения.
     */
    public ResolutionAlreadyExistsExeption(String message) {
        super(message); // Передача сообщения в конструктор базового класса Exception
    }
}

