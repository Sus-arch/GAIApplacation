package controllers;

import ui.ViolationsPanel;
import validators.ViolationValidator;

import services.ViolationService;
import entities.Violation;
import entities.Car;
import entities.ViolationArticle;
import entities.ViolationType;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для работы с нарушениями.
 * Обрабатывает действия, связанные с добавлением, редактированием, удалением и поиском нарушений.
 */
public class ViolationController {
	// Панель с интерфейсом для работы с нарушениями
	private ViolationsPanel violationsPanel;
	
	// Сервис для работы с нарушениями
	private ViolationService violationService;
	
	// Окно родительского компонента
	private JFrame parentWindow;
	
	// Модель таблицы для отображения нарушений
	private DefaultTableModel violationsTableModel;
	
	// Контроллеры для работы с автомобилями, статьями нарушений и типами нарушений
	private CarController carController;
	private ViolationArticleController violationArticleController;
	private ViolationTypeController violationTypeController;
	
	/**
	 * Конструктор для инициализации контроллера нарушений.
	 * 
	 * @param parentWindow родительское окно
	 * @param violationsPanel панель с интерфейсом для работы с нарушениями
	 * @param violationService сервис для работы с нарушениями
	 * @param violationsTableModel модель таблицы для нарушений
	 * @param carController контроллер для работы с автомобилями
	 * @param violationArticleController контроллер для работы со статьями нарушений
	 * @param violationTypeController контроллер для работы с типами нарушений
	 */
	public ViolationController(JFrame parentWindow, ViolationsPanel violationsPanel, ViolationService violationService, DefaultTableModel violationsTableModel, CarController carController, ViolationArticleController violationArticleController, ViolationTypeController violationTypeController) {
		this.parentWindow = parentWindow;
		this.violationsPanel = violationsPanel;
		this.violationService = violationService;
		this.violationsTableModel = violationsTableModel;
		this.carController = carController;
		this.violationArticleController = violationArticleController;
		this.violationTypeController = violationTypeController;
		
        initEventHandlers(); // Инициализация обработчиков событий
	}
	
	/**
	 * Инициализирует обработчики событий для панели нарушений.
	 * Устанавливает действия для добавления, редактирования, удаления, поиска нарушений
	 * и сброса фильтров.
	 */
	private void initEventHandlers() {
		// Обработчик для добавления нарушения
		violationsPanel.setAddViolationAction(e -> openAddViolationWindow());
		// Обработчик для редактирования нарушения
		violationsPanel.setEditViolationAction(e -> openEditViolationWindow());
		// Обработчик для удаления нарушения
		violationsPanel.setDeleteViolationAction(e -> deleteViolation());
		// Обработчик для поиска нарушения
		violationsPanel.setSearchViolationAction(e -> openSearchViolationWindow());
		// Обработчик для сброса фильтров
		violationsPanel.setResetFiltersAction(e -> violationsPanel.updateViolationData(violationService.getAllViolations()));
	}
	
	/**
	 * Обновляет данные о нарушениях в панели.
	 * Загружает все нарушения из сервиса и отображает их на панели.
	 */
	public void updateViolationData() {
	    violationsPanel.updateViolationData(violationService.getAllViolations());
	}
	
