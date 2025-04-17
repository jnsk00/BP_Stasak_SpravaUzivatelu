package testng;

import cz.Stasak.desktop.Classes.Admin;
import cz.Stasak.desktop.Classes.UserManager;
import cz.Stasak.desktop.GUI.GUIController;
import cz.Stasak.desktop.GUI.JavaFXInitializer;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutils.FakeLabel;
import testutils.FakeListView;

import static org.testng.Assert.*;

public class GUIControllerTestNGTest {

    private UserManager userManager;
    private Admin admin;
    private GUIController controller;

    @BeforeClass
    public void initJavaFX() {
        JavaFXInitializer.init();
    }

    @BeforeMethod
    public void setUp() {
        userManager = new UserManager();
        admin = new Admin("admin", "admin123");
        controller = new GUIController(userManager, admin);
    }

    private void registerUserIfNotExists(String username, String password) {
        FakeLabel errorLabel = new FakeLabel();

        if (userManager.getUser(username) == null) {
            boolean result = controller.registerUser(username, password, errorLabel);
            assertTrue(result, "Nepodařilo se zaregistrovat uživatele: " + username);
        }
    }

    @Test
    public void testRegisterUser_Success() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.registerUser("testUser", "testPass123", errorLabel);

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
        assertNotNull(userManager.getUser("testUser"));
    }

    @Test
    public void testRegisterUser_Failure() {
        FakeLabel errorLabel = new FakeLabel();


        controller.registerUser("testUser", "testPass", errorLabel);
        boolean result = controller.registerUser("testUser", "anotherPass", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Username already exists. Please choose another one.");
    }

    @Test
    public void testLoginUser_Admin() {
        FakeLabel errorLabel = new FakeLabel();

        assertTrue(controller.loginUser("admin", "admin123", errorLabel));
    }

    @Test
    public void testLoginUser_ValidUser() {
        FakeLabel errorLabel = new FakeLabel();

        registerUserIfNotExists("testUser", "testPass");
        boolean result = controller.loginUser("testUser", "testPass", errorLabel);

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

        assertEquals(userListView.getItems().size(), 2);
        assertTrue(userListView.getItems().contains("user1"));
        assertTrue(userListView.getItems().contains("user2"));
    }

    @Test
    public void testDeleteUser_Success() {
        FakeListView<String> userListView = new FakeListView<>();
        userManager.registerUser("user1", "pass1");
        userListView.getItems().add("user1");

        controller.deleteUser("user1", userListView);

        assertEquals(userListView.getItems().size(), 0);
        assertNull(userManager.getUser("user1"));
    }

    @Test
    public void testDeleteUser_Failure() {
        FakeListView<String> userListView = new FakeListView<>();
        userListView.getItems().add("user1");

        controller.deleteUser("user1", userListView);

        assertEquals(userListView.getItems().size(), 1);
        assertEquals(userListView.getItems().get(0), "user1");
    }

    @Test
    public void testUpdateUserProfile_Success() {
        userManager.registerUser("oldUser", "oldPass");

        boolean result = controller.updateUserProfile("oldUser", "newUser", "newPass");

        assertTrue(result);
        assertNull(userManager.getUser("oldUser"));
        assertNotNull(userManager.getUser("newUser"));
        assertEquals(userManager.getUser("newUser").getPassword(), "newPass");
    }

    @Test
    public void testUpdateUserProfile_Failure() {
        boolean result = controller.updateUserProfile("nonExistentUser", "newUser", "newPass");

        assertFalse(result);
    }

    @Test
    public void testValidatePassword_Valid() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword("strongPass", errorLabel);

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
    }

    @Test
    public void testValidatePassword_TooShort() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword("weak", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password must be at least 8 characters long.");
    }

    @Test
    public void testValidatePassword_NullPassword() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword(null, errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot be empty.");
    }

    @Test
    public void testValidatePassword_EmptyPassword() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword("   ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot be empty.");
    }

    @Test
    public void testValidatePassword_StartsWithSpace() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword(" pass1234", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot start or end with spaces.");
    }

    @Test
    public void testValidatePassword_EndsWithSpace() {
        FakeLabel errorLabel = new FakeLabel();

        boolean result = controller.validatePassword("pass1234 ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot start or end with spaces.");
    }
}
