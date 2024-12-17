package controllers;

import ui.CarsPanel;
import validators.CarValidator;

import services.CarService;
import entities.Car;
import entities.Driver;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import application.Application;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для работы с автомобилями.
 * Обрабатывает все действия, связанные с добавлением, редактированием, удалением и поиском автомобилей.
 */
public class CarController {
    private Application application;
    private CarsPanel carsPanel;
    private CarService carService;
    private JFrame parentWindow;
    private DefaultTableModel carsTableModel;
    private DriverController driverController;

    /**
     * Конструктор контроллера.
     * @param application экземпляр приложения
     * @param parentWindow родительское окно
     * @param carsPanel панель с автомобилями
     * @param carService сервис для работы с автомобилями
     * @param carTableModel модель таблицы автомобилей
     * @param driverController контроллер водителей
     */
    public CarController(Application application, JFrame parentWindow, CarsPanel carsPanel, CarService carService, DefaultTableModel carTableModel, DriverController driverController) {
        this.application = application;
        this.parentWindow = parentWindow;
        this.carsPanel = carsPanel;
        this.carService = carService;
        this.carsTableModel = carTableModel;
        this.driverController = driverController;
        
        // Инициализация обработчиков событий
        initEventHandlers();
    }
    
    /**
     * Инициализация обработчиков событий для панели автомобилей.
     * Устанавливает действия для кнопок добавления, редактирования, удаления и поиска автомобилей.
     */
    private void initEventHandlers() {
        // Обработчик для кнопки добавления автомобиля
        carsPanel.setAddCarAction(e -> openAddCarWindow());
        // Обработчик для кнопки редактирования автомобиля
        carsPanel.setEditCarAction(e -> openEditCarWindow());
        // Обработчик для кнопки удаления автомобиля
        carsPanel.setDeleteCarAction(e -> deleteCar());
        // Обработчик для кнопки поиска автомобиля
        carsPanel.setSearchCarAction(e -> openSearchCarWindow());
        // Обработчик для кнопки сброса фильтров поиска
        carsPanel.setResetFiltersAction(e -> carsPanel.updateCarData(carService.getAllCars()));
    }

    /**
     * Обновление данных автомобилей на панели.
     */
    public void updateCarData() {
        carsPanel.updateCarData(carService.getAllCars());
    }

    /**
     * Открытие окна для добавления нового автомобиля.
     */
    public void openAddCarWindow() {
        JDialog addCarDialog = new JDialog(parentWindow, "Добавление автомобиля", true);
        addCarDialog.setSize(600, 300);
        addCarDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField vinField = new JTextField();
        JTextField licensePlateField = new JTextField();
        JTextField lastVehicleInspectionField = new JTextField();

        // Поле для выбора владельца
        JTextField ownerField = new JTextField(20);
        ownerField.setEditable(false);
        JPanel ownerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectOwnerButton = new JButton("Выбрать");

        ownerPanel.add(ownerField);
        ownerPanel.add(selectOwnerButton);

        // Добавление всех полей ввода на панель
        inputPanel.add(new JLabel("Марка:"));
        inputPanel.add(brandField);
        inputPanel.add(new JLabel("Модель:"));
        inputPanel.add(modelField);
        inputPanel.add(new JLabel("VIN:"));
        inputPanel.add(vinField);
        inputPanel.add(new JLabel("Госномер:"));
        inputPanel.add(licensePlateField);
        inputPanel.add(new JLabel("Владелец:"));
        inputPanel.add(ownerPanel);
        inputPanel.add(new JLabel("Дата последнего ТО:"));
        inputPanel.add(lastVehicleInspectionField);
       
        // Панель с кнопками
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        addCarDialog.add(inputPanel, BorderLayout.CENTER);
        addCarDialog.add(buttonPanel, BorderLayout.SOUTH);

        EntityManager em = carService.getEntityManager();

        // Обработчик для кнопки выбора владельца
        selectOwnerButton.addActionListener(e -> {
            JDialog driverSelectionDialog = new JDialog(addCarDialog, "Выберите водителя", true);
            driverSelectionDialog.setSize(400, 300);
            
            List<Driver> drivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
            String[] driverNames = drivers.stream()
                .map(dr -> dr.getFullName() + " (" + dr.getLicenseNumber() + ")")
                .toArray(String[]::new);

            JList<String> driverList = new JList<>(driverNames);
            driverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            driverSelectionDialog.add(new JScrollPane(driverList), BorderLayout.CENTER);

            JButton addNewDriverButtonInDialog = new JButton("Добавить нового водителя");
            driverSelectionDialog.add(addNewDriverButtonInDialog, BorderLayout.SOUTH);

            // Обработчик для кнопки "Добавить нового водителя"
            addNewDriverButtonInDialog.addActionListener(addEvent -> {
                driverController.openAddDriverWindow();
                List<Driver> updatedDrivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
                String[] updatedDriverNames = updatedDrivers.stream()
                    .map(dr -> dr.getFullName() + " (" + dr.getLicenseNumber() + ")")
                    .toArray(String[]::new);
                driverList.setListData(updatedDriverNames);
                
                if (updatedDriverNames.length > 0) {
                    driverList.setSelectedIndex(updatedDriverNames.length - 1);
                    ownerField.setText(updatedDriverNames[updatedDriverNames.length - 1]);
                }
            });

            // Обработчик для выбора водителя
            driverList.addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    String selectedDriver = driverList.getSelectedValue();
                    if (selectedDriver != null) {
                        ownerField.setText(selectedDriver);
                        driverSelectionDialog.dispose();
                    }
                }
            });

