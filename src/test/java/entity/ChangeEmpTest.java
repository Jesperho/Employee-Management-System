package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ChangeEmpTest {

    private ChangeEmp changeEmp;
    private Admin admin;
    private Employee employee;
    private Company company;
    private Department department;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        admin = new Admin("admin@testcorp.com", "securePassword123", company);
        department = new Department(50, "IT", "IT Department", LocalDate.now(), company);
        employee = new Employee("John Doe", "Male", LocalDate.now(),
                55000.0, company, department, "Developer", "Full-time", "Senior");
        date = LocalDate.of(2024, 6, 15);
        changeEmp = new ChangeEmp(date, "Hired employee", "", "", "", admin, employee);
    }

    @Test
    @DisplayName("ChangeEmp should be created with constructor parameters")
    void testConstructor() {
        assertNotNull(changeEmp);
        assertEquals(date, changeEmp.getDate());
        assertEquals("Hired employee", changeEmp.getMessage("en"));
        assertEquals(admin, changeEmp.getAdmin());
        assertEquals(employee, changeEmp.getEmployee());
    }

    @Test
    @DisplayName("ChangeEmp default constructor should create empty object")
    void testDefaultConstructor() {
        ChangeEmp empty = new ChangeEmp();
        assertNotNull(empty);
        assertNull(empty.getDate());
        assertNull(empty.getMessage("en"));
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
    @DisplayName("setMessageEn should update English message")
    void testSetMessage() {
        changeEmp.setMessageEn("Promoted employee");
        assertEquals("Promoted employee", changeEmp.getMessage("en"));
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

    @Test
    @DisplayName("getMessage returns correct translation for each language code")
    void testGetMessageAllLanguages() {
        changeEmp = new ChangeEmp(date, "en-msg", "fa-msg", "ar-msg", "es-msg", admin, employee);
        assertEquals("en-msg", changeEmp.getMessage("en"));
        assertEquals("fa-msg", changeEmp.getMessage("fa"));
        assertEquals("ar-msg", changeEmp.getMessage("ar"));
        assertEquals("es-msg", changeEmp.getMessage("es"));
        assertEquals("en-msg", changeEmp.getMessage("unknown"));
    }

    @Test
    @DisplayName("setMessageFa and getMessageFa work correctly")
    void testSetMessageFa() {
        changeEmp.setMessageFa("استخدام کارمند");
        assertEquals("استخدام کارمند", changeEmp.getMessageFa());
    }

    @Test
    @DisplayName("setMessageAr and getMessageAr work correctly")
    void testSetMessageAr() {
        changeEmp.setMessageAr("توظيف موظف");
        assertEquals("توظيف موظف", changeEmp.getMessageAr());
    }

    @Test
    @DisplayName("setMessageEs and getMessageEs work correctly")
    void testSetMessageEs() {
        changeEmp.setMessageEs("Empleado contratado");
        assertEquals("Empleado contratado", changeEmp.getMessageEs());
    }

    @Test
    @DisplayName("getId returns null for unsaved entity")
    void testGetId() {
        assertNull(changeEmp.getId());
    }

    @Test
    @DisplayName("getMessageEn returns the English message")
    void testGetMessageEn() {
        assertEquals("Hired employee", changeEmp.getMessageEn());
    }
}
