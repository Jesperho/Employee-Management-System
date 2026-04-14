package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ChangeDeptTest {

    private ChangeDept changeDept;
    private Admin admin;
    private Department department;
    private Company company;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        admin = new Admin("admin@testcorp.com", "securePassword123", company);
        department = new Department(50, "IT", "IT Department", LocalDate.now(), company);
        date = LocalDate.of(2024, 6, 15);
        changeDept = new ChangeDept(date, "Created department", "", "", "", admin, department);
    }

    @Test
    @DisplayName("ChangeDept should be created with constructor parameters")
    void testConstructor() {
        assertNotNull(changeDept);
        assertEquals(date, changeDept.getDate());
        assertEquals("Created department", changeDept.getMessage("en"));
        assertEquals(admin, changeDept.getAdmin());
        assertEquals(department, changeDept.getDepartment());
    }

    @Test
    @DisplayName("ChangeDept default constructor should create empty object")
    void testDefaultConstructor() {
        ChangeDept empty = new ChangeDept();
        assertNotNull(empty);
        assertNull(empty.getDate());
        assertNull(empty.getMessage("en"));
        assertNull(empty.getAdmin());
        assertNull(empty.getDepartment());
    }

    @Test
    @DisplayName("setDate should update date")
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2025, 1, 1);
        changeDept.setDate(newDate);
        assertEquals(newDate, changeDept.getDate());
    }

    @Test
    @DisplayName("setMessageEn should update English message")
    void testSetMessage() {
        changeDept.setMessageEn("Updated department name");
        assertEquals("Updated department name", changeDept.getMessage("en"));
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

    @Test
    @DisplayName("getMessage should return correct translation for each language code")
    void testGetMessageAllLanguages() {
        changeDept = new ChangeDept(date, "en-msg", "fa-msg", "ar-msg", "es-msg", admin, department);
        assertEquals("en-msg", changeDept.getMessage("en"));
        assertEquals("fa-msg", changeDept.getMessage("fa"));
        assertEquals("ar-msg", changeDept.getMessage("ar"));
        assertEquals("es-msg", changeDept.getMessage("es"));
        assertEquals("en-msg", changeDept.getMessage("unknown"));
    }

    @Test
    @DisplayName("setMessageFa and getMessageFa work correctly")
    void testSetMessageFa() {
        changeDept.setMessageFa("تغییر بخش");
        assertEquals("تغییر بخش", changeDept.getMessageFa());
    }

    @Test
    @DisplayName("setMessageAr and getMessageAr work correctly")
    void testSetMessageAr() {
        changeDept.setMessageAr("تغيير القسم");
        assertEquals("تغيير القسم", changeDept.getMessageAr());
    }

    @Test
    @DisplayName("setMessageEs and getMessageEs work correctly")
    void testSetMessageEs() {
        changeDept.setMessageEs("Cambio de departamento");
        assertEquals("Cambio de departamento", changeDept.getMessageEs());
    }

    @Test
    @DisplayName("getId returns null for unsaved entity")
    void testGetId() {
        assertNull(changeDept.getId());
    }

    @Test
    @DisplayName("getMessageEn returns the English message")
    void testGetMessageEn() {
        assertEquals("Created department", changeDept.getMessageEn());
    }
}