	/**
	 * Открывает окно для добавления нового нарушения.
	 * В этом окне пользователь может ввести данные о нарушении, выбрать автомобиль, тип нарушения и статью.
	 * Также предоставляется возможность добавить новый автомобиль, тип нарушения или статью прямо из окна.
	 */
	private void openAddViolationWindow() {
	    // Создание модального окна для добавления нарушения
	    JDialog addViolationDialog = new JDialog(parentWindow, "Добавление нарушения", true);
	    addViolationDialog.setSize(580, 300);
	    addViolationDialog.setLayout(new BorderLayout());

	    // Панель для ввода данных о нарушении
	    JPanel inputPanel = new JPanel(new GridLayout(7, 2));
	    JTextField dateField = new JTextField();
	    JTextField resolutionField = new JTextField();
	    JCheckBox paymentStatusCheckbox = new JCheckBox("Оплачено");

	    JTextField carField = new JTextField(20);
	    carField.setEditable(false);  // Поле для отображения выбранного автомобиля
	    JPanel carPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectCarButton = new JButton("Выбрать");

	    carPanel.add(carField);
	    carPanel.add(selectCarButton);

	    JTextField violationTypeField = new JTextField(20);
	    violationTypeField.setEditable(false);  // Поле для отображения выбранного типа нарушения
	    JPanel violationTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectViolationTypeButton = new JButton("Выбрать");

	    violationTypePanel.add(violationTypeField);
	    violationTypePanel.add(selectViolationTypeButton);

	    JTextField violationArticleField = new JTextField(20);
	    violationArticleField.setEditable(false);  // Поле для отображения выбранной статьи нарушения
	    JPanel violationArticlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectViolationArticleButton = new JButton("Выбрать");

	    violationArticlePanel.add(violationArticleField);
	    violationArticlePanel.add(selectViolationArticleButton);

	    // Добавляем все компоненты на панель ввода
	    inputPanel.add(new JLabel("Дата нарушения (ГГГГ-ММ-ДД):"));
	    inputPanel.add(dateField);
	    inputPanel.add(new JLabel("Номер постановления: "));
	    inputPanel.add(resolutionField);
	    inputPanel.add(new JLabel("Автомобиль:"));
	    inputPanel.add(carPanel);
	    inputPanel.add(new JLabel("Тип нарушения:"));
	    inputPanel.add(violationTypePanel);
	    inputPanel.add(new JLabel("Статья нарушения:"));
	    inputPanel.add(violationArticlePanel);
	    inputPanel.add(new JLabel("Статус оплаты:"));
	    inputPanel.add(paymentStatusCheckbox);

	    // Панель с кнопками "Добавить" и "Отмена"
	    JPanel buttonPanel = new JPanel();
	    JButton addButton = new JButton("Добавить");
	    JButton cancelButton = new JButton("Отмена");

	    buttonPanel.add(addButton);
	    buttonPanel.add(cancelButton);

	    addViolationDialog.add(inputPanel, BorderLayout.CENTER);
	    addViolationDialog.add(buttonPanel, BorderLayout.SOUTH);

	    // Получаем EntityManager для работы с базой данных
	    EntityManager em = violationService.getEntityManager();

	    // Обработчик выбора автомобиля
	    selectCarButton.addActionListener(e -> {
	        JDialog carSelectionDialog = new JDialog(addViolationDialog, "Выберите автомобиль", true);
	        carSelectionDialog.setSize(400, 300);

	        // Получаем список автомобилей из базы данных
	        List<Car> cars = em.createQuery("SELECT c FROM Car c", Car.class).getResultList();
	        String[] carDetails = cars.stream()
	            .map(c -> c.getBrand() + " " + c.getModel() + " (" + c.getLicensePlate() + ")")
	            .toArray(String[]::new);

	        // Создаем список с автомобилями
	        JList<String> carList = new JList<>(carDetails);
	        carList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        carSelectionDialog.add(new JScrollPane(carList), BorderLayout.CENTER);

	        // Кнопка для добавления нового автомобиля
	        JButton addNewCarButtonInDialog = new JButton("Добавить новый автомобиль");
	        carSelectionDialog.add(addNewCarButtonInDialog, BorderLayout.SOUTH);

	        // Обработчик для добавления нового автомобиля
	        addNewCarButtonInDialog.addActionListener(ev -> {
	            carController.openAddCarWindow();
	            List<Car> updatedCars = em.createQuery("SELECT c FROM Car c", Car.class).getResultList();
	            String[] updatedCarDetails = updatedCars.stream()
	                .map(c -> c.getBrand() + " " + c.getModel() + " (" + c.getLicensePlate() + ")")
	                .toArray(String[]::new);
	            carList.setListData(updatedCarDetails);

	            // Выбираем только что добавленный автомобиль
	            if (updatedCarDetails.length > 0) {
	                carList.setSelectedIndex(updatedCarDetails.length - 1);
	                carField.setText(updatedCarDetails[updatedCarDetails.length - 1]);
	            }
	        });

	        // Обработчик выбора автомобиля
	        carList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedCar = carList.getSelectedValue();
	                if (selectedCar != null) {
	                    carField.setText(selectedCar);
	                    carSelectionDialog.dispose();
	                }
	            }
	        });

