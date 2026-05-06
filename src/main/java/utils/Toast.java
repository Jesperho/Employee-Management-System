package utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class Toast {

    public static void showSuccess(String message, Window owner) {
        showToast(message, Color.web("#4caf50"), owner);
    }

    public static void showError(String message, Window owner) {
        showToast(message, Color.web("#f44336"), owner);
    }

    private static void showToast(String message, Color bgColor, Window owner) {
        javafx.application.Platform.runLater(() -> {
            Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            Label label = new Label(message);
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            label.setPadding(new Insets(10, 20, 10, 20));

            StackPane pane = new StackPane(label);
            pane.setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(10), Insets.EMPTY)));
            pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
            
            popup.getContent().add(pane);

            Window window = owner;
            if (window == null) {
                java.util.List<Window> windows = Stage.getWindows();
                for (Window w : windows) {
                    if (w.isShowing() && w instanceof Stage) {
                        Stage s = (Stage) w;
                        if (s.getTitle() != null && s.getTitle().contains("Employee Management System")) {
                            window = s;
                            break;
                        }
                    }
                }
                if (window == null) {
                    for (Window w : windows) {
                        if (w.isShowing() && w.getWidth() > 300 && w.getHeight() > 300) {
                            window = w;
                            break;
                        }
                    }
                }
                if (window == null && !windows.isEmpty()) {
                    window = windows.get(0);
                }
            }
            
            if (window != null) {
                popup.show(window);
                // force layout to get bounds
                pane.applyCss();
                pane.layout();
                double width = pane.prefWidth(-1);
                popup.setX(window.getX() + window.getWidth() / 2 - width / 2);
                popup.setY(window.getY() + window.getHeight() - 80);
            }

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> popup.hide()));
            timeline.play();
        });
    }
}
