package Mockito;

import cz.Stasak.backend.SpravaUzivateluApplication;
import cz.Stasak.backend.UserController;
import cz.Stasak.backend.UserService;
import cz.Stasak.shared.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserController.class)
public class UserControllerMockitoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testRegisterUser_Success() throws Exception {
        when(userService.registerUser("newUser", "newPass")).thenReturn(true);

        mockMvc.perform(post("/api/users/register")
                        .param("username", "newUser")
                        .param("password", "newPass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Uživatel zaregistrován!"));
    }

    @Test
    public void testRegisterUser_Failure_AlreadyExists() throws Exception {
        when(userService.registerUser("existingUser", "pass")).thenReturn(false);

        mockMvc.perform(post("/api/users/register")
                        .param("username", "existingUser")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Uživatel již existuje!"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        User user = new User("john", "pass123");
        user.setAdmin(false);
        when(userService.getUser("john")).thenReturn(user);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "john",
                                "password", "pass123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    public void testLoginUser_InvalidCredentials() throws Exception {
        when(userService.getUser("ghost")).thenReturn(null);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "ghost",
                                "password", "wrong"
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Neplatné přihlašovací údaje."));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        when(userService.updateUser("john", "johnny", "newPass")).thenReturn(true);

        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "oldUsername", "john",
                                "newUsername", "johnny",
                                "newPassword", "newPass"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testUpdateUser_Failure() throws Exception {
        when(userService.updateUser("ghost", "ghost2", "pass")).thenReturn(false);

        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "oldUsername", "ghost",
                                "newUsername", "ghost2",
                                "newPassword", "pass"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Chyba při aktualizaci uživatele."));
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        when(userService.deleteUser("john")).thenReturn(true);

        mockMvc.perform(delete("/api/users/delete/john"))
                .andExpect(status().isOk())
                .andExpect(content().string("Uživatel úspěšně smazán."));
    }

    @Test
    public void testDeleteUser_NotFound() throws Exception {
        when(userService.deleteUser("ghost")).thenReturn(false);

        mockMvc.perform(delete("/api/users/delete/ghost"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Uživatel nenalezen."));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(java.util.List.of(
                new User("user1", "pass1"),
                new User("user2", "pass2")
        ));

        mockMvc.perform(get("/api/users/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }
}
