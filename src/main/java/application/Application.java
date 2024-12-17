package application;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import controllers.CarController;
import controllers.DriverController;
import controllers.ReportsController;
import controllers.ViolationArticleController;
import controllers.ViolationController;
import controllers.ViolationTypeController;

import services.CarService;
import services.DriverService;
import services.ViolationArticleService;
import services.ViolationService;
import services.ViolationTypeService;
import services.ReportService;
import ui.CarsPanel;
import ui.DriversPanel;
import ui.ViolationArticlesPanel;
import ui.ViolationTypesPanel;
import ui.ViolationsPanel;
import ui.ReportsPanel;
import utils.XMLManager;

/**
 * Класс для создания и отображения главного окна приложения, а также управления
 * взаимодействием между компонентами.
 */
public class Application {
    // Объявление компонентов UI
	private JFrame mainWindow;
	private JPanel navigationPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem exportItem;
    private JMenuItem importItem;
    
    private JButton btnDrivers;
    private JButton btnCars;
    private JButton btnViolations;
    private JButton btnViolationArticles;
    private JButton btnViolationTypes;
    private JButton btnReports;
    private JButton btnAbout;
    
    private JPanel aboutPanel;
    
    // Сервисы для работы с данными
    private DriverService driverService;
    private DriversPanel driversPanel;
    private CarService carService;
    private CarsPanel carsPanel;
    private ViolationService violationService;
    private ViolationsPanel violationsPanel;
    private ViolationArticleService violationArticleService;
    private ViolationArticlesPanel violationArticlesPanel;
    private ViolationTypeService violationTypeService;
    private ViolationTypesPanel violationTypesPanel;
    private ReportService reportService;
    private ReportsPanel reportsPanel;
    
    // Контроллеры для взаимодействия с данными
    private DriverController driverController;
    private CarController carController;
    private ViolationController violationController;
    private ViolationArticleController violationArticleController;
    private ViolationTypeController violationTypeController;
    
    // EntityManager для работы с базой данных
    private EntityManagerFactory emf;
	private EntityManager em;
    
	/**
     * Метод для отображения главного окна приложения.
     * Инициализирует соединение с базой данных и компоненты интерфейса.
     */
	public void show() {
		// Инициализация соединения с базой данных
		initDataBaseConnection();
		
		// Создание главного окна приложения
		mainWindow = new JFrame("Система учета для ГАИ");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(1250, 600);
        mainWindow.setLocationRelativeTo(null);
        
        // Инициализация компонентов интерфейса
        initComponents();
        
        // Настройка компоновки главного окна
        mainWindow.setLayout(new BorderLayout());
        mainWindow.add(navigationPanel, BorderLayout.WEST);
        mainWindow.add(mainPanel, BorderLayout.CENTER);
        
        // Установка меню
        mainWindow.setJMenuBar(menuBar);
        
        // Отображение окна
        mainWindow.setVisible(true);
	}
	
	/**
     * Инициализация соединения с базой данных.
     * Создается EntityManager для работы с базой.
     */
	private void initDataBaseConnection() {
		emf = Persistence.createEntityManagerFactory("persistence_connection");
		em = emf.createEntityManager();
		driverService = new DriverService(em);
		carService = new CarService(em);
		violationService = new ViolationService(em);
		violationArticleService = new ViolationArticleService(em);
		violationTypeService = new ViolationTypeService(em);
		reportService = new ReportService(em);
	}
	
	/**
     * Инициализация всех компонентов интерфейса.
     * Включает панели, меню и навигацию.
     */
	private void initComponents() {
        initNavigationPanel();	// Инициализация панели навигации
        initMainPanel();		// Инициализация основной панели
        initMenuBarPanel();		// Инициализация панели меню
    }
	
