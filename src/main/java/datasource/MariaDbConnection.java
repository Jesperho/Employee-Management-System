package datasource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class MariaDbConnection {

    private static final EntityManagerFactory emf;

    static {
        // Detect Docker vs local
        // If DB_HOST is set, we use it (for Docker)
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");

        if (host == null || host.isEmpty()) {
            // Local machine fallback
            host = "localhost";
            port = "3307"; // default MariaDB port locally
        }

        String url = "jdbc:mariadb://" + host + ":" + port + "/employment_management";

        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", url);

        // Optional: read user/password from env as well
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "password");
        props.put("jakarta.persistence.jdbc.user", user);
        props.put("jakarta.persistence.jdbc.password", password);

        emf = Persistence.createEntityManagerFactory("CompanyMariaDbUnit", props);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}