package app;

import entity.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUIApplication extends Application {

    // ── DEV ONLY: set to true to skip the login screen ───────────────────────
    private static final boolean DEV_BYPASS = true;
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting Employee Management System...");

        String fxml, css, title;
        if (DEV_BYPASS) {
            // Pre-populate session so sidebar profile doesn't show nulls
            UserSession.getInstance().setUser("Dev Admin", "Administrator");
            fxml  = "/fxml/dashboard.fxml";
            css   = "/css/dashboard.css";
            title = "Employee Management System - Dashboard [DEV]";
        } else {
            fxml  = "/fxml/login.fxml";
            css   = "/css/login.css";
            title = "Employee Management System - Login";
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setResources(LanguageManager.getInstance().getCurrentBundle());
        Parent root = loader.load();

        Scene scene = new Scene(root, 1050, 700);
        scene.getStylesheets().add(getClass().getResource(css).toExternalForm());

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(650);
        primaryStage.show();

        System.out.println("Application started successfully!");
    }

}