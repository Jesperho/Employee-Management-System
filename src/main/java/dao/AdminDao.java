package dao;

import datasource.MariaDbConnection;
import entity.Admin;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AdminDao extends BaseDao<Admin> {

    public AdminDao() {
        super(Admin.class);
    }

    // LOGIN: Find admin by email + password
    public Admin findByEmailAndPassword(String email, String password) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Admin a WHERE a.email = :email AND a.password = :password",
                            Admin.class
                    )
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    // READ: Find admin by email (for validation)
    public Admin findByEmail(String email) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Admin a WHERE a.email = :email",
                            Admin.class
                    )
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    // VALIDATION: Check if email already exists
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    // READ: Find all admins belonging to a company
    public List<Admin> findByCompany(int companyId) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM Admin a WHERE a.company.id = :companyId",
                            Admin.class
                    )
                    .setParameter("companyId", companyId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}