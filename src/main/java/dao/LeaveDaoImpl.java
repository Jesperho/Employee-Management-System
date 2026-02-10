package dao;

import datasource.MariaDbConnection;
import entity.Leave;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class LeaveDaoImpl implements LeaveDao {

    @Override
    public void create(Leave leave) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(leave);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Leave find(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.find(Leave.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Leave leave) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(leave);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            Leave l = em.find(Leave.class, id);
            if (l != null) {
                em.remove(l);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Leave> findAll() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            TypedQuery<Leave> q =
                    em.createQuery("SELECT l FROM Leave l", Leave.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}