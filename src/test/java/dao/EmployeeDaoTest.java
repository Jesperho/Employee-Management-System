package dao;

import datasource.MariaDbConnection;
import entity.Company;
import entity.Department;
import entity.Employee;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDaoTest {

    private EmployeeDao dao = new EmployeeDao();

    // Helper method to create an Employee entity with dependencies
    private Employee createEmployeeWithDependencies() {

        EntityManager em = MariaDbConnection.getEntityManager();
        em.getTransaction().begin();

        Company company = new Company("TestCo", "Street 1", "Finland");
        Department department = new Department(10, "IT", "Tech dept", LocalDate.now(), company);

        em.persist(company);
        em.persist(department);

        em.getTransaction().commit();
        em.close();

        // Create Employee entity
        return new Employee(
                "John Doe",
                "Male",
                "Test employee",
                LocalDate.of(2024, 1, 1),
                3000.0,
                company,
                department,
                "Developer",    // role
                "Full-time",    // contractType
                "Engineer"      // position
        );
    }

    /*@Test
    void testCreateAndFind() {
        Employee e = createEmployeeWithDependencies();
        dao.create(e);  // Persist Employee

        assertTrue(e.getId() > 0, "Employee ID should be set after create");

        Employee found = dao.find(e.getId());
        assertNotNull(found);
        assertEquals("John Doe", found.getFullName());
    }*/

    /*
    @Test
    void testUpdate() {
        Employee e = createEmployeeWithDependencies();
        dao.create(e);

        e.setFullName("Updated Name");
        dao.update(e);

        Employee updated = dao.find(e.getId());
        assertEquals("Updated Name", updated.getFullName());
    }
  */
   /* @Test
    void testDelete() {
        Employee e = createEmployeeWithDependencies();
        dao.create(e);

        int id = e.getId();
        dao.delete(id);

        Employee deleted = dao.find(id);
        assertNull(deleted);
    }*/
}
