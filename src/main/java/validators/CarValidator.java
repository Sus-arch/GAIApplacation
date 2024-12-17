package validators;

import entities.Car;
import exceptions.InvalidVinNumberException;
import exceptions.InvalidLicensePlateException;
import exceptions.VinNumberAlreadyExistsExeption;
import exceptions.LicensePlateAlreadyExistsExeption;

import javax.persistence.EntityManager;
import java.time.LocalDate;

/**
 * Класс для валидации данных автомобиля.
 * Содержит метод для проверки корректности данных, таких как бренд, модель, VIN-номер, государственный номер,
 * владелец и дата последнего техосмотра.
 */
public class CarValidator {

    /**
     * Выполняет валидацию данных автомобиля.
     * Проверяет корректность бренда, модели, VIN-номера, государственного номера, владельца и даты последнего ТО.
     * Если обнаруживаются ошибки, выбрасывается исключение с подробным описанием.
     *
     * @param car              объект {@link Car}, содержащий данные автомобиля.
     * @param em               объект {@link EntityManager} для выполнения проверок уникальности VIN-номера и номера.
     * @param oldLicensePlate  предыдущий номер автомобиля для исключения проверки уникальности, если номер не изменился.
     * @param oldVin           предыдущий VIN автомобиля для исключения проверки уникальности, если VIN не изменился.
     * @throws Exception если в данных автомобиля обнаружены ошибки.
     */
    public static void validateCar(Car car, EntityManager em, String oldLicensePlate, String oldVin) throws Exception {
        StringBuilder errors = new StringBuilder();

        // Проверка бренда автомобиля
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            errors.append("Бренд не может быть пустым.\n");
        } else if (!car.getBrand().matches("[а-яА-Яa-zA-Z]+")) {
            errors.append("Название бренда может содержать только буквы.\n");
        } else if (!car.getBrand().matches("[а-яА-Я]+") && !car.getBrand().matches("[a-zA-Z]+")) {
            errors.append("Название бренда может содержать только русские или только английские буквы.\n");
        }

        // Проверка модели автомобиля
        if (car.getModel().isEmpty() || car.getModel().trim().isEmpty()) {
            errors.append("Модель не может быть пустой.\n");
        } else if (!car.getModel().matches("[а-яА-Я\\p{Punct}0-9\\s]+") && !car.getModel().matches("[a-zA-Z\\p{Punct}0-9\\s]+")) {
            errors.append("Название модели не может содержать одновременно русские и английские буквы.\n");
        }

        // Проверка VIN-номера, если он изменился
        if (!car.getVinNumber().equals(oldVin)) {
            try {
                VinNumberValidator.validateVin(car.getVinNumber());
                VinNumberValidator.validateVinNumberUniqueness(car.getVinNumber(), em);
            } catch (InvalidVinNumberException | VinNumberAlreadyExistsExeption ex) {
                errors.append(ex.getMessage()).append("\n");
            }
        }

        // Проверка государственного номера, если он изменился
        if (!car.getLicensePlate().equals(oldLicensePlate)) {
            try {
                LicensePlateValidator.validateLicensePlate(car.getLicensePlate());
                LicensePlateValidator.validateLicensePlateUniqueness(car.getLicensePlate(), em);
            } catch (InvalidLicensePlateException | LicensePlateAlreadyExistsExeption ex) {
                errors.append(ex.getMessage()).append("\n");
            }
        }

        // Проверка наличия владельца
        if (car.getOwner() == null) {
            errors.append("Владелец не может быть пустым.\n");
        }

        // Проверка даты последнего техосмотра
        if (car.getLastVehicleInspection() == null) {
            errors.append("Дата последнего ТО должна быть в формате ГГГГ-ММ-ДД.\n");
        } else if (car.getLastVehicleInspection().isAfter(LocalDate.now())) {
            errors.append("Дата последнего ТО должна находиться в прошлом.\n");
        }

        // Если обнаружены ошибки, выбрасываем исключение с их описанием
        if (errors.length() > 0) {
            throw new Exception(errors.toString());
        }
    }
}
