package org.example.desktop.spravauzivatelu_grafika;

import org.example.desktop.Classes.Admin;
import org.example.desktop.Classes.UserManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUI extends Application {
    @Override
    public void start(Stage primaryStage) {

        UserManager userManager = UserManager.getInstance();
        Admin admin = new Admin("admin", "admin123");
        GUIController controller = new GUIController(userManager, admin);

        userManager.loadUsersFromFile("users.dat");

        primaryStage.setTitle("Správa uživatelů");

        showMainScreen(primaryStage, controller);
    }

    @Override
    public void stop() {
        UserManager userManager = UserManager.getInstance();

        userManager.saveUsersToFile("users.dat");
    }


    private void showMainScreen(Stage stage, GUIController controller) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: linear-gradient(to bottom, #ece9e6, #ffffff);");

        Label welcomeLabel = new Label("Vítejte v aplikaci");
        welcomeLabel.setFont(new Font("Arial", 18));
        welcomeLabel.setTextFill(Color.DARKSLATEGRAY);

        Button loginButton = new Button("Přihlásit se");
        Button registerButton = new Button("Registrovat");

        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        registerButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        loginButton.setOnAction(event -> showLoginScreen(stage, controller));
        registerButton.setOnAction(event -> showRegisterScreen(stage, controller));

        layout.getChildren().addAll(welcomeLabel, loginButton, registerButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }


    private void showLoginScreen(Stage stage, GUIController controller) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: linear-gradient(to bottom, #ffffff, #ece9e6);");

        Label loginLabel = new Label("Login");
        loginLabel.setFont(new Font("Arial", 16));
        loginLabel.setTextFill(Color.DARKSLATEGRAY);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-padding: 10;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-padding: 10;");

        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        backButton.setOnAction(event -> showMainScreen(stage, controller));

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);


        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (controller.loginUser(username, password)) {
                // Zjistíme, zda je uživatel admin
                if (username.equals("admin")) {
                    showAdminPanel(stage, controller, true);
                } else {
                    showUserAccountScreen(stage, username, controller);
                }
            } else {
                // Neplatné přihlašovací údaje
                errorLabel.setText("Invalid username or password.");
                errorLabel.setVisible(true);
            }
        });
        layout.getChildren().addAll(loginLabel, usernameField, passwordField, submitButton, backButton, errorLabel);

        stage.setScene(new Scene(layout, 400, 300));
    }

    private void showRegisterScreen(Stage stage, GUIController controller) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: linear-gradient(to bottom, #ffffff, #ece9e6);");

        Label registerLabel = new Label("Register");
        registerLabel.setFont(new Font("Arial", 16));
        registerLabel.setTextFill(Color.DARKSLATEGRAY);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-padding: 10;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-padding: 10;");

        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        submitButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (!controller.validatePassword(password, errorLabel)) {
                return;
            }

            boolean registered = controller.registerUser(username, password, errorLabel);
            if (registered) {
                showUserAccountScreen(stage, username, controller);
            }
        });


        backButton.setOnAction(event -> showMainScreen(stage, controller));



        layout.getChildren().addAll(registerLabel, usernameField, passwordField, errorLabel, submitButton, backButton);

        stage.setScene(new Scene(layout, 400, 300));
    }


    private void showUserAccountScreen(Stage stage, String username, GUIController controller){
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: linear-gradient(to bottom, #ece9e6, #ffffff);");

        Label accountLabel = new Label("Welcome, " + username + "!");
        accountLabel.setFont(new Font("Arial", 16));
        accountLabel.setTextFill(Color.DARKSLATEGRAY);

        Button editProfileButton = new Button("Edit Profile");
        Button logoutButton = new Button("Logout");

        editProfileButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        editProfileButton.setOnAction(event -> showEditProfileScreen(stage, username, controller,false));
        logoutButton.setOnAction(event -> showMainScreen(stage, controller));

        layout.getChildren().addAll(accountLabel, editProfileButton, logoutButton);

        stage.setScene(new Scene(layout, 400, 300));
    }

    private void showEditProfileScreen(Stage stage, String oldUsername, GUIController controller, boolean isAdmin) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: linear-gradient(to bottom, #ffffff, #ece9e6);");

        Label editLabel = new Label("Edit Profile");
        editLabel.setFont(new Font("Arial", 16));
        editLabel.setTextFill(Color.DARKSLATEGRAY);

        TextField usernameField = new TextField(oldUsername);
        usernameField.setStyle("-fx-padding: 10;");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.setStyle("-fx-padding: 10;");

        Button saveButton = new Button("Save");
        Button backButton = new Button("Back");

        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.RED);
        messageLabel.setVisible(false);

        saveButton.setOnAction(event -> {
            String newUsername = usernameField.getText();
            String newPassword = newPasswordField.getText();

            if (!controller.validatePassword(newPassword, messageLabel)) {
                return;
            }

            boolean success = controller.updateUserProfile(oldUsername, newUsername, newPassword);
            if (success) {
                if (isAdmin) {
                    showAdminPanel(stage, controller, true);
                } else {
                    showUserAccountScreen(stage, newUsername, controller);
                }
            } else {
                messageLabel.setText("Failed to update profile. Try a different username.");
                messageLabel.setTextFill(Color.RED);
                messageLabel.setVisible(true);
            }
        });

        backButton.setOnAction(event -> {
            if (isAdmin) {
                showAdminPanel(stage, controller, true);
            } else {
                showUserAccountScreen(stage, oldUsername, controller);
            }
        });

        layout.getChildren().addAll(editLabel, usernameField, newPasswordField, saveButton, backButton, messageLabel);

        stage.setScene(new Scene(layout, 400, 300));
    }

    private void showAdminPanel(Stage stage, GUIController controller, boolean isAdmin) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: linear-gradient(to bottom, #ece9e6, #ffffff);");

        Label adminLabel = new Label("Admin Panel");
        adminLabel.setFont(new Font("Arial", 16));
        adminLabel.setTextFill(Color.DARKSLATEGRAY);

        // Seznam uživatelů
        ListView<String> userListView = new ListView<>();
        controller.refreshUserList(userListView);

        Button editUserButton = new Button("Edit Selected User");
        editUserButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        editUserButton.setOnAction(event -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                showEditProfileScreen(stage, selectedUser, controller, true);
            }
        });


        Button deleteUserButton = new Button("Delete Selected User");
        deleteUserButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        deleteUserButton.setOnAction(event -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            controller.deleteUser(selectedUser, userListView);
        });

        Button backButton = new Button("Logout");
        backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10;");
        backButton.setOnAction(event -> showMainScreen(stage, controller));




        layout.getChildren().addAll(adminLabel, userListView, editUserButton, deleteUserButton, backButton);

        stage.setScene(new Scene(layout, 400, 400));
    }

    private void refreshUserList(ListView<String> userListView, UserManager userManager) {
        userListView.getItems().clear();
        userManager.listUsers().forEach(user -> userListView.getItems().add(user.getUsername()));
    }


    public static void main(String[] args) {
        launch();
    }
}