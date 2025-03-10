package com.jataxmltransformer.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class LoadVerifyController {

    @FXML
    private ListView<HBox> ontologyListView; // ListView to display ontology lines with buttons
    @FXML
    private Label statusLabel;

    private List<String> ontologyLines = new ArrayList<>();

    @FXML
    public void initialize() {
        // Initialize UI components if needed
    }

    @FXML
    private void loadOntologyFile() {
        // Simulate loading an ontology file
        String ontologyContent = "<root>\n    <validTag>Valid Content</validTag>\n    <invalidTag>Invalid Content</invalidTag>\n</root>";
        ontologyLines = List.of(ontologyContent.split("\\n"));
        highlightErrors();
    }

    @FXML
    private void verifyFile() {
        // Placeholder for validation logic
        statusLabel.setText("Verification complete.");
    }

    @FXML
    public void highlightErrors() {
        ontologyListView.getItems().clear(); // Clear previous content

        // Loop through lines and display them in the ListView
        for (int i = 0; i < ontologyLines.size(); i++) {
            String line = ontologyLines.get(i);
            HBox hbox = new HBox(10);
            Label lineLabel = new Label(line);

            if (line.contains("invalidTag")) {
                lineLabel.setStyle("-fx-text-fill: red;"); // Highlight error line in red
                Button errorButton = new Button("Fix");
                int finalI = i;
                errorButton.setOnAction(e -> openDiffDialog(finalI, line)); // Fix button for the error line
                hbox.getChildren().addAll(lineLabel, errorButton);
            } else {
                hbox.getChildren().add(lineLabel);
            }

            ontologyListView.getItems().add(hbox); // Add the HBox (line + button) to the ListView
        }
    }

    private void openDiffDialog(int lineIndex, String errorText) {
        // Create a new Popup
        Popup popup = new Popup();

        // Load the FXML layout for the popup
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Popup_layout.fxml"));
        try {
            VBox popupContent = loader.load(); // Load the content from FXML
            popup.getContent().add(popupContent); // Add the loaded content to the popup
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception if FXML loading fails
            return;
        }

        // Get the controller from the FXML loader and set the context
        PopupController controller = loader.getController();
        controller.setPopupContext(lineIndex, errorText, popup, this); // Set line index and parent controller

        // Get the HBox containing the button to determine the correct position of the popup
        HBox sourceHBox = ontologyListView.getItems().get(lineIndex); // Get the corresponding HBox for the line
        Button sourceButton = (Button) sourceHBox.getChildren().get(1); // Get the button in the HBox (assumed to be the second child)

        // Get the position of the error button in screen coordinates
        double x = sourceButton.localToScreen(sourceButton.getBoundsInLocal()).getMinX();
        double y = sourceButton.localToScreen(sourceButton.getBoundsInLocal()).getMinY();

        // Position the popup relative to the button and show it
        popup.setAnchorX(x);
        popup.setAnchorY(y);
        popup.show(sourceButton, x, y);
    }

    public List<String> getOntologyLines() {
        return ontologyLines;
    }
}