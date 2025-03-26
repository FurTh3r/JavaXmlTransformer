package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.utilities.FileHandler;
import com.jataxmltransformer.logic.utilities.MyPair;
import com.jataxmltransformer.logs.AppLogger;
import com.jataxmltransformer.middleware.Middleware;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing the structure and attributes of a system.
 * Handles UI interactions for defining structures, attributes, and saving/loading them.
 */
public class StructureHandlerController {

    @FXML
    private ListView<String> structureListView;
    @FXML
    private ListView<MyPair<String, String>> classAttributesListView;
    @FXML
    private ComboBox<String> typeOfElement;

    private int structureCount = 0;
    private int classAttributeCount = 0;

    /**
     * Initializes UI components and sets up editing capabilities.
     * Configures the structure and class attributes list views and populates
     * the ComboBox with predefined structure element types.
     */
    @FXML
    public void initialize() {
        setupStructureTypeOptions();
        setupStructureEditing();
        setupClassAttributesEditing();
    }

    /**
     * Configures the class attributes list to allow in-place editing.
     * Sets up how the list displays class and attribute pairs and handles
     * editing events to update the underlying data.
     */
    private void setupClassAttributesEditing() {
        classAttributesListView.setEditable(true);
        classAttributesListView.setCellFactory(_ -> new ClassAttributeCell());
    }

