package cz.Stasak.desktop.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebViewApp extends Application {
    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        webView.getEngine().load("http://localhost:8080/");

        Scene scene = new Scene(webView, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Správa uživatelů");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
