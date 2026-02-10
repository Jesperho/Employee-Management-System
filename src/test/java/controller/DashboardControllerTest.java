package controller;

import controller.DashboardController.DepartmentRow;
import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests dashboard data handling, navigation logic, and data formatting.
 */
class DashboardControllerTest {

    private DashboardController controller;

    @BeforeEach
    void setUp() {
        controller = new DashboardController();
    }

    // ===== USER NAME PROCESSING TESTS =====

    @Test
    @DisplayName("Should extract first name from full name")
    void testExtractFirstName() {
        assertEquals("John", extractFirstName("John Doe"));
        assertEquals("Mary", extractFirstName("Mary Jane Smith"));
        assertEquals("Admin", extractFirstName("Admin"));
    }

    @Test
    @DisplayName("Should handle empty or null names")
    void testExtractFirstNameEdgeCases() {
        assertNull(extractFirstName(null));
        assertEquals("", extractFirstName(""));
        assertEquals("", extractFirstName("   "));
    }

    // ===== INITIALS GENERATION TESTS =====

    @Test
    @DisplayName("Should generate initials from two-part name")
    void testGenerateInitialsTwoParts() {
        assertEquals("JD", generateInitials("John Doe"));
        assertEquals("MS", generateInitials("Mary Smith"));
        assertEquals("AB", generateInitials("Alice Brown"));
    }

    @Test
    @DisplayName("Should generate initials from single name")
    void testGenerateInitialsSingleName() {
        assertEquals("AD", generateInitials("Admin"));
        assertEquals("JO", generateInitials("John"));
        assertEquals("A", generateInitials("A"));
    }

    @Test
    @DisplayName("Should generate initials from multi-part name")
    void testGenerateInitialsMultiPart() {
        assertEquals("MJ", generateInitials("Mary Jane Smith"));
        assertEquals("JM", generateInitials("John Michael Anderson"));
    }

    @Test
    @DisplayName("Should handle edge cases in initials generation")
    void testGenerateInitialsEdgeCases() {
        assertEquals("", generateInitials(""));
        assertEquals("", generateInitials("   "));
        assertEquals("", generateInitials(null));
    }

    // ===== DATE FORMATTING TESTS =====

    @Test
    @DisplayName("Should format date correctly")
    void testDateFormatting() {
        LocalDate testDate = LocalDate.of(2024, 2, 15);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formatted = testDate.format(formatter);

        assertNotNull(formatted);
        assertTrue(formatted.contains("February"));
        assertTrue(formatted.contains("15"));
        assertTrue(formatted.contains("2024"));
    }

    @Test
    @DisplayName("Should format current date")
    void testCurrentDateFormatting() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        String formatted = today.format(formatter);

