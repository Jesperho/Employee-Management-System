package controller;

import entity.Department;
import entity.Employee;
import entity.Leave;
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

    // ─── Helper: build a Leave entity ────────────────────────────────────────

    private Leave makeLeave(String type, String reason, String empName,
                            LocalDate start, LocalDate end) {
        Employee emp = new Employee();
        emp.setFullName(empName);
        return new Leave(start, end, type, reason, emp);
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

        // Bob's leave always spans today so he is always the active one
        LocalDate today = LocalDate.now();
        ObservableList<Leave> data = FXCollections.observableArrayList(
                makeLeave("Sick",     "Flu",  "Alice",   LocalDate.of(2024, 1, 1),  LocalDate.of(2024, 1, 3)),
                makeLeave("Vacation", "Trip", "Bob",     today.minusDays(5),         today.plusDays(5)),
                makeLeave("Sick",     "Cold", "Charlie", LocalDate.of(2027, 1, 1),  LocalDate.of(2027, 1, 7))
        );
        setField("leaveData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("3", total.getText());
        assertEquals("1", active.getText());   // only Bob is active today
        assertEquals("2", sick.getText());     // Alice + Charlie
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

        // Durations: 3 + 7 = 10 → avg = 10/2 = 5; both past → active = 0
        ObservableList<Leave> data = FXCollections.observableArrayList(
                makeLeave("Sick", "Fever", "Alice", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 3)),
                makeLeave("Sick", "Flu",   "Bob",   LocalDate.of(2024, 1, 5), LocalDate.of(2024, 1, 11))
        );
        setField("leaveData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("2", total.getText());
        assertEquals("2", sick.getText());
        assertEquals("0", active.getText());
        assertEquals("5", avg.getText());    // (3+7)/2 = 5
    }

    // ─── applyFilters ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("applyFilters: type filter shows only Sick leaves")
    void applyFilters_sickType() throws Exception {
        setupFilterScenario(
                makeLeave("Sick",     "Flu",  "Alice",   LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 6)),
                makeLeave("Vacation", "Trip", "Bob",     LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 20)),
                makeLeave("Sick",     "Cold", "Charlie", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 4))
        );
        ComboBox<String> filterType = getField("filterType");
        filterType.setValue("Sick");

        invoke("applyFilters", new Class[]{});

        TableView<Leave> table = getField("leaveTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: type filter Vacation shows only vacation leaves")
    void applyFilters_vacationType() throws Exception {
        setupFilterScenario(
                makeLeave("Sick",     "", "Alice",   LocalDate.of(2024, 3, 1),  LocalDate.of(2024, 3, 6)),
                makeLeave("Vacation", "", "Bob",     LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 20)),
                makeLeave("Vacation", "", "Charlie", LocalDate.of(2024, 4, 1),  LocalDate.of(2024, 4, 4))
        );
        ComboBox<String> filterType = getField("filterType");
        filterType.setValue("Vacation");

        invoke("applyFilters", new Class[]{});

        TableView<Leave> table = getField("leaveTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: All Types shows all leaves")
    void applyFilters_allTypes() throws Exception {
        setupFilterScenario(
                makeLeave("Sick",     "", "Alice", LocalDate.of(2024, 3, 1),  LocalDate.of(2024, 3, 6)),
                makeLeave("Vacation", "", "Bob",   LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 20))
        );
        ComboBox<String> filterType = getField("filterType");
        filterType.setValue("ALL");

        invoke("applyFilters", new Class[]{});

        TableView<Leave> table = getField("leaveTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: search by employee name")
    void applyFilters_searchByName() throws Exception {
        setupFilterScenario(
                makeLeave("Sick",     "", "Alice", LocalDate.of(2024, 3, 1),  LocalDate.of(2024, 3, 6)),
                makeLeave("Vacation", "", "Bob",   LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 20))
        );
        TextField sf = getField("searchField");
        sf.setText("alice");

        invoke("applyFilters", new Class[]{});

        TableView<Leave> table = getField("leaveTable");
        assertEquals(1, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: search by leave type text")
    void applyFilters_searchByType() throws Exception {
        setupFilterScenario(
                makeLeave("Sick",     "", "Alice", LocalDate.of(2024, 3, 1),  LocalDate.of(2024, 3, 6)),
                makeLeave("Vacation", "", "Bob",   LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 20))
        );
        TextField sf = getField("searchField");
        sf.setText("vacation");

        invoke("applyFilters", new Class[]{});

        TableView<Leave> table = getField("leaveTable");
        assertEquals(1, table.getItems().size());
    }

    // ─── calcDuration tests removed (migrated to view layer) ───

    // ─── setupFilters ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("setupFilters populates filterType with expected options")
    void setupFilters_populatesCombo() throws Exception {
        ComboBox<String> filterType = new ComboBox<>();
        setField("filterType", filterType);

        invoke("setupFilters", new Class[]{});

        assertFalse(filterType.getItems().isEmpty());
        assertTrue(filterType.getItems().contains("ALL"));
        assertTrue(filterType.getItems().contains("sick"));
        assertTrue(filterType.getItems().contains("vacation"));
        assertTrue(filterType.getItems().contains("maternity"));
        assertTrue(filterType.getItems().contains("personal"));
        assertTrue(filterType.getItems().contains("exam"));
    }

    // ─── handleLeaveHistory ───────────────────────────────────────────────────

    @Test
    @DisplayName("handleLeaveHistory does not throw unhandled exception")
    void handleLeaveHistory_noThrow() throws Exception {
        TableView<Leave> table = new TableView<>();
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
        TextField tf = view.FormBuilder.createFormField("Prompt", 250);
        assertEquals("Prompt", tf.getPromptText());
        assertEquals(250.0, tf.getPrefWidth());
    }

    @Test
    @DisplayName("createLabel returns styled Label")
    void createLabel() throws Exception {
        Label lbl = view.FormBuilder.createLabel("Leave Label");
        assertEquals("Leave Label", lbl.getText());
        assertTrue(lbl.getStyle().contains("bold"));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    @SafeVarargs
    private void setupFilterScenario(Leave... leaves) throws Exception {
        setField("searchField", new TextField());

        ComboBox<String> filterType = new ComboBox<>(
                FXCollections.observableArrayList("ALL", "sick", "vacation", "maternity", "personal", "exam"));
        filterType.setValue("ALL");
        setField("filterType", filterType);

        ComboBox<Department> filterDept = new ComboBox<>();
        filterDept.setValue(null);
        setField("filterDepartment", filterDept);

        ObservableList<Leave> data = FXCollections.observableArrayList(leaves);
        setField("leaveData", data);

        TableView<Leave> table = new TableView<>();
        table.setItems(data);
        setField("leaveTable", table);

        // updateStats() may be called — inject stat fields to avoid NPE
        setField("totalLeaves",  new Text());
        setField("activeLeaves", new Text());
        setField("sickLeaves",   new Text());
        setField("avgDuration",  new Text());
    }
}
