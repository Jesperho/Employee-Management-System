package view;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Shared factory for common form field and label components used across all view builders.
 */
public final class FormBuilder {

    private static final String FIELD_STYLE =
            "-fx-background-color: #f8f9fa; -fx-border-color: #e0e3e8; " +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 12;";

    private static final String LABEL_STYLE =
            "-fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-font-weight: bold;";

    private FormBuilder() {}

    /** Creates a styled text field with default width (200px). */
    public static TextField createFormField(String prompt) {
        return createFormField(prompt, 200);
    }

    /** Creates a styled text field with a custom preferred width. */
    public static TextField createFormField(String prompt, double width) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(width);
        tf.setStyle(FIELD_STYLE);
        return tf;
    }

    /** Creates a bold form label. */
    public static Label createLabel(String text) {
        Label l = new Label(text);
        l.setStyle(LABEL_STYLE);
        return l;
    }
}
