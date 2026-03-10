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
class ChangeDeptDaoTest {

    private ChangeDeptDao changeDeptDao;
    private DepartmentDao departmentDao;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        changeDeptDao = new ChangeDeptDao();
        departmentDao = new DepartmentDao();
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

    private Admin createAdmin() {
        Company c = createCompany();
        Admin a = new Admin("Admin_" + unique(), "pass123", c);
        em.getTransaction().begin();
        em.persist(a);
        em.getTransaction().commit();
        return a;
    }

    private ChangeDept createChangeDept(Department d) {
        ChangeDept cd = new ChangeDept(
                LocalDateTime.now(),
                "Department updated",
                null,
                d
        );
        changeDeptDao.create(cd);
        return cd;
    }

    @Test
    @Order(1)
    void testCreate() {
        Department d = createDepartment();
        ChangeDept cd = createChangeDept(d);
        assertTrue(cd.getDate() != null);
    }

    @Test
    @Order(2)
    void testFind() {
        Department d = createDepartment();
        ChangeDept cd = createChangeDept(d);

        ChangeDept found = changeDeptDao.find(cd.getId());
        assertNotNull(found);
        assertEquals(cd.getMessage(), found.getMessage());
    }

    @Test
    @Order(3)
    void testFindByDepartment() {
        Department d = createDepartment();
        ChangeDept cd1 = createChangeDept(d);
        ChangeDept cd2 = createChangeDept(d);

        List<ChangeDept> list = changeDeptDao.findByDepartment(d.getId());

        assertTrue(list.stream().anyMatch(x -> x.getId() == cd1.getId()));
        assertTrue(list.stream().anyMatch(x -> x.getId() == cd2.getId()));
    }

    @Test
    @Order(4)
    void testFindByAdmin() {
        Department d = createDepartment();
        Admin a = createAdmin();

        ChangeDept cd = new ChangeDept(
                LocalDateTime.now(),
                "Admin updated department",
                a,
                d
        );
        changeDeptDao.create(cd);

        List<ChangeDept> list = changeDeptDao.findByAdmin(a.getId());
        assertTrue(list.stream().anyMatch(x -> x.getId() == cd.getId()));
    }

    @Test
    @Order(5)
    void testFindLatestForDepartment() {
        Department d = createDepartment();

        ChangeDept older = new ChangeDept(
                LocalDateTime.now().minusDays(1),
                "Old message",
                null,
                d
        );
        changeDeptDao.create(older);

        ChangeDept newer = new ChangeDept(
                LocalDateTime.now(),
                "New message",
                null,
                d
        );
        changeDeptDao.create(newer);

        ChangeDept found = changeDeptDao.findLatestForDepartment(d.getId());
        assertEquals("New message", found.getMessage());
    }

    @Test
    @Order(6)
    void testSortByDate() {
        Department d = createDepartment();

        ChangeDept older = new ChangeDept(
                LocalDateTime.now().minusDays(2),
                "Older",
                null,
                d
        );
        changeDeptDao.create(older);

        ChangeDept newer = new ChangeDept(
                LocalDateTime.now(),
                "Newer",
                null,
                d
        );
        changeDeptDao.create(newer);

        List<ChangeDept> sorted = changeDeptDao.sortByDate(true);

        int idxOld = sorted.indexOf(older);
        int idxNew = sorted.indexOf(newer);

        assertFalse(idxOld < idxNew);
    }
}