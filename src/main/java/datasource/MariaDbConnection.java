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
            port = "3306"; // default MariaDB port locally
        }

        // Determine if we are running in a test environment
        boolean isTest = false;
        for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
            if (el.getClassName().contains("junit")) {
                isTest = true;
                break;
            }
        }
        
        String dbName = isTest ? "employment_management_test" : "employment_management";
        String url = "jdbc:mariadb://" + host + ":" + port + "/" + dbName + "?createDatabaseIfNotExist=true";

        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", url);

        // Optional: read user/password from env as well
        String user = System.getenv().getOrDefault("DB_USER", "appuser");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "password");
        props.put("jakarta.persistence.jdbc.user", user);
        props.put("jakarta.persistence.jdbc.password", password);

        emf = Persistence.createEntityManagerFactory("CompanyMariaDbUnit", props);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}