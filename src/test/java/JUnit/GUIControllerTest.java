package JUnit;

import cz.Stasak.desktop.Classes.Admin;
import cz.Stasak.desktop.Classes.User;
import cz.Stasak.desktop.Classes.UserManager;
import cz.Stasak.desktop.GUI.GUIController;
import cz.Stasak.desktop.GUI.JavaFXInitializer;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class    GUIControllerTest {

    private UserManager userManager; // Skutečná instance
    private Admin admin; // Skutečný admin
    private GUIController controller; // Testovaná třída
    private String testUsername;

    @BeforeAll
    public static void setupFX() {
        JavaFXInitializer.init();
    }


    @BeforeEach
    public void setUp() {
        try {
            // Inicializace skutečných instancí
            userManager = new UserManager();
            userManager.clearUsers();

            admin = new Admin("admin", "admin123");
            controller = new GUIController(userManager, admin);
            testUsername = "testUser_" + UUID.randomUUID();
        } catch (Exception e) {
            fail("Initialization of test failed: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterUser_Success() {
        Label errorLabel = new Label();

        System.out.println("👤 Testovací uživatel: " + testUsername);

        System.out.println("Starting testRegisterUser_Success");
        boolean result = controller.registerUser(testUsername, "testPass123", errorLabel);

        // Výstupy pro ladění
        System.out.println("Result: " + result);
        System.out.println("Error label visible: " + errorLabel.isVisible());
        System.out.println("Error label text: " + errorLabel.getText());
        System.out.println("User exists in UserManager: " + (userManager.getUser(testUsername) != null));

        assertTrue(result, "⚠️ Registrace selhala!");
        assertFalse(errorLabel.isVisible(), "❌ Error label se neměl zobrazit po úspěšné registraci!");
        assertNotNull(userManager.getUser(testUsername), "⚠️ Uživatel nebyl nalezen po registraci!");
    }


    @Test
    public void testRegisterUser_Failure() {
        Label errorLabel = new Label();

        // Registrace uživatele poprvé
        controller.registerUser(testUsername, "testPass", errorLabel);

        // Druhá registrace stejného uživatele (musí selhat)
        boolean result = controller.registerUser(testUsername, "anotherPass", errorLabel);

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
        Label errorLabel = new Label();
        System.out.println("🔥 Kontroluji existenci testUser před přihlášením...");

        // ❗ Registrujeme testUser **jen pokud neexistuje**
        if (userManager.getUser(testUsername) == null) {
            System.out.println("❌ testUser neexistuje! Registruji...");
            boolean registrationSuccess = controller.registerUser(testUsername, "testPass123", errorLabel);
            assertTrue(registrationSuccess, "⚠️ Registrace testUser selhala!");
        }

        // ✅ Ujistíme se, že je testUser správně registrován
        User registeredUser = userManager.getUser(testUsername);
        assertNotNull(registeredUser, "⚠️ testUser nebyl nalezen po registraci!");
        System.out.println("🔑 Heslo uložené v UserManager: " + registeredUser.getPassword());

        // 📝 Pokusíme se přihlásit
        boolean result = controller.loginUser(testUsername, "testPass123");
        System.out.println("✅ Přihlášení výsledek: " + result);

        assertTrue(result, "⚠️ Přihlášení testUser selhalo!");
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

        // Debug výpisy – kontrola instance userManager
        System.out.println("🔍 userManager (test): " + userManager);
        System.out.println("🔍 userManager (controller): " + controller.getUserManager());

        // Registrace uživatele a přidání do seznamu
        userManager.registerUser("user1", "pass1");
        userListView.getItems().add("user1");

        // Smazání uživatele
        boolean result = controller.deleteUser("user1", userListView);
        System.out.println("🗑️ Výsledek smazání: " + result);

        assertTrue(result, "Uživatel nebyl smazán!");
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
        boolean result = controller.validatePassword("strongPass", errorLabel);

        assertTrue(result); // Heslo je validní
        assertFalse(errorLabel.isVisible());
    }

    @Test
    public void testValidatePassword_TooShort() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("weak", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password must be at least 8 characters long.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_Null() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword(null, errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot be empty.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_OnlySpaces() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("   ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot be empty.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_LeadingSpace() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword(" pass1234", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot start or end with spaces.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_TrailingSpace() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("pass1234 ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot start or end with spaces.", errorLabel.getText());
    }

}
