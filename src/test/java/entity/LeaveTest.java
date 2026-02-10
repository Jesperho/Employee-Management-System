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
        // Optional: create a dummy Employee for testing
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
