package dataseeder;

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;

public class DataSeeder {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CompanyMariaDbUnit");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // ── Get or create company ──────────────────────────────────
        Company c1 = getOrCreateCompany(em, "TechCorp", "123 Tech Street", "Finland");

        // ── Get or create departments ──────────────────────────────
        Department dIT  = getOrCreateDept(em, "IT",         "Handles IT systems",        20, LocalDate.of(2023, 1, 1), c1);
        Department dHR  = getOrCreateDept(em, "HR",         "Handles HR tasks",           15, LocalDate.of(2023, 2, 1), c1);
        Department dFin = getOrCreateDept(em, "Finance",    "Financial management",       12, LocalDate.of(2023, 3, 1), c1);
        Department dMkt = getOrCreateDept(em, "Marketing",  "Marketing and promotions",   18, LocalDate.of(2023, 4, 1), c1);
        Department dOps = getOrCreateDept(em, "Operations", "Day-to-day operations",      25, LocalDate.of(2023, 5, 1), c1);

        // ── IT Department – 7 employees ────────────────────────────
        addEmp(em, "Alice Johnson",  "Female", "Senior Dev",         LocalDate.of(2021, 3, 15), 6200.0, c1, dIT,  "Developer",      "Full-time", "Software Engineer");
        addEmp(em, "Bob Smith",      "Male",   "Backend Dev",        LocalDate.of(2022, 1, 10), 5500.0, c1, dIT,  "Developer",      "Full-time", "Backend Developer");
        addEmp(em, "Carol White",    "Female", "Frontend Dev",       LocalDate.of(2022, 6, 20), 5200.0, c1, dIT,  "Developer",      "Full-time", "Frontend Developer");
        addEmp(em, "David Lee",      "Male",   "DevOps Engineer",    LocalDate.of(2021, 9,  5), 5800.0, c1, dIT,  "DevOps",         "Full-time", "DevOps Engineer");
        addEmp(em, "Eva Martinez",   "Female", "QA Engineer",        LocalDate.of(2023, 2, 14), 4800.0, c1, dIT,  "QA",             "Full-time", "QA Specialist");
        addEmp(em, "Frank Brown",    "Male",   "IT Support",         LocalDate.of(2022,11,  1), 4200.0, c1, dIT,  "Support",        "Part-time", "IT Support");
        addEmp(em, "Grace Kim",      "Female", "Data Analyst",       LocalDate.of(2023, 5,  1), 5100.0, c1, dIT,  "Analyst",        "Full-time", "Data Analyst");

        // ── HR Department – 6 employees ────────────────────────────
        addEmp(em, "Henry Adams",    "Male",   "HR Manager",         LocalDate.of(2020, 7, 10), 5500.0, c1, dHR,  "HR Manager",     "Full-time", "HR Specialist");
        addEmp(em, "Isla Thompson",  "Female", "Recruiter",          LocalDate.of(2021, 4, 18), 4500.0, c1, dHR,  "Recruiter",      "Full-time", "Talent Acquisition");
        addEmp(em, "Jack Wilson",    "Male",   "Payroll Specialist", LocalDate.of(2022, 8, 22), 4700.0, c1, dHR,  "Payroll",        "Full-time", "Payroll Specialist");
        addEmp(em, "Karen Davis",    "Female", "HR Assistant",       LocalDate.of(2023, 1,  9), 3800.0, c1, dHR,  "HR Assistant",   "Part-time", "HR Assistant");
        addEmp(em, "Leo Garcia",     "Male",   "Training Coord.",    LocalDate.of(2021,12,  3), 4200.0, c1, dHR,  "Training",       "Full-time", "L&D Coordinator");
        addEmp(em, "Mia Robinson",   "Female", "HR Generalist",      LocalDate.of(2022, 3, 15), 4600.0, c1, dHR,  "HR Generalist",  "Full-time", "HR Generalist");

        // ── Finance Department – 5 employees ──────────────────────
        addEmp(em, "Noah Clark",     "Male",   "CFO",                LocalDate.of(2019, 5,  1), 9000.0, c1, dFin, "CFO",            "Full-time", "Chief Financial Officer");
        addEmp(em, "Olivia Hall",    "Female", "Accountant",         LocalDate.of(2021, 8, 12), 5200.0, c1, dFin, "Accountant",     "Full-time", "Senior Accountant");
        addEmp(em, "Peter Young",    "Male",   "Financial Analyst",  LocalDate.of(2022, 2, 28), 5600.0, c1, dFin, "Analyst",        "Full-time", "Financial Analyst");
        addEmp(em, "Quinn Lewis",    "Female", "Budget Planner",     LocalDate.of(2022, 9,  5), 4900.0, c1, dFin, "Budget",         "Full-time", "Budget Analyst");
        addEmp(em, "Rachel Scott",   "Female", "Accounts Payable",   LocalDate.of(2023, 4, 17), 4300.0, c1, dFin, "Accounts",       "Part-time", "AP Specialist");

