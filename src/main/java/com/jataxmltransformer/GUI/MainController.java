package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logs.AppLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class MainController {

    @FXML
    private TabPane tabPane;

    @FXML
    public void initialize() {
        loadTabContent("/GUI/NamespacesHandler.fxml", tabPane.getTabs().get(0));
        loadTabContent("/GUI/StructureHandler.fxml", tabPane.getTabs().get(1));
        loadTabContent("/GUI/LoadVerify.fxml", tabPane.getTabs().get(2));
        loadTabContent("/GUI/Save.fxml", tabPane.getTabs().get(3));
    }

    private void loadTabContent(String fxmlFile, Tab tab) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane content = loader.load();
            tab.setContent(content);
        } catch (IOException e) {
            AppLogger.severe(e.getMessage());
            tab.setContent(new AnchorPane());
        }
    }
}
