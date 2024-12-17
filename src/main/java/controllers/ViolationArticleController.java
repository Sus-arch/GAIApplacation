package controllers;

import ui.ViolationArticlesPanel;
import validators.ViolationArticleValidator;
import services.ViolationArticleService;
import entities.ViolationArticle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import application.Application;

import java.awt.*;

/**
 * Контроллер для управления статьями нарушений.
 * Обрабатывает действия пользователя, такие как добавление, редактирование, удаление и поиск статей нарушений.
 */
public class ViolationArticleController {
    private Application application;
    private ViolationArticlesPanel violationArticlesPanel;
    private ViolationArticleService violationArticleService;
    private JFrame parentWindow;
    private DefaultTableModel violationArticlesTableModel;
    
    /**
     * Конструктор для инициализации контроллера.
     * 
     * @param application           Приложение, с которым работает контроллер.
     * @param parentWindow         Окно родителя для диалоговых окон.
     * @param violationArticlesPanel Панель, содержащая элементы управления для работы со статьями нарушений.
     * @param violationArticleService Сервис для работы с данными статей нарушений.
     * @param violationArticlesTableModel Модель таблицы для отображения статей нарушений.
     */
    public ViolationArticleController(Application application, JFrame parentWindow, ViolationArticlesPanel violationArticlesPanel, ViolationArticleService violationArticleService, DefaultTableModel violationArticlesTableModel) {
        this.application = application;
        this.parentWindow = parentWindow;
        this.violationArticlesPanel = violationArticlesPanel;
        this.violationArticleService = violationArticleService;
        this.violationArticlesTableModel = violationArticlesTableModel;
        
        initEventHandlers();  // Инициализация обработчиков событий
    }
    
    /**
     * Инициализирует обработчики событий для элементов управления на панели статей нарушений.
     */
    private void initEventHandlers() {
        // Обработчик для кнопки "Добавить статью"
        violationArticlesPanel.setAddViolationArticleAction(e -> openAddViolationArticleWindow());
        
        // Обработчик для кнопки "Редактировать статью"
        violationArticlesPanel.setEditViolationArticleAction(e -> openEditViolationArticleWindow());
        
        // Обработчик для кнопки "Удалить статью"
        violationArticlesPanel.setDeleteViolationArticleAction(e -> deleteViolationArticle());
        
        // Обработчик для кнопки "Поиск статьи"
        violationArticlesPanel.setSearchViolationArticleAction(e -> openSearchViolationArticleWindow());
        
        // Обработчик для кнопки "Сбросить фильтры"
        violationArticlesPanel.setResetFiltersAction(e -> violationArticlesPanel.updateViolationArticleData(violationArticleService.getAllViolationArticles()));
    }
    
    /**
     * Обновляет данные статей нарушений на панели.
     */
    public void updateViolationArticleData() {
        violationArticlesPanel.updateViolationArticleData(violationArticleService.getAllViolationArticles());
    }
    
