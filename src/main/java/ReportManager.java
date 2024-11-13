import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import java.util.HashMap;
import java.util.Map;

public class ReportManager {

	public static void generateViolationReport(String xmlFilePath, String outputPath) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("net.sf.jasperreports.fonts.DejaVuSans", "DejaVuSans.ttf");

            JasperReport jasperReport = JasperCompileManager.compileReport("ViolationReport.jrxml");
            JRXmlDataSource xmlDataSource = new JRXmlDataSource(xmlFilePath, "/report/violations/violation");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, xmlDataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}