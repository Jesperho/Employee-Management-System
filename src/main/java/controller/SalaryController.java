package controller;

import dao.DepartmentDao;
import dao.EmployeeDao;
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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SalaryController implements Initializable {

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

    @FXML private Text totalPayout; // kept for backward compat — node removed from FXML
    @FXML private Text pageInfo;

    // Navigation
    @FXML private Button btnDashboard;
    @FXML private Button btnEmployees;
    @FXML private Button btnDepartments;
    @FXML private Button btnLeave;
    @FXML private Button btnSalary;
    @FXML private Button btnLogout;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final DepartmentDao departmentDao = new DepartmentDao();

    private ObservableList<ObservableList<String>> salaryData = FXCollections.observableArrayList();
    // Tracks the rows currently visible after filtering — used for stats & payroll
    private List<ObservableList<String>> currentFilteredData = new java.util.ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadFromDatabase();
        setupFilters();
        if (searchField != null)
            searchField.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    // Loads all employees from DB and maps their salary data to the table rows
    private void loadFromDatabase() {
        salaryData.clear();
        List<Employee> employees = employeeDao.findAll();
        for (Employee emp : employees) {
            String deptName = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "N/A";
            int salary = emp.getSalary() != null ? emp.getSalary().intValue() : 0;
            salaryData.add(FXCollections.observableArrayList(
                    String.valueOf(emp.getId()),
                    emp.getFullName() != null ? emp.getFullName() : "N/A",
                    deptName,
                    emp.getPosition() != null ? emp.getPosition() : "N/A",
                    emp.getRole() != null ? emp.getRole() : "N/A",
                    String.valueOf(salary),
                    emp.getGender() != null ? emp.getGender() : "N/A"
            ));
        }
        salaryTable.setItems(salaryData);
        currentFilteredData = new java.util.ArrayList<>(salaryData);
        updateStats();
    }

    // Populates the department filter from DB
    private void setupFilters() {
        List<String> deptNames = departmentDao.findAll().stream()
                .map(Department::getDepartmentName)
                .collect(Collectors.toList());
        deptNames.add(0, "All Departments");
        filterDepartment.setItems(FXCollections.observableArrayList(deptNames));
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

        // Keep a snapshot of the visible rows so stats + payroll use only what's shown
        currentFilteredData = new java.util.ArrayList<>(filtered);
        updateFilteredStats(currentFilteredData, dept);
    }

    private void setupTable() {
        salaryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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

    private void updateStats() {
        updateFilteredStats(salaryData, null);
    }

    // Updates the 4 stat cards based on any subset of rows (filtered or all)
    private void updateFilteredStats(List<ObservableList<String>> rows, String deptLabel) {
        int total = rows.stream().mapToInt(r -> {
            try { return Integer.parseInt(r.get(5)); } catch (NumberFormatException e) { return 0; }
        }).sum();
        int avg = rows.isEmpty() ? 0 : total / rows.size();
        int max = rows.stream().mapToInt(r -> {
            try { return Integer.parseInt(r.get(5)); } catch (NumberFormatException e) { return 0; }
        }).max().orElse(0);

        totalPayroll.setText("€" + total);
        avgSalary.setText("€" + avg);
        highestSalary.setText("€" + max);
        employeesPaid.setText(String.valueOf(rows.size()));
    }

    private void showViewPopup(ObservableList<String> row) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        // Header — green salary theme
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 22, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a3a2a, #1e6b45); -fx-background-radius: 15 15 0 0;");

        // Avatar initials
        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle avatarCircle = new javafx.scene.shape.Circle(32);
        avatarCircle.setStyle("-fx-fill: linear-gradient(to bottom right, #27ae60, #1e8449);");
        String n = row.get(1) != null ? row.get(1) : "?";
        String initials = n.trim().contains(" ")
                ? String.valueOf(n.charAt(0)) + n.trim().split(" ")[1].charAt(0)
                : String.valueOf(n.charAt(0));
        javafx.scene.text.Text avatarTxt = new javafx.scene.text.Text(initials.toUpperCase());
        avatarTxt.setStyle("-fx-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().addAll(avatarCircle, avatarTxt);

        javafx.scene.text.Text nameText = new javafx.scene.text.Text(row.get(1));
        nameText.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        javafx.scene.text.Text roleText = new javafx.scene.text.Text(row.get(3) + "  •  " + row.get(2));
        roleText.setStyle("-fx-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");

        // Salary badge
        Label salaryBadge = new Label("€ " + row.get(5));
        salaryBadge.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: #a8f5c0; " +
                "-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 25;");
        header.getChildren().addAll(avatar, nameText, roleText, salaryBadge);

        // Info grid
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(14);
        grid.setPadding(new Insets(24, 30, 10, 30));
        addSalaryInfoCard(grid, 0, "🪪  Employee ID",  row.get(0),  "⚧  Gender",     row.get(6));
        addSalaryInfoCard(grid, 1, "🏢  Department",   row.get(2),  "📋  Job Title",   row.get(3));
        addSalaryInfoCard(grid, 2, "🎯  Position",     row.get(4),  "💰  Monthly Salary", "€ " + row.get(5));

        Button closeBtn = new Button("✕  Close");
        closeBtn.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #1e8449); -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
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

    private void addSalaryInfoCard(GridPane grid, int row,
                                    String l1, String v1, String l2, String v2) {
        Label lbl1 = new Label(l1);
        lbl1.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val1 = new Label(v1);
        val1.setStyle("-fx-text-fill: #1a1f36; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c1 = new VBox(3, lbl1, val1);
        c1.setStyle("-fx-background-color: #f0faf4; -fx-background-radius: 8; -fx-padding: 10 14;");
        c1.setMinWidth(160);
        grid.add(c1, 0, row);

        Label lbl2 = new Label(l2);
        lbl2.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label val2 = new Label(v2);
        val2.setStyle("-fx-text-fill: " + (l2.contains("Salary") ? "#27ae60" : "#1a1f36") +
                "; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox c2 = new VBox(3, lbl2, val2);
        c2.setStyle("-fx-background-color: #f0faf4; -fx-background-radius: 8; -fx-padding: 10 14;");
        c2.setMinWidth(160);
        grid.add(c2, 1, row);
    }

    // Opens edit popup; persists salary and role changes to the Employee entity in DB
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
            double salaryValue;
            try {
                salaryValue = Double.parseDouble(newSal);
            } catch (NumberFormatException ex) {
                showAlert("Validation Error", "Salary must be a number.");
                return;
            }
            // Persist the salary change to the Employee entity in DB
            int empId = Integer.parseInt(row.get(0));
            Employee emp = employeeDao.find(empId);
            if (emp != null) {
                emp.setSalary(salaryValue);
                if (positionCombo.getValue() != null) {
                    emp.setRole(positionCombo.getValue());
                }
                employeeDao.update(emp);
            }
            loadFromDatabase();
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
        // Use the currently filtered/visible rows — respects dept filter
        List<ObservableList<String>> dataToProcess = currentFilteredData.isEmpty() && filterDepartment.getValue() == null
                ? new java.util.ArrayList<>(salaryData)
                : currentFilteredData;

        int total    = dataToProcess.stream().mapToInt(r -> {
            try { return Integer.parseInt(r.get(5)); } catch (NumberFormatException e) { return 0; }
        }).sum();
        int empCount = dataToProcess.size();

        // Label for which dept (or all)
        String deptVal = filterDepartment.getValue();
        boolean isDeptFilter = deptVal != null && !deptVal.equals("All Departments");
        String scopeLabel = isDeptFilter ? deptVal + " Department" : "All Departments";

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        // Header
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(28, 30, 20, 30));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1f36, #2d3561); -fx-background-radius: 15 15 0 0;");

        javafx.scene.text.Text titleTxt = new javafx.scene.text.Text("💵  Run Payroll");
        titleTxt.setStyle("-fx-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        // Scope badge
        Label scopeBadge = new Label((isDeptFilter ? "🏢 " : "🌐 ") + scopeLabel);
        scopeBadge.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: rgba(255,255,255,0.85);" +
                            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 4 14; -fx-background-radius: 20;");

        javafx.scene.text.Text subTxt = new javafx.scene.text.Text(
                "Processing salaries for " + empCount + " employee" + (empCount == 1 ? "" : "s") + "...");
        subTxt.setStyle("-fx-fill: rgba(255,255,255,0.65); -fx-font-size: 12px;");
        header.getChildren().addAll(titleTxt, scopeBadge, subTxt);

        // Animated counter label
        Label counterLabel = new Label("€ 0");
        counterLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        counterLabel.setAlignment(Pos.CENTER);
        counterLabel.setMaxWidth(Double.MAX_VALUE);

        // Progress bar
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(12);
        progressBar.setStyle("-fx-accent: #27ae60; -fx-background-color: #e8f5e9; -fx-background-radius: 6; -fx-effect: null;");

        Label statusLabel = new Label("Initializing payroll run...");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        VBox body = new VBox(16, counterLabel, progressBar, statusLabel);
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(28, 35, 10, 35));

        // Summary cards (shown after animation)
        HBox summaryRow = new HBox(12);
        summaryRow.setAlignment(Pos.CENTER);
        summaryRow.setPadding(new Insets(0, 30, 0, 30));
        summaryRow.setVisible(false);

        summaryRow.getChildren().addAll(
            makeSummaryCard("👥 Employees", String.valueOf(empCount), "#dbeafe", "#1d4ed8"),
            makeSummaryCard("💰 Total Payout", "€ " + String.format("%,d", total), "#d1fae5", "#065f46"),
            makeSummaryCard("📊 Avg Salary", "€ " + String.format("%,d", empCount > 0 ? total / empCount : 0), "#fef3c7", "#92400e"),
            makeSummaryCard("🏢 Scope", isDeptFilter ? deptVal : "All", "#f3e5f5", "#6a1b9a")
        );

        Button doneBtn = new Button("✓  Done");
        doneBtn.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #1e8449); -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 11 35; -fx-background-radius: 8; -fx-cursor: hand;");
        doneBtn.setVisible(false);
        doneBtn.setOnAction(e -> popup.close());
        HBox footer = new HBox(doneBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 30, 24, 30));

        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 22, 0, 0, 6);");
        card.getChildren().addAll(header, body, summaryRow, footer);
        card.setMaxWidth(480);
        card.setMinWidth(420);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        var css = getClass().getResource("/css/management.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        popup.setScene(scene);
        popup.show();

        // ── Animated counting sequence ────────────────────────────
        String[] steps = {
            "Validating employee records...",
            "Calculating base salaries...",
            "Applying deductions & bonuses...",
            "Generating payslips...",
            "Finalising transactions...",
            "✅ Payroll complete!"
        };
        AtomicInteger stepIdx = new AtomicInteger(0);
        AtomicInteger displayed = new AtomicInteger(0);

        Timeline countUp = new Timeline();
        int frames = 60;
        for (int i = 1; i <= frames; i++) {
            final int frame = i;
            countUp.getKeyFrames().add(new KeyFrame(Duration.millis(i * 30L), e -> {
                int current = (int)((frame / (double) frames) * total);
                counterLabel.setText("€ " + String.format("%,d", current));
                progressBar.setProgress(frame / (double) frames);
                int stepFor = Math.min((frame / (frames / steps.length)), steps.length - 1);
                if (stepFor > stepIdx.get()) {
                    stepIdx.set(stepFor);
                    statusLabel.setText(steps[stepFor]);
                }
            }));
        }

        countUp.setOnFinished(e -> {
            counterLabel.setText("€ " + String.format("%,d", total));
            statusLabel.setText("✅  Payroll complete!  " + empCount + " employees processed.");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px; -fx-font-weight: bold;");
            summaryRow.setVisible(true);
            doneBtn.setVisible(true);
        });
        countUp.play();
    }

    private VBox makeSummaryCard(String label, String value, String bg, String fg) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + fg + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + fg + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        VBox box = new VBox(3, lbl, val);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10; -fx-padding: 12 16;");
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
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