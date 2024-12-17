package controllers;

import ui.ReportsPanel;
import services.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Контроллер для работы с отчетами.
 * Управляет взаимодействием между панелью отчетов и сервисом отчетов.
 * Содержит логику обработки событий, таких как отображение отчетов и генерация PDF-файлов.
 */
public class ReportsController {
    private ReportsPanel reportsPanel;
    private ReportService reportService;

    /**
     * Конструктор контроллера отчетов.
     * Инициализирует панель отчетов и сервис отчетов, а также настраивает обработчики событий.
     *
     * @param reportsPanel панель для отображения отчетов
     * @param reportService сервис для генерации и получения данных отчетов
     */
    public ReportsController(ReportsPanel reportsPanel, ReportService reportService) {
        this.reportsPanel = reportsPanel;
        this.reportService = reportService;

        initEventHandlers(); // Инициализация обработчиков событий
    }

    /**
     * Инициализирует обработчики событий для кнопок на панели отчетов.
     * Обработчики включают действия для отображения отчетов и генерации PDF.
     */
    private void initEventHandlers() {
        // Обработчик события для кнопки отображения отчета
        reportsPanel.getShowReportButton().addActionListener(e -> {
            String startDateText = reportsPanel.getStartDateField().getText();
            String endDateText = reportsPanel.getEndDateField().getText();
            StringBuilder errors = new StringBuilder();

            // Проверка корректности введенных дат
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
                    throw new Exception(errors.toString()); // Выводим ошибку, если есть проблемы с датами
                }

                // Преобразуем строки в LocalDate и вызываем метод для отображения отчета
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(startDateText, formatter);
                LocalDate endDate = LocalDate.parse(endDateText, formatter);

                showReport(startDate, endDate);
            } catch (Exception ex) {
                // Отображаем сообщение об ошибке
                JOptionPane.showMessageDialog(reportsPanel, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик события для кнопки генерации PDF отчета
        reportsPanel.getGeneratePdfButton().addActionListener(e -> {
            String startDateText = reportsPanel.getStartDateField().getText();
            String endDateText = reportsPanel.getEndDateField().getText();

            // Генерация PDF отчета
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(startDateText, formatter);
                LocalDate endDate = LocalDate.parse(endDateText, formatter);

                // Генерация отчета в формате PDF
                reportService.generatePdfReport(startDate, endDate);

                // Сообщение об успешной генерации
                JOptionPane.showMessageDialog(reportsPanel, "Создание отчёта успешно завершено.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // Отображаем сообщение об ошибке
                JOptionPane.showMessageDialog(reportsPanel, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    /**
     * Отображает отчет в таблице на панели отчетов.
     * Заполняет таблицу данными, полученными от сервиса отчетов.
     *
     * @param startDate дата начала периода
     * @param endDate дата конца периода
     */
    private void showReport(LocalDate startDate, LocalDate endDate) {
        // Включаем кнопку для генерации PDF отчета
        reportsPanel.getGeneratePdfButton().setEnabled(true);

        // Получаем данные для отчета из сервиса
        List<Object[]> data = reportService.getReportData(startDate, endDate);

        // Получаем модель таблицы отчетов
        DefaultTableModel tableModel = reportsPanel.getReportsTableModel();
        tableModel.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

        // Добавляем строки данных в таблицу
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
}
