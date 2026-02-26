package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardControllerBackup implements Initializable {

    // User Profile Elements
    @FXML private Text avatarInitials;
    @FXML private Text userName;
    @FXML private Text userRole;

    // Header Elements
    @FXML private Text welcomeText;
    @FXML private Text dateText;
    @FXML private TextField searchField;

    // Statistics Elements
    @FXML private Text totalEmployeesCount;
    @FXML private Text activeEmployeesCount;
    @FXML private Text onLeaveCount;
    @FXML private Text departmentsCount;

    // Department Table
    @FXML private TableView<DepartmentRow> departmentTable;
    @FXML private TableColumn<DepartmentRow, String> deptNameColumn;
    @FXML private TableColumn<DepartmentRow, String> deptHeadColumn;
    @FXML private TableColumn<DepartmentRow, String> deptEmployeesColumn;
    @FXML private TableColumn<DepartmentRow, String> deptStatusColumn;

    // Salary Summary Elements
    @FXML private Text totalPayroll;
    @FXML private Text avgSalary;
    @FXML private Text pendingPayments;

    // Navigation Buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    // Activities Container
    @FXML private VBox recentActivitiesContainer;

    // Current user info (would be passed from login)
    private String currentUserName = "Osama Admin";
    private String currentUserRole = "Administrator";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUserProfile();
        setupDateDisplay();
        setupDepartmentTable();
        loadDashboardData();
    }

    private void setupUserProfile() {
        if (userName != null) {
            userName.setText(currentUserName);
        }
        if (userRole != null) {
            userRole.setText(currentUserRole);
        }
        if (avatarInitials != null) {
            String[] nameParts = currentUserName.split(" ");
            String initials = "";
            if (nameParts.length >= 2) {
                initials = nameParts[0].substring(0, 1) + nameParts[1].substring(0, 1);
            } else if (nameParts.length == 1) {
                initials = nameParts[0].substring(0, Math.min(2, nameParts[0].length()));
            }
            avatarInitials.setText(initials.toUpperCase());
        }
        if (welcomeText != null) {
            String firstName = currentUserName.split(" ")[0];
            welcomeText.setText("Welcome back, " + firstName + "!");
        }
    }

    private void setupDateDisplay() {
        if (dateText != null) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            dateText.setText(today.format(formatter));
        }
    }

    private void setupDepartmentTable() {
        if (departmentTable != null) {
            deptNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
            deptHeadColumn.setCellValueFactory(data -> data.getValue().headProperty());
            deptEmployeesColumn.setCellValueFactory(data -> data.getValue().employeeCountProperty());
            deptStatusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

            ObservableList<DepartmentRow> departments = FXCollections.observableArrayList(
                    new DepartmentRow("IT Department", "Alice Smith", "45", "Active"),
                    new DepartmentRow("HR Department", "Bob Johnson", "12", "Active"),
                    new DepartmentRow("Marketing", "Carol White", "28", "Active"),
                    new DepartmentRow("Finance", "David Brown", "15", "Active"),
                    new DepartmentRow("Operations", "Eve Davis", "67", "Active")
            );
            departmentTable.setItems(departments);
        }
    }

    private void loadDashboardData() {
        if (totalEmployeesCount != null) totalEmployeesCount.setText("540");
        if (activeEmployeesCount != null) activeEmployeesCount.setText("532");
        if (onLeaveCount != null) onLeaveCount.setText("8");
        if (departmentsCount != null) departmentsCount.setText("12");
        if (totalPayroll != null) totalPayroll.setText("€285,000");
        if (avgSalary != null) avgSalary.setText("€4,200");
        if (pendingPayments != null) pendingPayments.setText("€12,500");
    }

    @FXML
    private void handleDashboard() {
        setActiveNavButton(btnDashboard);
        loadDashboardData();
    }

    @FXML
    private void handleEmployees() {
        navigateTo("/fxml/employee.fxml", "/css/management.css");
    }

    @FXML
    private void handleDepartments() {
        navigateTo("/fxml/department.fxml", "/css/management.css");
    }

    @FXML
    private void handleLeave() {
        navigateTo("/fxml/leave.fxml", "/css/management.css");
    }

    @FXML
    private void handleSalary() {
        navigateTo("/fxml/salary.fxml", "/css/management.css");
    }

    private void navigateTo(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            var css = getClass().getResource(cssPath);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load view: " + fxmlPath);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root, 1050, 700);
            scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Employee Management System - Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to logout. Please try again.");
        }
    }

    private void setActiveNavButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnEmployees.getStyleClass().remove("nav-button-active");
        btnDepartments.getStyleClass().remove("nav-button-active");
        btnLeave.getStyleClass().remove("nav-button-active");
        btnSalary.getStyleClass().remove("nav-button-active");
        if (!activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            System.out.println("Searching for: " + searchText);
        }
    }

    @FXML private void handleAddEmployee() { System.out.println("Opening Add Employee form..."); }
    @FXML private void handleCreateDepartment() { System.out.println("Opening Create Department form..."); }
    @FXML private void handleProcessLeave() { System.out.println("Opening Leave Processing..."); }
    @FXML private void handleRunPayroll() { System.out.println("Running payroll..."); }
    @FXML private void handleGenerateReport() { System.out.println("Generating report..."); }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentUser(String name, String role) {
        this.currentUserName = name;
        this.currentUserRole = role;
        setupUserProfile();
    }

    public static class DepartmentRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty head;
        private final SimpleStringProperty employeeCount;
        private final SimpleStringProperty status;

        public DepartmentRow(String name, String head, String employeeCount, String status) {
            this.name = new SimpleStringProperty(name);
            this.head = new SimpleStringProperty(head);
            this.employeeCount = new SimpleStringProperty(employeeCount);
            this.status = new SimpleStringProperty(status);
        }

        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty headProperty() { return head; }
        public SimpleStringProperty employeeCountProperty() { return employeeCount; }
        public SimpleStringProperty statusProperty() { return status; }

        public String getName() { return name.get(); }
        public String getHead() { return head.get(); }
        public String getEmployeeCount() { return employeeCount.get(); }
        public String getStatus() { return status.get(); }
    }
}