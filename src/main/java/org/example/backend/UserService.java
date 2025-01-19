package org.example.backend;

import org.example.shared.User;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {
    private final Map<String, User> users = new HashMap<>();

    public UserService() {
        // Přidání admin účtu při startu aplikace
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123"); // Případně zahashuj heslo
        admin.setAdmin(true); // Přidání role admina
        users.put("admin", admin);

        System.out.println("Admin účet přidán: " + admin.getUsername());
    }

    public boolean registerUser(String username, String password) {
        // Ověření, že vstupy nejsou prázdné
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Uživatelské jméno a heslo nesmí být prázdné!");
        }

        // Ověření, že uživatel již neexistuje
        if (users.containsKey(username)) {
            return false; // Uživatelské jméno je již obsazené
        }

        // Vytvoření a uložení nového uživatele
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setAdmin(username.equals("admin"));
        users.put(username, user);

        return true;
    }

    public boolean loginUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public boolean updateUser(String oldUsername, String newUsername, String newPassword) {
        User user = users.get(oldUsername);

        if (user == null) {
            return false; // Uživatel neexistuje
        }

        if (newUsername != null && !newUsername.isEmpty()) {
            users.remove(oldUsername); // Smazání starého klíče
            user.setUsername(newUsername);
            users.put(newUsername, user);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }

        return true;
    }



    public boolean deleteUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            return true;
        }
        return false;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public Collection<User> getAllUsers() {
        if (users == null) {
            return new ArrayList<>(); // Vrátí prázdný seznam místo `null`
        }
        return users.values();
    }


}
