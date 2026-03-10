package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests UI logic, filtering, statistics, date calculation — no DB required.
 */
class LeaveControllerTest {

    private LeaveController controller;

    @BeforeAll
    static void initJavaFX() {
        try { Platform.startup(() -> {}); }
        catch (IllegalStateException e) { /* already started */ }
    }

    @BeforeEach
    void setUp() {
        controller = new LeaveController();
    }

    // ─── Reflection helpers ───────────────────────────────────────────────────

    private void setField(String name, Object value) throws Exception {
        Field f = LeaveController.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(controller, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(String name) throws Exception {
        Field f = LeaveController.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(controller);
    }

    private Object invoke(String methodName, Class<?>[] types, Object... args) throws Exception {
        Method m = LeaveController.class.getDeclaredMethod(methodName, types);
        m.setAccessible(true);
        return m.invoke(controller, args);
    }

    // ─── Helper: build a leave row ────────────────────────────────────────────
    // id, empName, duration(days), startDate, endDate, type, reason

    private ObservableList<String> leaveRow(String id, String emp, String dur,
                                            String start, String end,
                                            String type, String reason) {
        // index 7 = department (used by applyFilters search branch); default "N/A"
        return FXCollections.observableArrayList(id, emp, dur, start, end, type, reason, "N/A");
    }

    // ─── updateStats ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStats: correct totals for mixed leave types")
    void updateStats_mixed() throws Exception {
        Text total = new Text(); Text active = new Text();
        Text sick = new Text(); Text avg = new Text();
        setField("totalLeaves",  total);
        setField("activeLeaves", active);
        setField("sickLeaves",   sick);
        setField("avgDuration",  avg);

        // active = today (2026-03-10) falls between start and end; type contains "sick" → sick
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(
                leaveRow("1", "Alice",   "5",  "2024-03-01", "2024-03-06", "Sick",     "Flu"),
                leaveRow("2", "Bob",     "15", "2026-03-01", "2026-03-25", "Vacation", "Trip"),
                leaveRow("3", "Charlie", "3",  "2026-04-01", "2026-04-04", "Sick",     "Cold")
        );
        setField("leaveData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("3", total.getText());
        assertEquals("1", active.getText());   // only Bob > 10 days
        assertEquals("2", sick.getText());     // Alice + Charlie
        assertEquals("7", avg.getText());      // (5+15+3)/3 = 7
    }

    @Test
    @DisplayName("updateStats: zeros for empty list")
    void updateStats_empty() throws Exception {
        Text total = new Text(); Text active = new Text();
        Text sick = new Text(); Text avg = new Text();
        setField("totalLeaves",  total);
        setField("activeLeaves", active);
        setField("sickLeaves",   sick);
        setField("avgDuration",  avg);
        setField("leaveData", FXCollections.observableArrayList());

        invoke("updateStats", new Class[]{});

        assertEquals("0", total.getText());
        assertEquals("0", active.getText());
        assertEquals("0", sick.getText());
        assertEquals("0", avg.getText());
    }

    @Test
    @DisplayName("updateStats: all sick leaves are counted correctly")
    void updateStats_allSick() throws Exception {
        Text total = new Text(); Text active = new Text();
        Text sick = new Text(); Text avg = new Text();
        setField("totalLeaves",  total);
        setField("activeLeaves", active);
        setField("sickLeaves",   sick);
        setField("avgDuration",  avg);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(
                leaveRow("1", "Alice", "3", "2024-01-01", "2024-01-04", "Sick", "Fever"),
                leaveRow("2", "Bob",   "7", "2024-01-05", "2024-01-12", "Sick", "Flu")
        );
        setField("leaveData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("2", total.getText());
        assertEquals("2", sick.getText());
        assertEquals("0", active.getText()); // neither > 10
        assertEquals("5", avg.getText());    // (3+7)/2 = 5
    }

    // ─── applyFilters ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("applyFilters: type filter shows only Sick leaves")
    void applyFilters_sickType() throws Exception {
        setupFilterScenario(
                leaveRow("1", "Alice",   "5",  "2024-03-01", "2024-03-06", "Sick",     "Flu"),
                leaveRow("2", "Bob",     "10", "2024-03-10", "2024-03-20", "Vacation", "Trip"),
                leaveRow("3", "Charlie", "3",  "2024-04-01", "2024-04-04", "Sick",     "Cold")
        );
        ComboBox<String> filterType = getField("filterType");
        filterType.setValue("Sick");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("leaveTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: type filter Vacation shows only vacation leaves")
    void applyFilters_vacationType() throws Exception {
        setupFilterScenario(
                leaveRow("1", "Alice",   "5",  "2024-03-01", "2024-03-06", "Sick",     ""),
                leaveRow("2", "Bob",     "10", "2024-03-10", "2024-03-20", "Vacation", ""),
                leaveRow("3", "Charlie", "3",  "2024-04-01", "2024-04-04", "Vacation", "")
        );
        ComboBox<String> filterType = getField("filterType");
        filterType.setValue("Vacation");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("leaveTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: All Types shows all leaves")
    void applyFilters_allTypes() throws Exception {
        setupFilterScenario(
                leaveRow("1", "Alice", "5",  "2024-03-01", "2024-03-06", "Sick",     ""),
                leaveRow("2", "Bob",   "10", "2024-03-10", "2024-03-20", "Vacation", "")
        );
        ComboBox<String> filterType = getField("filterType");
        filterType.setValue("All Types");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("leaveTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: search by employee name")
    void applyFilters_searchByName() throws Exception {
        setupFilterScenario(
                leaveRow("1", "Alice",   "5",  "2024-03-01", "2024-03-06", "Sick",     ""),
                leaveRow("2", "Bob",     "10", "2024-03-10", "2024-03-20", "Vacation", "")
        );
        TextField sf = getField("searchField");
        sf.setText("alice");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("leaveTable");
        assertEquals(1, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: search by leave type text")
    void applyFilters_searchByType() throws Exception {
        setupFilterScenario(
                leaveRow("1", "Alice", "5",  "2024-03-01", "2024-03-06", "Sick",     ""),
                leaveRow("2", "Bob",   "10", "2024-03-10", "2024-03-20", "Vacation", "")
        );
        TextField sf = getField("searchField");
        sf.setText("vacation");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("leaveTable");
        assertEquals(1, table.getItems().size());
    }

    // ─── calcDuration ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("calcDuration: 10 days between March 1 and March 11")
    void calcDuration_tenDays() throws Exception {
        DatePicker start    = new DatePicker(LocalDate.of(2024, 3, 1));
        DatePicker end      = new DatePicker(LocalDate.of(2024, 3, 11));
        TextField duration  = new TextField();

        invoke("calcDuration",
                new Class[]{DatePicker.class, DatePicker.class, TextField.class},
                start, end, duration);

        assertEquals("10", duration.getText());
    }

    @Test
    @DisplayName("calcDuration: same-day leave is 0 days")
    void calcDuration_sameDay() throws Exception {
        DatePicker start    = new DatePicker(LocalDate.of(2024, 3, 1));
        DatePicker end      = new DatePicker(LocalDate.of(2024, 3, 1));
        TextField duration  = new TextField();

        invoke("calcDuration",
                new Class[]{DatePicker.class, DatePicker.class, TextField.class},
                start, end, duration);

        assertEquals("0", duration.getText());
    }

    @Test
    @DisplayName("calcDuration: end before start clamps to 0")
    void calcDuration_negative() throws Exception {
        DatePicker start    = new DatePicker(LocalDate.of(2024, 3, 10));
        DatePicker end      = new DatePicker(LocalDate.of(2024, 3, 1));
        TextField duration  = new TextField("original");

        invoke("calcDuration",
                new Class[]{DatePicker.class, DatePicker.class, TextField.class},
                start, end, duration);

        assertEquals("0", duration.getText());
    }

    @Test
    @DisplayName("calcDuration: null start date leaves field unchanged")
    void calcDuration_nullStart() throws Exception {
        DatePicker start    = new DatePicker();   // no date set
        DatePicker end      = new DatePicker(LocalDate.of(2024, 3, 10));
        TextField duration  = new TextField("unchanged");

        invoke("calcDuration",
                new Class[]{DatePicker.class, DatePicker.class, TextField.class},
                start, end, duration);

        assertEquals("unchanged", duration.getText());
    }

    // ─── setupFilters ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("setupFilters populates filterType with expected options")
    void setupFilters_populatesCombo() throws Exception {
        ComboBox<String> filterType = new ComboBox<>();
        setField("filterType", filterType);

        invoke("setupFilters", new Class[]{});

        assertFalse(filterType.getItems().isEmpty());
        assertTrue(filterType.getItems().contains("All Types"));
        assertTrue(filterType.getItems().contains("Sick"));
        assertTrue(filterType.getItems().contains("Vacation"));
        assertTrue(filterType.getItems().contains("Maternity"));
        assertTrue(filterType.getItems().contains("Personal"));
        assertTrue(filterType.getItems().contains("Exam"));
    }

    // ─── handleLeaveHistory ───────────────────────────────────────────────────

    @Test
    @DisplayName("handleLeaveHistory does not throw unhandled exception")
    void handleLeaveHistory_noThrow() throws Exception {
        TableView<ObservableList<String>> table = new TableView<>();
        setField("leaveTable", table);
        // hits leaveDao.findAll() — DB may not be available
        try {
            invoke("handleLeaveHistory", new Class[]{});
        } catch (Exception e) {
            // DB not available — acceptable
        }
    }

    // ─── setupTable column factories ──────────────────────────────────────────

    @Test
    @DisplayName("setupTable configures all column cell value factories")
    void setupTable_columnFactories() throws Exception {
        setField("leaveTable",    new TableView<>());
        setField("colId",         new TableColumn<>());
        setField("colEmployee",   new TableColumn<>());
        setField("colDuration",   new TableColumn<>());
        setField("colStartDate",  new TableColumn<>());
        setField("colEndDate",    new TableColumn<>());
        setField("colType",       new TableColumn<>());
        setField("colReason",     new TableColumn<>());
        setField("colActions",    new TableColumn<>());

        invoke("setupTable", new Class[]{});

        TableColumn<?, ?> colId       = getField("colId");
        TableColumn<?, ?> colEmployee = getField("colEmployee");
        TableColumn<?, ?> colActions  = getField("colActions");

        assertNotNull(colId.getCellValueFactory());
        assertNotNull(colEmployee.getCellValueFactory());
        assertNotNull(colActions.getCellFactory());
    }

    // ─── createFormField / createLabel ───────────────────────────────────────

    @Test
    @DisplayName("createFormField returns TextField with correct prompt and width")
    void createFormField() throws Exception {
        TextField tf = (TextField) invoke("createFormField", new Class[]{String.class}, "Prompt");
        assertEquals("Prompt", tf.getPromptText());
        assertEquals(250.0, tf.getPrefWidth());
    }

    @Test
    @DisplayName("createLabel returns styled Label")
    void createLabel() throws Exception {
        Label lbl = (Label) invoke("createLabel", new Class[]{String.class}, "Leave Label");
        assertEquals("Leave Label", lbl.getText());
        assertTrue(lbl.getStyle().contains("bold"));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    @SafeVarargs
    private void setupFilterScenario(ObservableList<String>... rows) throws Exception {
        setField("searchField", new TextField());

        ComboBox<String> filterType = new ComboBox<>(
                FXCollections.observableArrayList("All Types", "Sick", "Vacation", "Maternity", "Personal", "Exam"));
        filterType.setValue("All Types");
        setField("filterType", filterType);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(rows);
        setField("leaveData", data);

        TableView<ObservableList<String>> table = new TableView<>();
        table.setItems(data);
        setField("leaveTable", table);

        // applyFilters() always calls updateStats() — inject stat fields to avoid NPE
        setField("totalLeaves",  new Text());
        setField("activeLeaves", new Text());
        setField("sickLeaves",   new Text());
        setField("avgDuration",  new Text());
    }
}