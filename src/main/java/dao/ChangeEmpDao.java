package dao;

import datasource.MariaDbConnection;
import entity.ChangeEmp;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ChangeEmpDao extends BaseDao<ChangeEmp> {

    public ChangeEmpDao() {
        super(ChangeEmp.class);
    }

    // READ: Find all change logs for a specific employee
    public List<ChangeEmp> findByEmployee(int employeeId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeEmp c WHERE c.employee.id = :empId ORDER BY c.date DESC",
                            ChangeEmp.class
                    )
                    .setParameter("empId", employeeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Find all change logs created by a specific admin
    public List<ChangeEmp> findByAdmin(int adminId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeEmp c WHERE c.admin.id = :adminId ORDER BY c.date DESC",
                            ChangeEmp.class
                    )
                    .setParameter("adminId", adminId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Get the most recent change for an employee
    public ChangeEmp findLatestForEmployee(int employeeId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeEmp c WHERE c.employee.id = :empId ORDER BY c.date DESC",
                            ChangeEmp.class
                    )
                    .setParameter("empId", employeeId)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    // READ: Sort all changes by date (ascending or descending)
    public List<ChangeEmp> sortByDate(boolean ascending) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            if (ascending) {
                return em.createQuery(
                        "SELECT c FROM ChangeEmp c ORDER BY c.date ASC",
                        ChangeEmp.class
                ).getResultList();
            } else {
                return em.createQuery(
                        "SELECT c FROM ChangeEmp c ORDER BY c.date DESC",
                        ChangeEmp.class
                ).getResultList();
            }
        } finally {
            em.close();
        }
    }
}