package dao;

import datasource.MariaDbConnection;
import entity.Department;
import jakarta.persistence.EntityManager;

import java.util.List;

public class DepartmentDao extends BaseDao<Department> {

    public DepartmentDao() {
        super(Department.class);
    }

    // Find all departments belonging to a specific company
    public List<Department> findByCompany(int companyId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM Department d WHERE d.company.id = :companyId",
                            Department.class
                    )
                    .setParameter("companyId", companyId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Count employees assigned to a department
    public long countEmployees(int departmentId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(e) FROM Employee e WHERE e.department.id = :deptId",
                            Long.class
                    )
                    .setParameter("deptId", departmentId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    // Delete department only if no employees assigned
    public boolean safeDelete(int departmentId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            long count = em.createQuery(
                            "SELECT COUNT(e) FROM Employee e WHERE e.department.id = :deptId",
                            Long.class
                    )
                    .setParameter("deptId", departmentId)
                    .getSingleResult();

            if (count > 0) {
                return false; // Cannot delete
            }

            em.getTransaction().begin();
            Department dept = em.find(Department.class, departmentId);
            if (dept != null) {
                em.remove(dept);
            }
            em.getTransaction().commit();
            return true;

        } finally {
            em.close();
        }
    }
}