    /**
     * Открывает окно для добавления новой статьи нарушения.
     */
    public void openAddViolationArticleWindow() {
        JDialog addArticleDialog = new JDialog(parentWindow, "Добавить статью нарушения", true);
        addArticleDialog.setSize(400, 200);
        addArticleDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField articleCodeField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField fineField = new JTextField();
        
        // Добавление полей ввода для кода статьи, описания и штрафа
        inputPanel.add(new JLabel("Код статьи:"));
        inputPanel.add(articleCodeField);
        inputPanel.add(new JLabel("Описание:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Штраф:"));
        inputPanel.add(fineField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        addArticleDialog.add(inputPanel, BorderLayout.CENTER);
        addArticleDialog.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                ViolationArticle violationArticle = new ViolationArticle();
                violationArticle.setViolationArticleCode(articleCodeField.getText());
                violationArticle.setViolationArticleDescription(descriptionField.getText());
                String fine = fineField.getText();
                if (fine.matches("\\d+")) {
                    violationArticle.setViolationArticleFine(Integer.parseInt(fine));
                } else {
                    violationArticle.setViolationArticleFine(null);
                }
                
                // Валидация данных статьи нарушения
                ViolationArticleValidator.validateViolationArticle(violationArticle, violationArticleService.getEntityManager(), " ");
                
                violationArticleService.addViolationArticle(violationArticle);
                updateViolationArticleData();  // Обновление данных на панели
                addArticleDialog.dispose();  // Закрытие окна
            } catch (Exception ex) {
                // Обработка ошибок при добавлении статьи
                JOptionPane.showMessageDialog(addArticleDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addArticleDialog.dispose());

        addArticleDialog.setLocationRelativeTo(parentWindow);  // Размещение окна относительно родительского
        addArticleDialog.setVisible(true);  // Отображение окна
    }
    
    /**
     * Открывает окно для редактирования существующей статьи нарушения.
     */
    private void openEditViolationArticleWindow() {
        String selectedViolationArticleCode = violationArticlesPanel.getSelectedViolationArticleCode();

        if (selectedViolationArticleCode == null) {
            JOptionPane.showMessageDialog(parentWindow, "Выберите статью для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ViolationArticle violationArticle = violationArticleService.getViolationArticleByCode(selectedViolationArticleCode);
        if (violationArticle == null) {
            JOptionPane.showMessageDialog(parentWindow, "Статья нарушения с таким кодом не найдена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog editArticleDialog = new JDialog(parentWindow, "Изменить статью нарушения", true);
        editArticleDialog.setSize(400, 200);
        editArticleDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField articleCodeField = new JTextField(violationArticle.getViolationArticleCode());
        JTextField descriptionField = new JTextField(violationArticle.getViolationArticleDescription());
        JTextField fineField = new JTextField(violationArticle.getViolationArticleFine().toString());
        String oldCode = violationArticle.getViolationArticleCode();
        
        // Заполнение полей с текущими данными статьи
        inputPanel.add(new JLabel("Код статьи:"));
        inputPanel.add(articleCodeField);
        inputPanel.add(new JLabel("Описание:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Штраф:"));
        inputPanel.add(fineField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editArticleDialog.add(inputPanel, BorderLayout.CENTER);
        editArticleDialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                ViolationArticle tempViolationArticle = new ViolationArticle();
                tempViolationArticle.setViolationArticleCode(articleCodeField.getText());
                tempViolationArticle.setViolationArticleDescription(descriptionField.getText());
                String fine = fineField.getText();
                if (fine.matches("\\d+")) {
                    tempViolationArticle.setViolationArticleFine(Integer.parseInt(fine));
                } else {
                    tempViolationArticle.setViolationArticleFine(null);
                }
                
                // Валидация изменений статьи
                ViolationArticleValidator.validateViolationArticle(tempViolationArticle, violationArticleService.getEntityManager(), oldCode);
                
                violationArticle.setViolationArticleCode(articleCodeField.getText());
                violationArticle.setViolationArticleDescription(descriptionField.getText());
                violationArticle.setViolationArticleFine(Integer.parseInt(fineField.getText()));
                
                violationArticleService.updateViolationArticle(violationArticle);
                application.updateAllTables();  // Обновление всех таблиц в приложении
                editArticleDialog.dispose();  // Закрытие окна
            } catch (Exception ex) {
                // Обработка ошибок при сохранении изменений
                JOptionPane.showMessageDialog(editArticleDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> editArticleDialog.dispose());
        
        editArticleDialog.setLocationRelativeTo(parentWindow);  // Размещение окна относительно родительского
        editArticleDialog.setVisible(true);  // Отображение окна
    }
	
    /**
     * Метод для удаления выбранной статьи нарушения.
     * Ожидает, что будет выбрана статья нарушения для удаления.
     * При удалении запрашивается подтверждение пользователя.
     */
    private void deleteViolationArticle() {
        // Получаем код выбранной статьи нарушения
        String selectedViolationArticleCode = violationArticlesPanel.getSelectedViolationArticleCode();
        
        // Если статья не выбрана, показываем ошибку
        if (selectedViolationArticleCode == null) {
            JOptionPane.showMessageDialog(parentWindow, "Выберите статью нарушения для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Подтверждение удаления
        int confirm = JOptionPane.showConfirmDialog(
                parentWindow,
                "Вы уверены, что хотите удалить выбранную статью нарушения?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

        // Если пользователь подтвердил удаление
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Удаляем статью нарушения из базы данных
                violationArticleService.deleteViolationArticle(selectedViolationArticleCode);
                // Обновляем таблицу
                application.updateAllTables();
                JOptionPane.showMessageDialog(parentWindow, "Статья нарушения успешно удалена.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                // Если произошла ошибка при удалении
                JOptionPane.showMessageDialog(parentWindow, "Ошибка при удалении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Метод для открытия окна поиска статей нарушений.
     * В окне можно ввести критерии поиска, такие как код статьи, описание и диапазон штрафов.
     */
    private void openSearchViolationArticleWindow() {
        // Создание панели для ввода данных поиска
        JPanel searchPanel = new JPanel(new GridLayout(5, 2));

        // Поля для ввода критериев поиска
        JTextField articleCodeField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField fineFromField = new JTextField();
        JTextField fineToField = new JTextField();

        // Добавляем компоненты на панель
        searchPanel.add(new JLabel("Код статьи:"));
        searchPanel.add(articleCodeField);
        searchPanel.add(new JLabel("Описание:"));
        searchPanel.add(descriptionField);
        searchPanel.add(new JLabel("Штраф (от):"));
        searchPanel.add(fineFromField);
        searchPanel.add(new JLabel("Штраф (до):"));
        searchPanel.add(fineToField);

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("Поиск");
        JButton resetButton = new JButton("Сбросить фильтры");
        
        // Добавляем кнопки на панель
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);
        searchPanel.add(buttonPanel);

        // Действие при нажатии кнопки поиска
        searchButton.addActionListener(e -> {
            // Очищаем таблицу перед загрузкой новых данных
            violationArticlesTableModel.setRowCount(0);

            // Получаем данные из полей ввода
            String articleCode = articleCodeField.getText();
            String description = descriptionField.getText();
            String fineFrom = fineFromField.getText();
            String fineTo = fineToField.getText();
            
            // Загружаем и обновляем данные таблицы по заданным критериям
            violationArticlesPanel.updateViolationArticleData(violationArticleService.searchViolationArticles(articleCode, description, fineFrom, fineTo));
        });

        // Действие при нажатии кнопки сброса фильтров
        resetButton.addActionListener(e -> {
            // Очищаем поля ввода и обновляем таблицу
            articleCodeField.setText("");
            descriptionField.setText("");
            fineFromField.setText("");
            fineToField.setText("");
            updateViolationArticleData();
        });

        // Открываем диалог с панелью поиска
        Object[] options = {searchButton, resetButton};
        int result = JOptionPane.showOptionDialog(null, searchPanel, "Поиск статей нарушений",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        // В зависимости от выбранной кнопки выполняем поиск или сброс
        if (result == 0) {
            searchButton.doClick();
        } else if (result == 1) {
            resetButton.doClick();
        }
    }

}
