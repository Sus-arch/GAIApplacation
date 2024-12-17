package utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Car;
import entities.Driver;
import entities.Violation;
import entities.ViolationArticle;
import entities.ViolationType;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Утилитный класс для работы с XML-файлами, таких как экспорт и импорт данных.
 */
public class XMLManager {
    private static final Logger logger = Logger.getLogger(XMLManager.class);

    /**
     * Экспортирует данные из базы данных в XML-файл.
     *
     * @param filePath путь к файлу для сохранения данных.
     * @param em экземпляр EntityManager для выполнения запросов.
     */
    public static void exportDataToXML(String filePath, EntityManager em) {
        logger.info("Старт экспорта данных в XML-файл");

        try {
            // Создание нового XML-документа
            Document doc = createDocument();
            // Создание корневого элемента
            Element rootElement = createRootElement(doc, "data");
            
            // Добавление всех сущностей в XML
            appendDrivers(doc, rootElement, em);
            appendCars(doc, rootElement, em);
            appendViolations(doc, rootElement, em);
            appendViolationArticles(doc, rootElement, em);
            appendViolationTypes(doc, rootElement, em);

            // Сохранение XML-документа в файл
            writeDocumentToFile(doc, filePath);
            logger.info("Данные успешно сохранены в XML-файл: " + filePath);
        } catch (Exception ex) {
            logger.error("Ошибка во время экспорта данных в XML-файл", ex);
        }
    }
    
    /**
     * Создаёт XML-файл для отчёта на основе переданных данных.
     *
     * @param filePath путь к файлу для сохранения отчёта.
     * @param data список объектов для записи в отчёт.
     * @param startDate начало периода отчёта.
     * @param endDate конец периода отчёта.
     */
    public static void createReportXML(String filePath, List<Object[]> data, String startDate, String endDate) {
    	try {
            logger.info("Старт экспорта данных для отчёта в XML-файл");

    		// Создание нового XML-документа
            Document doc = createDocument();
            // Создание корневого элемента
            Element rootElement = createRootElement(doc, "report");
            
            // Добавление даты и времени экспорта
            appendDateTimeElement(doc, rootElement);
            
            // Добавление дат отчёта в корневой элемент
            Element startDateElement = doc.createElement("startDate");
            startDateElement.appendChild(doc.createTextNode(startDate));
            rootElement.appendChild(startDateElement);
            
            Element endDateElement = doc.createElement("endDate");
            endDateElement.appendChild(doc.createTextNode(endDate));
            rootElement.appendChild(endDateElement);
            
            // Добавление данных отчёта
            appendReport(doc, rootElement, data);
            
            // Сохранение XML-документа в файл
            writeDocumentToFile(doc, filePath);
            logger.info("Данные для отчёта успешно сохранены в XML-файл: " + filePath);
    	} catch (Exception ex) {
            logger.error("Ошибка во время экспорта данных для отчёта в XML-файл", ex);
        }
    }
    
