package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private Admin admin;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        admin = new Admin(
                "admin@testcorp.com",
                "securePassword123",
                company
        );
    }

    @Test
    @DisplayName("Admin should be created with constructor parameters")
    void testAdminConstructor() {
        assertNotNull(admin);
        assertEquals("admin@testcorp.com", admin.getEmail());
        assertEquals("securePassword123", admin.getPassword());
        assertEquals(company, admin.getCompany());
    }

    @Test
    @DisplayName("Admin default constructor should create empty object")
    void testAdminDefaultConstructor() {
        Admin emptyAdmin = new Admin();
        assertNotNull(emptyAdmin);
        assertNull(emptyAdmin.getEmail());
        assertNull(emptyAdmin.getPassword());
        assertNull(emptyAdmin.getCompany());
    }

    @Test
    @DisplayName("setEmail should update admin email")
    void testSetEmail() {
        admin.setEmail("newadmin@testcorp.com");
        assertEquals("newadmin@testcorp.com", admin.getEmail());
    }

    @Test
    @DisplayName("setPassword should update admin password")
    void testSetPassword() {
        admin.setPassword("newPassword456");
        assertEquals("newPassword456", admin.getPassword());
    }

    @Test
    @DisplayName("setCompany should update admin company")
    void testSetCompany() {
        Company newCompany = new Company("NewCorp", "456 New Street", "Sweden");
        admin.setCompany(newCompany);
        assertEquals(newCompany, admin.getCompany());
    }

    @Test
    @DisplayName("setId should update admin id")
    void testSetId() {
        admin.setId(10);
        assertEquals(10, admin.getId());
    }
}
