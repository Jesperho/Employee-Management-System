package controller;

import entity.Admin;
import app.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.AuthService;
import utils.BaseController;

// Make sure to import your BaseController if it's in a different package!
// import util.BaseController;

public class LoginController extends BaseController {

    private final AuthService authService = new AuthService();

    // Login Tab Fields
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginMessage;

    // Register Tab Fields - Company
    @FXML private TextField companyName;
    @FXML private TextField companyAddress;
    @FXML private ComboBox<String> companyCountry;

    // Register Tab Fields - Admin
    @FXML private TextField registerFirstName;
    @FXML private TextField registerLastName;
    @FXML private TextField registerEmail;
    @FXML private ComboBox<String> registerGender;
    @FXML private PasswordField registerPassword;
    @FXML private PasswordField registerConfirmPassword;
    @FXML private Label registerMessage;

    @FXML private TabPane tabPane;

    // 1. Replaced initialize() with setupController()
    @Override
    protected void setupController() {
        setupComboBoxes();

    }


    @Override
    protected void updateUIText(String newLanguage) {
        System.out.println("Login View updating to: " + newLanguage);


    }

    private void setupComboBoxes() {
        registerGender.setItems(FXCollections.observableArrayList(
                "Male", "Female", "Other"
        ));

        companyCountry.setItems(FXCollections.observableArrayList(
                "Finland", "Sweden", "Norway", "Denmark", "Germany",
                "United Kingdom", "United States", "Canada", "France",
                "Netherlands", "Belgium", "Switzerland", "Austria",
                "Spain", "Italy", "Poland", "Australia", "Other"
        ));
    }

    @FXML
    private void handleLogin() {
        String email = loginEmail.getText().trim();
        String password = loginPassword.getText();

        clearMessage(loginMessage);

        if (email.isEmpty() || password.isEmpty()) {
            showError(loginMessage, "Please fill in all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            showError(loginMessage, "Please enter a valid email address.");
            return;
        }

        // Authenticate against database
        Admin admin = authService.login(email, password);
        if (admin != null) {
            String emailPrefix = admin.getEmail() != null
                    ? admin.getEmail().split("@")[0] : "Admin";
            String displayName = capitaliseEmailPrefix(emailPrefix);
            UserSession.getInstance().setUser(displayName, "Administrator");
            showSuccess(loginMessage, "Login successful!");
            navigateToDashboard();
        } else {
            showError(loginMessage, "Invalid email or password.");
        }
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginEmail.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Employee Management System - Dashboard");
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
        } catch (Exception e) {
            e.printStackTrace();
            showError(loginMessage, "Error loading dashboard. Please try again.");
        }
    }

    @FXML
    private void handleRegister() {
        // Company fields
        String company = companyName.getText().trim();
        String address = companyAddress.getText().trim();
        String country = companyCountry.getValue();

        // Admin fields
        String firstName = registerFirstName.getText().trim();
        String lastName = registerLastName.getText().trim();
        String email = registerEmail.getText().trim();
        String gender = registerGender.getValue();
        String password = registerPassword.getText();
        String confirmPassword = registerConfirmPassword.getText();

        clearMessage(registerMessage);

        // Validate company
        if (company.isEmpty()) {
            showError(registerMessage, "Please enter company name.");
            return;
        }

        if (country == null) {
            showError(registerMessage, "Please select a country.");
            return;
        }

        // Validate admin
        if (firstName.isEmpty() || lastName.isEmpty()) {
            showError(registerMessage, "Please enter your name.");
            return;
        }

        if (email.isEmpty()) {
            showError(registerMessage, "Please enter your email.");
            return;
        }

        if (!isValidEmail(email)) {
            showError(registerMessage, "Please enter a valid email address.");
            return;
        }

        if (gender == null) {
            showError(registerMessage, "Please select gender.");
            return;
        }

        if (password.isEmpty()) {
            showError(registerMessage, "Please enter a password.");
            return;
        }

        if (password.length() < 6) {
            showError(registerMessage, "Password must be at least 6 characters.");
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            showError(registerMessage, "Password must contain at least one uppercase letter.");
            return;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            showError(registerMessage, "Password must contain at least one special character.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(registerMessage, "Passwords do not match.");
            return;
        }

        // Register in database
        boolean success = authService.register(company, address, country, firstName, lastName, email, gender, password);
        if (success) {
            showSuccess(registerMessage, "Account created successfully! Please login.");
            clearRegisterForm();
        } else {
            showError(registerMessage, "Email already registered.");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.getStyleClass().remove("success");
    }

    private void showSuccess(Label label, String message) {
        label.setText(message);
        if (!label.getStyleClass().contains("success")) {
            label.getStyleClass().add("success");
        }
    }

    private void clearMessage(Label label) {
        label.setText("");
        label.getStyleClass().remove("success");
    }

    private void clearRegisterForm() {
        companyName.clear();
        companyAddress.clear();
        companyCountry.setValue(null);
        registerFirstName.clear();
        registerLastName.clear();
        registerEmail.clear();
        registerGender.setValue(null);
        registerPassword.clear();
        registerConfirmPassword.clear();
    }

    private String capitaliseEmailPrefix(String prefix) {
        String[] parts = prefix.split("[._\\-]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
            }
        }
        return sb.length() > 0 ? sb.toString() : prefix;
    }
}