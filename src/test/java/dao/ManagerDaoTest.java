package dao;

import datasource.MariaDbConnection;
import entity.Company;
import entity.Manager;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerDaoTest {

    private ManagerDao dao = new ManagerDaoImpl();

    private Manager createManagerWithCompany() {

        EntityManager em = MariaDbConnection.getEntityManager();
        em.getTransaction().begin();

        Company company = new Company("TestCo", "Street 1", "Finland");
        em.persist(company);

        Manager manager = new Manager(
                "Alice",
                "Smith",
                "alice@test.com",
                "pass123",
                "Female",
                company
        );

        em.getTransaction().commit();
        em.close();

        return manager;
    }

    @Test
    void testCreateAndFind() {
        Manager m = createManagerWithCompany();
        dao.create(m);

        assertTrue(m.getId() > 0);

        Manager found = dao.find(m.getId());
        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
    }

    @Test
    void testUpdate() {
        Manager m = createManagerWithCompany();
        dao.create(m);

        m.setFirstName("UpdatedName");
        dao.update(m);

        Manager updated = dao.find(m.getId());
        assertEquals("UpdatedName", updated.getFirstName());
    }

    @Test
    void testDelete() {
        Manager m = createManagerWithCompany();
        dao.create(m);

        int id = m.getId();
        dao.delete(id);

        Manager deleted = dao.find(id);
        assertNull(deleted);
    }
}
