package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.utilities.FileHandler;
import com.jataxmltransformer.logs.AppLogger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling namespace management within the application.
 * Provides functionalities to add, remove, load, and save namespaces.
 */
public class NamespacesHandlerController {
    private final List<String> namespaces = new ArrayList<>();
    @FXML
    private ListView<String> namespaceListView;
    private int namespaceCount = 0;

    /**
     * Initializes the controller and configures the namespace list for editing.
     */
    @FXML
    private void initialize() {
        setupNamespaceEditing();
    }

    /**
     * Configures the namespace list to allow in-place editing.
     */
    private void setupNamespaceEditing() {
        namespaceListView.setEditable(true);
        namespaceListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));
        namespaceListView.setOnEditCommit(event -> {
            namespaceListView.getItems().set(event.getIndex(), event.getNewValue());
            updateNamespaces();
        });
    }

    /**
     * Updates the internal list of namespaces with the current list view items.
     */
    private void updateNamespaces() {
        namespaces.clear();
        namespaces.addAll(namespaceListView.getItems());
    }

    /**
     * Adds a new namespace to the list with a default value.
     */
    @FXML
    private void addNamespace() {
        namespaceCount++;
        namespaces.add("New Namespace" + namespaceCount + "- http://example.com");
        refreshNamespaceListView();
    }

    /**
     * Removes the selected namespace from the list.
     */
    @FXML
    private void removeNamespace() {
        String namespace = namespaceListView.getSelectionModel().getSelectedItem();
        if (namespace != null) {
            namespaces.remove(namespace);
            namespaceListView.getItems().remove(namespace);
        }
    }

    /**
     * Loads namespaces from a JSON file and updates the namespace list.
     */
    @FXML
    private void loadNamespaces() {
        try {
            String jsonString = FileHandler.loadFile("JSON File", "*.json");
            if (jsonString == null) {
                CustomAlert.showError("File selection error", "No file selected.");
                return;
            }

            JSONArray JSONContent = new JSONArray(jsonString);
            List<String> namespacesTemp = new ArrayList<>();

            for (int i = 0; i < JSONContent.length(); i++) {
                JSONObject obj = JSONContent.getJSONObject(i);
                if (obj.has("namespace")) namespacesTemp.add(obj.getString("namespace"));
                else throw new JSONException("Missing name or uri in object: " + obj);
            }

            namespaces.addAll(namespacesTemp);
            refreshNamespaceListView();

        } catch (JSONException e) {
            CustomAlert.showError("Error while reading file.", "Error while reading file: "
                    + e.getMessage());
            AppLogger.severe("Error while reading file: " + e.getMessage());
        }
    }

    /**
     * Refreshes the ListView with the latest namespace data.
     */
    private void refreshNamespaceListView() {
        namespaceListView.getItems().clear();
        namespaceListView.setItems(FXCollections.observableArrayList(namespaces));
    }

    /**
     * Saves the current list of namespaces to a JSON file.
     */
    @FXML
    private void saveNamespaces() {
        JSONArray JSONContent = new JSONArray();
        namespaces.forEach(namespace -> {
            JSONObject obj = new JSONObject();
            obj.put("namespace", namespace);
            JSONContent.put(obj);
        });

        FileHandler.saveFile("JSON File", "*.json", JSONContent);
    }
}