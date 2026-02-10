package dao;

import entity.Leave;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LeaveDaoTest {

    private LeaveDao dao = new LeaveDaoImpl();

    @Test
    void testCreateAndFind() {
        Leave leave = new Leave(
                3,
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 12),
                "Sick Leave",
                "Flu"
        );

        dao.create(leave);

        assertTrue(leave.getId() > 0);

        Leave found = dao.find(leave.getId());
        assertNotNull(found);
        assertEquals("Sick Leave", found.getType());
        assertEquals(3, found.getDuration());
    }

    @Test
    void testUpdate() {
        Leave leave = new Leave(
                5,
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 5),
                "Vacation",
                "Trip"
        );

        dao.create(leave);

        leave.setType("Updated Vacation");
        leave.setReason("Updated reason");
        dao.update(leave);

        Leave updated = dao.find(leave.getId());
        assertEquals("Updated Vacation", updated.getType());
        assertEquals("Updated reason", updated.getReason());
    }

    @Test
    void testDelete() {
        Leave leave = new Leave(
                2,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 2),
                "Personal Leave",
                "Family matter"
        );

        dao.create(leave);

        int id = leave.getId();
        dao.delete(id);

        Leave deleted = dao.find(id);
        assertNull(deleted);
    }
}