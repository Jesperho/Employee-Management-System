package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTranslationTest {

    private EmployeeTranslation translation;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        translation = new EmployeeTranslation(
                "en", "Senior Developer", "Full-time", "Male", employee, "15 January 2023", "5500.0"
        );
    }

    @Test
    @DisplayName("Constructor sets all fields")
    void testConstructor() {
        assertNotNull(translation);
        assertEquals("en", translation.getLanguage_code());
        assertEquals("Senior Developer", translation.getPosition());
        assertEquals("Full-time", translation.getContractType());
        assertEquals("Male", translation.getGender());
        assertEquals("15 January 2023", translation.getStartDate());
        assertEquals("5500.0", translation.getSalary());
    }

    @Test
    @DisplayName("Default constructor creates empty object")
    void testDefaultConstructor() {
        EmployeeTranslation empty = new EmployeeTranslation();
        assertNotNull(empty);
        assertNull(empty.getLanguage_code());
        assertNull(empty.getPosition());
        assertNull(empty.getContractType());
        assertNull(empty.getGender());
        assertNull(empty.getStartDate());
        assertNull(empty.getSalary());
    }

    @Test
    @DisplayName("setId and getId work correctly")
    void testSetId() {
        translation.setId(10);
        assertEquals(10, translation.getId());
    }

    @Test
    @DisplayName("setGender updates gender")
    void testSetGender() {
        translation.setGender("Female");
        assertEquals("Female", translation.getGender());
    }

    @Test
    @DisplayName("setPosition updates position")
    void testSetPosition() {
        translation.setPosition("Lead Developer");
        assertEquals("Lead Developer", translation.getPosition());
    }

    @Test
    @DisplayName("setContractType updates contract type")
    void testSetContractType() {
        translation.setContractType("Part-time");
        assertEquals("Part-time", translation.getContractType());
    }

    @Test
    @DisplayName("setLanguage_code updates language code")
    void testSetLanguageCode() {
        translation.setLanguage_code("es");
        assertEquals("es", translation.getLanguage_code());
    }
}