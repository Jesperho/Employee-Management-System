package service;

import dao.AdminDao;
import dao.CompanyDao;
import datasource.MariaDbConnection;
import entity.Admin;
import entity.Company;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTest {

    private AuthService authService;
    private EntityManager em;

    private static final String TEST_EMAIL = "authtest+" + System.nanoTime() + "@test.com";
    private static final String TEST_PASSWORD = "securePass123";

    @BeforeEach
    void setUp() {
        authService = new AuthService();
        em = MariaDbConnection.getEntityManager();
    }

    @AfterEach
    void tearDown() {
        AuthService.logout();
        if (em.isOpen()) em.close();
    }

    @Test
    @Order(1)
    @DisplayName("register creates a new company and admin, returns true")
    void testRegisterSuccess() {
        boolean result = authService.register(
                "AuthTestCo_" + System.nanoTime(), "Street 1", "Finland",
                "John", "Doe", TEST_EMAIL, "Male", TEST_PASSWORD
        );
        assertTrue(result);

        Admin created = new AdminDao().findByEmail(TEST_EMAIL);
        assertNotNull(created);
    }

    @Test
    @Order(2)
    @DisplayName("register returns false when email already exists")
    void testRegisterDuplicateEmail() {
        // Register once
        String email = "dup+" + System.nanoTime() + "@test.com";
        authService.register("Co1", "Addr1", "Finland", "A", "B", email, "Male", "pass1");

        // Second attempt with same email should fail
        boolean result = authService.register("Co2", "Addr2", "Sweden", "C", "D", email, "Female", "pass2");
        assertFalse(result);
    }

    @Test
    @Order(3)
    @DisplayName("login returns admin when credentials are correct")
    void testLoginSuccess() {
        String email = "login+" + System.nanoTime() + "@test.com";
        authService.register("LoginCo", "Road 1", "Finland", "Jane", "Doe", email, "Female", TEST_PASSWORD);

        Admin result = authService.login(email, TEST_PASSWORD);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertNotNull(AuthService.getCurrentAdmin());
    }

    @Test
    @Order(4)
    @DisplayName("login returns null when password is wrong")
    void testLoginWrongPassword() {
        String email = "wrongpass+" + System.nanoTime() + "@test.com";
        authService.register("WrongPassCo", "Road 2", "Finland", "Jim", "Beam", email, "Male", TEST_PASSWORD);

        Admin result = authService.login(email, "wrongPassword");

        assertNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("login returns null when email does not exist")
    void testLoginEmailNotFound() {
        Admin result = authService.login("nonexistent@nowhere.com", "anyPassword");
        assertNull(result);
    }

    @Test
    @Order(6)
    @DisplayName("logout clears the current admin")
    void testLogout() {
        String email = "logout+" + System.nanoTime() + "@test.com";
        authService.register("LogoutCo", "Road 3", "Finland", "Out", "User", email, "Male", TEST_PASSWORD);
        authService.login(email, TEST_PASSWORD);

        assertNotNull(AuthService.getCurrentAdmin());

        AuthService.logout();

        assertNull(AuthService.getCurrentAdmin());
    }

    @Test
    @Order(7)
    @DisplayName("getCurrentAdmin returns null before any login")
    void testGetCurrentAdminInitiallyNull() {
        assertNull(AuthService.getCurrentAdmin());
    }

    @Test
    @Order(8)
    @DisplayName("AuthService(dao, dao) constructor works and can perform login")
    void testInjectedConstructor() {
        AdminDao adminDao = new AdminDao();
        CompanyDao companyDao = new CompanyDao();
        AuthService injected = new AuthService(adminDao, companyDao);

        Admin result = injected.login("nonexistent_injected@nowhere.com", "pass");
        assertNull(result);
    }
}