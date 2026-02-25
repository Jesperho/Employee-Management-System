package dao;

import datasource.MariaDbConnection;
import entity.Admin;
import jakarta.persistence.EntityManager;

public class AdminDao extends BaseDao<Admin> {
    public AdminDao() {
        super(Admin.class);
    }

    public Admin findByEmail(String email) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Admin a WHERE a.email = :email", Admin.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}
