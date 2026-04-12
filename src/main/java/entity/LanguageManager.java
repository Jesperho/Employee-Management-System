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

    public String getTranslatedTexts(String key, String code){
        Locale locale;

        switch (code.toLowerCase()) {
            case "es":
                locale = new Locale("es", "ES");
                break;
            case "fa":
                locale = new Locale("fa", "AF");
                break;
            case "ar":
                locale = new Locale("ar", "AR");
                break;
            default:
                locale = new Locale("en", "US");
                break;
        }

        ResourceBundle temporalResoruce = ResourceBundle.getBundle("MessagesBundle", locale);

        return temporalResoruce.getString(key);
    }

    public String getKey(String text){
        for (String key : currentBundle.keySet()) {
            if (currentBundle.getString(key).equals(text)) {
                return key;
            }
        }
        return null;
    }


    private void updateResourceBundle(String languageName) {
        Locale locale;

        switch (languageName.toLowerCase()) {
            case "spanish":
                locale = new Locale("es", "ES");
                break;
            case "persia":
                locale = new Locale("fa", "AF");
                break;
            case "arabic":
                locale = new Locale("ar", "AR");
                break;
            default:
                locale = new Locale("en", "US");
                break;
        }
        currentBundle = ResourceBundle.getBundle("MessagesBundle", locale);
        System.out.println("Local bundle set to" + languageName);
    }

    public String getAbreviation(String language){
        switch (language.toLowerCase()){
            case "spanish":
                return "es";
            case "persia":
                return "fa";
            case "arabic":
                return "ar";
            default:
                return "en";
        }
    }

    public String getCurrentLanguageCode(){
        String currentlang = currentLanguage.get().toLowerCase();;
        switch (currentlang){
            case "spanish":
                return "es";
            case "persia":
                return "fa";
            case "arabic":
                return "ar";
            default:
                return "en";
        }
    }


}