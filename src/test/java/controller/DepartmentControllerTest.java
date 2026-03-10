package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests UI logic, filtering, statistics, and table setup
 */
class DepartmentControllerTest {

    private DepartmentController controller;

    @BeforeAll
    static void initJavaFX() {
        try { Platform.startup(() -> {}); }
        catch (IllegalStateException e) { /* already started */ }
    }

    @BeforeEach
    void setUp() {
        controller = new DepartmentController();
    }

    // ─── Reflection helpers ───────────────────────────────────────────────────

    private void setField(String name, Object value) throws Exception {
        Field f = DepartmentController.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(controller, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(String name) throws Exception {
        Field f = DepartmentController.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(controller);
    }

    private Object invoke(String methodName, Class<?>[] types, Object... args) throws Exception {
        Method m = DepartmentController.class.getDeclaredMethod(methodName, types);
        m.setAccessible(true);
        return m.invoke(controller, args);
    }

    // ─── Helper: build a dept row (id, name, capacity, employees, date, desc) ─

    private ObservableList<String> deptRow(String id, String name, String cap, String emps, String date, String desc) {
        return FXCollections.observableArrayList(id, name, cap, emps, date, desc);
    }

    // ─── updateStats ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStats: correct totals from three departments")
    void updateStats_threeDepts() throws Exception {
        Text total = new Text(); Text active = new Text();
        Text totalEmps = new Text(); Text avgCap = new Text();
        setField("totalDeptCount", total);
        setField("activeDeptCount", active);
        setField("totalEmpInDepts", totalEmps);
        setField("avgCapacity", avgCap);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(
                deptRow("1", "IT",        "50", "30", "2024-01-01", "Tech"),
                deptRow("2", "HR",        "20", "15", "2024-02-01", "HR"),
                deptRow("3", "Marketing", "30", "5",  "2024-03-01", "Mkt")
        );
        setField("deptData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("3", total.getText());
        assertEquals("3", active.getText());
        assertEquals("50", totalEmps.getText());   // 30+15+5
    }

    @Test
    @DisplayName("updateStats: zeros for empty list")
    void updateStats_empty() throws Exception {
        Text total = new Text(); Text active = new Text();
        Text totalEmps = new Text(); Text avgCap = new Text();
        setField("totalDeptCount", total);
        setField("activeDeptCount", active);
        setField("totalEmpInDepts", totalEmps);
        setField("avgCapacity", avgCap);
        setField("deptData", FXCollections.observableArrayList());

        invoke("updateStats", new Class[]{});

        assertEquals("0", total.getText());
        assertEquals("0", active.getText());
        assertEquals("0", totalEmps.getText());
        assertEquals("0%", avgCap.getText());
    }

    // ─── hideEmployeeSection ──────────────────────────────────────────────────

   /* @Test
    @DisplayName("hideEmployeeSection hides and unmanages the VBox")
    void hideEmployeeSection() throws Exception {
        VBox section = new VBox();
        section.setVisible(true);
        section.setManaged(true);
        setField("employeeSection", section);

        invoke("hideEmployeeSection", new Class[]{});

        assertFalse(section.isVisible());
        assertFalse(section.isManaged());
    }
*/
    // ─── handleSearch ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("handleSearch: text match filters results")
    void handleSearch_textMatch() throws Exception {
        setupSearchScenario(
                deptRow("1", "IT Department", "50", "30", "2024-01-01", "Tech"),
                deptRow("2", "HR Department", "20", "15", "2024-02-01", "HR"),
                deptRow("3", "Marketing",     "30", "5",  "2024-03-01", "Mkt")
        );
        TextField sf = getField("searchField");
        sf.setText("IT");

        invoke("handleSearch", new Class[]{});

        TableView<ObservableList<String>> table = getField("departmentTable");
        assertEquals(1, table.getItems().size());
    }

    @Test
    @DisplayName("handleSearch: empty search returns all rows")
    void handleSearch_emptyReturnsAll() throws Exception {
        setupSearchScenario(
                deptRow("1", "IT", "50", "30", "2024-01-01", "Tech"),
                deptRow("2", "HR", "20", "15", "2024-02-01", "HR")
        );
        TextField sf = getField("searchField");
        sf.setText("");

        invoke("handleSearch", new Class[]{});

        TableView<ObservableList<String>> table = getField("departmentTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("handleSearch: Small capacity filter shows only small depts")
    void handleSearch_smallCapacity() throws Exception {
        setupSearchScenario(
                deptRow("1", "IT",   "100", "80", "2024-01-01", "Tech"),
                deptRow("2", "Mini", "10",  "5",  "2024-02-01", "Small")
        );
        ComboBox<String> capFilter = getField("filterCapacity");
        capFilter.setValue("Small (< 30)");

        invoke("handleSearch", new Class[]{});

        TableView<ObservableList<String>> table = getField("departmentTable");
        assertEquals(1, table.getItems().size());
        assertEquals("Mini", table.getItems().get(0).get(1));
    }

    @Test
    @DisplayName("handleSearch: Large capacity filter shows only large depts")
    void handleSearch_largeCapacity() throws Exception {
        setupSearchScenario(
                deptRow("1", "Big",  "100", "80", "2024-01-01", "Big"),
                deptRow("2", "Mini", "10",  "5",  "2024-02-01", "Small")
        );
        ComboBox<String> capFilter = getField("filterCapacity");
        capFilter.setValue("Large (> 50)");

        invoke("handleSearch", new Class[]{});

        TableView<ObservableList<String>> table = getField("departmentTable");
        assertEquals(1, table.getItems().size());
        assertEquals("Big", table.getItems().get(0).get(1));
    }

    @Test
    @DisplayName("handleSearch: Medium capacity filter shows only medium depts")
    void handleSearch_mediumCapacity() throws Exception {
        setupSearchScenario(
                deptRow("1", "Big",    "100", "80", "2024-01-01", ""),
                deptRow("2", "Medium", "40",  "20", "2024-02-01", ""),
                deptRow("3", "Mini",   "10",  "5",  "2024-03-01", "")
        );
        ComboBox<String> capFilter = getField("filterCapacity");
        capFilter.setValue("Medium (30-50)");

        invoke("handleSearch", new Class[]{});

        TableView<ObservableList<String>> table = getField("departmentTable");
        assertEquals(1, table.getItems().size());
        assertEquals("Medium", table.getItems().get(0).get(1));
    }

    @Test
    @DisplayName("handleSearch: combined text + capacity filter")
    void handleSearch_combined() throws Exception {
        setupSearchScenario(
                deptRow("1", "IT Support", "15",  "5",  "2024-01-01", ""),
                deptRow("2", "IT Core",    "100", "80", "2024-02-01", ""),
                deptRow("3", "HR",         "20",  "10", "2024-03-01", "")
        );
        TextField sf = getField("searchField");
        sf.setText("IT");
        ComboBox<String> capFilter = getField("filterCapacity");
        capFilter.setValue("Small (< 30)");

        invoke("handleSearch", new Class[]{});

        TableView<ObservableList<String>> table = getField("departmentTable");
        assertEquals(1, table.getItems().size());
        assertEquals("IT Support", table.getItems().get(0).get(1));
    }

    // ─── handleShowAllDepartments ─────────────────────────────────────────────

   /* @Test
    @DisplayName("handleShowAllDepartments resets filter and hides employee section")
    void handleShowAllDepartments() throws Exception {
        setupSearchScenario(
                deptRow("1", "IT", "50", "30", "2024-01-01", "Tech")
        );
        VBox empSection = new VBox();
        empSection.setVisible(true);
        empSection.setManaged(true);
        setField("employeeSection", empSection);

        ComboBox<String> deptFilter = getField("filterDepartment");
        deptFilter.setValue("IT");

        invoke("handleShowAllDepartments", new Class[]{});

        assertEquals("All Departments", deptFilter.getValue());
        assertFalse(empSection.isVisible());
    }*/

    // ─── createFormField ──────────────────────────────────────────────────────

    @Test
    @DisplayName("createFormField returns TextField with correct prompt and width")
    void createFormField() throws Exception {
        TextField tf = (TextField) invoke("createFormField", new Class[]{String.class}, "Dept Name");
        assertEquals("Dept Name", tf.getPromptText());
        assertEquals(250.0, tf.getPrefWidth());
    }

    // ─── createLabel ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("createLabel returns styled Label with correct text")
    void createLabel() throws Exception {
        Label lbl = (Label) invoke("createLabel", new Class[]{String.class}, "Test");
        assertEquals("Test", lbl.getText());
        assertTrue(lbl.getStyle().contains("bold"));
    }

    // ─── setupTable column factories ──────────────────────────────────────────

    @Test
    @DisplayName("setupTable configures cell value factories and action cell factory")
    void setupTable_columnFactories() throws Exception {
        setField("departmentTable",  new TableView<>());
        setField("colId",            new TableColumn<>());
        setField("colName",          new TableColumn<>());
        setField("colCapacity",      new TableColumn<>());
        setField("colEmployees",     new TableColumn<>());
        setField("colCreationDate",  new TableColumn<>());
        setField("colDescription",   new TableColumn<>());
        setField("colActions",       new TableColumn<>());

        invoke("setupTable", new Class[]{});

        TableColumn<?, ?> colId      = getField("colId");
        TableColumn<?, ?> colName    = getField("colName");
        TableColumn<?, ?> colActions = getField("colActions");
        assertNotNull(colId.getCellValueFactory());
        assertNotNull(colName.getCellValueFactory());
        assertNotNull(colActions.getCellFactory());
    }

    @Test
    @DisplayName("setupEmployeeTable configures employee sub-table column factories")
    void setupEmployeeTable_columnFactories() throws Exception {
        setField("employeeTable",    new TableView<>());
        setField("empColId",         new TableColumn<>());
        setField("empColName",       new TableColumn<>());
        setField("empColDept",       new TableColumn<>());
        setField("empColJobTitle",   new TableColumn<>());
        setField("empColStartDate",  new TableColumn<>());
        setField("empColContract",   new TableColumn<>());
        setField("empColGender",     new TableColumn<>());
        setField("empColActions",    new TableColumn<>());

        invoke("setupEmployeeTable", new Class[]{});

        TableColumn<?, ?> empColId      = getField("empColId");
        TableColumn<?, ?> empColName    = getField("empColName");
        TableColumn<?, ?> empColActions = getField("empColActions");
        assertNotNull(empColId.getCellValueFactory());
        assertNotNull(empColName.getCellValueFactory());
        assertNotNull(empColActions.getCellFactory());
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    @SafeVarargs
    private void setupSearchScenario(ObservableList<String>... rows) throws Exception {
        setField("searchField", new TextField());

        ComboBox<String> deptFilter = new ComboBox<>(
                FXCollections.observableArrayList("All Departments", "IT", "HR", "Marketing"));
        deptFilter.setValue("All Departments");
        setField("filterDepartment", deptFilter);

        ComboBox<String> capFilter = new ComboBox<>(
                FXCollections.observableArrayList("All Capacities", "Small (< 30)", "Medium (30-50)", "Large (> 50)"));
        capFilter.setValue("All Capacities");
        setField("filterCapacity", capFilter);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(rows);
        setField("deptData", data);

        TableView<ObservableList<String>> table = new TableView<>();
        table.setItems(data);
        setField("departmentTable", table);
    }
}
