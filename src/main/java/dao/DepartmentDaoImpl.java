package dao;

import datasource.MariaDbConnection;
import entity.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class DepartmentDaoImpl implements DepartmentDao {

    @Override
    public void create(Department department) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(department);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Department find(int id) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Department department) {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(department);
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
            Department d = em.find(Department.class, id);
            if (d != null) {
                em.remove(d);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Department> findAll() {
        EntityManager em = MariaDbConnection.getEntityManager();
        try {
            TypedQuery<Department> q =
                    em.createQuery("SELECT d FROM Department d", Department.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }
}