package controller;

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
import java.util.Optional;
import java.util.ResourceBundle;

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

    private ObservableList<ObservableList<String>> deptData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> employeeData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        setupTable();
        setupEmployeeTable();
        loadSampleData();
        loadEmployeeData();
    }

    private void setupFilters() {
        filterDepartment.setItems(FXCollections.observableArrayList(
                "All Departments", "IT Department", "HR Department", "Marketing", "Finance", "Design", "R&D", "Operations", "Sales"));
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
            showEmployeesForDepartment(dept);
        } else {
            hideEmployeeSection();
        }

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(deptData, row -> {
            // Department name filter
            if (dept != null && !dept.equals("All Departments")) {
                if (!row.get(1).equalsIgnoreCase(dept)) return false;
            }
            // Capacity filter
            if (cap != null && !cap.equals("All Capacities")) {
                int capacity = Integer.parseInt(row.get(2));
                if (cap.equals("Small (< 30)") && capacity >= 30) return false;
                if (cap.equals("Medium (30-50)") && (capacity < 30 || capacity > 50)) return false;
                if (cap.equals("Large (> 50)") && capacity <= 50) return false;
            }
            return true;
        });
        departmentTable.setItems(filtered);
    }

    private void showEmployeesForDepartment(String deptName) {
        // Map department filter names to the department values used in employee data
        String matchDept = deptName.replace(" Department", "");

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(employeeData,
                row -> row.get(2).equalsIgnoreCase(matchDept) || row.get(2).equalsIgnoreCase(deptName));

        employeeTable.setItems(filtered);
        employeeSectionTitle.setText("👥 Employees in " + deptName + " (" + filtered.size() + " found)");

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

    private void loadSampleData() {
        deptData.addAll(
            FXCollections.observableArrayList("1", "IT Department", "60", "45", "2020-01-15", "Information Technology and Software Development"),
            FXCollections.observableArrayList("2", "HR Department", "20", "12", "2019-06-10", "Human Resources and People Management"),
            FXCollections.observableArrayList("3", "Marketing", "35", "28", "2020-03-22", "Marketing, Branding and Communications"),
            FXCollections.observableArrayList("4", "Finance", "25", "15", "2019-01-05", "Financial Planning and Accounting"),
            FXCollections.observableArrayList("5", "Design", "30", "22", "2021-02-14", "UI/UX Design and Creative"),
            FXCollections.observableArrayList("6", "R&D", "40", "35", "2020-08-01", "Research and Development"),
            FXCollections.observableArrayList("7", "Operations", "80", "67", "2019-01-01", "Operations and Logistics"),
            FXCollections.observableArrayList("8", "Sales", "50", "42", "2020-05-18", "Sales and Business Development")
        );
        departmentTable.setItems(deptData);
        updateStats();
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

    private void loadEmployeeData() {
        employeeData.addAll(
            FXCollections.observableArrayList("911", "Yeabsire Abebe", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Female"),
            FXCollections.observableArrayList("20", "Feven Tesfaye", "IT", "Backend Engineer", "28/04/2022", "Remote", "Female"),
            FXCollections.observableArrayList("5", "Amanuel Beyene", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Male"),
            FXCollections.observableArrayList("588", "Tedla Atalay", "IT", "Frontend Engineer", "28/04/2022", "Full Time", "Male"),
            FXCollections.observableArrayList("112", "Redwan Husein", "IT", "Backend Engineer", "28/04/2022", "Full Time", "Male"),
            FXCollections.observableArrayList("17", "Abel Beyene", "R&D", "Research Engineer", "28/04/2022", "Full Time", "Male"),
            FXCollections.observableArrayList("404", "Temesgen Melak", "IT", "Project Manager", "28/04/2022", "Full Time", "Male"),
            FXCollections.observableArrayList("23", "Sara Johnson", "IT", "Frontend Engineer", "15/01/2023", "Full Time", "Female"),
            FXCollections.observableArrayList("67", "Mike Peters", "HR", "HR Manager", "10/06/2021", "Full Time", "Male"),
            FXCollections.observableArrayList("89", "Lisa Chen", "Marketing", "Content Strategist", "03/09/2022", "Remote", "Female"),
            FXCollections.observableArrayList("101", "James Wilson", "Finance", "Financial Analyst", "12/03/2021", "Full Time", "Male"),
            FXCollections.observableArrayList("102", "Anna Berg", "HR", "Recruiter", "22/08/2022", "Full Time", "Female"),
            FXCollections.observableArrayList("103", "David Kim", "Marketing", "Marketing Manager", "05/05/2020", "Full Time", "Male"),
            FXCollections.observableArrayList("104", "Emily Brown", "Design", "Graphic Designer", "14/11/2021", "Full Time", "Female"),
            FXCollections.observableArrayList("105", "Oscar Lund", "Operations", "Operations Lead", "01/02/2020", "Full Time", "Male"),
            FXCollections.observableArrayList("106", "Maria Garcia", "Sales", "Sales Representative", "18/07/2022", "Remote", "Female"),
            FXCollections.observableArrayList("107", "Thomas Holm", "R&D", "Data Scientist", "25/09/2021", "Full Time", "Male"),
            FXCollections.observableArrayList("108", "Sofia Nils", "Finance", "Accountant", "30/01/2023", "Full Time", "Female")
        );
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
        int totalEmp = deptData.stream().mapToInt(r -> Integer.parseInt(r.get(3))).sum();
        totalEmpInDepts.setText(String.valueOf(totalEmp));
        int totalCap = deptData.stream().mapToInt(r -> Integer.parseInt(r.get(2))).sum();
        int pct = totalCap > 0 ? (totalEmp * 100 / totalCap) : 0;
        avgCapacity.setText(pct + "%");
    }

    @FXML
    private void handleAddDepartment() {
        showDeptFormPopup("Add New Department", null);
    }

    private void showDeptFormPopup(String title, ObservableList<String> existingData) {
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

        TextField nameField = createFormField("Department Name");
        TextField capacityField = createFormField("Capacity");
        DatePicker creationDate = new DatePicker();
        creationDate.setPromptText("Creation Date");
        creationDate.setPrefWidth(250);

        ComboBox<String> budgetCombo = new ComboBox<>(FXCollections.observableArrayList(
                "€10,000", "€25,000", "€50,000", "€100,000", "€200,000"));
        budgetCombo.setPromptText("Budget");
        budgetCombo.setPrefWidth(250);
        budgetCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefRowCount(3);
        descriptionField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8;");

        grid.add(createLabel("Department Name"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(createLabel("Capacity"), 2, 0);        grid.add(capacityField, 3, 0);
        grid.add(createLabel("Creation Date"), 0, 1);   grid.add(creationDate, 1, 1);
        grid.add(createLabel("Budget"), 2, 1);           grid.add(budgetCombo, 3, 1);
        grid.add(createLabel("Description"), 0, 2);
        GridPane.setColumnSpan(descriptionField, 3);
        grid.add(descriptionField, 1, 2);

        if (existingData != null) {
            nameField.setText(existingData.get(1));
            capacityField.setText(existingData.get(2));
            descriptionField.setText(existingData.get(5));
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
            if (existingData == null) {
                String id = String.valueOf(deptData.size() + 1);
                ObservableList<String> newRow = FXCollections.observableArrayList(
                        id, nameField.getText(),
                        capacityField.getText().isEmpty() ? "0" : capacityField.getText(),
                        "0",
                        creationDate.getValue() != null ? creationDate.getValue().toString() : "N/A",
                        descriptionField.getText()
                );
                deptData.add(newRow);
            } else {
                existingData.set(1, nameField.getText());
                existingData.set(2, capacityField.getText().isEmpty() ? existingData.get(2) : capacityField.getText());
                existingData.set(5, descriptionField.getText());
                departmentTable.refresh();
            }
            updateStats();
            popup.close();
        });

        cancelBtn.setOnAction(e -> popup.close());
        buttonBar.getChildren().addAll(submitBtn, cancelBtn);

        form.getChildren().addAll(formTitle, grid, buttonBar);

        Scene scene = new Scene(form, 700, 380);
        var css = getClass().getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void handleDeleteDepartment(ObservableList<String> row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Department");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete department: " + row.get(1) + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deptData.remove(row);
            updateStats();
        }
    }

    @FXML
    private void handleSearch() {
        String search = searchField.getText().trim().toLowerCase();
        String dept = filterDepartment.getValue();
        String cap = filterCapacity.getValue();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(deptData, row -> {
            // Search filter
            if (!search.isEmpty() && !row.get(1).toLowerCase().contains(search)) return false;
            // Department name filter
            if (dept != null && !dept.equals("All Departments")) {
                if (!row.get(1).equalsIgnoreCase(dept)) return false;
            }
            // Capacity filter
            if (cap != null && !cap.equals("All Capacities")) {
                int capacity = Integer.parseInt(row.get(2));
                if (cap.equals("Small (< 30)") && capacity >= 30) return false;
                if (cap.equals("Medium (30-50)") && (capacity < 30 || capacity > 50)) return false;
                if (cap.equals("Large (> 50)") && capacity <= 50) return false;
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
            Scene scene = new Scene(root, 1280, 800);
            var css = getClass().getResource(cssPath);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
            stage.show();
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