        // ── Marketing Department – 6 employees ────────────────────
        addEmp(em, "Sam Turner",     "Male",   "Marketing Lead",     LocalDate.of(2020,11,  7), 6000.0, c1, dMkt, "Marketing Lead", "Full-time", "Marketing Manager");
        addEmp(em, "Tina Nelson",    "Female", "Content Writer",     LocalDate.of(2021, 6, 14), 4400.0, c1, dMkt, "Writer",         "Full-time", "Content Specialist");
        addEmp(em, "Uma Carter",     "Female", "SEO Specialist",     LocalDate.of(2022, 4, 20), 4800.0, c1, dMkt, "SEO",            "Full-time", "SEO Expert");
        addEmp(em, "Victor Hill",    "Male",   "Social Media Mgr",   LocalDate.of(2022, 7, 11), 4600.0, c1, dMkt, "Social Media",   "Full-time", "Social Media Manager");
        addEmp(em, "Wendy Green",    "Female", "Graphic Designer",   LocalDate.of(2023, 1, 25), 4200.0, c1, dMkt, "Design",         "Part-time", "Graphic Designer");
        addEmp(em, "Xavier Harris",  "Male",   "Campaign Analyst",   LocalDate.of(2023, 3,  8), 4500.0, c1, dMkt, "Analyst",        "Full-time", "Campaign Analyst");

        // ── Operations Department – 7 employees ───────────────────
        addEmp(em, "Yara King",      "Female", "Ops Director",       LocalDate.of(2018, 9,  1), 8500.0, c1, dOps, "Director",       "Full-time", "Operations Director");
        addEmp(em, "Zach Wright",    "Male",   "Logistics Coord.",   LocalDate.of(2020, 4, 15), 5000.0, c1, dOps, "Logistics",      "Full-time", "Logistics Coordinator");
        addEmp(em, "Amy Lopez",      "Female", "Supply Chain Mgr",   LocalDate.of(2021, 1, 20), 5400.0, c1, dOps, "Supply Chain",   "Full-time", "Supply Chain Manager");
        addEmp(em, "Brian Walker",   "Male",   "Facilities Mgr",     LocalDate.of(2021, 7,  9), 4800.0, c1, dOps, "Facilities",     "Full-time", "Facilities Manager");
        addEmp(em, "Clara Evans",    "Female", "Process Analyst",    LocalDate.of(2022, 5, 30), 4700.0, c1, dOps, "Analyst",        "Full-time", "Process Analyst");
        addEmp(em, "Dylan Baker",    "Male",   "Warehouse Sup.",     LocalDate.of(2022,10,  3), 4100.0, c1, dOps, "Warehouse",      "Part-time", "Warehouse Supervisor");
        addEmp(em, "Emma Perez",     "Female", "QC Inspector",       LocalDate.of(2023, 2,  7), 4300.0, c1, dOps, "QC",             "Full-time", "Quality Inspector");

        em.getTransaction().commit();

        // ── Leave records (separate transaction after employees exist) ─
        em.getTransaction().begin();
        addLeave(em, "Alice Johnson", LocalDate.of(2024, 6,  1), LocalDate.of(2024, 6, 10), "Vacation",   "Summer holiday");
        addLeave(em, "Bob Smith",     LocalDate.of(2024, 9,  5), LocalDate.of(2024, 9,  8), "Sick Leave", "Flu");
        addLeave(em, "Henry Adams",   LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 17), "Vacation",   "New Year break");
        addLeave(em, "Olivia Hall",   LocalDate.of(2025, 3,  1), LocalDate.of(2025, 3,  3), "Personal",   "Personal matters");
        addLeave(em, "Sam Turner",    LocalDate.of(2025, 7, 14), LocalDate.of(2025, 7, 21), "Vacation",   "Family trip");
        em.getTransaction().commit();

        em.close();
        emf.close();
        System.out.println("✅ Done! Departments: 5, Employees: 31, Leaves: 5");
    }

    // ── Helpers ────────────────────────────────────────────────────

    private static Company getOrCreateCompany(EntityManager em, String name, String address, String country) {
        List<Company> list = em.createQuery("SELECT c FROM Company c WHERE c.name = :n", Company.class)
                .setParameter("n", name).getResultList();
        if (!list.isEmpty()) return list.get(0);
        Company c = new Company(name, address, country);
        em.persist(c);
        return c;
    }

    private static Department getOrCreateDept(EntityManager em, String name, String desc, int cap,
                                               LocalDate created, Company company) {
        List<Department> list = em.createQuery(
                "SELECT d FROM Department d WHERE d.departmentName = :n", Department.class)
                .setParameter("n", name).getResultList();
        if (!list.isEmpty()) return list.get(0);
        Department d = new Department(cap, name, desc, created, company);
        em.persist(d);
        return d;
    }

    private static void addEmp(EntityManager em, String fullName, String gender, String desc,
                                LocalDate start, double salary, Company company, Department dept,
                                String role, String contract, String position) {
        List<Employee> existing = em.createQuery(
                "SELECT e FROM Employee e WHERE e.fullName = :n", Employee.class)
                .setParameter("n", fullName).getResultList();
        if (!existing.isEmpty()) return; // skip if already exists
        em.persist(new Employee(fullName, gender, desc, start, salary, company, dept, role, contract, position));
    }

    private static void addLeave(EntityManager em, String empName,
                                  LocalDate start, LocalDate end, String type, String reason) {
        Employee emp = em.createQuery("SELECT e FROM Employee e WHERE e.fullName = :n", Employee.class)
                .setParameter("n", empName).getResultStream().findFirst().orElse(null);
        if (emp == null) return;
        boolean exists = !em.createQuery(
                "SELECT l FROM Leave l WHERE l.employee = :e AND l.startDate = :s", Leave.class)
                .setParameter("e", emp).setParameter("s", start).getResultList().isEmpty();
        if (!exists) em.persist(new Leave(start, end, type, reason, emp));
    }
}
