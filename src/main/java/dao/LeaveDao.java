package dao;

import datasource.MariaDbConnection;
import entity.Leave;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class LeaveDao extends BaseDao<Leave> {

    public LeaveDao() {
        super(Leave.class);
    }

    // READ: Find all leaves for a specific employee
    public List<Leave> findByEmployee(int employeeId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT l FROM Leave l WHERE l.employee.id = :empId",
                            Leave.class
                    )
                    .setParameter("empId", employeeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Filter leaves by type (Vacation, Sick Leave, etc.)
    public List<Leave> findByType(String type) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT l FROM Leave l WHERE l.type = :type",
                            Leave.class
                    )
                    .setParameter("type", type)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Find leaves within a date range
    public List<Leave> findByDateRange(LocalDate start, LocalDate end) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT l FROM Leave l WHERE l.startDate >= :start AND l.endDate <= :end",
                            Leave.class
                    )
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Sort leaves by start date
    public List<Leave> sortByStartDate(boolean ascending) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            String order = ascending ? "ASC" : "DESC";
            return em.createQuery(
                    "SELECT l FROM Leave l ORDER BY l.startDate " + order,
                    Leave.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // READ: Sort leaves by end date
    public List<Leave> sortByEndDate(boolean ascending) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            String order = ascending ? "ASC" : "DESC";
            return em.createQuery(
                    "SELECT l FROM Leave l ORDER BY l.endDate " + order,
                    Leave.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // VALIDATION: Check if employee has overlapping leave
    public boolean hasOverlappingLeave(int employeeId, LocalDate start, LocalDate end) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            long count = em.createQuery(
                            "SELECT COUNT(l) FROM Leave l " +
                                    "WHERE l.employee.id = :empId " +
                                    "AND l.startDate <= :end " +
                                    "AND l.endDate >= :start",
                            Long.class
                    )
                    .setParameter("empId", employeeId)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    // READ: Count leaves for dashboards
    public long countLeaves() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(l) FROM Leave l",
                    Long.class
            ).getSingleResult();
        } finally {
            em.close();
        }
    }
}