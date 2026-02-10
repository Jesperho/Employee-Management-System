package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting Employee Management System...");

        // Start with login screen (change to dashboard if needed)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1050, 700);
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

        primaryStage.setTitle("Employee Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(650);
        primaryStage.show();

        System.out.println("Application started successfully!");
        System.out.println("Login with any email/password to see the dashboard.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
