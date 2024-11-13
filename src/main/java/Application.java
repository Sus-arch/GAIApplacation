import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;

import exceptions.*;
import validators.*;

public class Application {
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
    private JButton btnViolationsArticle;
    private JButton btnViolationsType;
    private JButton btnReports;
    private JButton btnAbout;
    
    private JPanel driversPanel;
    private JTable driversTable;
    private DefaultTableModel driversTableModel;
    private JButton addDriverButton;
    private JButton editDriverButton;
    private JButton deleteDriverButton;
    private JButton searchDriverButton;
    
    private JPanel carsPanel;
    private JTable carsTable;
    private DefaultTableModel carsTableModel;
    private JButton addCarButton;
    private JButton editCarButton;
    private JButton deleteCarButton;
    private JButton searchCarButton;
    private JButton resetFiltersButton;
    
    private JPanel violationsPanel;
    private JTable violationsTable;
    private DefaultTableModel violationsTableModel;
    private JButton addViolationButton;
    private JButton editViolationButton;
    private JButton deleteViolationButton;
    private JButton searchViolationButton;
    
    private JPanel violationsArticlePanel;
    private JTable violationsArticleTable;
    private DefaultTableModel violationsArticleTableModel;
    private JButton addViolationArticleButton;
    private JButton editViolationArticleButton;
    private JButton deleteViolationArticleButton;
    private JButton searchViolationArticleButton;
    
    private JPanel violationsTypePanel;
    private JTable violationsTypeTable;
    private DefaultTableModel violationsTypeTableModel;
    private JButton addViolationTypeButton;
    private JButton editViolationTypeButton;
    private JButton deleteViolationTypeButton;
    private JButton searchViolationTypeButton;
    
    private JPanel reportsPanel;
    private JTable reportsTable;
    private DefaultTableModel reportsTableModel;
    private JButton showReportButton;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton generatePdfButton;
    private boolean isReportGenerated = false;
    
    
    private JPanel aboutPanel;
    
    private EntityManagerFactory emf;
	private EntityManager em;
    
    	public void show() {
    		initDataBaseConnection();
    		
    		mainWindow = new JFrame("Система учета для ГАИ");
    		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainWindow.setSize(1250, 600);
            mainWindow.setLocationRelativeTo(null);
            
            initComponents();
            
            mainWindow.setLayout(new BorderLayout());
            mainWindow.add(navigationPanel, BorderLayout.WEST);
            mainWindow.add(mainPanel, BorderLayout.CENTER);
            
            mainWindow.setJMenuBar(menuBar);
            
            mainWindow.setVisible(true);
    	}
    	
    	private void initDataBaseConnection() {
    		emf = Persistence.createEntityManagerFactory("persistence_connection");
    		em = emf.createEntityManager();
    	}
    	
    	private void initComponents() {
            initNavigationPanel();
            initMainPanel();
            initMenuBarPanel();
        }
    	
