package dao;

import datasource.MariaDbConnection;
import entity.Admin;
import entity.Company;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BaseDaoTest {

    private AdminDao adminDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        adminDao = new AdminDao();
        em = MariaDbConnection.getEntityManager();
    }

    // Helper: unique email to avoid DB conflicts
    private String uniqueEmail() {
        return "admin+" + System.nanoTime() + "@test.com";
    }

    private Company createCompany() {
        em.getTransaction().begin();
        Company c = new Company("BaseDaoCo", "Street 1", "Finland");
        em.persist(c);
        em.getTransaction().commit();
        return c;
    }

    private Admin createAdmin() {
        Company c = createCompany();
        Admin a = new Admin(uniqueEmail(), "pass123", c);
        adminDao.create(a);
        return a;
    }

    @Test
    @Order(1)
    void testCreate() {
        Admin admin = createAdmin();
        assertTrue(admin.getId() > 0);
    }

    @Test
    @Order(2)
    void testFind() {
        Admin admin = createAdmin();
        Admin found = adminDao.find(admin.getId());

        assertNotNull(found);
        assertEquals(admin.getEmail(), found.getEmail());
    }

    @Test
    @Order(3)
    void testUpdate() {
        Admin admin = createAdmin();

        String newEmail = uniqueEmail();
        admin.setEmail(newEmail);
        adminDao.update(admin);

        Admin updated = adminDao.find(admin.getId());
        assertEquals(newEmail, updated.getEmail());
    }

    @Test
    @Order(4)
    void testDelete() {
        Admin admin = createAdmin();
        int id = admin.getId();

        adminDao.delete(id);

        Admin deleted = adminDao.find(id);
        assertNull(deleted);
    }

    @Test
    @Order(5)
    void testFindAll() {
        createAdmin();
        createAdmin();

        List<Admin> list = adminDao.findAll();
        assertTrue(list.size() > 0);
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) {
            em.close();
        }
    }
}