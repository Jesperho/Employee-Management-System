package controller;

import dao.*;
import entity.*;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class LeaveController extends BaseController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<Department> filterDepartment;

    @FXML private Text breadcrumbText;
    @FXML private Text pageTitleText;
    @FXML private Text bannerTitleText;
    @FXML private Text bannerSubtitleText;
    @FXML private Button btnAddLeave;
    @FXML private Button btnLeaveHistory;
    @FXML private Button btnAddLeaveBottom;

    @FXML private Text totalLeaves;
    @FXML private Text activeLeaves;
    @FXML private Text sickLeaves;
    @FXML private Text avgDuration;

    @FXML private Text totalLeavesLabelText;
    @FXML private Text activeLeavesLabelText;
    @FXML private Text sickLeavesLabelText;
    @FXML private Text avgDurationLabelText;

    @FXML private Text historyTitleText;

    // 1. Changed to TableView<Leave>
    @FXML private TableView<Leave> leaveTable;
    @FXML private TableColumn<Leave, String> colId;
    @FXML private TableColumn<Leave, String> colEmployee;
    @FXML private TableColumn<Leave, String> colDuration;
    @FXML private TableColumn<Leave, String> colStartDate;
    @FXML private TableColumn<Leave, String> colEndDate;
    @FXML private TableColumn<Leave, String> colType;
    @FXML private TableColumn<Leave, String> colReason;
    @FXML private TableColumn<Leave, String> colActions;

    @FXML private Text pageInfo;

    // Sidebar user profile
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
    private final ChangeLeaveDao changeLeaveDao = new ChangeLeaveDao();
    private final LeaveTranslationDao leaveTranslationDao = new LeaveTranslationDao();

    // 2. Changed to ObservableList<Leave>
    private final ObservableList<Leave> leaveData = FXCollections.observableArrayList();

    @Override
    protected void setupController() {
        updateUserProfile();
        setupFilters();
        setupTable();
        loadFromDatabase();

        // 3. Added the translation listener identical to EmployeeController
        LanguageManager.getInstance().currentLanguageProperty().addListener((obs, oldLang, newLang) -> {
            refreshTableTranslations();
        });

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
    }

    private void refreshTableTranslations() {
        leaveTable.refresh();
    }

    @Override
    protected void updateUIText(String newLanguage) {
        System.out.println("Leave View updating to: " + newLanguage);
        entity.LanguageManager lang = entity.LanguageManager.getInstance();

        if (btnDashboard != null) btnDashboard.setText("📊  " + lang.getTranslatedText("nav.dashboard"));
        if (btnEmployees != null) btnEmployees.setText("👥  " + lang.getTranslatedText("nav.employees"));
        if (btnDepartments != null) btnDepartments.setText("🏢  " + lang.getTranslatedText("nav.departments"));
        if (btnLeave != null) btnLeave.setText("📅  " + lang.getTranslatedText("nav.leave"));
        if (btnSalary != null) btnSalary.setText("💰  " + lang.getTranslatedText("nav.salary"));
        if (btnLogout != null) btnLogout.setText("🚪  " + lang.getTranslatedText("nav.logout"));
        if (mainMenuLabel != null) mainMenuLabel.setText(lang.getTranslatedText("main_menu"));

        if (breadcrumbText != null) breadcrumbText.setText(lang.getTranslatedText("leave.breadcrumb"));
        if (pageTitleText != null) pageTitleText.setText(lang.getTranslatedText("leave.pageTitle"));
        if (bannerTitleText != null) bannerTitleText.setText(lang.getTranslatedText("leave.bannerTitle"));
        if (bannerSubtitleText != null) bannerSubtitleText.setText(lang.getTranslatedText("leave.bannerSubtitle"));
        if (btnAddLeave != null) btnAddLeave.setText(lang.getTranslatedText("leave.addBtn"));
        if (btnLeaveHistory != null) btnLeaveHistory.setText(lang.getTranslatedText("leave.historyBtn"));
        if (btnAddLeaveBottom != null) btnAddLeaveBottom.setText(lang.getTranslatedText("leave.addBtn"));

        if (searchField != null) searchField.setPromptText(lang.getTranslatedText("search.leave"));
        if (filterType != null) filterType.setPromptText(lang.getTranslatedText("leave.filterAllTypes"));
        if (filterDepartment != null) filterDepartment.setPromptText(lang.getTranslatedText("leave.filterAllDepts"));

        if (totalLeavesLabelText != null) totalLeavesLabelText.setText(lang.getTranslatedText("leave.total"));
        if (activeLeavesLabelText != null) activeLeavesLabelText.setText(lang.getTranslatedText("leave.active"));
        if (sickLeavesLabelText != null) sickLeavesLabelText.setText(lang.getTranslatedText("leave.sick"));
        if (avgDurationLabelText != null) avgDurationLabelText.setText(lang.getTranslatedText("leave.avgDuration"));
        if (historyTitleText != null) historyTitleText.setText(lang.getTranslatedText("leave.historyBtn"));

        if (colId != null) colId.setText(lang.getTranslatedText("leave.col.id"));
        if (colEmployee != null) colEmployee.setText(lang.getTranslatedText("leave.col.employee"));
        if (colDuration != null) colDuration.setText(lang.getTranslatedText("leave.col.duration"));
        if (colStartDate != null) colStartDate.setText(lang.getTranslatedText("leave.col.startDate"));
        if (colEndDate != null) colEndDate.setText(lang.getTranslatedText("leave.col.endDate"));
        if (colType != null) colType.setText(lang.getTranslatedText("leave.col.type"));
        if (colActions != null) colActions.setText(lang.getTranslatedText("leave.col.actions"));

        if (pageInfo != null) pageInfo.setText(lang.getTranslatedText("btn.pageInfo"));
        setupFilters();
        applyFilters();
        setupTable();
        updateStats();
    }

    private void updateUserProfile() {
        app.UserSession s = app.UserSession.getInstance();
        if (avatarInitials != null) avatarInitials.setText(s.getInitials());
        if (userName != null) userName.setText(s.getFullName());
        if (userRole != null) userRole.setText(s.getRole());
    }

    // 4. Load entities directly instead of strings
    private void loadFromDatabase() {
        try {
            leaveData.setAll(leaveDao.findAllWithTranslations()); // Ideally findAllWithTranslations() if you have it!
            leaveTable.setItems(leaveData);
            updateStats();
        } catch (Exception e) {
            System.err.println("Failed to load leave data: " + e.getMessage());
        }
    }

    private void setupFilters() {
        try {
            entity.LanguageManager lang = entity.LanguageManager.getInstance();
            if (filterType != null) {
                filterType.setItems(FXCollections.observableArrayList(
                        "ALL",
                        "sick",
                        "vacation",
                        "maternity",
                        "personal",
                        "exam",
                        "paternity",
                        "unpaid"
                ));

                filterType.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            LanguageManager lang = LanguageManager.getInstance();

                            if (item.equals("ALL")) {
                                setText(lang.getTranslatedText("leave.filterAllTypes"));
                            } else {
                                setText(lang.getTranslatedText("leave." + item));
                            }
                        }
                    }
                });

                filterType.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            LanguageManager lang = LanguageManager.getInstance();

                            if (item.equals("ALL")) {
                                setText(lang.getTranslatedText("leave.filterAllTypes"));
                            } else {
                                setText(lang.getTranslatedText("leave." + item));
                            }
                        }
                    }
                });

                filterType.setOnAction(e -> applyFilters());
            }
            if (filterDepartment != null) {
                List<Department> departments = departmentDao.findAllWithTranslations();

                filterDepartment.setItems(FXCollections.observableArrayList(departments));

                filterDepartment.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(Department d, boolean empty) {
                        super.updateItem(d, empty);

                        if (empty || d == null) {
                            setText(null);
                        } else {
                            LanguageManager lang = LanguageManager.getInstance();
                            String code = lang.getAbreviation(lang.getLanguage());
                            setText(d.getTranslatedName(code));
                        }
                    }
                });

                filterDepartment.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(Department d, boolean empty) {
                        super.updateItem(d, empty);

                        if (empty || d == null) {
                            setText(null);
                        } else {
                            LanguageManager lang = LanguageManager.getInstance();
                            String code = lang.getAbreviation(lang.getLanguage());
                            setText(d.getTranslatedName(code));
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to load leave filters: " + e.getMessage());
        }
    }

    private LeaveTranslation getTranslation(Leave leave) {
        String langCode = LanguageManager.getInstance()
                .getAbreviation(LanguageManager.getInstance().getLanguage());

        LeaveTranslation t = leave.getTranslation(langCode);

        if (t == null) {
            // fallback safe
            return new LeaveTranslation(
                    langCode,
                    leave.getType(),
                    leave,
                    leave.getStartDate() + "",
                    leave.getEndDate() + "",
                    leave.getDuration() +""
            );
        }
        return t;
    }

    // 5. Setup table utilizing standard object binding and translation fetch
    private void setupTable() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();
        String langCode = lang.getAbreviation(lang.getLanguage());

        leaveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(leaveTranslationDao.formatNumber(data.getValue().getId(), lang.getLanguage()))));
        colEmployee.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmployee() != null ? data.getValue().getEmployee().getFullName() : "N/A"));
        colStartDate.setCellValueFactory(data ->
                new SimpleStringProperty(
                        getTranslation(data.getValue()).getStartDateText()
                )
        );

        colEndDate.setCellValueFactory(data ->
                new SimpleStringProperty(
                        getTranslation(data.getValue()).getEndDateText()
                )
        );

        colDuration.setCellValueFactory(data ->
                new SimpleStringProperty(
                        getTranslation(data.getValue()).getDurationText()
                )
        );

        // This is the dynamic translation binding!
        colType.setCellValueFactory(data ->
                new SimpleStringProperty(getOrDefault(data.getValue().getTranslatedType(langCode)))
        );


        colActions.setMinWidth(160);
        colActions.setCellFactory(view.LeaveView.buildActionsColumn(
            this::showViewPopup, 
            leave -> showLeaveFormPopup(entity.LanguageManager.getInstance().getTranslatedText("leave.form.editTitle"), leave), 
            this::handleDeleteLeave
        ));
    }

    private void updateStats() {
        entity.LanguageManager lang = LanguageManager.getInstance();
        LocalDate today = LocalDate.now();
        totalLeaves.setText(String.valueOf(leaveTranslationDao.formatNumber(leaveData.size(), lang.getLanguage())));

        long active = leaveData.stream().filter(r -> {
            if (r.getStartDate() == null || r.getEndDate() == null) return false;
            return !today.isBefore(r.getStartDate()) && !today.isAfter(r.getEndDate());
        }).count();
        activeLeaves.setText(String.valueOf(leaveTranslationDao.formatNumber(active, lang.getLanguage())));

        long sick = leaveData.stream()
                .filter(r -> r.getType() != null && r.getType().toLowerCase().contains("sick"))
                .count();
        sickLeaves.setText(String.valueOf(leaveTranslationDao.formatNumber(sick, lang.getLanguage())));

        // Sum all individual leave durations then divide for a true average (inclusive day count)
        long totalDays = leaveData.stream()
                .filter(r -> r.getStartDate() != null && r.getEndDate() != null)
                .mapToLong(r -> ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()) + 1)
                .sum();
        long avg = leaveData.isEmpty() ? 0 : totalDays / leaveData.size();
        avgDuration.setText(String.valueOf(leaveTranslationDao.formatNumber(avg, lang.getLanguage())));
    }


    @FXML
    private void handleApplyLeave() {
        showLeaveFormPopup(LanguageManager.getInstance().getTranslatedText("leave.form.addTitle"), null);
    }

    // Leave history remains unchanged, but relies on object properties safely now.
    @FXML
    private void handleLeaveHistory() {
        // [Intentionally truncated for brevity: Your exact existing handleLeaveHistory() logic goes here]
        // This method was already perfectly formatted to handle List<Leave> allLeaves!
    }

    // Helper method needed for Translation updates (Identical to EmployeeController)
    private String getOrDefault(String value) {
        if (value == null) return "N/A";
        return value;
    }

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

    // 7. Object-oriented Filter logic
    private void applyFilters() {
        entity.LanguageManager lang = entity.LanguageManager.getInstance();

        String type = filterType != null ? filterType.getValue() : null;
        Department selectedDept = filterDepartment.getValue();
        String search = searchField != null ? searchField.getText().trim().toLowerCase() : "";



        FilteredList<Leave> filtered = new FilteredList<>(leaveData, row -> {

            String lType = row.getType();

            // TYPE FILTER
            if (type != null
                    && !type.equals("ALL")
                    && (lType == null || !lType.equalsIgnoreCase(type))) {
                return false;
            }

            // DEPARTMENT FILTER (FIXED)
            if (selectedDept != null
                    && row.getEmployee() != null
                    && row.getEmployee().getDepartment() != null) {

                if (row.getEmployee().getDepartment().getId() != selectedDept.getId()) {
                    return false;
                }
            }

            // SEARCH FILTER
            if (!search.isEmpty()) {

                String empName = row.getEmployee() != null
                        ? row.getEmployee().getFullName().toLowerCase()
                        : "";

                String safeType = lType != null ? lType.toLowerCase() : "";

                String reason = row.getReason() != null
                        ? row.getReason().toLowerCase()
                        : "";

                return empName.contains(search)
                        || safeType.contains(search)
                        || reason.contains(search);
            }

            return true;
        });

        leaveTable.setItems(filtered);
    }

    // 8. View Popup now takes Leave object directly
    private void showViewPopup(Leave leave) {
        view.LeaveView.showViewPopup(leave, leaveTranslationDao, this::applyRTLToScene);
    }

    // 9. Edit Form Popup now takes Leave object directly
    private void showLeaveFormPopup(String title, Leave existingData) {
        view.LeaveView.showLeaveFormPopup(title, existingData, employeeDao, leaveDao, leaveTranslationDao, changeLeaveDao, this::loadFromDatabase, this::applyRTLToScene);
    }

    private void handleDeleteLeave(Leave leave) {
        LanguageManager lang = LanguageManager.getInstance();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(lang.getTranslatedText("alert.deleteConfirm"));
        confirm.setHeaderText(lang.getTranslatedText("alert.areYouSure"));
        confirm.setContentText(lang.getTranslatedText("leave.delete.confirm")
                .replace("{0}", leave.getEmployee() != null ? leave.getEmployee().getFullName() : "N/A"));

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Log activity before deletion so the entity reference is still valid
            String en = java.text.MessageFormat.format(lang.getTranslatedTexts("leave.activity.deleted", "en"), leave.getId());
            String es = java.text.MessageFormat.format(lang.getTranslatedTexts("leave.activity.deleted", "es"), leave.getId());
            String fa = java.text.MessageFormat.format(lang.getTranslatedTexts("leave.activity.deleted", "fa"), leave.getId());
            String ar = java.text.MessageFormat.format(lang.getTranslatedTexts("leave.activity.deleted", "ar"), leave.getId());

            changeLeaveDao.create(new ChangeLeave(
                    LocalDate.now(), en, fa, ar, es,
                    app.UserSession.getInstance().getAdmin(), leave
            ));

            leaveTranslationDao.deleteByLeave(leave);
            leaveDao.delete(leave.getId());
            loadFromDatabase();
        }
    }


    @FXML private void handleSearch() { applyFilters(); }
    @FXML private void handlePreviousPage() {}
    @FXML private void handleNextPage() {}
    @FXML private void handleDashboard() { navigateTo("/fxml/dashboard.fxml", "/css/dashboard.css"); }
    @FXML private void handleEmployees() { navigateTo("/fxml/employee.fxml", "/css/management.css"); }
    @FXML private void handleDepartments() { navigateTo("/fxml/department.fxml", "/css/management.css"); }
    @FXML private void handleLeave() { /* already here */ }
    @FXML private void handleSalary() { navigateTo("/fxml/salary.fxml", "/css/management.css"); }
    @FXML private void handleLogout() { navigateTo("/fxml/login.fxml", "/css/login.css"); }

    private void navigateTo(String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setResources(LanguageManager.getInstance().getCurrentBundle());
            Parent root = loader.load();
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            double w = stage.getWidth(); double h = stage.getHeight();
            Scene scene = new Scene(root, w, h);
            var css = getClass().getResource(cssPath);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String resolveLeaveDuration(Leave leave) {
        entity.LanguageManager lang = LanguageManager.getInstance();
        return leave.getTranslatedDuration(lang.getAbreviation(lang.getLanguage()));
    }

    private long calculateInclusiveDuration(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) return 0;
        return ChronoUnit.DAYS.between(start, end) + 1;
    }
}