package JUnit;

import cz.Stasak.desktop.Classes.Admin;
import cz.Stasak.desktop.Classes.UserManager;
import cz.Stasak.desktop.GUI.GUIController;
import cz.Stasak.desktop.GUI.JavaFXInitializer;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import testutils.FakeLabel;
import testutils.FakeListView;

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
        userManager = new UserManager();
        userManager.clearUsers();
        admin = new Admin("admin", "admin123");
        controller = new GUIController(userManager, admin);
        testUsername = "testUser_" + UUID.randomUUID();
    }

    private void registerUserIfNotExists(String username, String password) {
        FakeLabel errorLabel = new FakeLabel();

        if (userManager.getUser(username) == null) {
            assertTrue(controller.registerUser(username, password, errorLabel));
        }
    }

    @Test
    public void testRegisterUser_Success() {
        FakeLabel errorLabel = new FakeLabel();


        boolean result = controller.registerUser(testUsername, "testPass123", errorLabel);

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
        assertNotNull(userManager.getUser(testUsername));
    }


    @Test
    public void testRegisterUser_Failure() {
        FakeLabel errorLabel = new FakeLabel();


        controller.registerUser(testUsername, "testPass", errorLabel);
        boolean result = controller.registerUser(testUsername, "anotherPass", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Username already exists. Please choose another one.", errorLabel.getText());
    }

    @Test
    public void testLoginUser_Admin() {
        FakeLabel errorLabel = new FakeLabel();
        assertTrue(controller.loginUser("admin", "admin123", errorLabel));
    }

    @Test
    public void testLoginUser_ValidUser() {
        FakeLabel errorLabel = new FakeLabel();
        registerUserIfNotExists(testUsername, "testPass123");
        boolean result = controller.loginUser(testUsername, "testPass123", errorLabel);
        assertTrue(result);
    }

    @Test
    public void testLoginUser_InvalidUser() {
        FakeLabel errorLabel = new FakeLabel();
        boolean result = controller.loginUser("invalidUser", "wrongPass", errorLabel);
        assertFalse(result);
    }

    @Test
    public void testRefreshUserList() {
        FakeListView<String> userListView = new FakeListView<>();


        userManager.registerUser("user1", "pass1");
        userManager.registerUser("user2", "pass2");

        controller.refreshUserList(userListView);

        assertEquals(2, userListView.getItems().size());
        assertTrue(userListView.getItems().containsAll(java.util.List.of("user1", "user2")));
    }

    @Test
    public void testDeleteUser_Success() {
        FakeListView<String> userListView = new FakeListView<>();

        userManager.registerUser("user1", "pass1");
        userListView.getItems().add("user1");

        boolean result = controller.deleteUser("user1", userListView);

        assertTrue(result);
        assertEquals(0, userListView.getItems().size());
        assertNull(userManager.getUser("user1"));
    }


    @Test
    public void testDeleteUser_Failure() {
        FakeListView<String> userListView = new FakeListView<>();
        ;
        userListView.getItems().add("user1");

        controller.deleteUser("user1", userListView);

        assertEquals(1, userListView.getItems().size());
        assertEquals("user1", userListView.getItems().get(0));
    }

    @Test
    public void testUpdateUserProfile_Success() {
        userManager.registerUser("oldUser", "oldPass");

        boolean result = controller.updateUserProfile("oldUser", "newUser", "newPass");

        assertTrue(result);
        assertNull(userManager.getUser("oldUser"));
        assertNotNull(userManager.getUser("newUser"));
        assertEquals("newPass", userManager.getUser("newUser").getPassword());
    }

    @Test
    public void testUpdateUserProfile_Failure() {
        boolean result = controller.updateUserProfile("nonExistentUser", "newUser", "newPass");

        assertFalse(result);
    }

    @Test
    public void testValidatePassword_Valid() {
        FakeLabel errorLabel = new FakeLabel();
        boolean result = controller.validatePassword("strongPass123", errorLabel);

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
    }

    @Test
    public void testValidatePassword_TooShort() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword("short", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password must be at least 8 characters long.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_Null() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword(null, errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot be empty.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_OnlySpaces() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword("   ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot be empty.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_LeadingSpace() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword(" pass1234", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot start or end with spaces.", errorLabel.getText());
    }

    @Test
    public void testValidatePassword_TrailingSpace() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword("pass1234 ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals("Password cannot start or end with spaces.", errorLabel.getText());
    }

}
