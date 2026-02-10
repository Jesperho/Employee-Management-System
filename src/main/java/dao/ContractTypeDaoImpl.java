package dao;

import datasource.MariaDbConnection;
import entity.ContractType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ContractTypeDaoImpl implements ContractTypeDao {

    @Override
    public void create(ContractType contractType) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(contractType);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public ContractType find(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.find(ContractType.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(ContractType contractType) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(contractType);
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
            ContractType ct = em.find(ContractType.class, id);
            if (ct != null) {
                em.remove(ct);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ContractType> findAll() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            TypedQuery<ContractType> q =
                    em.createQuery("SELECT c FROM ContractType c", ContractType.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}