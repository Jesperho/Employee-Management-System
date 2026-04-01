package controller;

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

import java.util.Optional;

// Make sure to import BaseController if it's in a different package!
// import util.BaseController;

// 1. Extend BaseController instead of implementing Initializable
public class EmployeeControllerBackup extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDepartment;
    @FXML private ComboBox<String> filterGender;
    @FXML private ComboBox<String> filterContract;

    @FXML private Text totalCount;
    @FXML private Text fullTimeCount;
    @FXML private Text remoteCount;
    @FXML private Text internCount;

    @FXML private TableView<ObservableList<String>> employeeTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colDepartment;
    @FXML private TableColumn<ObservableList<String>, String> colJobTitle;
    @FXML private TableColumn<ObservableList<String>, String> colStartDate;
    @FXML private TableColumn<ObservableList<String>, String> colContract;
    @FXML private TableColumn<ObservableList<String>, String> colGender;
    @FXML private TableColumn<ObservableList<String>, String> colActions;

    @FXML private Text pageInfo;

    // Navigation
    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    private ObservableList<ObservableList<String>> employeeData = FXCollections.observableArrayList();

    // 2. Replace initialize() with setupController()
    @Override
    protected void setupController() {
        setupFilters();
        setupTable();
        loadSampleData();
    }

    // 3. Add the required updateUIText method
    @Override
    protected void updateUIText(String newLanguage) {
        System.out.println("Employee Backup View updating to: " + newLanguage);

        // Example:
        // if (newLanguage.equals("Spanish")) {
        //     btnDashboard.setText("Tablero");
        //     searchField.setPromptText("Buscar empleado...");
        //     // Update table column headers, stats titles, etc.
        // }
    }

    private void setupFilters() {
        filterDepartment.setItems(FXCollections.observableArrayList(
                "All Departments", "IT", "HR", "Marketing", "Finance", "Design", "R&D", "Operations"));
        filterGender.setItems(FXCollections.observableArrayList(
                "All Genders", "Male", "Female", "Other"));
        filterContract.setItems(FXCollections.observableArrayList(
                "All Contracts", "Full Time", "Part Time", "Remote", "Intern"));

        filterDepartment.setOnAction(e -> applyFilters());
        filterGender.setOnAction(e -> applyFilters());
        filterContract.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String dept = filterDepartment.getValue();
        String gender = filterGender.getValue();
        String contract = filterContract.getValue();
        String search = searchField.getText().trim().toLowerCase();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(employeeData, row -> {
            if (dept != null && !dept.equals("All Departments")) {
                if (!row.get(2).equalsIgnoreCase(dept)) return false;
            }
            if (gender != null && !gender.equals("All Genders")) {
                if (!row.get(6).equalsIgnoreCase(gender)) return false;
            }
            if (contract != null && !contract.equals("All Contracts")) {
                if (!row.get(5).equalsIgnoreCase(contract)) return false;
            }
            if (!search.isEmpty()) {
                if (!row.get(1).toLowerCase().contains(search) && !row.get(2).toLowerCase().contains(search)) return false;
            }
            return true;
        });
        employeeTable.setItems(filtered);
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colDepartment.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colJobTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colStartDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colContract.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        colGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

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

                viewBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showViewPopup(row);
                });
                editBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showEditPopup(row);
                });
                deleteBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    handleDeleteEmployee(row);
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
        employeeData.addAll(
                FXCollections.observableArrayList("911", "Yeabsire Abebe", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Female"),
                FXCollections.observableArrayList("20", "Feven Tesfaye", "IT", "Backend Engineer", "28/04/2022", "Remote", "Female"),
                FXCollections.observableArrayList("5", "Amanuel Beyene", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Male"),
                FXCollections.observableArrayList("588", "Tedla Atalay", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Male"),
                FXCollections.observableArrayList("112", "Redwan Husein", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Male"),
                FXCollections.observableArrayList("17", "Abel Beyene", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Male"),
                FXCollections.observableArrayList("404", "Temesgen Melak", "Design", "UI UX Designer", "28/04/2022", "Full Time", "Male"),
                FXCollections.observableArrayList("23", "Sara Johnson", "IT", "Frontend Engineer", "15/01/2023", "Full Time", "Female"),
                FXCollections.observableArrayList("67", "Mike Peters", "HR", "HR Manager", "10/06/2021", "Full Time", "Male"),
                FXCollections.observableArrayList("89", "Lisa Chen", "Marketing", "Content Strategist", "03/09/2022", "Remote", "Female")
        );
        employeeTable.setItems(employeeData);
        updateStats();
    }

    private void updateStats() {
        totalCount.setText(String.valueOf(employeeData.size()));
        long ft = employeeData.stream().filter(r -> r.get(5).equals("Full Time")).count();
        long rm = employeeData.stream().filter(r -> r.get(5).equals("Remote")).count();
        long in = employeeData.stream().filter(r -> r.get(5).equals("Intern")).count();
        fullTimeCount.setText(String.valueOf(ft));
        remoteCount.setText(String.valueOf(rm));
        internCount.setText(String.valueOf(in));
    }

    @FXML
    private void handleAddEmployee() {
        showEmployeeFormPopup("Add New Employee", null);
    }

    private void showViewPopup(ObservableList<String> row) {
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

    private void showEditPopup(ObservableList<String> row) {
        showEmployeeFormPopup("Edit Employee", row);
    }

    private void showEmployeeFormPopup(String title, ObservableList<String> existingData) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle(title);

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.getStyleClass().add("popup-form");
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

        ComboBox<String> deptCombo = new ComboBox<>(FXCollections.observableArrayList("IT", "HR", "Marketing", "Finance", "Design", "R&D", "Operations"));
        deptCombo.setPromptText("Select Department");
        deptCombo.setPrefWidth(200);
        deptCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

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

        grid.add(createLabel("Name"), 0, 0);        grid.add(nameField, 1, 0);
        grid.add(createLabel("Gender"), 2, 0);       grid.add(genderCombo, 3, 0);
        grid.add(createLabel("Department"), 0, 1);   grid.add(deptCombo, 1, 1);
        grid.add(createLabel("Job Title"), 2, 1);    grid.add(jobTitleField, 3, 1);
        grid.add(createLabel("Description"), 0, 2);
        GridPane.setColumnSpan(descriptionField, 3);
        grid.add(descriptionField, 1, 2);
        grid.add(createLabel("Start Date"), 0, 3);   grid.add(startDatePicker, 1, 3);
        grid.add(createLabel("Contract"), 2, 3);     grid.add(contractCombo, 3, 3);
        grid.add(createLabel("Salary"), 0, 4);       grid.add(salaryField, 1, 4);

        if (existingData != null) {
            nameField.setText(existingData.get(1));
            deptCombo.setValue(existingData.get(2));
            jobTitleField.setText(existingData.get(3));
            contractCombo.setValue(existingData.get(5));
            genderCombo.setValue(existingData.get(6));
        }

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button(existingData == null ? "Submit" : "Update");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                showAlert("Validation Error", "Name is required.");
                return;
            }
            if (existingData == null) {
                String id = String.valueOf((int)(Math.random() * 900) + 100);
                ObservableList<String> newRow = FXCollections.observableArrayList(
                        id, nameField.getText(),
                        deptCombo.getValue() != null ? deptCombo.getValue() : "N/A",
                        jobTitleField.getText(),
                        startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "N/A",
                        contractCombo.getValue() != null ? contractCombo.getValue() : "N/A",
                        genderCombo.getValue() != null ? genderCombo.getValue() : "N/A"
                );
                employeeData.add(newRow);
            } else {
                int idx = employeeData.indexOf(existingData);
                if (idx >= 0) {
                    existingData.set(1, nameField.getText());
                    existingData.set(2, deptCombo.getValue() != null ? deptCombo.getValue() : existingData.get(2));
                    existingData.set(3, jobTitleField.getText());
                    existingData.set(5, contractCombo.getValue() != null ? contractCombo.getValue() : existingData.get(5));
                    existingData.set(6, genderCombo.getValue() != null ? genderCombo.getValue() : existingData.get(6));
                    employeeTable.refresh();
                }
            }
            updateStats();
            popup.close();
        });

        cancelBtn.setOnAction(e -> popup.close());

        buttonBar.getChildren().addAll(submitBtn, cancelBtn);

        form.getChildren().addAll(formTitle, grid, buttonBar);

        Scene scene = new Scene(form, 700, 480);
        var css = getClass().getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void handleDeleteEmployee(ObservableList<String> row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Employee");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete employee: " + row.get(1) + " (ID: " + row.get(0) + ")?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            employeeData.remove(row);
            updateStats();
        }
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML private void handlePreviousPage() { System.out.println("Previous page"); }
    @FXML private void handleNextPage() { System.out.println("Next page"); }

    @FXML private void handleDashboard() { navigateTo("/fxml/dashboard.fxml", "/css/dashboard.css"); }
    @FXML private void handleEmployees() { /* already here */ }
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