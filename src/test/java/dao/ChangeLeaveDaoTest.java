package dao;

import datasource.MariaDbConnection;
import entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChangeLeaveDaoTest {

    private ChangeLeaveDao changeLeaveDao;
    private LeaveDao leaveDao;
    private EmployeeDao employeeDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        changeLeaveDao = new ChangeLeaveDao();
        leaveDao = new LeaveDao();
        employeeDao = new EmployeeDao();
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

    private Department createDepartment(Company company) {
        em.getTransaction().begin();
        Department d = new Department(
                10,
                "Dept_" + unique(),
                "Test dept",
                LocalDate.now(),
                company
        );
        em.persist(d);
        em.getTransaction().commit();
        return d;
    }

    private Employee createEmployee() {
        Company c = createCompany();
        Department d = createDepartment(c);

        Employee e = new Employee(
                "Emp_" + unique(),
                "Male",
                "Test emp",
                LocalDate.now(),
                3000.0,
                c,
                d,
                "Developer",
                "Full-time",
                "Junior"
        );

        e.setLeaves(new ArrayList<>());
        employeeDao.create(e);
        return e;
    }

    private Leave createLeave(Employee e) {
        Leave l = new Leave(
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "Vacation",
                "Test leave",
                e
        );
        leaveDao.create(l);
        return l;
    }

    private ChangeLeave createChangeLeave(Leave leave) {
        ChangeLeave cl = new ChangeLeave(
                LocalDateTime.now(),
                "Leave updated",
                null,   // admin optional
                leave
        );
        changeLeaveDao.create(cl);
        return cl;
    }

    @Test
    @Order(1)
    void testCreate() {
        Employee e = createEmployee();
        Leave l = createLeave(e);
        ChangeLeave cl = createChangeLeave(l);

        assertTrue(cl.getId() > 0);
        assertEquals("Leave updated", cl.getMessage());
    }

    @Test
    @Order(2)
    void testFind() {
        Employee e = createEmployee();
        Leave l = createLeave(e);
        ChangeLeave cl = createChangeLeave(l);

        ChangeLeave found = changeLeaveDao.find(cl.getId());

        assertNotNull(found);
        assertEquals(cl.getMessage(), found.getMessage());
        assertEquals(l.getId(), found.getLeave().getId());
    }

    @Test
    @Order(3)
    void testFindByLeave() {
        Employee e = createEmployee();
        Leave l = createLeave(e);

        ChangeLeave cl1 = createChangeLeave(l);
        ChangeLeave cl2 = createChangeLeave(l);

        List<ChangeLeave> list = changeLeaveDao.findByLeave(l.getId());

        assertTrue(list.stream().anyMatch(x -> x.getId() == cl1.getId()));
        assertTrue(list.stream().anyMatch(x -> x.getId() == cl2.getId()));
    }

    @Test
    @Order(4)
    void testFindByAdmin() {
        Employee e = createEmployee();
        Leave l = createLeave(e);

        Company c = createCompany();
        Admin admin = new Admin("Admin_" + unique(), "pass123", c);

        em.getTransaction().begin();
        em.persist(admin);
        em.getTransaction().commit();

        ChangeLeave cl = new ChangeLeave(
                LocalDateTime.now(),
                "Admin updated leave",
                admin,
                l
        );
        changeLeaveDao.create(cl);

        List<ChangeLeave> list = changeLeaveDao.findByAdmin(admin.getId());

        assertTrue(list.stream().anyMatch(x -> x.getId() == cl.getId()));
    }

    @Test
    @Order(5)
    void testFindLatestForLeave() {
        Employee e = createEmployee();
        Leave l = createLeave(e);

        ChangeLeave older = new ChangeLeave(
                LocalDateTime.now().minusDays(1),
                "Old message",
                null,
                l
        );
        changeLeaveDao.create(older);

        ChangeLeave newer = new ChangeLeave(
                LocalDateTime.now(),
                "New message",
                null,
                l
        );
        changeLeaveDao.create(newer);

        ChangeLeave found = changeLeaveDao.findLatestForLeave(l.getId());

        assertNotNull(found);
        assertEquals("New message", found.getMessage());
    }

    @Test
    @Order(6)
    void testSortByDate() {
        Employee e = createEmployee();
        Leave l = createLeave(e);

        ChangeLeave older = new ChangeLeave(
                LocalDateTime.now().minusDays(2),
                "Older",
                null,
                l
        );
        changeLeaveDao.create(older);

        ChangeLeave newer = new ChangeLeave(
                LocalDateTime.now(),
                "Newer",
                null,
                l
        );
        changeLeaveDao.create(newer);

        List<ChangeLeave> sorted = changeLeaveDao.sortByDate(true);

        int idxOld = sorted.indexOf(older);
        int idxNew = sorted.indexOf(newer);

        assertFalse(idxOld < idxNew);
    }

    @Test
    @Order(7)
    void testDelete() {
        Employee e = createEmployee();
        Leave l = createLeave(e);
        ChangeLeave cl = createChangeLeave(l);

        int id = cl.getId();
        changeLeaveDao.delete(id);

        assertNull(changeLeaveDao.find(id));
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }
}