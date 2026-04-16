package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private Employee employee;
    private Company company;
    private Department department;

    @BeforeEach
    void setUp() {
        company = new Company("TestCorp", "123 Test Street", "Finland");
        department = new Department(10, "IT", "IT Department", LocalDate.now(), company);

        employee = new Employee(
                "Jane Smith",
                "Female",
                LocalDate.of(2023, 1, 15),
                5500.0,
                company,
                department,
                "Developer",       // role
                "Full-time",       // contractType
                "Senior"           // position
        );
    }

    @Test
    @DisplayName("Employee should be created with constructor parameters")
    void testEmployeeConstructor() {
        assertNotNull(employee);
        assertEquals("Jane Smith", employee.getFullName());
        assertEquals("Female", employee.getGender());
        assertEquals(LocalDate.of(2023, 1, 15), employee.getStartDate());
        assertEquals(5500.0, employee.getSalary());
        assertEquals(company, employee.getCompany());
        assertEquals(department, employee.getDepartment());
        assertEquals("Developer", employee.getRole());
        assertEquals("Full-time", employee.getContractType());
        assertEquals("Senior", employee.getPosition());
    }

    @Test
    @DisplayName("Employee default constructor should create empty object")
    void testEmployeeDefaultConstructor() {
        Employee emptyEmployee = new Employee();
        assertNotNull(emptyEmployee);
        assertNull(emptyEmployee.getFullName());
        assertNull(emptyEmployee.getSalary());
        assertNull(emptyEmployee.getRole());
        assertNull(emptyEmployee.getContractType());
        assertNull(emptyEmployee.getPosition());
    }

    @Test
    @DisplayName("setFullName should update employee name")
    void testSetFullName() {
        employee.setFullName("John Doe");
        assertEquals("John Doe", employee.getFullName());
    }

    @Test
    @DisplayName("setGender should update employee gender")
    void testSetGender() {
        employee.setGender("Male");
        assertEquals("Male", employee.getGender());
    }

    @Test
    @DisplayName("setSalary should update employee salary")
    void testSetSalary() {
        employee.setSalary(6000.0);
        assertEquals(6000.0, employee.getSalary());
    }

    @Test
    @DisplayName("setStartDate should update employee start date")
    void testSetStartDate() {
        LocalDate newDate = LocalDate.of(2024, 6, 1);
        employee.setStartDate(newDate);
        assertEquals(newDate, employee.getStartDate());
    }

    @Test
    @DisplayName("setCompany should update employee company")
    void testSetCompany() {
        Company newCompany = new Company("NewCorp", "456 New Street", "Sweden");
        employee.setCompany(newCompany);
        assertEquals(newCompany, employee.getCompany());
    }

    @Test
    @DisplayName("setDepartment should update employee department")
    void testSetDepartment() {
        Department newDepartment = new Department(20, "HR", "HR Department", LocalDate.now(), company);
        employee.setDepartment(newDepartment);
        assertEquals(newDepartment, employee.getDepartment());
    }

    @Test
    @DisplayName("setRole should update employee role")
    void testSetRole() {
        employee.setRole("Manager");
        assertEquals("Manager", employee.getRole());
    }

    @Test
    @DisplayName("setContractType should update employee contract type")
    void testSetContractType() {
        employee.setContractType("Part-time");
        assertEquals("Part-time", employee.getContractType());
    }

    @Test
    @DisplayName("setPosition should update employee position")
    void testSetPosition() {
        employee.setPosition("Lead");
        assertEquals("Lead", employee.getPosition());
    }

    @Test
    @DisplayName("setLeaves should update employee leaves")
    void testSetLeaves() {
        Leave leave1 = new Leave(LocalDate.now(), LocalDate.now().plusDays(5), "Vacation", "Holiday",employee);
        Leave leave2 = new Leave(LocalDate.now(), LocalDate.now().plusDays(3), "Sick", "Medical",employee);
        List<Leave> leaveList = new ArrayList<>();
        leaveList.add(leave1);
        leaveList.add(leave2);

        employee.setLeaves(leaveList);
        assertEquals(leaveList, employee.getLeaves());
    }

    @Test
    @DisplayName("setId should update employee id")
    void testSetId() {
        employee.setId(100);
        assertEquals(100, employee.getId());
    }

    @Test
    @DisplayName("Salary should handle null values")
    void testNullSalary() {
        employee.setSalary(null);
        assertNull(employee.getSalary());
    }

    @Test
    @DisplayName("getTranslation returns matching translation by language code")
    void testGetTranslation() throws Exception {
        EmployeeTranslation enTranslation = new EmployeeTranslation(
                "en", "Senior Developer", "Full-time", "Female", employee, "15 January 2023", "5500.0"
        );
        addTranslation(employee, enTranslation);

        EmployeeTranslation found = employee.getTranslation("en");
        assertNotNull(found);
        assertEquals("en", found.getLanguage_code());
    }

    @Test
    @DisplayName("getTranslation returns null when no matching language code")
    void testGetTranslationNotFound() {
        assertNull(employee.getTranslation("ar"));
    }

    @Test
    @DisplayName("getTranslatedGender returns translated value when translation exists")
    void testGetTranslatedGender() throws Exception {
        EmployeeTranslation translation = new EmployeeTranslation(
                "es", "Desarrolladora Senior", "Tiempo completo", "Femenino", employee, "15 enero 2023", "5500.0"
        );
        addTranslation(employee, translation);

        assertEquals("Femenino", employee.getTranslatedGender("es"));
    }

    @Test
    @DisplayName("getTranslatedGender falls back to raw gender when no translation")
    void testGetTranslatedGenderFallback() {
        assertEquals("Female", employee.getTranslatedGender("ar"));
    }

    @Test
    @DisplayName("getTranslatedContract returns translated value when translation exists")
    void testGetTranslatedContract() throws Exception {
        EmployeeTranslation translation = new EmployeeTranslation(
                "es", "Desarrolladora Senior", "Tiempo completo", "Femenino", employee, "15 enero 2023", "5500.0"
        );
        addTranslation(employee, translation);

        assertEquals("Tiempo completo", employee.getTranslatedContract("es"));
    }

    @Test
    @DisplayName("getTranslatedContract falls back to raw contractType when no translation")
    void testGetTranslatedContractFallback() {
        assertEquals("Full-time", employee.getTranslatedContract("ar"));
    }

    @Test
    @DisplayName("getTranslatedPosition returns translated value when translation exists")
    void testGetTranslatedPosition() throws Exception {
        EmployeeTranslation translation = new EmployeeTranslation(
                "es", "Desarrolladora Senior", "Tiempo completo", "Femenino", employee, "15 enero 2023", "5500.0"
        );
        addTranslation(employee, translation);

        assertEquals("Desarrolladora Senior", employee.getTranslatedPosition("es"));
    }

    @Test
    @DisplayName("getTranslatedPosition falls back to raw position when no translation")
    void testGetTranslatedPositionFallback() {
        assertEquals("Senior", employee.getTranslatedPosition("ar"));
    }

    @Test
    @DisplayName("getTranslatedStartDate returns translated value when translation exists")
    void testGetTranslatedStartDate() throws Exception {
        EmployeeTranslation translation = new EmployeeTranslation(
                "es", "Desarrolladora Senior", "Tiempo completo", "Femenino", employee, "15 enero 2023", "5500.0"
        );
        addTranslation(employee, translation);

        assertEquals("15 enero 2023", employee.getTranslatedStartDate("es"));
    }

    @Test
    @DisplayName("getTranslatedSalary returns translated salary string when translation exists")
    void testGetTranslatedSalary() throws Exception {
        EmployeeTranslation translation = new EmployeeTranslation(
                "es", "Desarrolladora Senior", "Tiempo completo", "Femenino", employee, "15 enero 2023", "5500.0"
        );
        addTranslation(employee, translation);

        assertEquals("5500.0", employee.getTranslatedSalary("es"));
    }

    @Test
    @DisplayName("setLeaves with null initializes to empty list")
    void testSetLeavesNull() {
        employee.setLeaves(null);
        assertNotNull(employee.getLeaves());
        assertTrue(employee.getLeaves().isEmpty());
    }

    private void addTranslation(Employee emp, EmployeeTranslation translation) throws Exception {
        java.lang.reflect.Field field = Employee.class.getDeclaredField("translations");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Set<EmployeeTranslation> translations = (java.util.Set<EmployeeTranslation>) field.get(emp);
        translations.add(translation);
    }
}
