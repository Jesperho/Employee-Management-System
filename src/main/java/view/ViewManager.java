package view;

import entity.LanguageManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {

    private static Stage primaryStage;

    /**
     * Sets the primary stage for the application.
     * Should be called once during application initialization.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Loads and displays the Dashboard view.
     */
    public static void showDashboard() {
        loadView("/fxml/dashboard.fxml", "/css/dashboard.css", "Employee Management System - Dashboard", 1280, 800);
    }

    /**
     * Loads and displays the Login view.
     */
    public static void showLogin() {
        loadView("/fxml/login.fxml", "/css/login.css", "Employee Management System - Login", 1050, 700);
    }

    public static void showEmployees() {
        loadView("/fxml/employee.fxml", "/css/management.css", "Employee Management System - Employees", 1280, 800);
    }

    public static void showDepartments() {
        loadView("/fxml/department.fxml", "/css/management.css", "Employee Management System - Departments", 1280, 800);
    }

    public static void showLeaveManagement() {
        loadView("/fxml/leave.fxml", "/css/management.css", "Employee Management System - Leave Management", 1280, 800);
    }

    public static void showSalary() {
        loadView("/fxml/salary.fxml", "/css/management.css", "Employee Management System - Salary", 1280, 800);
    }

    private static void loadView(String fxmlPath, String cssPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            loader.setResources(LanguageManager.getInstance().getCurrentBundle());

            Scene scene = new Scene(root, width, height);

            var cssResource = ViewManager.class.getResource(cssPath);
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.setMinWidth(950);
            primaryStage.setMinHeight(650);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Loads a view and returns its controller.
     * Useful when you need to pass data to the controller.
     *
     * @param fxmlPath Path to the FXML file
     * @return The controller associated with the view, or null if loading fails
     */
    public static <T> T loadViewWithController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(fxmlPath));
            loader.load();
            loader.setResources(LanguageManager.getInstance().getCurrentBundle());
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Error loading view with controller: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
}
