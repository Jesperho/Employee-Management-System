package dao;

import entity.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionDaoTest {

    private PositionDao dao = new PositionDaoImpl();

    @Test
    void testCreateAndFind() {
        Position p = new Position("Developer");
        dao.create(p);

        assertTrue(p.getId() > 0);

        Position found = dao.find(p.getId());
        assertNotNull(found);
        assertEquals("Developer", found.getTitle());
    }

    @Test
    void testUpdate() {
        Position p = new Position("Tester");
        dao.create(p);

        p.setTitle("Senior Tester");
        dao.update(p);

        Position updated = dao.find(p.getId());
        assertEquals("Senior Tester", updated.getTitle());
    }

    @Test
    void testDelete() {
        Position p = new Position("Intern");
        dao.create(p);

        int id = p.getId();
        dao.delete(id);

        Position deleted = dao.find(id);
        assertNull(deleted);
    }
}
