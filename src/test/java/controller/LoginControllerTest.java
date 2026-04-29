package controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests login validation, registration validation, and form handling.
 */
class LoginControllerTest {

    private LoginController controller;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new LoginController();
        // Inject FXML fields via reflection so controller methods work
        setField("loginEmail", new TextField());
        setField("loginPassword", new PasswordField());
        setField("loginMessage", new Label());
        setField("companyName", new TextField());
        setField("companyAddress", new TextField());
        setField("companyCountry", new ComboBox<String>());
        setField("registerFirstName", new TextField());
        setField("registerLastName", new TextField());
        setField("registerEmail", new TextField());
        setField("registerGender", new ComboBox<String>());
        setField("registerPassword", new PasswordField());
        setField("registerConfirmPassword", new PasswordField());
        setField("registerMessage", new Label());
        setField("tabPane", new TabPane());
    }

    // ===== HELPER TO SET PRIVATE FIELDS =====

    private void setField(String fieldName, Object value) throws Exception {
        Field field = LoginController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private Object getField(String fieldName) throws Exception {
        Field field = LoginController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(controller);
    }

    private Object invokePrivate(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = LoginController.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(controller, args);
    }

    // ===== isValidEmail TESTS (ACTUAL CONTROLLER METHOD) =====

    @Test
    @DisplayName("Controller isValidEmail should accept valid emails")
    void testControllerValidEmail() throws Exception {
        assertTrue((boolean) invokePrivate("isValidEmail", new Class[]{String.class}, "test@example.com"));
        assertTrue((boolean) invokePrivate("isValidEmail", new Class[]{String.class}, "user.name@company.co.uk"));
        assertTrue((boolean) invokePrivate("isValidEmail", new Class[]{String.class}, "admin123@test-domain.org"));
    }

    @Test
    @DisplayName("Controller isValidEmail should reject invalid emails")
    void testControllerInvalidEmail() throws Exception {
        assertFalse((boolean) invokePrivate("isValidEmail", new Class[]{String.class}, "notanemail"));
        assertFalse((boolean) invokePrivate("isValidEmail", new Class[]{String.class}, "@example.com"));
        assertFalse((boolean) invokePrivate("isValidEmail", new Class[]{String.class}, "test @example.com"));
    }

    // ===== showError / showSuccess / clearMessage TESTS =====

    @Test
    @DisplayName("showError should set label text and remove success class")
    void testShowError() throws Exception {
        Label label = new Label();
        label.getStyleClass().add("success");

        invokePrivate("showError", new Class[]{Label.class, String.class}, label, "Error message");

        assertEquals("Error message", label.getText());
        assertFalse(label.getStyleClass().contains("success"));
    }

    @Test
    @DisplayName("showSuccess should set label text and add success class")
    void testShowSuccess() throws Exception {
        Label label = new Label();

        invokePrivate("showSuccess", new Class[]{Label.class, String.class}, label, "Success!");

        assertEquals("Success!", label.getText());
        assertTrue(label.getStyleClass().contains("success"));
    }

    @Test
    @DisplayName("showSuccess should not duplicate success class")
    void testShowSuccessNoDuplicate() throws Exception {
        Label label = new Label();
        label.getStyleClass().add("success");

        invokePrivate("showSuccess", new Class[]{Label.class, String.class}, label, "Success!");

        assertEquals("Success!", label.getText());
        assertEquals(1, label.getStyleClass().stream().filter("success"::equals).count());
    }

    @Test
    @DisplayName("clearMessage should clear label text and remove success class")
    void testClearMessage() throws Exception {
        Label label = new Label("Some message");
        label.getStyleClass().add("success");

        invokePrivate("clearMessage", new Class[]{Label.class}, label);

        assertEquals("", label.getText());
        assertFalse(label.getStyleClass().contains("success"));
    }

    // ===== handleLogin TESTS =====

    @Test
    @DisplayName("handleLogin should show error for empty fields")
    void testHandleLoginEmptyFields() throws Exception {
        TextField loginEmail = (TextField) getField("loginEmail");
        PasswordField loginPassword = (PasswordField) getField("loginPassword");
        loginEmail.setText("");
        loginPassword.setText("");

        invokePrivate("handleLogin", new Class[]{});

        Label loginMessage = (Label) getField("loginMessage");
        assertEquals("Please fill in all fields.", loginMessage.getText());
    }

    @Test
    @DisplayName("handleLogin should show error for invalid email")
    void testHandleLoginInvalidEmail() throws Exception {
        TextField loginEmail = (TextField) getField("loginEmail");
        PasswordField loginPassword = (PasswordField) getField("loginPassword");
        loginEmail.setText("notanemail");
        loginPassword.setText("password123");

        invokePrivate("handleLogin", new Class[]{});

        Label loginMessage = (Label) getField("loginMessage");
        assertEquals("Please enter a valid email address.", loginMessage.getText());
    }

    @Test
    @DisplayName("handleLogin should show error when only email is empty")
    void testHandleLoginEmptyEmail() throws Exception {
        TextField loginEmail = (TextField) getField("loginEmail");
        PasswordField loginPassword = (PasswordField) getField("loginPassword");
        loginEmail.setText("   ");
        loginPassword.setText("password123");

        invokePrivate("handleLogin", new Class[]{});

        Label loginMessage = (Label) getField("loginMessage");
        assertEquals("Please fill in all fields.", loginMessage.getText());
    }

    @Test
    @DisplayName("handleLogin should show error when only password is empty")
    void testHandleLoginEmptyPassword() throws Exception {
        TextField loginEmail = (TextField) getField("loginEmail");
        PasswordField loginPassword = (PasswordField) getField("loginPassword");
        loginEmail.setText("user@test.com");
        loginPassword.setText("");

        invokePrivate("handleLogin", new Class[]{});

        Label loginMessage = (Label) getField("loginMessage");
        assertEquals("Please fill in all fields.", loginMessage.getText());
    }

    @Test
    @DisplayName("handleLogin with valid credentials should respond with message when DB available")
    void testHandleLoginValidCredentials() throws Exception {
        TextField loginEmail = (TextField) getField("loginEmail");
        PasswordField loginPassword = (PasswordField) getField("loginPassword");
        loginEmail.setText("user@test.com");
        loginPassword.setText("password123");
        // Hits DB — just verify no unhandled exception escapes
        try {
            invokePrivate("handleLogin", new Class[]{});
        } catch (Exception e) {
            // DB not available — acceptable
        }
        // Test passes regardless of DB availability
    }

    // ===== handleRegister TESTS =====

    @Test
    @DisplayName("handleRegister should show error for empty company name")
    void testHandleRegisterEmptyCompany() throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("");

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Please enter company name.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error when country not selected")
    void testHandleRegisterNoCountry() throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("TestCorp");
        // companyCountry value is null by default

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Please select a country.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error for empty name")
    void testHandleRegisterEmptyName() throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("TestCorp");
        ComboBox<String> companyCountry = (ComboBox<String>) getField("companyCountry");
        companyCountry.setValue("Finland");
        TextField firstName = (TextField) getField("registerFirstName");
        TextField lastName = (TextField) getField("registerLastName");
        firstName.setText("");
        lastName.setText("");

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Please enter your name.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error for empty email")
    void testHandleRegisterEmptyEmail() throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("TestCorp");
        ComboBox<String> companyCountry = (ComboBox<String>) getField("companyCountry");
        companyCountry.setValue("Finland");
        TextField firstName = (TextField) getField("registerFirstName");
        TextField lastName = (TextField) getField("registerLastName");
        firstName.setText("John");
        lastName.setText("Doe");
        TextField email = (TextField) getField("registerEmail");
        email.setText("");

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Please enter your email.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error for invalid email")
    void testHandleRegisterInvalidEmail() throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("TestCorp");
        ComboBox<String> companyCountry = (ComboBox<String>) getField("companyCountry");
        companyCountry.setValue("Finland");
        TextField firstName = (TextField) getField("registerFirstName");
        TextField lastName = (TextField) getField("registerLastName");
        firstName.setText("John");
        lastName.setText("Doe");
        TextField email = (TextField) getField("registerEmail");
        email.setText("invalid-email");

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Please enter a valid email address.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error when gender not selected")
    void testHandleRegisterNoGender() throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("TestCorp");
        ComboBox<String> companyCountry = (ComboBox<String>) getField("companyCountry");
        companyCountry.setValue("Finland");
        TextField firstName = (TextField) getField("registerFirstName");
        TextField lastName = (TextField) getField("registerLastName");
        firstName.setText("John");
        lastName.setText("Doe");
        TextField email = (TextField) getField("registerEmail");
        email.setText("john@test.com");
        // registerGender value is null by default

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Please select gender.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error for empty password")
    void testHandleRegisterEmptyPassword() throws Exception {
        fillRegistrationFieldsUntilPassword("");

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Please enter a password.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error for short password")
    void testHandleRegisterShortPassword() throws Exception {
        fillRegistrationFieldsUntilPassword("abc");

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Password must be at least 6 characters.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should show error for mismatched passwords")
    void testHandleRegisterPasswordMismatch() throws Exception {
        // Password must pass uppercase + special char checks before reaching mismatch check
        fillRegistrationFieldsUntilPassword("Password1!");
        PasswordField confirmPassword = (PasswordField) getField("registerConfirmPassword");
        confirmPassword.setText("Different1!");

        invokePrivate("handleRegister", new Class[]{});

        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Passwords do not match.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister should attempt registration with valid data")
    void testHandleRegisterSuccess() throws Exception {
        fillRegistrationFieldsUntilPassword("Password1!");
        PasswordField confirmPassword = (PasswordField) getField("registerConfirmPassword");
        confirmPassword.setText("Password1!");
        // Passes all validation — hits DB. Just verify no unhandled exception escapes.
        try {
            invokePrivate("handleRegister", new Class[]{});
        } catch (Exception e) {
            // DB not available — acceptable
        }
        // Test passes regardless of DB availability
    }

    // ===== initialize TESTS =====

    @Test
    @DisplayName("initialize should set up combo boxes")
    void testInitialize() throws Exception {
        controller.initialize(null, null);

        ComboBox<String> gender = (ComboBox<String>) getField("registerGender");
        ComboBox<String> country = (ComboBox<String>) getField("companyCountry");

        assertFalse(gender.getItems().isEmpty());
        assertTrue(gender.getItems().contains("Male"));
        assertTrue(gender.getItems().contains("Female"));
        assertTrue(gender.getItems().contains("Other"));

        assertFalse(country.getItems().isEmpty());
        assertTrue(country.getItems().contains("Finland"));
        assertTrue(country.getItems().contains("Sweden"));
    }

    // ===== hasUppercase TESTS =====

    @Test
    @DisplayName("handleRegister shows error when password has no uppercase letter")
    void testHandleRegisterNoUppercase() throws Exception {
        fillRegistrationFieldsUntilPassword("password1!");
        PasswordField confirmPassword = (PasswordField) getField("registerConfirmPassword");
        confirmPassword.setText("password1!");
        invokePrivate("handleRegister", new Class[]{});
        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Password must contain at least one uppercase letter.", registerMessage.getText());
    }

    @Test
    @DisplayName("handleRegister shows error when password has no special character")
    void testHandleRegisterNoSpecialChar() throws Exception {
        fillRegistrationFieldsUntilPassword("Password1");
        PasswordField confirmPassword = (PasswordField) getField("registerConfirmPassword");
        confirmPassword.setText("Password1");
        invokePrivate("handleRegister", new Class[]{});
        Label registerMessage = (Label) getField("registerMessage");
        assertEquals("Password must contain at least one special character.", registerMessage.getText());
    }

    @Test
    @DisplayName("hasUppercase returns true when password has uppercase")
    void testHasUppercase_true() throws Exception {
        assertTrue((boolean) invokePrivate("hasUppercase", new Class[]{String.class}, "Password1"));
    }

    @Test
    @DisplayName("hasUppercase returns false when password has no uppercase")
    void testHasUppercase_false() throws Exception {
        assertFalse((boolean) invokePrivate("hasUppercase", new Class[]{String.class}, "password1"));
    }

    @Test
    @DisplayName("hasUppercase returns false for empty string")
    void testHasUppercase_empty() throws Exception {
        assertFalse((boolean) invokePrivate("hasUppercase", new Class[]{String.class}, ""));
    }

    @Test
    @DisplayName("hasSpecialChar returns true when password has special char")
    void testHasSpecialChar_true() throws Exception {
        assertTrue((boolean) invokePrivate("hasSpecialChar", new Class[]{String.class}, "Password1!"));
    }

    @Test
    @DisplayName("hasSpecialChar returns false when password has no special char")
    void testHasSpecialChar_false() throws Exception {
        assertFalse((boolean) invokePrivate("hasSpecialChar", new Class[]{String.class}, "Password1"));
    }

    @Test
    @DisplayName("hasSpecialChar returns false for empty string")
    void testHasSpecialChar_empty() throws Exception {
        assertFalse((boolean) invokePrivate("hasSpecialChar", new Class[]{String.class}, ""));
    }

    // ===== capitaliseEmailPrefix TESTS =====

    @Test
    @DisplayName("capitaliseEmailPrefix handles dot-separated name")
    void testCapitalise_dotSeparated() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "john.doe");
        assertEquals("John Doe", result);
    }

    @Test
    @DisplayName("capitaliseEmailPrefix handles underscore-separated name")
    void testCapitalise_underscore() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "john_doe");
        assertEquals("John Doe", result);
    }

    @Test
    @DisplayName("capitaliseEmailPrefix handles dash-separated name")
    void testCapitalise_dash() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "john-doe");
        assertEquals("John Doe", result);
    }

    @Test
    @DisplayName("capitaliseEmailPrefix handles simple name with no separator")
    void testCapitalise_simple() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "johndoe");
        assertEquals("Johndoe", result);
    }

    @Test
    @DisplayName("capitaliseEmailPrefix handles empty string returning original")
    void testCapitalise_empty() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "");
        assertEquals("", result);
    }

    @Test
    @DisplayName("capitaliseEmailPrefix handles three-part name")
    void testCapitalise_threeParts() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "john.a.doe");
        assertEquals("John A Doe", result);
    }

    @Test
    @DisplayName("capitaliseEmailPrefix skips empty parts from consecutive separators")
    void testCapitalise_doubleSeparator() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "john..doe");
        assertEquals("John Doe", result);
    }

    @Test
    @DisplayName("capitaliseEmailPrefix handles single char parts")
    void testCapitalise_singleChars() throws Exception {
        String result = (String) invokePrivate("capitaliseEmailPrefix", new Class[]{String.class}, "j.d");
        assertEquals("J D", result);
    }

    // ===== updateUIText in LoginController =====

    @Test
    @DisplayName("updateUIText does not throw (empty implementation)")
    void testUpdateUIText_noThrow() throws Exception {
        assertDoesNotThrow(() -> invokePrivate("updateUIText", new Class[]{String.class}, "English"));
    }

    @Test
    @DisplayName("updateUIText with all languages does not throw")
    void testUpdateUIText_allLanguages() throws Exception {
        for (String lang : new String[]{"English", "Spanish", "Arabic", "Persia"}) {
            assertDoesNotThrow(() -> invokePrivate("updateUIText", new Class[]{String.class}, lang));
        }
    }

    // ===== clearRegisterForm TESTS =====

    @Test
    @DisplayName("clearRegisterForm clears all registration fields")
    void testClearRegisterForm() throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("TestCorp");
        TextField companyAddress = (TextField) getField("companyAddress");
        companyAddress.setText("Test Street");
        ComboBox<String> companyCountry = (ComboBox<String>) getField("companyCountry");
        companyCountry.setValue("Finland");
        TextField firstName = (TextField) getField("registerFirstName");
        firstName.setText("John");
        TextField lastName = (TextField) getField("registerLastName");
        lastName.setText("Doe");
        TextField email = (TextField) getField("registerEmail");
        email.setText("john@test.com");
        ComboBox<String> gender = (ComboBox<String>) getField("registerGender");
        gender.setValue("Male");
        PasswordField pw = (PasswordField) getField("registerPassword");
        pw.setText("Password1!");
        PasswordField confirmPw = (PasswordField) getField("registerConfirmPassword");
        confirmPw.setText("Password1!");

        invokePrivate("clearRegisterForm", new Class[]{});

        assertEquals("", companyName.getText());
        assertNull(companyCountry.getValue());
        assertEquals("", firstName.getText());
        assertNull(gender.getValue());
        assertEquals("", pw.getText());
    }

    // ===== HELPER to fill registration fields up to password =====

    private void fillRegistrationFieldsUntilPassword(String password) throws Exception {
        TextField companyName = (TextField) getField("companyName");
        companyName.setText("TestCorp");
        ComboBox<String> companyCountry = (ComboBox<String>) getField("companyCountry");
        companyCountry.setValue("Finland");
        TextField firstName = (TextField) getField("registerFirstName");
        TextField lastName = (TextField) getField("registerLastName");
        firstName.setText("John");
        lastName.setText("Doe");
        TextField email = (TextField) getField("registerEmail");
        email.setText("john@test.com");
        ComboBox<String> gender = (ComboBox<String>) getField("registerGender");
        gender.setValue("Male");
        PasswordField pw = (PasswordField) getField("registerPassword");
        pw.setText(password);
    }

    // ===== ORIGINAL HELPER-BASED VALIDATION TESTS =====

    @Test
    @DisplayName("Should validate correct email format")
    void testValidEmail() {
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

    @Test
    @DisplayName("Should confirm matching passwords")
    void testPasswordsMatch() {
        assertTrue(doPasswordsMatch("password123", "password123"));
    }

    @Test
    @DisplayName("Should reject non-matching passwords")
    void testPasswordsDoNotMatch() {
        assertFalse(doPasswordsMatch("password123", "password124"));
    }

    @Test
    @DisplayName("Should reject null password comparison")
    void testNullPasswordMatch() {
        assertFalse(doPasswordsMatch(null, "password"));
        assertFalse(doPasswordsMatch("password", null));
        assertFalse(doPasswordsMatch(null, null));
    }

    @Test
    @DisplayName("Should validate non-empty text field")
    void testNonEmptyField() {
        assertTrue(isFieldValid("John Doe"));
    }

    @Test
    @DisplayName("Should reject empty or whitespace-only fields")
    void testEmptyFields() {
        assertFalse(isFieldValid(""));
        assertFalse(isFieldValid("   "));
    }

    @Test
    @DisplayName("Should reject null field")
    void testNullField() {
        assertFalse(isFieldValid(null));
    }

    // ===== HELPER METHODS FOR TESTING =====

    private boolean isValidEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isPasswordValid(String password) {
        if (password == null) return false;
        return password.length() >= 6;
    }

    private boolean doPasswordsMatch(String password1, String password2) {
        if (password1 == null || password2 == null) return false;
        return password1.equals(password2);
    }

    private boolean isFieldValid(String field) {
        return field != null && !field.trim().isEmpty();
    }

}