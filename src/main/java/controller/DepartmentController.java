package controller;

import dao.*;
import entity.*;
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
import utils.BaseController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Make sure to import BaseController if it's in a different package!
// import util.BaseController;

// 1. Extend BaseController instead of implementing Initializable
public class DepartmentController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<Department> filterDepartment;
    @FXML private ComboBox<String> filterCapacity;

    @FXML private Text breadcrumbText;
    @FXML private Text pageTitleText;
    @FXML private Button btnAddDepartment;

    @FXML private Text totalDeptCount;
    @FXML private Text activeDeptCount;
    @FXML private Text totalEmpInDepts;
    @FXML private Text avgCapacity;

    @FXML private Text totalDeptLabelText;
    @FXML private Text activeDeptLabelText;
    @FXML private Text totalEmpLabelText;
    @FXML private Text avgCapacityLabelText;

    @FXML private TableView<ObservableList<String>> departmentTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colCapacity;
    @FXML private TableColumn<ObservableList<String>, String> colEmployees;
    @FXML private TableColumn<ObservableList<String>, String> colCreationDate;
    @FXML private TableColumn<ObservableList<String>, String> colActions;

    @FXML private Text pageInfo;

    // Employee detail view (full-page swap on double-click)
    @FXML private ScrollPane deptScrollPane;
    @FXML private VBox employeeDetailView;
    @FXML private Button btnBackToDepts;
    @FXML private TextField empSearchField;
    @FXML private Text empStatTotal;
    @FXML private Text empStatFullTime;
    @FXML private Text empStatPartTime;
    @FXML private Text empStatAvgSalary;

    @FXML private Text empStatTotalLabelText;
    @FXML private Text empStatFullTimeLabelText;
    @FXML private Text empStatPartTimeLabelText;
    @FXML private Text empStatAvgSalaryLabelText;

    // Employee detail section
    @FXML private VBox employeeSection;
    @FXML private Text employeeSectionTitle;
    @FXML private TableView<ObservableList<String>> employeeTable;
    @FXML private TableColumn<ObservableList<String>, String> empColId;
    @FXML private TableColumn<ObservableList<String>, String> empColName;
    @FXML private TableColumn<ObservableList<String>, String> empColDept;
    @FXML private TableColumn<ObservableList<String>, String> empColJobTitle;
    @FXML private TableColumn<ObservableList<String>, String> empColStartDate;
    @FXML private TableColumn<ObservableList<String>, String> empColContract;
    @FXML private TableColumn<ObservableList<String>, String> empColGender;
    @FXML private TableColumn<ObservableList<String>, String> empColActions;

    // Navigation
    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    // Sidebar user profile (populated from UserSession)
    @FXML private Text avatarInitials;
    @FXML private Text userName;
    @FXML private Text userRole;

    private final DepartmentDao departmentDao = new DepartmentDao();
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final CompanyDao companyDao = new CompanyDao();
    private final ChangeDeptDao changeDeptDao = new ChangeDeptDao();
    private final DepartmentTranslationDao departmentTranslationDao = new DepartmentTranslationDao();

    private ObservableList<ObservableList<String>> deptData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> employeeData = FXCollections.observableArrayList();
    private List<Employee> currentDeptEmployees = new java.util.ArrayList<>();

    // 2. Replace initialize() with setupController()
    @Override
    protected void setupController() {
        updateUserProfile();
        setupTable();
        setupEmployeeTable();
        loadFromDatabase();
        setupFilters();
        if (searchField != null)
            searchField.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    // 3. Add the required updateUIText method
    @Override
    protected void updateUIText(String newLanguage) {
        System.out.println("Department View updating to: " + newLanguage);
        entity.LanguageManager lang = entity.LanguageManager.getInstance();

        if (btnDashboard != null) btnDashboard.setText("📊  " + lang.getTranslatedText("nav.dashboard"));
        if (btnEmployees != null) btnEmployees.setText("👥  " + lang.getTranslatedText("nav.employees"));
        if (btnDepartments != null) btnDepartments.setText("🏢  " + lang.getTranslatedText("nav.departments"));
        if (btnLeave != null) btnLeave.setText("📅  " + lang.getTranslatedText("nav.leave"));
        if (btnSalary != null) btnSalary.setText("💰  " + lang.getTranslatedText("nav.salary"));
        if (btnLogout != null) btnLogout.setText("🚪  " + lang.getTranslatedText("nav.logout"));

        if (breadcrumbText != null) breadcrumbText.setText(lang.getTranslatedText("dept.breadcrumb"));
        if (pageTitleText != null) pageTitleText.setText(lang.getTranslatedText("dept.pageTitle"));
        if (btnAddDepartment != null) btnAddDepartment.setText(lang.getTranslatedText("dept.addBtn"));
        if (btnBackToDepts != null) btnBackToDepts.setText(lang.getTranslatedText("dept.backToDepts"));

        if (searchField != null) searchField.setPromptText(lang.getTranslatedText("search.departments"));
        if (empSearchField != null) empSearchField.setPromptText(lang.getTranslatedText("dept.searchEmployees"));
        if (filterDepartment != null) filterDepartment.setPromptText(lang.getTranslatedText("dept.filterAllDepts"));
        if (filterCapacity != null) filterCapacity.setPromptText(lang.getTranslatedText("dept.filterAllCaps"));

        if (totalDeptLabelText != null) totalDeptLabelText.setText(lang.getTranslatedText("dept.totalDepts"));
        if (activeDeptLabelText != null) activeDeptLabelText.setText(lang.getTranslatedText("dept.active"));
        if (totalEmpLabelText != null) totalEmpLabelText.setText(lang.getTranslatedText("dept.totalEmployees"));
        if (avgCapacityLabelText != null) avgCapacityLabelText.setText(lang.getTranslatedText("dept.avgCapacity"));

        if (empStatTotalLabelText != null) empStatTotalLabelText.setText(lang.getTranslatedText("dept.emp.total"));
        if (empStatFullTimeLabelText != null) empStatFullTimeLabelText.setText(lang.getTranslatedText("dept.emp.fullTime"));
        if (empStatPartTimeLabelText != null) empStatPartTimeLabelText.setText(lang.getTranslatedText("dept.emp.partTime"));
        if (empStatAvgSalaryLabelText != null) empStatAvgSalaryLabelText.setText(lang.getTranslatedText("dept.emp.avgSalary"));

        if (colId != null) colId.setText(lang.getTranslatedText("dept.col.id"));
        if (colName != null) colName.setText(lang.getTranslatedText("dept.col.name"));
        if (colCapacity != null) colCapacity.setText(lang.getTranslatedText("dept.col.capacity"));
        if (colEmployees != null) colEmployees.setText(lang.getTranslatedText("dept.col.employees"));
        if (colCreationDate != null) colCreationDate.setText(lang.getTranslatedText("dept.col.created"));
        if (colActions != null) colActions.setText(lang.getTranslatedText("dept.col.actions"));

        if (empColId != null) empColId.setText(lang.getTranslatedText("emp.col.id"));
        if (empColName != null) empColName.setText(lang.getTranslatedText("emp.col.name"));
        if (empColDept != null) empColDept.setText(lang.getTranslatedText("emp.col.department"));
        if (empColJobTitle != null) empColJobTitle.setText(lang.getTranslatedText("emp.col.jobTitle"));
        if (empColStartDate != null) empColStartDate.setText(lang.getTranslatedText("emp.col.startDate"));
        if (empColContract != null) empColContract.setText(lang.getTranslatedText("emp.col.contract"));
        if (empColGender != null) empColGender.setText(lang.getTranslatedText("emp.col.gender"));
        if (empColActions != null) empColActions.setText(lang.getTranslatedText("emp.col.actions"));

        if (employeeSectionTitle != null) employeeSectionTitle.setText(lang.getTranslatedText("nav.employees") + " - " + lang.getTranslatedText("dept.pageTitle"));

        loadFromDatabase();
        setupFilters();
        applyFilters();
        setupTable();

        departmentTable.setItems(null);
        departmentTable.setItems(deptData);
        departmentTable.refresh();
    }

    private void updateUserProfile() {
        app.UserSession s = app.UserSession.getInstance();
        if (avatarInitials != null) avatarInitials.setText(s.getInitials());
        if (userName != null) userName.setText(s.getFullName());
        if (userRole != null) userRole.setText(s.getRole());
    }

    // Loads all departments from DB and refreshes the table and stats
    private void loadFromDatabase() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        deptData.clear();

        List<Department> departments = departmentDao.findAllWithTranslations();

        for (Department dept : departments) {
            long empCount = departmentDao.countEmployees(dept.getId());

            deptData.add(FXCollections.observableArrayList(
                    String.valueOf(dept.getId()),
                    dept.getTranslatedName(lang.getCurrentLanguageCode()),
                    String.valueOf(dept.getCapacity()),
                    String.valueOf(empCount),
                    dept.getTranslatedDate(lang.getCurrentLanguageCode()) != null ? dept.getTranslatedDate(lang.getCurrentLanguageCode()) : "N/A",
                    dept.getDescription() != null ? dept.getDescription() : ""
            ));
        }

        departmentTable.setItems(deptData);
        updateStats();
    }

    // Populates the filter dropdowns; department names come from the DB
    private void setupFilters() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();

        filterCapacity.setItems(FXCollections.observableArrayList(
                lang.getTranslatedText("dept.filterAllCaps"),
                lang.getTranslatedText("dept.cap.small"),
                lang.getTranslatedText("dept.cap.medium"),
                lang.getTranslatedText("dept.cap.large")
        ));

        filterCapacity.getSelectionModel().selectFirst(); // default = "All"

        List<Department> departments = departmentDao.findAllWithTranslations();

        filterDepartment.setItems(FXCollections.observableArrayList(departments));

        filterDepartment.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null
                        ? null
                        : d.getTranslatedName(lang.getCurrentLanguageCode()));
            }
        });

        filterDepartment.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null
                        ? null
                        : d.getTranslatedName(lang.getCurrentLanguageCode()));
            }
        });

        filterDepartment.getItems().add(0, null); // "All departments"
        filterDepartment.setPromptText(lang.getTranslatedText("dept.filterAllDepts"));

        filterDepartment.setOnAction(e -> applyFilters());

        filterCapacity.setOnAction(e -> applyFilters());
    }

    private boolean matchesCapacityFilter(String capFilter, int capacity) {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();

        if (capFilter == null || capFilter.equals(lang.getTranslatedText("dept.filterAllCaps"))) {
            return true;
        }

        if (capFilter.equals(lang.getTranslatedText("dept.cap.small"))) {
            return capacity < 30;
        }

        if (capFilter.equals(lang.getTranslatedText("dept.cap.medium"))) {
            return capacity >= 30 && capacity <= 50;
        }

        if (capFilter.equals(lang.getTranslatedText("dept.cap.large"))) {
            return capacity > 50;
        }

        return true;
    }

    private void applyFilters() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        Department dept = filterDepartment.getValue();
        Department selectedDept = filterDepartment.getValue();
        String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String cap = filterCapacity.getValue();

        // Only do the full-page swap when dept filter changes AND no search text
        // (search should filter the dept TABLE, not jump to employee view)
        if (dept != null) {
            int deptId = -1;
            for (ObservableList<String> row : deptData) {
                if (!row.get(0).equals(String.valueOf(dept.getId()))) {
                    deptId = Integer.parseInt(row.get(0));
                    break;
                }
            }
            if (dept != null && search.isEmpty()) {
                showEmployeesForDepartment(
                        dept.getTranslatedName(lang.getCurrentLanguageCode()),
                        dept.getId()
                );
                return;
            }
        } else if ((dept == null || dept.equals("All Departments")) && !search.isEmpty()) {
            // Make sure we're on the dept view (not employee detail view) when searching
            if (!deptScrollPane.isVisible()) {
                deptScrollPane.setVisible(true);
                deptScrollPane.setManaged(true);
                employeeDetailView.setVisible(false);
                employeeDetailView.setManaged(false);
            }
        }

        // row indices: 0=id, 1=name, 2=capacity, 3=empCount, 4=creationDate, 5=description
        FilteredList<ObservableList<String>> filtered = new FilteredList<>(deptData, row -> {
            if (selectedDept != null) {
                if (!row.get(0).equals(String.valueOf(selectedDept.getId()))) {
                    return false;
                }
            }
            if (cap != null) {
                try {
                    int capacity = Integer.parseInt(row.get(2));
                    if (!matchesCapacityFilter(cap, capacity)) return false;
                } catch (NumberFormatException ignored) {}
            }

            if (!search.isEmpty()) {
                // Search by dept name (index 1) OR description (index 5)
                boolean nameMatch = row.get(1).toLowerCase().contains(search);
                boolean descMatch = row.size() > 5 && row.get(5).toLowerCase().contains(search);
                if (!nameMatch && !descMatch) return false;
            }
            return true;
        });
        departmentTable.setItems(filtered);
    }

    // Loads employees for a dept and swaps to the full-page employee detail view
    private void showEmployeesForDepartment(String deptName, int deptId) {
        List<Employee> employees = employeeDao.findByDepartment(deptId);
        currentDeptEmployees = new java.util.ArrayList<>(employees);
        populateEmployeeTable(employees, deptName);

        // Update stats
        long ft = employees.stream().filter(e -> "Full-time".equalsIgnoreCase(e.getContractType()) || "Full Time".equalsIgnoreCase(e.getContractType())).count();
        long pt = employees.stream().filter(e -> "Part-time".equalsIgnoreCase(e.getContractType()) || "Part Time".equalsIgnoreCase(e.getContractType())).count();
        double avg = employees.stream().filter(e -> e.getSalary() != null).mapToDouble(e -> e.getSalary()).average().orElse(0);

        if (empStatTotal != null)    empStatTotal.setText(String.valueOf(employees.size()));
        if (empStatFullTime != null) empStatFullTime.setText(String.valueOf(ft));
        if (empStatPartTime != null) empStatPartTime.setText(String.valueOf(pt));
        if (empStatAvgSalary != null) empStatAvgSalary.setText("€" + (int) avg);

        employeeSectionTitle.setText("🏢  " + deptName + "  —  " + employees.size() + " Employees");

        // Swap to employee detail view
        deptScrollPane.setVisible(false);
        deptScrollPane.setManaged(false);
        employeeDetailView.setVisible(true);
        employeeDetailView.setManaged(true);
    }

    private void populateEmployeeTable(List<Employee> employees, String deptName) {
        employeeData.clear();
        for (Employee emp : employees) {
            employeeData.add(FXCollections.observableArrayList(
                    String.valueOf(emp.getId()),
                    emp.getFullName() != null ? emp.getFullName() : "N/A",
                    deptName,
                    emp.getPosition() != null ? emp.getPosition() : "N/A",
                    emp.getStartDate() != null ? emp.getStartDate().toString() : "N/A",
                    emp.getContractType() != null ? emp.getContractType() : "N/A",
                    emp.getGender() != null ? emp.getGender() : "N/A"
            ));
        }
        employeeTable.setItems(employeeData);
    }

    private void hideEmployeeSection() {
        // no-op kept for compatibility
    }

    @FXML
    private void handleShowAllDepartments() {
        // Swap back to dept view
        employeeDetailView.setVisible(false);
        employeeDetailView.setManaged(false);
        deptScrollPane.setVisible(true);
        deptScrollPane.setManaged(true);
        filterDepartment.setValue(null);
    }

    @FXML
    private void handleEmployeeSearch() {
        String q = empSearchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            employeeTable.setItems(employeeData);
            return;
        }
        javafx.collections.transformation.FilteredList<ObservableList<String>> filtered =
                new javafx.collections.transformation.FilteredList<>(employeeData, row ->
                        row.get(1).toLowerCase().contains(q) ||
                                row.get(3).toLowerCase().contains(q) ||
                                row.get(5).toLowerCase().contains(q)
                );
        employeeTable.setItems(filtered);
    }

    private String getOrDefault(String value) {
        if (value == null) return "N/A";
        return value;
    }

    private void setupTable() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        String langCode = lang.getAbreviation(lang.getLanguage());

        departmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(data -> new SimpleStringProperty(departmentTranslationDao.formatNumber(Double.parseDouble(data.getValue().get(0)), lang.getLanguage())));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colCapacity.setCellValueFactory(data -> new SimpleStringProperty(departmentTranslationDao.formatNumber(Double.parseDouble(data.getValue().get(2)), lang.getLanguage())));
        colEmployees.setCellValueFactory(data -> new SimpleStringProperty(departmentTranslationDao.formatNumber(Double.parseDouble(data.getValue().get(3)), lang.getLanguage())));
        colCreationDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));


        // Double-click a row to show employees for that department
        departmentTable.setRowFactory(tv -> {
            TableRow<ObservableList<String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ObservableList<String> rowData = row.getItem();
                    String deptName = rowData.get(1);
                    int deptId = Integer.parseInt(rowData.get(0));
                    showEmployeesForDepartment(deptName, deptId);
                }
            });
            return row;
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button(lang.getTranslatedText("btn.edit"));
            private final Button deleteBtn = new Button(lang.getTranslatedText("btn.delete"));
            private final HBox box = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showDeptFormPopup(entity.LanguageManager.getInstance().getTranslatedText("dept.col.actions"), row);
                });
                deleteBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    handleDeleteDepartment(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupEmployeeTable() {
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        empColId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        empColName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        empColDept.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        empColJobTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        empColStartDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        empColContract.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        empColGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        empColActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn   = new Button("View");
            private final Button editBtn   = new Button("Edit");
            private final Button deleteBtn = new Button("Del");
            private final HBox   box       = new HBox(5, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < currentDeptEmployees.size())
                        showEmpViewPopup(currentDeptEmployees.get(idx));
                });
                editBtn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < currentDeptEmployees.size())
                        showEmpEditPopup(currentDeptEmployees.get(idx));
                });
                deleteBtn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < currentDeptEmployees.size())
                        handleDeleteEmployee(currentDeptEmployees.get(idx));
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void showEmpViewPopup(Employee emp) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setTitle("Employee Details");

        // Header gradient banner
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 22, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1f36, #2d3561); -fx-background-radius: 15 15 0 0;");

        // Avatar circle with initials
        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
        avatarCircle.setStyle("-fx-fill: linear-gradient(to bottom right, #6c63ff, #4834d4);");
        String initials = emp.getFullName() != null && !emp.getFullName().isEmpty()
                ? (emp.getFullName().trim().contains(" ")
                ? String.valueOf(emp.getFullName().charAt(0)) + emp.getFullName().trim().split(" ")[1].charAt(0)
                : String.valueOf(emp.getFullName().charAt(0)))
                : "?";
        javafx.scene.text.Text avatarText = new javafx.scene.text.Text(initials.toUpperCase());
        avatarText.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarCircle, avatarText);

        javafx.scene.text.Text nameText = new javafx.scene.text.Text(emp.getFullName() != null ? emp.getFullName() : "N/A");
        nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        String deptName = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "N/A";
        javafx.scene.text.Text roleText = new javafx.scene.text.Text(
                (emp.getPosition() != null ? emp.getPosition() : "Employee") + "  •  " + deptName);
        roleText.setStyle("-fx-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");
        header.getChildren().addAll(avatar, nameText, roleText);

        // Info grid
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(14);
        grid.setPadding(new Insets(24, 30, 10, 30));

        String salary = emp.getSalary() != null ? "€" + String.format("%,.0f", emp.getSalary()) : "N/A";
        addInfoRow(grid, 0, "🪪  Employee ID",  String.valueOf(emp.getId()),                    "📅  Start Date",  emp.getStartDate() != null ? emp.getStartDate().toString() : "N/A");
        addInfoRow(grid, 1, "⚧  Gender",        emp.getGender() != null ? emp.getGender() : "N/A", "📋  Contract",  emp.getContractType() != null ? emp.getContractType() : "N/A");
        addInfoRow(grid, 2, "💰  Salary",        salary,                                        "🏢  Department", deptName);


        // Close button
        Button closeBtn = new Button("✕  Close");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> popup.close());
        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 30, 24, 30));

        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);");
        card.getChildren().addAll(header, grid, footer);
        card.setMaxWidth(460);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        var css = getClass().getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void addInfoRow(GridPane grid, int row, String label1, String val1, String label2, String val2) {
        Label l1 = new Label(label1);
        l1.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label v1 = new Label(val1);
        v1.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox col1 = new VBox(3, l1, v1);
        col1.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 8; -fx-padding: 10 14;");
        col1.setMinWidth(160);
        grid.add(col1, 0, row);

        if (!label2.isEmpty()) {
            Label l2 = new Label(label2);
            l2.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
            Label v2 = new Label(val2);
            v2.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
            VBox col2 = new VBox(3, l2, v2);
            col2.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 8; -fx-padding: 10 14;");
            col2.setMinWidth(160);
            grid.add(col2, 1, row);
        } else {
            GridPane.setColumnSpan(col1, 2);
        }
    }

    private void showEmpEditPopup(Employee emp) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle("Edit Employee");

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Text formTitle = new Text("Edit Employee");
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField nameField = createFormField("Full Name");
        nameField.setText(emp.getFullName() != null ? emp.getFullName() : "");

        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        genderCombo.setValue(emp.getGender());
        genderCombo.setPrefWidth(200);
        genderCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        List<Department> departments = departmentDao.findAll();
        ComboBox<Department> deptCombo = new ComboBox<>(FXCollections.observableArrayList(departments));
        deptCombo.setPrefWidth(200);
        deptCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");
        deptCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null : d.getDepartmentName());
            }
        });
        deptCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Department d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null : d.getDepartmentName());
            }
        });
        if (emp.getDepartment() != null) {
            departments.stream().filter(d -> d.getId() == emp.getDepartment().getId())
                    .findFirst().ifPresent(deptCombo::setValue);
        }

        TextField jobTitleField = createFormField("Job Title");
        jobTitleField.setText(emp.getPosition() != null ? emp.getPosition() : "");


        DatePicker startDate = new DatePicker(emp.getStartDate());
        startDate.setPrefWidth(200);

        ComboBox<String> contractCombo = new ComboBox<>(
                FXCollections.observableArrayList("Full Time", "Part Time", "Remote", "Intern", "Full-time", "Part-time"));
        contractCombo.setValue(emp.getContractType());
        contractCombo.setPrefWidth(200);
        contractCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField salaryField = createFormField("Salary (€)");
        if (emp.getSalary() != null) salaryField.setText(String.valueOf(emp.getSalary()));

        grid.add(createLabel("Name"),        0, 0); grid.add(nameField,    1, 0);
        grid.add(createLabel("Gender"),      2, 0); grid.add(genderCombo,  3, 0);
        grid.add(createLabel("Department"),  0, 1); grid.add(deptCombo,    1, 1);
        grid.add(createLabel("Job Title"),   2, 1); grid.add(jobTitleField,3, 1);
        grid.add(createLabel("Start Date"),  0, 3); grid.add(startDate,    1, 3);
        grid.add(createLabel("Contract"),    2, 3); grid.add(contractCombo,3, 3);
        grid.add(createLabel("Salary"),      0, 4); grid.add(salaryField,  1, 4);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        updateBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) { showAlert("Validation Error", "Name is required."); return; }
            Double salary = null;
            if (!salaryField.getText().isEmpty()) {
                try { salary = Double.parseDouble(salaryField.getText()); }
                catch (NumberFormatException ex) { showAlert("Validation Error", "Salary must be a valid number."); return; }
            }
            emp.setFullName(nameField.getText());
            emp.setGender(genderCombo.getValue());
            emp.setPosition(jobTitleField.getText());
            emp.setStartDate(startDate.getValue());
            emp.setContractType(contractCombo.getValue());
            emp.setSalary(salary);
            emp.setDepartment(deptCombo.getValue());
            employeeDao.update(emp);
            // Refresh the employee section
            if (employeeSectionTitle.getText().contains("in ")) {
                String sectionTitle = employeeSectionTitle.getText();
                int deptId = emp.getDepartment() != null ? emp.getDepartment().getId() : -1;
                String dn  = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "";
                if (deptId != -1) showEmployeesForDepartment(dn, deptId);
            }
            popup.close();
        });
        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.getChildren().addAll(updateBtn, cancelBtn);

        form.getChildren().addAll(formTitle, grid, buttonBar);
        Scene scene = new Scene(form, 720, 490);
        var css = getClass().getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void handleDeleteEmployee(Employee emp) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Employee");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete employee: " + emp.getFullName() + "?");
        java.util.Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            employeeDao.delete(emp.getId());
            // Refresh
            if (emp.getDepartment() != null) {
                showEmployeesForDepartment(emp.getDepartment().getDepartmentName(), emp.getDepartment().getId());
            }
            loadFromDatabase();
        }
    }

    private void updateStats() {
        entity.LanguageManager lang = LanguageManager.getInstance();
        totalDeptCount.setText(String.valueOf(departmentTranslationDao.formatNumber(deptData.size(), lang.getLanguage())));
        activeDeptCount.setText(String.valueOf(departmentTranslationDao.formatNumber(deptData.size(), lang.getLanguage())));
        int totalEmp = 0;
        int totalCap = 0;
        for (ObservableList<String> row : deptData) {
            try { totalEmp += Integer.parseInt(row.get(3)); } catch (NumberFormatException ignored) {}
            try { totalCap += Integer.parseInt(row.get(2)); } catch (NumberFormatException ignored) {}
        }
        totalEmpInDepts.setText(String.valueOf(departmentTranslationDao.formatNumber(totalEmp, lang.getLanguage())));
        int pct = totalCap > 0 ? (totalEmp * 100 / totalCap) : 0;
        avgCapacity.setText(departmentTranslationDao.formatNumber(pct, lang.getLanguage()) + "%");
    }

    @FXML
    private void handleAddDepartment() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        showDeptFormPopup(lang.getTranslatedText("dept.addBtn"), null);
    }

    // Opens a form popup for adding a new department (existingData = null) or editing one
    private void showDeptFormPopup(String title, ObservableList<String> existingData) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle(title);

        Scene scene;
        var css = getClass().getResource("/css/management.css");

        if (existingData != null) {
            // Edit mode: TabPane with Department Info + Employees tabs
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            Tab infoTab = new Tab("Department Info");
            infoTab.setContent(buildDeptForm(popup, existingData));

            Tab empTab = new Tab("Employees");
            int deptId = Integer.parseInt(existingData.get(0));
            empTab.setContent(buildEditPopupEmployeesTab(deptId, existingData.get(1)));

            tabPane.getTabs().addAll(infoTab, empTab);
            scene = new Scene(tabPane, 900, 520);
        } else {
            // Add mode: plain form
            scene = new Scene(buildDeptForm(popup, null), 700, 380);
        }

        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private VBox buildDeptForm(Stage popup, ObservableList<String> existingData) {
        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        String formTitleText = existingData == null
                ? lang.getTranslatedText("dept.addBtn")
                : lang.getTranslatedText("dept.col.name") + " - " + existingData.get(1);
        Text formTitle = new Text(formTitleText);
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);


        ComboBox<String> nameField = new ComboBox<>(FXCollections.observableArrayList(
                lang.getTranslatedText("dept.operations"),
                lang.getTranslatedText("dept.production"),
                lang.getTranslatedText("dept.research"),
                lang.getTranslatedText("dept.sales"),
                lang.getTranslatedText("dept.marketing"),
                lang.getTranslatedText("dept.customerService"),
                lang.getTranslatedText("dept.hr"),
                lang.getTranslatedText("dept.finance"),
                lang.getTranslatedText("dept.legal"),
                lang.getTranslatedText("dept.it"),
                lang.getTranslatedText("dept.data"),
                lang.getTranslatedText("dept.executive"),
                lang.getTranslatedText("dept.strategy"),
                lang.getTranslatedText("dept.logistics"),
                lang.getTranslatedText("dept.quality"),
                lang.getTranslatedText("dept.assurance"),
                lang.getTranslatedText("dept.pr")
        ));

        nameField.setPrefWidth(200);
        nameField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField capacityField = createFormField(lang.getTranslatedText("dept.col.capacity"));
        DatePicker creationDate = new DatePicker();
        creationDate.setPromptText(lang.getTranslatedText("dept.col.created"));
        creationDate.setPrefWidth(250);



        grid.add(createLabel(lang.getTranslatedText("dept.col.name")), 0, 0); grid.add(nameField, 1, 0);
        grid.add(createLabel(lang.getTranslatedText("dept.col.capacity")), 2, 0); grid.add(capacityField, 3, 0);
        grid.add(createLabel(lang.getTranslatedText("dept.col.created")), 0, 1);  grid.add(creationDate, 1, 1);



        if (existingData != null) {
            nameField.setValue(existingData.get(1));
            capacityField.setText(existingData.get(2));
            if (!"N/A".equals(existingData.get(4))) {
                try {
                    creationDate.setValue(java.time.LocalDate.parse(existingData.get(4)));
                } catch (Exception ignored) {}
            }
        }

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button(existingData == null ? lang.getTranslatedText("btn.submit") : lang.getTranslatedText("btn.update"));
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button(lang.getTranslatedText("btn.cancel"));
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (nameField.getValue().isEmpty()) {
                showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.deptNameRequired"));
                return;
            }
            int capacity = 0;
            if (!capacityField.getText().isEmpty()) {
                try {
                    capacity = Integer.parseInt(capacityField.getText());
                } catch (NumberFormatException ex) {
                    showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.capacityMustBeNumber"));
                    return;
                }
            }

            if (existingData == null) {
                // Use the first company in the DB as the owner
                List<Company> companies = companyDao.findAll();
                Company company = companies.isEmpty() ? null : companies.get(0);
                Department newDept = new Department(
                        capacity,
                        nameField.getValue(),
                        "",
                        creationDate.getValue(),
                        company
                );

                departmentDao.create(newDept);

                departmentTranslationDao.submitCreateDepartmentTranslation(nameField.getValue(),newDept);


                LocalDate date = LocalDate.now();
                app.UserSession s = app.UserSession.getInstance();

                String deptName = newDept.getDepartmentName();
                String deptKey = lang.getKey(deptName);

                String en = "Department " + lang.getTranslatedTexts(deptKey, "en") + " was added.";
                String fa = "بخش " + lang.getTranslatedTexts(deptKey, "fa") + " اضافه شد.";
                String ar = "تمت إضافة قسم " + lang.getTranslatedTexts(deptKey, "ar");
                String es = "El departamento " + lang.getTranslatedTexts(deptKey, "es") + " fue agregado.";

                ChangeDept newChangeDept = new ChangeDept(
                        date,
                        en,
                        fa,
                        ar,
                        es,
                        s.getAdmin(),
                        newDept
                );
                changeDeptDao.create(newChangeDept);


            } else {
                int id = Integer.parseInt(existingData.get(0));
                Department dept = departmentDao.find(id);
                if (dept != null) {
                    dept.setDepartmentName(nameField.getValue());
                    dept.setCapacity(capacity);
                    dept.setCreationDate(creationDate.getValue());

                    departmentDao.update(dept);
                    departmentTranslationDao.deleteByDepartment(dept);
                    departmentTranslationDao.submitCreateDepartmentTranslation(nameField.getValue(),dept);



                    LocalDate date = LocalDate.now();
                    app.UserSession s = app.UserSession.getInstance();

                    String deptName = dept.getDepartmentName();
                    String deptKey = lang.getKey(deptName);

                    changeDeptDao.create(new ChangeDept(
                            date,
                            "Department " + lang.getTranslatedTexts(deptKey, "en") + " was edited.",
                            "بخش " + lang.getTranslatedTexts(deptKey, "fa") + " ویرایش شد.",
                            "تم تعديل قسم " + lang.getTranslatedTexts(deptKey, "ar"),
                            "El departamento " + lang.getTranslatedTexts(deptKey, "es") + " fue editado.",
                            s.getAdmin(),
                            dept
                    ));
                }
            }

            loadFromDatabase();
            setupFilters();
            popup.close();
        });

        cancelBtn.setOnAction(e -> popup.close());
        buttonBar.getChildren().addAll(submitBtn, cancelBtn);
        form.getChildren().addAll(formTitle, grid, buttonBar);
        return form;
    }

    private VBox buildEditPopupEmployeesTab(int deptId, String deptName) {
        VBox container = new VBox(12);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white;");

        List<Employee> employees = employeeDao.findByDepartment(deptId);

        Text header = new Text("Employees in " + deptName + " (" + employees.size() + ")");
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        TableView<ObservableList<String>> table = new TableView<>();
        table.getStyleClass().add("management-table");
        VBox.setVgrow(table, Priority.ALWAYS);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (Employee emp : employees) {
            data.add(FXCollections.observableArrayList(
                    String.valueOf(emp.getId()),
                    emp.getFullName() != null ? emp.getFullName() : "N/A",
                    emp.getPosition() != null ? emp.getPosition() : "N/A",
                    emp.getStartDate() != null ? emp.getStartDate().toString() : "N/A",
                    emp.getContractType() != null ? emp.getContractType() : "N/A",
                    emp.getGender() != null ? emp.getGender() : "N/A"
            ));
        }

        TableColumn<ObservableList<String>, String> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        cId.setPrefWidth(55);

        TableColumn<ObservableList<String>, String> cName = new TableColumn<>("Name");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        cName.setPrefWidth(200);

        TableColumn<ObservableList<String>, String> cTitle = new TableColumn<>("Job Title");
        cTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        cTitle.setPrefWidth(170);

        TableColumn<ObservableList<String>, String> cStart = new TableColumn<>("Start Date");
        cStart.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        cStart.setPrefWidth(110);

        TableColumn<ObservableList<String>, String> cContract = new TableColumn<>("Contract");
        cContract.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(4)));
        cContract.setPrefWidth(110);

        TableColumn<ObservableList<String>, String> cGender = new TableColumn<>("Gender");
        cGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(5)));
        cGender.setPrefWidth(90);

        table.getColumns().addAll(cId, cName, cTitle, cStart, cContract, cGender);
        table.setItems(data);

        if (employees.isEmpty()) {
            table.setPlaceholder(new Label("No employees in this department."));
        }

        container.getChildren().addAll(header, table);
        return container;
    }

    // Uses safeDelete — blocks deletion if employees are still assigned
    private void handleDeleteDepartment(ObservableList<String> row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Department");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete department: " + row.get(1) + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int id = Integer.parseInt(row.get(0));
            Department dept = departmentDao.find(id);

            departmentTranslationDao.deleteByDepartment(dept);

            LocalDate date = LocalDate.now();
            entity.LanguageManager lang = entity.LanguageManager.getInstance();
            app.UserSession s = app.UserSession.getInstance();


            String deptName = dept.getDepartmentName();
            String deptKey = lang.getKey(deptName);


            changeDeptDao.create(new ChangeDept(
                    date,
                    "Department " + lang.getTranslatedTexts(deptKey, "en") + " was deleted.",
                    "بخش " + lang.getTranslatedTexts(deptKey, "fa") + " حذف شد.",
                    "تم حذف قسم " + lang.getTranslatedTexts(deptKey, "ar"),
                    "El departamento " + lang.getTranslatedTexts(deptKey, "es") + " fue eliminado.",
                    s.getAdmin(),
                    dept
            ));

            boolean deleted = departmentDao.safeDelete(id);
            if (!deleted) {
                showAlert("Cannot Delete", "This department still has employees assigned to it.\nReassign or remove them first.");
                return;
            }
            loadFromDatabase();
            setupFilters();
        }
    }

    @FXML
    private void handleSearch() {
        String search = searchField.getText().trim().toLowerCase();
        Department dept = filterDepartment.getValue();
        String cap = filterCapacity.getValue();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(deptData, row -> {
            if (!search.isEmpty() && !row.get(1).toLowerCase().contains(search)) return false;
            if (dept != null) {
                if (!row.get(0).equals(String.valueOf(dept.getId()))) return false;
            }
            if (cap != null) {
                try {
                    int capacity = Integer.parseInt(row.get(2));
                    if (!matchesCapacityFilter(cap, capacity)) return false;
                } catch (NumberFormatException ignored) {}
            }
            return true;
        });
        departmentTable.setItems(filtered);
    }

    @FXML private void handlePreviousPage() {}
    @FXML private void handleNextPage() {}

    // ===== NAVIGATION =====
    @FXML private void handleDashboard() { navigateTo("/fxml/dashboard.fxml", "/css/dashboard.css"); }
    @FXML private void handleEmployees() { navigateTo("/fxml/employee.fxml", "/css/management.css"); }
    @FXML private void handleDepartments() { /* already here */ }
    @FXML private void handleLeave() { navigateTo("/fxml/leave.fxml", "/css/management.css"); }
    @FXML private void handleSalary() { navigateTo("/fxml/salary.fxml", "/css/management.css"); }

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

    private TextField createFormField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(250);
        tf.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12;");
        return tf;
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-font-weight: bold;");
        return l;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}