    /**
     * Configures the structure list to allow in-place editing.
     * Handles the editing of structure lines and updates the structure list in memory.
     */
    private void setupStructureEditing() {
        structureListView.setEditable(true);
        structureListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));
        structureListView.setOnEditCommit(event -> {
            structureListView.getItems().set(event.getIndex(), event.getNewValue());
            updateStructure();
        });
    }

    /**
     * Populates the structure type ComboBox with predefined options ("Class", "Attribute").
     * This allows the user to select the type of structure element (class or attribute).
     */
    private void setupStructureTypeOptions() {
        ObservableList<String> structureTypes = FXCollections.observableArrayList("Class", "Attribute");
        typeOfElement.setItems(structureTypes);
        typeOfElement.getSelectionModel().select(0);
    }

    /**
     * Updates class and attribute lists in the Middleware instance.
     * The lists are cleared and repopulated with the current class and attribute data.
     */
    private void updateClassAttributes() {
        Middleware middleware = Middleware.getInstance();
        middleware.getClasses().clear();
        middleware.getAttributes().clear();
        classAttributesListView.getItems().forEach(element -> {
            if ("Class".equals(element.getSecond())) middleware.getClasses().add(element.getFirst());
            else middleware.getAttributes().add(element.getFirst());
        });
    }

    /**
     * Updates the structure list in the Middleware instance.
     * The list of structures is cleared and repopulated with the current structure data.
     */
    private void updateStructure() {
        Middleware middleware = Middleware.getInstance();
        middleware.getStructure().clear();
        middleware.getStructure().addAll(structureListView.getItems());
    }

    /**
     * Adds a new line to the structure list with a default name.
     * The new line is also added to the structure data in Middleware.
     */
    @FXML
    private void addStructureLine() {
        String newElement = "type name" + (++structureCount);
        structureListView.getItems().add(newElement);
        Middleware.getInstance().getStructure().add(newElement);
    }

    /**
     * Removes the selected line from the structure list.
     * The removed line is also deleted from the structure data in Middleware.
     */
    @FXML
    private void removeStructureLine() {
        String selectedElement = structureListView.getSelectionModel().getSelectedItem();
        if (selectedElement != null) {
            Middleware.getInstance().getStructure().remove(selectedElement);
            structureListView.getItems().remove(selectedElement);
        }
    }

    /**
     * Adds a new class or attribute line to the list, based on the type selected
     * in the ComboBox.
     * The new element is also added to the corresponding class or
     * attribute list in Middleware.
     */
    @FXML
    private void addClassAttributeLine() {
        String name = "name" + (++classAttributeCount);
        String type = typeOfElement.getSelectionModel().getSelectedItem();
        classAttributesListView.getItems().add(new MyPair<>(name, type));

        if ("Class".equals(type)) Middleware.getInstance().getClasses().add(name);
        else Middleware.getInstance().getAttributes().add(name);
    }

    /**
     * Removes the selected class or attribute line from the list.
     * The removed element is also deleted from the corresponding class or
     * attribute list in Middleware.
     */
    @FXML
    private void removeClassAttributeLine() {
        MyPair<String, String> selectedAttribute = classAttributesListView.getSelectionModel().getSelectedItem();
        if (selectedAttribute != null) {
            if ("Class".equals(selectedAttribute.getSecond()))
                Middleware.getInstance().getClasses().remove(selectedAttribute.getFirst());
            else Middleware.getInstance().getAttributes().remove(selectedAttribute.getFirst());
            classAttributesListView.getItems().remove(selectedAttribute);
        }
    }

    /**
     * Loads a structure from a JSON file and updates UI components accordingly.
     * The file content is parsed, and the structure, classes, and attributes are
     * updated in the Middleware instance.
     */
    @FXML
    private void loadStructure() {
        try {
            String jsonString = FileHandler.loadFile("JSON File", "*.json");
            if (jsonString == null) {
                CustomAlert.showError("File Error", "No file selected");
                return;
            }

            JSONObject JSONContent = new JSONObject(jsonString);
            Middleware middleware = Middleware.getInstance();
            middleware.getStructure().clear();
            middleware.getClasses().clear();
            middleware.getAttributes().clear();

            middleware.getStructure().addAll(JSONContent.getJSONArray("structure").toList().stream()
                    .map(Object::toString).toList());
            middleware.getClasses().addAll(JSONContent.getJSONArray("classes").toList().stream()
                    .map(Object::toString).toList());
            middleware.getAttributes().addAll(JSONContent.getJSONArray("attributes").toList().stream()
                    .map(Object::toString).toList());

            List<MyPair<String, String>> items = new ArrayList<>();
            middleware.getClasses().forEach(element -> items.add(new MyPair<>(element, "Class")));
            middleware.getAttributes().forEach(element -> items.add(new MyPair<>(element, "Attribute")));

            structureListView.setItems(FXCollections.observableArrayList(middleware.getStructure()));
            classAttributesListView.setItems(FXCollections.observableArrayList(items));
        } catch (JSONException e) {
            CustomAlert.showError("Error while reading file.", "Invalid JSON format");
            AppLogger.severe("Error while reading file: " + e.getMessage());
        }
    }

    /**
     * Saves the current structure to a JSON file.
     * The structure, classes, and attributes are written to the file in a formatted JSON structure.
     */
    @FXML
    private void saveStructure() {
        if (!Middleware.getInstance().getStructure().isEmpty() || !Middleware.getInstance().getClasses().isEmpty()
                || !Middleware.getInstance().getAttributes().isEmpty()) {
            JSONObject JSONContent = new JSONObject();
            JSONContent.put("structure", new JSONArray(Middleware.getInstance().getStructure()));
            JSONContent.put("classes", new JSONArray(Middleware.getInstance().getClasses()));
            JSONContent.put("attributes", new JSONArray(Middleware.getInstance().getAttributes()));

            // Pretty print JSON
            FileHandler.saveFile("JSON File", "*.json", JSONContent.toString(4));
        } else {
            CustomAlert.showError("No data to save", """
                    No data to save:
                    Please add at least one structure line, class, or attribute.""");
            AppLogger.severe("No data to save");
        }
    }

    /**
     * Clears all class and attribute data from the lists and the Middleware instance.
     */
    @FXML
    private void clearAllClassAttribute() {
        Middleware.getInstance().getClasses().clear();
        Middleware.getInstance().getAttributes().clear();
        classAttributesListView.getItems().clear();
    }

    /**
     * Clears all structure data from the list and the Middleware instance.
     */
    @FXML
    private void clearAllStructure() {
        Middleware.getInstance().getStructure().clear();
        structureListView.getItems().clear();
    }

    /**
     * Custom ListCell for displaying and editing class and attribute pairs in a ListView.
     * This class handles in-place editing of class and attribute names, as well as
     * displaying the type (class or attribute) alongside the name.
     */
    public static class ClassAttributeCell extends ListCell<MyPair<String, String>> {
        private final HBox hBox = new HBox();
        private final TextField textField = new TextField();
        private final Label typeLabel = new Label();

        public ClassAttributeCell() {
            textField.setPromptText("Enter name");

            // Remove border and reduce height
            textField.setStyle("-fx-background-color: transparent; " +
                    "-fx-border-color: transparent; " +
                    "-fx-padding: 2px 5px; " +
                    "-fx-font-size: 12px;");

            typeLabel.setStyle("-fx-padding: 0 10px; -fx-font-weight: bold;");

            HBox.setHgrow(textField, Priority.ALWAYS);
            hBox.setSpacing(5);
            hBox.setAlignment(Pos.CENTER_LEFT); // Align items vertically

            // Push typeLabel to the right
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            hBox.getChildren().addAll(textField, spacer, typeLabel);

            // Commit text on Enter or loss of focus
            textField.setOnAction(event -> commitEdit(new MyPair<>(textField.getText(), getItem().getSecond())));
            textField.focusedProperty().addListener((_, _, newVal) -> {
                if (!newVal && isEditing()) commitEdit(new MyPair<>(textField.getText(), getItem().getSecond()));
            });

            // Enable editing only on double click
            setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !isEmpty()) {
                    startEdit();
                }
            });
        }

        /**
         * Updates the item displayed in the ListCell based on the current editing state.
         * If the cell is being edited, a text field is displayed. If not, the name and type
         * are displayed in a horizontal box.
         *
         * @param item  The item to be displayed in the ListCell.
         * @param empty A boolean indicating whether the item is empty.
         */
        @Override
        protected void updateItem(MyPair<String, String> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                if (isEditing()) {
                    textField.setText(item.getFirst());
                    setGraphic(hBox);
                } else {
                    HBox displayBox = new HBox(new Label(item.getFirst()), new Label("  "), typeLabel);
                    HBox.setHgrow(displayBox, Priority.ALWAYS);
                    displayBox.setAlignment(Pos.CENTER_LEFT);

                    // Push typeLabel to the right
                    HBox spacer = new HBox();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    displayBox.getChildren().add(1, spacer);

                    setGraphic(displayBox);
                }
                typeLabel.setText(item.getSecond());
            }
        }

        /**
         * Begins the editing of the current item. A text field is displayed to allow the user
         * to edit the name of the class or attribute.
         */
        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                textField.setText(getItem().getFirst());
                setGraphic(hBox);
                textField.requestFocus();
            }
        }

        /**
         * Cancels the current editing operation and restores the display box with the item.
         */
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setGraphic(getDisplayBox(getItem()));
        }

        /**
         * Commits the changes made during editing and updates the display box with the new item.
         *
         * @param newValue The new value for the item being edited.
         */
        @Override
        public void commitEdit(MyPair<String, String> newValue) {
            super.commitEdit(newValue);
            setGraphic(getDisplayBox(newValue));
        }

        /**
         * Creates a display box containing the class or attribute name and its type.
         *
         * @param item The item to be displayed in the box.
         * @return A HBox containing the name and type of the item.
         */
        private HBox getDisplayBox(MyPair<String, String> item) {
            Label nameLabel = new Label(item.getFirst());
            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox box = new HBox(nameLabel, spacer, typeLabel);
            box.setAlignment(Pos.CENTER_LEFT);
            return box;
        }
    }
}
