package com.jataxmltransformer.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SaveController {
    @FXML
    private Label numClassesLabel;
    @FXML
    private Label numAttributesLabel;
    @FXML
    private Label numErrorsFixedLabel;

    @FXML
    public void initialize() {

    }

    /**
     * Updates the statistics displayed in the UI.
     */
    private void updateStats(int numClasses, int numAttributes, int numErrorsFixed) {
        numClassesLabel.setText(String.valueOf(numClasses));
        numAttributesLabel.setText(String.valueOf(numAttributes));
        numErrorsFixedLabel.setText(String.valueOf(numErrorsFixed));
    }

    @FXML
    private void saveOntologyFile() {
        // TODO
    }
}
