package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import entities.Car;
import services.CarService;

/**
 * Панель для управления автомобилями.
 * Содержит таблицу с данными об автомобилях и кнопки для выполнения различных операций (добавление, изменение, удаление, поиск, сброс фильтров).
 */
public class CarsPanel extends JPanel {
    private JTable carsTable;
    private DefaultTableModel carsTableModel;
    private JButton addCarButton;
    private JButton editCarButton;
    private JButton deleteCarButton;
    private JButton searchCarButton;
    private JButton resetCarFiltersButton;
    private CarService carService;

    /**
     * Конструктор панели управления автомобилями.
     * @param carService Сервис для работы с данными автомобилей.
     */
    public CarsPanel(CarService carService) {
        this.carService = carService;
        setLayout(new BorderLayout());

        // Заголовок панели
        JLabel titleLabel = new JLabel("Управление автомобилями", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Инициализация таблицы автомобилей
        String[] columns = {"Бренд", "Модель", "VIN - номер", "Гос. номер", "Владелец", "Дата последнего ТО"};
        carsTableModel = new DefaultTableModel(columns, 0);
        carsTable = new JTable(carsTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Отключение редактирования ячеек таблицы
            }
        };

        carsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carsTable.getTableHeader().setReorderingAllowed(false); // Отключение перестановки колонок
        add(new JScrollPane(carsTable), BorderLayout.CENTER);

        initControlPanel(); // Инициализация панели управления кнопками
        updateCarData(carService.getAllCars()); // Заполнение таблицы данными
    }

    /**
     * Инициализация панели управления кнопками.
     */
    private void initControlPanel() {
        JPanel controlPanel = new JPanel();

        addCarButton = new JButton("Добавить");
        editCarButton = new JButton("Изменить");
        deleteCarButton = new JButton("Удалить");
        searchCarButton = new JButton("Поиск");
        resetCarFiltersButton = new JButton("Сбросить фильтры");

        controlPanel.add(addCarButton);
        controlPanel.add(editCarButton);
        controlPanel.add(deleteCarButton);
        controlPanel.add(searchCarButton);
        controlPanel.add(resetCarFiltersButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Получить модель данных таблицы автомобилей.
     * @return Модель данных {@link DefaultTableModel}.
     */
    public DefaultTableModel getCarsTableModel() {
        return carsTableModel;
    }

    /**
     * Установить действие для кнопки "Добавить".
     * @param action Слушатель действия.
     */
    public void setAddCarAction(ActionListener action) {
        addCarButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Изменить".
     * @param action Слушатель действия.
     */
    public void setEditCarAction(ActionListener action) {
        editCarButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Удалить".
     * @param action Слушатель действия.
     */
    public void setDeleteCarAction(ActionListener action) {
        deleteCarButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Поиск".
     * @param action Слушатель действия.
     */
    public void setSearchCarAction(ActionListener action) {
        searchCarButton.addActionListener(action);
    }

    /**
     * Установить действие для кнопки "Сбросить фильтры".
     * @param action Слушатель действия.
     */
    public void setResetFiltersAction(ActionListener action) {
        resetCarFiltersButton.addActionListener(action);
    }

    /**
     * Обновить данные таблицы автомобилей.
     * @param cars Список автомобилей для отображения.
     */
    public void updateCarData(List<Car> cars) {
        carsTableModel.setRowCount(0); // Очистка текущих данных таблицы
        for (Car car : cars) {
            carsTableModel.addRow(new Object[]{
                    car.getBrand(),
                    car.getModel(),
                    car.getVinNumber(),
                    car.getLicensePlate(),
                    car.getOwner().getFullName(),
                    car.getLastVehicleInspection()
            });
        }
    }

    /**
     * Получить индекс выбранной строки таблицы.
     * @return Индекс выбранной строки, или -1, если ничего не выбрано.
     */
    public int getSelectedRow() {
        return carsTable.getSelectedRow();
    }

    /**
     * Получить госномер выбранного автомобиля.
     * @return Госномер автомобиля, или {@code null}, если строка не выбрана.
     */
    public String getSelectedCarLicensePlate() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow != -1) {
            return (String) carsTableModel.getValueAt(selectedRow, 3);
        }
        return null; // Возврат null, если строка не выбрана
    }
}