            driverSelectionDialog.setLocationRelativeTo(addCarDialog);
            driverSelectionDialog.setVisible(true);
        });

        // Обработчик для кнопки добавления автомобиля
        addButton.addActionListener(e -> {
            try {
                // Создание нового автомобиля
                Car car = new Car();
                car.setBrand(brandField.getText());
                car.setModel(modelField.getText());
                car.setVinNumber(vinField.getText());
                car.setLicensePlate(licensePlateField.getText());
                String lastVehicleInspection = lastVehicleInspectionField.getText();
                if (lastVehicleInspection.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    car.setLastVehicleInspection(LocalDate.parse(lastVehicleInspectionField.getText()));            				
                } else {
                    car.setLastVehicleInspection(null);
                }
                // Получение владельца автомобиля
                String owner = ownerField.getText();
                if (owner.trim().isEmpty()) {
                	car.setOwner(null);
                } else {    	
                	int startIndex = owner.indexOf('(');
                	int endIndex = owner.indexOf(')');
                	
                	String licenseNumber = owner.substring(startIndex + 1, endIndex);
                	Driver driver = (Driver) em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :license").setParameter("license", licenseNumber).getSingleResult();
                	car.setOwner(driver);
                }
                
                // Валидация автомобиля
                CarValidator.validateCar(car, em, " ", " ");
                
                // Добавление автомобиля в сервис
                carService.addCar(car);
                updateCarData();
                addCarDialog.dispose();
            } catch (Exception ex) {
                // В случае ошибки показывается сообщение об ошибке
                JOptionPane.showMessageDialog(addCarDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик для кнопки отмены
        cancelButton.addActionListener(e -> addCarDialog.dispose());

        addCarDialog.setLocationRelativeTo(parentWindow);
        addCarDialog.setVisible(true);
    }
	
    /**
     * Открывает окно редактирования данных автомобиля.
     * В этом окне пользователь может изменить марку, модель, VIN, госномер, владельца и дату последнего ТО автомобиля.
     * Окно включает кнопки "Сохранить" и "Отмена". 
     * При сохранении изменений данные автомобиля обновляются в базе данных.
     */
    private void openEditCarWindow() {
        // Получаем выбранный номер автомобиля
        String selectedCarLicensePlate = carsPanel.getSelectedCarLicensePlate();

        // Если автомобиль не выбран, показываем ошибку
        if (selectedCarLicensePlate == null) {
            JOptionPane.showMessageDialog(parentWindow, "Выберите автомобиль для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ищем автомобиль по госномеру
        Car car = carService.getCarByLicensePlate(selectedCarLicensePlate);
        if (car == null) {
            JOptionPane.showMessageDialog(parentWindow, "Автомобиль с таким госномером не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создаем диалоговое окно для редактирования
        JDialog editCarDialog = new JDialog(parentWindow, "Изменение данных автомобиля", true);
        editCarDialog.setSize(600, 300);
        editCarDialog.setLayout(new BorderLayout());

        // Сохраняем старые значения для сравнения при сохранении
        String oldVin = car.getVinNumber();
        String oldLicensePlate = car.getLicensePlate();

        // Панель для ввода данных автомобиля
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JTextField brandField = new JTextField(car.getBrand());
        JTextField modelField = new JTextField(car.getModel());
        JTextField vinField = new JTextField(car.getVinNumber());
        JTextField licensePlateField = new JTextField(car.getLicensePlate());
        JTextField lastVehicleInspectionField = new JTextField(car.getLastVehicleInspection().toString());

        // Отображаем информацию о владельце, но поле недоступно для редактирования
        String curentOwner = car.getOwner().getFullName() + " (" + car.getOwner().getLicenseNumber() + ")";
        JTextField ownerField = new JTextField(curentOwner);
        ownerField.setEditable(false);
        JPanel ownerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectOwnerButton = new JButton("Выбрать");

        // Добавляем кнопку выбора владельца
        ownerPanel.add(ownerField);
        ownerPanel.add(selectOwnerButton);

        // Добавляем метки и поля ввода в панель
        inputPanel.add(new JLabel("Марка:"));
        inputPanel.add(brandField);
        inputPanel.add(new JLabel("Модель:"));
        inputPanel.add(modelField);
        inputPanel.add(new JLabel("VIN:"));
        inputPanel.add(vinField);
        inputPanel.add(new JLabel("Госномер:"));
        inputPanel.add(licensePlateField);
        inputPanel.add(new JLabel("Владелец:"));
        inputPanel.add(ownerPanel);
        inputPanel.add(new JLabel("Дата последнего ТО:"));
        inputPanel.add(lastVehicleInspectionField);

        // Панель с кнопками "Сохранить" и "Отмена"
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Добавляем панели в диалоговое окно
        editCarDialog.add(inputPanel, BorderLayout.CENTER);
        editCarDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Получаем менеджер сущностей для работы с базой данных
        EntityManager em = carService.getEntityManager();

        // Обработчик выбора владельца
        selectOwnerButton.addActionListener(e -> {
            JDialog driverSelectionDialog = new JDialog(editCarDialog, "Выберите водителя", true);
            driverSelectionDialog.setSize(400, 300);

            // Загружаем список водителей из базы данных
            List<Driver> drivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
            String[] driverNames = drivers.stream()
                .map(dr -> dr.getFullName() + " (" + dr.getLicenseNumber() + ")")
                .toArray(String[]::new);

            // Отображаем список водителей
            JList<String> driverList = new JList<>(driverNames);
            driverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            driverSelectionDialog.add(new JScrollPane(driverList), BorderLayout.CENTER);

            // Кнопка для добавления нового водителя
            JButton addNewDriverButtonInDialog = new JButton("Добавить нового водителя");
            driverSelectionDialog.add(addNewDriverButtonInDialog, BorderLayout.SOUTH);

            // Обработчик для добавления нового водителя
            addNewDriverButtonInDialog.addActionListener(addEvent -> {
                driverController.openAddDriverWindow();
                List<Driver> updatedDrivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
                String[] updatedDriverNames = updatedDrivers.stream()
                    .map(dr -> dr.getFullName() + " (" + dr.getLicenseNumber() + ")")
                    .toArray(String[]::new);
                driverList.setListData(updatedDriverNames);

                if (updatedDriverNames.length > 0) {
                    driverList.setSelectedIndex(updatedDriverNames.length - 1);
                    ownerField.setText(updatedDriverNames[updatedDriverNames.length - 1]);
                }
            });

            // Обработчик выбора водителя из списка
            driverList.addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    String selectedDriver = driverList.getSelectedValue();
                    if (selectedDriver != null) {
                        ownerField.setText(selectedDriver);
                        driverSelectionDialog.dispose();
                    }
                }
            });

            // Отображаем окно выбора водителя
            driverSelectionDialog.setLocationRelativeTo(editCarDialog);
            driverSelectionDialog.setVisible(true);
        });

        // Обработчик кнопки "Сохранить"
        saveButton.addActionListener(e -> {
            try {
                // Создаем временный объект автомобиля для сохранения изменений
                Car tempCar = new Car();
                tempCar.setBrand(brandField.getText());
                tempCar.setModel(modelField.getText());
                tempCar.setVinNumber(vinField.getText());
                tempCar.setLicensePlate(licensePlateField.getText());

                // Проверяем формат даты последнего ТО
                String lastVehicleInspection = lastVehicleInspectionField.getText();
                if (lastVehicleInspection.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    tempCar.setLastVehicleInspection(LocalDate.parse(lastVehicleInspectionField.getText()));
                } else {
                    tempCar.setLastVehicleInspection(null);
                }

                // Получаем владельца из поля
                String owner = ownerField.getText();
                int startIndex = owner.indexOf('(');
                int endIndex = owner.indexOf(')');

                // Извлекаем номер водительского удостоверения
                String licenseNumber = owner.substring(startIndex + 1, endIndex);
                Driver driver = (Driver) em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :license")
                                            .setParameter("license", licenseNumber)
                                            .getSingleResult();
                tempCar.setOwner(driver);

                // Проверка корректности данных автомобиля
                CarValidator.validateCar(tempCar, em, oldLicensePlate, oldVin);

                // Обновляем данные автомобиля в базе
                car.setBrand(brandField.getText());
                car.setModel(modelField.getText());
                car.setVinNumber(vinField.getText());
                car.setLicensePlate(licensePlateField.getText());
                car.setLastVehicleInspection(LocalDate.parse(lastVehicleInspectionField.getText()));
                car.setOwner(driver);

                // Сохраняем изменения
                carService.updateCar(car);
                application.updateAllTables();
                editCarDialog.dispose();
            } catch (Exception ex) {
                // В случае ошибки показываем сообщение
                JOptionPane.showMessageDialog(editCarDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик кнопки "Отмена"
        cancelButton.addActionListener(e -> editCarDialog.dispose());

        // Отображаем диалоговое окно редактирования
        editCarDialog.setLocationRelativeTo(parentWindow);
        editCarDialog.setVisible(true);
    }
	
    /**
     * Открывает окно для удаления выбранного автомобиля.
     * Проверяет, выбран ли автомобиль для удаления. Если нет, выводится ошибка.
     * Если выбран автомобиль, появляется окно подтверждения удаления.
     * При подтверждении удаления автомобиль удаляется из базы данных, и таблица обновляется.
     */
    private void deleteCar() {
        String selectedCarLicensePlate = carsPanel.getSelectedCarLicensePlate();

        if (selectedCarLicensePlate == null) {
            // Вывод сообщения об ошибке, если автомобиль не выбран
            JOptionPane.showMessageDialog(parentWindow, "Выберите автомобиль для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Окно подтверждения удаления
        int confirm = JOptionPane.showConfirmDialog(
                parentWindow,
                "Вы уверены, что хотите удалить выбранный автомобиль?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Удаление автомобиля и обновление таблицы
                carService.deleteCar(selectedCarLicensePlate);
                application.updateAllTables();
                JOptionPane.showMessageDialog(parentWindow, "Автомобиль успешно удален.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                // Обработка ошибки при удалении автомобиля
                JOptionPane.showMessageDialog(parentWindow, "Ошибка при удалении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Открывает окно для поиска автомобиля по различным параметрам.
     * Включает поля для ввода марки, модели, VIN, госномера, даты последнего ТО, и владельца.
     * Предоставляет возможность выбрать владельца из списка водителей или сбросить фильтры поиска.
     */
    private void openSearchCarWindow() {
        JPanel searchPanel = new JPanel(new GridLayout(8, 2));

        // Поля для ввода параметров поиска
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField vinField = new JTextField();
        JTextField licensePlateField = new JTextField();
        JTextField lastInspectionDateFromField = new JTextField();
        JTextField lastInspectionDateToField = new JTextField();
        JTextField ownerField = new JTextField(20);
        ownerField.setEditable(false);

        // Добавление компонентов в панель поиска
        searchPanel.add(new JLabel("Марка:"));
        searchPanel.add(brandField);
        searchPanel.add(new JLabel("Модель:"));
        searchPanel.add(modelField);
        searchPanel.add(new JLabel("VIN:"));
        searchPanel.add(vinField);
        searchPanel.add(new JLabel("Госномер:"));
        searchPanel.add(licensePlateField);
        searchPanel.add(new JLabel("Дата последнего ТО (с):"));
        searchPanel.add(lastInspectionDateFromField);
        searchPanel.add(new JLabel("Дата последнего ТО (до):"));
        searchPanel.add(lastInspectionDateToField);

        // Панель для выбора владельца
        JPanel ownerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectOwnerButton = new JButton("Выбрать владельца");
        ownerPanel.add(ownerField);
        ownerPanel.add(selectOwnerButton);
        searchPanel.add(new JLabel("Владелец:"));
        searchPanel.add(ownerPanel);

        // Панель для кнопок поиска и сброса фильтров
        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("Поиск");
        JButton resetButton = new JButton("Сбросить фильтры");
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);
        searchPanel.add(buttonPanel);
        
        EntityManager em = carService.getEntityManager();

        // Действие при выборе владельца
        selectOwnerButton.addActionListener(e -> {
            JDialog driverSelectionDialog = new JDialog(parentWindow, "Выберите владельца", true);
            driverSelectionDialog.setSize(400, 300);

            // Получение списка водителей из базы данных
            List<Driver> drivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
            String[] driverNames = drivers.stream()
                .map(dr -> dr.getFullName() + " (" + dr.getLicenseNumber() + ")")
                .toArray(String[]::new);

            JList<String> driverList = new JList<>(driverNames);
            driverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            driverSelectionDialog.add(new JScrollPane(driverList), BorderLayout.CENTER);

            // Действие при выборе водителя
            driverList.addListSelectionListener(event -> {
                if (!event.getValueIsAdjusting()) {
                    String selectedDriver = driverList.getSelectedValue();
                    if (selectedDriver != null) {
                        ownerField.setText(selectedDriver);
                        driverSelectionDialog.dispose();
                    }
                }
            });

            driverSelectionDialog.setLocationRelativeTo(null);
            driverSelectionDialog.setVisible(true);
        });

        // Действие при нажатии кнопки "Поиск"
        searchButton.addActionListener(e -> {
            carsTableModel.setRowCount(0);

            // Считывание значений из полей поиска
            String brand = brandField.getText();
            String model = modelField.getText();
            String vin = vinField.getText();
            String licensePlate = licensePlateField.getText();
            String lastInspectionDateFrom = lastInspectionDateFromField.getText();
            String lastInspectionDateTo = lastInspectionDateToField.getText();
            String owner = ownerField.getText();

            // Обновление данных в таблице
            carsPanel.updateCarData(carService.searchCars(brand, model, vin, licensePlate, lastInspectionDateFrom, lastInspectionDateTo, owner));
        });

        // Действие при нажатии кнопки "Сбросить фильтры"
        resetButton.addActionListener(e -> {
            brandField.setText("");
            modelField.setText("");
            vinField.setText("");
            licensePlateField.setText("");
            lastInspectionDateFromField.setText("");
            lastInspectionDateToField.setText("");
            ownerField.setText("");
            updateCarData();
        });

        // Диалоговое окно для поиска
        Object[] options = {searchButton, resetButton};
        int result = JOptionPane.showOptionDialog(null, searchPanel, "Поиск автомобилей",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (result == 0) {
            searchButton.doClick(); // Выполнение поиска
        } else if (result == 1) {
            resetButton.doClick(); // Сброс фильтров
        }
    }

}
