        package controller;

        import dao.*;
        import entity.*;
        import javafx.beans.property.SimpleStringProperty;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.collections.transformation.FilteredList;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.scene.text.Text;
        import javafx.stage.Stage;
        import utils.BaseController;

        import java.time.LocalDate;
        import java.util.List;
        import java.util.Optional;
        import java.util.stream.Collectors;

        // Make sure to import BaseController if it's in a different package!
        // import util.BaseController;

        // 1. Extend BaseController instead of implementing Initializable
        public class EmployeeController extends BaseController {

            @FXML private TextField searchField;
            @FXML private ComboBox<Department> filterDepartment;
            @FXML private ComboBox<String> filterGender;
            @FXML private ComboBox<String> filterContract;

            @FXML private Text breadcrumbText;
            @FXML private Text pageTitleText;
            @FXML private Button btnAddEmployee;

            @FXML private Text totalCount;
            @FXML private Text fullTimeCount;
            @FXML private Text remoteCount;
            @FXML private Text internCount;

            @FXML private Text totalLabelText;
            @FXML private Text fullTimeLabelText;
            @FXML private Text partTimeLabelText;
            @FXML private Text internLabelText;

            @FXML private TableView<Employee> employeeTable;
            @FXML private TableColumn<Employee, String> colId;
            @FXML private TableColumn<Employee, String> colName;
            @FXML private TableColumn<Employee, String> colDepartment;
            @FXML private TableColumn<Employee, String> colJobTitle;
            @FXML private TableColumn<Employee, String> colStartDate;
            @FXML private TableColumn<Employee, String> colContract;
            @FXML private TableColumn<Employee, String> colGender;
            @FXML private TableColumn<Employee, String> colActions;

            @FXML private Text pageInfo;

            // Sidebar user profile (populated from UserSession)
            @FXML private Text avatarInitials;
            @FXML private Text userName;
            @FXML private Text userRole;

            @FXML private Button btnDashboard;
            @FXML private Button btnEmployees;
            @FXML private Button btnDepartments;
            @FXML private Button btnLeave;
            @FXML private Button btnSalary;
            @FXML private Button btnLogout;

            // DAOs for database access
            private final EmployeeDao employeeDao = new EmployeeDao();
            private final DepartmentDao departmentDao = new DepartmentDao();
            private final CompanyDao companyDao = new CompanyDao();
            private final ChangeEmpDao changeEmpDao = new ChangeEmpDao();
            private final EmployeeTranslationDao employeeTranslationDao = new EmployeeTranslationDao();

            // Holds the full list of employees loaded from the DB
            private final ObservableList<Employee> employeeData = FXCollections.observableArrayList();

            // 2. Replace initialize() with setupController()
            @Override
            protected void setupController() {
                updateUserProfile();
                setupTable();
                setupFilters();
                loadFromDatabase();
                LanguageManager.getInstance().currentLanguageProperty().addListener((obs, oldLang, newLang) -> {
                    refreshTableTranslations();
                });
                if (searchField != null)
                    searchField.textProperty().addListener((obs, o, n) -> applyFilters());
            }

            // 3. Add the required updateUIText method
            @Override
            protected void updateUIText(String newLanguage) {
                System.out.println("Employee View updating to: " + newLanguage);
                entity.LanguageManager lang = entity.LanguageManager.getInstance();

                if (btnDashboard != null) btnDashboard.setText("📊  " + lang.getTranslatedText("nav.dashboard"));
                if (btnEmployees != null) btnEmployees.setText("👥  " + lang.getTranslatedText("nav.employees"));
                if (btnDepartments != null) btnDepartments.setText("🏢  " + lang.getTranslatedText("nav.departments"));
                if (btnLeave != null) btnLeave.setText("📅  " + lang.getTranslatedText("nav.leave"));
                if (btnSalary != null) btnSalary.setText("💰  " + lang.getTranslatedText("nav.salary"));
                if (btnLogout != null) btnLogout.setText("🚪  " + lang.getTranslatedText("nav.logout"));
                if (mainMenuLabel != null) mainMenuLabel.setText(lang.getTranslatedText("main_menu"));

                if (breadcrumbText != null) breadcrumbText.setText(lang.getTranslatedText("emp.breadcrumb"));
                if (pageTitleText != null) pageTitleText.setText(lang.getTranslatedText("emp.pageTitle"));
                if (btnAddEmployee != null) btnAddEmployee.setText(lang.getTranslatedText("emp.addBtn"));

                if (searchField != null) searchField.setPromptText(lang.getTranslatedText("search.employees"));
                if (filterDepartment != null) filterDepartment.setPromptText(lang.getTranslatedText("emp.filterAllDepts"));
                if (filterGender != null) filterGender.setPromptText(lang.getTranslatedText("emp.filterAllGenders"));
                if (filterContract != null) filterContract.setPromptText(lang.getTranslatedText("emp.filterAllContracts"));

                if (totalLabelText != null) totalLabelText.setText(lang.getTranslatedText("emp.totalCount"));
                if (fullTimeLabelText != null) fullTimeLabelText.setText(lang.getTranslatedText("emp.fullTime"));
                if (partTimeLabelText != null) partTimeLabelText.setText(lang.getTranslatedText("emp.partTime"));
                if (internLabelText != null) internLabelText.setText(lang.getTranslatedText("emp.interns"));

                if (colId != null) colId.setText(lang.getTranslatedText("emp.col.id"));
                if (colName != null) colName.setText(lang.getTranslatedText("emp.col.name"));
                if (colDepartment != null) colDepartment.setText(lang.getTranslatedText("emp.col.department"));
                if (colJobTitle != null) colJobTitle.setText(lang.getTranslatedText("emp.col.jobTitle"));
                if (colStartDate != null) colStartDate.setText(lang.getTranslatedText("emp.col.startDate"));
                if (colContract != null) colContract.setText(lang.getTranslatedText("emp.col.contract"));
                if (colGender != null) colGender.setText(lang.getTranslatedText("emp.col.gender"));
                if (colActions != null) colActions.setText(lang.getTranslatedText("emp.col.actions"));

                if (pageInfo != null) pageInfo.setText(lang.getTranslatedText("btn.pageInfo"));

                setupFilters();
                setupTable();
                updateStats();
            }

            private void refreshTableTranslations() {
                setupTable(); // IMPORTANT: rebind columns with new language
                employeeTable.refresh();
            }

            private void updateUserProfile() {
                app.UserSession s = app.UserSession.getInstance();
                if (avatarInitials != null) avatarInitials.setText(s.getInitials());
                if (userName != null) userName.setText(s.getFullName());
                if (userRole != null) userRole.setText(s.getRole());
            }

            // Fetches all employees from the DB and refreshes the table and stats
            private void loadFromDatabase() {
                javafx.concurrent.Task<List<Employee>> task = new javafx.concurrent.Task<>() {
                    @Override
                    protected List<Employee> call() {
                        return employeeDao.findAllWithTranslations();
                    }
                };
                task.setOnSucceeded(e -> {
                    employeeData.setAll(task.getValue());
                    employeeTable.setItems(employeeData);
                    updateStats();
                });
                new Thread(task).start();
            }

            private void setupDepartmentFilter() {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();

                List<Department> departments = departmentDao.findAllWithTranslations();

                filterDepartment.setItems(FXCollections.observableArrayList(departments));

                filterDepartment.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(Department d, boolean empty) {
                        super.updateItem(d, empty);
                        setText(empty || d == null
                                ? lang.getTranslatedText("emp.filterAllDepts")
                                : d.getTranslatedName(lang.getCurrentLanguageCode()));
                    }
                });

                filterDepartment.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(Department d, boolean empty) {
                        super.updateItem(d, empty);
                        setText(empty || d == null
                                ? lang.getTranslatedText("emp.filterAllDepts")
                                : d.getTranslatedName(lang.getCurrentLanguageCode()));
                    }
                });

                filterDepartment.getItems().add(0, null); // "All departments"
            }

            // Populates the filter dropdowns; department names come from the DB
            private void setupFilters() {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                List<String> deptNames = departmentDao.findAll().stream()
                        .map(Department::getDepartmentName)
                        .collect(Collectors.toList());
                deptNames.add(0, lang.getTranslatedText("emp.filterAllDepts"));
                setupDepartmentFilter();
                filterGender.setItems(FXCollections.observableArrayList(
                        "ALL",
                        "male",
                        "female",
                        "other"
                ));

                filterGender.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            if (item.equals("ALL")) {
                                setText(lang.getTranslatedText("emp.filterAllGenders"));
                            } else {
                                setText(lang.getTranslatedText("gender_" + item));
                            }
                        }
                    }
                });

                filterGender.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            if (item.equals("ALL")) {
                                setText(lang.getTranslatedText("emp.filterAllGenders"));
                            } else {
                                setText(lang.getTranslatedText("gender_" + item));
                            }
                        }
                    }
                });


                filterContract.setItems(FXCollections.observableArrayList(
                        lang.getTranslatedText("emp.filterAllContracts"),
                        lang.getTranslatedText("contract1"),  lang.getTranslatedText("contract2"), lang.getTranslatedText("contract3"), lang.getTranslatedText("contract4")));

                filterDepartment.setOnAction(e -> applyFilters());
                filterGender.setOnAction(e -> applyFilters());
                filterContract.setOnAction(e -> applyFilters());
            }



            // Filters the table based on the selected dropdown values and search text
            private void applyFilters() {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                Department dept = filterDepartment.getValue();

                String gender = filterGender.getValue();
                String contract = filterContract.getValue();
                String search = searchField.getText().trim().toLowerCase();

                FilteredList<Employee> filtered = new FilteredList<>(employeeData, emp -> {
                    if (dept != null) {
                        String empDept = getDepartmentName(emp);
                        if (!empDept.equalsIgnoreCase(dept.getDepartmentName())) return false;
                    }
                    if (gender != null && !gender.equals("ALL")) {
                        if (!gender.equalsIgnoreCase(emp.getGender())) return false;
                    }

                    if (contract != null && !contract.equals("ALL")) {
                        if (!contract.equalsIgnoreCase(emp.getContractType())) return false;
                    }


                    if (contract != null && !contract.equals(lang.getTranslatedText("emp.filterAllContracts"))) {
                        if (!contract.equalsIgnoreCase(emp.getContractType())) return false;
                    }
                    if (!search.isEmpty()) {
                        boolean nameMatch = emp.getFullName() != null && emp.getFullName().toLowerCase().contains(search);
                        boolean deptMatch = getDepartmentName(emp).toLowerCase().contains(search);
                        if (!nameMatch && !deptMatch) return false;
                    }
                    return true;
                });

                employeeTable.setItems(filtered);
            }

            // Binds each table column to the correct Employee field
            private void setupTable() {

                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                String langCode = lang.getAbreviation(lang.getLanguage());


                employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(employeeTranslationDao.formatNumber(data.getValue().getId(), lang.getLanguage()))));
                colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));

                colDepartment.setCellValueFactory(data -> {
                    Employee emp = data.getValue();
                    Department dept = emp.getDepartment();

                    if (dept == null) {
                        return new SimpleStringProperty("N/A");
                    }

                    return new SimpleStringProperty(dept.getTranslatedName(langCode));
                });


                colJobTitle.setCellValueFactory(data ->
                        new SimpleStringProperty(
                                getOrDefault(data.getValue().getTranslatedPosition(langCode))
                        )
                );
                colStartDate.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            getOrDefault(data.getValue().getTranslatedStartDate(langCode))
                    )
                );
                colContract.setCellValueFactory(data ->
                        new SimpleStringProperty(
                                getOrDefault(data.getValue().getTranslatedContract(langCode))
                        )
                );
                colGender.setCellValueFactory(data ->
                        new SimpleStringProperty(
                                getOrDefault(data.getValue().getTranslatedGender(langCode))
                        )
                );

                // Actions column renders View / Edit / Delete buttons for each row
                colActions.setMinWidth(160);
                colActions.setCellFactory(view.EmployeeView.buildActionsColumn(this::showViewPopup, this::showEditPopup, this::handleDeleteEmployee));
            }

            // Updates the stat counters at the top of the page
            private void updateStats() {
                entity.LanguageManager lang = LanguageManager.getInstance();
                totalCount.setText(String.valueOf(employeeTranslationDao.formatNumber(employeeData.size(), lang.getLanguage())));
                int ft = 0, pt = 0, intern = 0;
                for (Employee emp : employeeData) {
                    String ct = emp.getContractType();
                    if (ct != null) {
                        String ctLower = ct.toLowerCase().trim();
                        if (ctLower.contains("full")) ft++;
                        else if (ctLower.contains("part")) pt++;
                        else if (ctLower.contains("intern")) intern++;
                    }
                }
                fullTimeCount.setText(String.valueOf(employeeTranslationDao.formatNumber(ft, lang.getLanguage())));

                remoteCount.setText(String.valueOf(employeeTranslationDao.formatNumber(pt, lang.getLanguage())));   // remoteCount now shows Part-time
                internCount.setText(String.valueOf(employeeTranslationDao.formatNumber(intern, lang.getLanguage())));
            }

            @FXML
            protected void handleAddEmployee() {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                showEmployeeFormPopup(lang.getTranslatedText("emp.form.addTitle"), null);
            }

            private void showViewPopup(Employee emp) {
                view.EmployeeView.showViewPopup(emp, employeeTranslationDao, this::applyRTLToScene);
            }

            private void showEditPopup(Employee emp) {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                showEmployeeFormPopup(lang.getTranslatedText("emp.form.editTitle"), emp);
            }

            // Opens a form popup for adding a new employee (existingEmployee = null) or editing one
            private void showEmployeeFormPopup(String title, Employee existingEmployee) {
                view.EmployeeView.showEmployeeFormPopup(title, existingEmployee, departmentDao, employeeDao, employeeTranslationDao, companyDao, changeEmpDao, this::loadFromDatabase, this::applyRTLToScene);
            }

            // Asks for confirmation then deletes the employee from the DB
            private void handleDeleteEmployee(Employee emp) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                confirm.setTitle(lang.getTranslatedText("alert.deleteConfirm"));
                confirm.setHeaderText(lang.getTranslatedText("alert.areYouSure"));
                confirm.setContentText(lang.getTranslatedText("emp.delete.confirm")
                        .replace("{0}", emp.getFullName()));
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    employeeTranslationDao.deleteByEmployee(emp);
                    employeeDao.delete(emp.getId());

                    String en = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.deleted", "en"), emp.getFullName());
                    String es = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.deleted", "es"), emp.getFullName());
                    String fa = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.deleted", "fa"), emp.getFullName());
                    String ar = java.text.MessageFormat.format(lang.getTranslatedTexts("emp.activity.deleted", "ar"), emp.getFullName());

                    changeEmpDao.create(new ChangeEmp(
                            LocalDate.now(), en, fa, ar, es,
                            app.UserSession.getInstance().getAdmin(), emp
                    ));
                    loadFromDatabase();
                }
            }

            @FXML protected void handleSearch() {
                applyFilters();
            }

            @FXML protected void handlePreviousPage() {}
            @FXML protected void handleNextPage() {}

            @FXML protected void handleDashboard() { navigateTo("/fxml/dashboard.fxml", "/css/dashboard.css"); }
            @FXML protected void handleEmployees() {}
            @FXML protected void handleDepartments() { navigateTo("/fxml/department.fxml", "/css/management.css"); }
            @FXML protected void handleLeave() { navigateTo("/fxml/leave.fxml", "/css/management.css"); }
            @FXML protected void handleSalary() { navigateTo("/fxml/salary.fxml", "/css/management.css"); }

            @FXML
            protected void handleLogout() {
                navigateTo("/fxml/login.fxml", "/css/login.css");
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
                    e.printStackTrace();
                }
            }

            // Returns the department name for an employee, or "N/A" if not assigned
            private String getDepartmentName(Employee emp) {
                if (emp.getDepartment() == null) return "N/A";
                return emp.getDepartment().getDepartmentName();
            }

            private String getOrDefault(String value) {
                if (value == null) return "N/A";
                return value;
            }
        }