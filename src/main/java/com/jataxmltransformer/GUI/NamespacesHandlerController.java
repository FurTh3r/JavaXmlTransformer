package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.utilities.FileHandler;
import com.jataxmltransformer.logs.AppLogger;
import com.jataxmltransformer.middleware.Middleware;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private final List<String> userNamespaces = new ArrayList<>();
    private final List<String> defaultNamespaces = Middleware.getInstance().getDefaultNamespaces();

    @FXML
    private ListView<String> namespaceListView;
    private int namespaceCount = 0;

    /**
     * Initializes the controller, sets up the namespace list for editing,
     * and populates the list with default and user-added namespaces.
     */
    @FXML
    private void initialize() {
        setupNamespaceEditing();
        refreshNamespaceListView();
    }

    /**
     * Configures the namespace list to allow in-place editing for user-added namespaces.
     * The default namespaces are displayed in blue and cannot be edited.
     */
    private void setupNamespaceEditing() {
        namespaceListView.setEditable(true);

        namespaceListView.setCellFactory(_ -> new TextFieldListCell<>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object != null ? object : "";
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }) {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Reset styles
                } else {
                    setText(item);
                    if (defaultNamespaces.contains(item)) {
                        setStyle("-fx-text-fill: blue; -fx-font-weight: bold;"); // Blue text for default
                        setEditable(false); // Ensure it's non-editable
                    } else
                        setStyle(""); // Normal styling for user-added namespaces
                }
            }

            @Override
            public void startEdit() {
                if (defaultNamespaces.contains(getItem())) {
                    return; // Prevent editing default namespaces
                }
                super.startEdit();
            }
        });

        namespaceListView.setOnEditCommit(event -> {
            String editedValue = event.getNewValue();
            int index = event.getIndex();

            // Ensure only user-added namespaces can be edited
            if (!defaultNamespaces.contains(namespaceListView.getItems().get(index))) {
                namespaceListView.getItems().set(index, editedValue);
                updateNamespaces();
            }
        });
    }

    /**
     * Updates the internal list of namespaces with the current list view items.
     */
    private void updateNamespaces() {
        Middleware.getInstance().setNamespaces(userNamespaces);
        userNamespaces.clear();
        for (String ns : namespaceListView.getItems()) {
            if (!defaultNamespaces.contains(ns))
                userNamespaces.add(ns);
        }
    }

    /**
     * Adds a new namespace to the list with a default value.
     * The namespace will be named incrementally as "New NamespaceX - <a href="http://example.com">...</a>".
     */
    @FXML
    private void addNamespace() {
        namespaceCount++;
        String newNamespace = "New Namespace" + namespaceCount + " - http://example.com";
        userNamespaces.add(newNamespace);
        Middleware.getInstance().setNamespaces(userNamespaces);
        refreshNamespaceListView();
    }

    /**
     * Removes the selected namespace from the list, but not if it's a default namespace.
     */
    @FXML
    private void removeNamespace() {
        String namespace = namespaceListView.getSelectionModel().getSelectedItem();
        if (namespace != null && !defaultNamespaces.contains(namespace)) {
            userNamespaces.remove(namespace);
            namespaceListView.getItems().remove(namespace);
            Middleware.getInstance().setNamespaces(userNamespaces);
        }
    }

    /**
     * Loads namespaces from a JSON file and updates the namespace list.
     * The JSON file should contain an array of objects, each with a "namespace" key.
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
                else throw new JSONException("Missing namespace key in object: " + obj);
            }

            userNamespaces.addAll(namespacesTemp);
            refreshNamespaceListView();
            Middleware.getInstance().setNamespaces(userNamespaces);

        } catch (JSONException e) {
            CustomAlert.showError("Error while reading file.", "Error while reading file: " + e.getMessage());
            AppLogger.severe("Error while reading file: " + e.getMessage());
        }
    }

    /**
     * Refreshes the ListView with the latest namespace data,
     * combining both default and user-added namespaces.
     */
    private void refreshNamespaceListView() {
        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(defaultNamespaces);
        combinedList.addAll(userNamespaces);

        ObservableList<String> observableNamespaces = FXCollections.observableArrayList(combinedList);
        namespaceListView.setItems(observableNamespaces);
    }

    /**
     * Saves the current list of namespaces to a JSON file.
     * The file will contain an array of objects, each with a "namespace" key.
     */
    @FXML
    private void saveNamespaces() {
        JSONArray JSONContent = new JSONArray();
        userNamespaces.forEach(namespace -> {
            JSONObject obj = new JSONObject();
            obj.put("namespace", namespace);
            JSONContent.put(obj);
        });

        FileHandler.saveFile("JSON File", "*.json", JSONContent);
    }

    /**
     * Clears all user-added namespaces, but keeps the default ones.
     */
    @FXML
    private void clearAll() {
        userNamespaces.clear();
        Middleware.getInstance().setNamespaces(userNamespaces);
        refreshNamespaceListView();
    }
}