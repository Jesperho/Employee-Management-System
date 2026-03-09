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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDate;
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

    // Sidebar user profile (populated from UserSession)
    @FXML private Text avatarInitials;
    @FXML private Text userName;
    @FXML private Text userRole;

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
        updateUserProfile();
        setupFilters();
        setupTable();
        loadFromDatabase();
        // Real-time search: fire applyFilters on every keystroke
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
    }

    private void updateUserProfile() {
        app.UserSession s = app.UserSession.getInstance();
        if (avatarInitials != null) avatarInitials.setText(s.getInitials());
        if (userName != null) userName.setText(s.getFullName());
        if (userRole != null) userRole.setText(s.getRole());
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
        leaveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colEmployee.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colDuration.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colStartDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colEndDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        colReason.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(6)));

        colActions.setMinWidth(160);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button viewBtn = new Button("View");
            private final Button deleteBtn = new Button("Del");
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
        LocalDate today = LocalDate.now();

        totalLeaves.setText(String.valueOf(leaveData.size()));

        // Currently on leave: today is between start and end date
        long active = leaveData.stream().filter(r -> {
            try {
                String startStr = r.get(3);
                String endStr = r.get(4);
                if (startStr.equals("N/A") || endStr.equals("N/A")) return false;
                LocalDate start = LocalDate.parse(startStr);
                LocalDate end = LocalDate.parse(endStr);
                return !today.isBefore(start) && !today.isAfter(end);
            } catch (Exception e) { return false; }
        }).count();
        activeLeaves.setText(String.valueOf(active));

        // Sick leaves: type == "Sick"
        long sick = leaveData.stream().filter(r -> r.get(5).equalsIgnoreCase("Sick")).count();
        sickLeaves.setText(String.valueOf(sick));

        // Avg duration in days
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
        List<Leave> allLeaves = leaveDao.findAll();
        allLeaves.sort((a, b) -> {
            if (a.getStartDate() == null) return 1;
            if (b.getStartDate() == null) return -1;
            return b.getStartDate().compareTo(a.getStartDate());
        });

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        // ── HEADER ───────────────────────────────────────────────────────────
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(22, 25, 20, 25));
        header.setStyle("-fx-background-color: linear-gradient(to bottom right,#1a1f36,#2d3561);" +
                        "-fx-background-radius: 15 15 0 0;");

        StackPane iconWrap = new StackPane();
        javafx.scene.shape.Circle ic = new javafx.scene.shape.Circle(22);
        ic.setStyle("-fx-fill: rgba(108,99,255,0.3);");
        Label iconLbl = new Label("📊");
        iconLbl.setStyle("-fx-font-size: 17px;");
        iconWrap.getChildren().addAll(ic, iconLbl);

        VBox titleBox = new VBox(3);
        Label titleLbl = new Label("Leave History");
        titleLbl.setStyle("-fx-text-fill:white;-fx-font-size:20px;-fx-font-weight:bold;");
        Label subLbl = new Label("Complete leave record for all employees");
        subLbl.setStyle("-fx-text-fill:rgba(255,255,255,0.6);-fx-font-size:11px;");
        titleBox.getChildren().addAll(titleLbl, subLbl);

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // Search bar in header
        TextField searchBox = new TextField();
        searchBox.setPromptText("🔍  Search employee or type...");
        searchBox.setPrefWidth(220);
        searchBox.setStyle("-fx-background-color:rgba(255,255,255,0.12);-fx-text-fill:white;" +
                           "-fx-prompt-text-fill:rgba(255,255,255,0.45);-fx-background-radius:8;" +
                           "-fx-border-color:rgba(255,255,255,0.2);-fx-border-radius:8;-fx-padding:7 12;");

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color:rgba(255,255,255,0.1);-fx-text-fill:white;" +
                          "-fx-background-radius:6;-fx-font-size:14px;-fx-cursor:hand;-fx-padding:4 10;");
        closeBtn.setOnAction(e -> popup.close());
        header.getChildren().addAll(iconWrap, titleBox, hSpacer, searchBox, closeBtn);

        // ── SUMMARY STAT CARDS ───────────────────────────────────────────────
        long totalDaysAll = allLeaves.stream().mapToLong(l -> {
            if (l.getStartDate() != null && l.getEndDate() != null)
                return java.time.temporal.ChronoUnit.DAYS.between(l.getStartDate(), l.getEndDate());
            return 0;
        }).sum();
        long sickCount    = allLeaves.stream().filter(l -> "Sick".equalsIgnoreCase(l.getType())).count();
        long vacCount     = allLeaves.stream().filter(l -> "Vacation".equalsIgnoreCase(l.getType())).count();
        long avgDays      = allLeaves.isEmpty() ? 0 : totalDaysAll / allLeaves.size();

        HBox statsRow = new HBox(12);
        statsRow.setPadding(new Insets(16, 22, 8, 22));
        statsRow.setStyle("-fx-background-color:#f8f9ff;");
        statsRow.getChildren().addAll(
            makeSummaryStatCard("📋  Total Leaves",  String.valueOf(allLeaves.size()), "#dbeafe","#1d4ed8"),
            makeSummaryStatCard("🤒  Sick",          String.valueOf(sickCount),        "#fde8e8","#c0392b"),
            makeSummaryStatCard("🏖  Vacation",      String.valueOf(vacCount),         "#d1fae5","#065f46"),
            makeSummaryStatCard("⏱  Avg Days",       String.valueOf(avgDays),          "#fef9c3","#92400e"),
            makeSummaryStatCard("📆  Total Days",    String.valueOf(totalDaysAll),     "#f3e5f5","#6a1b9a")
        );

        // ── TYPE FILTER CHIPS ────────────────────────────────────────────────
        HBox chips = new HBox(8);
        chips.setPadding(new Insets(8, 22, 10, 22));
        chips.setStyle("-fx-background-color:#f8f9ff;");
        chips.setAlignment(Pos.CENTER_LEFT);

        String[] types = {"All", "Sick", "Vacation", "Maternity", "Personal", "Exam", "Paternity", "Unpaid"};
        ToggleGroup chipGroup = new ToggleGroup();
        for (String t : types) {
            ToggleButton chip = new ToggleButton(t);
            chip.setToggleGroup(chipGroup);
            chip.setUserData(t);
            String[] clr = t.equals("All") ? new String[]{"#6c63ff","white"} : getLeaveChipColor(t);
            chip.setStyle("-fx-background-color:" + (t.equals("All") ? clr[0] : "#e8eaf0") +
                          ";-fx-text-fill:" + (t.equals("All") ? "white" : "#555") +
                          ";-fx-font-size:11px;-fx-font-weight:bold;-fx-padding:5 14;" +
                          "-fx-background-radius:20;-fx-cursor:hand;");
            if (t.equals("All")) chip.setSelected(true);
            chips.getChildren().add(chip);
        }

        // ── LEAVE LIST ───────────────────────────────────────────────────────
        VBox listBox = new VBox(0);
        listBox.setStyle("-fx-background-color:white;");

        // Populate rows helper
        Runnable[] refreshList = {null};
        refreshList[0] = () -> {
            listBox.getChildren().clear();
            String selectedType = chipGroup.getSelectedToggle() != null
                    ? (String) chipGroup.getSelectedToggle().getUserData() : "All";
            String q = searchBox.getText().trim().toLowerCase();

            int[] rowNum = {0};
            for (Leave leave : allLeaves) {
                String empName = leave.getEmployee() != null ? leave.getEmployee().getFullName() : "Unknown";
                String dept    = (leave.getEmployee() != null && leave.getEmployee().getDepartment() != null)
                        ? leave.getEmployee().getDepartment().getDepartmentName() : "N/A";
                String lType   = leave.getType() != null ? leave.getType() : "N/A";
                String reason  = leave.getReason() != null && !leave.getReason().isEmpty()
                        ? leave.getReason() : "—";
                long dur = 0;
                if (leave.getStartDate() != null && leave.getEndDate() != null)
                    dur = java.time.temporal.ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());

                // Apply filters
                if (!selectedType.equals("All") && !lType.equalsIgnoreCase(selectedType)) continue;
                if (!q.isEmpty() && !empName.toLowerCase().contains(q)
                        && !lType.toLowerCase().contains(q)
                        && !dept.toLowerCase().contains(q)
                        && !reason.toLowerCase().contains(q)) continue;

                String[] lc = getLeaveChipColor(lType);
                boolean even = rowNum[0] % 2 == 0;

                // Row container
                HBox row = new HBox(14);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(13, 22, 13, 22));
                row.setStyle("-fx-background-color:" + (even ? "white" : "#fafbff") +
                             ";-fx-border-color:#f0f2f8;-fx-border-width:0 0 1 0;");

                // Left: colored type bubble
                StackPane bubble = new StackPane();
                bubble.setMinSize(44, 44);
                bubble.setMaxSize(44, 44);
                javafx.scene.shape.Circle bCircle = new javafx.scene.shape.Circle(22);
                bCircle.setStyle("-fx-fill:" + lc[2] + ";");
                Label emojiL = new Label(getLeaveEmoji(lType));
                emojiL.setStyle("-fx-font-size:17px;");
                bubble.getChildren().addAll(bCircle, emojiL);

                // Middle: employee info + dates
                VBox mid = new VBox(4);
                HBox.setHgrow(mid, Priority.ALWAYS);

                HBox topLine = new HBox(8);
                topLine.setAlignment(Pos.CENTER_LEFT);
                Label empLbl = new Label(empName);
                empLbl.setStyle("-fx-text-fill:#1a1f36;-fx-font-size:13px;-fx-font-weight:bold;");

                Label typePill = new Label(getLeaveEmoji(lType) + " " + lType);
                typePill.setStyle("-fx-background-color:" + lc[2] + ";-fx-text-fill:" + lc[0] +
                                  ";-fx-font-size:10px;-fx-font-weight:bold;-fx-padding:3 10;" +
                                  "-fx-background-radius:12;");

                Label deptPill = new Label("🏢 " + dept);
                deptPill.setStyle("-fx-background-color:#e8eaf6;-fx-text-fill:#3949ab;" +
                                  "-fx-font-size:10px;-fx-padding:3 10;-fx-background-radius:12;");
                topLine.getChildren().addAll(empLbl, typePill, deptPill);

                HBox dateLine = new HBox(16);
                dateLine.setAlignment(Pos.CENTER_LEFT);
                Label startL = new Label("📅 " + (leave.getStartDate() != null ? leave.getStartDate() : "N/A"));
                startL.setStyle("-fx-text-fill:#5f6c7b;-fx-font-size:11px;");
                Label arrowL = new Label("→");
                arrowL.setStyle("-fx-text-fill:#9ba8b7;-fx-font-size:11px;");
                Label endL   = new Label(leave.getEndDate() != null ? leave.getEndDate().toString() : "N/A");
                endL.setStyle("-fx-text-fill:#5f6c7b;-fx-font-size:11px;");
                Label durL   = new Label("(" + dur + " days)");
                durL.setStyle("-fx-text-fill:" + lc[0] + ";-fx-font-size:11px;-fx-font-weight:bold;");
                dateLine.getChildren().addAll(startL, arrowL, endL, durL);

                Label reasonL = new Label("📝 " + reason);
                reasonL.setStyle("-fx-text-fill:#9ba8b7;-fx-font-size:11px;");
                reasonL.setWrapText(true);
                reasonL.setMaxWidth(320);

                mid.getChildren().addAll(topLine, dateLine, reasonL);

                // Right: duration badge
                VBox right = new VBox(4);
                right.setAlignment(Pos.CENTER_RIGHT);
                right.setMinWidth(70);
                Label durBig = new Label(String.valueOf(dur));
                durBig.setStyle("-fx-text-fill:" + lc[0] + ";-fx-font-size:22px;-fx-font-weight:bold;");
                Label durUnit = new Label("days");
                durUnit.setStyle("-fx-text-fill:#9ba8b7;-fx-font-size:10px;");
                right.getChildren().addAll(durBig, durUnit);

                row.getChildren().addAll(bubble, mid, right);
                listBox.getChildren().add(row);
                rowNum[0]++;
            }

            if (listBox.getChildren().isEmpty()) {
                Label empty = new Label("No leave records found.");
                empty.setStyle("-fx-text-fill:#7f8c8d;-fx-font-size:13px;-fx-padding:30;");
                listBox.getChildren().add(empty);
            }
        };

        // Wire chip + search listeners
        chipGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n == null) { chipGroup.selectToggle(o); return; }
            // Re-style chips
            chips.getChildren().forEach(node -> {
                if (node instanceof ToggleButton tb) {
                    boolean sel = tb.isSelected();
                    String[] clr = tb.getText().equals("All")
                            ? new String[]{"#6c63ff","white"} : getLeaveChipColor(tb.getText());
                    tb.setStyle("-fx-background-color:" + (sel ? (tb.getText().equals("All") ? "#6c63ff" : clr[0]) : "#e8eaf0") +
                                ";-fx-text-fill:" + (sel ? "white" : "#555") +
                                ";-fx-font-size:11px;-fx-font-weight:bold;-fx-padding:5 14;" +
                                "-fx-background-radius:20;-fx-cursor:hand;");
                }
            });
            refreshList[0].run();
        });
        searchBox.textProperty().addListener((obs, o, n) -> refreshList[0].run());

        refreshList[0].run();

        // ── SCROLL WRAP ──────────────────────────────────────────────────────
        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:white;-fx-border-color:transparent;");
        scroll.setPrefHeight(400);

        // ── FOOTER ───────────────────────────────────────────────────────────
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(13, 22, 15, 22));
        footer.setStyle("-fx-background-color:#f8f9ff;-fx-background-radius:0 0 15 15;" +
                        "-fx-border-color:#e8eaf0;-fx-border-width:1 0 0 0;");
        Label footerNote = new Label("Total: " + allLeaves.size() + " leave records  •  " + totalDaysAll + " total days off");
        footerNote.setStyle("-fx-text-fill:#9ba8b7;-fx-font-size:11px;");
        HBox.setHgrow(footerNote, Priority.ALWAYS);
        Button closeBtn2 = new Button("Close");
        closeBtn2.setStyle("-fx-background-color:linear-gradient(to right,#6c63ff,#5a52d4);" +
                           "-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13px;" +
                           "-fx-padding:9 28;-fx-background-radius:8;-fx-cursor:hand;");
        closeBtn2.setOnAction(e -> popup.close());
        footer.getChildren().addAll(footerNote, closeBtn2);

        // ── ASSEMBLE ─────────────────────────────────────────────────────────
        VBox card = new VBox();
        card.setStyle("-fx-background-color:white;-fx-background-radius:15;" +
                      "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.28),24,0,0,6);");
        card.getChildren().addAll(header, statsRow, chips, scroll, footer);
        card.setMaxWidth(720);
        card.setMinWidth(680);

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color:transparent;");
        root.setPadding(new Insets(12));
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // Helper: colored chip/badge colors per leave type [fg, selectedBg, lightBg]
    private String[] getLeaveChipColor(String type) {
        if (type == null) return new String[]{"#6c63ff","#6c63ff","#ede9ff"};
        return switch (type.toLowerCase()) {
            case "sick"      -> new String[]{"#c0392b","#c0392b","#fde8e8"};
            case "vacation"  -> new String[]{"#1565c0","#1565c0","#dbeafe"};
            case "maternity","paternity" -> new String[]{"#7b1fa2","#7b1fa2","#f3e5f5"};
            case "personal"  -> new String[]{"#2e7d32","#2e7d32","#dcfce7"};
            case "exam"      -> new String[]{"#e65100","#e65100","#fff3e0"};
            case "unpaid"    -> new String[]{"#546e7a","#546e7a","#eceff1"};
            default          -> new String[]{"#6c63ff","#6c63ff","#ede9ff"};
        };
    }

    // Helper: summary stat card for the history popup
    private VBox makeSummaryStatCard(String label, String value, String bg, String fg) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:" + fg + ";-fx-font-size:10px;-fx-font-weight:bold;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:" + fg + ";-fx-font-size:20px;-fx-font-weight:bold;");
        VBox box = new VBox(3, lbl, val);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:10;-fx-padding:12 16;");
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
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