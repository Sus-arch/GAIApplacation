package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import entities.Driver;
import services.DriverService;

/**
 * Панель для управления данными о водителях.
 * Содержит таблицу с информацией о водителях и кнопки для выполнения операций:
 * добавление, изменение, удаление, поиск и сброс фильтров.
 */
public class DriversPanel extends JPanel {
    private JTable driversTable;
    private DefaultTableModel driversTableModel;
    private JButton addDriverButton;
    private JButton editDriverButton;
    private JButton deleteDriverButton;
    private JButton searchDriverButton;
    private JButton resetDriverFiltersButton;
    private DriverService driverService;

    /**
     * Конструктор панели управления водителями.
     * @param driverService Сервис для работы с данными водителей.
     */
    public DriversPanel(DriverService driverService) {
        this.driverService = driverService;
        setLayout(new BorderLayout());

        // Заголовок панели
        JLabel titleLabel = new JLabel("Управление водителями", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Инициализация таблицы водителей
        String[] columns = {"Имя", "Фамилия", "Отчество", "Номер ВУ", "Дата рождения", "Город проживания"};
        driversTableModel = new DefaultTableModel(columns, 0);
        driversTable = new JTable(driversTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Отключение редактирования ячеек таблицы
            }
        };

        driversTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Только одиночный выбор строки
        driversTable.getTableHeader().setReorderingAllowed(false); // Отключение перестановки колонок
        add(new JScrollPane(driversTable), BorderLayout.CENTER);

        initControlPanel(); // Инициализация панели управления кнопками
        updateDriverData(driverService.getAllDrivers()); // Заполнение таблицы данными
    }

    /**
     * Инициализация панели управления кнопками.
     */
    private void initControlPanel() {
        JPanel controlPanel = new JPanel();

        addDriverButton = new JButton("Добавить");
        editDriverButton = new JButton("Изменить");
        deleteDriverButton = new JButton("Удалить");
        searchDriverButton = new JButton("Поиск");
        resetDriverFiltersButton = new JButton("Сбросить фильтры");

        controlPanel.add(addDriverButton);
        controlPanel.add(editDriverButton);
        controlPanel.add(deleteDriverButton);
        controlPanel.add(searchDriverButton);
        controlPanel.add(resetDriverFiltersButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Получить модель данных таблицы водителей.
     * @return Модель данных {@link DefaultTableModel}.
     */
    public DefaultTableModel getDriversTableModel() {
        return driversTableModel;
    }

    /**
     * Установить действие для кнопки "Добавить".
     * @param action Слушатель действия.
     */
    public void setAddDriverAction(ActionListener action) {
        addDriverButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Изменить".
     * @param action Слушатель действия.
     */
    public void setEditDriverAction(ActionListener action) {
        editDriverButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Удалить".
     * @param action Слушатель действия.
     */
    public void setDeleteDriverAction(ActionListener action) {
        deleteDriverButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Поиск".
     * @param action Слушатель действия.
     */
    public void setSearchDriverAction(ActionListener action) {
        searchDriverButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Сбросить фильтры".
     * @param action Слушатель действия.
     */
    public void setResetFiltersAction(ActionListener action) {
        resetDriverFiltersButton.addActionListener(action);
    }

    /**
     * Обновить данные таблицы водителей.
     * @param drivers Список водителей для отображения.
     */
    public void updateDriverData(List<Driver> drivers) {
        driversTableModel.setRowCount(0); // Очистка текущих данных таблицы
        for (Driver driver : drivers) {
            driversTableModel.addRow(new Object[]{
                driver.getFirstName(),
                driver.getLastName(),
                driver.getMiddleName(),
                driver.getLicenseNumber(),
                driver.getBirthday(),
                driver.getCity()
            });
        }
    }

    /**
     * Получить индекс выбранной строки таблицы.
     * @return Индекс выбранной строки, или -1, если строка не выбрана.
     */
    public int getSelectedRow() {
        return driversTable.getSelectedRow();
    }

    /**
     * Получить номер водительского удостоверения выбранного водителя.
     * @return Номер ВУ, или {@code null}, если строка не выбрана.
     */
    public String getSelectedDriverLicense() {
        int selectedRow = driversTable.getSelectedRow();
        if (selectedRow != -1) {
            return (String) driversTableModel.getValueAt(selectedRow, 3);
        }
        return null; // Возврат null, если строка не выбрана
    }
}
