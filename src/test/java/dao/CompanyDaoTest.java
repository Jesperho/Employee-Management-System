package dao;

import entity.Company;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyDaoTest {

    @Test
    void testCreateAndFind() {
        CompanyDao dao = new CompanyDao();
        Company c = new Company("JUnitCo", "Test Road 5", "Finland");

        dao.create(c);

        Company found = dao.find(c.getId());
        assertNotNull(found);
        assertEquals("JUnitCo", found.getName());
    }

    @Test
    void testUpdate() {
        CompanyDao dao = new CompanyDao();
        Company c = new Company("OldName", "Street 1", "Finland");

        dao.create(c);

        c.setName("NewName");
        dao.update(c);

        Company updated = dao.find(c.getId());
        assertEquals("NewName", updated.getName());
    }

    @Test
    void testDelete() {
        CompanyDao dao = new CompanyDao();
        Company c = new Company("DeleteMe", "Street 2", "Finland");

        dao.create(c);
        int id = c.getId();

        dao.delete(id);

        Company deleted = dao.find(id);
        assertNull(deleted);
    }
}