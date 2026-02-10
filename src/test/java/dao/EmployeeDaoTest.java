package dao;

import datasource.MariaDbConnection;
import entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDaoTest {

    private EmployeeDao dao = new EmployeeDaoImpl();

    private Employee createEmployeeWithDependencies() {

        EntityManager em = MariaDbConnection.getEntityManager();
        em.getTransaction().begin();

        Company company = new Company("TestCo", "Street 1", "Finland");
        Department department = new Department(10, "IT", "Tech dept", LocalDate.now(), company, null);
        Position position = new Position("Developer");
        ContractType contractType = new ContractType("Full-time");

        em.persist(company);
        em.persist(department);
        em.persist(position);
        em.persist(contractType);

        em.getTransaction().commit();
        em.close();

        return new Employee(
                "John Doe",
                "Male",
                "Test employee",
                LocalDate.of(2024, 1, 1),
                3000.0,
                company,
                department,
                position,
                contractType
        );
    }

    @Test
    void testCreateAndFind() {
        Employee e = createEmployeeWithDependencies();
        dao.create(e);

        assertTrue(e.getId() > 0);

        Employee found = dao.find(e.getId());
        assertNotNull(found);
        assertEquals("John Doe", found.getFullName());
    }

    @Test
    void testUpdate() {
        Employee e = createEmployeeWithDependencies();
        dao.create(e);

        e.setFullName("Updated Name");
        dao.update(e);

        Employee updated = dao.find(e.getId());
        assertEquals("Updated Name", updated.getFullName());
    }

    @Test
    void testDelete() {
        Employee e = createEmployeeWithDependencies();
        dao.create(e);

        int id = e.getId();
        dao.delete(id);

        Employee deleted = dao.find(id);
        assertNull(deleted);
    }
}
