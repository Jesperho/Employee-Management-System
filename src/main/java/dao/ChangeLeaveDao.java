package dao;

import datasource.MariaDbConnection;
import entity.ChangeLeave;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ChangeLeaveDao extends BaseDao<ChangeLeave> {

    public ChangeLeaveDao() {
        super(ChangeLeave.class);
    }

    // READ: Find all change logs for a specific leave
    public List<ChangeLeave> findByLeave(int leaveId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeLeave c WHERE c.leave.id = :leaveId ORDER BY c.date DESC",
                            ChangeLeave.class
                    )
                    .setParameter("leaveId", leaveId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Find all change logs created by a specific admin
    public List<ChangeLeave> findByAdmin(int adminId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeLeave c WHERE c.admin.id = :adminId ORDER BY c.date DESC",
                            ChangeLeave.class
                    )
                    .setParameter("adminId", adminId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Get the most recent change for a leave
    public ChangeLeave findLatestForLeave(int leaveId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM ChangeLeave c WHERE c.leave.id = :leaveId ORDER BY c.date DESC",
                            ChangeLeave.class
                    )
                    .setParameter("leaveId", leaveId)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    // READ: Sort all change logs by date
    public List<ChangeLeave> sortByDate(boolean ascending) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            if (ascending) {
                return em.createQuery(
                        "SELECT c FROM ChangeLeave c ORDER BY c.date ASC",
                        ChangeLeave.class
                ).getResultList();
            } else {
                return em.createQuery(
                        "SELECT c FROM ChangeLeave c ORDER BY c.date DESC",
                        ChangeLeave.class
                ).getResultList();
            }
        } finally {
            em.close();
        }
    }
}
