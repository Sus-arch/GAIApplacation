package exceptions;

/**
 * Исключение, которое указывает на недопустимый VIN-номер транспортного средства.
 * Используется для обработки ошибок, связанных с некорректным форматом или содержанием VIN-номера.
 */
public class InvalidVinNumberException extends Exception {

    /**
     * Конструктор с параметром сообщения.
     *
     * @param message сообщение, описывающее причину исключения.
     */
    public InvalidVinNumberException(String message) {
        super(message); // Передача сообщения в базовый класс Exception
    }
}
