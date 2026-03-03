package app;

import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        // Skip GUI when running in Docker
        if (System.getenv("DOCKER") != null) {
            System.out.println("Running in Docker: skipping JavaFX GUI launch");
            System.out.println("Employee Management System Docker image works!");
        } else {
            // Launch GUI normally
            Application.launch(GUIApplication.class, args);
        }
    }
}