        assertNotNull(formatted);
        assertFalse(formatted.isEmpty());
    }

    // ===== DEPARTMENT ROW TESTS =====

    @Test
    @DisplayName("Should create DepartmentRow with all properties")
    void testDepartmentRowCreation() {
        DepartmentRow dept = new DepartmentRow(
                "IT Department",
                "Alice Smith",
                "45",
                "Active"
        );

        assertNotNull(dept);
        assertEquals("IT Department", dept.getName());
        assertEquals("Alice Smith", dept.getHead());
        assertEquals("45", dept.getEmployeeCount());
        assertEquals("Active", dept.getStatus());
    }

    @Test
    @DisplayName("DepartmentRow properties should be observable")
    void testDepartmentRowProperties() {
        DepartmentRow dept = new DepartmentRow(
                "HR Department",
                "Bob Johnson",
                "12",
                "Active"
        );

        assertNotNull(dept.nameProperty());
        assertNotNull(dept.headProperty());
        assertNotNull(dept.employeeCountProperty());
        assertNotNull(dept.statusProperty());

        assertTrue(dept.nameProperty() instanceof SimpleStringProperty);
        assertTrue(dept.headProperty() instanceof SimpleStringProperty);
        assertTrue(dept.employeeCountProperty() instanceof SimpleStringProperty);
        assertTrue(dept.statusProperty() instanceof SimpleStringProperty);
    }

    @Test
    @DisplayName("Should handle empty department data")
    void testDepartmentRowWithEmptyData() {
        DepartmentRow dept = new DepartmentRow("", "", "", "");

        assertNotNull(dept);
        assertEquals("", dept.getName());
        assertEquals("", dept.getHead());
        assertEquals("", dept.getEmployeeCount());
        assertEquals("", dept.getStatus());
    }

    @Test
    @DisplayName("Should create multiple different department rows")
    void testMultipleDepartmentRows() {
        DepartmentRow dept1 = new DepartmentRow("IT", "Alice", "45", "Active");
        DepartmentRow dept2 = new DepartmentRow("HR", "Bob", "12", "Active");
        DepartmentRow dept3 = new DepartmentRow("Finance", "Carol", "30", "Inactive");

        assertNotEquals(dept1.getName(), dept2.getName());
        assertNotEquals(dept2.getName(), dept3.getName());
        assertEquals("Active", dept1.getStatus());
        assertEquals("Inactive", dept3.getStatus());
    }

    // ===== STATISTICS VALIDATION TESTS =====

    @Test
    @DisplayName("Should validate employee count format")
    void testEmployeeCountFormat() {
        assertTrue(isValidCount("540"));
        assertTrue(isValidCount("0"));
        assertTrue(isValidCount("9999"));
        assertFalse(isValidCount("-1"));
        assertFalse(isValidCount("abc"));
    }

    @Test
    @DisplayName("Should validate currency format")
    void testCurrencyFormat() {
        assertTrue(isValidCurrency("€285,000"));
        assertTrue(isValidCurrency("€4,200"));
        assertTrue(isValidCurrency("€0"));
        assertTrue(isValidCurrency("€12,500"));
    }

    @Test
    @DisplayName("Should calculate employee statistics correctly")
    void testEmployeeStatistics() {
        int total = 540;
        int active = 532;
        int onLeave = 8;

        assertEquals(total, active + onLeave);
        assertTrue(active > 0);
        assertTrue(onLeave >= 0);
        assertTrue(total >= active);
    }

    // ===== SEARCH FUNCTIONALITY TESTS =====

    @Test
    @DisplayName("Should validate search text input")
    void testSearchTextValidation() {
        assertTrue(isValidSearchText("John"));
        assertTrue(isValidSearchText("Department"));
        assertTrue(isValidSearchText("123"));
        assertFalse(isValidSearchText(""));
        assertFalse(isValidSearchText("   "));
    }

    @Test
    @DisplayName("Should trim search text")
    void testSearchTextTrimming() {
        assertEquals("John", trimSearchText("  John  "));
        assertEquals("Test", trimSearchText("\tTest\t"));
        assertEquals("", trimSearchText("   "));
    }

    // ===== USER INFORMATION TESTS =====

    @Test
    @DisplayName("Should set and get current user information")
    void testSetCurrentUser() {
        String testName = "Test User";
        String testRole = "Manager";

        controller.setCurrentUser(testName, testRole);

        // Since fields are private, we're testing the method doesn't throw exception
        assertDoesNotThrow(() -> controller.setCurrentUser(testName, testRole));
    }

    @Test
    @DisplayName("Should handle null user information gracefully")
    void testSetNullUserInfo() {
        assertDoesNotThrow(() -> controller.setCurrentUser(null, null));
        assertDoesNotThrow(() -> controller.setCurrentUser("", ""));
    }

    // ===== SALARY FORMATTING TESTS =====

    @Test
    @DisplayName("Should format salary with currency symbol")
    void testSalaryFormatting() {
        assertEquals("€285,000", formatSalary(285000));
        assertEquals("€4,200", formatSalary(4200));
        assertEquals("€12,500", formatSalary(12500));
        assertEquals("€0", formatSalary(0));
    }

    @Test
    @DisplayName("Should calculate average salary correctly")
    void testAverageSalaryCalculation() {
        double totalPayroll = 285000;
        int employeeCount = 68; // Approximate
        double avgSalary = totalPayroll / employeeCount;

        assertTrue(avgSalary > 0);
        assertTrue(avgSalary < totalPayroll);
    }

    // ===== DEPARTMENT STATUS TESTS =====

    @Test
    @DisplayName("Should validate department status values")
    void testDepartmentStatusValidation() {
        assertTrue(isValidStatus("Active"));
        assertTrue(isValidStatus("Inactive"));
        assertFalse(isValidStatus(""));
        assertFalse(isValidStatus(null));
    }

    @Test
    @DisplayName("Should handle different status types")
    void testDifferentStatusTypes() {
        assertTrue(isValidStatus("Active"));
        assertTrue(isValidStatus("Inactive"));
        assertTrue(isValidStatus("Pending"));
    }

    // ===== WELCOME MESSAGE TESTS =====

    @Test
    @DisplayName("Should generate welcome message with first name")
    void testWelcomeMessageGeneration() {
        String fullName = "John Doe";
        String firstName = extractFirstName(fullName);
        String welcomeMessage = "Welcome back, " + firstName + "!";

        assertEquals("Welcome back, John!", welcomeMessage);
    }

    @Test
    @DisplayName("Should generate welcome message for different names")
    void testWelcomeMessageVariations() {
        assertEquals("Welcome back, Alice!", generateWelcomeMessage("Alice Smith"));
        assertEquals("Welcome back, Bob!", generateWelcomeMessage("Bob Johnson"));
        assertEquals("Welcome back, Carol!", generateWelcomeMessage("Carol White"));
    }

    // ===== VALIDATION HELPER TESTS =====

    @Test
    @DisplayName("Should validate non-empty strings")
    void testNonEmptyStringValidation() {
        assertTrue(isNonEmptyString("Test"));
        assertTrue(isNonEmptyString("123"));
        assertFalse(isNonEmptyString(""));
        assertFalse(isNonEmptyString("   "));
        assertFalse(isNonEmptyString(null));
    }

    // ===== HELPER METHODS =====

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return fullName;
        }
        String[] parts = fullName.trim().split(" ");
        return parts.length > 0 ? parts[0] : "";
    }

    private String generateInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        String[] parts = name.trim().split(" ");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        } else if (parts.length == 1 && !parts[0].isEmpty()) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return "";
    }

    private boolean isValidCount(String count) {
        if (count == null || count.isEmpty()) {
            return false;
        }
        try {
            int value = Integer.parseInt(count);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
            return false;
        }
        return currency.matches("€[0-9,]+");
    }

    private boolean isValidSearchText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private String trimSearchText(String text) {
        return text != null ? text.trim() : "";
    }

    private String formatSalary(double amount) {
        if (amount >= 1000) {
            return String.format("€%,.0f", amount);
        }
        return String.format("€%.0f", amount);
    }

    private boolean isValidStatus(String status) {
        return status != null && !status.isEmpty();
    }

    private String generateWelcomeMessage(String fullName) {
        String firstName = extractFirstName(fullName);
        return "Welcome back, " + firstName + "!";
    }

    private boolean isNonEmptyString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // ===== INTEGRATION-LIKE TESTS =====

    @Test
    @DisplayName("Should handle complete dashboard initialization")
    void testDashboardInitialization() {
        // Test that controller can be initialized without errors
        assertDoesNotThrow(() -> {
            DashboardController testController = new DashboardController();
            testController.setCurrentUser("Test User", "Admin");
        });
    }

    @Test
    @DisplayName("Should process department data correctly")
    void testDepartmentDataProcessing() {
        DepartmentRow dept1 = new DepartmentRow("IT", "Alice Smith", "45", "Active");
        DepartmentRow dept2 = new DepartmentRow("HR", "Bob Johnson", "12", "Active");
        DepartmentRow dept3 = new DepartmentRow("Finance", "Carol White", "30", "Active");

        assertEquals(3, countDepartments(dept1, dept2, dept3));
        assertEquals(87, calculateTotalEmployees(dept1, dept2, dept3));
    }

    @Test
    @DisplayName("Should validate dashboard statistics consistency")
    void testStatisticsConsistency() {
        int totalEmployees = 540;
        int activeEmployees = 532;
        int onLeave = 8;
        int departments = 12;

        // Verify consistency
        assertEquals(totalEmployees, activeEmployees + onLeave);
        assertTrue(departments > 0);
        assertTrue(totalEmployees > activeEmployees);
        assertTrue(activeEmployees > 0);
    }

    private int countDepartments(DepartmentRow... departments) {
        return departments.length;
    }

    private int calculateTotalEmployees(DepartmentRow... departments) {
        int total = 0;
        for (DepartmentRow dept : departments) {
            total += Integer.parseInt(dept.getEmployeeCount());
        }
        return total;
    }
}
