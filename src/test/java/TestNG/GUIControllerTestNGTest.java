package TestNG;

import cz.Stasak.desktop.Classes.Admin;
import cz.Stasak.desktop.Classes.UserManager;
import cz.Stasak.desktop.GUI.GUIController;
import cz.Stasak.desktop.GUI.JavaFXInitializer;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

    @Test(priority = 1)
    public void testRegisterUser_Success() {
        Label errorLabel = new Label();

        System.out.println("Starting testRegisterUser_Success");
        boolean result = controller.registerUser("testUser", "testPass123", errorLabel);

        System.out.println("Result: " + result);
        System.out.println("Error label visible: " + errorLabel.isVisible());
        System.out.println("Error label text: " + errorLabel.getText());
        System.out.println("User exists in UserManager: " + (userManager.getUser("testUser") != null));

        assertTrue(result);
        assertFalse(errorLabel.isVisible());
        assertNotNull(userManager.getUser("testUser"));
    }

    @Test(priority = 2)
    public void testRegisterUser_Failure() {
        Label errorLabel = new Label();

        controller.registerUser("testUser", "testPass", errorLabel);
        boolean result = controller.registerUser("testUser", "anotherPass", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Username already exists. Please choose another one.");
    }

    @Test(priority = 3)
    public void testLoginUser_Admin() {
        assertTrue(controller.loginUser("admin", "admin123"));
    }

    @Test(priority = 4)
    public void testLoginUser_ValidUser() {
        userManager.registerUser("testUser", "testPass");
        boolean result = controller.loginUser("testUser", "testPass");
        assertTrue(result);
    }

    @Test(priority = 5)
    public void testLoginUser_InvalidUser() {
        boolean result = controller.loginUser("invalidUser", "wrongPass");
        assertFalse(result);
    }

    @Test(priority = 6)
    public void testRefreshUserList() {
        ListView<String> userListView = new ListView<>();

        userManager.registerUser("user1", "pass1");
        userManager.registerUser("user2", "pass2");

        controller.refreshUserList(userListView);

        assertEquals(userListView.getItems().size(), 2);
        assertTrue(userListView.getItems().contains("user1"));
        assertTrue(userListView.getItems().contains("user2"));
    }

    @Test(priority = 7)
    public void testDeleteUser_Success() {
        ListView<String> userListView = new ListView<>();

        userManager.registerUser("user1", "pass1");
        userListView.getItems().add("user1");

        controller.deleteUser("user1", userListView);

        assertEquals(userListView.getItems().size(), 0);
        assertNull(userManager.getUser("user1"));
    }

    @Test(priority = 8)
    public void testDeleteUser_Failure() {
        ListView<String> userListView = new ListView<>();
        userListView.getItems().add("user1");

        controller.deleteUser("user1", userListView);

        assertEquals(userListView.getItems().size(), 1);
        assertEquals(userListView.getItems().get(0), "user1");
    }

    @Test(priority = 9)
    public void testUpdateUserProfile_Success() {
        userManager.registerUser("oldUser", "oldPass");

        boolean result = controller.updateUserProfile("oldUser", "newUser", "newPass");

        assertTrue(result);
        assertNull(userManager.getUser("oldUser"));
        assertNotNull(userManager.getUser("newUser"));
        assertEquals(userManager.getUser("newUser").getPassword(), "newPass");
    }

    @Test(priority = 10)
    public void testUpdateUserProfile_Failure() {
        boolean result = controller.updateUserProfile("nonExistentUser", "newUser", "newPass");
        assertFalse(result);
    }

    @Test(priority = 11)
    public void testValidatePassword_Valid() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("strongPass", errorLabel);
        assertTrue(result);
        assertFalse(errorLabel.isVisible());
    }

    @Test(priority = 12)
    public void testValidatePassword_TooShort() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("weak", errorLabel);
        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password must be at least 8 characters long.");
    }

    @Test(priority = 13)
    public void testValidatePassword_NullPassword() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword(null, errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot be empty.");
    }

    @Test(priority = 14)
    public void testValidatePassword_EmptyPassword() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("   ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot be empty.");
    }

    @Test(priority = 15)
    public void testValidatePassword_StartsWithSpace() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword(" pass1234", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot start or end with spaces.");
    }

    @Test(priority = 16)
    public void testValidatePassword_EndsWithSpace() {
        Label errorLabel = new Label();
        boolean result = controller.validatePassword("pass1234 ", errorLabel);

        assertFalse(result);
        assertTrue(errorLabel.isVisible());
        assertEquals(errorLabel.getText(), "Password cannot start or end with spaces.");
    }
}