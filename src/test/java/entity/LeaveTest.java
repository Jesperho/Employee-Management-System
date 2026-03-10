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
}
