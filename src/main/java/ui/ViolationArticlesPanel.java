package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;

import entities.ViolationArticle;
import services.ViolationArticleService;

/**
 * Панель управления статьями нарушений.
 * Предоставляет функционал для отображения, добавления, изменения,
 * удаления и поиска статей нарушений.
 */
public class ViolationArticlesPanel extends JPanel {
    private JTable violationArticleTable;
    private DefaultTableModel violationArticlesTableModel;
    private JButton addViolationArticleButton;
    private JButton editViolationArticleButton;
    private JButton deleteViolationArticleButton;
    private JButton searchViolationArticleButton;
    private JButton resetViolationArticleFiltersButton;
    private ViolationArticleService violationArticleService;

    /**
     * Конструктор панели управления статьями нарушений.
     * Инициализирует таблицу, кнопки управления и загружает данные из сервиса.
     *
     * @param violationArticleService Сервис для работы с данными статей нарушений.
     */
    public ViolationArticlesPanel(ViolationArticleService violationArticleService) {
        this.violationArticleService = violationArticleService;
        setLayout(new BorderLayout());

        // Заголовок панели
        JLabel titleLabel = new JLabel("Управление статьями нарушений", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Инициализация таблицы
        String[] columns = {"Статья нарушения", "Описание статьи", "Размер штрафа"};
        violationArticlesTableModel = new DefaultTableModel(columns, 0);
        violationArticleTable = new JTable(violationArticlesTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещено редактирование ячеек
            }
            @Override
            public String getToolTipText(MouseEvent event) {
                Point p = event.getPoint();
                int rowIndex = rowAtPoint(p);
                int columnIndex = columnAtPoint(p);
                
                if (rowIndex >= 0 && rowIndex < getRowCount() && columnIndex == 1) {
                    return (String) getValueAt(rowIndex, columnIndex); // Второй столбец
                }
                
                return super.getToolTipText(event); // Вывод полной строки описания статьи
            }
        };

        violationArticleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Одиночный выбор строки
        violationArticleTable.getTableHeader().setReorderingAllowed(false); // Отключение перестановки колонок
        add(new JScrollPane(violationArticleTable), BorderLayout.CENTER);

        // Инициализация панели управления
        initControlPanel();

        // Загрузка данных в таблицу
        updateViolationArticleData(violationArticleService.getAllViolationArticles());
    }

    /**
     * Инициализирует панель управления с кнопками.
     */
    private void initControlPanel() {
        JPanel controlPanel = new JPanel();

        addViolationArticleButton = new JButton("Добавить");
        editViolationArticleButton = new JButton("Изменить");
        deleteViolationArticleButton = new JButton("Удалить");
        searchViolationArticleButton = new JButton("Поиск");
        resetViolationArticleFiltersButton = new JButton("Сбросить фильтры");

        controlPanel.add(addViolationArticleButton);
        controlPanel.add(editViolationArticleButton);
        controlPanel.add(deleteViolationArticleButton);
        controlPanel.add(searchViolationArticleButton);
        controlPanel.add(resetViolationArticleFiltersButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Возвращает модель данных таблицы статей нарушений.
     *
     * @return Модель данных {@link DefaultTableModel}.
     */
    public DefaultTableModel getViolationArticlesDefaultTableModel() {
        return violationArticlesTableModel;
    }

    /**
     * Устанавливает действие для кнопки добавления статьи.
     *
     * @param action Слушатель события.
     */
    public void setAddViolationArticleAction(ActionListener action) {
        addViolationArticleButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки изменения статьи.
     *
     * @param action Слушатель события.
     */
    public void setEditViolationArticleAction(ActionListener action) {
        editViolationArticleButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки удаления статьи.
     *
     * @param action Слушатель события.
     */
    public void setDeleteViolationArticleAction(ActionListener action) {
        deleteViolationArticleButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки поиска статьи.
     *
     * @param action Слушатель события.
     */
    public void setSearchViolationArticleAction(ActionListener action) {
        searchViolationArticleButton.addActionListener(action);
    }

    /**
     * Устанавливает действие для кнопки сброса фильтров.
     *
     * @param action Слушатель события.
     */
    public void setResetFiltersAction(ActionListener action) {
        resetViolationArticleFiltersButton.addActionListener(action);
    }

    /**
     * Обновляет данные в таблице на основе переданного списка статей нарушений.
     *
     * @param violationArticles Список статей нарушений.
     */
    public void updateViolationArticleData(List<ViolationArticle> violationArticles) {
        violationArticlesTableModel.setRowCount(0); // Очистка таблицы
        for (ViolationArticle violationArticle : violationArticles) {
            violationArticlesTableModel.addRow(new Object[]{
                violationArticle.getViolationArticleCode(),
                violationArticle.getViolationArticleDescription(),
                violationArticle.getViolationArticleFine().toString()
            });
        }
    }

    /**
     * Возвращает индекс выбранной строки в таблице.
     *
     * @return Индекс выбранной строки или -1, если строка не выбрана.
     */
    public int getSelectedRow() {
        return violationArticleTable.getSelectedRow();
    }

    /**
     * Возвращает код выбранной статьи нарушения.
     *
     * @return Код статьи нарушения или {@code null}, если строка не выбрана.
     */
    public String getSelectedViolationArticleCode() {
        int selectedRow = violationArticleTable.getSelectedRow();
        if (selectedRow != -1) {
            return (String) violationArticlesTableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }
}
