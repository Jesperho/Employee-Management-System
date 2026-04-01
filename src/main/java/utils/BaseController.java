package utils;

import entity.LanguageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.geometry.NodeOrientation;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {


    @FXML
    protected ComboBox<String> languageComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (languageComboBox != null) {
            LanguageManager manager = LanguageManager.getInstance();


            languageComboBox.setValue(manager.getLanguage());

            languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(manager.getLanguage())) {
                    manager.setLanguage(newValue);
                }
            });


            manager.currentLanguageProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(languageComboBox.getValue())) {
                    languageComboBox.setValue(newValue);
                }
                updateUIText(newValue);
                applyLanguageDirection(newValue);
            });
        }

        setupController();
        updateUIText(LanguageManager.getInstance().getLanguage());
        applyLanguageDirection(LanguageManager.getInstance().getLanguage());
    }

    protected abstract void updateUIText(String newLanguage);
    protected abstract void setupController();

    protected void applyLanguageDirection(String language) {
        boolean isRTL = language != null && (
                language.equalsIgnoreCase("Arabic") ||
                language.equalsIgnoreCase("Persia") ||
                language.equalsIgnoreCase("Persian")
        );

        Platform.runLater(() -> {
            if (languageComboBox != null && languageComboBox.getScene() != null
                    && languageComboBox.getScene().getRoot() != null) {
                languageComboBox.getScene().getRoot().setNodeOrientation(
                        isRTL ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT
                );
            }
        });
    }
}