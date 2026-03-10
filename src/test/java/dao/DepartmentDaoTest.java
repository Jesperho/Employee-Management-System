package dao;

import datasource.MariaDbConnection;
import entity.Company;
import entity.Department;
import entity.Employee;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentDaoTest {

    private DepartmentDao departmentDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        departmentDao = new DepartmentDao();
        em = MariaDbConnection.getEntityManager();
    }

    private String uniqueDeptName() {
        return "Dept_" + System.nanoTime();
    }

    private Company createCompany() {
        em.getTransaction().begin();
        Company c = new Company("DeptTestCo_" + System.nanoTime(), "Street 1", "Finland");
        em.persist(c);
        em.getTransaction().commit();
        return c;
    }

    private Department createDepartment(Company company) {
        Department d = new Department(
                10,
                uniqueDeptName(),
                "Test department",
                LocalDate.now(),
                company
        );
        departmentDao.create(d);
        return d;
    }

    private Employee createEmployee(Department dept) {
        em.getTransaction().begin();

        Employee e = new Employee(
                "John Doe",
                "Male",
                "Test employee",
                LocalDate.now(),
                3000.0,
                dept.getCompany(),
                dept,
                "Developer",
                "Full-time",
                "Junior Developer"
        );

        em.persist(e);
        em.getTransaction().commit();
        return e;
    }

    @Test
    @Order(1)
    void testFindByCompany() {
        Company company = createCompany();
        Department d1 = createDepartment(company);
        Department d2 = createDepartment(company);

        List<Department> list = departmentDao.findByCompany(company.getId());

        assertTrue(list.size() >= 2);
        assertTrue(list.stream().anyMatch(d -> d.getId() == d1.getId()));
        assertTrue(list.stream().anyMatch(d -> d.getId() == d2.getId()));
    }

    @Test
    @Order(2)
    void testCountEmployees() {
        Company company = createCompany();
        Department dept = createDepartment(company);

        createEmployee(dept);
        createEmployee(dept);

        long count = departmentDao.countEmployees(dept.getId());
        assertEquals(2, count);
    }

    @Test
    @Order(3)
    void testSafeDeleteFailsWhenEmployeesExist() {
        Company company = createCompany();
        Department dept = createDepartment(company);

        createEmployee(dept);

        boolean deleted = departmentDao.safeDelete(dept.getId());
        assertFalse(deleted);

        Department found = departmentDao.find(dept.getId());
        assertNotNull(found);
    }

    @Test
    @Order(4)
    void testSafeDeleteSucceedsWhenNoEmployees() {
        Company company = createCompany();
        Department dept = createDepartment(company);

        boolean deleted = departmentDao.safeDelete(dept.getId());
        assertTrue(deleted);

        Department found = departmentDao.find(dept.getId());
        assertNull(found);
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) {
            em.close();
        }
    }
}