package dao;

import datasource.MariaDbConnection;
import entity.Admin;
import entity.Company;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminDaoTest {

    private AdminDao adminDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        adminDao = new AdminDao();
        em = MariaDbConnection.getEntityManager();
    }

    // Helper: create a unique email every time
    private String uniqueEmail() {
        return "admin+" + System.nanoTime() + "@test.com";
    }

    private Company createCompany() {
        em.getTransaction().begin();
        Company c = new Company("TestCo", "Street 1", "Finland");
        em.persist(c);
        em.getTransaction().commit();
        return c;
    }

    private Admin createAdmin() {
        Company c = createCompany();
        String email = uniqueEmail();

        Admin a = new Admin(email, "pass123", c);
        adminDao.create(a);
        return a;
    }

    @Test
    @Order(1)
    void testCreateAndFind() {
        Admin admin = createAdmin();

        assertTrue(admin.getId() > 0);

        Admin found = adminDao.find(admin.getId());
        assertNotNull(found);
        assertEquals(admin.getEmail(), found.getEmail());
        assertEquals("TestCo", found.getCompany().getName());
    }

    @Test
    @Order(2)
    void testFindByEmail() {
        Admin admin = createAdmin();

        Admin found = adminDao.findByEmail(admin.getEmail());
        assertNotNull(found);
        assertEquals(admin.getId(), found.getId());
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
    void testFindByEmailAndPassword() {
        Admin admin = createAdmin();
        Admin found = adminDao.findByEmailAndPassword(admin.getEmail(), "pass123");
        assertNotNull(found);
        assertEquals(admin.getId(), found.getId());
    }

    @Test
    @Order(6)
    void testFindByEmailAndPasswordWrongPassword() {
        Admin admin = createAdmin();
        Admin found = adminDao.findByEmailAndPassword(admin.getEmail(), "wrongPassword");
        assertNull(found);
    }

    @Test
    @Order(7)
    void testEmailExistsReturnsTrue() {
        Admin admin = createAdmin();
        assertTrue(adminDao.emailExists(admin.getEmail()));
    }

    @Test
    @Order(8)
    void testEmailExistsReturnsFalse() {
        assertFalse(adminDao.emailExists("nobody+" + System.nanoTime() + "@test.com"));
    }

    @Test
    @Order(9)
    void testFindByCompany() {
        Admin admin = createAdmin();
        java.util.List<Admin> list = adminDao.findByCompany(admin.getCompany().getId());
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(a -> a.getId() == admin.getId()));
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) {
            em.close();
        }
    }
}
