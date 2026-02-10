package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // Login Tab Fields
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginMessage;

    // Register Tab Fields - Company
    @FXML private TextField companyName;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
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

        // TODO: Add backend authentication logic here
        showSuccess(loginMessage, "Login successful!");
        System.out.println("Login - Email: " + email);

        // Navigate to dashboard after successful login
        navigateToDashboard();
    }

    /**
     * Navigates to the main dashboard view after successful login.
     */
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
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError(loginMessage, "Error loading dashboard. Please try again.");
        }
    }

    @FXML
    private void handleRegister() {
        // Company fields
        String company = companyName.getText().trim();
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

        if (!password.equals(confirmPassword)) {
            showError(registerMessage, "Passwords do not match.");
            return;
        }

        // TODO: Add backend registration logic here
        showSuccess(registerMessage, "Account created successfully!");
        System.out.println("Company: " + company + " (" + country + ")");
        System.out.println("Admin: " + firstName + " " + lastName + " - " + email);

        clearRegisterForm();
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
        companyCountry.setValue(null);
        registerFirstName.clear();
        registerLastName.clear();
        registerEmail.clear();
        registerGender.setValue(null);
        registerPassword.clear();
        registerConfirmPassword.clear();
    }
}
