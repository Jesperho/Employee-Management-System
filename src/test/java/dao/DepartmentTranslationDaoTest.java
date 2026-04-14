package dao;

import datasource.MariaDbConnection;
import entity.Company;
import entity.Department;
import entity.DepartmentTranslation;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentTranslationDaoTest {

    private DepartmentTranslationDao translationDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        translationDao = new DepartmentTranslationDao();
        em = MariaDbConnection.getEntityManager();
    }

    private Company createCompany() {
        em.getTransaction().begin();
        Company c = new Company("TransCo_" + System.nanoTime(), "Street 1", "Finland");
        em.persist(c);
        em.getTransaction().commit();
        return c;
    }

    private Department createDepartment(Company company) {
        em.getTransaction().begin();
        Department d = new Department(10, "IT_" + System.nanoTime(), "IT Dept", LocalDate.now(), company);
        em.persist(d);
        em.getTransaction().commit();
        return d;
    }

    private DepartmentTranslation createTranslation(Department dept, String langCode) {
        DepartmentTranslation t = new DepartmentTranslation(
                langCode, "IT Department", dept, "01 January 2022", 10
        );
        translationDao.create(t);
        return t;
    }

    @Test
    @Order(1)
    void testCreateAndFind() {
        Company company = createCompany();
        Department dept = createDepartment(company);
        DepartmentTranslation t = createTranslation(dept, "en");

        DepartmentTranslation found = translationDao.find(t.getId());
        assertNotNull(found);
        assertEquals("en", found.getLanguageCode());
        assertEquals(dept.getId(), found.getDepartment().getId());
    }

    @Test
    @Order(2)
    void testDeleteByDepartment() {
        Company company = createCompany();
        Department dept = createDepartment(company);
        DepartmentTranslation t1 = createTranslation(dept, "en");
        DepartmentTranslation t2 = createTranslation(dept, "es");

        translationDao.deleteByDepartment(dept);

        assertNull(translationDao.find(t1.getId()));
        assertNull(translationDao.find(t2.getId()));
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }
}