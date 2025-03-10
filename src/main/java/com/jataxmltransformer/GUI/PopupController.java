package com.jataxmltransformer.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class PopupController {

    @FXML
    private TextField originalText;

    @FXML
    private TextField editedText;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button removeButton;

    private int lineIndex;
    private Popup popup;
    private LoadVerifyController parentController;

    // Initialize method to set up actions for the buttons
    public void initialize() {
        saveButton.setOnAction(e -> saveChanges());
        cancelButton.setOnAction(e -> cancelChanges());
        removeButton.setOnAction(e -> removeLine());
    }

    // Method to set the context of the popup (lineIndex and parent controller)
    public void setPopupContext(int lineIndex, String errorText, Popup popup, LoadVerifyController parentController) {
        this.lineIndex = lineIndex;
        this.popup = popup;
        this.parentController = parentController;
        originalText.setText(errorText); // Set the error text in the original TextField
    }

    // Handle the Save button action
    private void saveChanges() {
        parentController.getOntologyLines().set(lineIndex, editedText.getText()); // Save edited text
        parentController.highlightErrors(); // Re-highlight errors in the main view
        popup.hide(); // Close the popup
    }

    // Handle the Cancel button action
    private void cancelChanges() {
        popup.hide(); // Just close the popup without saving changes
    }

    // Handle the Remove Line button action
    private void removeLine() {
        parentController.getOntologyLines().remove(lineIndex); // Remove the line from the list
        parentController.highlightErrors(); // Re-highlight errors in the main view
        popup.hide(); // Close the popup
    }
}
