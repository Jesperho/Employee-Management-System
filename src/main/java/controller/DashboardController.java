package controller;

import dao.DepartmentDao;
import dao.EmployeeDao;
import dao.LeaveDao;
import entity.Department;
import entity.Employee;
import entity.Leave;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Text avatarInitials;
    @FXML private Text userName;
    @FXML private Text userRole;

    @FXML private Text welcomeText;
    @FXML private Text dateText;
    @FXML private TextField searchField;

    @FXML private Text totalEmployeesCount;
    @FXML private Text activeEmployeesCount;
    @FXML private Text onLeaveCount;
    @FXML private Text pendingLeaveText;
    @FXML private Text departmentsCount;

    @FXML private TableView<DepartmentRow> departmentTable;
    @FXML private TableColumn<DepartmentRow, String> deptNameColumn;
    @FXML private TableColumn<DepartmentRow, String> deptHeadColumn;
    @FXML private TableColumn<DepartmentRow, String> deptEmployeesColumn;
    @FXML private TableColumn<DepartmentRow, String> deptStatusColumn;

    @FXML private Text totalPayroll;
    @FXML private Text avgSalary;
    @FXML private Text pendingPayments;

    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    @FXML private VBox recentActivitiesContainer;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final LeaveDao leaveDao = new LeaveDao();

    private String currentUserName = "Admin";
    private String currentUserRole = "Administrator";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUserProfile();
        setupDateDisplay();
        setupDepartmentTable();
        loadDashboardData();
        loadRecentActivities();
    }

    private void setupUserProfile() {
        if (userName != null) userName.setText(currentUserName);
        if (userRole != null) userRole.setText(currentUserRole);

        if (avatarInitials != null) {
            String[] parts = currentUserName.split(" ");
            String initials = "";
            if (parts.length >= 2) {
                initials = parts[0].substring(0, 1) + parts[1].substring(0, 1);
            } else if (parts.length == 1) {
                initials = parts[0].substring(0, Math.min(2, parts[0].length()));
            }
            avatarInitials.setText(initials.toUpperCase());
        }

        if (welcomeText != null) {
            welcomeText.setText("Welcome back, " + currentUserName.split(" ")[0] + "!");
        }
    }

    private void setupDateDisplay() {
        if (dateText != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            dateText.setText(LocalDate.now().format(formatter));
        }
    }

    private void setupDepartmentTable() {
        if (departmentTable == null) return;

        deptNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        deptHeadColumn.setCellValueFactory(data -> data.getValue().headProperty());
        deptEmployeesColumn.setCellValueFactory(data -> data.getValue().employeeCountProperty());
        deptStatusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        List<Department> departments = departmentDao.findAll();
        List<Employee> allEmployees = employeeDao.findAll();

        ObservableList<DepartmentRow> rows = FXCollections.observableArrayList();
        for (Department dept : departments) {
            int count = 0;
            for (Employee emp : allEmployees) {
                if (emp.getDepartment() != null && emp.getDepartment().getId() == dept.getId()) {
                    count++;
                }
            }
            rows.add(new DepartmentRow(dept.getDepartmentName(), "N/A", String.valueOf(count), "Active"));
        }
        departmentTable.setItems(rows);
    }

    private void loadDashboardData() {
        List<Employee> employees = employeeDao.findAll();
        List<Department> departments = departmentDao.findAll();
        List<Leave> leaves = leaveDao.findAll();

        LocalDate today = LocalDate.now();

        // Count employees currently on leave (today falls within their leave period)
        int currentlyOnLeave = 0;
        // Count future/pending leave requests (start date is after today)
        int pendingLeaves = 0;

        for (Leave leave : leaves) {
            if (leave.getStartDate() != null && leave.getEndDate() != null) {
                if (!today.isBefore(leave.getStartDate()) && !today.isAfter(leave.getEndDate())) {
                    currentlyOnLeave++;
                }
            }
            if (leave.getStartDate() != null && leave.getStartDate().isAfter(today)) {
                pendingLeaves++;
            }
        }

        int total = employees.size();
        int active = total - currentlyOnLeave;

        if (totalEmployeesCount != null) totalEmployeesCount.setText(String.valueOf(total));
        if (activeEmployeesCount != null) activeEmployeesCount.setText(String.valueOf(active));
        if (onLeaveCount != null) onLeaveCount.setText(String.valueOf(currentlyOnLeave));
        if (pendingLeaveText != null) pendingLeaveText.setText(pendingLeaves + " pending");
        if (departmentsCount != null) departmentsCount.setText(String.valueOf(departments.size()));

        // Calculate payroll from employee salaries
        int totalPayrollAmount = 0;
        int salaryCount = 0;
        for (Employee emp : employees) {
            if (emp.getSalary() != null) {
                totalPayrollAmount += emp.getSalary().intValue();
                salaryCount++;
            }
        }
        int avgSalaryAmount = salaryCount > 0 ? totalPayrollAmount / salaryCount : 0;

        if (totalPayroll != null) totalPayroll.setText("€" + totalPayrollAmount);
        if (avgSalary != null) avgSalary.setText("€" + avgSalaryAmount);
        if (pendingPayments != null) pendingPayments.setText("€0");
    }

    // Builds recent activity items from real DB data (last added employees + leaves)
    private void loadRecentActivities() {
        if (recentActivitiesContainer == null) return;
        recentActivitiesContainer.getChildren().clear();

        // Last 3 employees added (sorted by ID descending)
        List<Employee> employees = employeeDao.findAll();
        employees.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        int empLimit = Math.min(3, employees.size());
        for (int i = 0; i < empLimit; i++) {
            Employee emp = employees.get(i);
            String dept = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "No Department";
            recentActivitiesContainer.getChildren().add(
                    createActivityItem("+", "activity-new",
                            "New employee added",
                            emp.getFullName() + " joined " + dept,
                            emp.getStartDate() != null ? emp.getStartDate().toString() : "")
            );
        }

        // Last 2 leave records added (sorted by ID descending)
        List<Leave> leaves = leaveDao.findAll();
        leaves.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        int leaveLimit = Math.min(2, leaves.size());
        for (int i = 0; i < leaveLimit; i++) {
            Leave leave = leaves.get(i);
            String empName = leave.getEmployee() != null ? leave.getEmployee().getFullName() : "Unknown";
            String type = leave.getType() != null ? leave.getType() : "Leave";
            recentActivitiesContainer.getChildren().add(
                    createActivityItem("📅", "activity-leave",
                            "Leave record",
                            empName + " - " + type,
                            leave.getStartDate() != null ? leave.getStartDate().toString() : "")
            );
        }

        if (recentActivitiesContainer.getChildren().isEmpty()) {
            Text empty = new Text("No recent activity.");
            empty.setStyle("-fx-fill: #7f8c8d; -fx-font-size: 13px;");
            recentActivitiesContainer.getChildren().add(empty);
        }
    }

    // Builds one activity row matching the dashboard CSS structure
    private HBox createActivityItem(String icon, String iconStyle, String title, String description, String time) {
        HBox item = new HBox(12);
        item.getStyleClass().add("activity-item");
        item.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().addAll("activity-icon-container", iconStyle);
        Text iconText = new Text(icon);
        iconText.getStyleClass().add("activity-icon");
        iconContainer.getChildren().add(iconText);

        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Text titleText = new Text(title);
        titleText.getStyleClass().add("activity-title");
        Text descText = new Text(description);
        descText.getStyleClass().add("activity-description");
        textBox.getChildren().addAll(titleText, descText);

        Text timeText = new Text(time);
        timeText.getStyleClass().add("activity-time");

        item.getChildren().addAll(iconContainer, textBox, timeText);
        return item;
    }

    @FXML
    private void handleDashboard() {
        setActiveNavButton(btnDashboard);
        loadDashboardData();
        loadRecentActivities();
    }

    @FXML
    private void handleEmployees() { navigateTo("/fxml/employee.fxml", "/css/management.css"); }

    @FXML
    private void handleDepartments() { navigateTo("/fxml/department.fxml", "/css/management.css"); }

    @FXML
    private void handleLeave() { navigateTo("/fxml/leave.fxml", "/css/management.css"); }

    @FXML
    private void handleSalary() { navigateTo("/fxml/salary.fxml", "/css/management.css"); }

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
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to logout. Please try again.");
        }
    }

    private void navigateTo(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            Scene scene = new Scene(root, w, h);
            var css = getClass().getResource(cssPath);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load view: " + fxmlPath);
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

    @FXML private void handleAddEmployee() { navigateTo("/fxml/employee.fxml", "/css/management.css"); }
    @FXML private void handleCreateDepartment() { navigateTo("/fxml/department.fxml", "/css/management.css"); }
    @FXML private void handleProcessLeave() { navigateTo("/fxml/leave.fxml", "/css/management.css"); }
    @FXML private void handleRunPayroll() { navigateTo("/fxml/salary.fxml", "/css/management.css"); }
    @FXML private void handleGenerateReport() { System.out.println("Generating report..."); }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Called from LoginController after successful login to pass the user's name
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