package exceptions;

/**
 * Исключение, которое возникает, когда код статьи нарушения уже существует в системе.
 * Это исключение используется для обработки ситуации, когда в базе данных уже имеется запись с таким же кодом статьи нарушения.
 */
public class ViolationArticleCodeAlreadyExistsExeption extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, которое описывает причину возникновения исключения.
     */
    public ViolationArticleCodeAlreadyExistsExeption(String message) {
        super(message); // Передача сообщения в конструктор базового класса Exception
    }
}

