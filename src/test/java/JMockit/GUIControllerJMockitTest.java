package JMockit;

import cz.Stasak.desktop.Classes.Admin;
import cz.Stasak.desktop.Classes.User;
import cz.Stasak.desktop.Classes.UserManager;
import cz.Stasak.desktop.GUI.GUIController;

import cz.Stasak.desktop.GUI.JavaFXInitializer;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit5.JMockitExtension;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(JMockitExtension.class)
public class GUIControllerJMockitTest {

    @Mocked
    private UserManager userManager;

    private Label errorLabel;
    private ListView<String> userListView;
    private Button someButton;
    private TextField usernameField;

    private Admin admin;

    @Tested
    private GUIController controller;

    @BeforeAll
    public void initJavaFX() {
        JavaFXInitializer.init();
    }

    @BeforeEach
    public void setUp() {
        admin = new Admin("admin", "admin123");
        controller = new GUIController(userManager, admin);
        errorLabel = new Label();
        userListView = new ListView<>();
        someButton = new Button();
        usernameField = new TextField();
        Platform.runLater(() -> errorLabel = new Label());
    }

    @Test
    public void testRegisterUser_Success() {
        Label errorLabel = new Label();

        new Expectations() {{
            userManager.registerUser("testUser", "testPass");
            result = true;
        }};

        boolean result = controller.registerUser("testUser", "testPass", errorLabel);

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
    }

    @Test
    public void testRegisterUser_Failure_UserAlreadyExists() {
        Label errorLabel = new Label();

        new Expectations() {{
            userManager.registerUser("testUser", "testPass");
            result = false;
        }};

        boolean result = controller.registerUser("testUser", "testPass", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Username already exists. Please choose another one.", errorLabel.getText());
    }

    @Test
    public void testLoginUser_Admin() {
        assertTrue(controller.loginUser("admin", "admin123"));
    }

    @Test
    public void testLoginUser_ValidUser() {
        User mockUser = new User("testUser", "testPass");

        new Expectations() {{
            userManager.getUser("testUser");
            result = mockUser;
        }};

        assertTrue(controller.loginUser("testUser", "testPass"));
    }

    @Test
    public void testLoginUser_InvalidUser() {
        new Expectations() {{
            userManager.getUser("invalidUser");
            result = null;
        }};

        assertFalse(controller.loginUser("invalidUser", "wrongPass"));
    }

    @Test
    public void testDeleteUser_Success() {
        ListView<String> userListView = new ListView<>();

        new Expectations() {{
            userManager.deleteUser("testUser");
            result = true;
        }};

        boolean result = controller.deleteUser("testUser", userListView);

        assertTrue(result);
    }

    @Test
    public void testDeleteUser_Failure() {
        ListView<String> userListView = new ListView<>();

        new Expectations() {{
            userManager.deleteUser("testUser");
            result = false;
        }};

        boolean result = controller.deleteUser("testUser", userListView);

        assertFalse(result);
    }

    @Test
    public void testValidatePassword_Valid() {
        Label errorLabel = new Label();

        boolean result = controller.validatePassword("strongPass123", errorLabel);

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
    }

    @Test
    public void testValidatePassword_Invalid() {
        Label errorLabel = new Label();

        boolean result = controller.validatePassword("short", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password must be at least 8 characters long.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_NullPassword() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword(null, errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot be empty.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_EmptyPassword() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot be empty.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_BlankPassword() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("   ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot be empty.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_StartsWithSpace() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword(" pass1234", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot start or end with spaces.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_EndsWithSpace() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("pass1234 ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot start or end with spaces.", errorLabel.getText());
    }

}
