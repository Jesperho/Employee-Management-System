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

// Make sure to import BaseController if it's in a different package!
// import util.BaseController;

// 1. Extend BaseController instead of implementing Initializable
public class SalaryControllerBackup extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDepartment;

    @FXML private Text totalPayroll;
    @FXML private Text avgSalary;
    @FXML private Text highestSalary;
    @FXML private Text employeesPaid;

    @FXML private TableView<ObservableList<String>> salaryTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colDepartment;
    @FXML private TableColumn<ObservableList<String>, String> colJobTitle;
    @FXML private TableColumn<ObservableList<String>, String> colPosition;
    @FXML private TableColumn<ObservableList<String>, String> colSalary;
    @FXML private TableColumn<ObservableList<String>, String> colGender;
    @FXML private TableColumn<ObservableList<String>, String> colActions;

    @FXML private Text totalPayout;
    @FXML private Text pageInfo;

    // Navigation
    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    private ObservableList<ObservableList<String>> salaryData = FXCollections.observableArrayList();

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
        System.out.println("Salary Backup View updating to: " + newLanguage);

        // Example:
        // if (newLanguage.equals("Spanish")) {
        //     btnDashboard.setText("Tablero");
        //     searchField.setPromptText("Buscar...");
        //     // Update table column headers, stats titles, etc.
        // }
    }

    private void setupFilters() {
        filterDepartment.setItems(FXCollections.observableArrayList(
                "All Departments", "IT", "HR", "Marketing", "Finance", "Design", "R&D", "Operations"));
        filterDepartment.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String dept = filterDepartment.getValue();
        String search = searchField.getText().trim().toLowerCase();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(salaryData, row -> {
            if (dept != null && !dept.equals("All Departments") && !row.get(2).equalsIgnoreCase(dept)) return false;
            if (!search.isEmpty()) {
                if (!row.get(1).toLowerCase().contains(search) && !row.get(2).toLowerCase().contains(search)) return false;
            }
            return true;
        });
        salaryTable.setItems(filtered);
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colDepartment.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colJobTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colPosition.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colGender.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        // Salary column with green color
        colSalary.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        colSalary.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("€" + item);
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final HBox box = new HBox(5, viewBtn, editBtn);

            {
                viewBtn.getStyleClass().addAll("table-action-btn", "btn-view");
                editBtn.getStyleClass().addAll("table-action-btn", "btn-edit");
                box.setAlignment(Pos.CENTER);

                viewBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showViewPopup(row);
                });
                editBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    showEditSalaryPopup(row);
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
        salaryData.addAll(
                FXCollections.observableArrayList("911", "Yeabsire Abebe", "IT", "Frontend Engineer", "Manager", "7200", "Female"),
                FXCollections.observableArrayList("20", "Feven Tesfaye", "IT", "Backend Engineer", "Employee", "3200", "Female"),
                FXCollections.observableArrayList("5", "Amanuel Beyene", "IT", "Frontend Engineer", "Employee", "2900", "Male"),
                FXCollections.observableArrayList("588", "Tedla Atalay", "IT", "Backend Engineer", "Employee", "3400", "Male"),
                FXCollections.observableArrayList("112", "Redwan Husein", "IT", "Backend Engineer", "Intern", "2200", "Male"),
                FXCollections.observableArrayList("17", "Abel Beyene", "R&D", "Backend Engineer", "Specialist", "4200", "Male"),
                FXCollections.observableArrayList("404", "Temesgen Melak", "IT", "Project Manager", "Analyst", "5200", "Male"),
                FXCollections.observableArrayList("23", "Sara Johnson", "Design", "UI UX Designer", "Employee", "3800", "Female"),
                FXCollections.observableArrayList("67", "Mike Peters", "HR", "HR Manager", "Manager", "6500", "Male"),
                FXCollections.observableArrayList("89", "Lisa Chen", "Marketing", "Content Strategist", "Employee", "3600", "Female")
        );
        salaryTable.setItems(salaryData);
        updateStats();
    }

    private void updateStats() {
        int total = salaryData.stream().mapToInt(r -> Integer.parseInt(r.get(5))).sum();
        totalPayroll.setText("€" + String.format("%,d", total));
        int avg = salaryData.isEmpty() ? 0 : total / salaryData.size();
        avgSalary.setText("€" + String.format("%,d", avg));
        int max = salaryData.stream().mapToInt(r -> Integer.parseInt(r.get(5))).max().orElse(0);
        highestSalary.setText("€" + String.format("%,d", max));
        employeesPaid.setText(String.valueOf(salaryData.size()));
        totalPayout.setText("Total Payout: €" + String.format("%,d", total));
    }

    private void showViewPopup(ObservableList<String> row) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salary Details");
        alert.setHeaderText("Employee: " + row.get(1));
        alert.setContentText(
                "ID: " + row.get(0) + "\n" +
                        "Name: " + row.get(1) + "\n" +
                        "Department: " + row.get(2) + "\n" +
                        "Job Title: " + row.get(3) + "\n" +
                        "Position: " + row.get(4) + "\n" +
                        "Salary: €" + row.get(5) + "\n" +
                        "Gender: " + row.get(6)
        );
        alert.showAndWait();
    }

    private void showEditSalaryPopup(ObservableList<String> row) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle("Edit Salary - " + row.get(1));

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Text formTitle = new Text("Edit Salary");
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        Text empName = new Text("Employee: " + row.get(1) + " (" + row.get(2) + ")");
        empName.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField currentSalaryField = createFormField("Current Salary");
        currentSalaryField.setText("€" + row.get(5));
        currentSalaryField.setEditable(false);
        currentSalaryField.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12;");

        TextField newSalaryField = createFormField("New Salary (€)");

        ComboBox<String> positionCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Manager", "Employee", "Specialist", "Analyst", "Intern", "Lead"));
        positionCombo.setValue(row.get(4));
        positionCombo.setPrefWidth(250);
        positionCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextArea reasonField = new TextArea();
        reasonField.setPromptText("Reason for salary change");
        reasonField.setPrefRowCount(2);
        reasonField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8;");

        grid.add(createLabel("Current Salary"), 0, 0);  grid.add(currentSalaryField, 1, 0);
        grid.add(createLabel("New Salary"), 0, 1);       grid.add(newSalaryField, 1, 1);
        grid.add(createLabel("Position"), 0, 2);         grid.add(positionCombo, 1, 2);
        grid.add(createLabel("Reason"), 0, 3);
        GridPane.setColumnSpan(reasonField, 1);
        grid.add(reasonField, 1, 3);

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button updateBtn = new Button("Update Salary");
        updateBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        updateBtn.setOnAction(e -> {
            String newSal = newSalaryField.getText().trim();
            if (newSal.isEmpty()) {
                showAlert("Validation Error", "Please enter a new salary.");
                return;
            }
            try {
                Integer.parseInt(newSal);
            } catch (NumberFormatException ex) {
                showAlert("Validation Error", "Salary must be a number.");
                return;
            }
            row.set(5, newSal);
            if (positionCombo.getValue() != null) {
                row.set(4, positionCombo.getValue());
            }
            salaryTable.refresh();
            updateStats();
            popup.close();
        });

        cancelBtn.setOnAction(e -> popup.close());
        buttonBar.getChildren().addAll(updateBtn, cancelBtn);

        form.getChildren().addAll(formTitle, empName, grid, buttonBar);

        Scene scene = new Scene(form, 500, 420);
        var css = getClass().getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    @FXML
    private void handleRunPayroll() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Run Payroll");
        info.setHeaderText("Payroll Processing");
        int total = salaryData.stream().mapToInt(r -> Integer.parseInt(r.get(5))).sum();
        info.setContentText("Processing payroll for " + salaryData.size() + " employees.\n" +
                "Total amount: €" + String.format("%,d", total) + "\n\n" +
                "Payroll will be processed at the end of the month.");
        info.showAndWait();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML private void handlePreviousPage() {}
    @FXML private void handleNextPage() {}

    // ===== NAVIGATION =====
    @FXML private void handleDashboard() { navigateTo("/fxml/dashboard.fxml", "/css/dashboard.css"); }
    @FXML private void handleEmployees() { navigateTo("/fxml/employee.fxml", "/css/management.css"); }
    @FXML private void handleDepartments() { navigateTo("/fxml/department.fxml", "/css/management.css"); }
    @FXML private void handleLeave() { navigateTo("/fxml/leave.fxml", "/css/management.css"); }
    @FXML private void handleSalary() { /* already here */ }

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