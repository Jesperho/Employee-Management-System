package datasource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MariaDbConnection {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("CompanyMariaDbUnit");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
