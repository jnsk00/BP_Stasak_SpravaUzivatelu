package org.example.desktop.Classes;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager {
    public static final UserManager instance = new UserManager();
    private final Map<String, User> users = new HashMap<>();

    public UserManager() {}

    public User getUser(String username) {
        return users.get(username);
    }

    public static UserManager getInstance() {
        return instance;
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("User already exists: " + username);
            return false;
        }
        users.put(username, new User(username, password));
        return true;
    }

    public boolean deleteUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            return true;
        }
        return false;
    }

    public boolean updateUserProfile(String oldUsername, String newUsername, String newPassword) {
        if (!users.containsKey(oldUsername)) {
            System.out.println("User not found: " + oldUsername);
            return false;
        }
        User user = users.get(oldUsername);
        if (!oldUsername.equals(newUsername)) {
            users.remove(oldUsername);
            user.setUsername(newUsername);
            users.put(newUsername, user);
        }

        user.setPassword(newPassword);

        System.out.println("Updating user: " + oldUsername);
        System.out.println("New username: " + newUsername);
        System.out.println("New password: " + newPassword);
        System.out.println("User after update: " + users.get(newUsername) + " " + user.getPassword());

        return true;

    }

    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    public void saveUsersToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(new ArrayList<>(users.values()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUsersFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            List<User> loadedUsers = (List<User>) ois.readObject();
            for (User user : loadedUsers) {
                users.put(user.getUsername(), user);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No users found. Starting with an empty user list.");
        }
    }


}
