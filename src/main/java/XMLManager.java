import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class XMLManager {

    public static void exportDataToXML(String filePath, EntityManager em) {
        try {
            Document doc = createDocument();
            Element rootElement = createRootElement(doc, "data");

            appendDateTimeElement(doc, rootElement);
            appendDrivers(doc, rootElement, em);
            appendCars(doc, rootElement, em);
            appendViolations(doc, rootElement, em);
            appendViolationArticles(doc, rootElement, em);
            appendViolationTypes(doc, rootElement, em);

            writeDocumentToFile(doc, filePath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void createReportXML(String filePath, List<Object[]> data, String startDate, String endDate) {
    	try {
    		Document doc = createDocument();
            Element rootElement = createRootElement(doc, "report");
            
            appendDateTimeElement(doc, rootElement);
            Element startDateElement = doc.createElement("startDate");
            startDateElement.appendChild(doc.createTextNode(startDate));
            rootElement.appendChild(startDateElement);
            
            Element endDateElement = doc.createElement("endDate");
            endDateElement.appendChild(doc.createTextNode(endDate));
            rootElement.appendChild(endDateElement);
            
            appendReport(doc, rootElement, data);
            
            writeDocumentToFile(doc, filePath);
    	} catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Document createDocument() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.newDocument();
    }

    private static Element createRootElement(Document doc, String name) {
        Element rootElement = doc.createElement(name);
        doc.appendChild(rootElement);
        return rootElement;
    }

    private static void appendDateTimeElement(Document doc, Element rootElement) {
        Element exportDateTime = doc.createElement("exportDateTime");
        String currentDateTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        exportDateTime.appendChild(doc.createTextNode(currentDateTime));
        rootElement.appendChild(exportDateTime);
    }
    
    private static void appendReport(Document doc, Element rootElement, List<Object[]> data) {
    	Element violationsElement = doc.createElement("violations");
        rootElement.appendChild(violationsElement);
        
        for (Object[] violation : data) {
            Element violationElement = createElementWithChildren(doc, "violation",
                    "date", violation[0].toString(),
                    "driverName", violation[1].toString(),
                    "car", violation[2].toString(),
                    "violationType", violation[3].toString());
            violationsElement.appendChild(violationElement);
        }
    }

    private static void appendDrivers(Document doc, Element rootElement, EntityManager em) {
        Element driversElement = doc.createElement("drivers");
        rootElement.appendChild(driversElement);

        TypedQuery<Driver> query = em.createQuery("SELECT d FROM Driver d", Driver.class);
        for (Driver driver : query.getResultList()) {
            Element driverElement = createElementWithChildren(doc, "driver",
                    "id", driver.getDriverId().toString(),
                    "firstName", driver.getFirstName(),
                    "lastName", driver.getLastName(),
                    "middleName", driver.getMiddleName(),
                    "licenseNumber", driver.getLicenseNumber(),
                    "birthDate", driver.getBirthday().toString(),
                    "city", driver.getCity());
            driversElement.appendChild(driverElement);
        }
    }

    private static void appendCars(Document doc, Element rootElement, EntityManager em) {
        Element carsElement = doc.createElement("cars");
        rootElement.appendChild(carsElement);

        TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c", Car.class);
        for (Car car : query.getResultList()) {
            Element carElement = createElementWithChildren(doc, "car",
                    "id", car.getCarId().toString(),
                    "brand", car.getBrand(),
                    "model", car.getModel(),
                    "vinNumber", car.getVinNumber(),
                    "licensePlate", car.getLicensePlate(),
                    "ownerId", car.getOwner().getDriverId().toString(),
                    "lastVehicleInspection", car.getLastVehicleInspection().toString());
            carsElement.appendChild(carElement);
        }
    }

    private static void appendViolations(Document doc, Element rootElement, EntityManager em) {
        Element violationsElement = doc.createElement("violations");
        rootElement.appendChild(violationsElement);

        TypedQuery<Violation> query = em.createQuery("SELECT v FROM Violation v", Violation.class);
        for (Violation violation : query.getResultList()) {
            Element violationElement = createElementWithChildren(doc, "violation",
                    "id", violation.getViolationId().toString(),
                    "violationArticle", violation.getViolationArticle().getViolationArticleId().toString(),
                    "car", violation.getCar().getCarId().toString(),
                    "violationDate", violation.getViolationDate().toString(),
                    "violationPaid", violation.getViolationPaid().toString(),
                    "violationType", violation.getViolationType().getViolationTypeId().toString());
            violationsElement.appendChild(violationElement);
        }
    }

    private static void appendViolationArticles(Document doc, Element rootElement, EntityManager em) {
        Element articlesElement = doc.createElement("violationArticles");
        rootElement.appendChild(articlesElement);

        TypedQuery<ViolationArticle> query = em.createQuery("SELECT va FROM ViolationArticle va", ViolationArticle.class);
        for (ViolationArticle article : query.getResultList()) {
            Element articleElement = createElementWithChildren(doc, "violationArticle",
                    "id", article.getViolationArticleId().toString(),
                    "violationArticleCode", article.getViolationArticleCode(),
                    "violationArticleDescription", article.getViolationArticleDescription(),
                    "violationArticleFine", article.getViolationArticleFine().toString());
            articlesElement.appendChild(articleElement);
        }
    }

    private static void appendViolationTypes(Document doc, Element rootElement, EntityManager em) {
        Element typesElement = doc.createElement("violationTypes");
        rootElement.appendChild(typesElement);

        TypedQuery<ViolationType> query = em.createQuery("SELECT vt FROM ViolationType vt", ViolationType.class);
        for (ViolationType type : query.getResultList()) {
            Element typeElement = createElementWithChildren(doc, "violationType",
                    "id", type.getViolationTypeId().toString(),
                    "violationTypeName", type.getViolationTypeName());
            typesElement.appendChild(typeElement);
        }
    }

    private static Element createElementWithChildren(Document doc, String elementName, String... data) {
        Element element = doc.createElement(elementName);
        for (int i = 0; i < data.length; i += 2) {
            Element child = doc.createElement(data[i]);
            child.appendChild(doc.createTextNode(data[i + 1]));
            element.appendChild(child);
        }
        return element;
    }

    private static void writeDocumentToFile(Document doc, String filePath) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
}
