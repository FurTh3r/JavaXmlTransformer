package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.utilities.FileHandler;
import com.jataxmltransformer.logic.utilities.MyPair;
import com.jataxmltransformer.logs.AppLogger;
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

public class StructureHandlerController {
    private final List<String> attributes = new ArrayList<>();
    private final List<String> classes = new ArrayList<>();
    private final List<String> structure = new ArrayList<>();
    @FXML
    private ListView<String> structureListView;
    @FXML
    private ListView<MyPair<String, String>> classAttributesListView;
    @FXML
    private ComboBox<String> typeOfElement;
    private int structureCount = 0;
    private int classAttributeCount = 0;

    @FXML
    public void initialize() {
        setupStructureTypeOptions();
        setupStructureEditing();
        setupClassesAttributesEditing();
    }

    /**
     * Configures the namespace list to allow in-place editing.
     */
    private void setupClassesAttributesEditing() {
        classAttributesListView.setEditable(true);
        classAttributesListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(MyPair<String, String> object) {
                return object.getFirst() + " - " + object.getSecond();
            }

            @Override
            public MyPair<String, String> fromString(String string) {
                return new MyPair<>(string.split("-")[0], string.split("-")[1]);
            }
        }));
        classAttributesListView.setOnEditCommit(event -> {
            classAttributesListView.getItems().set(event.getIndex(), event.getNewValue());
            upgradeClassAttributes();
        });
    }

    /**
     * Configures the structure list to allow in-place editing.
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
            upgradeStructure();
        });
    }

    /**
     * Populates the structure type ComboBox with predefined options.
     */
    private void setupStructureTypeOptions() {
        ObservableList<String> structureTypes = FXCollections.observableArrayList("Class", "Attribute");
        typeOfElement.setItems(structureTypes);
        typeOfElement.getSelectionModel().select(0);
    }

    private void upgradeClassAttributes() {
        classes.clear();
        attributes.clear();
        classAttributesListView.getItems().forEach(element -> {
            if (element.getSecond().equals("Class")) classes.add(element.getFirst());
            else attributes.add(element.getFirst());
        });
    }

    private void upgradeStructure() {
        structure.clear();
        structure.addAll(structureListView.getItems());
    }

    @FXML
    private void addStructureLine() {
        structureCount++;
        structureListView.getItems().add("type name" + structureCount);
        structure.add("type name" + structureCount);
    }

    @FXML
    private void removeStructureLine() {
        String removedElement = structureListView.getSelectionModel().getSelectedItem();
        if (removedElement != null) {
            structure.remove(removedElement);
            structureListView.getItems().remove(removedElement);
        }
    }

    @FXML
    private void addClassAttributeLine() {
        classAttributeCount++;
        classAttributesListView.getItems().add(new MyPair<>("name" + classAttributeCount, typeOfElement.getSelectionModel().getSelectedItem()));

        if (typeOfElement.getSelectionModel().getSelectedItem().equals("Class"))
            classes.add("name" + classAttributeCount);
        else attributes.add("name" + classAttributeCount);
    }

    @FXML
    private void removeClassAttributeLine() {
        MyPair<String, String> selectedAttribute = classAttributesListView.getSelectionModel().getSelectedItem();

        if (selectedAttribute != null) {
            if (selectedAttribute.getSecond().equals("Class")) classes.remove(selectedAttribute.getFirst());
            else attributes.remove(selectedAttribute.getFirst());
            classAttributesListView.getItems().remove(selectedAttribute);
        }
    }

    @FXML
    private void loadStructure() {
        try {
            // Load the JSON content from the file
            String jsonString = FileHandler.loadFile("JSON File", "*.json");
            if (jsonString == null) {
                CustomAlert.showError("File Error", "No file selected");
                return;
            }

            // Parse the JSON content
            JSONObject JSONContent = new JSONObject(jsonString);

            // Convert JSON arrays to Java lists
            JSONArray structureArray = JSONContent.getJSONArray("structure");
            JSONArray classesArray = JSONContent.getJSONArray("classes");
            JSONArray attributesArray = JSONContent.getJSONArray("attributes");

            structure.clear();
            classes.clear();
            attributes.clear();

            for (int i = 0; i < structureArray.length(); i++) {
                structure.add(structureArray.getString(i));
            }
            for (int i = 0; i < classesArray.length(); i++) {
                classes.add(classesArray.getString(i));
            }
            for (int i = 0; i < attributesArray.length(); i++) {
                attributes.add(attributesArray.getString(i));
            }

            // Convert to MyPair and update UI
            List<MyPair<String, String>> items = new ArrayList<>();
            classes.forEach(element -> items.add(new MyPair<>(element, "Class")));
            attributes.forEach(element -> items.add(new MyPair<>(element, "Attribute")));

            structureListView.getItems().setAll(structure);
            classAttributesListView.setItems(FXCollections.observableArrayList(items));

        } catch (JSONException e) {
            CustomAlert.showError("Error while reading file.", "Error while reading file");
            AppLogger.severe("Error while reading file: " + e.getMessage());
        }
    }

    @FXML
    private void saveStructure() {
        if (!structure.isEmpty() || !classes.isEmpty() || !attributes.isEmpty()) {
            JSONObject JSONContent = new JSONObject();
            JSONContent.put("structure", new JSONArray(structure));
            JSONContent.put("classes", new JSONArray(classes));
            JSONContent.put("attributes", new JSONArray(attributes));

            FileHandler.saveFile("JSON File", "*.json", JSONContent.toString(4)); // Pretty print JSON
        }
    }
}
