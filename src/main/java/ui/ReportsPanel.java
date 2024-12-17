package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Панель для отображения и генерации отчётов по нарушениям.
 * Содержит элементы для ввода диапазона дат, таблицу с данными отчётов
 * и кнопки для управления отображением и сохранением отчётов.
 */
public class ReportsPanel extends JPanel {
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton showReportButton;
    private JButton generatePdfButton;
    private DefaultTableModel reportsTableModel;
    private JTable reportsTable;

    /**
     * Конструктор панели отчётов. Инициализирует все элементы интерфейса.
     */
    public ReportsPanel() {
        setLayout(new BorderLayout());

        // Заголовок панели
        JLabel titleLabel = new JLabel("Отчёты по нарушениям", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Панель для ввода диапазона дат и кнопок управления
        JPanel datePanel = new JPanel(new FlowLayout());

        JLabel startDateLabel = new JLabel("Начало периода (ГГГГ-ММ-ДД):");
        startDateField = new JTextField(10);
        JLabel endDateLabel = new JLabel("Конец периода (ГГГГ-ММ-ДД):");
        endDateField = new JTextField(10);

        showReportButton = new JButton("Показать отчёт");
        generatePdfButton = new JButton("Сохранить отчёт в PDF");
        generatePdfButton.setEnabled(false); // Кнопка отключена по умолчанию

        datePanel.add(startDateLabel);
        datePanel.add(startDateField);
        datePanel.add(endDateLabel);
        datePanel.add(endDateField);
        datePanel.add(showReportButton);
        datePanel.add(generatePdfButton);

        add(datePanel, BorderLayout.NORTH);

        // Инициализация таблицы для отображения отчётов
        String[] columnNames = {"Дата", "Водитель", "Автомобиль", "Нарушение"};
        reportsTableModel = new DefaultTableModel(columnNames, 0);
        reportsTable = new JTable(reportsTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещено редактирование ячеек таблицы
            }
        };
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Одиночный выбор строки
        reportsTable.getTableHeader().setReorderingAllowed(false); // Отключение перестановки колонок

        JScrollPane scrollPane = new JScrollPane(reportsTable); // Прокрутка для таблицы
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Получить текстовое поле для ввода начальной даты.
     * @return Поле ввода начальной даты.
     */
    public JTextField getStartDateField() {
        return startDateField;
    }

    /**
     * Получить текстовое поле для ввода конечной даты.
     * @return Поле ввода конечной даты.
     */
    public JTextField getEndDateField() {
        return endDateField;
    }

    /**
     * Получить кнопку для отображения отчёта.
     * @return Кнопка "Показать отчёт".
     */
    public JButton getShowReportButton() {
        return showReportButton;
    }

    /**
     * Получить кнопку для сохранения отчёта в PDF.
     * @return Кнопка "Сохранить отчёт в PDF".
     */
    public JButton getGeneratePdfButton() {
        return generatePdfButton;
    }

    /**
     * Получить модель данных таблицы отчётов.
     * @return Модель данных {@link DefaultTableModel}.
     */
    public DefaultTableModel getReportsTableModel() {
        return reportsTableModel;
    }

    /**
     * Получить таблицу для отображения отчётов.
     * @return Таблица отчётов {@link JTable}.
     */
    public JTable getReportsTable() {
        return reportsTable;
    }
}
