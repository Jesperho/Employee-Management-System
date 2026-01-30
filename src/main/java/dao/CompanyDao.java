package dao;

import datasource.MariaDbConnection;
import entity.Company;
import jakarta.persistence.EntityManager;

public class CompanyDao {



    public void persist(Company comp){
        EntityManager em = MariaDbConnection.getInstance();

        em.getTransaction().begin();

        em.persist(comp);

        em.getTransaction().commit();

    }


    public static void main(String[] args){

        CompanyDao cd = new CompanyDao();

        cd.persist(new Company("Metropolia","Myllypuurontie 12", "Suomi"));
    }

}