	/**
     * Инициализация панели меню.
     * Настроены действия для экспорта и импорта данных.
     */
	private void initMenuBarPanel() {
		menuBar = new JMenuBar();
        fileMenu = new JMenu("Файл");

        exportItem = new JMenuItem("Экспорт в XML");
        importItem = new JMenuItem("Импорт из XML");

        fileMenu.add(exportItem);
        fileMenu.add(importItem);
        menuBar.add(fileMenu);
        
        // Обработчик события для экспорта данных
        exportItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Подтверждение действия экспорта
                	int confirm = JOptionPane.showConfirmDialog(
            	            mainWindow,
            	            "Вы уверены, что хотите экспортировать данные в XML-файл?",
            	            "Подтверждение экспортирования",
            	            JOptionPane.YES_NO_OPTION,
            	            JOptionPane.WARNING_MESSAGE
            	        );

            	        if (confirm != JOptionPane.YES_OPTION) {
            	            return;
            	        }
                	XMLManager.exportDataToXML("data.xml", em);
                	
                    JOptionPane.showMessageDialog(mainWindow, "Экспорт данных выполнен успешно", "Экспорт", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                	ex.printStackTrace();
                    // Ошибка при экспорте
        	        JOptionPane.showMessageDialog(mainWindow, "Произошла ошибка при экспорте данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Обработчик события для импорта данных
        importItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Открытие диалога выбора файла для импорта
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                    FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML Files", "xml");
                    fileChooser.setFileFilter(xmlFilter);

                    int returnValue = fileChooser.showOpenDialog(mainWindow);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();

                        // Диалог для выбора режима импорта
                        String[] options = {"1 - Заменить все данные", "2 - Добавить новые записи", "3 - Добавить или обновить"};
                        String choice = (String) JOptionPane.showInputDialog(
                                mainWindow,
                                "Выберите режим импорта:",
                                "Импорт из XML",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]
                        );

                        if (choice != null) {
                        	int confirm = JOptionPane.showConfirmDialog(
                    	            mainWindow,
                    	            "Вы уверены, что хотите импортировать данные из XML-файла?",
                    	            "Подтверждение импортирования",
                    	            JOptionPane.YES_NO_OPTION,
                    	            JOptionPane.WARNING_MESSAGE
                    	        );

                    	        if (confirm != JOptionPane.YES_OPTION) {
                    	            return;
                    	        }
                            int mode = Integer.parseInt(choice.substring(0, 1));

                            XMLManager.importDataFromXML(selectedFile.getAbsolutePath(), em, mode - 1);

                            JOptionPane.showMessageDialog(mainWindow, "Импорт данных выполнен успешно", "Импорт", JOptionPane.INFORMATION_MESSAGE);
                            
                            driversPanel.updateDriverData(driverService.getAllDrivers());
                            carsPanel.updateCarData(carService.getAllCars());
                            violationsPanel.updateViolationData(violationService.getAllViolations());
                            violationArticlesPanel.updateViolationArticleData(violationArticleService.getAllViolationArticles());
                            violationTypesPanel.updateViolationTypeData(violationTypeService.getAllViolationTypes());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(mainWindow, "Произошла ошибка при импорте данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

	}
	
	/**
     * Инициализация панели навигации с кнопками для перехода между различными панелями приложения.
     */
	private void initNavigationPanel() {
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new GridLayout(7, 1));

        btnDrivers = new JButton("Водители");
        btnCars = new JButton("Автомобили");
        btnViolations = new JButton("Нарушения");
        btnViolationArticles = new JButton("Статьи нарушений");
        btnViolationTypes = new JButton("Типы нарушений");
        btnReports = new JButton("Отчёты");
        btnAbout = new JButton("О приложении");

        btnDrivers.addActionListener(e -> showPanel("driversPanel"));
        btnCars.addActionListener(e -> showPanel("carsPanel"));
        btnViolations.addActionListener(e -> showPanel("violationsPanel"));
        btnViolationArticles.addActionListener(e -> showPanel("violationArticlesPanel"));
        btnViolationTypes.addActionListener(e -> showPanel("violationTypesPanel"));
        btnReports.addActionListener(e -> showPanel("reportsPanel"));
        btnAbout.addActionListener(e -> showAboutDialog());

        navigationPanel.add(btnDrivers);
        navigationPanel.add(btnCars);
        navigationPanel.add(btnViolations);
        navigationPanel.add(btnViolationArticles);
        navigationPanel.add(btnViolationTypes);
        navigationPanel.add(btnReports);
        navigationPanel.add(btnAbout);
    }
	
	/**
     * Инициализация основной панели, которая будет содержать все динамические панели.
     */
	private void initMainPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        aboutPanel = new JPanel();
        aboutPanel.add(new JLabel("Информация о приложении"));
        
        // Инициализация панелей
        driversPanel = new DriversPanel(driverService);
        carsPanel = new CarsPanel(carService);
        violationsPanel = new ViolationsPanel(violationService);
        violationArticlesPanel = new ViolationArticlesPanel(violationArticleService);
        violationTypesPanel = new ViolationTypesPanel(violationTypeService);
        reportsPanel = new ReportsPanel();
        
        driverController = new DriverController(this, mainWindow, driversPanel, driverService, driversPanel.getDriversTableModel());
        carController = new CarController(this, mainWindow, carsPanel, carService, carsPanel.getCarsTableModel(), driverController);
        violationArticleController = new ViolationArticleController(this, mainWindow, violationArticlesPanel, violationArticleService, violationArticlesPanel.getViolationArticlesDefaultTableModel());
        violationTypeController = new ViolationTypeController(this, mainWindow, violationTypesPanel, violationTypeService, violationTypesPanel.getViolationTypesTableModel());
        violationController = new ViolationController(mainWindow, violationsPanel, violationService, violationsPanel.getViolationsTableModel(), carController, violationArticleController, violationTypeController);
        new ReportsController(reportsPanel, reportService);

        // Добавление панелей на основную панель
        mainPanel.add(driversPanel, "driversPanel");
        mainPanel.add(carsPanel, "carsPanel");
        mainPanel.add(violationsPanel, "violationsPanel");
        mainPanel.add(violationArticlesPanel, "violationArticlesPanel");
        mainPanel.add(violationTypesPanel, "violationTypesPanel");
        mainPanel.add(reportsPanel, "reportsPanel");
        mainPanel.add(aboutPanel, "aboutPanel");
    }
	
	/**
     * Обновить все таблицы в приложении.
     */
	public void updateAllTables() {
		driverController.updateCarData();
        carController.updateCarData();
        violationController.updateViolationData();
        violationArticleController.updateViolationArticleData();
        violationTypeController.updateViolationTypeData();
    }
	
	/**
     * Открыть диалоговое окно с информацией о приложении.
     */
	private void showAboutDialog() {
        String aboutText = "<html><h2>Система учета для ГАИ</h2>"
                + "<p>Программа создана для работников ГАИ</p>"
        		+ "<p>Версия: 1.0.0</p>"
                + "<p>Разработано: Харин Андрей Александрович</p>";
        JOptionPane.showMessageDialog(mainWindow, aboutText, "О приложении", JOptionPane.INFORMATION_MESSAGE);
    }
	
	/**
     * Показать панель по имени.
     * @param panelName Имя панели для отображения.
     */
	private void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
}
