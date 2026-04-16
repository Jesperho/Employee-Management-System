package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LeaveTest {

    private Leave leave;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();

        leave = new Leave(
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 6),
                "Vacation",
                "Family trip to Spain",
                employee
        );
    }

    @Test
    @DisplayName("Leave should be created with constructor parameters")
    void testLeaveConstructor() {
        assertNotNull(leave);
        assertEquals(LocalDate.of(2024, 3, 1), leave.getStartDate());
        assertEquals(LocalDate.of(2024, 3, 6), leave.getEndDate());
        assertEquals("Vacation", leave.getType());
        assertEquals("Family trip to Spain", leave.getReason());
        assertEquals(employee, leave.getEmployee());

        // NEW: duration should be 6 days (inclusive)
        assertEquals(6, leave.getDuration());
    }

    @Test
    @DisplayName("Leave default constructor should create empty object")
    void testLeaveDefaultConstructor() {
        Leave emptyLeave = new Leave();
        assertNotNull(emptyLeave);
        assertNull(emptyLeave.getStartDate());
        assertNull(emptyLeave.getEndDate());
        assertNull(emptyLeave.getType());
        assertNull(emptyLeave.getReason());
        assertNull(emptyLeave.getEmployee());
        assertNull(emptyLeave.getDuration());
    }

    @Test
    @DisplayName("setStartDate should update leave start date and duration")
    void testSetStartDate() {
        leave.setStartDate(LocalDate.of(2024, 3, 2));
        assertEquals(LocalDate.of(2024, 3, 2), leave.getStartDate());

        // NEW: duration should update
        assertEquals(5, leave.getDuration());
    }

    @Test
    @DisplayName("setEndDate should update leave end date and duration")
    void testSetEndDate() {
        leave.setEndDate(LocalDate.of(2024, 3, 10));
        assertEquals(LocalDate.of(2024, 3, 10), leave.getEndDate());

        // NEW: duration should update
        assertEquals(10, leave.getDuration());
    }

    @Test
    @DisplayName("setType should update leave type")
    void testSetType() {
        leave.setType("Sick Leave");
        assertEquals("Sick Leave", leave.getType());
    }

    @Test
    @DisplayName("setReason should update leave reason")
    void testSetReason() {
        leave.setReason("Medical appointment");
        assertEquals("Medical appointment", leave.getReason());
    }

    @Test
    @DisplayName("setEmployee should update leave employee")
    void testSetEmployee() {
        Employee newEmployee = new Employee();
        leave.setEmployee(newEmployee);
        assertEquals(newEmployee, leave.getEmployee());
    }

    @Test
    @DisplayName("setId should update leave id")
    void testSetId() {
        leave.setId(100);
        assertEquals(100, leave.getId());
    }

    @Test
    @DisplayName("getTranslation returns matching translation by language code")
    void testGetTranslation() throws Exception {
        LeaveTranslation t = new LeaveTranslation("es", "Vacaciones", leave, "01 marzo 2024", "06 marzo 2024", "6");
        addTranslation(leave, t);

        LeaveTranslation found = leave.getTranslation("es");
        assertNotNull(found);
        assertEquals("es", found.getLanguageCode());
    }

    @Test
    @DisplayName("getTranslation returns null when no match")
    void testGetTranslationNotFound() {
        assertNull(leave.getTranslation("ar"));
    }

    @Test
    @DisplayName("getTranslatedType returns translated value when translation exists")
    void testGetTranslatedType() throws Exception {
        LeaveTranslation t = new LeaveTranslation("es", "Vacaciones", leave, "01 marzo 2024", "06 marzo 2024", "6");
        addTranslation(leave, t);

        assertEquals("Vacaciones", leave.getTranslatedType("es"));
    }

    @Test
    @DisplayName("getTranslatedType falls back to raw type when no translation")
    void testGetTranslatedTypeFallback() {
        assertEquals("Vacation", leave.getTranslatedType("ar"));
    }

    @Test
    @DisplayName("getTranslatedStartDate returns text when translation exists")
    void testGetTranslatedStartDate() throws Exception {
        LeaveTranslation t = new LeaveTranslation("es", "Vacaciones", leave, "01 marzo 2024", "06 marzo 2024", "6");
        addTranslation(leave, t);

        assertEquals("01 marzo 2024", leave.getTranslatedStartDate("es"));
    }

    @Test
    @DisplayName("getTranslatedStartDate returns empty string when no translation")
    void testGetTranslatedStartDateFallback() {
        assertEquals("", leave.getTranslatedStartDate("ar"));
    }

    @Test
    @DisplayName("getTranslatedEndDate returns text when translation exists")
    void testGetTranslatedEndDate() throws Exception {
        LeaveTranslation t = new LeaveTranslation("es", "Vacaciones", leave, "01 marzo 2024", "06 marzo 2024", "6");
        addTranslation(leave, t);

        assertEquals("06 marzo 2024", leave.getTranslatedEndDate("es"));
    }

    @Test
    @DisplayName("getTranslatedEndDate returns empty string when no translation")
    void testGetTranslatedEndDateFallback() {
        assertEquals("", leave.getTranslatedEndDate("ar"));
    }

    @Test
    @DisplayName("getTranslatedDuration returns text when translation exists")
    void testGetTranslatedDuration() throws Exception {
        LeaveTranslation t = new LeaveTranslation("es", "Vacaciones", leave, "01 marzo 2024", "06 marzo 2024", "6");
        addTranslation(leave, t);

        assertEquals("6", leave.getTranslatedDuration("es"));
    }

    @Test
    @DisplayName("getTranslatedDuration returns empty string when no translation")
    void testGetTranslatedDurationFallback() {
        assertEquals("", leave.getTranslatedDuration("ar"));
    }

    private void addTranslation(Leave l, LeaveTranslation translation) throws Exception {
        java.lang.reflect.Field field = Leave.class.getDeclaredField("translations");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<LeaveTranslation> translations = (java.util.List<LeaveTranslation>) field.get(l);
        translations.add(translation);
    }
}