    /**
     * Создаёт новый XML-документ.
     *
     * @return экземпляр Document для работы с XML.
     * @throws Exception если возникает ошибка при создании документа.
     */
    private static Document createDocument() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.newDocument();
    }
    
    /**
     * Создаёт корневой элемент XML-документа.
     *
     * @param doc XML-документ, к которому будет добавлен элемент.
     * @param name имя корневого элемента.
     * @return созданный элемент.
     */
    private static Element createRootElement(Document doc, String name) {
        Element rootElement = doc.createElement(name);
        doc.appendChild(rootElement);
        return rootElement;
    }
    
    /**
     * Добавляет элемент с текущей датой и временем в XML-документ.
     *
     * @param doc XML-документ, к которому будет добавлен элемент.
     * @param rootElement корневой элемент, к которому будет добавлен элемент.
     */
    private static void appendDateTimeElement(Document doc, Element rootElement) {
        Element exportDateTime = doc.createElement("exportDateTime");
        // Генерация текущей даты и времени в формате ГГГГ-ММ-ДД ЧЧ:ММ:СС.ССС
        String currentDateTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        exportDateTime.appendChild(doc.createTextNode(currentDateTime));
        rootElement.appendChild(exportDateTime);
    }
    
    /**
     * Добавляет данные отчёта в XML-документ.
     *
     * @param doc XML-документ, к которому будет добавлен элемент.
     * @param rootElement корневой элемент, к которому будут добавлены данные.
     * @param data список данных для добавления в отчёт.
     */
    private static void appendReport(Document doc, Element rootElement, List<Object[]> data) {
        // Создание элемента для хранения всех нарушений
    	Element violationsElement = doc.createElement("violations");
        rootElement.appendChild(violationsElement);
        
        // Добавление каждого нарушения в виде дочернего элемента
        for (Object[] violation : data) {
            Element violationElement = createElementWithChildren(doc, "violation",
                    "date", violation[0].toString(),
                    "driverName", violation[1].toString(),
                    "car", violation[2].toString(),
                    "violationType", violation[3].toString());
            violationsElement.appendChild(violationElement);
        }
    }
    
    /**
     * Добавляет водителей в XML-документ.
     *
     * @param doc XML-документ, к которому будут добавлены данные.
     * @param rootElement корневой элемент, к которому будут добавлены данные.
     * @param em экземпляр EntityManager для получения данных из базы.
     */
    private static void appendDrivers(Document doc, Element rootElement, EntityManager em) {
        // Создаём элемент для водителей и добавляем его в корневой элемент
        Element driversElement = doc.createElement("drivers");
        rootElement.appendChild(driversElement);

        // Получаем список водителей из базы данных
        TypedQuery<Driver> query = em.createQuery("SELECT d FROM Driver d", Driver.class);
        for (Driver driver : query.getResultList()) {
            // Создаём элемент для каждого водителя с его данными
            Element driverElement = createElementWithChildren(doc, "driver",
                    "id", driver.getDriverId().toString(),
                    "firstName", driver.getFirstName(),
                    "lastName", driver.getLastName(),
                    "middleName", driver.getMiddleName(),
                    "licenseNumber", driver.getLicenseNumber(),
                    "birthDate", driver.getBirthday().toString(),
                    "city", driver.getCity());
            driversElement.appendChild(driverElement); // Добавляем элемент водителя в список
        }
    }
    
    /**
     * Добавляет автомобили в XML-документ.
     *
     * @param doc XML-документ, к которому будут добавлены данные.
     * @param rootElement корневой элемент, к которому будут добавлены данные.
     * @param em экземпляр EntityManager для получения данных из базы.
     */
    private static void appendCars(Document doc, Element rootElement, EntityManager em) {
        // Создаём элемент для автомобилей и добавляем его в корневой элемент
        Element carsElement = doc.createElement("cars");
        rootElement.appendChild(carsElement);

        // Получаем список автомобилей из базы данных
        TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c", Car.class);
        for (Car car : query.getResultList()) {
            // Создаём элемент для каждого автомобиля с его данными
            Element carElement = createElementWithChildren(doc, "car",
                    "id", car.getCarId().toString(),
                    "brand", car.getBrand(),
                    "model", car.getModel(),
                    "vinNumber", car.getVinNumber(),
                    "licensePlate", car.getLicensePlate(),
                    "ownerId", car.getOwner().getLicenseNumber(),
                    "lastVehicleInspection", car.getLastVehicleInspection().toString());
            carsElement.appendChild(carElement); // Добавляем элемент автомобиля в список
        }
    }
    
    /**
     * Добавляет нарушения в XML-документ.
     *
     * @param doc XML-документ, к которому будут добавлены данные.
     * @param rootElement корневой элемент, к которому будут добавлены данные.
     * @param em экземпляр EntityManager для получения данных из базы.
     */
    private static void appendViolations(Document doc, Element rootElement, EntityManager em) {
        // Создаём элемент для нарушений и добавляем его в корневой элемент
        Element violationsElement = doc.createElement("violations");
        rootElement.appendChild(violationsElement);

        // Получаем список нарушений из базы данных
        TypedQuery<Violation> query = em.createQuery("SELECT v FROM Violation v", Violation.class);
        for (Violation violation : query.getResultList()) {
            // Создаём элемент для каждого нарушения с его данными
            Element violationElement = createElementWithChildren(doc, "violation",
                    "id", violation.getViolationId().toString(),
                    "violationResolution", violation.getViolationResolution(),
                    "violationArticleV", violation.getViolationArticle().getViolationArticleCode(),
                    "violationCar", violation.getCar().getLicensePlate(),
                    "violationDate", violation.getViolationDate().toString(),
                    "violationPaid", violation.getViolationPaid().toString(),
                    "violationTypeV", violation.getViolationType().getViolationTypeName());
            violationsElement.appendChild(violationElement); // Добавляем элемент нарушения в список
        }
    }
    
    /**
     * Добавляет статьи нарушений в XML-документ.
     *
     * @param doc XML-документ, к которому будут добавлены данные.
     * @param rootElement корневой элемент, к которому будут добавлены данные.
     * @param em экземпляр EntityManager для получения данных из базы.
     */
    private static void appendViolationArticles(Document doc, Element rootElement, EntityManager em) {
        // Создаём элемент для статей нарушений и добавляем его в корневой элемент
        Element articlesElement = doc.createElement("violationArticles");
        rootElement.appendChild(articlesElement);

        // Получаем список статей нарушений из базы данных
        TypedQuery<ViolationArticle> query = em.createQuery("SELECT va FROM ViolationArticle va", ViolationArticle.class);
        for (ViolationArticle article : query.getResultList()) {
            // Создаём элемент для каждой статьи нарушения с её данными
            Element articleElement = createElementWithChildren(doc, "violationArticle",
                    "id", article.getViolationArticleId().toString(),
                    "violationArticleCode", article.getViolationArticleCode(),
                    "violationArticleDescription", article.getViolationArticleDescription(),
                    "violationArticleFine", article.getViolationArticleFine().toString());
            articlesElement.appendChild(articleElement); // Добавляем элемент статьи нарушения в список
        }
    }
    
    /**
     * Добавляет типы нарушений в XML-документ.
     *
     * @param doc XML-документ, к которому будут добавлены данные.
     * @param rootElement корневой элемент, к которому будут добавлены данные.
     * @param em экземпляр EntityManager для получения данных из базы.
     */
    private static void appendViolationTypes(Document doc, Element rootElement, EntityManager em) {
        // Создаём элемент для типов нарушений и добавляем его в корневой элемент
        Element typesElement = doc.createElement("violationTypes");
        rootElement.appendChild(typesElement);

        // Получаем список типов нарушений из базы данных
        TypedQuery<ViolationType> query = em.createQuery("SELECT vt FROM ViolationType vt", ViolationType.class);
        for (ViolationType type : query.getResultList()) {
            // Создаём элемент для каждого типа нарушения с его данными
            Element typeElement = createElementWithChildren(doc, "violationType",
                    "id", type.getViolationTypeId().toString(),
                    "violationTypeName", type.getViolationTypeName());
            typesElement.appendChild(typeElement); // Добавляем элемент типа нарушения в список
        }
    }
    
    /**
     * Создаёт элемент с дочерними элементами на основе переданных данных.
     *
     * @param doc XML-документ, к которому будет добавлен элемент.
     * @param elementName имя основного элемента.
     * @param data пары имя-значение для дочерних элементов.
     * @return созданный элемент.
     */
    private static Element createElementWithChildren(Document doc, String elementName, String... data) {
        // Создаём элемент с данным именем
        Element element = doc.createElement(elementName);
        // Для каждой пары имя-значение создаём дочерний элемент и добавляем его
        for (int i = 0; i < data.length; i += 2) {
            Element child = doc.createElement(data[i]);
            child.appendChild(doc.createTextNode(data[i + 1]));
            element.appendChild(child);
        }
        return element; // Возвращаем созданный элемент
    }
    
    /**
     * Сохраняет XML-документ в файл.
     *
     * @param doc XML-документ для сохранения.
     * @param filePath путь к файлу, в который будет сохранён документ.
     * @throws Exception если возникает ошибка при сохранении документа.
     */
    private static void writeDocumentToFile(Document doc, String filePath) throws Exception {
        // Создаём трансформер для записи XML в файл
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result); // Выполняем запись документа в файл
    }
    
    /**
     * Импортирует данные из XML-файла в базу данных.
     *
     * @param filePath путь к файлу для чтения данных.
     * @param em экземпляр EntityManager для выполнения запросов.
     * @param importMode режим импорта данных: 0 - заменить, 1 - добавить, 2 - обновить.
     */
    public static void importDataFromXML(String filePath, EntityManager em, int importMode) {
        logger.info("Начат импорт данных из XML-файла: " + filePath);

        try {
            // Чтение и парсинг XML-файла
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            logger.info("Запуск транзакции");
            em.getTransaction().begin(); // Начинаем транзакцию

            // Импорт данных в зависимости от режима
            if (importMode == 0) {
                logger.info("Выполняется полный импорт данных");
                clearAllData(em); // Очистка данных
            }

            // Проверяем наличие каждого типа данных и вызываем соответствующие методы импорта
            if (doc.getElementsByTagName("violationArticles").getLength() > 0) {
                logger.info("Импорт статей нарушений");
                importViolationArticles(doc, em, importMode);
            }
            if (doc.getElementsByTagName("violationTypes").getLength() > 0) {
                logger.info("Импорт типов нарушений");
                importViolationTypes(doc, em, importMode);
            }
            if (doc.getElementsByTagName("drivers").getLength() > 0) {
                logger.info("Импорт водителей");
                importDrivers(doc, em, importMode);
            }
            if (doc.getElementsByTagName("cars").getLength() > 0) {
                logger.info("Импорт автомобилей");
                importCars(doc, em, importMode);
            }
            if (doc.getElementsByTagName("violations").getLength() > 0) {
                logger.info("Импорт нарушений");
                importViolations(doc, em, importMode);
            }

            // Подтверждаем транзакцию
            logger.info("Подтверждение транзакции");
            em.getTransaction().commit();
            logger.info("Импорт данных завершён успешно");
        } catch (Exception ex) {
            logger.error("Ошибка при импорте данных из XML-файла", ex);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Откатываем транзакцию в случае ошибки
            }
        }
    }
    
    /**
     * Удаляет все данные из базы данных.
     *
     * @param em экземпляр EntityManager для выполнения запросов.
     */
    private static void clearAllData(EntityManager em) {
        // Удаляем все данные из таблиц
        em.createQuery("DELETE FROM Violation").executeUpdate();
        em.createQuery("DELETE FROM Car").executeUpdate();
        em.createQuery("DELETE FROM Driver").executeUpdate();
        em.createQuery("DELETE FROM ViolationType").executeUpdate();
        em.createQuery("DELETE FROM ViolationArticle").executeUpdate();
    }
    
    /**
     * Импортирует водителей из XML-файла.
     *
     * @param doc XML-документ для чтения данных.
     * @param em экземпляр EntityManager для выполнения запросов.
     * @param importMode режим импорта данных: 0 - заменить, 1 - добавить, 2 - обновить.
     */
    private static void importDrivers(Document doc, EntityManager em, int importMode) {
        // Получаем все элементы с данными водителей
        NodeList driverNodes = doc.getElementsByTagName("driver");

        for (int i = 0; i < driverNodes.getLength(); i++) {
            Element driverElement = (Element) driverNodes.item(i);

            String licenseNumber = safeGetText(driverElement, "licenseNumber");

            if (licenseNumber == null) {
                logger.warn("Номер водительского удостоверения отсутствует или некорректно задан. Пропускаем запись.");
                continue;
            }

            // Находим водителя по номеру удостоверения
            Driver driver = em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :licenseNumber", Driver.class)
                              .setParameter("licenseNumber", licenseNumber)
                              .getResultStream()
                              .findFirst()
                              .orElse(null);

            // Пропускаем водителя, если он уже существует в режиме добавления
            if (importMode == 1 && driver != null) continue;

            if (driver == null) {
                driver = new Driver(); // Создаём нового водителя
            }

            // Обновляем данные водителя
            driver.setFirstName(safeGetText(driverElement, "firstName"));
            driver.setLastName(safeGetText(driverElement, "lastName"));
            driver.setMiddleName(safeGetText(driverElement, "middleName"));
            driver.setBirthday(safeParseDate(driverElement, "birthDate"));
            driver.setCity(safeGetText(driverElement, "city"));
            driver.setLicenseNumber(licenseNumber);

            // Сохраняем или обновляем данные водителя в базе
            if (driver.getDriverId() == null) {
                em.persist(driver); // Новый водитель
            } else {
                em.merge(driver); // Обновление существующего водителя
            }
        }
    }
    /**
     * Импортирует данные об автомобилях из XML-документа в базу данных.
     *
     * @param doc XML-документ для чтения данных.
     * @param em экземпляр EntityManager для выполнения запросов.
     * @param importMode режим импорта данных: 0 - заменить, 1 - добавить, 2 - обновить.
     */
    private static void importCars(Document doc, EntityManager em, int importMode) {
        // Извлечение всех узлов <car> из XML-документа
        NodeList carNodes = doc.getElementsByTagName("car");

        for (int i = 0; i < carNodes.getLength(); i++) {
            Element carElement = (Element) carNodes.item(i);

            // Проверка наличия VIN-номера автомобиля
            String vinNumber = safeGetText(carElement, "vinNumber");
            if (vinNumber == null) {
                logger.warn("VIN номер автомобиля отсутствует или некорректно задан. Пропускаем запись.");
                continue;
            }

            // Поиск автомобиля в базе данных по VIN-номеру
            Car car = em.createQuery("SELECT c FROM Car c WHERE c.vinNumber = :vinNumber", Car.class)
                        .setParameter("vinNumber", vinNumber)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

            // Пропуск записи, если режим добавления и автомобиль уже существует
            if (importMode == 1 && car != null) continue;

            // Если автомобиль не найден, создаём новую запись
            if (car == null) {
                car = new Car();
            }

            // Заполнение данных об автомобиле из XML
            car.setBrand(safeGetText(carElement, "brand"));
            car.setModel(safeGetText(carElement, "model"));
            car.setVinNumber(vinNumber);
            car.setLicensePlate(safeGetText(carElement, "licensePlate"));

            // Установка владельца автомобиля
            String ownerLicenseNumber = safeGetText(carElement, "ownerId");
            if (ownerLicenseNumber != null) {
                Driver owner = em.createQuery("SELECT d FROM Driver d WHERE d.licenseNumber = :licenseNumber", Driver.class)
                                 .setParameter("licenseNumber", ownerLicenseNumber)
                                 .getResultStream()
                                 .findFirst()
                                 .orElse(null);
                if (owner != null) {
                    car.setOwner(owner);
                } else {
                    logger.warn("Владелец с номером удостоверения " + ownerLicenseNumber +
                                " не найден в базе данных для автомобиля с VIN " + vinNumber);
                }
            }

            car.setLastVehicleInspection(safeParseDate(carElement, "lastVehicleInspection"));

            // Сохранение данных в базу
            em.merge(car);
        }
    }

    /**
     * Импортирует данные о нарушениях из XML-документа в базу данных.
     *
     * @param doc XML-документ для чтения данных.
     * @param em экземпляр EntityManager для выполнения запросов.
     * @param importMode режим импорта данных: 0 - заменить, 1 - добавить, 2 - обновить.
     */
    private static void importViolations(Document doc, EntityManager em, int importMode) {
        // Извлечение всех узлов <violation> из XML-документа
        NodeList violationNodes = doc.getElementsByTagName("violation");

        for (int i = 0; i < violationNodes.getLength(); i++) {
            Element violationElement = (Element) violationNodes.item(i);

            // Проверка наличия номера постановления нарушения
            String violationResolution = safeGetText(violationElement, "violationResolution");
            if (violationResolution == null) {
                logger.warn("Номер постановления нарушения отсутствует или некорректно задан. Пропускаем запись.");
                continue;
            }

            // Поиск нарушения в базе данных по номеру постановления
            Violation violation = em.createQuery("SELECT v FROM Violation v WHERE v.violationResolution = :violationResolution", Violation.class)
                                     .setParameter("violationResolution", violationResolution)
                                     .getResultStream()
                                     .findFirst()
                                     .orElse(null);

            // Пропуск записи, если режим добавления и нарушение уже существует
            if (importMode == 1 && violation != null) continue;

            // Если нарушение не найдено, создаём новую запись
            if (violation == null) {
                violation = new Violation();
            }

            // Установка автомобиля, связанного с нарушением
            String licensePlate = safeGetText(violationElement, "violationCar");
            Car car = em.createQuery("SELECT c FROM Car c WHERE c.licensePlate = :licensePlate", Car.class)
                        .setParameter("licensePlate", licensePlate)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
            if (car != null) {
                violation.setCar(car);
            } else {
                logger.warn("Автомобиль с госномером " + licensePlate + " не найден в базе данных.");
            }

            // Установка статьи нарушения
            String violationArticle = safeGetText(violationElement, "violationArticleV");
            ViolationArticle article = em.createQuery("SELECT a FROM ViolationArticle a WHERE a.violationArticleCode = :violationArticle", ViolationArticle.class)
                                          .setParameter("violationArticle", violationArticle)
                                          .getResultStream()
                                          .findFirst()
                                          .orElse(null);
            if (article != null) {
                violation.setViolationArticle(article);
            } else {
                logger.warn("Статья нарушения " + violationArticle + " не найдена в базе данных.");
            }

            // Установка типа нарушения
            String violationType = safeGetText(violationElement, "violationTypeV");
            ViolationType type = em.createQuery("SELECT t FROM ViolationType t WHERE t.violationTypeName = :violationTypeName", ViolationType.class)
                                   .setParameter("violationTypeName", violationType)
                                   .getResultStream()
                                   .findFirst()
                                   .orElse(null);
            if (type != null) {
                violation.setViolationType(type);
            } else {
                logger.warn("Тип нарушения " + violationType + " не найден в базе данных.");
            }

            violation.setViolationResolution(violationResolution);
            violation.setViolationDate(safeParseDate(violationElement, "violationDate"));
            violation.setViolationPaid(Boolean.parseBoolean(safeGetText(violationElement, "violationPaid")));

            // Сохранение данных в базу
            em.merge(violation);
        }
    }

    /**
     * Импортирует данные о статьях нарушений из XML-документа в базу данных.
     *
     * @param doc XML-документ для чтения данных.
     * @param em экземпляр EntityManager для выполнения запросов.
     * @param importMode режим импорта данных: 0 - заменить, 1 - добавить, 2 - обновить.
     */
    private static void importViolationArticles(Document doc, EntityManager em, int importMode) {
        // Извлечение всех узлов <violationArticle> из XML-документа
        NodeList articleNodes = doc.getElementsByTagName("violationArticle");

        for (int i = 0; i < articleNodes.getLength(); i++) {
            Element articleElement = (Element) articleNodes.item(i);

            // Проверка наличия кода статьи нарушения
            String articleCode = safeGetText(articleElement, "violationArticleCode");
            if (articleCode == null) {
                logger.warn("Код статьи нарушения отсутствует или некорректно задан. Пропускаем запись.");
                continue;
            }

            // Поиск статьи нарушения в базе данных по коду
            ViolationArticle article = em.createQuery("SELECT a FROM ViolationArticle a WHERE a.violationArticleCode = :articleCode", ViolationArticle.class)
                                         .setParameter("articleCode", articleCode)
                                         .getResultStream()
                                         .findFirst()
                                         .orElse(null);

            // Пропуск записи, если режим добавления и статья уже существует
            if (importMode == 1 && article != null) continue;

            // Если статья не найдена, создаём новую запись
            if (article == null) {
                article = new ViolationArticle();
            }

            article.setViolationArticleCode(articleCode);
            article.setViolationArticleDescription(safeGetText(articleElement, "violationArticleDescription"));
            article.setViolationArticleFine(safeParseInt(articleElement, "violationArticleFine"));

            // Сохранение данных в базу
            em.merge(article);
        }
    }

    /**
     * Импортирует данные о типах нарушений из XML-документа в базу данных.
     *
     * @param doc XML-документ для чтения данных.
     * @param em экземпляр EntityManager для выполнения запросов.
     * @param importMode режим импорта данных: 0 - заменить, 1 - добавить, 2 - обновить.
     */
    private static void importViolationTypes(Document doc, EntityManager em, int importMode) {
        // Извлечение всех узлов <violationType> из XML-документа
        NodeList typeNodes = doc.getElementsByTagName("violationType");

        for (int i = 0; i < typeNodes.getLength(); i++) {
            Element typeElement = (Element) typeNodes.item(i);

            // Проверка наличия имени типа нарушения
            String typeName = safeGetText(typeElement, "violationTypeName");
            if (typeName == null) {
                logger.warn("Название нарушения отсутствует или некорректно задано. Пропускаем запись.");
                continue;
            }

            // Поиск типа нарушения в базе данных по имени
            ViolationType type = em.createQuery("SELECT t FROM ViolationType t WHERE t.violationTypeName = :typeName", ViolationType.class)
                                   .setParameter("typeName", typeName)
                                   .getResultStream()
                                   .findFirst()
                                   .orElse(null);

            // Пропуск записи, если режим добавления и тип уже существует
            if (importMode == 1 && type != null) continue;

            // Если тип не найден, создаём новую запись
            if (type == null) {
                type = new ViolationType();
            }

            type.setViolationTypeName(typeName);

            // Сохранение данных в базу
            em.merge(type);
        }
    }
    
    /**
     * Безопасно извлекает текстовое содержимое из указанного элемента XML по заданному имени тега.
     *
     * @param element XML-элемент, из которого извлекается текст.
     * @param tagName имя тега, текст которого необходимо извлечь.
     * @return текстовое содержимое указанного тега, или null, если тег отсутствует или его содержимое пустое.
     */
    private static String safeGetText(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0 && nodeList.item(0) != null) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    /**
     * Безопасно преобразует текстовое содержимое тега XML в целое число.
     *
     * @param element XML-элемент, из которого извлекается значение.
     * @param tagName имя тега, значение которого необходимо преобразовать в целое число.
     * @return целочисленное значение, или null, если значение отсутствует, пустое или не является допустимым числом.
     */
    private static Integer safeParseInt(Element element, String tagName) {
        String value = safeGetText(element, tagName);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Невозможно преобразовать значение в целое число для тега: " + tagName);
            }
        }
        return null;
    }

    /**
     * Безопасно преобразует текстовое содержимое тега XML в объект LocalDate.
     *
     * @param element XML-элемент, из которого извлекается значение.
     * @param tagName имя тега, значение которого необходимо преобразовать в дату.
     * @return объект LocalDate, или null, если значение отсутствует, пустое или не является допустимой датой.
     */
    private static LocalDate safeParseDate(Element element, String tagName) {
        String value = safeGetText(element, tagName);
        try {
            return value != null ? LocalDate.parse(value) : null;
        } catch (DateTimeParseException e) {
            logger.warn("Невозможно преобразовать строку в дату для тега: " + tagName);
            return null;
        }
    }

}