	        carSelectionDialog.setLocationRelativeTo(addViolationDialog);
	        carSelectionDialog.setVisible(true);
	    });

	    // Обработчик выбора типа нарушения
	    selectViolationTypeButton.addActionListener(e -> {
	        JDialog violationTypeDialog = new JDialog(addViolationDialog, "Выберите тип нарушения", true);
	        violationTypeDialog.setSize(400, 300);

	        // Получаем список типов нарушений из базы данных
	        List<ViolationType> violationTypes = em.createQuery("SELECT vt FROM ViolationType vt ORDER BY vt.violationTypeId", ViolationType.class).getResultList();
	        String[] violationTypeNames = violationTypes.stream()
	            .map(ViolationType::getViolationTypeName)
	            .toArray(String[]::new);

	        // Создаем список с типами нарушений
	        JList<String> violationTypeList = new JList<>(violationTypeNames);
	        violationTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	        violationTypeDialog.add(new JScrollPane(violationTypeList), BorderLayout.CENTER);

	        // Кнопка для добавления нового типа нарушения
	        JButton addNewvViolationTypeButtonInDialog = new JButton("Добавить новый тип нарушения");
	        violationTypeDialog.add(addNewvViolationTypeButtonInDialog, BorderLayout.SOUTH);

	        // Обработчик для добавления нового типа нарушения
	        addNewvViolationTypeButtonInDialog.addActionListener(ev -> {
	            violationTypeController.openAddViolationTypeWindow();
	            List<ViolationType> updatedViolationTypes = em.createQuery("SELECT vt FROM ViolationType vt ORDER BY vt.violationTypeId", ViolationType.class).getResultList();
	            String[] updatedViolationTypeNames = updatedViolationTypes.stream()
	                .map(ViolationType::getViolationTypeName)
	                .toArray(String[]::new);
	            violationTypeList.setListData(updatedViolationTypeNames);

	            // Выбираем только что добавленный тип нарушения
	            if (updatedViolationTypeNames.length > 0) {
	                violationTypeList.setSelectedIndex(updatedViolationTypeNames.length - 1);
	                violationTypeField.setText(updatedViolationTypeNames[updatedViolationTypeNames.length - 1]);
	            }
	        });

	        // Обработчик выбора типа нарушения
	        violationTypeList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedViolationType = violationTypeList.getSelectedValue();
	                if (selectedViolationType != null) {
	                    violationTypeField.setText(selectedViolationType);
	                    violationTypeDialog.dispose();
	                }
	            }
	        });

	        violationTypeDialog.setLocationRelativeTo(addViolationDialog);
	        violationTypeDialog.setVisible(true);
	    });

	    // Обработчик выбора статьи нарушения
	    selectViolationArticleButton.addActionListener(e -> {
	        JDialog violationArticleDialog = new JDialog(addViolationDialog, "Выберите статью нарушения", true);
	        violationArticleDialog.setSize(400, 300);

	        // Получаем список статей нарушений из базы данных
	        List<ViolationArticle> violationArticles = em.createQuery("SELECT va FROM ViolationArticle va", ViolationArticle.class).getResultList();
	        String[] violationArticleNames = violationArticles.stream()
	            .map(ViolationArticle::getViolationArticleCode)
	            .toArray(String[]::new);

	        // Создаем список с статьями нарушений
	        JList<String> violationArticleList = new JList<>(violationArticleNames);
	        violationArticleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	        violationArticleDialog.add(new JScrollPane(violationArticleList), BorderLayout.CENTER);

	        // Кнопка для добавления новой статьи нарушения
	        JButton addNewViolationArticleButtonInDialog = new JButton("Добавить новую статью");
	        violationArticleDialog.add(addNewViolationArticleButtonInDialog, BorderLayout.SOUTH);

	        // Обработчик для добавления новой статьи нарушения
	        addNewViolationArticleButtonInDialog.addActionListener(ev -> {
	            violationArticleController.openAddViolationArticleWindow();
	            List<ViolationArticle> updatedViolationArticles = em.createQuery("SELECT va FROM ViolationArticle va", ViolationArticle.class).getResultList();
	            String[] updatedViolationArticleNames = updatedViolationArticles.stream()
	                .map(ViolationArticle::getViolationArticleCode)
	                .toArray(String[]::new);
	            violationArticleList.setListData(updatedViolationArticleNames);

	            // Выбираем только что добавленную статью нарушения
	            if (updatedViolationArticleNames.length > 0) {
	                violationArticleList.setSelectedIndex(updatedViolationArticleNames.length - 1);
	                violationArticleField.setText(updatedViolationArticleNames[updatedViolationArticleNames.length - 1]);
	            }
	        });

	        // Обработчик выбора статьи нарушения
	        violationArticleList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedArticle = violationArticleList.getSelectedValue();
	                if (selectedArticle != null) {
	                    violationArticleField.setText(selectedArticle);
	                    violationArticleDialog.dispose();
	                }
	            }
	        });

	        violationArticleDialog.setLocationRelativeTo(addViolationDialog);
	        violationArticleDialog.setVisible(true);
	    });

	    // Обработчик добавления нарушения
	    addButton.addActionListener(e -> {
	        try {
	            // Создаем объект нарушения
	            Violation violation = new Violation();
	            violation.setViolationPaid(paymentStatusCheckbox.isSelected());
	            violation.setViolationResolution(resolutionField.getText());

	            // Парсим дату нарушения
	            String date = dateField.getText();
	            if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
	                violation.setViolationDate(LocalDate.parse(date));
	            } else {
	                violation.setViolationDate(null);
	            }

	            // Получаем выбранный автомобиль
	            String car = carField.getText();
	            if (car.trim().isEmpty()) {
	            	violation.setCar(null);
	            } else {	            	
	            	int carStart = car.indexOf('(');
	            	int carEnd = car.indexOf(')');
	            	String licensePlate = car.substring(carStart + 1, carEnd);
	            	
	            	// Ищем автомобиль по номеру
	            	Car selectedCar = em.createQuery("SELECT c FROM Car c WHERE c.licensePlate = :plate", Car.class)
	            			.setParameter("plate", licensePlate)
	            			.getSingleResult();
	            	violation.setCar(selectedCar);
	            }

	            // Ищем тип нарушения и статью
	            String violationType = violationTypeField.getText();
	            if (violationType.trim().isEmpty()) {
	            	violation.setViolationType(null);
	            } else {	            	
	            	ViolationType selectedType = em.createQuery("SELECT vt FROM ViolationType vt WHERE vt.violationTypeName = :name ", ViolationType.class)
	            			.setParameter("name", violationType)
	            			.getSingleResult();
	            	violation.setViolationType(selectedType);
	            }
	            String violationArticle = violationArticleField.getText();
	            if (violationArticle.trim().isEmpty()) {
	            	violation.setViolationArticle(null);
	            } else {	            	
	            	ViolationArticle selectedArticle = em.createQuery("SELECT va FROM ViolationArticle va WHERE va.violationArticleCode = :name", ViolationArticle.class)
	            			.setParameter("name", violationArticle)
	            			.getSingleResult();
	            	violation.setViolationArticle(selectedArticle);
	            }

	            // Валидация и добавление нарушения
	            ViolationValidator.validateViolation(violation, em, " ");
	            violationService.addViolation(violation);
	            updateViolationData();
	            addViolationDialog.dispose();
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(addViolationDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    // Обработчик отмены
	    cancelButton.addActionListener(e -> addViolationDialog.dispose());

	    // Показываем окно добавления нарушения
	    addViolationDialog.setLocationRelativeTo(parentWindow);
	    addViolationDialog.setVisible(true);
	}
	
	/**
	 * Открывает окно для редактирования нарушения.
	 * В этом окне можно изменить данные нарушения, такие как дата, номер постановления, автомобиль, тип нарушения, статья нарушения и статус оплаты.
	 */
	private void openEditViolationWindow() {
	    // Получаем выбранное нарушение для редактирования
	    String selectedViolationResolution = violationsPanel.getSelectedViolationResolution();
	    
	    // Если нарушение не выбрано, выводим ошибку
	    if (selectedViolationResolution == null) {
	        JOptionPane.showMessageDialog(parentWindow, "Выберите нарушение для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    
	    // Получаем объект нарушения по номеру постановления
	    Violation violation = violationService.getViolationByResolution(selectedViolationResolution);
	    if (violation == null) {
	        // Если нарушение не найдено, выводим ошибку
	        JOptionPane.showMessageDialog(parentWindow, "Нарушение с таким номером постановления не найдено.", "Ошибка", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    
	    // Создаем диалоговое окно для редактирования нарушения
	    JDialog editViolationDialog = new JDialog(parentWindow, "Редактирование нарушения", true);
	    editViolationDialog.setSize(580, 300);
	    editViolationDialog.setLayout(new BorderLayout());
	    
	    // Получаем старое значение номера постановления
	    String oldResolution = violation.getViolationResolution();
	    
	    // Панель ввода данных
	    JPanel inputPanel = new JPanel(new GridLayout(7, 2));
	    JTextField dateField = new JTextField(violation.getViolationDate().toString());
	    JTextField resolutionField = new JTextField(violation.getViolationResolution());
	    JCheckBox paymentStatusCheckbox = new JCheckBox("Оплачено", violation.getViolationPaid());
	    
	    // Панель для отображения информации об автомобиле
	    JTextField carField = new JTextField(20);
	    carField.setEditable(false);
	    JPanel carPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectCarButton = new JButton("Выбрать");
	    carField.setText(violation.getCar().getBrand() + " " + violation.getCar().getModel() + " (" + violation.getCar().getLicensePlate() + ")");
	    
	    carPanel.add(carField);
	    carPanel.add(selectCarButton);
	    
	    // Панель для отображения информации о типе нарушения
	    JTextField violationTypeField = new JTextField(20);
	    violationTypeField.setEditable(false);
	    JPanel violationTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectViolationTypeButton = new JButton("Выбрать");
	    violationTypeField.setText(violation.getViolationType().getViolationTypeName());
	    
	    violationTypePanel.add(violationTypeField);
	    violationTypePanel.add(selectViolationTypeButton);
	    
	    // Панель для отображения информации о статье нарушения
	    JTextField violationArticleField = new JTextField(20);
	    violationArticleField.setEditable(false);
	    JPanel violationArticlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectViolationArticleButton = new JButton("Выбрать");
	    violationArticleField.setText(violation.getViolationArticle().getViolationArticleCode());
	    
	    violationArticlePanel.add(violationArticleField);
	    violationArticlePanel.add(selectViolationArticleButton);

	    // Добавляем элементы на панель ввода
	    inputPanel.add(new JLabel("Дата нарушения (ГГГГ-ММ-ДД):"));
	    inputPanel.add(dateField);
	    inputPanel.add(new JLabel("Номер постановления: "));
	    inputPanel.add(resolutionField);
	    inputPanel.add(new JLabel("Автомобиль:"));
	    inputPanel.add(carPanel);
	    inputPanel.add(new JLabel("Тип нарушения:"));
	    inputPanel.add(violationTypePanel);
	    inputPanel.add(new JLabel("Статья нарушения:"));
	    inputPanel.add(violationArticlePanel);
	    inputPanel.add(new JLabel("Статус оплаты:"));
	    inputPanel.add(paymentStatusCheckbox);

	    // Панель с кнопками сохранения и отмены
	    JPanel buttonPanel = new JPanel();
	    JButton saveButton = new JButton("Сохранить");
	    JButton cancelButton = new JButton("Отмена");

	    buttonPanel.add(saveButton);
	    buttonPanel.add(cancelButton);

	    editViolationDialog.add(inputPanel, BorderLayout.CENTER);
	    editViolationDialog.add(buttonPanel, BorderLayout.SOUTH);
	    
	    EntityManager em = violationService.getEntityManager();
	    
	    // Обработчик нажатия кнопки выбора автомобиля
	    selectCarButton.addActionListener(e -> {
	        JDialog carSelectionDialog = new JDialog(editViolationDialog, "Выберите автомобиль", true);
	        carSelectionDialog.setSize(400, 300);

	        // Получаем список автомобилей из базы данных
	        List<Car> cars = em.createQuery("SELECT c FROM Car c", Car.class).getResultList();
	        String[] carDetails = cars.stream()
	            .map(c -> c.getBrand() + " " + c.getModel() + " (" + c.getLicensePlate() + ")")
	            .toArray(String[]::new);

	        JList<String> carList = new JList<>(carDetails);
	        carList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        carSelectionDialog.add(new JScrollPane(carList), BorderLayout.CENTER);
	        
	        JButton addNewCarButtonInDialog = new JButton("Добавить новый автомобиль");
	        carSelectionDialog.add(addNewCarButtonInDialog, BorderLayout.SOUTH);
	        
	        addNewCarButtonInDialog.addActionListener(ev -> {
	            carController.openAddCarWindow();
	            // Обновляем список автомобилей после добавления нового
	            List<Car> updatedCars = em.createQuery("SELECT c FROM Car c", Car.class).getResultList();
	            String[] updatedCarDetails = updatedCars.stream()
	                .map(c -> c.getBrand() + " " + c.getModel() + " (" + c.getLicensePlate() + ")")
	                .toArray(String[]::new);
	            carList.setListData(updatedCarDetails);

	            // Если обновленные данные существуют, выбираем последний элемент
	            if (updatedCarDetails.length > 0) {
	                carList.setSelectedIndex(updatedCarDetails.length - 1);
	                carField.setText(updatedCarDetails[updatedCarDetails.length - 1]);
	            }
	        });

	        // Обработчик выбора автомобиля
	        carList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedCar = carList.getSelectedValue();
	                if (selectedCar != null) {
	                    carField.setText(selectedCar);
	                    carSelectionDialog.dispose();
	                }
	            }
	        });

	        carSelectionDialog.setLocationRelativeTo(editViolationDialog);
	        carSelectionDialog.setVisible(true);
	    });

	    // Обработчик нажатия кнопки выбора типа нарушения
	    selectViolationTypeButton.addActionListener(e -> {
	        JDialog violationTypeDialog = new JDialog(editViolationDialog, "Выберите тип нарушения", true);
	        violationTypeDialog.setSize(400, 300);

	        // Получаем список типов нарушений
	        List<ViolationType> violationTypes = em.createQuery("SELECT vt FROM ViolationType vt ORDER BY vt.violationTypeId", ViolationType.class).getResultList();
	        String[] violationTypeNames = violationTypes.stream()
	            .map(ViolationType::getViolationTypeName)
	            .toArray(String[]::new);

	        JList<String> violationTypeList = new JList<>(violationTypeNames);
	        violationTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        
	        violationTypeDialog.add(new JScrollPane(violationTypeList), BorderLayout.CENTER);
	        
	        JButton addNewvViolationTypeButtonInDialog = new JButton("Добавить новый тип нарушения");
	        violationTypeDialog.add(addNewvViolationTypeButtonInDialog, BorderLayout.SOUTH);

	        addNewvViolationTypeButtonInDialog.addActionListener(ev -> {
	            violationTypeController.openAddViolationTypeWindow();
	            // Обновляем список типов нарушений после добавления нового
	            List<ViolationType> updatedViolationTypes = em.createQuery("SELECT vt FROM ViolationType vt ORDER BY vt.violationTypeId", ViolationType.class).getResultList();
	            String[] updatedViolationTypeNames = updatedViolationTypes.stream()
	                .map(ViolationType::getViolationTypeName)
	                .toArray(String[]::new);
	            violationTypeList.setListData(updatedViolationTypeNames);

	            // Если обновленные данные существуют, выбираем последний элемент
	            if (updatedViolationTypeNames.length > 0) {
	                violationTypeList.setSelectedIndex(updatedViolationTypeNames.length - 1);
	                violationTypeField.setText(updatedViolationTypeNames[updatedViolationTypeNames.length - 1]);
	            }
	        });
	        
	        violationTypeList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedViolationType = violationTypeList.getSelectedValue();
	                if (selectedViolationType != null) {
	                    violationTypeField.setText(selectedViolationType);
	                    violationTypeDialog.dispose();
	                }
	            }
	        });

	        violationTypeDialog.setLocationRelativeTo(editViolationDialog);
	        violationTypeDialog.setVisible(true);
	    });
	    
	    // Обработчик нажатия кнопки выбора статьи нарушения
	    selectViolationArticleButton.addActionListener(e -> {
	        JDialog violationArticleDialog = new JDialog(editViolationDialog, "Выберите статью нарушения", true);
	        violationArticleDialog.setSize(400, 300);

	        // Получаем список статей нарушений
	        List<ViolationArticle> violationArticles = em.createQuery("SELECT va FROM ViolationArticle va", ViolationArticle.class).getResultList();
	        String[] violationArticleNames = violationArticles.stream()
	            .map(ViolationArticle::getViolationArticleCode)
	            .toArray(String[]::new);

	        JList<String> violationArticleList = new JList<>(violationArticleNames);
	        violationArticleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        
	        violationArticleDialog.add(new JScrollPane(violationArticleList), BorderLayout.CENTER);
	        
	        JButton addNewViolationArticleButtonInDialog = new JButton("Добавить новую статью");
	        violationArticleDialog.add(addNewViolationArticleButtonInDialog, BorderLayout.SOUTH);

	        addNewViolationArticleButtonInDialog.addActionListener(ev -> {
	            violationArticleController.openAddViolationArticleWindow();
	            // Обновляем список статей нарушений после добавления новой
	            List<ViolationArticle> updatedViolationArticles = em.createQuery("SELECT va FROM ViolationArticle va", ViolationArticle.class).getResultList();
	            String[] updatedViolationArticleNames = updatedViolationArticles.stream()
	                .map(ViolationArticle::getViolationArticleCode)
	                .toArray(String[]::new);
	            violationArticleList.setListData(updatedViolationArticleNames);

	            // Если обновленные данные существуют, выбираем последний элемент
	            if (updatedViolationArticleNames.length > 0) {
	                violationArticleList.setSelectedIndex(updatedViolationArticleNames.length - 1);
	                violationArticleField.setText(updatedViolationArticleNames[updatedViolationArticleNames.length - 1]);
	            }
	        });
	        
	        violationArticleList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedArticle = violationArticleList.getSelectedValue();
	                if (selectedArticle != null) {
	                    violationArticleField.setText(selectedArticle);
	                    violationArticleDialog.dispose();
	                }
	            }
	        });

	        violationArticleDialog.setLocationRelativeTo(editViolationDialog);
	        violationArticleDialog.setVisible(true);
	    });
	    
        // Обработчик для кнопки "Сохранить" - добавляет изменение в нарушении в базу данных
	    saveButton.addActionListener(e -> {
	        try {
	        	Violation tempViolation = new Violation();
	        	tempViolation.setViolationPaid(paymentStatusCheckbox.isSelected());
	        	tempViolation.setViolationResolution(resolutionField.getText());
	        	String date = dateField.getText();
	        	if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
	        		tempViolation.setViolationDate(LocalDate.parse(date));
	        	} else {
	        		tempViolation.setViolationDate(null);
	        	}
	        	String car = carField.getText();
	            int carStart = car.indexOf('(');
	            int carEnd = car.indexOf(')');
	            String licensePlate = car.substring(carStart + 1, carEnd);

	            Car selectedCar = em.createQuery("SELECT c FROM Car c WHERE c.licensePlate = :plate", Car.class)
	                .setParameter("plate", licensePlate)
	                .getSingleResult();
	            tempViolation.setCar(selectedCar);
	            ViolationType selectedType = em.createQuery("SELECT vt FROM ViolationType vt WHERE vt.violationTypeName = :name ", ViolationType.class)
	            		.setParameter("name", violationTypeField.getText())
	            		.getSingleResult();
	            tempViolation.setViolationType(selectedType);
	            ViolationArticle selectedArticle = em.createQuery("SELECT va FROM ViolationArticle va WHERE va.violationArticleCode = :name", ViolationArticle.class)
	                    .setParameter("name", violationArticleField.getText())
	                    .getSingleResult();
	            tempViolation.setViolationArticle(selectedArticle);

	            ViolationValidator.validateViolation(tempViolation, em, oldResolution);
	            
	            violation.setViolationPaid(paymentStatusCheckbox.isSelected());
	        	violation.setViolationResolution(resolutionField.getText());
	            violation.setViolationDate(LocalDate.parse(date));
	            violation.setCar(selectedCar);
	            violation.setViolationArticle(selectedArticle);
	            violation.setViolationType(selectedType);
	            
	            violationService.updateViolation(violation);
	            updateViolationData();
	            editViolationDialog.dispose();
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(editViolationDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
	        }
	    });
	    // Обработчик отмены редактирования
	    cancelButton.addActionListener(e -> editViolationDialog.dispose());

	    editViolationDialog.setLocationRelativeTo(parentWindow);
	    editViolationDialog.setVisible(true);

	}
	
	/**
	 * Удаляет выбранное нарушение.
	 * 
	 * Метод сначала проверяет, выбрано ли нарушение для удаления. Если нет, 
	 * выводится сообщение об ошибке. Затем появляется окно подтверждения удаления.
	 * Если пользователь подтверждает удаление, нарушением удаляется из базы данных.
	 * В случае успеха отображается сообщение об успешном удалении, 
	 * в случае ошибки - сообщение с деталями ошибки.
	 */
	private void deleteViolation() {
	    // Получаем выбранное нарушение для удаления
	    String selectedViolationResolution = violationsPanel.getSelectedViolationResolution();
	    
	    // Проверяем, выбрано ли нарушение для удаления
	    if (selectedViolationResolution == null) {
	        JOptionPane.showMessageDialog(parentWindow, "Выберите нарушение для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
	        return;
	    }
	    
	    // Подтверждение удаления через диалоговое окно
	    int confirm = JOptionPane.showConfirmDialog(
	        parentWindow,
	        "Вы уверены, что хотите удалить выбранное нарушение?",
	        "Подтверждение удаления",
	        JOptionPane.YES_NO_OPTION,
	        JOptionPane.WARNING_MESSAGE
	    );

	    // Если пользователь подтверждает удаление
	    if (confirm == JOptionPane.YES_OPTION) {
	        try {
	            // Пытаемся удалить нарушение
	            violationService.deleteViolation(selectedViolationResolution);
	            updateViolationData(); // Обновляем данные после удаления
	            JOptionPane.showMessageDialog(parentWindow, "Нарушение успешно удалено.", "Успех", JOptionPane.INFORMATION_MESSAGE);
	        } catch (Exception e) {
	            // В случае ошибки выводим сообщение об ошибке
	            JOptionPane.showMessageDialog(parentWindow, "Ошибка при удалении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	/**
	 * Открывает окно поиска нарушений.
	 * 
	 * В этом методе создается и отображается диалоговое окно для поиска нарушений по различным критериям.
	 * В окне имеются поля для ввода дат нарушения, номера постановления, а также для выбора автомобиля, 
	 * типа нарушения и статьи. Пользователь может выбрать параметры поиска и выполнить поиск, либо сбросить фильтры.
	 * 
	 * Метод включает в себя:
	 * - Инициализацию элементов управления и панели для ввода данных.
	 * - Создание диалоговых окон для выбора автомобиля, типа нарушения и статьи.
	 * - Обработку событий для кнопок "Поиск" и "Сбросить фильтры".
	 */
	private void openSearchViolationWindow() {
	    // Создаем панель для поиска с элементами управления
	    JPanel searchPanel = new JPanel(new GridLayout(9, 2));

	    JTextField violationDateFromField = new JTextField(); // Поле для даты начала нарушения
	    JTextField violationDateToField = new JTextField(); // Поле для даты окончания нарушения
	    JTextField resolutionNumberField = new JTextField(); // Поле для номера постановления
	    
	    // Поля для отображения выбранных данных, редактирование отключено
	    JTextField carField = new JTextField(20); 
	    carField.setEditable(false);
	    JTextField violationTypeField = new JTextField(20);
	    violationTypeField.setEditable(false);
	    JTextField violationArticleField = new JTextField(20);
	    violationArticleField.setEditable(false);
	    
	    JCheckBox paymentStatusCheckbox = new JCheckBox(); // Чекбокс для статуса оплаты

	    // Добавление элементов в панель для ввода данных
	    searchPanel.add(new JLabel("Дата нарушения (с):"));
	    searchPanel.add(violationDateFromField);
	    searchPanel.add(new JLabel("Дата нарушения (до):"));
	    searchPanel.add(violationDateToField);
	    searchPanel.add(new JLabel("Номер постановления:"));
	    searchPanel.add(resolutionNumberField);
	    searchPanel.add(new JLabel("Статус оплаты:"));
	    searchPanel.add(paymentStatusCheckbox);

	    // Панель для выбора автомобиля
	    JPanel carPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectCarButton = new JButton("Выбрать автомобиль");
	    carPanel.add(carField);
	    carPanel.add(selectCarButton);
	    searchPanel.add(new JLabel("Автомобиль:"));
	    searchPanel.add(carPanel);

	    // Панель для выбора типа нарушения
	    JPanel violationTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectViolationTypeButton = new JButton("Выбрать тип нарушения");
	    violationTypePanel.add(violationTypeField);
	    violationTypePanel.add(selectViolationTypeButton);
	    searchPanel.add(new JLabel("Тип нарушения:"));
	    searchPanel.add(violationTypePanel);

	    // Панель для выбора статьи нарушения
	    JPanel violationArticlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JButton selectViolationArticleButton = new JButton("Выбрать статью");
	    violationArticlePanel.add(violationArticleField);
	    violationArticlePanel.add(selectViolationArticleButton);
	    searchPanel.add(new JLabel("Статья нарушений:"));
	    searchPanel.add(violationArticlePanel);

	    // Панель для кнопок "Поиск" и "Сбросить фильтры"
	    JPanel buttonPanel = new JPanel();
	    JButton searchButton = new JButton("Поиск");
	    JButton resetButton = new JButton("Сбросить фильтры");
	    buttonPanel.add(searchButton);
	    buttonPanel.add(resetButton);
	    searchPanel.add(buttonPanel);

	    EntityManager em = violationService.getEntityManager();

	    // Обработчик для выбора автомобиля
	    selectCarButton.addActionListener(e -> {
	        // Окно выбора автомобиля
	        JDialog carSelectionDialog = new JDialog(parentWindow, "Выберите автомобиль", true);
	        carSelectionDialog.setSize(400, 300);
	        
	        List<Car> cars = em.createQuery("SELECT c FROM Car c", Car.class).getResultList();
	        String[] carNames = cars.stream()
	            .map(car -> car.getBrand() + " " + car.getModel() + " (" + car.getLicensePlate() + ")")
	            .toArray(String[]::new);

	        JList<String> carList = new JList<>(carNames);
	        carList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        carSelectionDialog.add(new JScrollPane(carList), BorderLayout.CENTER);

	        // Обработчик выбора автомобиля
	        carList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedCar = carList.getSelectedValue();
	                if (selectedCar != null) {
	                    carField.setText(selectedCar);
	                    carSelectionDialog.dispose(); // Закрытие диалога после выбора
	                }
	            }
	        });

	        carSelectionDialog.setLocationRelativeTo(null);
	        carSelectionDialog.setVisible(true);
	    });

	    // Обработчик для выбора типа нарушения
	    selectViolationTypeButton.addActionListener(e -> {
	        // Окно выбора типа нарушения
	        JDialog violationTypeSelectionDialog = new JDialog(parentWindow, "Выберите тип нарушения", true);
	        violationTypeSelectionDialog.setSize(400, 300);

	        List<ViolationType> violationTypes = em.createQuery("SELECT v FROM ViolationType v ORDER BY v.violationTypeId", ViolationType.class).getResultList();
	        String[] violationTypeNames = violationTypes.stream()
	            .map(ViolationType::getViolationTypeName)
	            .toArray(String[]::new);

	        JList<String> violationTypeList = new JList<>(violationTypeNames);
	        violationTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        violationTypeSelectionDialog.add(new JScrollPane(violationTypeList), BorderLayout.CENTER);

	        // Обработчик выбора типа нарушения
	        violationTypeList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedViolationType = violationTypeList.getSelectedValue();
	                if (selectedViolationType != null) {
	                    violationTypeField.setText(selectedViolationType);
	                    violationTypeSelectionDialog.dispose(); // Закрытие диалога после выбора
	                }
	            }
	        });

	        violationTypeSelectionDialog.setLocationRelativeTo(null);
	        violationTypeSelectionDialog.setVisible(true);
	    });

	    // Обработчик для выбора статьи нарушения
	    selectViolationArticleButton.addActionListener(e -> {
	        // Окно выбора статьи нарушения
	        JDialog violationArticleSelectionDialog = new JDialog(parentWindow, "Выберите статью", true);
	        violationArticleSelectionDialog.setSize(400, 300);

	        List<ViolationArticle> violationArticles = em.createQuery("SELECT v FROM ViolationArticle v", ViolationArticle.class).getResultList();
	        String[] violationArticleNames = violationArticles.stream()
	            .map(ViolationArticle::getViolationArticleCode)
	            .toArray(String[]::new);

	        JList<String> violationArticleList = new JList<>(violationArticleNames);
	        violationArticleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        violationArticleSelectionDialog.add(new JScrollPane(violationArticleList), BorderLayout.CENTER);

	        // Обработчик выбора статьи нарушения
	        violationArticleList.addListSelectionListener(event -> {
	            if (!event.getValueIsAdjusting()) {
	                String selectedViolationArticle = violationArticleList.getSelectedValue();
	                if (selectedViolationArticle != null) {
	                    violationArticleField.setText(selectedViolationArticle);
	                    violationArticleSelectionDialog.dispose(); // Закрытие диалога после выбора
	                }
	            }
	        });

	        violationArticleSelectionDialog.setLocationRelativeTo(null);
	        violationArticleSelectionDialog.setVisible(true);
	    });

	    // Обработчик кнопки "Поиск"
	    searchButton.addActionListener(e -> {
	        violationsTableModel.setRowCount(0); // Очистка таблицы перед поиском
	        
	        // Получаем значения фильтров
	        String violationDateFrom = violationDateFromField.getText();
	        String violationDateTo = violationDateToField.getText();
	        String resolutionNumber = resolutionNumberField.getText();
	        String car = carField.getText();
	        String violationType = violationTypeField.getText();
	        String violationArticle = violationArticleField.getText();
	        boolean isPaid = paymentStatusCheckbox.isSelected();

	        // Выполнение поиска
	        violationsPanel.updateViolationData(violationService.searchViolations(violationDateFrom, violationDateTo, resolutionNumber, car, violationType, violationArticle, isPaid));
	    });

	    // Обработчик кнопки "Сбросить фильтры"
	    resetButton.addActionListener(e -> {
	        violationDateFromField.setText(""); // Очистка всех полей
	        violationDateToField.setText("");
	        resolutionNumberField.setText("");
	        carField.setText("");
	        violationTypeField.setText("");
	        violationArticleField.setText("");
	        paymentStatusCheckbox.setSelected(false);
	        updateViolationData(); // Обновление данных после сброса
	    });

	    // Открытие окна поиска с возможностью выбора кнопки "Поиск" или "Сбросить фильтры"
	    Object[] options = {searchButton, resetButton};
	    int result = JOptionPane.showOptionDialog(null, searchPanel, "Поиск нарушений",
	            JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
	    if (result == 0) {
	        searchButton.doClick(); // Нажатие кнопки "Поиск"
	    } else if (result == 1) {
	        resetButton.doClick(); // Нажатие кнопки "Сбросить фильтры"
	    }
	}

}
