package utils;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Класс для генерации отчётов в формате PDF с использованием JasperReports.
 * Генерирует отчёт на основе XML данных и сохраняет его в файл PDF.
 */
public class ReportManager {
    private static final Logger logger = Logger.getLogger(ReportManager.class);

    /**
     * Генерирует отчёт по нарушениям в формате PDF.
     * Использует файл XML в качестве источника данных для отчёта.
     *
     * @param xmlFilePath путь к XML-файлу с данными для отчёта.
     * @param outputPath путь, куда будет сохранён сгенерированный отчёт в формате PDF.
     */
    public static void generateViolationReport(String xmlFilePath, String outputPath) {
        logger.info("Старт генерации отчёта в PDF");

        try {
            // Создание карты для параметров отчёта (пока пустая)
            Map<String, Object> parameters = new HashMap<>();

            // Компиляция JRXML файла в объект JasperReport
            JasperReport jasperReport = JasperCompileManager.compileReport("ViolationReport.jrxml");

            // Создание источника данных из XML
            JRXmlDataSource xmlDataSource = new JRXmlDataSource(xmlFilePath, "/report/violations/violation");

            // Заполнение отчёта данными
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, xmlDataSource);

            // Экспорт отчёта в PDF файл
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            logger.info("Отчёт успешно сохранён в файл: " + outputPath);
        } catch (Exception e) {
            // Логирование ошибки, если генерация отчёта не удалась
            logger.error("Ошибка во время генерации", e);
        }
    }
}
