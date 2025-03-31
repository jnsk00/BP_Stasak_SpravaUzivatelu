package cz.Stasak.desktop.Classes;

public class Admin {
    private final String username;
    private final String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean deleteUser(String username, UserManager userManager) {
        boolean success = userManager.deleteUser(username);
        if (success) {
            System.out.println("Deleted user: " + username);
        } else {
            System.out.println("User not found: " + username);
        }
        return success;
    }

    public boolean updateUserProfile(String oldUsername, String newUsername, String newPassword, UserManager userManager) {
        boolean success = userManager.updateUserProfile(oldUsername, newUsername, newPassword);
        if (success) {
            System.out.println("Updated user profile: " + oldUsername + " -> " + newUsername);
        } else {
            System.out.println("Failed to update user profile: " + oldUsername);
        }
        return success;
    }
}
