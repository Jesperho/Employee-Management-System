package dao;

import datasource.MariaDbConnection;
import entity.ChangeDept;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ChangeDeptDao extends BaseDao<ChangeDept> {

    public ChangeDeptDao() {
        super(ChangeDept.class);
    }

    // READ: Find all change logs for a specific department
    public List<ChangeDept> findByDepartment(int departmentId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeDept c WHERE c.department.id = :deptId ORDER BY c.date DESC",
                            ChangeDept.class
                    )
                    .setParameter("deptId", departmentId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Find all change logs created by a specific admin
    public List<ChangeDept> findByAdmin(int adminId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeDept c WHERE c.admin.id = :adminId ORDER BY c.date DESC",
                            ChangeDept.class
                    )
                    .setParameter("adminId", adminId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Get the most recent change for a department
    public ChangeDept findLatestForDepartment(int departmentId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeDept c WHERE c.department.id = :deptId ORDER BY c.date DESC",
                            ChangeDept.class
                    )
                    .setParameter("deptId", departmentId)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    // Nullifies the department FK in all change logs so the department can be deleted
    public void nullifyDepartment(int departmentId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("UPDATE ChangeDept c SET c.department = null WHERE c.department.id = :deptId")
              .setParameter("deptId", departmentId)
              .executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // READ: Sort all department changes by date
    public List<ChangeDept> sortByDate(boolean ascending) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            if (ascending) {
                return em.createQuery(
                        "SELECT c FROM ChangeDept c ORDER BY c.date ASC",
                        ChangeDept.class
                ).getResultList();
            } else {
                return em.createQuery(
                        "SELECT c FROM ChangeDept c ORDER BY c.date DESC",
                        ChangeDept.class
                ).getResultList();
            }
        } finally {
            em.close();
        }
    }
}