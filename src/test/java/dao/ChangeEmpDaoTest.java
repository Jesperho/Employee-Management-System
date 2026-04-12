package dao;

import datasource.MariaDbConnection;
import entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChangeEmpDaoTest {

    private ChangeEmpDao changeEmpDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        changeEmpDao = new ChangeEmpDao();
        em = MariaDbConnection.getEntityManager();
    }

    private String unique() {
        return "X_" + System.nanoTime();
    }

    private Company createCompany() {
        em.getTransaction().begin();
        Company c = new Company("Co_" + unique(), "Street 1", "Finland");
        em.persist(c);
        em.getTransaction().commit();
        return c;
    }

    private Department createDepartment() {
        Company c = createCompany();
        em.getTransaction().begin();
        Department d = new Department(
                10,
                "Dept_" + unique(),
                "Test dept",
                LocalDate.now(),
                c
        );
        em.persist(d);
        em.getTransaction().commit();
        return d;
    }

    private Employee createEmployee() {
        Company c = createCompany();
        Department d = createDepartment();

        em.getTransaction().begin();

        Employee e = new Employee(
                "Emp_" + unique(),
                "Male",
                LocalDate.now(),
                3000.0,
                c,
                d,
                "Developer",
                "Full-time",
                "Software Engineer"
        );

        em.persist(e);
        em.getTransaction().commit();

        return e;
    }

    private Admin createAdmin() {
        Company c = createCompany();
        Admin a = new Admin("Admin_" + unique(), "pass123", c);

        em.getTransaction().begin();
        em.persist(a);
        em.getTransaction().commit();

        return a;
    }

    private ChangeEmp createChangeEmp(Employee e) {
        ChangeEmp ce = new ChangeEmp(
                LocalDate.now(),
                "Employee updated",
                null,
                e
        );
        changeEmpDao.create(ce);
        return ce;
    }

    @Test
    @Order(1)
    void testCreate() {
        Employee e = createEmployee();
        ChangeEmp ce = createChangeEmp(e);

        assertNotNull(ce.getDate());
    }

    @Test
    @Order(2)
    void testFind() {
        Employee e = createEmployee();
        ChangeEmp ce = createChangeEmp(e);

        ChangeEmp found = changeEmpDao.find(ce.getId());

        assertNotNull(found);
        assertEquals(ce.getMessage(), found.getMessage());
    }

    @Test
    @Order(3)
    void testFindByEmployee() {
        Employee e = createEmployee();

        ChangeEmp ce1 = createChangeEmp(e);
        ChangeEmp ce2 = createChangeEmp(e);

        List<ChangeEmp> list = changeEmpDao.findByEmployee(e.getId());

        assertTrue(list.stream().anyMatch(x -> x.getId() == ce1.getId()));
        assertTrue(list.stream().anyMatch(x -> x.getId() == ce2.getId()));
    }

    @Test
    @Order(4)
    void testFindByAdmin() {
        Employee e = createEmployee();
        Admin a = createAdmin();

        ChangeEmp ce = new ChangeEmp(
                LocalDate.now(),
                "Admin updated employee",
                a,
                e
        );

        changeEmpDao.create(ce);

        List<ChangeEmp> list = changeEmpDao.findByAdmin(a.getId());

        assertTrue(list.stream().anyMatch(x -> x.getId() == ce.getId()));
    }

    @Test
    @Order(5)
    void testFindLatestForEmployee() {
        Employee e = createEmployee();

        ChangeEmp older = new ChangeEmp(
                LocalDate.now().minusDays(1),
                "Old message",
                null,
                e
        );
        changeEmpDao.create(older);

        ChangeEmp newer = new ChangeEmp(
                LocalDate.now(),
                "New message",
                null,
                e
        );
        changeEmpDao.create(newer);

        ChangeEmp found = changeEmpDao.findLatestForEmployee(e.getId());

        assertEquals("New message", found.getMessage());
    }

    @Test
    @Order(6)
    void testSortByDate() {
        Employee e = createEmployee();

        ChangeEmp older = new ChangeEmp(
                LocalDate.now().minusDays(2),
                "Older",
                null,
                e
        );
        changeEmpDao.create(older);

        ChangeEmp newer = new ChangeEmp(
                LocalDate.now(),
                "Newer",
                null,
                e
        );
        changeEmpDao.create(newer);

        List<ChangeEmp> sorted = changeEmpDao.sortByDate(true);

        int idxOld = sorted.indexOf(older);
        int idxNew = sorted.indexOf(newer);

        assertFalse(idxOld < idxNew);
    }
}