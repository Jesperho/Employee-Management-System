package dao;

import datasource.MariaDbConnection;
import entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeTranslationDaoTest {

    private EmployeeTranslationDao translationDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        translationDao = new EmployeeTranslationDao();
        em = MariaDbConnection.getEntityManager();
    }

    private Employee createEmployee() {
        em.getTransaction().begin();
        Company c = new Company("EmpTransCo_" + System.nanoTime(), "Street 1", "Finland");
        em.persist(c);
        Department d = new Department(10, "IT_" + System.nanoTime(), "IT Dept", LocalDate.now(), c);
        em.persist(d);
        Employee e = new Employee("Emp_" + System.nanoTime(), "Male", LocalDate.now(),
                4000.0, c, d, "Developer", "Full-time", "Junior");
        e.setLeaves(new ArrayList<>());
        em.persist(e);
        em.getTransaction().commit();
        return e;
    }

    private EmployeeTranslation createTranslation(Employee employee, String langCode) {
        EmployeeTranslation t = new EmployeeTranslation(
                langCode, "Junior Developer", "Full-time", "Male", employee, "01 January 2024", 4000.0
        );
        translationDao.create(t);
        return t;
    }

    @Test
    @Order(1)
    void testCreateAndFind() {
        Employee emp = createEmployee();
        EmployeeTranslation t = createTranslation(emp, "en");

        EmployeeTranslation found = translationDao.find(t.getId());
        assertNotNull(found);
        assertEquals("en", found.getLanguage_code());
    }

    @Test
    @Order(2)
    void testDeleteByEmployee() {
        Employee emp = createEmployee();
        EmployeeTranslation t1 = createTranslation(emp, "en");
        EmployeeTranslation t2 = createTranslation(emp, "es");

        translationDao.deleteByEmployee(emp);

        assertNull(translationDao.find(t1.getId()));
        assertNull(translationDao.find(t2.getId()));
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }
}