package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.utilities.MyPair;
import com.jataxmltransformer.logic.xml.XMLDiffChecker;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import com.jataxmltransformer.logs.AppLogger;
import com.jataxmltransformer.middleware.Middleware;
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

public class LoadVerifyController {

    List<ErrorInfo> errors = new ArrayList<>();
    @FXML
    private ListView<HBox> ontologyListView; // ListView to display ontology lines with buttons
    @FXML
    private Label statusLabel;
    private List<String> ontologyLines;
    private List<String> ontologyBackup;
    private Ontology ontologyData;

    @FXML
    public void initialize() {
        ontologyLines = new ArrayList<>();
        ontologyData = new Ontology();
        ontologyBackup = new ArrayList<>();
    }

    @FXML
    private void loadOntologyFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml", "*.rdf", "*.owl"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                ontologyData.loadXmlFromFile(file.getAbsolutePath());
            } catch (IOException e) {
                AppLogger.severe(e.getMessage());
                // TODO error ?
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

            // Testing if ontology has to be transformed
            boolean result = Middleware.getInstance().verifyOntology();

            // Clear backup before transforming
            ontologyBackup.clear();
            ontologyBackup.addAll(ontologyLines); // Backup the current state

            // Transform ontology if not valid
            if (result)
                statusLabel.setText("Ontology is valid.");
            else {
                statusLabel.setText("Ontology is not valid.");

                // Transforming ontology
                if (!Middleware.getInstance().transformOntology())
                    statusLabel.setText("There is a Syntax error in the Ontology loaded!");
                List<ErrorInfo> errors = Middleware.getErrors();
                highlightErrors(errors);
            }
        } catch (Exception e) {
            AppLogger.severe(e.getMessage());
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void highlightErrors(List<ErrorInfo> errorList) {
        ontologyListView.getItems().clear(); // Clear the ListView

        List<Integer> processedLines = new ArrayList<>();

        for (int i = 0; i < ontologyLines.size(); i++) {
            if (processedLines.contains(i)) {
                continue; // Skip lines that were already grouped into a block
            }

            String line = ontologyLines.get(i);
            HBox hbox = new HBox(10);
            VBox block = new VBox();
            Label lineLabel = new Label(line);

            boolean isErrorBlock = false;
            String errorMessage = "";
            String errorDetails = "";
            int blockStart = i, blockEnd = i;

            // Check if this line starts an error block
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

        // Positioning near the first line of the block
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

    public List<String> getOntologyLines() {
        return new ArrayList<>(ontologyLines);
    }

    public void setOntologyLines(List<String> ontologyLines) {
        this.ontologyLines.clear();
        // Restore lines from MyPair list
        // Only add the string part
        this.ontologyLines.addAll(ontologyLines);
    }
}