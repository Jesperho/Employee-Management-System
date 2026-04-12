package dao;

import datasource.MariaDbConnection;
import entity.Employee;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EmployeeDao extends BaseDao<Employee> {

    public EmployeeDao() {
        super(Employee.class);
    }

    // READ: Find all employees in a specific department
    public List<Employee> findByDepartment(int departmentId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.department.id = :deptId",
                            Employee.class
                    )
                    .setParameter("deptId", departmentId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Employee> findAllWithTranslations() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT e " +
                            "FROM Employee e " +
                            "LEFT JOIN FETCH e.department d " +
                            "LEFT JOIN FETCH d.translations " +
                            "LEFT JOIN FETCH e.translations",
                    Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }


    // READ: Find all employees in a specific company
    public List<Employee> findByCompany(int companyId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.company.id = :companyId",
                            Employee.class
                    )
                    .setParameter("companyId", companyId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Search employees by name (for search bar)
    public List<Employee> searchByName(String name) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE LOWER(e.fullName) LIKE LOWER(:name)",
                            Employee.class
                    )
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Filter by role
    public List<Employee> findByRole(String role) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.role = :role",
                            Employee.class
                    )
                    .setParameter("role", role)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Filter by contract type (Full-time, Remote, etc.)
    public List<Employee> findByContractType(String contractType) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.contractType = :contractType",
                            Employee.class
                    )
                    .setParameter("contractType", contractType)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Sort employees by salary
    public List<Employee> sortBySalary(boolean ascending) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            String order = ascending ? "ASC" : "DESC";
            return em.createQuery(
                    "SELECT e FROM Employee e ORDER BY e.salary " + order,
                    Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Sort employees by hire/start date
    public List<Employee> sortByStartDate(boolean ascending) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            String order = ascending ? "ASC" : "DESC";
            return em.createQuery(
                    "SELECT e FROM Employee e ORDER BY e.startDate " + order,
                    Employee.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Count all employees (for dashboard)
    public long countEmployees() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(e) FROM Employee e",
                    Long.class
            ).getSingleResult();
        } finally {
            em.close();
        }
    }
}