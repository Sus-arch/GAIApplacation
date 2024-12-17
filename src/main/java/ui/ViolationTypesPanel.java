package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import entities.Violation;
import entities.ViolationType;
import services.ViolationTypeService;

/**
 * Панель для управления типами нарушений.
 * Позволяет добавлять, изменять, удалять, искать и сбрасывать фильтры типов нарушений.
 */
public class ViolationTypesPanel extends JPanel {
    private JTable violationTypesTable;
    private DefaultTableModel violationTypesTableModel;
    private JButton addViolationTypeButton;
    private JButton editViolationTypeButton;
    private JButton deleteViolationTypeButton;
    private JButton searchViolationTypeButton;
    private JButton resetViolationTypeFiltersButton;
    private ViolationTypeService violationTypeService;

    /**
     * Конструктор панели для управления типами нарушений.
     * 
     * @param violationTypeService сервис для работы с типами нарушений.
     */
    public ViolationTypesPanel(ViolationTypeService violationTypeService) {
        this.violationTypeService = violationTypeService;
        setLayout(new BorderLayout());
        
        // Создаем заголовок панели
        JLabel titleLabel = new JLabel("Управление типами нарушений", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        
        // Инициализация таблицы типов нарушений
        String[] columns = {"Тип нарушения", "Количество нарушений"};
        violationTypesTableModel = new DefaultTableModel(columns, 0);
        violationTypesTable = new JTable(violationTypesTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование ячеек
            }
        };
        
        violationTypesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Одиночный выбор строк
        violationTypesTable.getTableHeader().setReorderingAllowed(false); // Запрещаем перетаскивание заголовков колонок
        add(new JScrollPane(violationTypesTable), BorderLayout.CENTER);

        initControlPanel(); // Инициализация панели управления
        updateViolationTypeData(violationTypeService.getAllViolationTypes());
    }

    /**
     * Инициализация панели управления с кнопками действий.
     */
    private void initControlPanel() {
        JPanel controlPanel = new JPanel();

        addViolationTypeButton = new JButton("Добавить");
        editViolationTypeButton = new JButton("Изменить");
        deleteViolationTypeButton = new JButton("Удалить");
        searchViolationTypeButton = new JButton("Поиск");
        resetViolationTypeFiltersButton = new JButton("Сбросить фильтры");

        controlPanel.add(addViolationTypeButton);
        controlPanel.add(editViolationTypeButton);
        controlPanel.add(deleteViolationTypeButton);
        controlPanel.add(searchViolationTypeButton);
        controlPanel.add(resetViolationTypeFiltersButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * @return Модель таблицы типов нарушений.
     */
    public DefaultTableModel getViolationTypesTableModel() {
        return violationTypesTableModel;
    }

    /**
     * Устанавливает действие для кнопки "Добавить".
     * 
     * @param action обработчик действия.
     */
    public void setAddViolationTypeAction(ActionListener action) {
        addViolationTypeButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Изменить".
     * 
     * @param action обработчик действия.
     */
    public void setEditViolationTypeAction(ActionListener action) {
        editViolationTypeButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Удалить".
     * 
     * @param action обработчик действия.
     */
    public void setDeleteViolationTypeAction(ActionListener action) {
        deleteViolationTypeButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Поиск".
     * 
     * @param action обработчик действия.
     */
    public void setSearchViolationTypeAction(ActionListener action) {
        searchViolationTypeButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Сбросить фильтры".
     * 
     * @param action обработчик действия.
     */
    public void setResetFiltersAction(ActionListener action) {
        resetViolationTypeFiltersButton.addActionListener(action);
    }

    /**
     * Обновляет данные в таблице типов нарушений.
     * 
     * @param violationTypes список типов нарушений для отображения.
     */
    public void updateViolationTypeData(List<ViolationType> violationTypes) {
        violationTypesTableModel.setRowCount(0); // Очищаем таблицу перед добавлением новых данных
        for (ViolationType violationType : violationTypes) {
            // Получаем количество нарушений для типа
            Integer countViolation = violationTypeService.getEntityManager()
                    .createQuery("SELECT v from Violation v WHERE v.violationType = :type", Violation.class)
                    .setParameter("type", violationType)
                    .getResultList()
                    .size();
            violationTypesTableModel.addRow(new Object[]{
                violationType.getViolationTypeName(),
                countViolation.toString()
            });
        }
    }

    /**
     * @return Индекс выбранной строки в таблице типов нарушений.
     */
    public int getSelectedRow() {
        return violationTypesTable.getSelectedRow();
    }

    /**
     * @return Имя типа нарушения из выбранной строки таблицы или null, если строка не выбрана.
     */
    public String getSelectedViolationTypeName() {
        int selectedRow = violationTypesTable.getSelectedRow();
        if (selectedRow != -1) {
            return (String) violationTypesTableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }
}
