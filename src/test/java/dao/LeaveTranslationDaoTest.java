package dao;

import datasource.MariaDbConnection;
import entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LeaveTranslationDaoTest {

    private LeaveTranslationDao translationDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        translationDao = new LeaveTranslationDao();
        em = MariaDbConnection.getEntityManager();
    }

    private Employee createEmployee() {
        em.getTransaction().begin();
        Company c = new Company("LeaveCo_" + System.nanoTime(), "Street 1", "Finland");
        em.persist(c);
        Department d = new Department(10, "IT_" + System.nanoTime(), "IT Dept", LocalDate.now(), c);
        em.persist(d);
        Employee e = new Employee("Emp_" + System.nanoTime(), "Male", LocalDate.now(),
                3000.0, c, d, "Developer", "Full-time", "Junior");
        e.setLeaves(new ArrayList<>());
        em.persist(e);
        em.getTransaction().commit();
        return e;
    }

    private Leave createLeave(Employee employee) {
        em.getTransaction().begin();
        Leave leave = new Leave(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 10),
                "Annual",
                "Holiday",
                employee
        );
        em.persist(leave);
        em.getTransaction().commit();
        return leave;
    }

    private LeaveTranslation createTranslation(Leave leave, String langCode) {
        LeaveTranslation t = new LeaveTranslation(
                langCode, "Annual", leave, "01 May 2024", "10 May 2024", "10"
        );
        translationDao.create(t);
        return t;
    }

    @Test
    @Order(1)
    void testCreateAndFind() {
        Employee emp = createEmployee();
        Leave leave = createLeave(emp);
        LeaveTranslation t = createTranslation(leave, "en");

        LeaveTranslation found = translationDao.find(t.getId());
        assertNotNull(found);
        assertEquals("en", found.getLanguageCode());
        assertEquals(leave.getId(), found.getLeave().getId());
    }

    @Test
    @Order(2)
    void testDeleteByLeave() {
        Employee emp = createEmployee();
        Leave leave = createLeave(emp);
        LeaveTranslation t1 = createTranslation(leave, "en");
        LeaveTranslation t2 = createTranslation(leave, "es");

        translationDao.deleteByLeave(leave);

        assertNull(translationDao.find(t1.getId()));
        assertNull(translationDao.find(t2.getId()));
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }
}