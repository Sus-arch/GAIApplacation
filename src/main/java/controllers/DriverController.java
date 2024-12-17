package controllers;

import ui.DriversPanel;
import validators.DriverValidator;
import services.DriverService;
import entities.Driver;
import application.Application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

/**
 * Класс-контроллер для управления водителями.
 * Обрабатывает действия, такие как добавление, редактирование, удаление и поиск водителей.
 */
public class DriverController {
    private Application application;
    private DriversPanel driversPanel;
    private DriverService driverService;
    private JFrame parentWindow;
    private DefaultTableModel driversTableModel;

    /**
     * Конструктор для инициализации контроллера.
     * 
     * @param application приложение, в котором используется этот контроллер
     * @param parentWindow родительское окно, в котором отображаются диалоговые окна
     * @param driversPanel панель с информацией о водителях
     * @param driverService сервис для работы с данными водителей
     * @param driversTableModel модель таблицы водителей
     */
    public DriverController(Application application, JFrame parentWindow, DriversPanel driversPanel, DriverService driverService, DefaultTableModel driversTableModel) {
        this.application = application;
        this.parentWindow = parentWindow;
        this.driversPanel = driversPanel;
        this.driverService = driverService;
        this.driversTableModel = driversTableModel;

        initEventHandlers();
    }

    /**
     * Инициализирует обработчики событий для действий с водителями.
     */
    private void initEventHandlers() {
        // Устанавливаем обработчики действий для панели водителей
        driversPanel.setAddDriverAction(e -> openAddDriverWindow());
        driversPanel.setEditDriverAction(e -> openEditDriverWindow());
        driversPanel.setDeleteDriverAction(e -> deleteDriver());
        driversPanel.setSearchDriverAction(e -> openSearchDriverWindow());
        driversPanel.setResetFiltersAction(e -> driversPanel.updateDriverData(driverService.getAllDrivers()));
    }

    /**
     * Обновляет данные о водителях на панели.
     */
    public void updateCarData() {
        driversPanel.updateDriverData(driverService.getAllDrivers());
    }

