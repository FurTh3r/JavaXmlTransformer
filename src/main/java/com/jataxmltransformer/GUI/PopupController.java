package com.jataxmltransformer.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;
import java.util.List;

public class PopupController {

    @FXML private TextArea originalText;
    @FXML private TextArea editedText;
    @FXML private Label errorMessageLabel; // Label per mostrare il messaggio di errore
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button removeButton;

    private int lineIndex;
    private int endIndex;
    private Popup popup;
    private LoadVerifyController parentController;

    // Metodo di inizializzazione per impostare gli eventi dei pulsanti
    @FXML
    public void initialize() {
        System.out.println("PopupController initialized.");
        if (errorMessageLabel == null) {
            System.err.println("errorMessageLabel is null! Check FXML file.");
        }
        saveButton.setOnAction(e -> saveChanges());
        cancelButton.setOnAction(e -> cancelChanges());
        removeButton.setOnAction(e -> removeLine());
    }

    // Metodo per configurare il popup con informazioni dettagliate
    public void setPopupContext(int lineIndex, int endIndex, String errorText, String errorMessage, String errorDetails, Popup popup, LoadVerifyController parentController) {
        this.lineIndex = lineIndex;
        this.endIndex = endIndex;
        this.popup = popup;
        this.parentController = parentController;

        originalText.setText(errorText); // Testo originale con l'errore
        editedText.setText(errorText);   // Inizializza il campo modificabile con lo stesso testo

        // Controllo per evitare il NullPointerException
        if (errorMessageLabel != null) {
            errorMessageLabel.setText("Errore: " + errorMessage + "\nDettagli: " + errorDetails);
        } else {
            System.err.println("errorMessageLabel is null inside setPopupContext!");
        }
    }

    // Metodo per salvare le modifiche alla riga
    private void saveChanges() {
        if (lineIndex < 0 || lineIndex >= parentController.getOntologyLines().size()) {
            return;
        }

        // Get the current lines
        List<String> ontologyLines = parentController.getOntologyLines();

        // Get the modified text from the editedText field, split by newlines
        String[] text = editedText.getText().split("\n");

        // Remove the lines from lineIndex to endIndex (inclusive)
        ontologyLines.subList(lineIndex, endIndex + 1).clear();

        // Insert the new lines at the starting index (lineIndex)
        for (int i = 0; i < text.length; i++) {
            ontologyLines.add(lineIndex + i, text[i]);
        }

        // Update the ontology lines in the parent controller
        parentController.setOntologyLines(ontologyLines);

        // Refresh the ListView with the updated data
        parentController.highlightErrors(parentController.errors);
        popup.hide();
    }

    // Metodo per annullare le modifiche e chiudere il popup
    private void cancelChanges() {
        popup.hide();
    }

    // Metodo per rimuovere la riga con l'errore
    private void removeLine() {
        if (lineIndex < 0 || lineIndex >= parentController.getOntologyLines().size()) {
            return;
        }

        // Get the current lines
        List<String> ontologyLines = parentController.getOntologyLines();

        // Remove the lines in the range from lineIndex to endIndex
        ontologyLines.subList(lineIndex, endIndex + 1).clear();

        // Update the ontology lines in the parent controller
        parentController.setOntologyLines(ontologyLines);

        // Refresh the ListView with the updated data
        parentController.highlightErrors(parentController.errors);
        popup.hide();
    }
}