package dao;

import datasource.MariaDbConnection;
import entity.Admin;
import entity.Company;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminDaoTest {

    private AdminDao dao = new AdminDao();

    // Helper to create an Admin with a Company
    private Admin createAdminWithCompany() {
        EntityManager em = MariaDbConnection.getEntityManager();
        em.getTransaction().begin();

        // Create and persist a company
        Company company = new Company("TestCo", "Street 1", "Finland");
        em.persist(company);

        em.getTransaction().commit();
        em.close();

        // Create Admin linked to the company
        return new Admin(
                "alice@test.com",
                "pass123",
                company
        );
    }

    @Test
    void testCreateAndFind() {
        Admin m = createAdminWithCompany();
        dao.create(m);

        // ID should be generated
        assertTrue(m.getId() > 0);

        Admin found = dao.find(m.getId());
        assertNotNull(found);
        assertEquals("alice@test.com", found.getEmail());
        assertNotNull(found.getCompany());
        assertEquals("TestCo", found.getCompany().getName());
    }

    @Test
    void testUpdate() {
        Admin m = createAdminWithCompany();
        dao.create(m);

        // Update email
        m.setEmail("updated@test.com");
        dao.update(m);

        Admin updated = dao.find(m.getId());
        assertEquals("updated@test.com", updated.getEmail());
    }

    @Test
    void testDelete() {
        Admin m = createAdminWithCompany();
        dao.create(m);

        int id = m.getId();
        dao.delete(id);

        Admin deleted = dao.find(id);
        assertNull(deleted);
    }
}
