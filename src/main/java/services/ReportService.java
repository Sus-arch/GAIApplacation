package services;

import entities.Driver;
import entities.Violation;
import utils.ReportManager;
import utils.XMLManager;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для генерации отчетов по нарушениям.
 * Включает методы для получения данных для отчета и создания PDF-отчета.
 */
public class ReportService {
    private EntityManager entityManager;

    /**
     * Конструктор, инициализирует объект ReportService с переданным EntityManager.
     *
     * @param entityManager Менеджер сущностей для работы с базой данных.
     */
    public ReportService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Получает данные для отчета о нарушениях в указанный период времени.
     * Данные включают информацию о нарушении, водителе, автомобиле и типе нарушения.
     *
     * @param startDate Дата начала периода для отчета.
     * @param endDate   Дата окончания периода для отчета.
     * @return Список массивов объектов, представляющих данные для отчета.
     */
    public List<Object[]> getReportData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> data = new ArrayList<>();

        // Запрос к базе данных для получения нарушений в заданном периоде
        TypedQuery<Violation> query = entityManager.createQuery(
                "SELECT v FROM Violation v WHERE v.violationDate BETWEEN :startDate AND :endDate", Violation.class
        );
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        // Для каждого нарушения собираем данные о водителе, автомобиле и типе нарушения
        for (Violation violation : query.getResultList()) {
            Driver driver = violation.getCar().getOwner();
            data.add(new Object[]{
                    violation.getViolationDate(),  // Дата нарушения
                    driver.getFullName(),  // ФИО водителя
                    violation.getCar().getBrand() + " " + violation.getCar().getModel() + " (" + violation.getCar().getLicensePlate() + ")",  // Автомобиль (марка, модель, номер)
                    violation.getViolationType().getViolationTypeName()  // Тип нарушения
            });
        }
        return data;  // Возвращаем данные для отчета
    }

    /**
     * Генерирует PDF-отчет по нарушениям за указанный период времени.
     * Для этого создается XML-отчет, который затем используется для генерации PDF-отчета.
     *
     * @param startDate Дата начала периода для отчета.
     * @param endDate   Дата окончания периода для отчета.
     * @throws RuntimeException Если произошла ошибка при генерации отчета.
     */
    public void generatePdfReport(LocalDate startDate, LocalDate endDate) {
        try {
            // Создание XML-файла с данными для отчета
            XMLManager.createReportXML("report.xml", getReportData(startDate, endDate), startDate.toString(), endDate.toString());

            // Генерация PDF-отчета из XML-файла
            ReportManager.generateViolationReport("report.xml", "Report.pdf");
        } catch (Exception e) {
            // Обработка ошибок и выбрасывание исключения с сообщением об ошибке
            throw new RuntimeException("Ошибка генерации PDF-отчёта: " + e.getMessage(), e);
        }
    }
}
