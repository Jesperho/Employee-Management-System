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

public class EmployeeController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDepartment;
    @FXML private ComboBox<String> filterGender;
    @FXML private ComboBox<String> filterContract;

    @FXML private Text totalCount;
    @FXML private Text fullTimeCount;
    @FXML private Text remoteCount;
    @FXML private Text internCount;

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

    // Holds the full list of employees loaded from the DB
    private final ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilters();
        loadFromDatabase();
    }

    // Fetches all employees from the DB and refreshes the table and stats
    private void loadFromDatabase() {
        employeeData.setAll(employeeDao.findAll());
        employeeTable.setItems(employeeData);
        updateStats();
    }

    // Populates the filter dropdowns; department names come from the DB
    private void setupFilters() {
        List<String> deptNames = departmentDao.findAll().stream()
                .map(Department::getDepartmentName)
                .collect(Collectors.toList());
        deptNames.add(0, "All Departments");
        filterDepartment.setItems(FXCollections.observableArrayList(deptNames));
        filterGender.setItems(FXCollections.observableArrayList("All Genders", "Male", "Female", "Other"));
        filterContract.setItems(FXCollections.observableArrayList("All Contracts", "Full Time", "Part Time", "Remote", "Intern"));

        filterDepartment.setOnAction(e -> applyFilters());
        filterGender.setOnAction(e -> applyFilters());
        filterContract.setOnAction(e -> applyFilters());
    }

    // Filters the table based on the selected dropdown values and search text
    private void applyFilters() {
        String dept = filterDepartment.getValue();
        String gender = filterGender.getValue();
        String contract = filterContract.getValue();
        String search = searchField.getText().trim().toLowerCase();

        FilteredList<Employee> filtered = new FilteredList<>(employeeData, emp -> {
            if (dept != null && !dept.equals("All Departments")) {
                String empDept = getDepartmentName(emp);
                if (!empDept.equalsIgnoreCase(dept)) return false;
            }
            if (gender != null && !gender.equals("All Genders")) {
                if (!gender.equalsIgnoreCase(emp.getGender())) return false;
            }
            if (contract != null && !contract.equals("All Contracts")) {
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
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        colDepartment.setCellValueFactory(data -> new SimpleStringProperty(getDepartmentName(data.getValue())));
        colJobTitle.setCellValueFactory(data -> new SimpleStringProperty(getOrDefault(data.getValue().getPosition())));
        colStartDate.setCellValueFactory(data -> {
            if (data.getValue().getStartDate() == null) return new SimpleStringProperty("N/A");
            return new SimpleStringProperty(data.getValue().getStartDate().toString());
        });
        colContract.setCellValueFactory(data -> new SimpleStringProperty(getOrDefault(data.getValue().getContractType())));
        colGender.setCellValueFactory(data -> new SimpleStringProperty(getOrDefault(data.getValue().getGender())));

        // Actions column renders View / Edit / Delete buttons for each row
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Del");
            private final HBox box = new HBox(5, viewBtn, editBtn, deleteBtn);

            {
                viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                deleteBtn.getStyleClass().addAll("table-action-btn", "btn-delete");
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
        long ft = 0, rm = 0, in = 0;
        for (Employee emp : employeeData) {
            if ("Full Time".equalsIgnoreCase(emp.getContractType())) ft++;
            if ("Remote".equalsIgnoreCase(emp.getContractType())) rm++;
            if ("Intern".equalsIgnoreCase(emp.getContractType())) in++;
        }
        fullTimeCount.setText(String.valueOf(ft));
        remoteCount.setText(String.valueOf(rm));
        internCount.setText(String.valueOf(in));
    }

    @FXML
    private void handleAddEmployee() {
        showEmployeeFormPopup("Add New Employee", null);
    }

    private void showViewPopup(Employee emp) {
        String deptName = getDepartmentName(emp);
        String salary = emp.getSalary() != null ? "€" + emp.getSalary() : "N/A";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Employee Details");
        alert.setHeaderText(emp.getFullName());
        alert.setContentText(
                "ID: " + emp.getId() + "\n" +
                "Name: " + emp.getFullName() + "\n" +
                "Department: " + deptName + "\n" +
                "Position: " + getOrDefault(emp.getPosition()) + "\n" +
                "Start Date: " + getOrDefault(String.valueOf(emp.getStartDate())) + "\n" +
                "Contract: " + getOrDefault(emp.getContractType()) + "\n" +
                "Gender: " + getOrDefault(emp.getGender()) + "\n" +
                "Salary: " + salary
        );
        alert.showAndWait();
    }

    private void showEditPopup(Employee emp) {
        showEmployeeFormPopup("Edit Employee", emp);
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

        TextField nameField = createFormField("Full Name");

        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        genderCombo.setPromptText("Select Gender");
        genderCombo.setPrefWidth(200);
        genderCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        List<Department> departments = departmentDao.findAll();
        ComboBox<Department> deptCombo = new ComboBox<>(FXCollections.observableArrayList(departments));
        deptCombo.setPromptText("Select Department");
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

        TextField jobTitleField = createFormField("Job Title");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefRowCount(3);
        descriptionField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8;");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.setPrefWidth(200);

        ComboBox<String> contractCombo = new ComboBox<>(FXCollections.observableArrayList("Full Time", "Part Time", "Remote", "Intern"));
        contractCombo.setPromptText("Contract Type");
        contractCombo.setPrefWidth(200);
        contractCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField salaryField = createFormField("Salary (€)");

        grid.add(createLabel("Name"), 0, 0);       grid.add(nameField, 1, 0);
        grid.add(createLabel("Gender"), 2, 0);     grid.add(genderCombo, 3, 0);
        grid.add(createLabel("Department"), 0, 1); grid.add(deptCombo, 1, 1);
        grid.add(createLabel("Job Title"), 2, 1);  grid.add(jobTitleField, 3, 1);
        grid.add(createLabel("Description"), 0, 2);
        GridPane.setColumnSpan(descriptionField, 3);
        grid.add(descriptionField, 1, 2);
        grid.add(createLabel("Start Date"), 0, 3); grid.add(startDatePicker, 1, 3);
        grid.add(createLabel("Contract"), 2, 3);   grid.add(contractCombo, 3, 3);
        grid.add(createLabel("Salary"), 0, 4);     grid.add(salaryField, 1, 4);

        if (existingEmployee != null) {
            nameField.setText(existingEmployee.getFullName());
            genderCombo.setValue(existingEmployee.getGender());
            jobTitleField.setText(existingEmployee.getPosition());
            descriptionField.setText(existingEmployee.getDescription());
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

        Button submitBtn = new Button(existingEmployee == null ? "Submit" : "Update");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                showAlert("Validation Error", "Name is required.");
                return;
            }

            Double salary = null;
            if (!salaryField.getText().isEmpty()) {
                try {
                    salary = Double.parseDouble(salaryField.getText());
                } catch (NumberFormatException ex) {
                    showAlert("Validation Error", "Salary must be a valid number.");
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
                        descriptionField.getText(),
                        startDatePicker.getValue(),
                        salary,
                        company,
                        deptCombo.getValue(),
                        null,
                        contractCombo.getValue(),
                        jobTitleField.getText()
                );
                employeeDao.create(newEmp);
            } else {
                existingEmployee.setFullName(nameField.getText());
                existingEmployee.setGender(genderCombo.getValue());
                existingEmployee.setPosition(jobTitleField.getText());
                existingEmployee.setDescription(descriptionField.getText());
                existingEmployee.setStartDate(startDatePicker.getValue());
                existingEmployee.setContractType(contractCombo.getValue());
                existingEmployee.setSalary(salary);
                existingEmployee.setDepartment(deptCombo.getValue());
                employeeDao.update(existingEmployee);
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