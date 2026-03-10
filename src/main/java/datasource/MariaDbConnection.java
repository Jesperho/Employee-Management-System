package datasource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class MariaDbConnection {

    private static final EntityManagerFactory emf;

    static {

        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "3307");

        String url = "jdbc:mariadb://" + host + ":" + port + "/employment_management";

        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", url);

        emf = Persistence.createEntityManagerFactory("CompanyMariaDbUnit", props);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}