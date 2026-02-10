package dao;

import entity.ContractType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContractTypeDaoTest {

    private ContractTypeDao dao = new ContractTypeDaoImpl();

    @Test
    void testCreateAndFind() {
        ContractType ct = new ContractType("Full-time");
        dao.create(ct);

        assertTrue(ct.getId() > 0);

        ContractType found = dao.find(ct.getId());
        assertNotNull(found);
        assertEquals("Full-time", found.getTitle());
    }

    @Test
    void testUpdate() {
        ContractType ct = new ContractType("Part-time");
        dao.create(ct);

        ct.setTitle("Updated Contract");
        dao.update(ct);

        ContractType updated = dao.find(ct.getId());
        assertEquals("Updated Contract", updated.getTitle());
    }

    @Test
    void testDelete() {
        ContractType ct = new ContractType("Temporary");
        dao.create(ct);

        int id = ct.getId();
        dao.delete(id);

        ContractType deleted = dao.find(id);
        assertNull(deleted);
    }
}
