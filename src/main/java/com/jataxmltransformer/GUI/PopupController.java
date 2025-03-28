package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logs.AppLogger;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Popup;

import java.util.List;

/**
 * Controller class for handling the popup window that allows users
 * to edit or remove erroneous lines detected in the ontology file.
 */
public class PopupController {

    @FXML
    private TextArea originalText; // Displays the original text with the error
    @FXML
    private TextArea editedText; // Editable field for correcting the error
    @FXML
    private Label errorMessageLabel; // Label to display the error message
    @FXML
    private Button saveButton; // Button to save changes
    @FXML
    private Button cancelButton; // Button to cancel changes
    @FXML
    private Button removeButton; // Button to remove the erroneous line(s)

    private int lineIndex; // The index of the erroneous line
    private int endIndex; // The end index if the error spans multiple lines
    private Popup popup; // Reference to the popup window
    private LoadVerifyController parentController; // Reference to the parent controller

    /**
     * Initializes the controller and sets event handlers for buttons.
     */
    @FXML
    public void initialize() {
        AppLogger.info("PopupController initialized.");
        if (errorMessageLabel == null)
            AppLogger.severe("errorMessageLabel is null! Check FXML file.");

        saveButton.setOnAction(_ -> saveChanges());
        cancelButton.setOnAction(_ -> cancelChanges());
        removeButton.setOnAction(_ -> removeLine());

        // Synchronize scrolling between the two TextAreas
        synchronizeScrolling();
    }

    /**
     * Synchronizes the scrolling of the two TextAreas.
     */
    private void synchronizeScrolling() {
        originalText.scrollTopProperty().bindBidirectional(editedText.scrollTopProperty());
        originalText.scrollLeftProperty().bindBidirectional(editedText.scrollLeftProperty());
    }

    /**
     * Configures the popup with necessary details about the error.
     *
     * @param lineIndex        The index of the erroneous line.
     * @param endIndex         The end index if the error spans multiple lines.
     * @param errorText        The text containing the error.
     * @param errorMessage     The brief error message.
     * @param errorDetails     Detailed information about the error.
     * @param popup            The popup window reference.
     * @param parentController The parent controller handling ontology verification.
     */
    public void setPopupContext(int lineIndex, int endIndex, String errorText, String errorMessage,
                                String errorDetails, Popup popup, LoadVerifyController parentController) {
        this.lineIndex = lineIndex;
        this.endIndex = endIndex;
        this.popup = popup;
        this.parentController = parentController;

        originalText.setText(errorText); // Set the original text
        editedText.setText(errorText);   // Initialize editable field with the same text

        // Prevent NullPointerException when setting the error message label
        if (errorMessageLabel != null)
            errorMessageLabel.setText("Error: " + errorMessage + "\nDetails: " + errorDetails);
        else {
            AppLogger.severe("errorMessageLabel is null inside setPopupContext!");
            CustomAlert.showError("Error", "The errorMessageLabel is null! Check Log file for details.");
        }
    }

    /**
     * Saves the changes made to the erroneous line(s) and updates the ontology file.
     */
    private void saveChanges() {
        if (lineIndex < 0 || lineIndex >= parentController.getOntologyLines().size())
            return;

        List<String> ontologyLines = parentController.getOntologyLines(); // Get current lines

        String[] text = editedText.getText().split("\n"); // Get modified text

        // Remove the erroneous lines from the ontology list
        ontologyLines.subList(lineIndex, endIndex + 1).clear();

        // Insert the corrected lines at the original position
        for (int i = 0; i < text.length; i++)
            ontologyLines.add(lineIndex + i, text[i]);

        // Update ontology lines in the parent controller
        parentController.setOntologyLines(ontologyLines);

        // Refresh the ListView to reflect changes
        parentController.highlightErrors(parentController.getErrors());
        parentController.verifyFile();
        popup.hide(); // Close the popup
    }

    /**
     * Cancels any modifications and closes the popup without making changes.
     */
    private void cancelChanges() {
        popup.hide();
    }

    /**
     * Removes the erroneous line(s) from the ontology file.
     */
    private void removeLine() {
        if (lineIndex < 0 || lineIndex >= parentController.getOntologyLines().size())
            return;

        List<String> ontologyLines = parentController.getOntologyLines(); // Get current lines

        // Remove the lines within the specified range
        ontologyLines.subList(lineIndex, endIndex + 1).clear();

        // Update ontology lines in the parent controller
        parentController.setOntologyLines(ontologyLines);

        // Refresh the ListView to reflect changes
        parentController.highlightErrors(parentController.getErrors());
        parentController.verifyFile();
        popup.hide(); // Close the popup
    }
}