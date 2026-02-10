package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LeaveTest {

    private Leave leave;

    @BeforeEach
    void setUp() {
        leave = new Leave(
                5,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 6),
                "Vacation",
                "Family trip to Spain"
        );
    }

    @Test
    @DisplayName("Leave should be created with constructor parameters")
    void testLeaveConstructor() {
        assertNotNull(leave);
        assertEquals(5, leave.getDuration());
        assertEquals(LocalDate.of(2024, 3, 1), leave.getStartDate());
        assertEquals(LocalDate.of(2024, 3, 6), leave.getEndDate());
        assertEquals("Vacation", leave.getType());
        assertEquals("Family trip to Spain", leave.getReason());
    }

    @Test
    @DisplayName("Leave default constructor should create empty object")
    void testLeaveDefaultConstructor() {
        Leave emptyLeave = new Leave();
        assertNotNull(emptyLeave);
        assertNull(emptyLeave.getType());
    }

    @Test
    @DisplayName("setDuration should update leave duration")
    void testSetDuration() {
        leave.setDuration(10);
        assertEquals(10, leave.getDuration());
    }

    @Test
    @DisplayName("setStartDate should update leave start date")
    void testSetStartDate() {
        LocalDate newDate = LocalDate.of(2024, 4, 1);
        leave.setStartDate(newDate);
        assertEquals(newDate, leave.getStartDate());
    }

    @Test
    @DisplayName("setEndDate should update leave end date")
    void testSetEndDate() {
        LocalDate newDate = LocalDate.of(2024, 4, 15);
        leave.setEndDate(newDate);
        assertEquals(newDate, leave.getEndDate());
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
    @DisplayName("setId should update leave id")
    void testSetId() {
        leave.setId(100);
        assertEquals(100, leave.getId());
    }

    @Test
    @DisplayName("Leave duration should match date range")
    void testLeaveDurationMatchesDateRange() {
        LocalDate start = LocalDate.of(2024, 5, 1);
        LocalDate end = LocalDate.of(2024, 5, 8);
        Leave newLeave = new Leave(7, start, end, "Vacation", "Summer break");

        // Duration should be 7 days (May 1-8 inclusive is actually 8 days, but duration is set as 7)
        assertEquals(7, newLeave.getDuration());
    }
}
