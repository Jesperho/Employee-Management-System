package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests UI logic, filtering, statistics, and table setup
 */
class SalaryControllerTest {

    private SalaryController controller;

    @BeforeAll
    static void initJavaFX() {
        try { Platform.startup(() -> {}); }
        catch (IllegalStateException e) { /* already started */ }
    }

    @BeforeEach
    void setUp() {
        controller = new SalaryController();
    }

    // ─── Reflection helpers ───────────────────────────────────────────────────

    private void setField(String name, Object value) throws Exception {
        Field f = SalaryController.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(controller, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(String name) throws Exception {
        Field f = SalaryController.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(controller);
    }

    private Object invoke(String methodName, Class<?>[] types, Object... args) throws Exception {
        Method m = SalaryController.class.getDeclaredMethod(methodName, types);
        m.setAccessible(true);
        return m.invoke(controller, args);
    }

    // ─── Helper: salary row (id, name, dept, jobTitle, position, salary, gender)

    private ObservableList<String> salaryRow(String id, String name, String dept,
                                              String job, String pos, String salary, String gender) {
        return FXCollections.observableArrayList(id, name, dept, job, pos, salary, gender);
    }

    // ─── updateStats ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStats: correct totals for three employees")
    void updateStats_correct() throws Exception {
        Text totalPayroll = new Text(); Text avgSalary = new Text();
        Text highestSalary = new Text(); Text employeesPaid = new Text();
        Text totalPayout = new Text();

        setField("totalPayroll",   totalPayroll);
        setField("avgSalary",      avgSalary);
        setField("highestSalary",  highestSalary);
        setField("employeesPaid",  employeesPaid);
        setField("totalPayout",    totalPayout);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(
                salaryRow("1", "Alice",   "IT", "Engineer", "Lead",     "5000", "F"),
                salaryRow("2", "Bob",     "IT", "Engineer", "Employee", "4000", "M"),
                salaryRow("3", "Charlie", "HR", "Analyst",  "Manager",  "6000", "M")
        );
        setField("salaryData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("€15000", totalPayroll.getText());
        assertEquals("€5000",  avgSalary.getText());     // 15000/3
        assertEquals("€6000",  highestSalary.getText());
        assertEquals("3",      employeesPaid.getText());
        assertEquals("Total Payout: €15000", totalPayout.getText());
    }

    @Test
    @DisplayName("updateStats: zeros for empty salary data")
    void updateStats_empty() throws Exception {
        Text totalPayroll = new Text(); Text avgSalary = new Text();
        Text highestSalary = new Text(); Text employeesPaid = new Text();
        Text totalPayout = new Text();

        setField("totalPayroll",   totalPayroll);
        setField("avgSalary",      avgSalary);
        setField("highestSalary",  highestSalary);
        setField("employeesPaid",  employeesPaid);
        setField("totalPayout",    totalPayout);
        setField("salaryData",     FXCollections.observableArrayList());

        invoke("updateStats", new Class[]{});

        assertEquals("€0", totalPayroll.getText());
        assertEquals("€0", avgSalary.getText());
        assertEquals("€0", highestSalary.getText());
        assertEquals("0",  employeesPaid.getText());
        assertEquals("Total Payout: €0", totalPayout.getText());
    }

    @Test
    @DisplayName("updateStats: invalid salary value treated as 0")
    void updateStats_invalidSalary() throws Exception {
        Text totalPayroll = new Text(); Text avgSalary = new Text();
        Text highestSalary = new Text(); Text employeesPaid = new Text();
        Text totalPayout = new Text();

        setField("totalPayroll",   totalPayroll);
        setField("avgSalary",      avgSalary);
        setField("highestSalary",  highestSalary);
        setField("employeesPaid",  employeesPaid);
        setField("totalPayout",    totalPayout);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(
                salaryRow("1", "Alice", "IT", "Dev", "Lead", "N/A",  "F"),
                salaryRow("2", "Bob",   "IT", "Dev", "Emp",  "3000", "M")
        );
        setField("salaryData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("€3000", totalPayroll.getText());
        assertEquals("€1500", avgSalary.getText()); // 3000/2
    }

    @Test
    @DisplayName("updateStats: single employee — avg equals that salary")
    void updateStats_singleEmployee() throws Exception {
        Text totalPayroll = new Text(); Text avgSalary = new Text();
        Text highestSalary = new Text(); Text employeesPaid = new Text();
        Text totalPayout = new Text();

        setField("totalPayroll",   totalPayroll);
        setField("avgSalary",      avgSalary);
        setField("highestSalary",  highestSalary);
        setField("employeesPaid",  employeesPaid);
        setField("totalPayout",    totalPayout);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        data.add(salaryRow("1", "Alice", "IT", "Dev", "Lead", "7000", "F"));
        setField("salaryData", data);

        invoke("updateStats", new Class[]{});

        assertEquals("€7000", totalPayroll.getText());
        assertEquals("€7000", avgSalary.getText());
        assertEquals("€7000", highestSalary.getText());
        assertEquals("1",     employeesPaid.getText());
    }

    // ─── applyFilters ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("applyFilters: department filter shows only IT employees")
    void applyFilters_byDepartment() throws Exception {
        setupFilterScenario(
                salaryRow("1", "Alice",   "IT", "Dev",     "Lead", "5000", "F"),
                salaryRow("2", "Bob",     "HR", "Analyst", "Emp",  "4000", "M"),
                salaryRow("3", "Charlie", "IT", "Dev",     "Emp",  "4500", "M")
        );
        ComboBox<String> filterDept = getField("filterDepartment");
        filterDept.setValue("IT");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("salaryTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: search by name filters correctly")
    void applyFilters_searchByName() throws Exception {
        setupFilterScenario(
                salaryRow("1", "Alice Smith",  "IT", "Dev",     "Lead", "5000", "F"),
                salaryRow("2", "Bob Johnson",  "HR", "Analyst", "Emp",  "4000", "M")
        );
        TextField sf = getField("searchField");
        sf.setText("alice");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("salaryTable");
        assertEquals(1, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: search by department text")
    void applyFilters_searchByDept() throws Exception {
        setupFilterScenario(
                salaryRow("1", "Alice", "IT", "Dev",     "Lead", "5000", "F"),
                salaryRow("2", "Bob",   "HR", "Analyst", "Emp",  "4000", "M")
        );
        TextField sf = getField("searchField");
        sf.setText("hr");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("salaryTable");
        assertEquals(1, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: All Departments shows everyone")
    void applyFilters_allDepts() throws Exception {
        setupFilterScenario(
                salaryRow("1", "Alice", "IT", "Dev",     "Lead", "5000", "F"),
                salaryRow("2", "Bob",   "HR", "Analyst", "Emp",  "4000", "M")
        );
        ComboBox<String> filterDept = getField("filterDepartment");
        filterDept.setValue("All Departments");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("salaryTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: combined department + search text")
    void applyFilters_combined() throws Exception {
        setupFilterScenario(
                salaryRow("1", "Alice",   "IT", "Dev", "Lead", "5000", "F"),
                salaryRow("2", "Bob",     "IT", "Dev", "Emp",  "4000", "M"),
                salaryRow("3", "Charlie", "HR", "Ana", "Mgr",  "6000", "M")
        );
        ComboBox<String> filterDept = getField("filterDepartment");
        filterDept.setValue("IT");
        TextField sf = getField("searchField");
        sf.setText("alice");

        invoke("applyFilters", new Class[]{});

        TableView<ObservableList<String>> table = getField("salaryTable");
        assertEquals(1, table.getItems().size());
    }

    // ─── setupTable column factories ──────────────────────────────────────────

    @Test
    @DisplayName("setupTable configures all column factories including custom salary cell")
    void setupTable_columnFactories() throws Exception {
        setField("salaryTable",    new TableView<>());
        setField("colId",          new TableColumn<>());
        setField("colName",        new TableColumn<>());
        setField("colDepartment",  new TableColumn<>());
        setField("colJobTitle",    new TableColumn<>());
        setField("colPosition",    new TableColumn<>());
        setField("colSalary",      new TableColumn<>());
        setField("colGender",      new TableColumn<>());
        setField("colActions",     new TableColumn<>());

        invoke("setupTable", new Class[]{});

        TableColumn<?, ?> colId      = getField("colId");
        TableColumn<?, ?> colSalary  = getField("colSalary");
        TableColumn<?, ?> colActions = getField("colActions");

        assertNotNull(colId.getCellValueFactory());
        assertNotNull(colSalary.getCellValueFactory());
        assertNotNull(colSalary.getCellFactory());   // custom green color cell
        assertNotNull(colActions.getCellFactory());
    }

    // ─── createFormField / createLabel ───────────────────────────────────────

    @Test
    @DisplayName("createFormField returns TextField with correct prompt and width")
    void createFormField() throws Exception {
        TextField tf = (TextField) invoke("createFormField", new Class[]{String.class}, "Salary Amount");
        assertEquals("Salary Amount", tf.getPromptText());
        assertEquals(250.0, tf.getPrefWidth());
    }

    @Test
    @DisplayName("createLabel returns styled Label with correct text")
    void createLabel() throws Exception {
        Label lbl = (Label) invoke("createLabel", new Class[]{String.class}, "Pay Grade");
        assertEquals("Pay Grade", lbl.getText());
        assertTrue(lbl.getStyle().contains("bold"));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    @SafeVarargs
    private void setupFilterScenario(ObservableList<String>... rows) throws Exception {
        setField("searchField", new TextField());

        ComboBox<String> filterDept = new ComboBox<>(
                FXCollections.observableArrayList("All Departments", "IT", "HR", "Marketing"));
        filterDept.setValue("All Departments");
        setField("filterDepartment", filterDept);

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList(rows);
        setField("salaryData", data);

        TableView<ObservableList<String>> table = new TableView<>();
        table.setItems(data);
        setField("salaryTable", table);
    }
}
