package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChangeEmpTest {

    private ChangeEmp changeEmp;
    private Admin admin;
    private Employee employee;
    private Company company;
    private Department department;
    private LocalDate dateTime;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        admin = new Admin("admin@testcorp.com", "securePassword123", company);
        department = new Department(50, "IT", "IT Department", LocalDate.now(), company);
        employee = new Employee("John Doe", "Male", LocalDate.now(),
                55000.0, company, department, "Developer", "Full-time", "Senior");
        dateTime = LocalDate.of(2024, 6, 15);
        changeEmp = new ChangeEmp(dateTime, "Hired employee", admin, employee);
    }

    @Test
    @DisplayName("ChangeEmp should be created with constructor parameters")
    void testConstructor() {
        assertNotNull(changeEmp);
        assertEquals(dateTime, changeEmp.getDate());
        assertEquals("Hired employee", changeEmp.getMessage());
        assertEquals(admin, changeEmp.getAdmin());
        assertEquals(employee, changeEmp.getEmployee());
    }

    @Test
    @DisplayName("ChangeEmp default constructor should create empty object")
    void testDefaultConstructor() {
        ChangeEmp empty = new ChangeEmp();
        assertNotNull(empty);
        assertNull(empty.getDate());
        assertNull(empty.getMessage());
        assertNull(empty.getAdmin());
        assertNull(empty.getEmployee());
    }

    @Test
    @DisplayName("setDate should update date")
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2025, 1, 1);
        changeEmp.setDate(newDate);
        assertEquals(newDate, changeEmp.getDate());
    }

    @Test
    @DisplayName("setMessage should update message")
    void testSetMessage() {
        changeEmp.setMessage("Promoted employee");
        assertEquals("Promoted employee", changeEmp.getMessage());
    }

    @Test
    @DisplayName("setAdmin should update admin")
    void testSetAdmin() {
        Admin newAdmin = new Admin("new@testcorp.com", "pass123456", company);
        changeEmp.setAdmin(newAdmin);
        assertEquals(newAdmin, changeEmp.getAdmin());
    }

    @Test
    @DisplayName("setEmployee should update employee")
    void testSetEmployee() {
        Employee newEmployee = new Employee("Jane Doe", "Female", LocalDate.now(),
                65000.0, company, department, "Manager", "Full-time", "Lead");
        changeEmp.setEmployee(newEmployee);
        assertEquals(newEmployee, changeEmp.getEmployee());
    }
}