    /**
     * Открывает окно для добавления нового водителя.
     */
    public void openAddDriverWindow() {
        // Создаем диалоговое окно для добавления водителя
        JDialog addDriverDialog = new JDialog(parentWindow, "Добавление водителя", true);
        addDriverDialog.setSize(400, 300);
        addDriverDialog.setLayout(new BorderLayout());

        // Панель ввода данных водителя
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField middleNameField = new JTextField();
        JTextField licenseNumberField = new JTextField();
        JTextField birthdayField = new JTextField();
        JTextField cityField = new JTextField();

        // Добавляем поля для ввода
        inputPanel.add(new JLabel("Имя:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Фамилия:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Отчество:"));
        inputPanel.add(middleNameField);
        inputPanel.add(new JLabel("Номер ВУ:"));
        inputPanel.add(licenseNumberField);
        inputPanel.add(new JLabel("Дата рождения (ГГГГ-ММ-ДД):"));
        inputPanel.add(birthdayField);
        inputPanel.add(new JLabel("Город проживания:"));
        inputPanel.add(cityField);

        // Панель для кнопок добавления и отмены
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Добавляем элементы на диалоговое окно
        addDriverDialog.add(inputPanel, BorderLayout.CENTER);
        addDriverDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик кнопки добавления
        addButton.addActionListener(e -> {
            try {
                // Создаем объект водителя и заполняем его данными из полей ввода
                Driver driver = new Driver();
                driver.setFirstName(firstNameField.getText());
                driver.setLastName(lastNameField.getText());
                driver.setMiddleName(middleNameField.getText());
                driver.setLicenseNumber(licenseNumberField.getText());
                String birthday = birthdayField.getText();
                if (birthday.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    driver.setBirthday(LocalDate.parse(birthdayField.getText()));
                } else {
                    driver.setBirthday(null);
                }
                driver.setCity(cityField.getText());

                // Валидация данных водителя
                DriverValidator.validateDriver(driver, driverService.getEntityManager(), " ");

                // Добавляем водителя в базу данных
                driverService.addDriver(driver);
                updateCarData();
                addDriverDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addDriverDialog, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик кнопки отмены
        cancelButton.addActionListener(e -> addDriverDialog.dispose());

        addDriverDialog.setLocationRelativeTo(parentWindow);
        addDriverDialog.setVisible(true);
    }

    /**
     * Открывает окно для редактирования данных водителя.
     */
    private void openEditDriverWindow() {
        // Получаем выбранный номер водительского удостоверения
        String selectedDriverLicense = driversPanel.getSelectedDriverLicense();

        // Проверка на наличие выбранного водителя
        if (selectedDriverLicense == null) {
            JOptionPane.showMessageDialog(parentWindow, "Выберите водителя для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Driver driver = driverService.getDriverByLicense(selectedDriverLicense);
        if (driver == null) {
            JOptionPane.showMessageDialog(parentWindow, "Водитель с таким номером ВУ не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создаем диалоговое окно для редактирования водителя
        JDialog editDriverDialog = new JDialog(parentWindow, "Редактирование водителя", true);
        editDriverDialog.setSize(400, 300);
        editDriverDialog.setLayout(new BorderLayout());

        // Панель ввода данных
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JTextField firstNameField = new JTextField(driver.getFirstName());
        JTextField lastNameField = new JTextField(driver.getLastName());
        JTextField middleNameField = new JTextField(driver.getMiddleName());
        JTextField licenseNumberField = new JTextField(driver.getLicenseNumber());
        String oldLicenseNumber = driver.getLicenseNumber();
        JTextField birthdayField = new JTextField(driver.getBirthday().toString());
        JTextField cityField = new JTextField(driver.getCity());

        // Добавляем поля для редактирования
        inputPanel.add(new JLabel("Имя:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Фамилия:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Отчество:"));
        inputPanel.add(middleNameField);
        inputPanel.add(new JLabel("Номер ВУ:"));
        inputPanel.add(licenseNumberField);
        inputPanel.add(new JLabel("Дата рождения (ГГГГ-ММ-ДД):"));
        inputPanel.add(birthdayField);
        inputPanel.add(new JLabel("Город проживания:"));
        inputPanel.add(cityField);

        // Панель для кнопок сохранения и отмены
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Добавляем элементы на диалоговое окно
        editDriverDialog.add(inputPanel, BorderLayout.CENTER);
        editDriverDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик кнопки сохранения
        saveButton.addActionListener(e -> {
            try {
                // Создаем временный объект водителя для обновления данных
                Driver tempDriver = new Driver();
                tempDriver.setFirstName(firstNameField.getText());
                tempDriver.setLastName(lastNameField.getText());
                tempDriver.setMiddleName(middleNameField.getText());
                tempDriver.setLicenseNumber(licenseNumberField.getText());
                String birthday = birthdayField.getText();
                if (birthday.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    tempDriver.setBirthday(LocalDate.parse(birthdayField.getText()));
                } else {
                    tempDriver.setBirthday(null);
                }
                tempDriver.setCity(cityField.getText());

                // Валидация данных через DriverValidator
                DriverValidator.validateDriver(tempDriver, driverService.getEntityManager(), oldLicenseNumber);

                // Обновляем данные водителя
                driver.setFirstName(tempDriver.getFirstName());
                driver.setLastName(tempDriver.getLastName());
                driver.setMiddleName(tempDriver.getMiddleName());
                driver.setLicenseNumber(tempDriver.getLicenseNumber());
                driver.setBirthday(tempDriver.getBirthday());
                driver.setCity(tempDriver.getCity());

                // Сохраняем обновленного водителя в базу данных
                driverService.updateDriver(driver);
                application.updateAllTables();
                editDriverDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDriverDialog, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик кнопки отмены
        cancelButton.addActionListener(e -> editDriverDialog.dispose());

        editDriverDialog.setLocationRelativeTo(parentWindow);
        editDriverDialog.setVisible(true);
    }
    
    /**
     * Метод для удаления выбранного водителя.
     * В случае, если водитель не выбран, отображается сообщение об ошибке.
     * После подтверждения удаления, водителя удаляет из базы данных и обновляет таблицу.
     * В случае ошибки при удалении выводится сообщение об ошибке.
     */
    private void deleteDriver() {
        // Получаем номер ВУ выбранного водителя
        String selectedDriverLicense = driversPanel.getSelectedDriverLicense();

        // Проверяем, был ли выбран водитель
        if (selectedDriverLicense == null) {
            JOptionPane.showMessageDialog(parentWindow, "Выберите водителя для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Подтверждение удаления водителя
        int confirm = JOptionPane.showConfirmDialog(
                parentWindow,
                "Вы уверены, что хотите удалить выбранного водителя?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );

        // Если пользователь подтвердил удаление
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Удаление водителя из базы данных
                driverService.deleteDriver(selectedDriverLicense);
                // Обновляем все таблицы
                application.updateAllTables();
                JOptionPane.showMessageDialog(parentWindow, "Водитель успешно удалён.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                // В случае ошибки при удалении показываем сообщение об ошибке
                JOptionPane.showMessageDialog(parentWindow, "Ошибка при удалении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Открывает окно для поиска водителей по заданным критериям.
     * Включает поля для ввода имени, фамилии, отчества, номера ВУ, дат рождения и города.
     * Также предоставляет кнопки для запуска поиска и сброса фильтров.
     * При нажатии на кнопку поиска происходит фильтрация данных, результаты отображаются в таблице.
     * При нажатии на кнопку сброса фильтры очищаются, и данные возвращаются к исходному состоянию.
     */
    private void openSearchDriverWindow() {
        JPanel searchPanel = new JPanel(new GridLayout(8, 2));

        // Создание полей для ввода
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField middleNameField = new JTextField();
        JTextField licenseField = new JTextField();
        JTextField fromDateField = new JTextField();
        JTextField toDateField = new JTextField();
        JTextField cityField = new JTextField();

        // Добавление меток и полей в панель
        searchPanel.add(new JLabel("Имя:"));
        searchPanel.add(firstNameField);
        searchPanel.add(new JLabel("Фамилия:"));
        searchPanel.add(lastNameField);
        searchPanel.add(new JLabel("Отчество:"));
        searchPanel.add(middleNameField);
        searchPanel.add(new JLabel("Номер ВУ:"));
        searchPanel.add(licenseField);
        searchPanel.add(new JLabel("Дата рождения (с):"));
        searchPanel.add(fromDateField);
        searchPanel.add(new JLabel("Дата рождения (по):"));
        searchPanel.add(toDateField);
        searchPanel.add(new JLabel("Город:"));
        searchPanel.add(cityField);

        // Панель с кнопками поиска и сброса
        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("Поиск");
        JButton resetButton = new JButton("Сбросить фильтры");
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);

        searchPanel.add(buttonPanel);

        // Поиск водителей по фильтрам
        searchButton.addActionListener(e -> {
            // Очистка таблицы перед выводом новых данных
            driversTableModel.setRowCount(0);

            // Считывание введенных данных
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String middleName = middleNameField.getText();
            String license = licenseField.getText();
            String city = cityField.getText();
            String fromDate = fromDateField.getText();
            String toDate = toDateField.getText();

            // Обновляем таблицу с результатами поиска
            driversPanel.updateDriverData(driverService.searchDrivers(firstName, lastName, middleName, license, city, fromDate, toDate));
        });

        // Сброс фильтров
        resetButton.addActionListener(e -> {
            // Очищаем все поля
            firstNameField.setText("");
            lastNameField.setText("");
            middleNameField.setText("");
            licenseField.setText("");
            cityField.setText("");
            fromDateField.setText("");
            toDateField.setText("");
            updateCarData();  // Возвращаем данные в исходное состояние
        });

        // Диалоговое окно с кнопками поиска и сброса
        Object[] options = {searchButton, resetButton};
        int result = JOptionPane.showOptionDialog(null, searchPanel, "Поиск водителей",
            JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // Если пользователь нажал кнопку поиска
        if (result == 0) {
            searchButton.doClick();
        } else if (result == 1) {  // Если кнопка сброса
            resetButton.doClick(); 
        }
    }

}
	            
