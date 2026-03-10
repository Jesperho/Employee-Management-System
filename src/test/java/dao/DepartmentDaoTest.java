/*
package dao;

import datasource.MariaDbConnection;
import entity.Company;
import entity.Department;
import entity.Admin;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentDaoTest {

    private DepartmentDao dao = new DepartmentDao();

    private Department createDepartmentWithDependencies() {

        EntityManager em = MariaDbConnection.getEntityManager();
        em.getTransaction().begin();

        Company company = new Company("TestCo", "Street 1", "Finland");
        em.persist(company);

        Admin manager = new Admin(
                "alice@test.com",
                "pass123",
                company
        );
        em.persist(manager);

        Department department = new Department(
                10,
                "IT",
                "Tech department",
                LocalDate.now(),
                company
        );

        em.getTransaction().commit();
        em.close();

        return department;
    }

    @Test
    void testCreateAndFind() {
        Department d = createDepartmentWithDependencies();
        dao.create(d);

        assertTrue(d.getId() > 0);

        Department found = dao.find(d.getId());
        assertNotNull(found);
        assertEquals("IT", found.getDepartmentName());
    }

    @Test
    void testUpdate() {
        Department d = createDepartmentWithDependencies();
        dao.create(d);

        d.setDepartmentName("Updated Dept");
        dao.update(d);

        Department updated = dao.find(d.getId());
        assertEquals("Updated Dept", updated.getDepartmentName());
    }

    @Test
    void testDelete() {
        Department d = createDepartmentWithDependencies();
        dao.create(d);

        int id = d.getId();
        dao.delete(id);

        Department deleted = dao.find(id);
        assertNull(deleted);
    }
}*/
