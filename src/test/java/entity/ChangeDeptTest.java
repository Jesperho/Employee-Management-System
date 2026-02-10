package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChangeDeptTest {

    private ChangeDept changeDept;
    private Admin admin;
    private Department department;
    private Company company;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        admin = new Admin("admin@testcorp.com", "securePassword123", company);
        department = new Department(50, "IT", "IT Department", LocalDate.now(), company);
        dateTime = LocalDateTime.of(2024, 6, 15, 10, 30);
        changeDept = new ChangeDept(dateTime, "Created department", admin, department);
    }

    @Test
    @DisplayName("ChangeDept should be created with constructor parameters")
    void testConstructor() {
        assertNotNull(changeDept);
        assertEquals(dateTime, changeDept.getDate());
        assertEquals("Created department", changeDept.getMessage());
        assertEquals(admin, changeDept.getAdmin());
        assertEquals(department, changeDept.getDepartment());
    }

    @Test
    @DisplayName("ChangeDept default constructor should create empty object")
    void testDefaultConstructor() {
        ChangeDept empty = new ChangeDept();
        assertNotNull(empty);
        assertNull(empty.getDate());
        assertNull(empty.getMessage());
        assertNull(empty.getAdmin());
        assertNull(empty.getDepartment());
    }

    @Test
    @DisplayName("setDate should update date")
    void testSetDate() {
        LocalDateTime newDate = LocalDateTime.of(2025, 1, 1, 12, 0);
        changeDept.setDate(newDate);
        assertEquals(newDate, changeDept.getDate());
    }

    @Test
    @DisplayName("setMessage should update message")
    void testSetMessage() {
        changeDept.setMessage("Updated department name");
        assertEquals("Updated department name", changeDept.getMessage());
    }

    @Test
    @DisplayName("setAdmin should update admin")
    void testSetAdmin() {
        Admin newAdmin = new Admin("new@testcorp.com", "pass123456", company);
        changeDept.setAdmin(newAdmin);
        assertEquals(newAdmin, changeDept.getAdmin());
    }

    @Test
    @DisplayName("setDepartment should update department")
    void testSetDepartment() {
        Department newDept = new Department(100, "HR", "Human Resources", LocalDate.now(), company);
        changeDept.setDepartment(newDept);
        assertEquals(newDept, changeDept.getDepartment());
    }
}