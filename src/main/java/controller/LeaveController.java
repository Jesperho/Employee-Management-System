package controller;

import dao.DepartmentDao;
import dao.EmployeeDao;
import dao.LeaveDao;
import entity.Department;
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
    @FXML private ComboBox<String> filterDepartment;

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
    private final DepartmentDao departmentDao = new DepartmentDao();

    private ObservableList<ObservableList<String>> leaveData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilters();
        setupTable();
        loadFromDatabase();
        // Real-time search: fire applyFilters on every keystroke
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
    }

    // Fetches all leave records from the DB and refreshes the table and stats
    private void loadFromDatabase() {
        leaveData.clear();
        List<Leave> leaves = leaveDao.findAll();
        for (Leave leave : leaves) {
            String empName = leave.getEmployee() != null ? leave.getEmployee().getFullName() : "N/A";
            String deptName = (leave.getEmployee() != null && leave.getEmployee().getDepartment() != null)
                    ? leave.getEmployee().getDepartment().getDepartmentName() : "N/A";
            long duration = 0;
            if (leave.getStartDate() != null && leave.getEndDate() != null) {
                duration = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());
            }
            leaveData.add(FXCollections.observableArrayList(
                    String.valueOf(leave.getId()),  // 0
                    empName,                        // 1
                    String.valueOf(duration),        // 2
                    leave.getStartDate() != null ? leave.getStartDate().toString() : "N/A", // 3
                    leave.getEndDate()   != null ? leave.getEndDate().toString()   : "N/A", // 4
                    leave.getType()   != null ? leave.getType()   : "N/A",  // 5
                    leave.getReason() != null ? leave.getReason() : "",     // 6
                    deptName                        // 7  ← department (used for filtering, not shown in table)
            ));
        }
        leaveTable.setItems(leaveData);
        updateStats();
    }

    private void setupFilters() {
        if (filterType != null) {
            filterType.setItems(FXCollections.observableArrayList(
                    "All Types", "Sick", "Vacation", "Maternity", "Personal", "Exam", "Paternity", "Unpaid"));
            filterType.setOnAction(e -> applyFilters());
        }
        if (filterDepartment != null) {
            List<String> deptNames = departmentDao.findAll().stream()
                    .map(Department::getDepartmentName)
                    .collect(java.util.stream.Collectors.toList());
            deptNames.add(0, "All Departments");
            filterDepartment.setItems(FXCollections.observableArrayList(deptNames));
            filterDepartment.setOnAction(e -> applyFilters());
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
        String type   = filterType != null ? filterType.getValue() : null;
        String dept   = filterDepartment != null ? filterDepartment.getValue() : null;
        String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";

        FilteredList<ObservableList<String>> filtered = new FilteredList<>(leaveData, row -> {
            // Filter by leave type
            if (type != null && !type.equals("All Types") && !row.get(5).equalsIgnoreCase(type))
                return false;
            // Filter by department (stored at index 7)
            if (dept != null && !dept.equals("All Departments") && !row.get(7).equalsIgnoreCase(dept))
                return false;
            // Search: match employee name, leave type, department, or reason
            if (!search.isEmpty()) {
                boolean matches = row.get(1).toLowerCase().contains(search)  // employee name
                        || row.get(5).toLowerCase().contains(search)          // type
                        || row.get(7).toLowerCase().contains(search)          // department
                        || row.get(6).toLowerCase().contains(search);         // reason
                if (!matches) return false;
            }
            return true;
        });
        leaveTable.setItems(filtered);
    }

    private void showViewPopup(ObservableList<String> row) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        // Pick accent color by leave type
        String type = row.get(5);
        String[] colors = getLeaveColors(type);
        String gradFrom = colors[0], gradTo = colors[1], accent = colors[2], lightBg = colors[3];

        // Header
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 22, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, " + gradFrom + ", " + gradTo + "); -fx-background-radius: 15 15 0 0;");

        // Avatar initials
        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
        avatarCircle.setStyle("-fx-fill: rgba(255,255,255,0.2);");
        String n = row.get(1) != null ? row.get(1) : "?";
        String initials = n.trim().contains(" ")
                ? String.valueOf(n.charAt(0)) + n.trim().split(" ")[1].charAt(0)
                : String.valueOf(n.charAt(0));
        javafx.scene.text.Text avatarTxt = new javafx.scene.text.Text(initials.toUpperCase());
        avatarTxt.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarCircle, avatarTxt);

        javafx.scene.text.Text nameText = new javafx.scene.text.Text(row.get(1));
        nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Type badge
        Label typeBadge = new Label(getLeaveEmoji(type) + "  " + type);
        typeBadge.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 16; -fx-background-radius: 20;");

        // Duration badge
        Label durBadge = new Label("🗓  " + row.get(2) + " days");
        durBadge.setStyle("-fx-background-color: rgba(0,0,0,0.15); -fx-text-fill: white; " +
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 5 16; -fx-background-radius: 20;");

        HBox badges = new HBox(10, typeBadge, durBadge);
        badges.setAlignment(Pos.CENTER);
        header.getChildren().addAll(avatar, nameText, badges);

        // Info grid
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(14);
        grid.setPadding(new Insets(24, 30, 10, 30));

        addLeaveInfoCard(grid, 0, "🪪  Leave ID",    row.get(0),  "📋  Type",       type,       lightBg, accent);
        addLeaveInfoCard(grid, 1, "📅  Start Date",  row.get(3),  "📅  End Date",   row.get(4), lightBg, accent);
        addLeaveInfoCard(grid, 2, "⏱  Duration",     row.get(2) + " days", "📝  Reason", row.get(6).isEmpty() ? "—" : row.get(6), lightBg, accent);

        Button closeBtn = new Button("✕  Close");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, " + gradFrom + ", " + gradTo + "); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; " +
                "-fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> popup.close());
        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 30, 24, 30));

        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);");
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

    private String[] getLeaveColors(String type) {
        if (type == null) return new String[]{"#1a1f36", "#2d3561", "#6c63ff", "#f0f0ff"};
        return switch (type.toLowerCase()) {
            case "sick", "sick leave"    -> new String[]{"#7b1111", "#c0392b", "#e74c3c", "#fdf0f0"};
            case "vacation"              -> new String[]{"#0a3d62", "#1565c0", "#2196f3", "#e3f2fd"};
            case "maternity", "paternity"-> new String[]{"#4a0a77", "#7b1fa2", "#9c27b0", "#f3e5f5"};
            case "personal"              -> new String[]{"#1a4a1a", "#2e7d32", "#388e3c", "#e8f5e9"};
            default                      -> new String[]{"#1a1f36", "#2d3561", "#6c63ff", "#f0f0ff"};
        };
    }

    private String getLeaveEmoji(String type) {
        if (type == null) return "📋";
        return switch (type.toLowerCase()) {
            case "sick", "sick leave" -> "🤒";
            case "vacation"           -> "🏖";
            case "maternity"          -> "👶";
            case "personal"           -> "🏠";
            default                   -> "📋";
        };
    }

    private void addLeaveInfoCard(GridPane grid, int row,
                                   String l1, String v1, String l2, String v2,
                                   String bg, String accent) {
        Label lbl1 = new Label(l1);
        lbl1.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val1 = new Label(v1);
        val1.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c1 = new VBox(3, lbl1, val1);
        c1.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8; -fx-padding: 10 14;");
        c1.setMinWidth(160);
        grid.add(c1, 0, row);

        Label lbl2 = new Label(l2);
        lbl2.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val2 = new Label(v2);
        val2.setWrapText(true);
        val2.setMaxWidth(160);
        val2.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c2 = new VBox(3, lbl2, val2);
        c2.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8; -fx-padding: 10 14;");
        c2.setMinWidth(160);
        grid.add(c2, 1, row);
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