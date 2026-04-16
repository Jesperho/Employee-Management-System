package entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static final LanguageManager instance = new LanguageManager();
    private final StringProperty currentLanguage = new SimpleStringProperty("English");
    private ResourceBundle currentBundle;

    private LanguageManager() {
        updateResourceBundle("English");
    }

    public static LanguageManager getInstance() {
        return instance;
    }

    public StringProperty currentLanguageProperty() {
        return currentLanguage;
    }

    public String getLanguage() {
        return currentLanguage.get();
    }

    public ResourceBundle getCurrentBundle() {
        return currentBundle;
    }

    public void setLanguage(String language) {
        updateResourceBundle(language);
        this.currentLanguage.set(language);
    }

    public String getTranslatedText(String key) {
        if (currentBundle != null && currentBundle.containsKey(key)) {
            return currentBundle.getString(key);
        }
        return "!" + key + "!";
    }

    /** Retrieves a translated string from the bundle for an explicit language code (e.g. "ar", "en"). */
    public String getTranslatedTexts(String key, String code) {
        Locale locale = toLocale(code);
        ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return "!" + key + "!";
    }

    /**
     * Reverse-lookup: returns the bundle key whose value matches the given text.
     * Linear scan — only for use on small, known-size bundles such as department name translation maps.
     */
    public String getKey(String text) {
        for (String key : currentBundle.keySet()) {
            if (currentBundle.getString(key).equals(text)) {
                return key;
            }
        }
        return null;
    }

    public String getAbreviation(String language) {
        return toLanguageCode(language);
    }

    public String getCurrentLanguageCode() {
        return toLanguageCode(currentLanguage.get());
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Maps a display language name to its ISO-639 code. */
    private String toLanguageCode(String language) {
        return switch (language.toLowerCase()) {
            case "spanish" -> "es";
            case "persia"  -> "fa";
            case "arabic"  -> "ar";
            default        -> "en";
        };
    }

    private Locale toLocale(String code) {
        return switch (code.toLowerCase()) {
            case "es" -> new Locale("es", "ES");
            case "fa" -> new Locale("fa", "AF");
            case "ar" -> new Locale("ar", "AR");
            default   -> new Locale("en", "US");
        };
    }

    private void updateResourceBundle(String languageName) {
        Locale locale = switch (languageName.toLowerCase()) {
            case "spanish" -> new Locale("es", "ES");
            case "persia"  -> new Locale("fa", "AF");
            case "arabic"  -> new Locale("ar", "AR");
            default        -> new Locale("en", "US");
        };
        currentBundle = ResourceBundle.getBundle("MessagesBundle", locale);
    }
}