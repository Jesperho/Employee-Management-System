/*
package dao;

import entity.Company;
import entity.Department;
import entity.Employee;
import entity.Leave;
import org.junit.jupiter.api.Test;
import datasource.MariaDbConnection;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LeaveDaoTest {

    private LeaveDao dao = new LeaveDao();
    private EmployeeDao employeeDao = new EmployeeDao();

    // Helper to create an Employee with dependencies
    private Employee createEmployee() {
        EntityManager em = MariaDbConnection.getEntityManager();
        em.getTransaction().begin();

        Company company = new Company("TestCo", "Street 1", "Finland");
        Department department = new Department(10, "IT", "Tech dept", LocalDate.now(), company);

        em.persist(company);
        em.persist(department);

        em.getTransaction().commit();
        em.close();

        Employee employee = new Employee(
                "Jane Doe",
                "Female",
                "Test Employee",
                LocalDate.of(2024, 1, 1),
                3000.0,
                company,
                department,
                "Developer",
                "Full-time",
                "Engineer"
        );

        employeeDao.create(employee);
        return employee;
    }

    @Test
    void testCreateAndFind() {
        Employee employee = createEmployee();

        Leave leave = new Leave(
                LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 12),
                "Sick Leave",
                "Flu",
                employee
        );

        dao.create(leave);

        assertTrue(leave.getId() > 0);

        Leave found = dao.find(leave.getId());
        assertNotNull(found);
        assertEquals("Sick Leave", found.getType());
        assertEquals("Flu", found.getReason());
        assertEquals(employee.getId(), found.getEmployee().getId());
    }

    @Test
    void testUpdate() {
        Employee employee = createEmployee();

        Leave leave = new Leave(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 5),
                "Vacation",
                "Trip",
                employee
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
        Employee employee = createEmployee();

        Leave leave = new Leave(
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 2),
                "Personal Leave",
                "Family matter",
                employee
        );

        dao.create(leave);

        int id = leave.getId();
        dao.delete(id);

        Leave deleted = dao.find(id);
        assertNull(deleted);
    }
}
*/
