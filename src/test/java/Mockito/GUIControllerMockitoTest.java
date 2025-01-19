package Mockito;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.desktop.spravauzivatelu_grafika.GUIController;
import org.example.desktop.Classes.Admin;
import org.example.desktop.Classes.User;
import org.example.desktop.Classes.UserManager;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class) // Povolení Mockito v testovací třídě
public class GUIControllerMockitoTest {

    @Mock
    private UserManager userManager; // Mockovaná závislost

    private Label errorLabel;
    private ListView<String> userListView;
    private Button someButton;
    private TextField usernameField;

    private Admin admin;

    @InjectMocks
    private GUIController controller;

    @BeforeAll
    public static void initJavaFX() {
        // Spustíme JavaFX Toolkit na pozadí, pokud ještě neběží
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

        // Simulace úspěšné registrace
        when(userManager.registerUser("testUser", "testPass")).thenReturn(true);

        boolean result = controller.registerUser("testUser", "testPass", errorLabel);

        assertTrue(result);
        assertFalse(errorLabel.isVisible());

        // Ověření, že se metoda registerUser volala správně
        verify(userManager).registerUser("testUser", "testPass");
    }

    @Test
    public void testRegisterUser_Failure_UserAlreadyExists() {
        Label errorLabel = new Label();

        // Simulace, že uživatel již existuje
        when(userManager.registerUser("testUser", "testPass")).thenReturn(false);

        boolean result = controller.registerUser("testUser", "testPass", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Username already exists. Please choose another one.", errorLabel.getText());

        // Ověření, že se metoda registerUser volala správně
        verify(userManager).registerUser("testUser", "testPass");
    }

    @Test
    public void testLoginUser_Admin() {
        assertTrue(controller.loginUser("admin", "admin123"));
    }

    @Test
    public void testLoginUser_ValidUser() {
        User mockUser = new User("testUser", "testPass");
        when(userManager.getUser("testUser")).thenReturn(mockUser);

        assertTrue(controller.loginUser("testUser", "testPass"));

        verify(userManager).getUser("testUser");
    }

    @Test
    public void testLoginUser_InvalidUser() {
        when(userManager.getUser("invalidUser")).thenReturn(null);

        assertFalse(controller.loginUser("invalidUser", "wrongPass"));

        verify(userManager).getUser("invalidUser");
    }

    @Test
    public void testDeleteUser_Success() {
        ListView<String> userListView = new ListView<>();

        when(userManager.deleteUser("testUser")).thenReturn(true);

        boolean result = controller.deleteUser("testUser", userListView);

        assertTrue(result);
        verify(userManager).deleteUser("testUser");
    }

    @Test
    public void testDeleteUser_Failure() {
        ListView<String> userListView = new ListView<>();

        when(userManager.deleteUser("testUser")).thenReturn(false);

        boolean result = controller.deleteUser("testUser", userListView);

        assertFalse(result);
        verify(userManager).deleteUser("testUser");
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
}
