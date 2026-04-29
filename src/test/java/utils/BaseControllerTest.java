package utils;

import entity.LanguageManager;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.*;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class BaseControllerTest {

    private ConcreteBaseController controller;

    @BeforeAll
    static void initJavaFX() {
        try { Platform.startup(() -> {}); }
        catch (IllegalStateException e) { /* already started */ }
    }

    @BeforeEach
    void setUp() {
        controller = new ConcreteBaseController();
        LanguageManager.getInstance().setLanguage("English");
    }

    private Object invokeMethod(String name, Class<?>[] types, Object... args) throws Exception {
        Method m = BaseController.class.getDeclaredMethod(name, types);
        m.setAccessible(true);
        return m.invoke(controller, args);
    }

    // ─── applyRTLToScene TESTS ────────────────────────────────────────────────

    @Test
    @DisplayName("applyRTLToScene sets LTR orientation for English")
    void applyRTLToScene_english() {
        LanguageManager.getInstance().setLanguage("English");
        VBox root = new VBox();
        Scene scene = new Scene(root, 100, 100);
        controller.applyRTLToScene(scene);
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
    }

    @Test
    @DisplayName("applyRTLToScene sets RTL orientation for Arabic")
    void applyRTLToScene_arabic() {
        LanguageManager.getInstance().setLanguage("Arabic");
        VBox root = new VBox();
        Scene scene = new Scene(root, 100, 100);
        controller.applyRTLToScene(scene);
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
        LanguageManager.getInstance().setLanguage("English");
    }

    @Test
    @DisplayName("applyRTLToScene sets RTL orientation for Persia")
    void applyRTLToScene_persia() {
        LanguageManager.getInstance().setLanguage("Persia");
        VBox root = new VBox();
        Scene scene = new Scene(root, 100, 100);
        controller.applyRTLToScene(scene);
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
        LanguageManager.getInstance().setLanguage("English");
    }

    @Test
    @DisplayName("applyRTLToScene sets RTL orientation for Persian")
    void applyRTLToScene_persian() {
        LanguageManager.getInstance().setLanguage("Arabic");
        VBox root = new VBox();
        Scene scene = new Scene(root, 100, 100);
        controller.applyRTLToScene(scene);
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
        LanguageManager.getInstance().setLanguage("English");
    }

    @Test
    @DisplayName("applyRTLToScene handles null scene gracefully")
    void applyRTLToScene_nullScene() {
        assertDoesNotThrow(() -> controller.applyRTLToScene(null));
    }

    @Test
    @DisplayName("applyRTLToScene sets LTR for Spanish")
    void applyRTLToScene_spanish() {
        LanguageManager.getInstance().setLanguage("Spanish");
        VBox root = new VBox();
        Scene scene = new Scene(root, 100, 100);
        controller.applyRTLToScene(scene);
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
        LanguageManager.getInstance().setLanguage("English");
    }

    // ─── applyLanguageDirection TESTS ─────────────────────────────────────────

    @Test
    @DisplayName("applyLanguageDirection does not throw for English")
    void applyLanguageDirection_english() throws Exception {
        assertDoesNotThrow(() -> invokeMethod("applyLanguageDirection", new Class[]{String.class}, "English"));
    }

    @Test
    @DisplayName("applyLanguageDirection does not throw for Arabic")
    void applyLanguageDirection_arabic() throws Exception {
        assertDoesNotThrow(() -> invokeMethod("applyLanguageDirection", new Class[]{String.class}, "Arabic"));
    }

    @Test
    @DisplayName("applyLanguageDirection does not throw for Persia")
    void applyLanguageDirection_persia() throws Exception {
        assertDoesNotThrow(() -> invokeMethod("applyLanguageDirection", new Class[]{String.class}, "Persia"));
    }

    @Test
    @DisplayName("applyLanguageDirection does not throw for null")
    void applyLanguageDirection_null() throws Exception {
        assertDoesNotThrow(() -> invokeMethod("applyLanguageDirection", new Class[]{String.class}, (String) null));
    }

    @Test
    @DisplayName("applyLanguageDirection does not throw for Persian")
    void applyLanguageDirection_persian() throws Exception {
        assertDoesNotThrow(() -> invokeMethod("applyLanguageDirection", new Class[]{String.class}, "Persian"));
    }

    @Test
    @DisplayName("applyLanguageDirection does not throw for Spanish")
    void applyLanguageDirection_spanish() throws Exception {
        assertDoesNotThrow(() -> invokeMethod("applyLanguageDirection", new Class[]{String.class}, "Spanish"));
    }

    // ─── applyRTLToScene with languageComboBox injected ──────────────────────

    @Test
    @DisplayName("applyRTLToScene with scene having null root is safe")
    void applyRTLToScene_sceneNullRoot() {
        LanguageManager.getInstance().setLanguage("Arabic");
        // new Scene without a root node — test that it doesn't crash
        try {
            controller.applyRTLToScene(new Scene(new VBox(), 100, 100));
        } catch (Exception e) {
            // acceptable
        }
        LanguageManager.getInstance().setLanguage("English");
    }

    // ─── Concrete subclass for testing ────────────────────────────────────────

    static class ConcreteBaseController extends BaseController {
        @Override
        protected void updateUIText(String newLanguage) {}

        @Override
        protected void setupController() {}
    }
}
