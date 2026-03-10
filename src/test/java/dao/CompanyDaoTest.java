package dao;

import datasource.MariaDbConnection;
import entity.Company;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompanyDaoTest {

    private CompanyDao companyDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        companyDao = new CompanyDao();
        em = MariaDbConnection.getEntityManager();
    }

    // Helper: unique company name to avoid DB conflicts
    private String uniqueName() {
        return "Company_" + System.nanoTime();
    }

    private Company createCompany() {
        Company c = new Company(uniqueName(), "Street 1", "Finland");
        companyDao.create(c);
        return c;
    }

    @Test
    @Order(1)
    void testCreate() {
        Company c = createCompany();
        assertTrue(c.getId() > 0);
    }

    @Test
    @Order(2)
    void testFind() {
        Company c = createCompany();
        Company found = companyDao.find(c.getId());

        assertNotNull(found);
        assertEquals(c.getName(), found.getName());
        assertEquals("Finland", found.getCountry());
    }

    @Test
    @Order(3)
    void testUpdate() {
        Company c = createCompany();

        String newName = uniqueName();
        c.setName(newName);
        companyDao.update(c);

        Company updated = companyDao.find(c.getId());
        assertEquals(newName, updated.getName());
    }

    @Test
    @Order(4)
    void testDelete() {
        Company c = createCompany();
        int id = c.getId();

        companyDao.delete(id);

        Company deleted = companyDao.find(id);
        assertNull(deleted);
    }

    @Test
    @Order(5)
    void testFindAll() {
        createCompany();
        createCompany();

        List<Company> list = companyDao.findAll();
        assertTrue(list.size() > 0);
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) {
            em.close();
        }
    }
}