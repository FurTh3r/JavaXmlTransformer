package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.utilities.FileHandler;
import com.jataxmltransformer.logic.utilities.MyPair;
import com.jataxmltransformer.logs.AppLogger;
import com.jataxmltransformer.middleware.Middleware;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
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
        classAttributesListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(MyPair<String, String> object) {
                return object.getFirst() + " - " + object.getSecond();
            }

            @Override
            public MyPair<String, String> fromString(String string) {
                String[] parts = string.split("-");
                return new MyPair<>(parts[0].trim(), parts[1].trim());
            }
        }));
        classAttributesListView.setOnEditCommit(event -> {
            classAttributesListView.getItems().set(event.getIndex(), event.getNewValue());
            updateClassAttributes();
        });
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
}
