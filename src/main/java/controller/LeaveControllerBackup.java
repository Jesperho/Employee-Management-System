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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

public class LeaveControllerBackup implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;

    @FXML private Text totalLeaves;
    @FXML private Text activeLeaves;
    @FXML private Text sickLeaves;
    @FXML private Text avgDuration;

    @FXML private TableView<ObservableList<String>> leaveTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colEmployee;
    @FXML private TableColumn<ObservableList<String>, String> colDuration;
    @FXML private TableColumn<ObservableList<String>, String> colStartDate;
    @FXML private TableColumn<ObservableList<String>, String> colEndDate;
    @FXML private TableColumn<ObservableList<String>, String> colType;
    @FXML private TableColumn<ObservableList<String>, String> colReason;
    @FXML private TableColumn<ObservableList<String>, String> colActions;

    @FXML private Text pageInfo;

    // Navigation
    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    private ObservableList<ObservableList<String>> leaveData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        setupTable();
        loadSampleData();
    }

    private void setupFilters() {
        if (filterType != null) {
            filterType.setItems(FXCollections.observableArrayList(
                    "All Types", "Sick", "Vacation", "Maternity", "Personal", "Exam"));
            filterType.setOnAction(e -> applyFilters());
        }
    }

    private void setupTable() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colEmployee.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colDuration.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colStartDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colEndDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        colReason.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button viewBtn = new Button("View");
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
                    showLeaveFormPopup("Edit Leave", row);
                });
                deleteBtn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    handleDeleteLeave(row);
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
        leaveData.addAll(
            FXCollections.observableArrayList("1", "Abebe Gemechu", "5", "22/04/2022", "28/04/2022", "Sick", "Personal"),
            FXCollections.observableArrayList("2", "Aman Bey", "7", "22/04/2022", "30/04/2022", "Exam", "Examination"),
            FXCollections.observableArrayList("3", "Feven Tesfaye", "120", "22/04/2022", "28/06/2022", "Maternity", "Child Care"),
            FXCollections.observableArrayList("4", "Gelila Moges", "5", "22/04/2022", "28/04/2022", "Sick", "Personal"),
            FXCollections.observableArrayList("5", "Yanet Tesfaye", "5", "22/04/2022", "28/04/2022", "Sick", "Personal"),
            FXCollections.observableArrayList("6", "Beti Woloe", "5", "22/04/2022", "28/04/2022", "Sick", "Personal"),
            FXCollections.observableArrayList("7", "Dawit Int", "5", "22/04/2022", "28/04/2022", "Sick", "Personal"),
            FXCollections.observableArrayList("8", "Sara Johnson", "10", "01/06/2023", "11/06/2023", "Vacation", "Annual holiday")
        );
        leaveTable.setItems(leaveData);
        updateStats();
    }

    private void updateStats() {
        totalLeaves.setText(String.valueOf(leaveData.size()));
        // Count currently active leaves (placeholder: count those with duration > 10)
        long active = leaveData.stream().filter(r -> Integer.parseInt(r.get(2)) > 10).count();
        activeLeaves.setText(String.valueOf(active));
        long sick = leaveData.stream().filter(r -> r.get(5).equalsIgnoreCase("Sick")).count();
        sickLeaves.setText(String.valueOf(sick));
        int totalDays = leaveData.stream().mapToInt(r -> Integer.parseInt(r.get(2))).sum();
        int avg = leaveData.isEmpty() ? 0 : totalDays / leaveData.size();
        avgDuration.setText(String.valueOf(avg));
    }

    // ===== LEAVE FORM POPUP =====
    @FXML
    private void handleApplyLeave() {
        showLeaveFormPopup("Add Leave", null);
    }

    @FXML
    private void handleLeaveHistory() {
        // Scroll to table or refresh
        leaveTable.scrollTo(0);
    }

    private void applyFilters() {
        String type = filterType.getValue();
        String search = searchField.getText().trim().toLowerCase();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(leaveData, row -> {
            // Type filter
            if (type != null && !type.equals("All Types") && !row.get(5).equalsIgnoreCase(type)) return false;
            // Search filter
            if (!search.isEmpty()) {
                if (!row.get(1).toLowerCase().contains(search) && !row.get(5).toLowerCase().contains(search)) return false;
            }
            return true;
        });
        leaveTable.setItems(filtered);
    }

    private void showViewPopup(ObservableList<String> row) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Leave Details");
        alert.setHeaderText("Leave for: " + row.get(1));
        alert.setContentText(
                "ID: " + row.get(0) + "\n" +
                "Employee: " + row.get(1) + "\n" +
                "Duration: " + row.get(2) + " days\n" +
                "Start Date: " + row.get(3) + "\n" +
                "End Date: " + row.get(4) + "\n" +
                "Type: " + row.get(5) + "\n" +
                "Reason: " + row.get(6)
        );
        alert.showAndWait();
    }

    private void showLeaveFormPopup(String title, ObservableList<String> existingData) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.DECORATED);
        popup.setTitle(title);

        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Text formTitle = new Text(title);
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #1a1f36;");

        Text subtitle = new Text("Fill the required fields below to add an employee leave record.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-fill: #7f8c8d;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField employeeField = createFormField("Employee Name");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Sick", "Vacation", "Maternity", "Personal", "Exam", "Paternity", "Unpaid"));
        typeCombo.setPromptText("Leave Type");
        typeCombo.setPrefWidth(250);
        typeCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.setPrefWidth(250);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.setPrefWidth(250);

        TextField durationField = createFormField("Duration (days)");
        durationField.setEditable(false);

        // Auto-calculate duration
        startDatePicker.valueProperty().addListener((obs, o, n) -> calcDuration(startDatePicker, endDatePicker, durationField));
        endDatePicker.valueProperty().addListener((obs, o, n) -> calcDuration(startDatePicker, endDatePicker, durationField));

        TextArea reasonField = new TextArea();
        reasonField.setPromptText("Reason for leave");
        reasonField.setPrefRowCount(3);
        reasonField.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8;");

        grid.add(createLabel("Employee"), 0, 0);    grid.add(employeeField, 1, 0);
        grid.add(createLabel("Leave Type"), 2, 0);  grid.add(typeCombo, 3, 0);
        grid.add(createLabel("Start Date"), 0, 1);  grid.add(startDatePicker, 1, 1);
        grid.add(createLabel("End Date"), 2, 1);    grid.add(endDatePicker, 3, 1);
        grid.add(createLabel("Duration"), 0, 2);    grid.add(durationField, 1, 2);
        grid.add(createLabel("Reason"), 0, 3);
        GridPane.setColumnSpan(reasonField, 3);
        grid.add(reasonField, 1, 3);

        if (existingData != null) {
            employeeField.setText(existingData.get(1));
            durationField.setText(existingData.get(2));
            typeCombo.setValue(existingData.get(5));
            reasonField.setText(existingData.get(6));
        }

        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button(existingData == null ? "Submit" : "Update");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to right, #6c63ff, #5a52d4); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");

        Button resetBtn = new Button("Reset");
        resetBtn.setStyle("-fx-background-color: white; -fx-text-fill: #7f8c8d; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px; -fx-padding: 12 25; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 12 30; -fx-cursor: hand; -fx-font-weight: bold;");

        submitBtn.setOnAction(e -> {
            if (employeeField.getText().isEmpty()) {
                showAlert("Validation Error", "Employee name is required.");
                return;
            }
            if (existingData == null) {
                String id = String.valueOf(leaveData.size() + 1);
                ObservableList<String> newRow = FXCollections.observableArrayList(
                        id, employeeField.getText(),
                        durationField.getText().isEmpty() ? "0" : durationField.getText(),
                        startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : "N/A",
                        endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : "N/A",
                        typeCombo.getValue() != null ? typeCombo.getValue() : "N/A",
                        reasonField.getText()
                );
                leaveData.add(newRow);
            } else {
                existingData.set(1, employeeField.getText());
                existingData.set(2, durationField.getText());
                existingData.set(5, typeCombo.getValue() != null ? typeCombo.getValue() : existingData.get(5));
                existingData.set(6, reasonField.getText());
                leaveTable.refresh();
            }
            updateStats();
            popup.close();
        });

        resetBtn.setOnAction(e -> {
            employeeField.clear();
            typeCombo.setValue(null);
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            durationField.clear();
            reasonField.clear();
        });

        cancelBtn.setOnAction(e -> popup.close());
        buttonBar.getChildren().addAll(submitBtn, resetBtn, cancelBtn);

        form.getChildren().addAll(formTitle, subtitle, grid, buttonBar);

        Scene scene = new Scene(form, 720, 450);
        var css = getClass().getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void calcDuration(DatePicker start, DatePicker end, TextField duration) {
        if (start.getValue() != null && end.getValue() != null) {
            long days = ChronoUnit.DAYS.between(start.getValue(), end.getValue());
            duration.setText(String.valueOf(Math.max(days, 0)));
        }
    }

    private void handleDeleteLeave(ObservableList<String> row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Leave Record");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete leave record for: " + row.get(1) + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            leaveData.remove(row);
            updateStats();
        }
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
    @FXML private void handleLeave() { /* already here */ }
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
