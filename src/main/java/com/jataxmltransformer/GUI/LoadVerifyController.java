package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.utilities.MyPair;
import com.jataxmltransformer.logic.xml.XMLDiffChecker;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import com.jataxmltransformer.logs.AppLogger;
import com.jataxmltransformer.middleware.Middleware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for handling the loading, verification, and display of ontology files.
 * This class provides functionalities for loading ontology files (XML, RDF, OWL),
 * verifying the ontology structure, and highlighting any syntax or structural errors.
 */
public class LoadVerifyController {

    private List<ErrorInfo> errors = new ArrayList<>();
    private List<String> ontologyLines;
    private List<String> ontologyBackup;
    private Ontology ontologyData;

    @FXML
    private ListView<HBox> ontologyListView; // ListView to display ontology lines with error highlighting
    @FXML
    private Label statusLabel; // Label to display status messages during verification

    /**
     * Initializes the controller by setting up empty lists for ontology lines,
     * backing up ontology data, and preparing the ontology object.
     */
    @FXML
    public void initialize() {
        ontologyLines = new ArrayList<>();
        ontologyBackup = new ArrayList<>();
        ontologyData = new Ontology();
    }

    /**
     * Loads an ontology file selected by the user, parses it, and displays its contents in the UI.
     * Supported file types include XML, RDF, and OWL formats.
     */
    @FXML
    private void loadOntologyFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ontology Files", "*.xml", "*.rdf", "*.owl"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                ontologyData.loadXmlFromFile(file.getAbsolutePath());
            } catch (IOException e) {
                AppLogger.severe(e.getMessage());
            }

            ontologyData.setOntologyName(file.getName().replace(".xml", "").replace(".rdf", "").replace(".owl", ""));
            ontologyData.setOntologyExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));

            if (ontologyData.getXmlData() != null && !ontologyData.getXmlData().isEmpty()) {
                ontologyLines = new ArrayList<>(List.of(ontologyData.getXmlData().split("\\n")));
                highlightErrors(new ArrayList<>());
            } else {
                AppLogger.severe("Ontology file is empty or could not be loaded.");
            }

            Middleware.getInstance().setOntologyInput(ontologyData);
        } else {
            AppLogger.severe("Ontology file is null");
        }
    }

    /**
     * Verifies the loaded ontology and performs necessary transformations if the ontology is invalid.
     * The status label is updated with the verification status.
     */
    @FXML
    private void verifyFile() {
        try {
            StringBuilder data = new StringBuilder();
            for (String line : ontologyLines) {
                data.append(line).append("\n");
            }
            ontologyData.setXmlData(data.toString());

            Middleware.getInstance().setOntologyInput(ontologyData);

            statusLabel.setText("Verification started...");
            if (!Middleware.getInstance().loadStructure())
                throw new Exception("Failed to load structure");

            boolean result = Middleware.getInstance().verifyOntology();

            // Backup current ontology state before transformation
            ontologyBackup.clear();
            ontologyBackup.addAll(ontologyLines);

            if (result)
                statusLabel.setText("Ontology is valid.");
            else {
                statusLabel.setText("Ontology is not valid.");

                // Transforming the ontology if it's invalid
                if (!Middleware.getInstance().transformOntology())
                    statusLabel.setText("There is a syntax error in the ontology loaded!");
                List<ErrorInfo> errors = Middleware.getErrors();
                highlightErrors(errors);
            }
        } catch (Exception e) {
            AppLogger.severe(e.getMessage());
            statusLabel.setText(e.getMessage());
        }
    }

    /**
     * Highlights the errors in the ontology and displays them in the ListView.
     * Each error block is displayed with a "Fix" button that opens a diff dialog to correct the error.
     *
     * @param errorList The list of errors to be highlighted in the ontology lines.
     */
    @FXML
    public void highlightErrors(List<ErrorInfo> errorList) {
        ontologyListView.getItems().clear(); // Clear previous error highlights

        List<Integer> processedLines = new ArrayList<>();

        for (int i = 0; i < ontologyLines.size(); i++) {
            if (processedLines.contains(i)) {
                continue; // Skip lines that were already grouped into an error block
            }

            String line = ontologyLines.get(i);
            HBox hbox = new HBox(10);
            VBox block = new VBox();
            Label lineLabel = new Label(line);

            boolean isErrorBlock = false;
            String errorMessage = "";
            String errorDetails = "";
            int blockStart = i, blockEnd = i;

            // Check if the current line starts an error block
            for (ErrorInfo error : errorList) {
                if (i >= (error.startLine() - 1) && i <= (error.endLine() - 1)) {
                    isErrorBlock = true;
                    errorMessage = error.errorMessage();
                    errorDetails = error.elementDetails();
                    blockStart = error.startLine() - 1;
                    blockEnd = error.endLine() - 1;
                    break;
                }
            }

            // Collect all lines in this error block
            for (int j = blockStart; j <= blockEnd; j++) {
                if (j < ontologyLines.size()) {
                    block.getChildren().add(new Label(ontologyLines.get(j)));
                    processedLines.add(j);
                }
            }

            // Style the error block
            if (isErrorBlock) {
                block.setStyle("-fx-background-color: rgba(255, 0, 0, 0.2); -fx-padding: 5; -fx-border-color: red;");
                Button errorButton = new Button("Fix");
                int finalStart = blockStart;
                int finalEnd = blockEnd;
                String finalErrorMessage = errorMessage;
                String finalErrorDetails = errorDetails;

                errorButton.setOnAction(e -> openDiffDialog(finalStart, finalEnd, finalErrorMessage, finalErrorDetails));
                hbox.getChildren().addAll(block, errorButton);
            } else {
                hbox.getChildren().add(block);
            }

            ontologyListView.getItems().add(hbox);
        }
    }

    /**
     * Opens a popup dialog displaying the differences for a selected error block, allowing the user
     * to fix the error by modifying the corresponding ontology lines.
     *
     * @param startLineIndex The starting line index of the error block.
     * @param endLineIndex The ending line index of the error block.
     * @param errorMessage The message describing the error.
     * @param errorDetails Additional details about the error.
     */
    private void openDiffDialog(int startLineIndex, int endLineIndex, String errorMessage, String errorDetails) {
        Popup popup = new Popup();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Popup_layout.fxml"));

        try {
            VBox popupContent = loader.load();
            popup.getContent().add(popupContent);
        } catch (IOException e) {
            AppLogger.severe("Error loading popup layout: " + e.getMessage());
            return;
        }

        PopupController controller = loader.getController();
        String blockText = String.join("\n", ontologyLines.subList(startLineIndex, Math.min(endLineIndex + 1, ontologyLines.size())));
        controller.setPopupContext(startLineIndex, endLineIndex, blockText, errorMessage, errorDetails, popup, this);

        // Position the popup near the first line of the error block
        if (startLineIndex >= ontologyListView.getItems().size()) {
            AppLogger.severe("Invalid block index for popup: " + startLineIndex);
            return;
        }

        HBox sourceHBox = ontologyListView.getItems().get(startLineIndex);
        Button sourceButton = (Button) sourceHBox.getChildren().get(1);

        double x = sourceButton.localToScreen(sourceButton.getBoundsInLocal()).getMinX();
        double y = sourceButton.localToScreen(sourceButton.getBoundsInLocal()).getMinY();

        popup.setAnchorX(x);
        popup.setAnchorY(y);
        popup.show(sourceButton, x, y);
    }

    /**
     * Clears all the ontology data, including the list of lines and any error highlights.
     */
    @FXML
    private void clearAll() {
        this.ontologyLines.clear();
        this.ontologyListView.getItems().clear();
        this.ontologyBackup.clear();
        Middleware.getInstance().setOntologyInput(null);
    }

    /**
     * Returns a copy of the list of ontology lines.
     *
     * @return A list of ontology lines.
     */
    public List<String> getOntologyLines() {
        return new ArrayList<>(ontologyLines);
    }

    /**
     * Sets the list of ontology lines from a given list.
     *
     * @param ontologyLines The list of ontology lines to be set.
     */
    public void setOntologyLines(List<String> ontologyLines) {
        this.ontologyLines.clear();
        this.ontologyLines.addAll(ontologyLines);
    }

    /**
     * Returns a list of errors that occurred during the ontology verification process.
     *
     * @return A list of errors encountered during verification.
     */
    public List<ErrorInfo> getErrors() {
        return errors;
    }
}