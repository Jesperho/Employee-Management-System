package dao;

import datasource.MariaDbConnection;
import entity.Manager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ManagerDaoImpl implements ManagerDao {

    @Override
    public void create(Manager manager) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(manager);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Manager find(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.find(Manager.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Manager manager) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(manager);
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
            Manager m = em.find(Manager.class, id);
            if (m != null) {
                em.remove(m);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Manager> findAll() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            TypedQuery<Manager> q =
                    em.createQuery("SELECT m FROM Manager m", Manager.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}