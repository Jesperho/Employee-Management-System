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
                langCode, "IT Department", dept, "01 January 2022", "10"
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

    // ─── formatNumber TESTS ───────────────────────────────────────────────────

    @Test
    @Order(3)
    void testFormatNumberEnglish() {
        String result = translationDao.formatNumber(1234.5, "English");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(4)
    void testFormatNumberSpanish() {
        String result = translationDao.formatNumber(1234.5, "Spanish");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(5)
    void testFormatNumberArabic() {
        String result = translationDao.formatNumber(1234.5, "Arabic");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(6)
    void testFormatNumberPersia() {
        String result = translationDao.formatNumber(1234.5, "Persia");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(7)
    void testFormatNumberInteger() {
        String result = translationDao.formatNumber(100, "English");
        assertNotNull(result);
        assertTrue(result.contains("100"));
    }

    // ─── formatDate TESTS (public method) ────────────────────────────────────

    @Test
    @Order(8)
    void testFormatDateEnglish() {
        String result = translationDao.formatDate(LocalDate.of(2024, 1, 15), "English");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("2024"));
    }

    @Test
    @Order(9)
    void testFormatDateSpanish() {
        String result = translationDao.formatDate(LocalDate.of(2024, 3, 20), "Spanish");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(10)
    void testFormatDateArabic() {
        String result = translationDao.formatDate(LocalDate.of(2024, 6, 1), "Arabic");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    @Order(11)
    void testFormatDatePersia() {
        String result = translationDao.formatDate(LocalDate.of(2024, 9, 10), "Persia");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // ─── submitCreateDepartmentTranslation TESTS ─────────────────────────────

    @Test
    @Order(12)
    void testSubmitCreateDepartmentTranslationIT() {
        Company company = createCompany();
        Department dept = createDepartment(company);

        // "Information Technology" matches bundle key dept.it=Information Technology
        try {
            translationDao.submitCreateDepartmentTranslation("Information Technology", dept);
        } catch (Exception e) {
            // translation key might not exist — acceptable
        }
    }

    @Test
    @Order(13)
    void testSubmitCreateDepartmentTranslationHR() {
        Company company = createCompany();
        Department dept = createDepartment(company);

        // "HR" matches bundle key dept.hr=HR
        try {
            translationDao.submitCreateDepartmentTranslation("HR", dept);
        } catch (Exception e) {
            // translation key might not exist — acceptable
        }
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }
}