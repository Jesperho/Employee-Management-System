package controller;

import entity.Department;
import entity.Employee;
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
 * Tests UI logic, filtering, statistics, and form utilities
 */
class EmployeeControllerTest {

    private EmployeeController controller;

    @BeforeAll
    static void initJavaFX() {
        try { Platform.startup(() -> {}); }
        catch (IllegalStateException e) { /* already started */ }
    }

    @BeforeEach
    void setUp() {
        controller = new EmployeeController();
    }

    // ─── Reflection helpers ───────────────────────────────────────────────────

    private void setField(String name, Object value) throws Exception {
        Field f = EmployeeController.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(controller, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(String name) throws Exception {
        Field f = EmployeeController.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(controller);
    }

    private Object invoke(String methodName, Class<?>[] types, Object... args) throws Exception {
        Method m = EmployeeController.class.getDeclaredMethod(methodName, types);
        m.setAccessible(true);
        return m.invoke(controller, args);
    }

    // ─── Helper: build a test Employee ───────────────────────────────────────

    private Employee emp(int id, String name, String gender, String contract, String position, double salary) {
        Employee e = new Employee();
        e.setId(id);
        e.setFullName(name);
        e.setGender(gender);
        e.setContractType(contract);
        e.setPosition(position);
        e.setSalary(salary);
        e.setStartDate(LocalDate.of(2024, 1, 1));
        return e;
    }

    // ─── getOrDefault ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getOrDefault returns value when non-null")
    void getOrDefault_nonNull() throws Exception {
        assertEquals("Engineer", invoke("getOrDefault", new Class[]{String.class}, "Engineer"));
    }

    @Test
    @DisplayName("getOrDefault returns N/A when null")
    void getOrDefault_null() throws Exception {
        assertEquals("N/A", invoke("getOrDefault", new Class[]{String.class}, (String) null));
    }

    // ─── getDepartmentName ────────────────────────────────────────────────────

    @Test
    @DisplayName("getDepartmentName returns N/A when no department assigned")
    void getDeptName_noDept() throws Exception {
        Employee e = emp(1, "Alice", "Female", "Full Time", "Dev", 5000);
        assertEquals("N/A", invoke("getDepartmentName", new Class[]{Employee.class}, e));
    }

    @Test
    @DisplayName("getDepartmentName returns department name when assigned")
    void getDeptName_withDept() throws Exception {
        Employee e = emp(1, "Alice", "Female", "Full Time", "Dev", 5000);
        Department d = new Department();
        d.setDepartmentName("IT");
        e.setDepartment(d);
        assertEquals("IT", invoke("getDepartmentName", new Class[]{Employee.class}, e));
    }

    // ─── updateStats ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStats counts correctly across contract types")
    void updateStats_counts() throws Exception {
        Text total = new Text(); Text ft = new Text(); Text remote = new Text(); Text intern = new Text();
        setField("totalCount", total);
        setField("fullTimeCount", ft);
        setField("remoteCount", remote);
        setField("internCount", intern);

        ObservableList<Employee> data = FXCollections.observableArrayList(
                emp(1, "A", "Male",   "Full Time", "Dev",    5000),
                emp(2, "B", "Female", "Remote",    "Des",    4000),
                emp(3, "C", "Male",   "Full Time", "Lead",   6000),
                emp(4, "D", "Female", "Intern",    "Intern", 1500),
                emp(5, "E", "Other",  "Part Time", "Tester", 3000)
        );
        setField("employeeData", data);
        invoke("updateStats", new Class[]{});

        assertEquals("5", total.getText());
        assertEquals("2", ft.getText());
        assertEquals("1", remote.getText());
        assertEquals("1", intern.getText());
    }

    @Test
    @DisplayName("updateStats shows zeros for empty list")
    void updateStats_empty() throws Exception {
        Text total = new Text(); Text ft = new Text(); Text remote = new Text(); Text intern = new Text();
        setField("totalCount", total);
        setField("fullTimeCount", ft);
        setField("remoteCount", remote);
        setField("internCount", intern);
        setField("employeeData", FXCollections.observableArrayList());
        invoke("updateStats", new Class[]{});

        assertEquals("0", total.getText());
        assertEquals("0", ft.getText());
        assertEquals("0", remote.getText());
        assertEquals("0", intern.getText());
    }

    // ─── applyFilters ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("applyFilters: gender filter Male shows only males")
    void applyFilters_gender() throws Exception {
        setupFiltersAndTable(
                emp(1, "Alice", "Female", "Full Time", "Dev", 5000),
                emp(2, "Bob",   "Male",   "Full Time", "Dev", 4500),
                emp(3, "Carol", "Male",   "Remote",    "Lead",6000)
        );
        ComboBox<String> filterGender = getField("filterGender");
        filterGender.setValue("Male");

        invoke("applyFilters", new Class[]{});

        TableView<Employee> table = getField("employeeTable");
        assertEquals(2, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: contract filter shows only matching contract type")
    void applyFilters_contract() throws Exception {
        setupFiltersAndTable(
                emp(1, "Alice", "Female", "Full Time", "Dev", 5000),
                emp(2, "Bob",   "Male",   "Remote",    "Dev", 4500),
                emp(3, "Carol", "Male",   "Full Time", "Lead",6000)
        );
        ComboBox<String> filterContract = getField("filterContract");
        filterContract.setValue("Remote");

        invoke("applyFilters", new Class[]{});

        TableView<Employee> table = getField("employeeTable");
        assertEquals(1, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: search by name filters correctly")
    void applyFilters_search() throws Exception {
        setupFiltersAndTable(
                emp(1, "Alice Smith",  "Female", "Full Time", "Dev", 5000),
                emp(2, "Bob Johnson",  "Male",   "Full Time", "Dev", 4500)
        );
        TextField searchField = getField("searchField");
        searchField.setText("alice");

        invoke("applyFilters", new Class[]{});

        TableView<Employee> table = getField("employeeTable");
        assertEquals(1, table.getItems().size());
    }

    @Test
    @DisplayName("applyFilters: All Departments + All Genders shows everyone")
    void applyFilters_showAll() throws Exception {
        setupFiltersAndTable(
                emp(1, "Alice", "Female", "Full Time", "Dev", 5000),
                emp(2, "Bob",   "Male",   "Remote",    "Dev", 4500)
        );
        invoke("applyFilters", new Class[]{});

        TableView<Employee> table = getField("employeeTable");
        assertEquals(2, table.getItems().size());
    }

    // ─── handleSearch ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("handleSearch delegates to applyFilters, filters by name")
    void handleSearch_filtersByName() throws Exception {
        setupFiltersAndTable(
                emp(1, "Alice", "Female", "Full Time", "Dev", 5000),
                emp(2, "Bob",   "Male",   "Remote",    "Dev", 4500)
        );
        TextField searchField = getField("searchField");
        searchField.setText("Bob");

        invoke("handleSearch", new Class[]{});

        TableView<Employee> table = getField("employeeTable");
        assertEquals(1, table.getItems().size());
    }

    // ─── createFormField ──────────────────────────────────────────────────────

    @Test
    @DisplayName("createFormField returns TextField with correct prompt and width")
    void createFormField() throws Exception {
        TextField tf = view.FormBuilder.createFormField("Test Prompt");
        assertEquals("Test Prompt", tf.getPromptText());
        assertEquals(200.0, tf.getPrefWidth());
    }

    // ─── createLabel ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("createLabel returns styled Label with correct text")
    void createLabel() throws Exception {
        Label lbl = view.FormBuilder.createLabel("My Label");
        assertEquals("My Label", lbl.getText());
        assertTrue(lbl.getStyle().contains("#2c3e50"));
    }

    // ─── setupTable column factories ──────────────────────────────────────────

    @Test
    @DisplayName("setupTable configures cell value factories for all columns")
    void setupTable_columnFactories() throws Exception {
        TableView<Employee> table = new TableView<>();
        TableColumn<Employee, String> colId       = new TableColumn<>();
        TableColumn<Employee, String> colName     = new TableColumn<>();
        TableColumn<Employee, String> colDept     = new TableColumn<>();
        TableColumn<Employee, String> colJob      = new TableColumn<>();
        TableColumn<Employee, String> colStart    = new TableColumn<>();
        TableColumn<Employee, String> colContract = new TableColumn<>();
        TableColumn<Employee, String> colGender   = new TableColumn<>();
        TableColumn<Employee, String> colActions  = new TableColumn<>();

        setField("employeeTable",  table);
        setField("colId",          colId);
        setField("colName",        colName);
        setField("colDepartment",  colDept);
        setField("colJobTitle",    colJob);
        setField("colStartDate",   colStart);
        setField("colContract",    colContract);
        setField("colGender",      colGender);
        setField("colActions",     colActions);

        invoke("setupTable", new Class[]{});

        assertNotNull(colId.getCellValueFactory());
        assertNotNull(colName.getCellValueFactory());
        assertNotNull(colDept.getCellValueFactory());
        assertNotNull(colJob.getCellValueFactory());
        assertNotNull(colStart.getCellValueFactory());
        assertNotNull(colContract.getCellValueFactory());
        assertNotNull(colGender.getCellValueFactory());
        assertNotNull(colActions.getCellFactory());
    }

    // ─── Helper: wire up filters + table + data ───────────────────────────────

    private void setupFiltersAndTable(Employee... employees) throws Exception {
        setField("searchField", new TextField());

        ComboBox<Department> filterDept = new ComboBox<>();
        filterDept.setValue(null);

        ComboBox<String> filterGender = new ComboBox<>();
        filterGender.setValue(null);

        ComboBox<String> filterContract = new ComboBox<>();
        filterContract.setValue(null);

        setField("filterDepartment", filterDept);
        setField("filterGender",     filterGender);
        setField("filterContract",   filterContract);

        ObservableList<Employee> data = FXCollections.observableArrayList(employees);
        setField("employeeData", data);

        TableView<Employee> table = new TableView<>();
        table.setItems(data);
        setField("employeeTable", table);
    }
}
