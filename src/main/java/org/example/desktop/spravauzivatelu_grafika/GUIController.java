package org.example.desktop.spravauzivatelu_grafika;

import org.example.desktop.Classes.Admin;
import org.example.desktop.Classes.User;
import org.example.desktop.Classes.UserManager;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class GUIController {
    private final UserManager userManager;
    private final Admin admin;

    public GUIController(UserManager userManager, Admin admin) {
        this.userManager = userManager;
        this.admin = admin;
    }

    public boolean registerUser(String username, String password, Label errorLabel) {
        System.out.println("Registering user: " + username);
        if (userManager.registerUser(username, password)) {
            errorLabel.setVisible(false); // Ujistíme se, že errorLabel je skrytý
            System.out.println("Registration successful: " + username);
            return true;
        } else {
            errorLabel.setText("Username already exists. Please choose another one.");
            errorLabel.setVisible(true);
            return false;
        }
    }



    public boolean loginUser(String username, String password) {
        System.out.println("Attempting login with username: " + username + " and password: " + password);

        // Ověření admin přihlášení
        if (username != null && password != null &&
                username.equals(admin.getUsername()) && password.equals(admin.getPassword())) {
            System.out.println("Admin login successful.");
            return true; // Přihlášení jako admin
        }

        // Ověření přihlášení běžného uživatele
        User user = userManager.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("User login successful for: " + username);
            return true; // Úspěšné přihlášení běžného uživatele
        }

        // Neplatné přihlašovací údaje
        System.out.println("Login failed for: " + username);
        return false;
    }





    public void refreshUserList(ListView<String> userListView) {
        userListView.getItems().clear();
        userManager.listUsers().forEach(user -> userListView.getItems().add(user.getUsername()));
    }

    public boolean deleteUser(String username, ListView<String> userListView) {
        if (userManager.deleteUser(username)) {
            refreshUserList(userListView);
            return true;
        }
        return false;
    }

    public boolean updateUserProfile(String oldUsername, String newUsername, String newPassword) {
        boolean success = userManager.updateUserProfile(oldUsername, newUsername, newPassword);
        if (success) {
            System.out.println("Updated user profile: " + oldUsername + " -> " + newUsername);
        } else {
            System.out.println("Failed to update user profile for: " + oldUsername);
        }
        return success;
    }

    public boolean validatePassword(String password, Label errorLabel) {
        if (password.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters long.");
            errorLabel.setVisible(true);
            return false; // Heslo není validní
        }
        errorLabel.setVisible(false);
        return true; // Heslo je validní
    }


}