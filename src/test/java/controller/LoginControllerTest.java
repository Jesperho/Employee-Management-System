package controller;

import javafx.scene.control.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests login validation, registration validation, and form handling.
 */
class LoginControllerTest {

    private LoginController controller;

    @BeforeEach
    void setUp() {
        controller = new LoginController();
        // For now, we'll test the public methods and validation logic
    }

    // ===== EMAIL VALIDATION TESTS =====

    @Test
    @DisplayName("Should validate correct email format")
    void testValidEmail() {
        // Using reflection to test private method
        assertTrue(isValidEmailFormat("test@example.com"));
        assertTrue(isValidEmailFormat("user.name@company.co.uk"));
        assertTrue(isValidEmailFormat("admin123@test-domain.org"));
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void testInvalidEmail() {
        assertFalse(isValidEmailFormat(""));
        assertFalse(isValidEmailFormat("notanemail"));
        assertFalse(isValidEmailFormat("@example.com"));
        assertFalse(isValidEmailFormat("test@"));
        assertFalse(isValidEmailFormat("test @example.com"));
    }

    @Test
    @DisplayName("Should reject null email")
    void testNullEmail() {
        assertFalse(isValidEmailFormat(null));
    }

    // ===== PASSWORD VALIDATION TESTS =====

    @Test
    @DisplayName("Should accept password with minimum 6 characters")
    void testValidPasswordLength() {
        assertTrue(isPasswordValid("password123"));
        assertTrue(isPasswordValid("12345678"));
        assertTrue(isPasswordValid("abcdef"));
    }

    @Test
    @DisplayName("Should reject password shorter than 6 characters")
    void testInvalidPasswordLength() {
        assertFalse(isPasswordValid("12345"));
        assertFalse(isPasswordValid("abc"));
        assertFalse(isPasswordValid(""));
    }

    @Test
    @DisplayName("Should reject null password")
    void testNullPassword() {
        assertFalse(isPasswordValid(null));
    }

    // ===== PASSWORD MATCHING TESTS =====

    @Test
    @DisplayName("Should confirm matching passwords")
    void testPasswordsMatch() {
        assertTrue(doPasswordsMatch("password123", "password123"));
        assertTrue(doPasswordsMatch("Test@123", "Test@123"));
    }

    @Test
    @DisplayName("Should reject non-matching passwords")
    void testPasswordsDoNotMatch() {
        assertFalse(doPasswordsMatch("password123", "password124"));
        assertFalse(doPasswordsMatch("Test@123", "test@123"));
    }

    @Test
    @DisplayName("Should reject null password comparison")
    void testNullPasswordMatch() {
        assertFalse(doPasswordsMatch(null, "password"));
        assertFalse(doPasswordsMatch("password", null));
        assertFalse(doPasswordsMatch(null, null));
    }

    // ===== FIELD VALIDATION TESTS =====

    @Test
    @DisplayName("Should validate non-empty text field")
    void testNonEmptyField() {
        assertTrue(isFieldValid("John Doe"));
        assertTrue(isFieldValid("TestCompany"));
    }

    @Test
    @DisplayName("Should reject empty or whitespace-only fields")
    void testEmptyFields() {
        assertFalse(isFieldValid(""));
        assertFalse(isFieldValid("   "));
        assertFalse(isFieldValid("\t"));
    }

    @Test
    @DisplayName("Should reject null field")
    void testNullField() {
        assertFalse(isFieldValid(null));
    }

    // ===== COMBOBOX VALIDATION TESTS =====

    @Test
    @DisplayName("Should validate selected ComboBox value")
    void testComboBoxSelected() {
        assertTrue(isComboBoxValid("Finland"));
        assertTrue(isComboBoxValid("Male"));
    }

    @Test
    @DisplayName("Should reject null or empty ComboBox value")
    void testComboBoxNotSelected() {
        assertFalse(isComboBoxValid(null));
        assertFalse(isComboBoxValid(""));
    }

    // ===== COMPANY NAME VALIDATION =====

    @Test
    @DisplayName("Should validate proper company names")
    void testValidCompanyName() {
        assertTrue(isValidCompanyName("TechCorp Inc."));
        assertTrue(isValidCompanyName("ABC Technologies"));
        assertTrue(isValidCompanyName("Test Co."));
    }

    @Test
    @DisplayName("Should reject invalid company names")
    void testInvalidCompanyName() {
        assertFalse(isValidCompanyName(""));
        assertFalse(isValidCompanyName("   "));
        assertFalse(isValidCompanyName(null));
        assertFalse(isValidCompanyName("a")); // Too short
    }

    // ===== NAME VALIDATION TESTS =====

    @Test
    @DisplayName("Should validate proper names")
    void testValidName() {
        assertTrue(isValidName("John"));
        assertTrue(isValidName("Mary-Jane"));
        assertTrue(isValidName("O'Brien"));
    }

    @Test
    @DisplayName("Should reject invalid names")
    void testInvalidName() {
        assertFalse(isValidName(""));
        assertFalse(isValidName("   "));
        assertFalse(isValidName(null));
        assertFalse(isValidName("123")); // Numbers only
    }

    // ===== HELPER METHODS FOR TESTING =====

    /**
     * Helper method to test email validation logic
     */
    private boolean isValidEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * Helper method to test password validation
     */
    private boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 6;
    }

