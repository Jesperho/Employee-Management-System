package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChangeLeaveTest {

    private ChangeLeave changeLeave;
    private Admin admin;
    private Leave leave;
    private Company company;
    private Department department;
    private Employee employee;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        admin = new Admin("admin@testcorp.com", "securePassword123", company);
        department = new Department(50, "IT", "IT Department", LocalDate.now(), company);
        employee = new Employee("John Doe", "Male", "Developer", LocalDate.now(),
                55000.0, company, department, "Developer", "Full-time", "Senior");
        leave = new Leave(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 14),
                "Annual", "Summer vacation", employee);
        dateTime = LocalDateTime.of(2024, 6, 15, 10, 30);
        changeLeave = new ChangeLeave(dateTime, "Approved leave", admin, leave);
    }

    @Test
    @DisplayName("ChangeLeave should be created with constructor parameters")
    void testConstructor() {
        assertNotNull(changeLeave);
        assertEquals(dateTime, changeLeave.getDate());
        assertEquals("Approved leave", changeLeave.getMessage());
        assertEquals(admin, changeLeave.getAdmin());
        assertEquals(leave, changeLeave.getLeave());
    }

    @Test
    @DisplayName("ChangeLeave default constructor should create empty object")
    void testDefaultConstructor() {
        ChangeLeave empty = new ChangeLeave();
        assertNotNull(empty);
        assertNull(empty.getDate());
        assertNull(empty.getMessage());
        assertNull(empty.getAdmin());
        assertNull(empty.getLeave());
    }

    @Test
    @DisplayName("setDate should update date")
    void testSetDate() {
        LocalDateTime newDate = LocalDateTime.of(2025, 1, 1, 12, 0);
        changeLeave.setDate(newDate);
        assertEquals(newDate, changeLeave.getDate());
    }

    @Test
    @DisplayName("setMessage should update message")
    void testSetMessage() {
        changeLeave.setMessage("Rejected leave");
        assertEquals("Rejected leave", changeLeave.getMessage());
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