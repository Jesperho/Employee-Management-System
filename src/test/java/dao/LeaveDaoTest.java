package dao;

import datasource.MariaDbConnection;
import entity.Company;
import entity.Department;
import entity.Employee;
import entity.Leave;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LeaveDaoTest {

    private LeaveDao leaveDao;
    private EmployeeDao employeeDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        leaveDao = new LeaveDao();
        employeeDao = new EmployeeDao();
        em = MariaDbConnection.getEntityManager();
    }

    private String uniqueName() {
        return "Emp_" + System.nanoTime();
    }

    private Company createCompany() {
        em.getTransaction().begin();
        Company c = new Company("LeaveTestCo_" + System.nanoTime(), "Street 1", "Finland");
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
                uniqueName(),
                "Male",
                "Test employee",
                LocalDate.now(),
                3500.0,
                company,
                dept,
                "Developer",
                "Full-time",
                "Junior Developer"
        );

        e.setLeaves(new ArrayList<>()); // avoid orphanRemoval issues

        employeeDao.create(e);
        return e;
    }

    private Leave createLeave(Employee employee) {
        Leave leave = new Leave(
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "Vacation",
                "Test leave",
                employee
        );

        leaveDao.create(leave);
        return leave;
    }

    @Test
    @Order(1)
    void testCreate() {
        Employee e = createEmployee();
        Leave l = createLeave(e);

        assertTrue(l.getId() > 0);
        assertEquals(4, l.getDuration());
    }

    @Test
    @Order(2)
    void testFind() {
        Employee e = createEmployee();
        Leave l = createLeave(e);

        Leave found = leaveDao.find(l.getId());

        assertNotNull(found);
        assertEquals("Vacation", found.getType());
        assertEquals(e.getId(), found.getEmployee().getId());
    }

    @Test
    @Order(3)
    void testUpdate() {
        Employee e = createEmployee();
        Leave l = createLeave(e);

        l.setReason("Updated reason");
        leaveDao.update(l);

        Leave updated = leaveDao.find(l.getId());
        assertEquals("Updated reason", updated.getReason());
    }

    @Test
    @Order(4)
    void testDelete() {
        Employee e = createEmployee();
        Leave l = createLeave(e);
        int id = l.getId();

        leaveDao.delete(id);

        Leave deleted = leaveDao.find(id);
        assertNull(deleted);
    }

    @Test
    @Order(5)
    void testFindAll() {
        Employee e = createEmployee();
        createLeave(e);
        createLeave(e);

        List<Leave> list = leaveDao.findAll();
        assertTrue(list.size() > 0);
    }

    @Test
    @Order(6)
    void testFindByEmployee() {
        Employee e = createEmployee();
        Leave l1 = createLeave(e);
        Leave l2 = createLeave(e);

        List<Leave> list = leaveDao.findByEmployee(e.getId());

        assertTrue(list.size() >= 2);
        assertTrue(list.stream().anyMatch(l -> l.getId() == l1.getId()));
        assertTrue(list.stream().anyMatch(l -> l.getId() == l2.getId()));
    }

    @Test
    @Order(7)
    void testFindByType() {
        Employee e = createEmployee();
        createLeave(e);

        List<Leave> list = leaveDao.findByType("Vacation");
        assertTrue(list.size() > 0);
    }

    @Test
    @Order(8)
    void testFindByDateRange() {
        Employee e = createEmployee();

        Leave l = new Leave(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                "Vacation",
                "Range test",
                e
        );
        leaveDao.create(l);

        List<Leave> list = leaveDao.findByDateRange(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 10)
        );

        assertTrue(list.stream().anyMatch(x -> x.getId() == l.getId()));
    }

    @Test
    @Order(9)
    void testSortByStartDate() {
        Employee e = createEmployee();

        Leave l1 = new Leave(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 3), "Vacation", "A", e);
        Leave l2 = new Leave(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 12), "Vacation", "B", e);

        leaveDao.create(l1);
        leaveDao.create(l2);

        List<Leave> sorted = leaveDao.sortByStartDate(true);

        int idx1 = -1;
        int idx2 = -1;

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getId() == l1.getId()) idx1 = i;
            if (sorted.get(i).getId() == l2.getId()) idx2 = i;
        }

        assertTrue(idx1 != -1 && idx2 != -1);
        assertTrue(idx1 < idx2);
    }

    @Test
    @Order(10)
    void testSortByEndDate() {
        Employee e = createEmployee();

        Leave l1 = new Leave(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 3), "Vacation", "A", e);
        Leave l2 = new Leave(LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 12), "Vacation", "B", e);

        leaveDao.create(l1);
        leaveDao.create(l2);

        List<Leave> sorted = leaveDao.sortByEndDate(true);

        int idx1 = -1;
        int idx2 = -1;

        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getId() == l1.getId()) idx1 = i;
            if (sorted.get(i).getId() == l2.getId()) idx2 = i;
        }

        assertTrue(idx1 != -1 && idx2 != -1);
        assertTrue(idx1 < idx2);
    }

    @Test
    @Order(11)
    void testHasOverlappingLeave() {
        Employee e = createEmployee();

        Leave l1 = new Leave(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5),
                "Vacation",
                "Overlap test",
                e
        );
        leaveDao.create(l1);

        boolean overlaps = leaveDao.hasOverlappingLeave(
                e.getId(),
                LocalDate.of(2024, 1, 4),
                LocalDate.of(2024, 1, 10)
        );

        assertTrue(overlaps);
    }

    @Test
    @Order(12)
    void testCountLeaves() {
        long before = leaveDao.countLeaves();

        Employee e = createEmployee();
        createLeave(e);

        long after = leaveDao.countLeaves();

        assertEquals(before + 1, after);
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) {
            em.close();
        }
    }
}
