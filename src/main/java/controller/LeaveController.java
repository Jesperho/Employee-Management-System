package controller;

import dao.EmployeeDao;
import dao.LeaveDao;
import entity.Employee;
import entity.Leave;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LeaveController implements Initializable {

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

    private final LeaveDao leaveDao = new LeaveDao();
    private final EmployeeDao employeeDao = new EmployeeDao();

    private ObservableList<ObservableList<String>> leaveData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        setupTable();
        loadFromDatabase();
    }

    // Fetches all leave records from the DB and refreshes the table and stats
    private void loadFromDatabase() {
        leaveData.clear();
        List<Leave> leaves = leaveDao.findAll();
        for (Leave leave : leaves) {
            String empName = leave.getEmployee() != null ? leave.getEmployee().getFullName() : "N/A";
            long duration = 0;
            if (leave.getStartDate() != null && leave.getEndDate() != null) {
                duration = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());
            }
            leaveData.add(FXCollections.observableArrayList(
                    String.valueOf(leave.getId()),
                    empName,
                    String.valueOf(duration),
                    leave.getStartDate() != null ? leave.getStartDate().toString() : "N/A",
                    leave.getEndDate() != null ? leave.getEndDate().toString() : "N/A",
                    leave.getType() != null ? leave.getType() : "N/A",
                    leave.getReason() != null ? leave.getReason() : ""
            ));
        }
        leaveTable.setItems(leaveData);
        updateStats();
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

    private void updateStats() {
        totalLeaves.setText(String.valueOf(leaveData.size()));
        long active = leaveData.stream().filter(r -> {
            try { return Long.parseLong(r.get(2)) > 10; } catch (NumberFormatException e) { return false; }
        }).count();
        activeLeaves.setText(String.valueOf(active));
        long sick = leaveData.stream().filter(r -> r.get(5).equalsIgnoreCase("Sick")).count();
        sickLeaves.setText(String.valueOf(sick));
        long totalDays = leaveData.stream().mapToLong(r -> {
            try { return Long.parseLong(r.get(2)); } catch (NumberFormatException e) { return 0; }
        }).sum();
        long avg = leaveData.isEmpty() ? 0 : totalDays / leaveData.size();
        avgDuration.setText(String.valueOf(avg));
    }

    // ===== LEAVE FORM POPUP =====
    @FXML
    private void handleApplyLeave() {
        showLeaveFormPopup("Add Leave", null);
    }

    @FXML
    private void handleLeaveHistory() {
        leaveTable.scrollTo(0);
    }

    private void applyFilters() {
        String type = filterType.getValue();
        String search = searchField.getText().trim().toLowerCase();

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(leaveData, row -> {
            if (type != null && !type.equals("All Types") && !row.get(5).equalsIgnoreCase(type)) return false;
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

    // Opens a form popup for adding (existingData = null) or editing a leave record
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

        // Employee selector from DB
        List<Employee> employees = employeeDao.findAll();
        ComboBox<Employee> empCombo = new ComboBox<>(FXCollections.observableArrayList(employees));
        empCombo.setPromptText("Select Employee");
        empCombo.setPrefWidth(250);
        empCombo.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; -fx-border-radius: 8; -fx-background-radius: 8;");
        empCombo.setCellFactory(lv -> new ListCell<Employee>() {
            @Override
            protected void updateItem(Employee e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getFullName() + " (ID: " + e.getId() + ")");
            }
        });
        empCombo.setButtonCell(new ListCell<Employee>() {
            @Override
            protected void updateItem(Employee e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getFullName() + " (ID: " + e.getId() + ")");
            }
        });

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

        grid.add(createLabel("Employee"), 0, 0);    grid.add(empCombo, 1, 0);
        grid.add(createLabel("Leave Type"), 2, 0);  grid.add(typeCombo, 3, 0);
        grid.add(createLabel("Start Date"), 0, 1);  grid.add(startDatePicker, 1, 1);
        grid.add(createLabel("End Date"), 2, 1);    grid.add(endDatePicker, 3, 1);
        grid.add(createLabel("Duration"), 0, 2);    grid.add(durationField, 1, 2);
        grid.add(createLabel("Reason"), 0, 3);
        GridPane.setColumnSpan(reasonField, 3);
        grid.add(reasonField, 1, 3);

        // Pre-fill for edit — find the Leave entity by ID and pre-select its employee
        if (existingData != null) {
            int leaveId = Integer.parseInt(existingData.get(0));
            Leave existing = leaveDao.find(leaveId);
            if (existing != null) {
                if (existing.getEmployee() != null) {
                    for (Employee emp : employees) {
                        if (emp.getId() == existing.getEmployee().getId()) {
                            empCombo.setValue(emp);
                            break;
                        }
                    }
                }
                typeCombo.setValue(existing.getType());
                startDatePicker.setValue(existing.getStartDate());
                endDatePicker.setValue(existing.getEndDate());
                reasonField.setText(existing.getReason() != null ? existing.getReason() : "");
                calcDuration(startDatePicker, endDatePicker, durationField);
            }
            empCombo.setDisable(true); // Don't change employee on edit
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
            if (existingData == null && empCombo.getValue() == null) {
                showAlert("Validation Error", "Please select an employee.");
                return;
            }
            if (typeCombo.getValue() == null) {
                showAlert("Validation Error", "Please select a leave type.");
                return;
            }

            if (existingData == null) {
                // Create new leave record
                Leave newLeave = new Leave(
                        startDatePicker.getValue(),
                        endDatePicker.getValue(),
                        typeCombo.getValue(),
                        reasonField.getText(),
                        empCombo.getValue()
                );
                leaveDao.create(newLeave);
            } else {
                // Update existing leave record by ID
                int id = Integer.parseInt(existingData.get(0));
                Leave leave = leaveDao.find(id);
                if (leave != null) {
                    leave.setType(typeCombo.getValue());
                    leave.setStartDate(startDatePicker.getValue());
                    leave.setEndDate(endDatePicker.getValue());
                    leave.setReason(reasonField.getText());
                    leaveDao.update(leave);
                }
            }

            loadFromDatabase();
            popup.close();
        });

        resetBtn.setOnAction(e -> {
            empCombo.setValue(null);
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

    // Asks for confirmation then deletes the leave from the DB
    private void handleDeleteLeave(ObservableList<String> row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Leave Record");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete leave record for: " + row.get(1) + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int id = Integer.parseInt(row.get(0));
            leaveDao.delete(id);
            loadFromDatabase();
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