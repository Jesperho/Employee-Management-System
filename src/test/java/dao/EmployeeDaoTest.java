package dao;

import datasource.MariaDbConnection;
import entity.Company;
import entity.Department;
import entity.Employee;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeDaoTest {

    private EmployeeDao employeeDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        employeeDao = new EmployeeDao();
        em = MariaDbConnection.getEntityManager();
    }

    // Unique name to avoid DB conflicts
    private String uniqueName() {
        return "Employee_" + System.nanoTime();
    }

    private Company createCompany() {
        em.getTransaction().begin();
        Company c = new Company("EmpTestCo_" + System.nanoTime(), "Street 1", "Finland");
        em.persist(c);
        em.getTransaction().commit();
        return c;
    }

    private Department createDepartment(Company company) {
        em.getTransaction().begin();
        Department d = new Department(
                10,
                "Dept_" + System.nanoTime(),
                "Test department",
                LocalDate.now(),
                company
        );
        em.persist(d);
        em.getTransaction().commit();
        return d;
    }

    private Employee createEmployee() {
        Company company = createCompany();
        Department dept = createDepartment(company);

        Employee e = new Employee(
                uniqueName(),          // fullName
                "Male",                // gender
                LocalDate.now(),       // startDate
                3500.0,                // salary
                company,               // company
                dept,                  // department
                "Developer",           // role
                "Full-time",           // contractType
                "Junior Developer"     // position
        );

        // IMPORTANT FIX: prevent orphanRemoval crash
        e.setLeaves(new ArrayList<>());

        employeeDao.create(e);
        return e;
    }

    @Test
    @Order(1)
    void testCreate() {
        Employee e = createEmployee();
        assertTrue(e.getId() > 0);
    }

    @Test
    @Order(2)
    void testFind() {
        Employee e = createEmployee();
        Employee found = employeeDao.find(e.getId());

        assertNotNull(found);
        assertEquals(e.getFullName(), found.getFullName());
        assertEquals(e.getCompany().getId(), found.getCompany().getId());
        assertEquals(e.getDepartment().getId(), found.getDepartment().getId());
    }

    /*
    @Test
    @Order(3)
    void testUpdate() {
        Employee e = createEmployee();

        // IMPORTANT FIX: ensure leaves is not null before merge
        if (e.getLeaves() == null) {
            e.setLeaves(new ArrayList<>());
        }

        String newName = uniqueName();
        e.setFullName(newName);
        employeeDao.update(e);

        Employee updated = employeeDao.find(e.getId());
        assertEquals(newName, updated.getFullName());
    }
  */
    @Test
    @Order(4)
    void testDelete() {
        Employee e = createEmployee();
        int id = e.getId();

        employeeDao.delete(id);

        Employee deleted = employeeDao.find(id);
        assertNull(deleted);
    }

    @Test
    @Order(5)
    void testFindAll() {
        createEmployee();
        createEmployee();

        List<Employee> list = employeeDao.findAll();
        assertTrue(list.size() > 0);
    }

    @Test
    @Order(6)
    void testFindByDepartment() {
        Employee e = createEmployee();
        List<Employee> list = employeeDao.findByDepartment(e.getDepartment().getId());
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(emp -> emp.getId() == e.getId()));
    }

    @Test
    @Order(7)
    void testFindByCompany() {
        Employee e = createEmployee();
        List<Employee> list = employeeDao.findByCompany(e.getCompany().getId());
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(emp -> emp.getId() == e.getId()));
    }

    @Test
    @Order(8)
    void testSearchByName() {
        Employee e = createEmployee();
        List<Employee> list = employeeDao.searchByName(e.getFullName().substring(0, 8));
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(9)
    void testFindByRole() {
        createEmployee();
        List<Employee> list = employeeDao.findByRole("Developer");
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(10)
    void testFindByContractType() {
        createEmployee();
        List<Employee> list = employeeDao.findByContractType("Full-time");
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(11)
    void testSortBySalaryAscending() {
        createEmployee();
        createEmployee();
        List<Employee> list = employeeDao.sortBySalary(true);
        assertFalse(list.isEmpty());
        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).getSalary() <= list.get(i).getSalary());
        }
    }

    @Test
    @Order(12)
    void testSortBySalaryDescending() {
        createEmployee();
        createEmployee();
        List<Employee> list = employeeDao.sortBySalary(false);
        assertFalse(list.isEmpty());
        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).getSalary() >= list.get(i).getSalary());
        }
    }

    @Test
    @Order(13)
    void testSortByStartDateAscending() {
        createEmployee();
        List<Employee> list = employeeDao.sortByStartDate(true);
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(14)
    void testSortByStartDateDescending() {
        createEmployee();
        List<Employee> list = employeeDao.sortByStartDate(false);
        assertFalse(list.isEmpty());
    }

    @Test
    @Order(15)
    void testCountEmployees() {
        createEmployee();
        long count = employeeDao.countEmployees();
        assertTrue(count > 0);
    }

    @Test
    @Order(16)
    void testFindAllWithTranslations() {
        createEmployee();
        List<Employee> list = employeeDao.findAllWithTranslations();
        assertFalse(list.isEmpty());
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) {
            em.close();
        }
    }
}
