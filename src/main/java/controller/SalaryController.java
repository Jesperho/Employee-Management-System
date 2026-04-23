package controller;

import dao.DepartmentDao;
import dao.EmployeeDao;
import entity.LanguageManager;
import entity.Department;
import entity.Employee;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import utils.BaseController;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// 1. Extend BaseController instead of implementing Initializable
public class SalaryController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDepartment;

    @FXML private Text breadcrumbText;
    @FXML private Text pageTitleText;
    @FXML private Button btnRunPayroll;

    @FXML private Text totalPayroll;
    @FXML private Text avgSalary;
    @FXML private Text highestSalary;
    @FXML private Text employeesPaid;

    @FXML private Text totalPayrollLabelText;
    @FXML private Text avgSalaryLabelText;
    @FXML private Text highestSalaryLabelText;
    @FXML private Text employeesPaidLabelText;

    @FXML private TableView<ObservableList<String>> salaryTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colDepartment;
    @FXML private TableColumn<ObservableList<String>, String> colJobTitle;
    @FXML private TableColumn<ObservableList<String>, String> colPosition;
    @FXML private TableColumn<ObservableList<String>, String> colSalary;
    @FXML private TableColumn<ObservableList<String>, String> colGender;
    @FXML private TableColumn<ObservableList<String>, String> colActions;

    @FXML private Text totalPayout; // kept for backward compat — node removed from FXML
    @FXML private Text pageInfo;

    // Sidebar user profile (populated from UserSession)
    @FXML private Text avatarInitials;
    @FXML private Text userName;
    @FXML private Text userRole;

    // Navigation
    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final DepartmentDao departmentDao = new DepartmentDao();

    private ObservableList<ObservableList<String>> salaryData = FXCollections.observableArrayList();
    // Tracks the rows currently visible after filtering — used for stats & payroll
    private List<ObservableList<String>> currentFilteredData = new java.util.ArrayList<>();

    // 2. Replace initialize() with setupController()
    @Override
    protected void setupController() {
        updateUserProfile();
        setupTable();
        loadFromDatabase();
        setupFilters();
        if (searchField != null)
            searchField.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    // 3. Add the required updateUIText method
    @Override
    protected void updateUIText(String newLanguage) {
        System.out.println("Salary View updating to: " + newLanguage);
        entity.LanguageManager lang = entity.LanguageManager.getInstance();

        if (btnDashboard != null) btnDashboard.setText("📊  " + lang.getTranslatedText("nav.dashboard"));
        if (btnEmployees != null) btnEmployees.setText("👥  " + lang.getTranslatedText("nav.employees"));
        if (btnDepartments != null) btnDepartments.setText("🏢  " + lang.getTranslatedText("nav.departments"));
        if (btnLeave != null) btnLeave.setText("📅  " + lang.getTranslatedText("nav.leave"));
        if (btnSalary != null) btnSalary.setText("💰  " + lang.getTranslatedText("nav.salary"));
        if (btnLogout != null) btnLogout.setText("🚪  " + lang.getTranslatedText("nav.logout"));
        if (mainMenuLabel != null) mainMenuLabel.setText(lang.getTranslatedText("main_menu"));

        if (breadcrumbText != null) breadcrumbText.setText(lang.getTranslatedText("salary.breadcrumb"));
        if (pageTitleText != null) pageTitleText.setText(lang.getTranslatedText("salary.pageTitle"));
        if (btnRunPayroll != null) btnRunPayroll.setText(lang.getTranslatedText("salary.runPayroll"));
        if (searchField != null) searchField.setPromptText(lang.getTranslatedText("search.salary"));
        if (filterDepartment != null) filterDepartment.setPromptText(lang.getTranslatedText("salary.filterAllDepts"));

        if (totalPayrollLabelText != null) totalPayrollLabelText.setText(lang.getTranslatedText("salary.totalPayroll"));
        if (avgSalaryLabelText != null) avgSalaryLabelText.setText(lang.getTranslatedText("salary.avgSalary"));
        if (highestSalaryLabelText != null) highestSalaryLabelText.setText(lang.getTranslatedText("salary.highestSalary"));
        if (employeesPaidLabelText != null) employeesPaidLabelText.setText(lang.getTranslatedText("salary.employeesPaid"));

        if (colId != null) colId.setText(lang.getTranslatedText("salary.col.id"));
        if (colName != null) colName.setText(lang.getTranslatedText("salary.col.name"));
        if (colDepartment != null) colDepartment.setText(lang.getTranslatedText("salary.col.department"));
        if (colJobTitle != null) colJobTitle.setText(lang.getTranslatedText("salary.col.jobTitle"));
        if (colPosition != null) colPosition.setText(lang.getTranslatedText("salary.col.position"));
        if (colSalary != null) colSalary.setText(lang.getTranslatedText("salary.col.salary"));
        if (colGender != null) colGender.setText(lang.getTranslatedText("salary.col.gender"));
        if (colActions != null) colActions.setText(lang.getTranslatedText("salary.col.actions"));

        if (pageInfo != null) pageInfo.setText(lang.getTranslatedText("btn.pageInfo"));
        setupFilters();
    }

    private void updateUserProfile() {
        app.UserSession s = app.UserSession.getInstance();
        if (avatarInitials != null) avatarInitials.setText(s.getInitials());
        if (userName != null) userName.setText(s.getFullName());
        if (userRole != null) userRole.setText(s.getRole());
    }

    // Loads all employees from DB and maps their salary data to the table rows
    private void loadFromDatabase() {
        salaryData.clear();
        List<Employee> employees = employeeDao.findAll();
        for (Employee emp : employees) {
            String deptName = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "N/A";
            int salary = emp.getSalary() != null ? emp.getSalary().intValue() : 0;
            salaryData.add(FXCollections.observableArrayList(
                    String.valueOf(emp.getId()),
                    emp.getFullName() != null ? emp.getFullName() : "N/A",
                    deptName,
                    emp.getPosition() != null ? emp.getPosition() : "N/A",
                    emp.getRole() != null ? emp.getRole() : "N/A",
                    String.valueOf(salary),
                    emp.getGender() != null ? emp.getGender() : "N/A"
            ));
        }
        salaryTable.setItems(salaryData);
        currentFilteredData = new java.util.ArrayList<>(salaryData);
        updateStats();
    }

    // Populates the department filter from DB
    private void setupFilters() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        List<String> deptNames = departmentDao.findAll().stream()
                .map(Department::getDepartmentName)
                .collect(Collectors.toList());
        deptNames.add(0, lang.getTranslatedText("salary.filterAllDepts"));
        filterDepartment.setItems(FXCollections.observableArrayList(deptNames));
        filterDepartment.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        String dept = filterDepartment.getValue();
        String search = searchField.getText().trim().toLowerCase();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(salaryData, row -> {
            if (dept != null && !dept.equals(lang.getTranslatedText("salary.filterAllDepts")) && !row.get(2).equalsIgnoreCase(dept)) return false;
            if (!search.isEmpty()) {
                if (!row.get(1).toLowerCase().contains(search) && !row.get(2).toLowerCase().contains(search)) return false;
            }
            return true;
        });
        salaryTable.setItems(filtered);

        // Keep a snapshot of the visible rows so stats + payroll use only what's shown
        currentFilteredData = new java.util.ArrayList<>(filtered);
        updateFilteredStats(currentFilteredData, dept);
    }

    private void setupTable() {
        salaryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colDepartment.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colJobTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colPosition.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        // Salary column — coloured cell delegated to SalaryView
        colSalary.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        colSalary.setCellFactory(view.SalaryView.buildSalaryColorColumn());

        // Actions column — delegated to SalaryView
        colActions.setCellFactory(view.SalaryView.buildActionsColumn(
                employeeDao, this::loadFromDatabase, this::applyRTLToScene));
    }

    private void updateStats() {
        updateFilteredStats(salaryData, null);
    }

    // Updates the 4 stat cards based on any subset of rows (filtered or all)
    private void updateFilteredStats(List<ObservableList<String>> rows, String deptLabel) {
        int total = rows.stream().mapToInt(r -> {
            try { return Integer.parseInt(r.get(5)); } catch (NumberFormatException e) { return 0; }
        }).sum();
        int avg = rows.isEmpty() ? 0 : total / rows.size();
        int max = rows.stream().mapToInt(r -> {
            try { return Integer.parseInt(r.get(5)); } catch (NumberFormatException e) { return 0; }
        }).max().orElse(0);

        totalPayroll.setText("€" + total);
        avgSalary.setText("€" + avg);
        highestSalary.setText("€" + max);
        employeesPaid.setText(String.valueOf(rows.size()));
    }

    @FXML
    private void handleRunPayroll() {
        // Data calculation — stays in controller
        List<ObservableList<String>> dataToProcess = currentFilteredData.isEmpty() && filterDepartment.getValue() == null
                ? new java.util.ArrayList<>(salaryData)
                : currentFilteredData;

        int total    = dataToProcess.stream().mapToInt(r -> {
            try { return Integer.parseInt(r.get(5)); } catch (NumberFormatException e) { return 0; }
        }).sum();
        int empCount = dataToProcess.size();

        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        String deptVal       = filterDepartment.getValue();
        String allDeptsLabel = lang.getTranslatedText("salary.filterAllDepts");
        boolean isDeptFilter = deptVal != null && !deptVal.equals(allDeptsLabel);
        String scopeLabel    = isDeptFilter ? deptVal : allDeptsLabel;

        // Popup building delegated to SalaryView
        view.SalaryView.showPayrollPopup(total, empCount, scopeLabel, isDeptFilter, this::applyRTLToScene);
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML private void handlePreviousPage() {}
    @FXML private void handleNextPage() {}

    // ===== NAVIGATION =====
    @FXML private void handleDashboard()    { navigateTo("/fxml/dashboard.fxml",  "/css/dashboard.css"); }
    @FXML private void handleEmployees()    { navigateTo("/fxml/employee.fxml",   "/css/management.css"); }
    @FXML private void handleDepartments()  { navigateTo("/fxml/department.fxml", "/css/management.css"); }
    @FXML private void handleLeave()        { navigateTo("/fxml/leave.fxml",      "/css/management.css"); }
    @FXML private void handleSalary()       { /* already here */ }

    @FXML
    private void handleLogout() { navigateTo("/fxml/login.fxml", "/css/login.css"); }

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
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}