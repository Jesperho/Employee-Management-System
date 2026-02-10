package dao;

import datasource.MariaDbConnection;
import entity.Position;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PositionDaoImpl implements PositionDao {

    @Override
    public void create(Position position) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(position);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Position find(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.find(Position.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Position position) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(position);
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
            Position p = em.find(Position.class, id);
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Position> findAll() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            TypedQuery<Position> q =
                    em.createQuery("SELECT p FROM Position p", Position.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}