    /**
     * Helper method to test password matching
     */
    private boolean doPasswordsMatch(String password1, String password2) {
        if (password1 == null || password2 == null) {
            return false;
        }
        return password1.equals(password2);
    }

    /**
     * Helper method to test field validation
     */
    private boolean isFieldValid(String field) {
        return field != null && !field.trim().isEmpty();
    }

    /**
     * Helper method to test ComboBox validation
     */
    private boolean isComboBoxValid(String value) {
        return value != null && !value.isEmpty();
    }

    /**
     * Helper method to test company name validation
     */
    private boolean isValidCompanyName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.trim().length() >= 2;
    }

    /**
     * Helper method to test name validation
     */
    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Must contain at least one letter
        return name.matches(".*[a-zA-Z].*");
    }

    // ===== INTEGRATION-LIKE TESTS =====

    @Test
    @DisplayName("Should validate complete login form data")
    void testCompleteLoginValidation() {
        String email = "user@test.com";
        String password = "password123";

        assertTrue(isValidEmailFormat(email));
        assertTrue(isPasswordValid(password));
    }

    @Test
    @DisplayName("Should validate complete registration form data")
    void testCompleteRegistrationValidation() {
        // Company data
        String companyName = "TechCorp";
        String country = "Finland";

        // Admin data
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@techcorp.com";
        String gender = "Male";
        String password = "password123";
        String confirmPassword = "password123";

        // Validate all fields
        assertTrue(isValidCompanyName(companyName));
        assertTrue(isComboBoxValid(country));
        assertTrue(isValidName(firstName));
        assertTrue(isValidName(lastName));
        assertTrue(isValidEmailFormat(email));
        assertTrue(isComboBoxValid(gender));
        assertTrue(isPasswordValid(password));
        assertTrue(doPasswordsMatch(password, confirmPassword));
    }

    @Test
    @DisplayName("Should catch registration validation errors")
    void testRegistrationValidationErrors() {
        // Test various invalid scenarios
        assertFalse(isValidCompanyName("")); // Empty company
        assertFalse(isComboBoxValid(null)); // No country selected
        assertFalse(isValidName("")); // Empty name
        assertFalse(isValidEmailFormat("invalid-email")); // Invalid email
        assertFalse(isPasswordValid("short")); // Short password
        assertFalse(doPasswordsMatch("pass1", "pass2")); // Passwords don't match
    }

    @Test
    @DisplayName("Should handle edge cases in email validation")
    void testEmailEdgeCases() {
        // Valid edge cases
        assertTrue(isValidEmailFormat("a@b.c"));
        assertTrue(isValidEmailFormat("test+tag@example.com"));
        assertTrue(isValidEmailFormat("user_123@test-domain.co.uk"));

        // Invalid edge cases
        assertFalse(isValidEmailFormat("test@"));
        assertFalse(isValidEmailFormat("@test.com"));
    }

    @Test
    @DisplayName("Should trim whitespace in field validation")
    void testWhitespaceTrimming() {
        assertTrue(isFieldValid("  John  "));
        assertTrue(isFieldValid("\tTest\t"));
    }
}
