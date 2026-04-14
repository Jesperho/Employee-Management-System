package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTranslationTest {

    private DepartmentTranslation translation;
    private Department department;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Street", "Finland");
        department = new Department(50, "IT", "IT Department", LocalDate.of(2022, 1, 1), company);
        translation = new DepartmentTranslation("en", "IT", department, "01 January 2022", 50);
    }

    @Test
    @DisplayName("Constructor sets all fields")
    void testConstructor() {
        assertNotNull(translation);
        assertEquals("en", translation.getLanguageCode());
        assertEquals("IT", translation.getDepartmentName());
        assertEquals(department, translation.getDepartment());
        assertEquals("01 January 2022", translation.getCreationDateText());
        assertEquals(50, translation.getCapacity());
    }

    @Test
    @DisplayName("Default constructor creates empty object")
    void testDefaultConstructor() {
        DepartmentTranslation empty = new DepartmentTranslation();
        assertNotNull(empty);
        assertNull(empty.getLanguageCode());
        assertNull(empty.getDepartmentName());
        assertNull(empty.getDepartment());
        assertNull(empty.getCreationDateText());
        assertEquals(0, empty.getCapacity());
    }

    @Test
    @DisplayName("setId and getId work correctly")
    void testSetId() {
        translation.setId(99);
        assertEquals(99, translation.getId());
    }

    @Test
    @DisplayName("setLanguageCode updates language code")
    void testSetLanguageCode() {
        translation.setLanguageCode("ar");
        assertEquals("ar", translation.getLanguageCode());
    }

    @Test
    @DisplayName("setDepartmentName updates name")
    void testSetDepartmentName() {
        translation.setDepartmentName("Human Resources");
        assertEquals("Human Resources", translation.getDepartmentName());
    }

    @Test
    @DisplayName("setDepartment updates department reference")
    void testSetDepartment() {
        Department newDept = new Department(10, "HR", "HR Dept", LocalDate.now(), company);
        translation.setDepartment(newDept);
        assertEquals(newDept, translation.getDepartment());
    }
}