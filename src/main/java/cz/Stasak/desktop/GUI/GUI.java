package cz.Stasak.desktop.GUI;

import cz.Stasak.desktop.Classes.Admin;
import cz.Stasak.desktop.Classes.UserManager;
import cz.Stasak.desktop.adapters.FXLabelAdapter;
import cz.Stasak.desktop.adapters.FXListViewAdapter;
import cz.Stasak.shared.ui.LabelInterface;
import cz.Stasak.shared.ui.ListViewInterface;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application {

    UserManager userManager = new UserManager(); // pokud má default konstruktor
    Admin admin = new Admin("admin", "admin123"); // nebo jakákoliv testovací kombinace

    GUIController controller = new GUIController(userManager, admin);


    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage stage) {
        VBox layout = new VBox(10);
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Label loginErrorLabel = new Label();
        Button loginButton = new Button("Login");
        Button toRegisterButton = new Button("Register");

        FXLabelAdapter errorAdapter = new FXLabelAdapter(loginErrorLabel);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (controller.loginUser(username, password, errorAdapter)) {
                showAdminPanel(stage);
            }
        });

        toRegisterButton.setOnAction(e -> showRegisterScreen(stage));

        layout.getChildren().addAll(usernameField, passwordField, loginButton, toRegisterButton, loginErrorLabel);
        stage.setScene(new Scene(layout, 300, 200));
        stage.show();
    }

    private void showRegisterScreen(Stage stage) {
        VBox layout = new VBox(10);
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Label errorLabel = new Label();
        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");

        FXLabelAdapter errorAdapter = new FXLabelAdapter(errorLabel);

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (!controller.validatePassword(password, errorAdapter)) return;

            if (controller.registerUser(username, password, errorAdapter)) {
                showLoginScreen(stage);
            }
        });

        backButton.setOnAction(e -> showLoginScreen(stage));

        layout.getChildren().addAll(usernameField, passwordField, registerButton, backButton, errorLabel);
        stage.setScene(new Scene(layout, 300, 200));
        stage.show();
    }

    private void showAdminPanel(Stage stage) {
        VBox layout = new VBox(10);
        ListView<String> userListView = new ListView<>();
        FXListViewAdapter<String> listViewAdapter = new FXListViewAdapter<>(userListView);
        TextField newUserField = new TextField();
        PasswordField newPasswordField = new PasswordField();
        Button addButton = new Button("Add User");
        Button deleteButton = new Button("Delete User");
        Button logoutButton = new Button("Logout");

        controller.refreshUserList(listViewAdapter);

        addButton.setOnAction(e -> {
            String username = newUserField.getText();
            String password = newPasswordField.getText();
            controller.registerUser(username, password, new FXLabelAdapter(new Label())); // nebo null adapter, pokud nepotřebuješ výstup
            controller.refreshUserList(listViewAdapter);
        });

        deleteButton.setOnAction(e -> {
            String selectedUser = userListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                controller.deleteUser(selectedUser, listViewAdapter);
                controller.refreshUserList(listViewAdapter);
            }
        });

        logoutButton.setOnAction(e -> showLoginScreen(stage));

        layout.getChildren().addAll(userListView, newUserField, newPasswordField, addButton, deleteButton, logoutButton);
        stage.setScene(new Scene(layout, 300, 400));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
