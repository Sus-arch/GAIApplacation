package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import entities.Violation;
import services.ViolationService;

/**
 * Панель для управления нарушениями.
 * Отображает таблицу с информацией о нарушениях и предоставляет кнопки для выполнения основных операций.
 */
public class ViolationsPanel extends JPanel {
    private JTable violationsTable;
    private DefaultTableModel violationsTableModel;
    private JButton addViolationButton;
    private JButton editViolationButton;
    private JButton deleteViolationButton;
    private JButton searchViolationButton;
    private JButton resetViolationFiltersButton;
    private ViolationService violationService;

    /**
     * Конструктор панели нарушений.
     * @param violationService объект сервиса для работы с данными о нарушениях.
     */
    public ViolationsPanel(ViolationService violationService) {
        this.violationService = violationService;
        setLayout(new BorderLayout());

        // Создание и добавление заголовка панели.
        JLabel titleLabel = new JLabel("Управление нарушениями", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Инициализация таблицы с колонками.
        String[] columns = {"Номер постановления", "Статья нарушения", "Тип нарушения", "Гос. номер", 
                            "Нарушитель", "Дата нарушения", "Оплачено"};
        violationsTableModel = new DefaultTableModel(columns, 0);
        violationsTable = new JTable(violationsTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование ячеек таблицы.
            }
        };

        // Устанавливаем выбор только одной строки и запрет на перетаскивание заголовков.
        violationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        violationsTable.getTableHeader().setReorderingAllowed(false);

        // Добавление таблицы в скролл-панель.
        add(new JScrollPane(violationsTable), BorderLayout.CENTER);

        // Инициализация панели управления кнопками.
        initControlPanel();

        // Заполнение таблицы данными.
        updateViolationData(violationService.getAllViolations());
    }

    /**
     * Инициализирует панель управления с кнопками.
     */
    private void initControlPanel() {
        JPanel controlPanel = new JPanel();

        // Создание кнопок для основных операций.
        addViolationButton = new JButton("Добавить");
        editViolationButton = new JButton("Изменить");
        deleteViolationButton = new JButton("Удалить");
        searchViolationButton = new JButton("Поиск");
        resetViolationFiltersButton = new JButton("Сбросить фильтры");

        // Добавление кнопок в панель управления.
        controlPanel.add(addViolationButton);
        controlPanel.add(editViolationButton);
        controlPanel.add(deleteViolationButton);
        controlPanel.add(searchViolationButton);
        controlPanel.add(resetViolationFiltersButton);

        // Добавление панели управления в нижнюю часть панели.
        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Возвращает модель таблицы нарушений.
     * @return объект {@link DefaultTableModel}, связанный с таблицей нарушений.
     */
    public DefaultTableModel getViolationsTableModel() {
        return violationsTableModel;
    }

    /**
     * Устанавливает действие для кнопки "Добавить".
     * @param action слушатель события для кнопки.
     */
    public void setAddViolationAction(ActionListener action) {
        addViolationButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Изменить".
     * @param action слушатель события для кнопки.
     */
    public void setEditViolationAction(ActionListener action) {
        editViolationButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Удалить".
     * @param action слушатель события для кнопки.
     */
    public void setDeleteViolationAction(ActionListener action) {
        deleteViolationButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Поиск".
     * @param action слушатель события для кнопки.
     */
    public void setSearchViolationAction(ActionListener action) {
        searchViolationButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки "Сбросить фильтры".
     * @param action слушатель события для кнопки.
     */
    public void setResetFiltersAction(ActionListener action) {
        resetViolationFiltersButton.addActionListener(action);
    }

    /**
     * Обновляет данные в таблице нарушений.
     * @param violations список нарушений для отображения в таблице.
     */
    public void updateViolationData(List<Violation> violations) {
        violationsTableModel.setRowCount(0); // Очистка текущих данных таблицы.
        for (Violation violation : violations) {
            violationsTableModel.addRow(new Object[] {
                violation.getViolationResolution(),
                violation.getViolationArticle().getViolationArticleCode(),
                violation.getViolationType().getViolationTypeName(),
                violation.getCar().getLicensePlate(),
                violation.getCar().getOwner().getFullName(),
                violation.getViolationDate().toString(),
                violation.getViolationPaid() ? "Да" : "Нет"
            });
        }
    }

    /**
     * Возвращает индекс выбранной строки в таблице.
     * @return индекс выбранной строки или -1, если строка не выбрана.
     */
    public int getSelectedRow() {
        return violationsTable.getSelectedRow();
    }

    /**
     * Возвращает номер постановления для выбранной строки.
     * @return номер постановления или {@code null}, если строка не выбрана.
     */
    public String getSelectedViolationResolution() {
        int selectedRow = violationsTable.getSelectedRow();
        if (selectedRow != -1) {
            return (String) violationsTableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }
}
