package entity;

import javafx.application.Platform;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LanguageManagerTest {

    private LanguageManager manager;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @BeforeEach
    void setUp() {
        manager = LanguageManager.getInstance();
        manager.setLanguage("English");
    }

    @Test
    @DisplayName("getInstance returns the same singleton")
    void testGetInstance() {
        assertSame(manager, LanguageManager.getInstance());
    }

    @Test
    @DisplayName("getLanguage returns current language name")
    void testGetLanguage() {
        assertEquals("English", manager.getLanguage());
    }

    @Test
    @DisplayName("getCurrentBundle returns non-null bundle after initialization")
    void testGetCurrentBundle() {
        assertNotNull(manager.getCurrentBundle());
    }

    @Test
    @DisplayName("currentLanguageProperty reflects the current language")
    void testCurrentLanguageProperty() {
        assertNotNull(manager.currentLanguageProperty());
        assertEquals("English", manager.currentLanguageProperty().get());
    }

    @Test
    @DisplayName("setLanguage to Spanish loads Spanish bundle")
    void testSetLanguageSpanish() {
        manager.setLanguage("Spanish");
        assertEquals("Spanish", manager.getLanguage());
        assertNotNull(manager.getCurrentBundle());
    }

    @Test
    @DisplayName("setLanguage to Persia loads Persian bundle")
    void testSetLanguagePersia() {
        manager.setLanguage("Persia");
        assertEquals("Persia", manager.getLanguage());
        assertNotNull(manager.getCurrentBundle());
    }

    @Test
    @DisplayName("setLanguage to Arabic loads Arabic bundle")
    void testSetLanguageArabic() {
        manager.setLanguage("Arabic");
        assertEquals("Arabic", manager.getLanguage());
        assertNotNull(manager.getCurrentBundle());
    }

    @Test
    @DisplayName("getTranslatedText returns value for a known key")
    void testGetTranslatedTextKnownKey() {
        manager.setLanguage("English");
        String result = manager.getTranslatedText("nav.dashboard");
        assertNotNull(result);
        assertFalse(result.startsWith("!"));
    }

    @Test
    @DisplayName("getTranslatedText returns !key! for unknown key")
    void testGetTranslatedTextUnknownKey() {
        String result = manager.getTranslatedText("nonexistent.key.xyz");
        assertEquals("!nonexistent.key.xyz!", result);
    }

    @Test
    @DisplayName("getTranslatedTexts returns translation for es code")
    void testGetTranslatedTextsEs() {
        String result = manager.getTranslatedTexts("nav.dashboard", "es");
        assertNotNull(result);
    }

    @Test
    @DisplayName("getTranslatedTexts returns translation for fa code")
    void testGetTranslatedTextsFa() {
        String result = manager.getTranslatedTexts("nav.dashboard", "fa");
        assertNotNull(result);
    }

    @Test
    @DisplayName("getTranslatedTexts returns translation for ar code")
    void testGetTranslatedTextsAr() {
        String result = manager.getTranslatedTexts("nav.dashboard", "ar");
        assertNotNull(result);
    }

    @Test
    @DisplayName("getTranslatedTexts returns English translation for unknown code")
    void testGetTranslatedTextsDefault() {
        String result = manager.getTranslatedTexts("nav.dashboard", "xx");
        assertNotNull(result);
    }

    @Test
    @DisplayName("getKey returns the key matching a known translated value")
    void testGetKey() {
        manager.setLanguage("English");
        String knownValue = manager.getTranslatedText("nav.dashboard");
        String foundKey = manager.getKey(knownValue);
        assertEquals("nav.dashboard", foundKey);
    }

    @Test
    @DisplayName("getKey returns null when value does not exist in bundle")
    void testGetKeyNotFound() {
        String result = manager.getKey("!!this_value_does_not_exist_in_bundle!!");
        assertNull(result);
    }

    @Test
    @DisplayName("getAbreviation returns correct codes for each language")
    void testGetAbreviation() {
        assertEquals("es", manager.getAbreviation("Spanish"));
        assertEquals("fa", manager.getAbreviation("Persia"));
        assertEquals("ar", manager.getAbreviation("Arabic"));
        assertEquals("en", manager.getAbreviation("English"));
        assertEquals("en", manager.getAbreviation("Unknown"));
    }

    @Test
    @DisplayName("getCurrentLanguageCode returns correct code for each language")
    void testGetCurrentLanguageCode() {
        manager.setLanguage("English");
        assertEquals("en", manager.getCurrentLanguageCode());

        manager.setLanguage("Spanish");
        assertEquals("es", manager.getCurrentLanguageCode());

        manager.setLanguage("Persia");
        assertEquals("fa", manager.getCurrentLanguageCode());

        manager.setLanguage("Arabic");
        assertEquals("ar", manager.getCurrentLanguageCode());
    }
}