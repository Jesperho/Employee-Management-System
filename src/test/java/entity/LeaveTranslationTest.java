package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LeaveTranslationTest {

    private LeaveTranslation translation;
    private Leave leave;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        leave = new Leave(
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 6),
                "Vacation",
                "Family trip",
                employee
        );
        translation = new LeaveTranslation("en", "Vacation", leave, "01 March 2024", "06 March 2024", "6");
    }

    @Test
    @DisplayName("Constructor sets all fields")
    void testConstructor() {
        assertNotNull(translation);
        assertEquals("en", translation.getLanguageCode());
        assertEquals("Vacation", translation.getType());
        assertEquals(leave, translation.getLeave());
        assertEquals("01 March 2024", translation.getStartDateText());
        assertEquals("06 March 2024", translation.getEndDateText());
        assertEquals("6", translation.getDurationText());
    }

    @Test
    @DisplayName("Default constructor creates empty object")
    void testDefaultConstructor() {
        LeaveTranslation empty = new LeaveTranslation();
        assertNotNull(empty);
        assertNull(empty.getLanguageCode());
        assertNull(empty.getType());
        assertNull(empty.getLeave());
        assertNull(empty.getStartDateText());
        assertNull(empty.getEndDateText());
        assertNull(empty.getDurationText());
    }

    @Test
    @DisplayName("setId and getId work correctly")
    void testSetId() {
        translation.setId(42);
        assertEquals(42, translation.getId());
    }

    @Test
    @DisplayName("setLanguageCode updates language code")
    void testSetLanguageCode() {
        translation.setLanguageCode("ar");
        assertEquals("ar", translation.getLanguageCode());
    }

    @Test
    @DisplayName("setReason updates type field")
    void testSetReason() {
        translation.setReason("Sick Leave");
        assertEquals("Sick Leave", translation.getType());
    }

    @Test
    @DisplayName("setLeave updates the leave reference")
    void testSetLeave() {
        Leave newLeave = new Leave();
        translation.setLeave(newLeave);
        assertEquals(newLeave, translation.getLeave());
    }
}