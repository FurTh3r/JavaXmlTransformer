package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import com.jataxmltransformer.logs.AppLogger;
import com.jataxmltransformer.middleware.Middleware;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

    private final List<ErrorInfo> errors = new ArrayList<>();

    private List<String> ontologyLines;
    private Ontology ontologyData;

    @FXML
    private ListView<HBox> ontologyListView; // ListView to display ontology lines with error highlighting
    @FXML
    private ListView<String> ontologyTransformedListView; // ListView to display ontology lines transformed
    @FXML
    private Label statusLabel; // Label to display status messages during verification

    /**
     * Initializes the controller by setting up empty lists for ontology lines,
     * backing up ontology data, and preparing the ontology object.
     */
    @FXML
    public void initialize() {
        ontologyLines = new ArrayList<>();
        ontologyData = new Ontology();
        ontologyListView.onScrollToProperty().bindBidirectional(ontologyTransformedListView.onScrollToProperty());
    }

    /**
     * Loads an ontology file selected by the user, parses it, and displays its contents in the UI.
     * Supported file types include XML, RDF, and OWL formats.
     */
    @FXML
    private void loadOntologyFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser
                .ExtensionFilter("Ontology Files", "*.xml", "*.rdf", "*.owl"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                ontologyData.loadXmlFromFile(file.getAbsolutePath());
            } catch (IOException e) {
                AppLogger.severe(e.getMessage());
                CustomAlert.showError("Error", "Error loading ontology file: " + e.getMessage());
                return;
            }

            ontologyData.setOntologyName(file.getName().replace(".xml", "")
                    .replace(".rdf", "").replace(".owl", ""));
            ontologyData.setOntologyExtension(file.getName().substring(file.getName()
                    .lastIndexOf(".") + 1));

            if (ontologyData.getXmlData() != null && !ontologyData.getXmlData().isEmpty()) {
                ontologyLines = new ArrayList<>(List.of(ontologyData.getXmlData().split("\\n")));
                highlightErrors(new ArrayList<>());
            } else {
                AppLogger.severe("Ontology file is empty or could not be loaded.");
                CustomAlert.showError("Error", "Ontology file is empty or could not be loaded.");
                return;
            }

            Middleware.getInstance().setOntologyInput(ontologyData);

            // Clearing the transformed ListView
            ontologyTransformedListView.getItems().clear();
        } else {
            AppLogger.severe("Ontology file is null");
            CustomAlert.showError("Error", "Ontology file is null.");
        }
    }

    /**
     * Verifies the loaded ontology and performs the necessary transformations if the ontology is invalid.
     * The status label is updated with the verification status.
     */
    @FXML
    private void verifyFile() {
        // Clearing the transformed ListView
        ontologyTransformedListView.getItems().clear();
        try {
            if (ontologyLines.isEmpty()) {
                statusLabel.setText("The ontology to verify cannot be empty!");
                return;
            }

            StringBuilder data = new StringBuilder();
            for (String line : ontologyLines)
                data.append(line).append("\n");

            ontologyData.setXmlData(data.toString());

            Middleware.getInstance().setOntologyInput(ontologyData);

            statusLabel.setText("Verification started...");
            if (!Middleware.getInstance().loadStructure())
                throw new Exception("Failed to load structure");

            boolean result = Middleware.getInstance().verifyOntology();

            statusLabel.getStyleClass().removeAll("status-success", "status-error", "status-warning");
            if (result) {
                statusLabel.getStyleClass().add("status-success");
                statusLabel.setText("Ontology is valid.");
                ontologyListView.setStyle("-fx-background-color: green;");
            } else {
                statusLabel.getStyleClass().add("status-error");
                statusLabel.setText("Ontology is not valid.");
                ontologyListView.setStyle("-fx-background-color: #e6f2ff;");

                // Transforming the ontology if it's invalid
                if (!Middleware.getInstance().transformOntology()) {
                    statusLabel.getStyleClass().removeAll("status-success", "status-error", "status-warning");
                    statusLabel.getStyleClass().add("status-warning");
                    statusLabel.setText("There is a syntax error in the ontology loaded!");
                    ontologyListView.setStyle("-fx-background-color: orange;");
                }
                List<ErrorInfo> errors = Middleware.getErrors();
                setTransformedOntologyLines();
                highlightErrors(errors);
            }
        } catch (Exception e) {
            AppLogger.severe(e.getMessage());
            CustomAlert.showError("Error", "Error verifying ontology: " + e.getMessage());
        }
    }

    /**
     * Processes and sets the list of ontology lines that have undergone transformation.
     * This method updates the internal state of the controller to reflect the transformed ontology lines
     */
    private void setTransformedOntologyLines() {
        try {
            Ontology transformedOntology = Middleware.getInstance().getOntologyOutput();
            if (transformedOntology.isEmpty() || transformedOntology.getXmlData() == null) {
                ontologyTransformedListView.getItems().clear();
                return;
            }
            transformedOntology = XMLFormatter.formatOntology(transformedOntology, true);
            ontologyTransformedListView.getItems().clear();
            ontologyTransformedListView.getItems().addAll(transformedOntology.getXmlData().split("\\n"));
            ontologyTransformedListView.setCellFactory(TextFieldListCell.forListView());
            ontologyTransformedListView.setEditable(false);
        } catch (Exception e) {
            AppLogger.severe(e.getMessage());
            CustomAlert.showError("Error", "Error setting transformed ontology lines: " + e.getMessage());
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
            if (processedLines.contains(i))
                continue; // Skip lines that were already grouped into an error block

            HBox hbox = new HBox(10);
            VBox block = new VBox();
            HBox.setHgrow(block, Priority.ALWAYS);

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
                    if (isErrorBlock) {
                        // Instead of a TextField, create a Label for error messages
                        Label errorLabel = new Label(ontologyLines.get(j));
                        errorLabel.setStyle("-fx-background-color: transparent; " +
                                "-fx-border-color: transparent; " +
                                "-fx-padding: 2px 5px; " +
                                "-fx-font-size: 12px; -fx-text-fill: red;");
                        block.getChildren().add(errorLabel);
                    } else {
                        // Add TextField if there is no error
                        TextField textField = new TextField(ontologyLines.get(j));
                        HBox.setHgrow(textField, Priority.ALWAYS); // Ensures horizontal growth
                        textField.setStyle("-fx-background-color: transparent; " +
                                "-fx-border-color: transparent; " +
                                "-fx-padding: 2px 5px; " +
                                "-fx-font-size: 12px;");

                        // Add listener to update ontologyLines when the text is changed
                        int finalJ = j;
                        textField.textProperty().addListener((_, _, newValue)
                                -> ontologyLines.set(finalJ, newValue));

                        block.getChildren().add(textField);
                    }
                    processedLines.add(j);
                }
            }

            // Style the error block
            if (isErrorBlock) {
                block.setStyle("-fx-background-color: rgba(255, 0, 0, 0.2); -fx-padding: 5; -fx-border-color: red;");

                // Create the "Fix" button
                Button errorButton = new Button("Fix");
                errorButton.getStyleClass().add("button-blue");
                int finalStart = blockStart;
                int finalEnd = blockEnd;
                String finalErrorMessage = errorMessage;
                String finalErrorDetails = errorDetails;

                errorButton.setOnAction(_ ->
                        openDiffDialog(finalStart, finalEnd, finalErrorMessage, finalErrorDetails));
                hbox.getChildren().addAll(block, errorButton);
            } else
                hbox.getChildren().add(block);

            ontologyListView.getItems().add(hbox);
        }
    }

    /**
     * Opens a popup dialog displaying the differences for a selected error block, allowing the user
     * to fix the error by modifying the corresponding ontology lines.
     *
     * @param startLineIndex The starting line index of the error block.
     * @param endLineIndex   The ending line index of the error block.
     * @param errorMessage   The message describing the error.
     * @param errorDetails   Additional details about the error.
     */
    private void openDiffDialog(int startLineIndex, int endLineIndex, String errorMessage, String errorDetails) {
        Popup popup = new Popup();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Popup_layout.fxml"));

        try {
            VBox popupContent = loader.load();
            popup.getContent().add(popupContent);
        } catch (IOException e) {
            AppLogger.severe("Error loading popup layout: " + e.getMessage());
            CustomAlert.showError("Error", "Error loading popup layout: " + e.getMessage());
            return;
        }

        PopupController controller = loader.getController();
        String blockText = String.join("\n", ontologyLines
                .subList(startLineIndex, Math.min(endLineIndex + 1, ontologyLines.size())));
        controller.setPopupContext(startLineIndex, endLineIndex, blockText,
                errorMessage, errorDetails, popup, this);

        // Position the popup near the first line of the error block
        if (startLineIndex >= ontologyListView.getItems().size()) {
            AppLogger.severe("Invalid block index for popup: " + startLineIndex);
            CustomAlert.showError("Error", "Invalid block index for popup, check Logs for details.");
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