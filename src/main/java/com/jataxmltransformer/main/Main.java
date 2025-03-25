package com.jataxmltransformer.main;

import com.jataxmltransformer.middleware.Middleware;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        // Initializing the middleware singleton
        Middleware.getInstance();

        // Launching App
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Main.fxml"));
        AnchorPane root = loader.load();

        // Create Scene
        Scene scene = new Scene(root);

        // Set app title
        primaryStage.setTitle("XML Ontology Verifier");

        // Set the app icon
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/app_icon.png")));
        primaryStage.getIcons().add(appIcon);

        // Set the main scene on the main window
        primaryStage.setScene(scene);

        primaryStage.setResizable(false);

        // Show Window
        primaryStage.show();
    }
}