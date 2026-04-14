package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ChangeLeaveTest {

    private ChangeLeave changeLeave;
    private Admin admin;
    private Leave leave;
    private Company company;
    private Department department;
    private Employee employee;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        admin = new Admin("admin@testcorp.com", "securePassword123", company);
        department = new Department(50, "IT", "IT Department", LocalDate.now(), company);
        employee = new Employee("John Doe", "Male", LocalDate.now(),
                55000.0, company, department, "Developer", "Full-time", "Senior");
        leave = new Leave(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 14),
                "Annual", "Summer vacation", employee);
        date = LocalDate.of(2024, 6, 15);
        changeLeave = new ChangeLeave(date, "Approved leave", "", "", "", admin, leave);
    }

    @Test
    @DisplayName("ChangeLeave should be created with constructor parameters")
    void testConstructor() {
        assertNotNull(changeLeave);
        assertEquals(date, changeLeave.getDate());
        assertEquals("Approved leave", changeLeave.getMessage("en"));
        assertEquals(admin, changeLeave.getAdmin());
        assertEquals(leave, changeLeave.getLeave());
    }

    @Test
    @DisplayName("ChangeLeave default constructor should create empty object")
    void testDefaultConstructor() {
        ChangeLeave empty = new ChangeLeave();
        assertNotNull(empty);
        assertNull(empty.getDate());
        assertNull(empty.getMessage("en"));
        assertNull(empty.getAdmin());
        assertNull(empty.getLeave());
    }

    @Test
    @DisplayName("setDate should update date")
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2025, 1, 1);
        changeLeave.setDate(newDate);
        assertEquals(newDate, changeLeave.getDate());
    }

    @Test
    @DisplayName("setMessageEn should update English message")
    void testSetMessage() {
        changeLeave.setMessageEn("Rejected leave");
        assertEquals("Rejected leave", changeLeave.getMessage("en"));
    }

    @Test
    @DisplayName("setAdmin should update admin")
    void testSetAdmin() {
        Admin newAdmin = new Admin("new@testcorp.com", "pass123456", company);
        changeLeave.setAdmin(newAdmin);
        assertEquals(newAdmin, changeLeave.getAdmin());
    }

    @Test
    @DisplayName("setLeave should update leave")
    void testSetLeave() {
        Leave newLeave = new Leave(LocalDate.of(2024, 12, 20), LocalDate.of(2024, 12, 31),
                "Sick", "Medical leave", employee);
        changeLeave.setLeave(newLeave);
        assertEquals(newLeave, changeLeave.getLeave());
    }
}