    	private void initMenuBarPanel() {
    		menuBar = new JMenuBar();
            fileMenu = new JMenu("Файл");

            exportItem = new JMenuItem("Экспорт в XML");
            importItem = new JMenuItem("Импорт из XML");

            fileMenu.add(exportItem);
            fileMenu.add(importItem);
            menuBar.add(fileMenu);
            
            exportItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {                        
                    	XMLManager.exportDataToXML("data.xml", em);
                    	
                        JOptionPane.showMessageDialog(mainWindow, "Экспорт данных выполнен успешно", "Экспорт", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                    	ex.printStackTrace();
            	        JOptionPane.showMessageDialog(mainWindow, "Произошла ошибка при экспорте данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
    	}
    	private void initNavigationPanel() {
            navigationPanel = new JPanel();
            navigationPanel.setLayout(new GridLayout(7, 1));

            btnDrivers = new JButton("Водители");
            btnCars = new JButton("Автомобили");
            btnViolations = new JButton("Нарушения");
            btnViolationsArticle = new JButton("Статьи нарушений");
            btnViolationsType = new JButton("Типы нарушений");
            btnReports = new JButton("Отчёты");
            btnAbout = new JButton("О приложении");

            btnDrivers.addActionListener(e -> showPanel("driversPanel"));
            btnCars.addActionListener(e -> showPanel("carsPanel"));
            btnViolations.addActionListener(e -> showPanel("violationsPanel"));
            btnViolationsArticle.addActionListener(e -> showPanel("violationsArticlePanel"));
            btnViolationsType.addActionListener(e -> showPanel("violationsTypePanel"));
            btnReports.addActionListener(e -> showPanel("reportsPanel"));
            btnAbout.addActionListener(e -> showAboutDialog());

            navigationPanel.add(btnDrivers);
            navigationPanel.add(btnCars);
            navigationPanel.add(btnViolations);
            navigationPanel.add(btnViolationsArticle);
            navigationPanel.add(btnViolationsType);
            navigationPanel.add(btnReports);
            navigationPanel.add(btnAbout);
        }
    	
    	private void initMainPanel() {
            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);

            aboutPanel = new JPanel();
            aboutPanel.add(new JLabel("Информация о приложении"));
            
            initDriversPanel();
            initCarsPanel();
            initViolationsPanel();
            initViolationsArticlePanel();
            initViolationsTypePanel();
            initReportsPanel();

            mainPanel.add(driversPanel, "driversPanel");
            mainPanel.add(carsPanel, "carsPanel");
            mainPanel.add(violationsPanel, "violationsPanel");
            mainPanel.add(violationsArticlePanel, "violationsArticlePanel");
            mainPanel.add(violationsTypePanel, "violationsTypePanel");
            mainPanel.add(reportsPanel, "reportsPanel");
            mainPanel.add(aboutPanel, "aboutPanel");
        }
    	
    	private void initDriversPanel() {
    		driversPanel = new JPanel(new BorderLayout());
    		JLabel titleLabel = new JLabel("Управление водителями", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            driversPanel.add(titleLabel, BorderLayout.NORTH);
    		String [] columns = {"Имя", "Фамилия", "Отчество", "Номер ВУ", "Дата рождения", "Город проживания"};
    		driversTableModel = new DefaultTableModel(columns, 0);
    		driversTable = new JTable(driversTableModel) {
    			@Override
    		    public boolean isCellEditable(int row, int column) {
    		        return false;
    		    }
    		};
    		
    	    driversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	    driversTable.getTableHeader().setReorderingAllowed(false);

            JScrollPane scrollPane = new JScrollPane(driversTable);
            
            updateDriverData();
            
            JPanel driverControlPanel = new JPanel();
            addDriverButton = new JButton("Добавить");
            editDriverButton = new JButton("Изменить");
            deleteDriverButton = new JButton("Удалить");
            searchDriverButton = new JButton("Поиск");
            resetFiltersButton = new JButton("Сбросить фильтры");
            
            driverControlPanel.add(addDriverButton);
            driverControlPanel.add(editDriverButton);
            driverControlPanel.add(deleteDriverButton);
            driverControlPanel.add(searchDriverButton);
            driverControlPanel.add(resetFiltersButton);
            
            addDriverButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	openAddDriverWindow();
                }
            });

            editDriverButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	editDriverWindow();
                }
            });

            deleteDriverButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteDriver();
                }
            });

            searchDriverButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchDriver();
                }
            });
            
            resetFiltersButton.addActionListener(e -> updateDriverData());
            
            driversPanel.add(scrollPane, BorderLayout.CENTER);
            driversPanel.add(driverControlPanel, BorderLayout.SOUTH);
    	}
    	
    	private void deleteDriver() {
    		int selectedRow = driversTable.getSelectedRow();

    	    if (selectedRow  == -1) {
    	        JOptionPane.showMessageDialog(mainWindow, "Выберите водителя для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }
    	    
    	    em.getTransaction().begin();
    	    
    	    String license = (String) driversTableModel.getValueAt(selectedRow, 3);
            Driver driver = (Driver) em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :license").setParameter("license", license).getSingleResult();
            
            em.remove(driver);
            em.getTransaction().commit();
            
    	    driversTableModel.removeRow(selectedRow);
    	    
            JOptionPane.showMessageDialog(mainWindow, "Водетель успешно удален.", "Успех", JOptionPane.INFORMATION_MESSAGE);

    	}
    	
    	private void searchDriver() {
    		JPanel searchPanel = new JPanel(new GridLayout(8, 2));

    		JTextField firstNameField = new JTextField();
		    JTextField lastNameField = new JTextField();
		    JTextField middleNameField = new JTextField();
		    JTextField licenseField = new JTextField();
		    JTextField fromDateField = new JTextField();
		    JTextField toDateField = new JTextField();
		    JTextField cityField = new JTextField();
    		    
		    
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

		    JPanel buttonPanel = new JPanel();
		    JButton searchButton = new JButton("Поиск");
		    JButton resetButton = new JButton("Сбросить фильтры");
		    buttonPanel.add(searchButton);
		    buttonPanel.add(resetButton);
		    
		    searchPanel.add(buttonPanel);
		    
		    searchButton.addActionListener(e -> {
		    	driversTableModel.setRowCount(0);
		        String firstName = firstNameField.getText();
		        String lastName = lastNameField.getText();
		        String middleName = middleNameField.getText();
		        String license = licenseField.getText();
		        String city = cityField.getText();
		        String fromDate = fromDateField.getText();
		        String toDate = toDateField.getText();

		        List<Object[]> searchResults = searchDrivers(firstName, lastName, middleName, license, city, fromDate, toDate);
		        for (Object[] dr : searchResults) {
	                driversTableModel.addRow(dr);
	    		}
		    });

		    resetButton.addActionListener(e -> {
		        firstNameField.setText("");
		        lastNameField.setText("");
		        middleNameField.setText("");
		        licenseField.setText("");
		        cityField.setText("");
		        fromDateField.setText("");
		        toDateField.setText("");
		        updateDriverData();
		    });
		    
		    Object[] options = {searchButton, resetButton};
		    int result = JOptionPane.showOptionDialog(null, searchPanel, "Поиск водителей", 
		        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		    if (result == 0) {
		        searchButton.doClick();
		    } else if (result == 1) {
		        resetButton.doClick();
		    }
    	}
    	
    	private void openAddDriverWindow() {
    		JDialog addDriverDialog = new JDialog(mainWindow, "Добавление водителя", true);
    	    addDriverDialog.setSize(400, 300);
    	    addDriverDialog.setLayout(new BorderLayout());
    	    
    	    JPanel inputPanel = new JPanel(new GridLayout(6, 2));
    	    
    	    JLabel firstNameLabel = new JLabel("Имя:");
    	    JTextField firstNameField = new JTextField();
    	    
    	    JLabel lastNameLabel = new JLabel("Фамилия:");
    	    JTextField lastNameField = new JTextField();
    	    
    	    JLabel middleNameLabel = new JLabel("Отчество:");
    	    JTextField middleNameField = new JTextField();
    	    
    	    JLabel licenseLabel = new JLabel("Номер ВУ:");
    	    JTextField licenseField = new JTextField();
    	    
    	    JLabel birthDateLabel = new JLabel("Дата рождения (ГГГГ-ММ-ДД):");
    	    JTextField birthDateField = new JTextField();
    	    
    	    JLabel cityLabel = new JLabel("Город:");
    	    JTextField cityField = new JTextField();
    	    
    	    inputPanel.add(firstNameLabel);
    	    inputPanel.add(firstNameField);
    	    inputPanel.add(lastNameLabel);
    	    inputPanel.add(lastNameField);
    	    inputPanel.add(middleNameLabel);
    	    inputPanel.add(middleNameField);
    	    inputPanel.add(licenseLabel);
    	    inputPanel.add(licenseField);
    	    inputPanel.add(birthDateLabel);
    	    inputPanel.add(birthDateField);
    	    inputPanel.add(cityLabel);
    	    inputPanel.add(cityField);
    	    
    	    addDriverDialog.add(inputPanel, BorderLayout.CENTER);
    	    
    	    JPanel buttonPanel = new JPanel();
    	    JButton addButton = new JButton("Добавить");
    	    JButton cancelButton = new JButton("Отмена");
    	    
    	    buttonPanel.add(addButton);
    	    buttonPanel.add(cancelButton);
    	    
    	    addDriverDialog.add(buttonPanel, BorderLayout.SOUTH);
    	    
    	    addButton.addActionListener(new ActionListener() {
    	        @Override
    	        public void actionPerformed(ActionEvent e) {
    	            String firstName = firstNameField.getText();
    	            String lastName = lastNameField.getText();
    	            String middleName = middleNameField.getText();
    	            String license = licenseField.getText();
    	            String birthDate = birthDateField.getText();
    	            String city = cityField.getText();
    	            
    	            StringBuilder errors = new StringBuilder();
    	            try {
	    	            if (firstName.isEmpty()) {
	    	                errors.append("Имя не может быть пустым.\n");
	    	            }
	    	            if (lastName.isEmpty()) {
	    	                errors.append("Фамилия не может быть пустой.\n");
	    	            }
	    	            if (!birthDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
	    	                errors.append("Дата рождения должна быть в формате ГГГГ-ММ-ДД.\n");
	    	            }
	    	            if (city.isEmpty()) {
	    	                errors.append("Город не может быть пустым.\n");
	    	            }
	    	            if (errors.length() > 0) {
	                        throw new Exception(errors.toString());
	    	            }
		    	            LicenseNumberValidator.validateLicenseNumber(license);
	                    	LicenseNumberValidator.validateLicenseUniqueness(license, em);
	                    	
	    	            	em.getTransaction().begin();
	    	                Driver newDriver = new Driver();
	    	                newDriver.setFirstName(firstName);
	    	                newDriver.setLastName(lastName);
	    	                newDriver.setMiddleName(middleName);
	    	                newDriver.setLicenseNumber(license);
	    	                newDriver.setBirthday(LocalDate.parse(birthDate));
	    	                newDriver.setCity(city);
	    	                
	    	                em.persist(newDriver);
	    	        		em.getTransaction().commit();
	    	            	
	    	        		updateDriverData();
	    	                
	    	                addDriverDialog.dispose();
	    	            } catch (InvalidLicenseNumberException | LicenseAlreadyExistsException ex) {
	    	                JOptionPane.showMessageDialog(addDriverDialog, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
	    	            } catch (Exception ex) {
	    	                JOptionPane.showMessageDialog(addDriverDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
	    	            }
    	        }
    	    });
    	    
    	    cancelButton.addActionListener(new ActionListener() {
    	        @Override
    	        public void actionPerformed(ActionEvent e) {
    	            addDriverDialog.dispose();
    	        }
    	    });
    	    
    	    addDriverDialog.setLocationRelativeTo(mainWindow);
    	    addDriverDialog.setVisible(true);
    	            
    	}
    	
    	private void editDriverWindow() {
    		int selectedRow = driversTable.getSelectedRow();

    	    if (selectedRow == -1) {
    	        JOptionPane.showMessageDialog(mainWindow, "Выберите водителя для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }
    	    
    	    String currentFirstName  = (String) driversTableModel.getValueAt(selectedRow, 0);
    	    String currentLastName  = (String) driversTableModel.getValueAt(selectedRow, 1);
    	    String currentMiddleName  = (String) driversTableModel.getValueAt(selectedRow, 2);
    	    String currentLicense  = (String) driversTableModel.getValueAt(selectedRow, 3);
    	    String currentBirthDate  = (String) driversTableModel.getValueAt(selectedRow, 4).toString();
    	    String currentCity  = (String) driversTableModel.getValueAt(selectedRow, 5);
    	    
    	    JDialog editDriverDialog = new JDialog(mainWindow, "Изменение данных водителя", true);
            editDriverDialog.setSize(400, 300);
            editDriverDialog.setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel(new GridLayout(6, 2));

            JLabel firstNameLabel = new JLabel("Имя:");
            JTextField firstNameField = new JTextField(currentFirstName );

            JLabel lastNameLabel = new JLabel("Фамилия:");
            JTextField lastNameField = new JTextField(currentLastName );

            JLabel middleNameLabel = new JLabel("Отчество:");
            JTextField middleNameField = new JTextField(currentMiddleName );

            JLabel licenseLabel = new JLabel("Номер ВУ:");
            JTextField licenseField = new JTextField(currentLicense );

            JLabel birthDateLabel = new JLabel("Дата рождения (ГГГГ-ММ-ДД):");
            JTextField birthDateField = new JTextField(currentBirthDate);

            JLabel cityLabel = new JLabel("Город:");
            JTextField cityField = new JTextField(currentCity);

            inputPanel.add(firstNameLabel);
            inputPanel.add(firstNameField);
            inputPanel.add(lastNameLabel);
            inputPanel.add(lastNameField);
            inputPanel.add(middleNameLabel);
            inputPanel.add(middleNameField);
            inputPanel.add(licenseLabel);
            inputPanel.add(licenseField);
            inputPanel.add(birthDateLabel);
            inputPanel.add(birthDateField);
            inputPanel.add(cityLabel);
            inputPanel.add(cityField);

    	    
            editDriverDialog.add(inputPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Сохранить");
            JButton cancelButton = new JButton("Отмена");

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            editDriverDialog.add(buttonPanel, BorderLayout.SOUTH);
    	    
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    String middleName = middleNameField.getText();
                    String license = licenseField.getText();
                    String birthDate = birthDateField.getText();
                    String city = cityField.getText();

                    StringBuilder errors = new StringBuilder();
                    try {
	                    if (firstName.isEmpty()) {
	                        errors.append("Имя не может быть пустым.\n");
	                    }
	                    if (lastName.isEmpty()) {
	                        errors.append("Фамилия не может быть пустой.\n");
	                    }
	                    if (!birthDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
	                        errors.append("Дата рождения должна быть в формате ГГГГ-ММ-ДД.\n");
	                    }
	                    if (city.isEmpty()) {
	                        errors.append("Город не может быть пустым.\n");
	                    }
	
	                    if (errors.length() > 0) {
	                        throw new Exception(errors.toString());
	                    }
	                    	LicenseNumberValidator.validateLicenseNumber(license);
	                    	
	                    	if (!license.equals(currentLicense)) {
	                    		LicenseNumberValidator.validateLicenseUniqueness(license, em);
	                    	}
	                    	
	                    	em.getTransaction().begin();
	                    	Driver driver = (Driver) em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :license").setParameter("license", currentLicense).getSingleResult();
	                        
	                        driver.setFirstName(firstName);
	                        driver.setLastName(lastName);
	                        driver.setMiddleName(middleName);
	                        driver.setLicenseNumber(license);
	                        driver.setBirthday(LocalDate.parse(birthDate));
	                        driver.setCity(city);
	                        
	                        em.merge(driver);
	                	    
	                	    em.getTransaction().commit();
	
	                	    updateDriverData();
	                        editDriverDialog.dispose();
                    } catch (InvalidLicenseNumberException | LicenseAlreadyExistsException ex) {
                        JOptionPane.showMessageDialog(editDriverDialog, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(editDriverDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
    	    
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editDriverDialog.dispose();
                }
            });

            editDriverDialog.setLocationRelativeTo(mainWindow);
            editDriverDialog.setVisible(true);
    	}
    	
    	private List<Object[]> searchDrivers(String firstName, String lastName, String middleName, String license, String city, String fromDate, String toDate) {
    	    List<Object[]> data = new ArrayList<>();
    	    
    	    StringBuilder queryBuilder = new StringBuilder("SELECT d FROM Driver d WHERE 1=1");
    	    
    	    if (!firstName.isEmpty()) {
    	        queryBuilder.append(" AND d.firstName LIKE :firstName");
    	    }
    	    if (!lastName.isEmpty()) {
    	        queryBuilder.append(" AND d.lastName LIKE :lastName");
    	    }
    	    if (!middleName.isEmpty()) {
    	        queryBuilder.append(" AND d.middleName LIKE :middleName");
    	    }
    	    if (!license.isEmpty()) {
    	        queryBuilder.append(" AND d.license LIKE :license");
    	    }
    	    if (!city.isEmpty()) {
    	        queryBuilder.append(" AND d.city LIKE :city");
    	    }
    	    if (!fromDate.isEmpty()) {
    	        queryBuilder.append(" AND d.birthday >= :fromDate");
    	    }
    	    if (!toDate.isEmpty()) {
    	        queryBuilder.append(" AND d.birthday <= :toDate");
    	    }

    	    TypedQuery<Driver> query = em.createQuery(queryBuilder.toString(), Driver.class);
    	    
    	    if (!firstName.isEmpty()) {
    	        query.setParameter("firstName", "%" + firstName + "%");
    	    }
    	    if (!lastName.isEmpty()) {
    	        query.setParameter("lastName", "%" + lastName + "%");
    	    }
    	    if (!middleName.isEmpty()) {
    	        query.setParameter("middleName", "%" + middleName + "%");
    	    }
    	    if (!license.isEmpty()) {
    	        query.setParameter("license", "%" + license + "%");
    	    }
    	    if (!city.isEmpty()) {
    	        query.setParameter("city", "%" + city + "%");
    	    }
    	    if (!fromDate.isEmpty()) {
    	        LocalDate fromLocalDate = LocalDate.parse(fromDate);
    	        query.setParameter("fromDate", fromLocalDate);
    	    }
    	    if (!toDate.isEmpty()) {
    	        LocalDate toLocalDate = LocalDate.parse(toDate);
    	        query.setParameter("toDate", toLocalDate);
    	    }

    	    List<Driver> drivers = query.getResultList();
    	    
    	    for (Driver driver : drivers) {
    	        data.add(new Object[]{
    	            driver.getFirstName(),
    	            driver.getLastName(),
    	            driver.getMiddleName(),
    	            driver.getLicenseNumber(),
    	            driver.getBirthday(),
    	            driver.getCity()
    	        });
    	    }

    	    return data;
    	}
    	
    	private void updateDriverData() {
    		driversTableModel.setRowCount(0);
    		List<Driver> drivers = null;
    		TypedQuery<Driver> query = em.createQuery("SELECT d from Driver d", Driver.class);
    		drivers = query.getResultList();
    		
    		for (Driver dr : drivers) {
    			Object[] row = new Object[]{dr.getFirstName(), dr.getLastName(), dr.getMiddleName(), dr.getLicenseNumber(), dr.getBirthday(), dr.getCity()};
                driversTableModel.addRow(row);
    		}
    		
    	}
    	
    	private void initCarsPanel() {
    		carsPanel = new JPanel(new BorderLayout());
    		JLabel titleLabel = new JLabel("Управление автомобилями", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            carsPanel.add(titleLabel, BorderLayout.NORTH);
    		String [] columns = {"Бренд", "Модель", "VIN - номер", "Гос. номер", "Владелец", "Дата последнего ТО"};
    		carsTableModel = new DefaultTableModel(columns, 0){
    			@Override
    		    public boolean isCellEditable(int row, int column) {
    		        return false;
    		    }
    		};
    		carsTable = new JTable(carsTableModel);
            JScrollPane scrollPane = new JScrollPane(carsTable);
            
            carsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            carsTable.getTableHeader().setReorderingAllowed(false);
            
            updateCarData();
            
            JPanel carControlPanel = new JPanel();
            addCarButton = new JButton("Добавить");
            editCarButton = new JButton("Изменить");
            deleteCarButton = new JButton("Удалить");
            searchCarButton = new JButton("Поиск");
            
            carControlPanel.add(addCarButton);
            carControlPanel.add(editCarButton);
            carControlPanel.add(deleteCarButton);
            carControlPanel.add(searchCarButton);
            
            addCarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	openAddCarWindow();
                }
            });

            editCarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editCarWindow();
                }
            });

            deleteCarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteCar();
                }
            });

            searchCarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchCar();
                }
            });
            
            carsPanel.add(scrollPane, BorderLayout.CENTER);
            carsPanel.add(carControlPanel, BorderLayout.SOUTH);
    	}
    	
    	private void openAddCarWindow() {
    	    JDialog addCarDialog = new JDialog(mainWindow, "Добавление автомобиля", true);
    	    addCarDialog.setSize(580, 300);
    	    addCarDialog.setLayout(new BorderLayout());

    	    JPanel inputPanel = new JPanel(new GridLayout(6, 2));

    	    JLabel brandLabel = new JLabel("Марка:");
    	    JTextField brandField = new JTextField();

    	    JLabel modelLabel = new JLabel("Модель:");
    	    JTextField modelField = new JTextField();

    	    JLabel vinLabel = new JLabel("VIN:");
    	    JTextField vinField = new JTextField();

    	    JLabel licensePlateLabel = new JLabel("Госномер:");
    	    JTextField licensePlateField = new JTextField();

    	    JLabel ownerLabel = new JLabel("Владелец:");

    	    JPanel ownerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	    JTextField ownerField = new JTextField(20);
    	    ownerField.setEditable(false);

    	    JButton selectOwnerButton = new JButton("Выбрать");
    	    JButton addNewDriverButton = new JButton("Добавить нового водителя");

    	    ownerPanel.add(ownerField);
    	    ownerPanel.add(selectOwnerButton);

    	    JLabel lastVehicleInspectionLabel = new JLabel("Дата последнего ТО:");
    	    JTextField lastVehicleInspectionField = new JTextField();

    	    inputPanel.add(brandLabel);
    	    inputPanel.add(brandField);
    	    inputPanel.add(modelLabel);
    	    inputPanel.add(modelField);
    	    inputPanel.add(vinLabel);
    	    inputPanel.add(vinField);
    	    inputPanel.add(licensePlateLabel);
    	    inputPanel.add(licensePlateField);
    	    inputPanel.add(ownerLabel);
    	    inputPanel.add(ownerPanel);
    	    inputPanel.add(lastVehicleInspectionLabel);
    	    inputPanel.add(lastVehicleInspectionField);

    	    JPanel buttonPanel = new JPanel();
    	    JButton addButton = new JButton("Добавить");
    	    JButton cancelButton = new JButton("Отмена");

    	    buttonPanel.add(addButton);
    	    buttonPanel.add(cancelButton);

    	    addCarDialog.add(inputPanel, BorderLayout.CENTER);
    	    addCarDialog.add(buttonPanel, BorderLayout.SOUTH);

    	    selectOwnerButton.addActionListener(e -> {
    	        JDialog driverSelectionDialog = new JDialog(addCarDialog, "Выберите водителя", true);
    	        driverSelectionDialog.setSize(400, 300);
    	        
    	        List<Driver> drivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
    	        String[] driverNames = drivers.stream()
    	            .map(dr -> dr.getLastName() + " " + dr.getFirstName().charAt(0) + "." + dr.getMiddleName().charAt(0) + " (" + dr.getLicenseNumber() + ")")
    	            .toArray(String[]::new);

    	        JList<String> driverList = new JList<>(driverNames);
    	        driverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	        driverSelectionDialog.add(new JScrollPane(driverList), BorderLayout.CENTER);
    	        
    	        JButton addNewDriverButtonInDialog = new JButton("Добавить нового водителя");
    	        driverSelectionDialog.add(addNewDriverButtonInDialog, BorderLayout.SOUTH);

    	        addNewDriverButtonInDialog.addActionListener(addEvent -> {
    	            openAddDriverWindow();
    	            List<Driver> updatedDrivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
    	            String[] updatedDriverNames = updatedDrivers.stream()
    	                .map(dr -> dr.getLastName() + " " + dr.getFirstName().charAt(0) + "." + dr.getMiddleName().charAt(0) + " (" + dr.getLicenseNumber() + ")")
    	                .toArray(String[]::new);
    	            driverList.setListData(updatedDriverNames);
    	            
    	            if (updatedDriverNames.length > 0) {
    	                driverList.setSelectedIndex(updatedDriverNames.length - 1);
    	                ownerField.setText(updatedDriverNames[updatedDriverNames.length - 1]);
    	            }
    	        });

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

    	    addButton.addActionListener(e -> {
    	    	String brand = brandField.getText();
    	    	String model = modelField.getText();
    	    	String vin = vinField.getText();
    	    	String licensePlate = licensePlateField.getText();
    	    	String owner = ownerField.getText();
    	    	String lastVehicleInspection = lastVehicleInspectionField.getText();
	            
	            StringBuilder errors = new StringBuilder();
	            try {
    	            if (brand.isEmpty()) {
    	                errors.append("Бренд не может быть пустым.\n");
    	            }
    	            if (model.isEmpty()) {
    	                errors.append("Модель не может быть пустой.\n");
    	            }
    	            if (vin.isEmpty()) {
    	                errors.append("VIN-номер не может быть пустым.\n");
    	            }
    	            if (licensePlate.isEmpty()) {
    	                errors.append("Госномер не может быть пустым.\n");
    	            }
    	            if (owner.isEmpty()) {
    	                errors.append("Владелец не может быть пустым.\n");
    	            }
    	            if (!lastVehicleInspection.matches("\\d{4}-\\d{2}-\\d{2}") & (!lastVehicleInspection.isEmpty())) {
                        errors.append("Дата последнего ТО должна быть в формате ГГГГ-ММ-ДД.\n");
                    }
    	            if (errors.length() > 0) {
                        throw new Exception(errors.toString());
    	            }
    	            	LicensePlateValidator.validateLicensePlate(licensePlate);
    	            	VinNumberValidator.validateVin(vin);
    	            	
    	            	int startIndex = owner.indexOf('(');
    	            	int endIndex = owner.indexOf(')');
    	            	
    	            	String licenseNumber = owner.substring(startIndex + 1, endIndex);
                    	Driver driver = (Driver) em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :license").setParameter("license", licenseNumber).getSingleResult();

    	            	
    	            	em.getTransaction().begin();
    	                Car newCar = new Car();
    	                newCar.setBrand(brand);
    	                newCar.setModel(model);
    	                newCar.setVinNumber(vin);
    	                newCar.setLicensePlate(licensePlate);
    	                newCar.setOwner(driver);
    	                newCar.setLastVehicleInspection(LocalDate.parse(lastVehicleInspection));
    	                
    	                em.persist(newCar);
    	        		em.getTransaction().commit();
    	            	
    	        		updateCarData();
    	                
    	                addCarDialog.dispose();
    	            } catch (InvalidLicensePlateException | InvalidVinNumberException ex) {
    	                JOptionPane.showMessageDialog(addCarDialog, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    	            } catch (Exception ex) {
    	                JOptionPane.showMessageDialog(addCarDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    	            }
    	    });

    	    cancelButton.addActionListener(e -> addCarDialog.dispose());

    	    addCarDialog.setLocationRelativeTo(mainWindow);
    	    addCarDialog.setVisible(true);
    	}


    	private void editCarWindow() {
    	    int selectedRow = carsTable.getSelectedRow();
    	    JDialog editCarDialog = new JDialog(mainWindow, "Изменение данных автомобиля", true);
    	    editCarDialog.setSize(580, 300);
    	    editCarDialog.setLayout(new BorderLayout());
    	    
    	    if (selectedRow == -1) {
    	        JOptionPane.showMessageDialog(mainWindow, "Выберите автомобиль для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }
    	    
    	    String currentBrand = (String) carsTableModel.getValueAt(selectedRow, 0);
    	    String currentModel = (String) carsTableModel.getValueAt(selectedRow, 1);
    	    String currentVin = (String) carsTableModel.getValueAt(selectedRow, 2);
    	    String currentLicensePlate = (String) carsTableModel.getValueAt(selectedRow, 3);
    	    String currentOwner = (String) carsTableModel.getValueAt(selectedRow, 4);
    	    Car currentCar = (Car) em.createQuery("SELECT c FROM Car c WHERE c.licensePlate = :licensePlate").setParameter("licensePlate", currentLicensePlate).getSingleResult();
    	    currentOwner = currentOwner + " (" + currentCar.getOwner().getLicenseNumber() + ")";

    	    String currentLastVehicleInspection = (String) carsTableModel.getValueAt(selectedRow, 5).toString();
    	    JPanel inputPanel = new JPanel(new GridLayout(6, 2));
    	    

    	    JLabel brandLabel = new JLabel("Марка:");
    	    JTextField brandField = new JTextField(currentBrand);

    	    JLabel modelLabel = new JLabel("Модель:");
    	    JTextField modelField = new JTextField(currentModel);

    	    JLabel vinLabel = new JLabel("VIN:");
    	    JTextField vinField = new JTextField(currentVin);

    	    JLabel licensePlateLabel = new JLabel("Госномер:");
    	    JTextField licensePlateField = new JTextField(currentLicensePlate);

    	    JLabel ownerLabel = new JLabel("Владелец:");
    	    JPanel ownerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	    JTextField ownerField = new JTextField(20);
    	    ownerField.setEditable(false);
    	    ownerField.setText(currentOwner);

    	    JButton selectOwnerButton = new JButton("Выбрать");

    	    ownerPanel.add(ownerField);
    	    ownerPanel.add(selectOwnerButton);

    	    JLabel lastVehicleInspectionLabel = new JLabel("Дата последнего ТО (ГГГГ-ММ-ДД):");
    	    JTextField lastVehicleInspectionField = new JTextField(currentLastVehicleInspection);

    	    inputPanel.add(brandLabel);
    	    inputPanel.add(brandField);
    	    inputPanel.add(modelLabel);
    	    inputPanel.add(modelField);
    	    inputPanel.add(vinLabel);
    	    inputPanel.add(vinField);
    	    inputPanel.add(licensePlateLabel);
    	    inputPanel.add(licensePlateField);
    	    inputPanel.add(ownerLabel);
    	    inputPanel.add(ownerPanel);
    	    inputPanel.add(lastVehicleInspectionLabel);
    	    inputPanel.add(lastVehicleInspectionField);

    	    editCarDialog.add(inputPanel, BorderLayout.CENTER);

    	    JPanel buttonPanel = new JPanel();
    	    JButton saveButton = new JButton("Сохранить");
    	    JButton cancelButton = new JButton("Отмена");

    	    buttonPanel.add(saveButton);
    	    buttonPanel.add(cancelButton);
    	    
    	    editCarDialog.add(inputPanel, BorderLayout.CENTER);
    	    editCarDialog.add(buttonPanel, BorderLayout.SOUTH);
    	    
    	    selectOwnerButton.addActionListener(e -> {
    	        JDialog driverSelectionDialog = new JDialog(editCarDialog, "Выберите водителя", true);
    	        driverSelectionDialog.setSize(400, 300);

    	        List<Driver> drivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
    	        String[] driverNames = drivers.stream()
    	            .map(dr -> dr.getLastName() + " " + dr.getFirstName().charAt(0) + "." + dr.getMiddleName().charAt(0) + " (" + dr.getLicenseNumber() + ")")
    	            .toArray(String[]::new);

    	        JList<String> driverList = new JList<>(driverNames);
    	        driverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	        driverSelectionDialog.add(new JScrollPane(driverList), BorderLayout.CENTER);

    	        JButton addNewDriverButtonInDialog = new JButton("Добавить нового водителя");
    	        driverSelectionDialog.add(addNewDriverButtonInDialog, BorderLayout.SOUTH);

    	        addNewDriverButtonInDialog.addActionListener(addEvent -> {
    	            openAddDriverWindow();
    	            List<Driver> updatedDrivers = em.createQuery("SELECT d FROM Driver d", Driver.class).getResultList();
    	            String[] updatedDriverNames = updatedDrivers.stream()
    	                .map(dr -> dr.getLastName() + " " + dr.getFirstName().charAt(0) + "." + dr.getMiddleName().charAt(0) + " (" + dr.getLicenseNumber() + ")")
    	                .toArray(String[]::new);
    	            driverList.setListData(updatedDriverNames);

    	            if (updatedDriverNames.length > 0) {
    	                driverList.setSelectedIndex(updatedDriverNames.length - 1);
    	                ownerField.setText(updatedDriverNames[updatedDriverNames.length - 1]);
    	            }
    	        });

    	        driverList.addListSelectionListener(event -> {
    	            if (!event.getValueIsAdjusting()) {
    	                String selectedDriver = driverList.getSelectedValue();
    	                if (selectedDriver != null) {
    	                    ownerField.setText(selectedDriver);
    	                    driverSelectionDialog.dispose();
    	                }
    	            }
    	        });

    	        driverSelectionDialog.setLocationRelativeTo(editCarDialog);
    	        driverSelectionDialog.setVisible(true);
    	    });
    	    
    	    saveButton.addActionListener(e -> {
	            String brand = brandField.getText();
	            String model = modelField.getText();
	            String vin = vinField.getText();
	            String licensePlate = licensePlateField.getText();
	            String owner = (String) ownerField.getText();
	            String lastVehicleInspection = lastVehicleInspectionField.getText();

	            StringBuilder errors = new StringBuilder();
	            try {
	            	if (brand.isEmpty()) {
    	                errors.append("Бренд не может быть пустым.\n");
    	            }
    	            if (model.isEmpty()) {
    	                errors.append("Модель не может быть пустой.\n");
    	            }
    	            if (vin.isEmpty()) {
    	                errors.append("VIN-номер не может быть пустым.\n");
    	            }
    	            if (licensePlate.isEmpty()) {
    	                errors.append("Госномер не может быть пустым.\n");
    	            }
    	            if (owner.isEmpty()) {
    	                errors.append("Владелец не может быть пустым.\n");
    	            }
    	            if (!lastVehicleInspection.matches("\\d{4}-\\d{2}-\\d{2}") & (!lastVehicleInspection.isEmpty())) {
                        errors.append("Дата последнего ТО должна быть в формате ГГГГ-ММ-ДД.\n");
                    }
    	            if (errors.length() > 0) {
                        throw new Exception(errors.toString());
    	            }

    	            LicensePlateValidator.validateLicensePlate(licensePlate);
	            	VinNumberValidator.validateVin(vin);
	            	
	            	int startIndex = owner.indexOf('(');
	            	int endIndex = owner.indexOf(')');
	            	
	            	String licenseNumber = owner.substring(startIndex + 1, endIndex);
                	Driver driver = (Driver) em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :license").setParameter("license", licenseNumber).getSingleResult();

    	            em.getTransaction().begin();
    	            Car car = em.createQuery("SELECT c FROM Car c WHERE c.licensePlate = :licensePlate", Car.class)
    	                        .setParameter("licensePlate", currentLicensePlate)
    	                        .getSingleResult();

    	            car.setBrand(brand);
    	            car.setModel(model);
    	            car.setVinNumber(vin);
    	            car.setLicensePlate(licensePlate);
    	            car.setOwner(driver);
    	            car.setLastVehicleInspection(LocalDate.parse(lastVehicleInspection));

    	            em.merge(car);
    	            em.getTransaction().commit();

    	            updateCarData();
    	            editCarDialog.dispose();
	            } catch (InvalidLicensePlateException | InvalidVinNumberException ex) {
	                JOptionPane.showMessageDialog(editCarDialog, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    	        } catch (Exception ex) {
    	            JOptionPane.showMessageDialog(editCarDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    	        }
    	    });

    	    cancelButton.addActionListener(e -> editCarDialog.dispose());

    	    editCarDialog.setLocationRelativeTo(mainWindow);
    	    editCarDialog.setVisible(true);
    	}
    	
    	
    	private void deleteCar() {
    		int selectedRow = carsTable.getSelectedRow();

    	    if (selectedRow  == -1) {
    	        JOptionPane.showMessageDialog(mainWindow, "Выберите автомобиль для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }
    	    
    	    em.getTransaction().begin();
 
    	    String number = (String) carsTableModel.getValueAt(selectedRow, 3);
            Car car = (Car) em.createQuery("SELECT c FROM Car c WHERE c.licensePlate = :licensePlate").setParameter("licensePlate", number).getSingleResult();
            
            em.remove(car);
            em.getTransaction().commit();
            
    	    carsTableModel.removeRow(selectedRow);
    	    
            JOptionPane.showMessageDialog(mainWindow, "Автомобиль успешно удален.", "Успех", JOptionPane.INFORMATION_MESSAGE);
    	}
    	
    	private void searchCar() {
    		
    	}
    	
    	private void updateCarData() {
    		carsTableModel.setRowCount(0);
    		List<Car> cars = null;
    		TypedQuery<Car> query = em.createQuery("SELECT c from Car c", Car.class);
    		cars = query.getResultList();
    		
    		for (Car car : cars) {
    			Driver dr = car.getOwner();
    			String ownerName = dr.getLastName() + " " + dr.getFirstName().charAt(0) + "." + dr.getMiddleName().charAt(0);
    			Object[] row = new Object[]{car.getBrand(), car.getModel(), car.getVinNumber(), car.getLicensePlate(), ownerName, car.getLastVehicleInspection()};
    			carsTableModel.addRow(row);

    		}
    	}
    	
    	private void initViolationsPanel() {
    		violationsPanel = new JPanel(new BorderLayout());
    		JLabel titleLabel = new JLabel("Управление нарушениями", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            violationsPanel.add(titleLabel, BorderLayout.NORTH);
    		String [] columns = {"Статья нарушения", "Тип нарушения", "Гос. номер", "Нарушитель", "Дата нарушения", "Оплачено"};
    		violationsTableModel = new DefaultTableModel(columns, 0){
    			@Override
    		    public boolean isCellEditable(int row, int column) {
    		        return false;
    		    }
    		};
    		violationsTable = new JTable(violationsTableModel);
            JScrollPane scrollPane = new JScrollPane(violationsTable);
            
            violationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            violationsTable.getTableHeader().setReorderingAllowed(false);
            
            updateViolationData();
            
            JPanel violationControlPanel = new JPanel();
            addViolationButton = new JButton("Добавить");
            editViolationButton = new JButton("Изменить");
            deleteViolationButton = new JButton("Удалить");
            searchViolationButton = new JButton("Поиск");
            
            violationControlPanel.add(addViolationButton);
            violationControlPanel.add(editViolationButton);
            violationControlPanel.add(deleteViolationButton);
            violationControlPanel.add(searchViolationButton);
            
            addViolationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	addViolation();
                }
            });

            editViolationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editViolation();
                }
            });

            deleteViolationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteViolation();
                }
            });

            searchViolationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchViolation();
                }
            });
            
            violationsPanel.add(scrollPane, BorderLayout.CENTER);
            violationsPanel.add(violationControlPanel, BorderLayout.SOUTH);
    	}
    	
    	private void addViolation() {
    		String violationArticle = JOptionPane.showInputDialog(mainWindow, "Введите статью нарушения:");
            String car = JOptionPane.showInputDialog(mainWindow, "Введите номер автомобиля:");
            String driver = JOptionPane.showInputDialog(mainWindow, "Введите номер ВУ нарушителя:");
            String date = JOptionPane.showInputDialog(mainWindow, "Введите дату совершения нарушения(ГГГГ-ММ-ДД):");
            String paid = JOptionPane.showInputDialog(mainWindow, "Введите статус оплаты:");
            

    	}
    	
    	private void editViolation() {
    		
    	}
    	
    	private void deleteViolation() {
    		
    	}
    	
    	private void searchViolation() {
    		
    	}
    	
    	private void updateViolationData() {
            violationsTableModel.setRowCount(0);
    		List<Violation> violations = null;
    		TypedQuery<Violation> query = em.createQuery("SELECT v from Violation v", Violation.class);
    		violations = query.getResultList();
    		
    		for (Violation violation : violations) {
    			Driver dr = violation.getCar().getOwner();
    			String ownerName = dr.getLastName() + " " + dr.getFirstName().charAt(0) + "." + dr.getMiddleName().charAt(0) + ".";
    			Object[] row = new Object[]{violation.getViolationArticle().getViolationArticleCode(), violation.getViolationType().getViolationTypeName(), violation.getCar().getLicensePlate(), ownerName, violation.getViolationDate(), violation.getViolationPaid()};
    			violationsTableModel.addRow(row);

    		}
    	}
    	
    	private void initViolationsArticlePanel() {
    		violationsArticlePanel = new JPanel(new BorderLayout());
    		JLabel titleLabel = new JLabel("Управление статьями нарушений", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            violationsArticlePanel.add(titleLabel, BorderLayout.NORTH);
    		String [] columns = {"Статья нарушения", "Описание статьи", "Размер штрафа"};
    		violationsArticleTableModel = new DefaultTableModel(columns, 0){
    			@Override
    		    public boolean isCellEditable(int row, int column) {
    		        return false;
    		    }
    		};
    		violationsArticleTable = new JTable(violationsArticleTableModel);
            JScrollPane scrollPane = new JScrollPane(violationsArticleTable);
            
            violationsArticleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            violationsArticleTable.getTableHeader().setReorderingAllowed(false);
            
            updateViolationArticleData();
            
            JPanel violationArticleControlPanel = new JPanel();
            addViolationArticleButton = new JButton("Добавить");
            editViolationArticleButton = new JButton("Изменить");
            deleteViolationArticleButton = new JButton("Удалить");
            searchViolationArticleButton = new JButton("Поиск");
            
            violationArticleControlPanel.add(addViolationArticleButton);
            violationArticleControlPanel.add(editViolationArticleButton);
            violationArticleControlPanel.add(deleteViolationArticleButton);
            violationArticleControlPanel.add(searchViolationArticleButton);
            
            addViolationArticleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	addViolationArticle();
                }
            });

            editViolationArticleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editViolationArticle();
                }
            });

            deleteViolationArticleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteViolationArticle();
                }
            });

            searchViolationArticleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchViolationArticle();
                }
            });
            
            violationsArticlePanel.add(scrollPane, BorderLayout.CENTER);
            violationsArticlePanel.add(violationArticleControlPanel, BorderLayout.SOUTH);
    	}
    	
    	private void addViolationArticle() {
    		String violationArticle = JOptionPane.showInputDialog(mainWindow, "Введите статью нарушения:");
            String violationArticleDescription = JOptionPane.showInputDialog(mainWindow, "Введите описание статьи:");
    		String fine = JOptionPane.showInputDialog(mainWindow, "Введите размер штрафа:");

    	}
    	
    	private void editViolationArticle() {
    		
    	}
    	
    	private void deleteViolationArticle() {
    		
    	}
    	
    	private void searchViolationArticle() {
    		
    	}
    	
    	private void updateViolationArticleData() {
            violationsArticleTableModel.setRowCount(0);
    		List<ViolationArticle> violationArticles = null;
    		TypedQuery<ViolationArticle> query = em.createQuery("SELECT va from ViolationArticle va", ViolationArticle.class);
    		violationArticles = query.getResultList();
    		
    		for (ViolationArticle violationArticle : violationArticles) {
    			Object[] row = new Object[]{violationArticle.getViolationArticleCode(), violationArticle.getViolationArticleDescription(), violationArticle.getViolationArticleFine()};
    			violationsArticleTableModel.addRow(row);

    		}
    	}
    	
    	private void initViolationsTypePanel() {
    		violationsTypePanel = new JPanel(new BorderLayout());
    		JLabel titleLabel = new JLabel("Управление типами нарушений", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            violationsTypePanel.add(titleLabel, BorderLayout.NORTH);
    		String [] columns = {"Тип нарушения", "Количество нарушений"};
    		violationsTypeTableModel = new DefaultTableModel(columns, 0){
    			@Override
    		    public boolean isCellEditable(int row, int column) {
    		        return false;
    		    }
    		};
    		violationsTypeTable = new JTable(violationsTypeTableModel);
            JScrollPane scrollPane = new JScrollPane(violationsTypeTable);
            
            violationsTypeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            violationsTypeTable.getTableHeader().setReorderingAllowed(false);
            
            updateViolationTypeData();
            
            JPanel violationTypeControlPanel = new JPanel();
            addViolationTypeButton = new JButton("Добавить");
            editViolationTypeButton = new JButton("Изменить");
            deleteViolationTypeButton = new JButton("Удалить");
            searchViolationTypeButton = new JButton("Поиск");
            
            violationTypeControlPanel.add(addViolationTypeButton);
            violationTypeControlPanel.add(editViolationTypeButton);
            violationTypeControlPanel.add(deleteViolationTypeButton);
            violationTypeControlPanel.add(searchViolationTypeButton);
            
            addViolationTypeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	addViolationType();
                }
            });

            editViolationTypeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editViolationType();
                }
            });

            deleteViolationTypeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteViolationType();
                }
            });

            searchViolationTypeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    searchViolationType();
                }
            });
            
            violationsTypePanel.add(scrollPane, BorderLayout.CENTER);
            violationsTypePanel.add(violationTypeControlPanel, BorderLayout.SOUTH);
    	}
    	
    	private void addViolationType() {
    		String violationType = JOptionPane.showInputDialog(mainWindow, "Введите тип нарушения:");
    	}
    	
    	private void editViolationType() {
    		
    	}
    	
    	private void deleteViolationType() {
    		
    	}
    	
    	private void searchViolationType() {
    		
    	}
    	
    	private void updateViolationTypeData() {
            violationsTypeTableModel.setRowCount(0);
    		List<ViolationType> violationTypes = null;
    		TypedQuery<ViolationType> query = em.createQuery("SELECT vt from ViolationType vt", ViolationType.class);
    		violationTypes = query.getResultList();
    		
    		for (ViolationType violationType : violationTypes) {
    			Integer typeId = violationType.getViolationTypeId();
    			Integer countViolation = em.createQuery("SELECT v from Violation v WHERE v.violationType = :type", Violation.class).setParameter("type", violationType).getResultList().size();
    			Object[] row = new Object[]{violationType.getViolationTypeName(), countViolation};
    			violationsTypeTableModel.addRow(row);

    		}
    	}
    	
    	private void initReportsPanel() {
    		reportsPanel = new JPanel(new BorderLayout());

            JLabel titleLabel = new JLabel("Отчеты по нарушениям", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            reportsPanel.add(titleLabel, BorderLayout.NORTH);

            JPanel datePanel = new JPanel();
            datePanel.setLayout(new FlowLayout());

            JLabel startDateLabel = new JLabel("Начало периода (ГГГГ-ММ-ДД):");
            startDateField = new JTextField(10);

            JLabel endDateLabel = new JLabel("Конец периода (ГГГГ-ММ-ДД):");
            endDateField = new JTextField(10);

            datePanel.add(startDateLabel);
            datePanel.add(startDateField);
            datePanel.add(endDateLabel);
            datePanel.add(endDateField);

            showReportButton = new JButton("Показать отчет");
            generatePdfButton = new JButton("Сохранить отчет в PDF");
            generatePdfButton.setEnabled(false);
            datePanel.add(showReportButton);
            datePanel.add(generatePdfButton);
            
            reportsPanel.add(datePanel, BorderLayout.NORTH);

            String[] columnNames = {"Дата", "Водитель", "Автомобиль", "Нарушение"};
            reportsTableModel = new DefaultTableModel(columnNames, 0);
            reportsTable = new JTable(reportsTableModel){
    			@Override
    		    public boolean isCellEditable(int row, int column) {
    		        return false;
    		    }
    		};
            JScrollPane scrollPane = new JScrollPane(reportsTable);
            reportsPanel.add(scrollPane, BorderLayout.CENTER);
            
            reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            reportsTable.getTableHeader().setReorderingAllowed(false);
            
            showReportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
	        		String startDateText = startDateField.getText();
	                String endDateText = endDateField.getText();
                	StringBuilder errors = new StringBuilder();
                	
    	            try {
    	            	if (startDateText.isEmpty()) {
	    	                errors.append("Не указана дата начала периода.\n");
    	            	}
    	            	if (endDateText.isEmpty()) {
	    	                errors.append("Не указана дата конца периода.\n");
    	            	}
	    	            if (!startDateText.matches("\\d{4}-\\d{2}-\\d{2}") && !startDateText.isEmpty()) {
	    	                errors.append("Дата начала периода должна быть в формате ГГГГ-ММ-ДД.\n");
	    	            }
	    	            if (!endDateText.matches("\\d{4}-\\d{2}-\\d{2}") && !endDateText.isEmpty()) {
	    	                errors.append("Дата конца периода должна быть в формате ГГГГ-ММ-ДД.\n");
	    	            }
	    	            if (errors.length() > 0) {
	                        throw new Exception(errors.toString());
	    	            }	
		    	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    	            
		    	            LocalDate startDate = LocalDate.parse(startDateText, formatter);
		    	            LocalDate endDate = LocalDate.parse(endDateText, formatter);
		    	            
	    	            	showReport(startDate, endDate);
	    	            } catch (Exception ex) {
	    	                JOptionPane.showMessageDialog(reportsPanel, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
	    	            }
                    
                }
            });
            generatePdfButton.addActionListener(new ActionListener() {
            	@Override
            	public void actionPerformed(ActionEvent e) {
            		String startDateText = startDateField.getText();
	                String endDateText = endDateField.getText();
                	StringBuilder errors = new StringBuilder();
                	
                	try {
    	            	if (!isReportGenerated) {
	    	                errors.append("Сначала нужно сгенерировать отчёт перед сохранением в PDF.\n");
    	            	}
	    	            if (errors.length() > 0) {
	                        throw new Exception(errors.toString());
	    	            }	
		    	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    	            
		    	            LocalDate startDate = LocalDate.parse(startDateText, formatter);
		    	            LocalDate endDate = LocalDate.parse(endDateText, formatter);
		    	            
	    	            	XMLManager.createReportXML("report.xml", getReportData(startDate, endDate), startDateText, endDateText);
	    	            	ReportManager.generateViolationReport("report.xml", "Report.pdf");
	                        JOptionPane.showMessageDialog(reportsPanel, "Создание отчёта успешно завершено.", "Отчёт создан", JOptionPane.INFORMATION_MESSAGE);
                	} catch (Exception ex) {
    	                JOptionPane.showMessageDialog(reportsPanel, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    	            }
            	}
            });
    	}
    	
    	private void showReport(LocalDate startDate, LocalDate endDate) {
    		generatePdfButton.setEnabled(true);
    		isReportGenerated = true; 
            List<Object[]> data = getReportData(startDate, endDate);
            reportsTableModel.setRowCount(0);
            
            for (Object[] row : data) {
                reportsTableModel.addRow(row);
            }
    	}
    	
    	private List<Object[]> getReportData(LocalDate startDate, LocalDate endDate) {
            List<Object[]> data = new ArrayList<>();
            List<Violation> violations = null;
    		TypedQuery<Violation> query = em.createQuery("SELECT v from Violation v WHERE v.violationDate BETWEEN :startDate AND :endDate ", Violation.class)
    				.setParameter("startDate", startDate).setParameter("endDate", endDate);
    		violations = query.getResultList();
    		for (Violation violation : violations) {
    			Driver dr = violation.getCar().getOwner();
    			String driverName = dr.getLastName() + " " + dr.getFirstName().charAt(0) + "." + dr.getMiddleName().charAt(0) + ".";
    	        data.add(new Object[]{violation.getViolationDate(), driverName, violation.getCar().getBrand() + " " + violation.getCar().getModel() + " (" + violation.getCar().getLicensePlate() + ")", violation.getViolationType().getViolationTypeName()});
    	    }

            return data;
        }
    	
    	private void showAboutDialog() {
            String aboutText = "<html><h2>Система учета для ГАИ</h2>"
                    + "<p>Программа создана для работников ГАИ</p>"
                    + "<p>Разработано: Харин Андрей Александрович</p>";;
            JOptionPane.showMessageDialog(mainWindow, aboutText, "О приложении", JOptionPane.INFORMATION_MESSAGE);
        }
    	
    	private void showPanel(String panelName) {
            cardLayout.show(mainPanel, panelName);
        }
}
