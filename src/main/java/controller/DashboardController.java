package controller;

import dao.*;
import entity.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.BaseController;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

// Make sure to import BaseController if it's in a different package!
// import util.BaseController;

// 1. Extend BaseController instead of implementing Initializable
public class DashboardController extends BaseController {

    @FXML private Text avatarInitials;
    @FXML private Text userName;
    @FXML private Text userRole;
    @FXML private Text main_menu;

    @FXML private Text totalEmployees;
    @FXML private Text activeEmployees;

    @FXML private Text welcomeText;
    @FXML private Text dateText;
    @FXML private TextField searchField;

    @FXML private Text logoSubtitle;
    @FXML private Text languageLabelText;
    @FXML private Text totalEmpChangeLabel;
    @FXML private Text activeEmpChangeLabel;
    @FXML private Text onLeaveLabel;
    @FXML private Text leaveRequestsLabel;
    @FXML private Text departmentsLabel;
    @FXML private Text departmentsStatusLabel;
    @FXML private Text recentActivitiesTitle;
    @FXML private Button viewMoreBtn;
    @FXML private Text quickActionsTitle;
    @FXML private Button btnAddEmployee;
    @FXML private Button btnCreateDepartment;
    @FXML private Button btnProcessLeave;
    @FXML private Button btnRunPayroll;
    @FXML private Text deptOverviewTitle;
    @FXML private Button btnManageDepts;
    @FXML private Text salarySummaryTitle;
    @FXML private Text totalMonthlyPayrollLabel;
    @FXML private Text avgSalaryLabel;
    @FXML private Text pendingPaymentsLabel;
    @FXML private Button btnViewFullReport;



    @FXML private Text totalEmployeesCount;
    @FXML private Text activeEmployeesCount;
    @FXML private Text onLeaveCount;
    @FXML private Text pendingLeaveText;
    @FXML private Text departmentsCount;

