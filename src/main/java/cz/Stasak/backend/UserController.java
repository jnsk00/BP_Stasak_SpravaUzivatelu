package cz.Stasak.backend;

import cz.Stasak.shared.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final Map<String, String> sessions = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser
            (@RequestParam String username, @RequestParam String password) {
        boolean success = userService.registerUser(username, password);

        // Vracíme JSON odpověď místo prostého textu
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "Uživatel zaregistrován!" : "Uživatel již existuje!");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userService.getUser(username);

        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Neplatné přihlašovací údaje."));
        }

        boolean isAdmin = userService.getUser(username).isAdmin();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "admin", isAdmin
        ));
    }

    @PostMapping("/logout")
    public String logoutUser(@RequestParam("token") String token) {
        sessions.remove(token);
        return "OK";
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, String> userData) {
        String oldUsername = userData.get("oldUsername");
        String newUsername = userData.get("newUsername");
        String newPassword = userData.get("newPassword");

        boolean success = userService.updateUser(oldUsername, newUsername, newPassword);

        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Chyba při aktualizaci uživatele."));
        }
    }

    @PutMapping("/admin/update")
    public ResponseEntity<Map<String, Object>> updateUserAdmin(@RequestBody Map<String, String> userData) {
        String oldUsername = userData.get("oldUsername");
        String newUsername = userData.get("newUsername");
        String newPassword = userData.get("newPassword");

        boolean success = userService.updateUser(oldUsername, newUsername, newPassword);

        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Chyba při aktualizaci uživatele."));
        }
    }



    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        boolean removed = userService.deleteUser(username);
        if (removed) {
            return ResponseEntity.ok("Uživatel úspěšně smazán.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Uživatel nenalezen.");
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> getAllUsers() {
        try {
            Collection<User> users = userService.getAllUsers();
            if (users == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Chyba: seznam uživatelů je null");
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Chyba při načítání uživatelů: " + e.getMessage());
        }
    }

}

