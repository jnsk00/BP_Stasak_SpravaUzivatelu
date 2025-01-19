package JUnit;

import org.example.desktop.Classes.Admin;
import org.example.desktop.Classes.UserManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.desktop.spravauzivatelu_grafika.GUIController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GUIControllerTest {

    private UserManager userManager; // Skutečná instance
    private Admin admin; // Skutečný admin
    private GUIController controller; // Testovaná třída

    @BeforeAll
    public static void initJavaFX() {
        // Inicializace JavaFX prostředí
        Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() {
        try {
            // Inicializace skutečných instancí
            userManager = new UserManager();
            admin = new Admin("admin", "admin123");
            controller = new GUIController(userManager, admin);
        } catch (Exception e) {
            fail("Initialization of test failed: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterUser_Success() {
        Label errorLabel = new Label();


        System.out.println("Starting testRegisterUser_Success");
        boolean result = controller.registerUser("testUser", "testPass123", errorLabel);

        // Výstupy pro ladění
        System.out.println("Result: " + result);
        System.out.println("Error label visible: " + errorLabel.isVisible());
        System.out.println("Error label text: " + errorLabel.getText());
        System.out.println("User exists in UserManager: " + (userManager.getUser("testUser") != null));

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
        assertNotNull(userManager.getUser("testUser"));
    }




    @Test
    public void testRegisterUser_Failure() {
        Label errorLabel = new Label();

        // Registrace uživatele poprvé
        controller.registerUser("testUser", "testPass", errorLabel);

        // Druhá registrace stejného uživatele (musí selhat)
        boolean result = controller.registerUser("testUser", "anotherPass", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Username already exists. Please choose another one.", errorLabel.getText());
    }

    @Test
    public void testLoginUser_Admin() {
        // Ověření přihlášení admina
        assertTrue(controller.loginUser("admin", "admin123"));
    }

    @Test
    public void testLoginUser_ValidUser() {
        // Registrace a přihlášení běžného uživatele
        userManager.registerUser("testUser", "testPass");

        boolean result = controller.loginUser("testUser", "testPass");

        assertTrue(result);
    }

    @Test
    public void testLoginUser_InvalidUser() {
        // Přihlášení s neplatnými údaji
        boolean result = controller.loginUser("invalidUser", "wrongPass");

        assertFalse(result);
    }

    @Test
    public void testRefreshUserList() {
        ListView<String> userListView = new ListView<>();

        // Registrace uživatelů
        userManager.registerUser("user1", "pass1");
        userManager.registerUser("user2", "pass2");

        // Obnovení seznamu uživatelů
        controller.refreshUserList(userListView);

        assertEquals(2, userListView.getItems().size());
        assertTrue(userListView.getItems().contains("user1"));
        assertTrue(userListView.getItems().contains("user2"));
    }

    @Test
    public void testDeleteUser_Success() {
        ListView<String> userListView = new ListView<>();

        // Registrace uživatele a přidání do seznamu
        userManager.registerUser("user1", "pass1");
        userListView.getItems().add("user1");

        // Smazání uživatele
        controller.deleteUser("user1", userListView);

        assertEquals(0, userListView.getItems().size());
        assertNull(userManager.getUser("user1"));
    }

    @Test
    public void testDeleteUser_Failure() {
        ListView<String> userListView = new ListView<>();

        // Přidání uživatele do seznamu
        userListView.getItems().add("user1");

        // Smazání uživatele, který neexistuje v UserManager
        controller.deleteUser("user1", userListView);

        assertEquals(1, userListView.getItems().size());
        assertEquals("user1", userListView.getItems().get(0));
    }

    @Test
    public void testUpdateUserProfile_Success() {
        // Registrace uživatele
        userManager.registerUser("oldUser", "oldPass");

        // Aktualizace uživatelského profilu
        boolean result = controller.updateUserProfile("oldUser", "newUser", "newPass");

        assertTrue(result);
        assertNull(userManager.getUser("oldUser"));
        assertNotNull(userManager.getUser("newUser"));
        assertEquals("newPass", userManager.getUser("newUser").getPassword());
    }

    @Test
    public void testUpdateUserProfile_Failure() {
        // Pokus o aktualizaci neexistujícího uživatele
        boolean result = controller.updateUserProfile("nonExistentUser", "newUser", "newPass");

        assertFalse(result);
    }

    @Test
    public void testValidatePassword_Valid() {
        Label errorLabel = new Label();

        // Heslo splňuje podmínky
        boolean result = controller.validatePassword("strongPass", errorLabel);

        assertTrue(result); // Heslo je validní
        assertFalse(errorLabel.isVisible());
    }


    @Test
    public void testValidatePassword_Invalid() {
        Label errorLabel = new Label();

        boolean result = controller.validatePassword("weak", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password must be at least 8 characters long.", errorLabel.getText());
    }
}
