package controllers;

import ui.ViolationTypesPanel;
import validators.ViolationTypeValidator;
import services.ViolationTypeService;
import entities.ViolationType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import application.Application;

import java.awt.*;

/**
 * Контроллер для управления типами нарушений в приложении.
 * Отвечает за обработку действий пользователя в интерфейсе и взаимодействие с сервисами.
 */
public class ViolationTypeController {
    private Application application;
    private ViolationTypesPanel violationTypesPanel;
    private ViolationTypeService violationTypeService;
    private JFrame parentWindow;
    private DefaultTableModel violationTypesTableModel;

    /**
     * Конструктор контроллера для типов нарушений.
     *
     * @param application          экземпляр приложения
     * @param parentWindow         родительское окно для диалоговых окон
     * @param violationTypesPanel панель с таблицей типов нарушений
     * @param violationTypeService сервис для работы с типами нарушений
     * @param violationTypesTableModel модель таблицы типов нарушений
     */
    public ViolationTypeController(Application application, JFrame parentWindow, ViolationTypesPanel violationTypesPanel, ViolationTypeService violationTypeService, DefaultTableModel violationTypesTableModel) {
        this.application = application;
        this.parentWindow = parentWindow;
        this.violationTypesPanel = violationTypesPanel;
        this.violationTypeService = violationTypeService;
        this.violationTypesTableModel = violationTypesTableModel;

        initEventHandlers();
    }

    /**
     * Инициализация обработчиков событий для панели типов нарушений.
     * Связывает действия с методами контроллера.
     */
    private void initEventHandlers() {
        violationTypesPanel.setAddViolationTypeAction(e -> openAddViolationTypeWindow());
        violationTypesPanel.setEditViolationTypeAction(e -> openEditViolationTypeWindow());
        violationTypesPanel.setDeleteViolationTypeAction(e -> deleteViolationType());
        violationTypesPanel.setSearchViolationTypeAction(e -> openSearchViolationTypeWindow());
        violationTypesPanel.setResetFiltersAction(e -> violationTypesPanel.updateViolationTypeData(violationTypeService.getAllViolationTypes()));
    }

    /**
     * Обновляет данные типов нарушений на панели.
     * Получает все типы нарушений из сервиса и отображает их.
     */
    public void updateViolationTypeData() {
        violationTypesPanel.updateViolationTypeData(violationTypeService.getAllViolationTypes());
    }

    /**
     * Открывает окно для добавления нового типа нарушения.
     */
    public void openAddViolationTypeWindow() {
        JDialog addTypeDialog = new JDialog(parentWindow, "Добавить тип нарушения", true);
        addTypeDialog.setSize(400, 150);
        addTypeDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        JTextField nameField = new JTextField();

        inputPanel.add(new JLabel("Название типа:"));
        inputPanel.add(nameField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        addTypeDialog.add(inputPanel, BorderLayout.CENTER);
        addTypeDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик добавления типа нарушения
        addButton.addActionListener(e -> {
            try {
                ViolationType violationType = new ViolationType();
                violationType.setViolationTypeName(nameField.getText());

                ViolationTypeValidator.validateViolationType(violationType, violationTypeService.getEntityManager(), " ");

                violationTypeService.addViolationType(violationType);
                updateViolationTypeData();
                addTypeDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addTypeDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик отмены добавления
        cancelButton.addActionListener(e -> addTypeDialog.dispose());

        addTypeDialog.setLocationRelativeTo(parentWindow);
        addTypeDialog.setVisible(true);
    }

    /**
     * Открывает окно для редактирования типа нарушения.
     * В случае отсутствия выбранного типа нарушения отображается ошибка.
     */
    private void openEditViolationTypeWindow() {
        String selectedViolationTypeName = violationTypesPanel.getSelectedViolationTypeName();

        if (selectedViolationTypeName == null) {
            JOptionPane.showMessageDialog(parentWindow, "Выберите тип нарушения для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ViolationType violationType = violationTypeService.getViolationTypeByName(selectedViolationTypeName);
        if (violationType == null) {
            JOptionPane.showMessageDialog(parentWindow, "Тип нарушения с таким названием не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog editTypeDialog = new JDialog(parentWindow, "Изменить тип нарушения", true);
        editTypeDialog.setSize(400, 150);
        editTypeDialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        JTextField nameField = new JTextField(violationType.getViolationTypeName());
        String oldName = violationType.getViolationTypeName();

        inputPanel.add(new JLabel("Название типа:"));
        inputPanel.add(nameField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editTypeDialog.add(inputPanel, BorderLayout.CENTER);
        editTypeDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Обработчик сохранения изменений типа нарушения
        saveButton.addActionListener(e -> {
            try {
                ViolationType tempType = new ViolationType();
                tempType.setViolationTypeName(nameField.getText());

                ViolationTypeValidator.validateViolationType(tempType, violationTypeService.getEntityManager(), oldName);

                violationType.setViolationTypeName(nameField.getText());

                violationTypeService.updateViolationType(violationType);
                application.updateAllTables();
                editTypeDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editTypeDialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик отмены редактирования
        cancelButton.addActionListener(e -> editTypeDialog.dispose());

        editTypeDialog.setLocationRelativeTo(parentWindow);
        editTypeDialog.setVisible(true);
    }

    /**
     * Удаляет выбранный тип нарушения.
     * В случае отсутствия выбранного типа нарушения отображается ошибка.
     */
    private void deleteViolationType() {
        String selectedViolationTypeName = violationTypesPanel.getSelectedViolationTypeName();

        if (selectedViolationTypeName == null) {
            JOptionPane.showMessageDialog(parentWindow, "Выберите тип нарушения для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                parentWindow,
                "Вы уверены, что хотите удалить выбранный тип нарушения?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                violationTypeService.deleteViolationType(selectedViolationTypeName);
                application.updateAllTables();
                JOptionPane.showMessageDialog(parentWindow, "Тип нарушения успешно удален.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parentWindow, "Ошибка при удалении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Открывает окно для поиска типов нарушений.
     * Пользователь может задать фильтры по названию типа нарушения.
     */
    private void openSearchViolationTypeWindow() {
        JPanel searchPanel = new JPanel(new GridLayout(2, 2));

        JTextField typeNameField = new JTextField();
        searchPanel.add(new JLabel("Название типа:"));
        searchPanel.add(typeNameField);

        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("Поиск");
        JButton resetButton = new JButton("Сбросить фильтры");

        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);
        searchPanel.add(buttonPanel);

        // Обработчик поиска типов нарушений
        searchButton.addActionListener(e -> {
            violationTypesTableModel.setRowCount(0);

            String typeName = typeNameField.getText();
            violationTypesPanel.updateViolationTypeData(violationTypeService.searchViolationTypes(typeName));
        });

        // Обработчик сброса фильтров
        resetButton.addActionListener(e -> {
            typeNameField.setText("");
            updateViolationTypeData();
        });

        Object[] options = {searchButton, resetButton};
        int result = JOptionPane.showOptionDialog(null, searchPanel, "Поиск типов нарушений",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (result == 0) {
            searchButton.doClick();
        } else if (result == 1) {
            resetButton.doClick();
        }
    }
}
