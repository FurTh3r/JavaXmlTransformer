package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logs.AppLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Controller for the main window of the application.
 * Manages the loading process of the content for each tab in the TabPane.
 */
public class MainController {

    @FXML
    private TabPane tabPane;

    /**
     * Initializes the controller and loads content for each tab.
     * This method is automatically invoked when the scene is fully loaded.
     */
    @FXML
    public void initialize() {
        loadTabContent("/GUI/NamespacesHandler.fxml", tabPane.getTabs().get(0));
        loadTabContent("/GUI/StructureHandler.fxml", tabPane.getTabs().get(1));
        loadTabContent("/GUI/LoadVerify.fxml", tabPane.getTabs().get(2));
        loadTabContent("/GUI/Save.fxml", tabPane.getTabs().get(3));
    }

    /**
     * Loads the FXML content into a specified tab.
     *
     * @param fxmlFile The relative path of the FXML file to load.
     * @param tab      The tab in which to load the content.
     */
    private void loadTabContent(String fxmlFile, Tab tab) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane content = loader.load();
            tab.setContent(content);
        } catch (IOException e) {
            // Log error and set empty content in case of failure
            AppLogger.severe(e.getMessage());
            tab.setContent(new AnchorPane());
        }
    }
}
