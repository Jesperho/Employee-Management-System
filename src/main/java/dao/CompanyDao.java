package dao;

import datasource.MariaDbConnection;
import entity.Company;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class CompanyDao {

    public void create(Company company) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(company);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Company find(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.find(Company.class, id);
        } finally {
            em.close();
        }
    }

    public void update(Company company) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(company);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            Company c = em.find(Company.class, id);
            if (c != null) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Company> findAll() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            TypedQuery<Company> q =
                    em.createQuery("SELECT c FROM Company c", Company.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}