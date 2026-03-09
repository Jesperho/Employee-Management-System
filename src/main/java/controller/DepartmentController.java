package controller;

import dao.CompanyDao;
import dao.DepartmentDao;
import dao.EmployeeDao;
import entity.Company;
import entity.Department;
import entity.Employee;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DepartmentController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDepartment;
    @FXML private ComboBox<String> filterCapacity;

    @FXML private Text totalDeptCount;
    @FXML private Text activeDeptCount;
    @FXML private Text totalEmpInDepts;
    @FXML private Text avgCapacity;

    @FXML private TableView<ObservableList<String>> departmentTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colCapacity;
    @FXML private TableColumn<ObservableList<String>, String> colEmployees;
    @FXML private TableColumn<ObservableList<String>, String> colCreationDate;
    @FXML private TableColumn<ObservableList<String>, String> colDescription;
    @FXML private TableColumn<ObservableList<String>, String> colActions;

    @FXML private Text pageInfo;

    // Employee detail section (shown when filtering by specific department)
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

    private final DepartmentDao departmentDao = new DepartmentDao();
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final CompanyDao companyDao = new CompanyDao();

    private ObservableList<ObservableList<String>> deptData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> employeeData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupEmployeeTable();
        loadFromDatabase();
        setupFilters();
    }

    // Loads all departments from DB and refreshes the table and stats
    private void loadFromDatabase() {
        deptData.clear();
        List<Department> departments = departmentDao.findAll();
        for (Department dept : departments) {
            long empCount = departmentDao.countEmployees(dept.getId());
            deptData.add(FXCollections.observableArrayList(
                    String.valueOf(dept.getId()),
                    dept.getDepartmentName() != null ? dept.getDepartmentName() : "",
                    String.valueOf(dept.getCapacity()),
                    String.valueOf(empCount),
                    dept.getCreationDate() != null ? dept.getCreationDate().toString() : "N/A",
                    dept.getDescription() != null ? dept.getDescription() : ""
            ));
        }
        departmentTable.setItems(deptData);
        updateStats();
    }

    // Populates the filter dropdowns; department names come from the DB
    private void setupFilters() {
        List<String> deptNames = departmentDao.findAll().stream()
                .map(Department::getDepartmentName)
                .collect(Collectors.toList());
        deptNames.add(0, "All Departments");
        filterDepartment.setItems(FXCollections.observableArrayList(deptNames));
        filterDepartment.setOnAction(e -> applyFilters());

        filterCapacity.setItems(FXCollections.observableArrayList(
                "All Capacities", "Small (< 30)", "Medium (30-50)", "Large (> 50)"));
        filterCapacity.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String dept = filterDepartment.getValue();
        String cap = filterCapacity.getValue();

        // If a specific department is selected, show employee details for that department
        if (dept != null && !dept.equals("All Departments")) {
            int deptId = -1;
            for (ObservableList<String> row : deptData) {
                if (row.get(1).equalsIgnoreCase(dept)) {
                    deptId = Integer.parseInt(row.get(0));
                    break;
                }
            }
            if (deptId != -1) {
                showEmployeesForDepartment(dept, deptId);
            }
        } else {
            hideEmployeeSection();
        }

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(deptData, row -> {
            if (dept != null && !dept.equals("All Departments")) {
                if (!row.get(1).equalsIgnoreCase(dept)) return false;
            }
            if (cap != null && !cap.equals("All Capacities")) {
                try {
                    int capacity = Integer.parseInt(row.get(2));
                    if (cap.equals("Small (< 30)") && capacity >= 30) return false;
                    if (cap.equals("Medium (30-50)") && (capacity < 30 || capacity > 50)) return false;
                    if (cap.equals("Large (> 50)") && capacity <= 50) return false;
                } catch (NumberFormatException ignored) {}
            }
            return true;
        });
        departmentTable.setItems(filtered);
    }

    // Loads real employees for the selected department from DB
    private void showEmployeesForDepartment(String deptName, int deptId) {
        List<Employee> employees = employeeDao.findByDepartment(deptId);
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
        employeeSectionTitle.setText("Employees in " + deptName + " (" + employees.size() + " found)");
        employeeSection.setVisible(true);
        employeeSection.setManaged(true);
    }

    private void hideEmployeeSection() {
        employeeSection.setVisible(false);
        employeeSection.setManaged(false);
    }

    @FXML
    private void handleShowAllDepartments() {
        filterDepartment.setValue("All Departments");
        applyFilters();
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colCapacity.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colEmployees.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colCreationDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Del");
            private final HBox box = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");
                box.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showDeptFormPopup("Edit Department", row);
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
        empColId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        empColName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        empColDept.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        empColJobTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        empColStartDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        empColContract.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        empColGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        empColActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final HBox box = new HBox(5, viewBtn, editBtn);

            {
                viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showEmployeeViewPopup(row);
                });
                editBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showEmployeeViewPopup(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void showEmployeeViewPopup(ObservableList<String> row) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Employee Details");
        alert.setHeaderText("Employee: " + row.get(1));
        alert.setContentText(
                "ID: " + row.get(0) + "\n" +
                "Name: " + row.get(1) + "\n" +
                "Department: " + row.get(2) + "\n" +
                "Job Title: " + row.get(3) + "\n" +
                "Start Date: " + row.get(4) + "\n" +
                "Contract: " + row.get(5) + "\n" +
                "Gender: " + row.get(6)
        );
        alert.showAndWait();
    }

    private void updateStats() {
        totalDeptCount.setText(String.valueOf(deptData.size()));
        activeDeptCount.setText(String.valueOf(deptData.size()));
        int totalEmp = 0;
        int totalCap = 0;
        for (ObservableList<String> row : deptData) {
            try { totalEmp += Integer.parseInt(row.get(3)); } catch (NumberFormatException ignored) {}
            try { totalCap += Integer.parseInt(row.get(2)); } catch (NumberFormatException ignored) {}
        }
        totalEmpInDepts.setText(String.valueOf(totalEmp));
        int pct = totalCap > 0 ? (totalEmp * 100 / totalCap) : 0;
        avgCapacity.setText(pct + "%");
    }

    @FXML
    private void handleAddDepartment() {
        showDeptFormPopup("Add New Department", null);
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

        String formTitleText = existingData == null ? "Add New Department" : "Edit Department";
        Text formTitle = new Text(formTitleText);
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField nameField = createFormField("Department Name");
        TextField capacityField = createFormField("Capacity");
        DatePicker creationDate = new DatePicker();
        creationDate.setPromptText("Creation Date");
        creationDate.setPrefWidth(250);

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefRowCount(3);
        descriptionField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8;");

        grid.add(createLabel("Department Name"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(createLabel("Capacity"), 2, 0);        grid.add(capacityField, 3, 0);
        grid.add(createLabel("Creation Date"), 0, 1);   grid.add(creationDate, 1, 1);
        grid.add(createLabel("Description"), 0, 2);
        GridPane.setColumnSpan(descriptionField, 3);
        grid.add(descriptionField, 1, 2);

        if (existingData != null) {
            nameField.setText(existingData.get(1));
            capacityField.setText(existingData.get(2));
            descriptionField.setText(existingData.get(5));
            if (!"N/A".equals(existingData.get(4))) {
                try {
                    creationDate.setValue(java.time.LocalDate.parse(existingData.get(4)));
                } catch (Exception ignored) {}
            }
        }

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button(existingData == null ? "Submit" : "Update");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                showAlert("Validation Error", "Department name is required.");
                return;
            }
            int capacity = 0;
            if (!capacityField.getText().isEmpty()) {
                try {
                    capacity = Integer.parseInt(capacityField.getText());
                } catch (NumberFormatException ex) {
                    showAlert("Validation Error", "Capacity must be a valid number.");
                    return;
                }
            }

            if (existingData == null) {
                // Use the first company in the DB as the owner
                List<Company> companies = companyDao.findAll();
                Company company = companies.isEmpty() ? null : companies.get(0);
                Department newDept = new Department(
                        capacity,
                        nameField.getText(),
                        descriptionField.getText(),
                        creationDate.getValue(),
                        company
                );
                departmentDao.create(newDept);
            } else {
                int id = Integer.parseInt(existingData.get(0));
                Department dept = departmentDao.find(id);
                if (dept != null) {
                    dept.setDepartmentName(nameField.getText());
                    dept.setCapacity(capacity);
                    dept.setDescription(descriptionField.getText());
                    dept.setCreationDate(creationDate.getValue());
                    departmentDao.update(dept);
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
        String dept = filterDepartment.getValue();
        String cap = filterCapacity.getValue();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(deptData, row -> {
            if (!search.isEmpty() && !row.get(1).toLowerCase().contains(search)) return false;
            if (dept != null && !dept.equals("All Departments")) {
                if (!row.get(1).equalsIgnoreCase(dept)) return false;
            }
            if (cap != null && !cap.equals("All Capacities")) {
                try {
                    int capacity = Integer.parseInt(row.get(2));
                    if (cap.equals("Small (< 30)") && capacity >= 30) return false;
                    if (cap.equals("Medium (30-50)") && (capacity < 30 || capacity > 50)) return false;
                    if (cap.equals("Large (> 50)") && capacity <= 50) return false;
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