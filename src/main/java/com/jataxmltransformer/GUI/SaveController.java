package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import com.jataxmltransformer.logs.AppLogger;
import com.jataxmltransformer.middleware.Middleware;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveController {
    @FXML
    public void initialize() {

    }

    @FXML
    private void saveOntologyFile() {
        File file = null;
        try {
            // Create and configure FileChooser for saving XML file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Ontology");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Ontology", "*.xml"));

            // Show the Save dialog and get the selected file
            file = fileChooser.showSaveDialog(new Stage());

            // Check if a file was selected, if not, abort the operation
            if (file == null) {
                return;  // User canceled the save dialog
            }

            // Get the ontology data and format it
            Ontology ontology = XMLFormatter.formatOntologyNamespaces(Middleware.getInstance().getOntologyOutput(),
                    true);

            if (ontology.getXmlData() == null || ontology.getXmlData().isEmpty()) {
                AppLogger.severe("Ontology data is empty or null");
                CustomAlert.showError("Error", """
                        Ontology data is empty or null:
                        Please verify that the ontology is valid and try again.
                        """);
                return;
            }

            // Write the XML data to the selected file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(ontology.getXmlData());
            }

            // Inform the user of success
            CustomAlert.showInfo("Success", "Ontology file saved successfully.");

        } catch (IOException e) {
            // Handle IOExceptions (e.g., issues with writing to the file)
            AppLogger.severe("Error saving ontology file: " + file.getAbsolutePath());
            CustomAlert.showError("Error", "Error saving ontology file: " + e.getMessage());

        } catch (Exception e) {
            // Catch any other exceptions and log them
            AppLogger.severe("Unexpected error saving ontology file");
            CustomAlert.showError("Error", "Unexpected error saving ontology file: " + e.getMessage());
        }
    }
}