    // Dynamic sub-labels under each stat card
    @FXML private Text employeeChangeText;
    @FXML private Text attendanceRateText;
    @FXML private Text deptStatusText;

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
    private final ChangeEmpDao changeEmpDao = new ChangeEmpDao();
    private final ChangeLeaveDao changeLeaveDao = new ChangeLeaveDao();
    private final ChangeDeptDao changeDeptDao = new ChangeDeptDao();
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final DepartmentTranslationDao departmentTranslationDao = new DepartmentTranslationDao();
    private final LeaveDao leaveDao = new LeaveDao();
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());

    @Override
    protected void setupController() {
        setupUserProfile();
        setupDateDisplay();
        setupDepartmentTable();
        loadDashboardData();
        loadRecentActivities();
    }

    @Override
    protected void updateUIText(String newLanguage) {
        System.out.println("Dashboard View updating to: " + newLanguage);

        LanguageManager langManager = LanguageManager.getInstance();

        if (btnDashboard != null) btnDashboard.setText(langManager.getTranslatedText("nav.dashboard"));
        if (btnEmployees != null) btnEmployees.setText(langManager.getTranslatedText("nav.employees"));
        if (btnDepartments != null) btnDepartments.setText(langManager.getTranslatedText("nav.departments"));
        if (btnLeave != null) btnLeave.setText(langManager.getTranslatedText("nav.leave"));
        if (btnSalary != null) btnSalary.setText(langManager.getTranslatedText("nav.salary"));
        if (btnLogout != null) btnLogout.setText(langManager.getTranslatedText("nav.logout"));

        if (deptNameColumn != null) deptNameColumn.setText(langManager.getTranslatedText("table.deptName"));
        if (deptHeadColumn != null) deptHeadColumn.setText(langManager.getTranslatedText("table.deptHead"));
        if (deptEmployeesColumn != null) deptEmployeesColumn.setText(langManager.getTranslatedText("table.employees"));
        if (deptStatusColumn != null) deptStatusColumn.setText(langManager.getTranslatedText("table.status"));

        if (userRole != null) userRole.setText(langManager.getTranslatedText("user.role"));
        if (main_menu != null) main_menu.setText(langManager.getTranslatedText("main_menu"));
        if (totalEmployees != null) totalEmployees.setText(langManager.getTranslatedText("totalEmployees"));
        if (activeEmployees != null) activeEmployees.setText(langManager.getTranslatedText("activeEmployees"));

        if (logoSubtitle != null) logoSubtitle.setText(langManager.getTranslatedText("app.subtitle"));
        if (languageLabelText != null) languageLabelText.setText(langManager.getTranslatedText("lang.label"));
        if (totalEmpChangeLabel != null) totalEmpChangeLabel.setText(langManager.getTranslatedText("dashboard.vsLastMonth"));
        if (activeEmpChangeLabel != null) activeEmpChangeLabel.setText(langManager.getTranslatedText("dashboard.attendanceRate"));
        if (onLeaveLabel != null) onLeaveLabel.setText(langManager.getTranslatedText("dashboard.onLeave"));
        if (leaveRequestsLabel != null) leaveRequestsLabel.setText(langManager.getTranslatedText("dashboard.requests"));
        if (departmentsLabel != null) departmentsLabel.setText(langManager.getTranslatedText("dashboard.departments"));
        if (departmentsStatusLabel != null) departmentsStatusLabel.setText(langManager.getTranslatedText("dashboard.status"));

        if (recentActivitiesTitle != null) recentActivitiesTitle.setText(langManager.getTranslatedText("dashboard.recentActivities"));
        if (viewMoreBtn != null) viewMoreBtn.setText(langManager.getTranslatedText("dashboard.viewMore"));
        if (quickActionsTitle != null) quickActionsTitle.setText(langManager.getTranslatedText("dashboard.quickActions"));
        if (deptOverviewTitle != null) deptOverviewTitle.setText(langManager.getTranslatedText("dashboard.deptOverview"));
        if (btnManageDepts != null) btnManageDepts.setText(langManager.getTranslatedText("dashboard.manage"));
        if (salarySummaryTitle != null) salarySummaryTitle.setText(langManager.getTranslatedText("dashboard.salarySummary"));
        if (totalMonthlyPayrollLabel != null) totalMonthlyPayrollLabel.setText(langManager.getTranslatedText("dashboard.totalPayroll"));
        if (avgSalaryLabel != null) avgSalaryLabel.setText(langManager.getTranslatedText("dashboard.avgSalary"));
        if (pendingPaymentsLabel != null) pendingPaymentsLabel.setText(langManager.getTranslatedText("dashboard.pendingPayments"));
        if (btnViewFullReport != null) btnViewFullReport.setText(langManager.getTranslatedText("dashboard.viewFullReport"));

        if (btnAddEmployee != null) btnAddEmployee.setText("➕  " + langManager.getTranslatedText("action.addEmployee"));
        if (btnCreateDepartment != null) btnCreateDepartment.setText("📋  " + langManager.getTranslatedText("action.createDepartment"));
        if (btnProcessLeave != null) btnProcessLeave.setText("📝  " + langManager.getTranslatedText("action.processLeave"));
        if (btnRunPayroll != null) btnRunPayroll.setText("💵  " + langManager.getTranslatedText("action.runPayroll"));

        if (searchField != null) searchField.setPromptText(langManager.getTranslatedText("search.prompt"));


        if (welcomeText != null) {
            app.UserSession s = app.UserSession.getInstance();
            String firstName = s.getFullName().split(" ")[0];
            String greeting = langManager.getTranslatedText("dashboard.welcome");
            welcomeText.setText(greeting + ", " + firstName + "!");
        }

        loadRecentActivities();
        loadDashboardData();
    }

    private void setupUserProfile() {
        app.UserSession s = app.UserSession.getInstance();
        if (userName != null) userName.setText(s.getFullName());
        if (userRole != null) userRole.setText(s.getRole());
        if (avatarInitials != null) avatarInitials.setText(s.getInitials());
        if (welcomeText != null) {
            String firstName = s.getFullName().split(" ")[0];
            welcomeText.setText("Welcome back, " + firstName + "!");
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
        entity.LanguageManager lang= LanguageManager.getInstance();
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

        if (totalEmployeesCount != null) totalEmployeesCount.setText(String.valueOf(departmentTranslationDao.formatNumber(total, lang.getLanguage())));
        if (activeEmployeesCount != null) activeEmployeesCount.setText(String.valueOf(departmentTranslationDao.formatNumber(active, lang.getLanguage())));
        if (onLeaveCount != null) onLeaveCount.setText(String.valueOf(departmentTranslationDao.formatNumber(currentlyOnLeave, lang.getLanguage())));
        if (pendingLeaveText != null) pendingLeaveText.setText(departmentTranslationDao.formatNumber(pendingLeaves, lang.getLanguage()) +"");
        if (departmentsCount != null) departmentsCount.setText(String.valueOf(departmentTranslationDao.formatNumber(departments.size(), lang.getLanguage())));

        // Dynamic sub-labels
        // Attendance rate = active / total * 100
        if (attendanceRateText != null) {
            if (total > 0) {
                double rate = (active * 100.0) / total;
                attendanceRateText.setText(String.format("%.1f%%", rate));
            } else {
                attendanceRateText.setText("N/A");
            }
        }

        // "All active" vs how many departments have employees
        if (deptStatusText != null) {
            long activeDepts = departments.stream()
                    .filter(d -> departmentDao.countEmployees(d.getId()) > 0)
                    .count();
            if (activeDepts == departments.size() && !departments.isEmpty()) {
                deptStatusText.setText("All active");
            } else {
                deptStatusText.setText(activeDepts + " active");
            }
        }

        // Employee change: show leave count relative to total as a simple indicator
        if (employeeChangeText != null) {
            if (total > 0) {
                int leaveRate = (currentlyOnLeave * 100) / total;
                String formatedRate = formatNumber(leaveRate, lang.getLanguage());

                if (currentlyOnLeave == 0) {
                    employeeChangeText.setText(lang.getTranslatedText("full_staff"));
                    employeeChangeText.getStyleClass().setAll("stat-change-positive");
                } else {
                    employeeChangeText.setText(formatedRate + "% on leave");
                    employeeChangeText.getStyleClass().setAll("stat-change-neutral");
                }
            } else {
                employeeChangeText.setText("No data");
            }
        }

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

        if (totalPayroll != null) totalPayroll.setText(departmentTranslationDao.formatNumber(totalPayrollAmount, lang.getLanguage()));
        if (avgSalary != null) avgSalary.setText(departmentTranslationDao.formatNumber(avgSalaryAmount, lang.getLanguage()));
        if (pendingPayments != null) pendingPayments.setText(departmentTranslationDao.formatNumber(0, lang.getLanguage()));
    }

    public String formatNumber(double number, String language) {
        entity.LanguageManager lang = LanguageManager.getInstance();
        String code = lang.getAbreviation(language);

        Locale locale = switch (code) {
            case "ar" -> new Locale.Builder()
                    .setLanguage("ar")
                    .setRegion("AE")
                    .setUnicodeLocaleKeyword("nu", "arab") // 🔥 THIS LINE
                    .build();
            case "es" -> new Locale("es", "ES");
            case "fa" -> new Locale("fa", "AF");
            default -> Locale.US;
        };

        NumberFormat formatter = NumberFormat.getInstance(locale);
        return formatter.format(number);
    }


    // Builds recent activity items from real DB data (last added employees + leaves)
    private void loadRecentActivities() {
        app.UserSession s = app.UserSession.getInstance();
        LanguageManager lang = LanguageManager.getInstance();
        if (recentActivitiesContainer == null) return;
        recentActivitiesContainer.getChildren().clear();

        List<ChangeEmp> changesEmps = changeEmpDao.findAll();
        changesEmps.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        int empLimit = Math.min(3, changesEmps.size());

        for (int i = 0; i < empLimit; i++) {
            ChangeEmp emp = changesEmps.get(i);
            recentActivitiesContainer.getChildren().add(
                    view.DashboardView.createActivityItem("+", "activity-new",
                            emp.getMessage(lang.getCurrentLanguageCode()),
                            java.text.MessageFormat.format(lang.getTranslatedText("madeby"), s.getFullName()),
                            emp.getEmployee() != null && emp.getEmployee().getStartDate() != null ? emp.getEmployee().getStartDate().toString() : "")
            );
        }

        // Last 2 leave records added (sorted by ID descending)
        List<ChangeLeave> leaves = changeLeaveDao.findAll();
        leaves.sort((a, b) -> Integer.compare(b.getId(), a.getId()));

        int leaveLimit = Math.min(2, leaves.size());
        for (int i = 0; i < leaveLimit; i++) {
            ChangeLeave leave = leaves.get(i);
            recentActivitiesContainer.getChildren().add(
                    view.DashboardView.createActivityItem("📅", "activity-leave",
                            leave.getMessage(lang.getCurrentLanguageCode()),
                            java.text.MessageFormat.format(lang.getTranslatedText("madeby"), s.getFullName()),
                            leave.getLeave().getStartDate() != null ? leave.getLeave().getStartDate().toString() : "")
            );
        }

        List<ChangeDept> changeDepts = changeDeptDao.findAll();
        changeDepts.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        int changeDeptsLimit = Math.min(2, changeDepts.size());
        for (int i = 0; i < changeDeptsLimit; i++) {
            ChangeDept changeDept = changeDepts.get(i);
            recentActivitiesContainer.getChildren().add(
                    view.DashboardView.createActivityItem("+", "activity-new",
                            changeDept.getMessage(lang.getCurrentLanguageCode()),
                            java.text.MessageFormat.format(lang.getTranslatedText("madeby"), s.getFullName()),
                            changeDept.getDepartment() != null && changeDept.getDepartment().getCreationDate() != null ? changeDept.getDepartment().getCreationDate().toString() : "")
            );
        }






        if (recentActivitiesContainer.getChildren().isEmpty()) {
            Text empty = new Text(lang.getTranslatedText("dashboard.noActivity"));
            empty.setStyle("-fx-fill: #7f8c8d; -fx-font-size: 13px;");
            recentActivitiesContainer.getChildren().add(empty);
        }
    }

    // ── "View More" Recent Activity Popup ─────────────────────────────────────
    @FXML
    private void handleViewMoreActivities() {
        view.DashboardView.showViewMorePopup(changeEmpDao, changeLeaveDao, changeDeptDao, this::applyRTLToScene);
    }
    // ─────────────────────────────────────────────────────────────────────────

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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Are you sure you want to logout?");
        confirm.setContentText("Any unsaved changes will be lost.");
        if (confirm.showAndWait().filter(b -> b == ButtonType.OK).isEmpty()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setResources(LanguageManager.getInstance().getCurrentBundle());
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();
            boolean wasFullscreen = stage.isFullScreen();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Employee Management System - Login");
            if (wasMaximized) stage.setMaximized(true);
            if (wasFullscreen) stage.setFullScreen(true);
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to logout", e);
            showAlert("Error", "Failed to logout. Please try again.");
        }
    }

    private void navigateTo(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setResources(LanguageManager.getInstance().getCurrentBundle());
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();
            Scene scene = new Scene(root, w, h);
            var css = getClass().getResource(cssPath);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to load view: " + fxmlPath, e);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setCurrentUser(String name, String role) {
        app.UserSession.getInstance().setUser(name, role);
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