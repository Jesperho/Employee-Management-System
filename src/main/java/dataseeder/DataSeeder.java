package dataseeder;

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;

public class DataSeeder {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CompanyMariaDbUnit");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Create sample companies
        Company c1 = new Company("TechCorp", "123 Tech Street", "USA");
        Company c2 = new Company("BizSolutions", "456 Business Ave", "UK");
        em.persist(c1);
        em.persist(c2);

        // Create sample managers
        Manager m1 = new Manager("Alice", "Smith", "alice@techcorp.com", "pass123", "Female", c1);
        Manager m2 = new Manager("Bob", "Johnson", "bob@bizsolutions.com", "pass123", "Male", c2);
        em.persist(m1);
        em.persist(m2);

        // Create departments
        Department d1 = new Department(10, "IT", "Handles IT systems", LocalDate.now(), c1, m1);
        Department d2 = new Department(15, "HR", "Handles HR tasks", LocalDate.now(), c2, m2);
        em.persist(d1);
        em.persist(d2);

        // Create positions
        Position p1 = new Position("Developer");
        Position p2 = new Position("HR Specialist");
        em.persist(p1);
        em.persist(p2);

        // Create contract types
        ContractType ct1 = new ContractType("Full-time");
        ContractType ct2 = new ContractType("Part-time");
        em.persist(ct1);
        em.persist(ct2);

        // Create employees
        Employee e1 = new Employee("John Doe", "Male", "Developer", LocalDate.of(2022,1,10), 5000.0, c1, d1, p1, ct1);
        Employee e2 = new Employee("Jane Roe", "Female", "HR Manager", LocalDate.of(2023,3,5), 4500.0, c2, d2, p2, ct2);
        em.persist(e1);
        em.persist(e2);

        // Create leaves
        Leave l1 = new Leave(5, LocalDate.of(2024,1,1), LocalDate.of(2024,1,6), "Vacation", "Family trip");
        em.persist(l1);

        // Assign leave to employee
        e1.setLeave(l1);

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Sample data inserted successfully!");
    }
}