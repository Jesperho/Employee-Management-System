package dataseeder;

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        // Create sample admins
        Admin a1 = new Admin("alice@techcorp.com", "pass123", c1);
        Admin a2 = new Admin("bob@bizsolutions.com", "pass123", c2);
        em.persist(a1);
        em.persist(a2);

        // Create departments
        Department d1 = new Department(10, "IT", "Handles IT systems", LocalDate.of(2023,1,1), c1);
        Department d2 = new Department(15, "HR", "Handles HR tasks", LocalDate.of(2023,2,1), c2);
        em.persist(d1);
        em.persist(d2);

        // Create employees
        Employee e1 = new Employee(
                "John Doe",
                "Male",
                "Developer",
                LocalDate.of(2022, 1, 10),
                5000.0,
                c1,
                d1,
                "Developer",
                "Full-time",
                "Software Engineer"
        );

        Employee e2 = new Employee(
                "Jane Roe",
                "Female",
                "HR Manager",
                LocalDate.of(2023, 3, 5),
                4500.0,
                c2,
                d2,
                "HR Manager",
                "Part-time",
                "HR Specialist"
        );

        em.persist(e1);
        em.persist(e2);

        // Create leaves
        Leave l1 = new Leave(
                LocalDate.of(2024,1,1),
                LocalDate.of(2024,1,6),
                "Vacation",
                "Family trip",
                e1
        );

        em.persist(l1);

        // Assign leave to employee
        List<Leave> leavesE1 = new ArrayList<>();
        leavesE1.add(l1);
        e1.setLeaves(leavesE1);

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Sample data inserted successfully!");
    }
}
