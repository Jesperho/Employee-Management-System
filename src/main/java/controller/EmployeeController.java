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
        import java.time.LocalDateTime;
        import java.util.ArrayList;
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
                employeeData.setAll(employeeDao.findAllWithTranslations());
                employeeTable.setItems(employeeData);
                updateStats();
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
                colActions.setCellFactory(col -> new TableCell<>() {
                    private final Button viewBtn = new Button(lang.getTranslatedText("btn.view"));
                    private final Button editBtn = new Button(lang.getTranslatedText("btn.edit"));
                    private final Button deleteBtn = new Button(lang.getTranslatedText("btn.delete"));
                    private final HBox box = new HBox(4, viewBtn, editBtn, deleteBtn);

                    {
                        viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                        editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                        deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");
                        // Compact sizing so all 3 fit
                        for (Button b : new Button[]{viewBtn, editBtn, deleteBtn}) {
                            b.setMinWidth(42);
                            b.setPrefWidth(42);
                            b.setMaxWidth(52);
                        }
                        box.setAlignment(Pos.CENTER);

                        viewBtn.setOnAction(e -> showViewPopup(getTableView().getItems().get(getIndex())));
                        editBtn.setOnAction(e -> showEditPopup(getTableView().getItems().get(getIndex())));
                        deleteBtn.setOnAction(e -> handleDeleteEmployee(getTableView().getItems().get(getIndex())));
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : box);
                    }
                });
            }

            // Updates the stat counters at the top of the page
            private void updateStats() {
                totalCount.setText(String.valueOf(employeeData.size()));
                long ft = 0, pt = 0, intern = 0;
                for (Employee emp : employeeData) {
                    String ct = emp.getContractType();
                    if (ct != null) {
                        String ctLower = ct.toLowerCase().trim();
                        if (ctLower.contains("full")) ft++;
                        else if (ctLower.contains("part")) pt++;
                        else if (ctLower.contains("intern")) intern++;
                    }
                }
                fullTimeCount.setText(String.valueOf(ft));
                remoteCount.setText(String.valueOf(pt));   // remoteCount now shows Part-time
                internCount.setText(String.valueOf(intern));
            }

            @FXML
            private void handleAddEmployee() {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                showEmployeeFormPopup(lang.getTranslatedText("emp.form.addTitle"), null);
            }

            private void showViewPopup(Employee emp) {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                String langCode = lang.getAbreviation(lang.getLanguage());

                Stage popup = new Stage();
                popup.initModality(Modality.APPLICATION_MODAL);
                popup.initStyle(StageStyle.UNDECORATED);

                // Header gradient banner
                VBox header = new VBox(6);
                header.setAlignment(Pos.CENTER);
                header.setPadding(new Insets(28, 30, 22, 30));
                header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1f36, #2d3561); -fx-background-radius: 15 15 0 0;");

                // Avatar initials circle
                StackPane avatar = new StackPane();
                javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
                avatarCircle.setStyle("-fx-fill: linear-gradient(to bottom right, #6c63ff, #4834d4);");
                String name = emp.getFullName() != null ? emp.getFullName() : "?";
                String initials = name.trim().contains(" ")
                        ? String.valueOf(name.charAt(0)) + name.trim().split(" ")[1].charAt(0)
                        : String.valueOf(name.charAt(0));
                javafx.scene.text.Text avatarTxt = new javafx.scene.text.Text(initials.toUpperCase());
                avatarTxt.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
                avatar.getChildren().addAll(avatarCircle, avatarTxt);

                javafx.scene.text.Text nameText = new javafx.scene.text.Text(emp.getFullName() != null ? emp.getFullName() : "N/A");
                nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
                String deptName = getDepartmentName(emp);
                javafx.scene.text.Text roleText = new javafx.scene.text.Text(
                        getOrDefault(emp.getTranslatedPosition(langCode)) + "  •  " + deptName);
                roleText.setStyle("-fx-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");
                header.getChildren().addAll(avatar, nameText, roleText);

                // Info grid
                GridPane grid = new GridPane();
                grid.setHgap(20); grid.setVgap(14);
                grid.setPadding(new Insets(24, 30, 10, 30));
                String salary = emp.getSalary() != null ? "€" + String.format("%,.0f", emp.getSalary()) : "N/A";


                emp.getTranslatedStartDate(langCode);
                addInfoCard(grid, 0, "🪪  " + lang.getTranslatedText("emp.col.id"),         String.valueOf(emp.getId()),       "📅  " + lang.getTranslatedText("emp.col.startDate"), getOrDefault(emp.getTranslatedStartDate(langCode)));
                addInfoCard(grid, 1, "⚧  " + lang.getTranslatedText("emp.col.gender"),      getOrDefault(emp.getTranslatedGender(langCode)),     "📋  " + lang.getTranslatedText("emp.col.contract"),  getOrDefault(emp.getTranslatedContract(langCode)));
                addInfoCard(grid, 2, "💰  " + lang.getTranslatedText("emp.form.salary"),     getOrDefault(emp.getTranslatedSalary(langCode)),                            "🏢  " + lang.getTranslatedText("emp.col.department"), deptName);


                Button closeBtn = new Button("✕  " + lang.getTranslatedText("btn.close"));
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

            private void addInfoCard(GridPane grid, int row, String label1, String val1, String label2, String val2) {
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

            private void showEditPopup(Employee emp) {
                entity.LanguageManager lang = entity.LanguageManager.getInstance();
                showEmployeeFormPopup(lang.getTranslatedText("emp.form.editTitle"), emp);
            }

            // Opens a form popup for adding a new employee (existingEmployee = null) or editing one
            private void showEmployeeFormPopup(String title, Employee existingEmployee) {
                Stage popup = new Stage();
                popup.initModality(Modality.APPLICATION_MODAL);
                popup.initStyle(StageStyle.DECORATED);
                popup.setTitle(title);

                VBox form = new VBox(15);
                form.setPadding(new Insets(25));
                form.setStyle("-fx-background-color: white;");

                Text formTitle = new Text(title);
                formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

                GridPane grid = new GridPane();
                grid.setHgap(15);
                grid.setVgap(12);

                entity.LanguageManager lang = entity.LanguageManager.getInstance();

                TextField nameField = createFormField(lang.getTranslatedText("emp.form.name"));

                ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList(lang.getTranslatedText("gender1"), lang.getTranslatedText("gender2"), lang.getTranslatedText("gender3")));

                genderCombo.setPromptText(lang.getTranslatedText("emp.form.selectGender"));
                genderCombo.setPrefWidth(200);
                genderCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

                List<Department> departments = departmentDao.findAll();
                ComboBox<Department> deptCombo = new ComboBox<>(FXCollections.observableArrayList(departments));
                deptCombo.setPromptText(lang.getTranslatedText("emp.form.selectDept"));
                deptCombo.setPrefWidth(200);
                deptCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");
                deptCombo.setCellFactory(lv -> new ListCell<Department>() {
                    @Override
                    protected void updateItem(Department d, boolean empty) {
                        super.updateItem(d, empty);
                        if (empty || d == null) {
                            setText(null);
                        } else {
                            setText(d.getDepartmentName());
                        }
                    }
                });
                deptCombo.setButtonCell(new ListCell<Department>() {
                    @Override
                    protected void updateItem(Department d, boolean empty) {
                        super.updateItem(d, empty);
                        if (empty || d == null) {
                            setText(null);
                        } else {
                            setText(d.getDepartmentName());
                        }
                    }
                });

                TextField jobTitleField = createFormField(lang.getTranslatedText("emp.form.jobTitle"));

                DatePicker startDatePicker = new DatePicker();
                startDatePicker.setPromptText(lang.getTranslatedText("emp.form.startDate"));
                startDatePicker.setPrefWidth(200);

                ComboBox<String> contractCombo = new ComboBox<>(FXCollections.observableArrayList(lang.getTranslatedText("contract1"), lang.getTranslatedText("contract2"), lang.getTranslatedText("contract3"), lang.getTranslatedText("contract4")));
                contractCombo.setPromptText(lang.getTranslatedText("emp.form.contractType"));
                contractCombo.setPrefWidth(200);
                contractCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

                TextField salaryField = createFormField(lang.getTranslatedText("emp.form.salary"));

                grid.add(createLabel(lang.getTranslatedText("emp.form.name")), 0, 0);       grid.add(nameField, 1, 0);
                grid.add(createLabel(lang.getTranslatedText("emp.form.gender")), 2, 0);     grid.add(genderCombo, 3, 0);
                grid.add(createLabel(lang.getTranslatedText("emp.form.department")), 0, 1); grid.add(deptCombo, 1, 1);
                grid.add(createLabel(lang.getTranslatedText("emp.form.jobTitle")), 2, 1);   grid.add(jobTitleField, 3, 1);
                grid.add(createLabel(lang.getTranslatedText("emp.form.startDate")), 0, 3); grid.add(startDatePicker, 1, 3);
                grid.add(createLabel(lang.getTranslatedText("emp.form.contract")), 2, 3);  grid.add(contractCombo, 3, 3);
                grid.add(createLabel(lang.getTranslatedText("emp.form.salary")), 0, 4);    grid.add(salaryField, 1, 4);

                if (existingEmployee != null) {
                    nameField.setText(existingEmployee.getFullName());
                    genderCombo.setValue(existingEmployee.getGender());
                    jobTitleField.setText(existingEmployee.getPosition());
                    startDatePicker.setValue(existingEmployee.getStartDate());
                    contractCombo.setValue(existingEmployee.getContractType());
                    if (existingEmployee.getSalary() != null) {
                        salaryField.setText(String.valueOf(existingEmployee.getSalary()));
                    }
                    if (existingEmployee.getDepartment() != null) {
                        for (Department d : departments) {
                            if (d.getId() == existingEmployee.getDepartment().getId()) {
                                deptCombo.setValue(d);
                                break;
                            }
                        }
                    }
                }

                Button submitBtn = new Button(existingEmployee == null ? lang.getTranslatedText("emp.form.submit") : lang.getTranslatedText("emp.form.update"));
                submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

                Button cancelBtn = new Button(lang.getTranslatedText("emp.form.cancel"));
                cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

                submitBtn.setOnAction(e -> {
                    if (nameField.getText().isEmpty()) {
                        showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.nameRequired"));
                        return;
                    }

                    Double salary = null;
                    if (!salaryField.getText().isEmpty()) {
                        try {
                            salary = Double.parseDouble(salaryField.getText());
                        } catch (NumberFormatException ex) {
                            showAlert(lang.getTranslatedText("alert.validationError"), lang.getTranslatedText("alert.salaryMustBeNumber"));
                            return;
                        }
                    }

                    if (existingEmployee == null) {
                        // Use the first company in the DB as the owner for now
                        List<Company> companies = companyDao.findAll();
                        Company company = companies.isEmpty() ? null : companies.get(0);

                        Employee newEmp = new Employee(
                                nameField.getText(),
                                genderCombo.getValue(),
                                startDatePicker.getValue(),
                                salary,
                                company,
                                deptCombo.getValue(),
                                null,
                                contractCombo.getValue(),
                                jobTitleField.getText()
                        );

                        employeeDao.create(newEmp);
                        // Create translation table for new Employee
                        employeeTranslationDao.submitCreateEmployeeTranslation(genderCombo.getValue(), contractCombo.getValue(), jobTitleField, newEmp);

                        //Save message to Db
                        LocalDate date = LocalDate.now();
                        app.UserSession s = app.UserSession.getInstance();



                        String deptName = newEmp.getDepartment().getDepartmentName();
                        String deptKey = lang.getKey(deptName);

                        String en = "Employee " + newEmp.getFullName() + " was added to " + lang.getTranslatedTexts(deptKey, "en");
                        String es = "El empleado " + newEmp.getFullName() + " fue agregado a " + lang.getTranslatedTexts(deptKey, "es");
                        String fa = "کارمند " + newEmp.getFullName() + " به " + lang.getTranslatedTexts(deptKey, "fa") + " اضافه شد";
                        String ar = "تمت إضافة الموظف " + newEmp.getFullName() + " إلى " + lang.getTranslatedTexts(deptKey, "ar");

                        ChangeEmp newChangeEmp = new ChangeEmp(
                                date,
                                en,
                                fa,
                                ar,
                                es,
                                s.getAdmin(),
                                newEmp
                        );

                        changeEmpDao.create(newChangeEmp);

                    } else {
                        existingEmployee.setFullName(nameField.getText());
                        existingEmployee.setStartDate(startDatePicker.getValue());
                        existingEmployee.setSalary(salary);
                        existingEmployee.setDepartment(deptCombo.getValue());

                        existingEmployee.setGender(genderCombo.getValue());
                        existingEmployee.setPosition(jobTitleField.getText());
                        existingEmployee.setContractType(contractCombo.getValue());

                        employeeDao.update(existingEmployee);

                        //Delete previous translations
                        employeeTranslationDao.deleteByEmployee(existingEmployee);
                        // Create translation table for existing employee
                        employeeTranslationDao.submitCreateEmployeeTranslation(genderCombo.getValue(), contractCombo.getValue(), jobTitleField, existingEmployee);


                        LocalDate date = LocalDate.now();
                        app.UserSession s = app.UserSession.getInstance();

                        String en = "Employee " + existingEmployee.getFullName() + " was edited!";
                        String es = "El empleado " + existingEmployee.getFullName() + " fue editado!";
                        String fa = "کارمند " + existingEmployee.getFullName() + " ویرایش شد!";
                        String ar = "تم تعديل الموظف " + existingEmployee.getFullName() + "!";

                        ChangeEmp newChangeEmp = new ChangeEmp(
                                date,
                                en,
                                fa,
                                ar,
                                es,
                                s.getAdmin(),
                                existingEmployee
                        );

                        changeEmpDao.create(newChangeEmp);





                    }

                    loadFromDatabase();
                    popup.close();
                });

                cancelBtn.setOnAction(e -> popup.close());

                HBox buttonBar = new HBox(15);
                buttonBar.setAlignment(Pos.CENTER_RIGHT);
                buttonBar.getChildren().addAll(submitBtn, cancelBtn);

                form.getChildren().addAll(formTitle, grid, buttonBar);

                Scene scene = new Scene(form, 700, 480);
                var css = getClass().getResource("/css/management.css");
                if (css != null) scene.getStylesheets().add(css.toExternalForm());
                popup.setScene(scene);
                popup.showAndWait();
            }

            // Asks for confirmation then deletes the employee from the DB
            private void handleDeleteEmployee(Employee emp) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Employee");
                confirm.setHeaderText("Are you sure?");
                confirm.setContentText("Delete employee: " + emp.getFullName() + " (ID: " + emp.getId() + ")?");
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    LocalDate date = LocalDate.now();
                    app.UserSession s = app.UserSession.getInstance();

                    String en = "Employee " + emp.getFullName() + " was edited!";
                    String es = "El empleado " + emp.getFullName() + " fue editado!";
                    String fa = "کارمند " + emp.getFullName() + " ویرایش شد!";
                    String ar = "تم تعديل الموظف " + emp.getFullName() + "!";

                    ChangeEmp newChangeEmp = new ChangeEmp(
                            date,
                            en,
                            fa,
                            ar,
                            es,
                            s.getAdmin(),
                            emp
                    );
                    changeEmpDao.create(newChangeEmp);

                    changeEmpDao.create(newChangeEmp);
                    employeeTranslationDao.deleteByEmployee(emp);
                    employeeDao.delete(emp.getId());
                    loadFromDatabase();
                }
            }

            @FXML
            private void handleSearch() {
                applyFilters();
            }

            @FXML private void handlePreviousPage() {}
            @FXML private void handleNextPage() {}

            @FXML private void handleDashboard() { navigateTo("/fxml/dashboard.fxml", "/css/dashboard.css"); }
            @FXML private void handleEmployees() {}
            @FXML private void handleDepartments() { navigateTo("/fxml/department.fxml", "/css/management.css"); }
            @FXML private void handleLeave() { navigateTo("/fxml/leave.fxml", "/css/management.css"); }
            @FXML private void handleSalary() { navigateTo("/fxml/salary.fxml", "/css/management.css"); }

            @FXML
            private void handleLogout() {
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

            // Returns the value if not null, otherwise "N/A"
            private String getOrDefault(String value) {
                if (value == null) return "N/A";
                return value;
            }

            private TextField createFormField(String prompt) {
                TextField tf = new TextField();
                tf.setPromptText(prompt);
                tf.setPrefWidth(200);
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