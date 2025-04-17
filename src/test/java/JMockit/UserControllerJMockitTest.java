package JMockit;

import cz.Stasak.backend.UserController;
import cz.Stasak.backend.UserService;
import cz.Stasak.shared.User;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerJMockitTest {

    @Tested
    private UserController userController;

    @Injectable
    private UserService userService;

    @Test
    public void testRegisterUser_Success() {
        new Expectations() {{
            userService.registerUser("newUser", "newPass");
            result = true;
        }};

        ResponseEntity<Map<String, Object>> response = userController.registerUser("newUser", "newPass");

        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals("Uživatel zaregistrován!", response.getBody().get("message"));
    }

    @Test
    public void testRegisterUser_Failure_AlreadyExists() {
        new Expectations() {{
            userService.registerUser("existingUser", "pass");
            result = false;
        }};

        ResponseEntity<Map<String, Object>> response = userController.registerUser("existingUser", "pass");

        assertFalse((Boolean) response.getBody().get("success"));
        assertEquals("Uživatel již existuje!", response.getBody().get("message"));
    }

    @Test
    public void testLoginUser_Success() {
        User user = new User("john", "pass123");
        user.setAdmin(true);

        new Expectations() {{
            userService.getUser("john");
            result = user;
        }};

        ResponseEntity<Map<String, Object>> response = userController.loginUser(Map.of(
                "username", "john",
                "password", "pass123"
        ));

        assertTrue((Boolean) response.getBody().get("success"));
        assertTrue((Boolean) response.getBody().get("admin"));
    }

    @Test
    public void testLoginUser_InvalidCredentials() {
        new Expectations() {{
            userService.getUser("ghost");
            result = null;
        }};

        ResponseEntity<Map<String, Object>> response = userController.loginUser(Map.of(
                "username", "ghost",
                "password", "wrong"
        ));

        assertEquals(401, response.getStatusCodeValue());
        assertFalse((Boolean) response.getBody().get("success"));
        assertEquals("Neplatné přihlašovací údaje.", response.getBody().get("message"));
    }

    @Test
    public void testUpdateUser_Success() {
        new Expectations() {{
            userService.updateUser("john", "johnny", "newPass");
            result = true;
        }};

        ResponseEntity<Map<String, Object>> response = userController.updateUser(Map.of(
                "oldUsername", "john",
                "newUsername", "johnny",
                "newPassword", "newPass"
        ));

        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    public void testUpdateUser_Failure() {
        new Expectations() {{
            userService.updateUser("ghost", "ghost2", "pass");
            result = false;
        }};

        ResponseEntity<Map<String, Object>> response = userController.updateUser(Map.of(
                "oldUsername", "ghost",
                "newUsername", "ghost2",
                "newPassword", "pass"
        ));

        assertEquals(400, response.getStatusCodeValue());
        assertFalse((Boolean) response.getBody().get("success"));
        assertEquals("Chyba při aktualizaci uživatele.", response.getBody().get("message"));
    }

    @Test
    public void testDeleteUser_Success() {
        new Expectations() {{
            userService.deleteUser("john");
            result = true;
        }};

        ResponseEntity<String> response = userController.deleteUser("john");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Uživatel úspěšně smazán.", response.getBody());
    }

    @Test
    public void testDeleteUser_NotFound() {
        new Expectations() {{
            userService.deleteUser("ghost");
            result = false;
        }};

        ResponseEntity<String> response = userController.deleteUser("ghost");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Uživatel nenalezen.", response.getBody());
    }

    @Test
    public void testGetAllUsers() {
        List<User> mockUsers = List.of(
                new User("user1", "pass1"),
                new User("user2", "pass2")
        );

        new Expectations() {{
            userService.getAllUsers();
            result = mockUsers;
        }};

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        List<User> resultList = (List<User>) response.getBody();
        assertEquals(2, resultList.size());
        assertEquals("user1", resultList.get(0).getUsername());
        assertEquals("user2", resultList.get(1).getUsername());
    }

}
