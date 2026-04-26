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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    @FXML private TableColumn<ObservableList<String>, String> colDescription;
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

    private static final Logger LOGGER = Logger.getLogger(DepartmentController.class.getName());

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
        if (mainMenuLabel != null) mainMenuLabel.setText(lang.getTranslatedText("main_menu"));

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

        if (employeeSectionTitle != null) employeeSectionTitle.setText(lang.getTranslatedText("dept.empSection.title").replace("{0}", ""));

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
        javafx.concurrent.Task<List<ObservableList<String>>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<ObservableList<String>> call() {
                List<ObservableList<String>> newData = new java.util.ArrayList<>();
                List<Department> departments = departmentDao.findAllWithTranslations();

                for (Department dept : departments) {
                    long empCount = departmentDao.countEmployees(dept.getId());

                    newData.add(FXCollections.observableArrayList(
                            String.valueOf(dept.getId()),
                            dept.getTranslatedName(lang.getCurrentLanguageCode()),
                            String.valueOf(dept.getCapacity()),
                            String.valueOf(empCount),
                            dept.getTranslatedDate(lang.getCurrentLanguageCode()) != null ? dept.getTranslatedDate(lang.getCurrentLanguageCode()) : "N/A",
                            dept.getDescription() != null ? dept.getDescription() : ""
                    ));
                }
                return newData;
            }
        };

        task.setOnSucceeded(e -> {
            deptData.setAll(task.getValue());
            departmentTable.setItems(deptData);
            updateStats();
        });

        new Thread(task).start();
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
        } else if (dept == null && !search.isEmpty()) {
            // Ensure the dept list view is visible when searching with no dept filter
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
    protected void handleEmployeeSearch() {
        String q = empSearchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            employeeTable.setItems(employeeData);
            return;
        }
        FilteredList<ObservableList<String>> filtered =
                new FilteredList<>(employeeData, row ->
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
        LanguageManager lang = LanguageManager.getInstance();
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

        colActions.setCellFactory(view.DepartmentView.buildDeptActionsColumn(
            row -> showDeptFormPopup(LanguageManager.getInstance().getTranslatedText("dept.col.actions"), row),
            this::handleDeleteDepartment
        ));
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

        empColActions.setCellFactory(view.DepartmentView.buildEmpActionsColumn(
            currentDeptEmployees,
            this::showEmpViewPopup,
            this::showEmpEditPopup,
            this::handleDeleteEmployee
        ));
    }

    private void showEmpViewPopup(Employee emp) {
        view.DepartmentView.showEmpViewPopup(emp, this::applyRTLToScene);
    }

    private void showEmpEditPopup(Employee emp) {
        Runnable onSuccess = () -> {
            if (employeeSectionTitle != null && employeeSectionTitle.getText().contains("in ")) {
                int deptId = emp.getDepartment() != null ? emp.getDepartment().getId() : -1;
                String dn  = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "";
                if (deptId != -1) showEmployeesForDepartment(dn, deptId);
            }
        };
        view.DepartmentView.showEmpEditPopup(emp, employeeDao, departmentDao, onSuccess, this::applyRTLToScene);
    }

    private void handleDeleteEmployee(Employee emp) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Employee");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete employee: " + emp.getFullName() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
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
        LanguageManager lang = LanguageManager.getInstance();
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
    protected void handleAddDepartment() {
        LanguageManager lang = LanguageManager.getInstance();
        showDeptFormPopup(lang.getTranslatedText("dept.addBtn"), null);
    }

    // Opens a form popup for adding a new department (existingData = null) or editing one
    private void showDeptFormPopup(String title, ObservableList<String> existingData) {
        Runnable onSuccess = () -> {
            loadFromDatabase();
            setupFilters();
        };
        view.DepartmentView.showDeptFormPopup(title, existingData, departmentDao, companyDao, departmentTranslationDao, changeDeptDao, employeeDao, onSuccess, this::applyRTLToScene);
    }

    // Uses safeDelete — blocks deletion if employees are still assigned
    private void handleDeleteDepartment(ObservableList<String> row) {
        LanguageManager lang = LanguageManager.getInstance();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(lang.getTranslatedText("alert.deleteConfirm"));
        confirm.setHeaderText(lang.getTranslatedText("alert.areYouSure"));
        confirm.setContentText(lang.getTranslatedText("dept.delete.confirm")
                .replace("{0}", row.get(1)));
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int id = Integer.parseInt(row.get(0));
            Department dept = departmentDao.find(id);

            // Attempt deletion first; only proceed if no employees are assigned
            boolean deleted = departmentDao.safeDelete(id);
            if (!deleted) {
                showAlert(lang.getTranslatedText("alert.cannotDelete"),
                        lang.getTranslatedText("dept.delete.hasEmployees"));
                return;
            }

            // Safe to remove translations and log activity now
            departmentTranslationDao.deleteByDepartment(dept);

            String deptName = dept.getDepartmentName();
            String deptKey = lang.getKey(deptName);
            changeDeptDao.create(new ChangeDept(
                    LocalDate.now(),
                    java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.deleted", "en"), deptKey != null ? lang.getTranslatedTexts(deptKey, "en") : deptName),
                    java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.deleted", "fa"), deptKey != null ? lang.getTranslatedTexts(deptKey, "fa") : deptName),
                    java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.deleted", "ar"), deptKey != null ? lang.getTranslatedTexts(deptKey, "ar") : deptName),
                    java.text.MessageFormat.format(lang.getTranslatedTexts("dept.activity.deleted", "es"), deptKey != null ? lang.getTranslatedTexts(deptKey, "es") : deptName),
                    app.UserSession.getInstance().getAdmin(),
                    dept
            ));

            loadFromDatabase();
            setupFilters();
        }
    }


    @FXML
    protected void handleSearch() {
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

    @FXML protected void handlePreviousPage() {}
    @FXML protected void handleNextPage() {}

    // ===== NAVIGATION =====
    @FXML protected void handleDashboard() { navigateTo("/fxml/dashboard.fxml", "/css/dashboard.css"); }
    @FXML protected void handleEmployees() { navigateTo("/fxml/employee.fxml", "/css/management.css"); }
    @FXML protected void handleDepartments() { /* already here */ }
    @FXML protected void handleLeave() { navigateTo("/fxml/leave.fxml", "/css/management.css"); }
    @FXML protected void handleSalary() { navigateTo("/fxml/salary.fxml", "/css/management.css"); }

    @FXML
    protected void handleLogout() { navigateTo("/fxml/login.fxml", "/css/login.css"); }

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
            LOGGER.log(Level.SEVERE, "Navigation failed: " + fxmlPath, e);
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