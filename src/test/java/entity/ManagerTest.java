package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ManagerTest {

    private Manager manager;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        manager = new Manager(
                "John",
                "Smith",
                "john.smith@testcorp.com",
                "securePassword123",
                "Male",
                company
        );
    }

    @Test
    @DisplayName("Manager should be created with constructor parameters")
    void testManagerConstructor() {
        assertNotNull(manager);
        assertEquals("John", manager.getFirstName());
        assertEquals("Smith", manager.getLastName());
        assertEquals("john.smith@testcorp.com", manager.getEmail());
        assertEquals("securePassword123", manager.getPassword());
        assertEquals("Male", manager.getGender());
        assertEquals(company, manager.getCompany());
    }

    @Test
    @DisplayName("Manager default constructor should create empty object")
    void testManagerDefaultConstructor() {
        Manager emptyManager = new Manager();
        assertNotNull(emptyManager);
        assertNull(emptyManager.getFirstName());
    }

    @Test
    @DisplayName("setFirstName should update manager first name")
    void testSetFirstName() {
        manager.setFirstName("Jane");
        assertEquals("Jane", manager.getFirstName());
    }

    @Test
    @DisplayName("setLastName should update manager last name")
    void testSetLastName() {
        manager.setLastName("Doe");
        assertEquals("Doe", manager.getLastName());
    }

    @Test
    @DisplayName("setEmail should update manager email")
    void testSetEmail() {
        manager.setEmail("jane.doe@testcorp.com");
        assertEquals("jane.doe@testcorp.com", manager.getEmail());
    }

    @Test
    @DisplayName("setPassword should update manager password")
    void testSetPassword() {
        manager.setPassword("newPassword456");
        assertEquals("newPassword456", manager.getPassword());
    }

    @Test
    @DisplayName("setGender should update manager gender")
    void testSetGender() {
        manager.setGender("Female");
        assertEquals("Female", manager.getGender());
    }

    @Test
    @DisplayName("setCompany should update manager company")
    void testSetCompany() {
        Company newCompany = new Company("NewCorp", "456 New Street", "Sweden");
        manager.setCompany(newCompany);
        assertEquals(newCompany, manager.getCompany());
    }

    @Test
    @DisplayName("setId should update manager id")
    void testSetId() {
        manager.setId(50);
        assertEquals(50, manager.getId());
    }
}
