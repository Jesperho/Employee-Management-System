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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @FXML private Button btnGenerateReport;
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
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final LeaveDao leaveDao = new LeaveDao();

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

        entity.LanguageManager langManager = entity.LanguageManager.getInstance();

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

        userRole.setText(langManager.getTranslatedText("user.role"));
        main_menu.setText(langManager.getTranslatedText("main_menu"));
        totalEmployees.setText(langManager.getTranslatedText("totalEmployees"));
        activeEmployees.setText(langManager.getTranslatedText("activeEmployees"));

        if (logoSubtitle != null) logoSubtitle.setText(langManager.getTranslatedText("app.subtitle"));
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
        if (btnGenerateReport != null) btnGenerateReport.setText("📊  " + langManager.getTranslatedText("action.generateReport"));

        if (searchField != null) searchField.setPromptText(langManager.getTranslatedText("search.prompt"));


        if (welcomeText != null) {
            app.UserSession s = app.UserSession.getInstance();
            String firstName = s.getFullName().split(" ")[0];
            String greeting = langManager.getTranslatedText("dashboard.welcome");
            welcomeText.setText(greeting + ", " + firstName + "!");
        }
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
                double leaveRate = (currentlyOnLeave * 100.0) / total;
                if (currentlyOnLeave == 0) {
                    employeeChangeText.setText("✓ Full staff");
                    employeeChangeText.getStyleClass().setAll("stat-change-positive");
                } else {
                    employeeChangeText.setText(String.format("%.0f%% on leave", leaveRate));
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

        if (totalPayroll != null) totalPayroll.setText("€" + String.format("%,d", totalPayrollAmount));
        if (avgSalary != null) avgSalary.setText("€" + String.format("%,d", avgSalaryAmount));
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

    // ── "View More" Recent Activity Popup ─────────────────────────────────────
    @FXML
    private void handleViewMoreActivities() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        // ── Header ──────────────────────────────────────────────────────────
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(22, 25, 18, 25));
        header.setSpacing(12);
        header.setStyle("-fx-background-color: linear-gradient(to right, #1a1f36, #2d3561);" +
                "-fx-background-radius: 15 15 0 0;");

        StackPane iconWrap = new StackPane();
        javafx.scene.shape.Circle iconCircle = new javafx.scene.shape.Circle(20);
        iconCircle.setStyle("-fx-fill: rgba(108,99,255,0.35);");
        Label iconLbl = new Label("🕐");
        iconLbl.setStyle("-fx-font-size: 16px;");
        iconWrap.getChildren().addAll(iconCircle, iconLbl);

        VBox titleBox = new VBox(2);
        Label titleLbl = new Label("Recent Activities");
        titleLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label subLbl  = new Label("All employee and leave events from the system");
        subLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 11px;");
        titleBox.getChildren().addAll(titleLbl, subLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white;" +
                "-fx-background-radius: 6; -fx-font-size: 14px; -fx-cursor: hand;" +
                "-fx-padding: 4 10;");
        closeBtn.setOnAction(e -> popup.close());

        header.getChildren().addAll(iconWrap, titleBox, spacer, closeBtn);

        // ── Build activity entries ────────────────────────────────────────
        VBox activityList = new VBox(0);
        activityList.setStyle("-fx-background-color: white;");

        List<Employee> employees = employeeDao.findAll();
        employees.sort((a, b) -> Integer.compare(b.getId(), a.getId()));

        List<Leave> leaves = leaveDao.findAll();
        leaves.sort((a, b) -> Integer.compare(b.getId(), a.getId()));

        // Interleave entries: all employees + all leaves, styled differently
        int maxRows = Math.max(employees.size(), leaves.size());
        int empIdx = 0, leaveIdx = 0;
        int rowNum = 0;

        while (empIdx < employees.size() || leaveIdx < leaves.size()) {
            if (empIdx < employees.size()) {
                Employee emp = employees.get(empIdx++);
                String dept = emp.getDepartment() != null
                        ? emp.getDepartment().getDepartmentName() : "No Department";
                String date = emp.getStartDate() != null ? emp.getStartDate().toString() : "—";
                activityList.getChildren().add(
                        buildActivityRow(rowNum++,
                                "👤", "#e8f4ff", "#1565c0",
                                "New Employee Added",
                                emp.getFullName() + " joined " + dept,
                                date, "Employee", "#1565c0", "#dbeafe"));
            }
            if (leaveIdx < leaves.size()) {
                Leave leave = leaves.get(leaveIdx++);
                String empName = leave.getEmployee() != null
                        ? leave.getEmployee().getFullName() : "Unknown";
                String type  = leave.getType() != null ? leave.getType() : "Leave";
                String date  = leave.getStartDate() != null ? leave.getStartDate().toString() : "—";
                String[] lc  = getActivityLeaveColor(type);
                activityList.getChildren().add(
                        buildActivityRow(rowNum++,
                                getActivityLeaveEmoji(type), lc[0], lc[1],
                                type + " Leave",
                                empName + " — " + leave.getReason() != null ? (empName + " — " + (leave.getReason() != null && !leave.getReason().isEmpty() ? leave.getReason() : "No reason")) : empName,
                                date, type, lc[1], lc[2]));
            }
        }

        if (activityList.getChildren().isEmpty()) {
            Label empty = new Label("No activity found.");
            empty.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 30;");
            activityList.getChildren().add(empty);
        }

        // ── Scroll pane wraps list ────────────────────────────────────────
        ScrollPane scroll = new ScrollPane(activityList);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
        scroll.setPrefHeight(460);

        // ── Footer ───────────────────────────────────────────────────────
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(14, 25, 16, 25));
        footer.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 0 0 15 15;" +
                "-fx-border-color: #e8eaf0; -fx-border-width: 1 0 0 0;");
        Label countLbl = new Label(
                "Showing " + activityList.getChildren().size() + " activities  •  " +
                        employees.size() + " employees  •  " + leaves.size() + " leave records");
        countLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
        HBox.setHgrow(countLbl, Priority.ALWAYS);

        Button doneBtn = new Button("Close");
        doneBtn.setStyle("-fx-background-color: linear-gradient(to right,#6c63ff,#5a52d4);" +
                "-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13px;" +
                "-fx-padding:9 28;-fx-background-radius:8;-fx-cursor:hand;");
        doneBtn.setOnAction(e -> popup.close());
        footer.getChildren().addAll(countLbl, doneBtn);

        // ── Assemble card ─────────────────────────────────────────────────
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.28),24,0,0,6);");
        card.getChildren().addAll(header, scroll, footer);
        card.setMaxWidth(600);
        card.setMinWidth(560);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(12));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    /** Builds one styled row in the activity popup list */
    private HBox buildActivityRow(int index, String emoji, String iconBg, String iconFg,
                                  String title, String detail, String date,
                                  String badgeText, String badgeFg, String badgeBg) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 22, 14, 22));
        row.setStyle("-fx-background-color: " + (index % 2 == 0 ? "white" : "#fafbff") + ";" +
                "-fx-border-color: #f0f2f8; -fx-border-width: 0 0 1 0;");

        // Icon bubble
        StackPane bubble = new StackPane();
        bubble.setMinSize(42, 42);
        bubble.setMaxSize(42, 42);
        javafx.scene.shape.Circle bg = new javafx.scene.shape.Circle(21);
        bg.setStyle("-fx-fill: " + iconBg + ";");
        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size: 16px;");
        bubble.getChildren().addAll(bg, emojiLbl);

        // Text section
        VBox textBox = new VBox(3);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Badge
        Label badge = new Label(badgeText);
        badge.setStyle("-fx-background-color: " + badgeBg + "; -fx-text-fill: " + badgeFg + ";" +
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 8;" +
                "-fx-background-radius: 10;");
        titleRow.getChildren().addAll(titleLbl, badge);

        Label detailLbl = new Label(detail);
        detailLbl.setStyle("-fx-text-fill: #5f6c7b; -fx-font-size: 12px;");
        detailLbl.setWrapText(true);
        detailLbl.setMaxWidth(350);
        textBox.getChildren().addAll(titleRow, detailLbl);

        // Date on the right
        Label dateLbl = new Label("📅 " + date);
        dateLbl.setStyle("-fx-text-fill: #9ba8b7; -fx-font-size: 11px;");
        dateLbl.setMinWidth(95);
        dateLbl.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(bubble, textBox, dateLbl);
        return row;
    }

    private String[] getActivityLeaveColor(String type) {
        if (type == null) return new String[]{"#f0f0ff", "#6c63ff", "#ede9ff"};
        return switch (type.toLowerCase()) {
            case "sick"      -> new String[]{"#fff0f0", "#c0392b", "#fde8e8"};
            case "vacation"  -> new String[]{"#e3f2fd", "#1565c0", "#dbeafe"};
            case "maternity" -> new String[]{"#f3e5f5", "#7b1fa2", "#ede7f6"};
            case "personal"  -> new String[]{"#e8f5e9", "#2e7d32", "#dcfce7"};
            case "exam"      -> new String[]{"#fff8e1", "#f57f17", "#fef9c3"};
            default          -> new String[]{"#f0f0ff", "#6c63ff", "#ede9ff"};
        };
    }

    private String getActivityLeaveEmoji(String type) {
        if (type == null) return "📋";
        return switch (type.toLowerCase()) {
            case "sick"      -> "🤒";
            case "vacation"  -> "🏖";
            case "maternity" -> "👶";
            case "personal"  -> "🏠";
            case "exam"      -> "📚";
            default          -> "📋";
        };
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