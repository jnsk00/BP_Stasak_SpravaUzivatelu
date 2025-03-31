package cz.Stasak.backend;


import cz.Stasak.shared.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final Map<String, String> sessions = new HashMap<>(); // Simulace session

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = userService.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            sessions.put(token, username);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("isAdmin", user.isAdmin());

            return response;
        }
        throw new RuntimeException("Invalid credentials");
    }
}
