package com.jataxmltransformer.GUI;

import com.jataxmltransformer.logic.utilities.FileHandler;
import com.jataxmltransformer.logic.utilities.MyPair;
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

public class NamespacesHandlerController {
    private final List<MyPair<String, String>> namespaces = new ArrayList<>();
    @FXML
    private ListView<MyPair<String, String>> namespaceListView;
    private int namespaceCount = 0;

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
            public String toString(MyPair<String, String> object) {
                return object.getFirst() + " - " + object.getSecond();
            }

            @Override
            public MyPair<String, String> fromString(String string) {
                return new MyPair<>(string.split("-")[0], string.split("-")[1]);
            }
        }));
        namespaceListView.setOnEditCommit(event -> {
            namespaceListView.getItems().set(event.getIndex(), event.getNewValue());
            upgradeNamespaces();
        });
    }

    private void upgradeNamespaces() {
        namespaces.clear();
        namespaces.addAll(namespaceListView.getItems());
    }

    @FXML
    private void addNamespace() {
        namespaceCount++;
        namespaces.add(new MyPair<>("New Namespace" + namespaceCount, "http://example.com"));
        namespaceListView.setItems(FXCollections.observableArrayList(namespaces));
    }

    @FXML
    private void removeNamespace() {
        MyPair<String, String> namespace = namespaceListView.getSelectionModel().getSelectedItem();
        if (namespace != null) {
            namespaces.remove(namespace);
            namespaceListView.getItems().remove(namespace);
        }
    }

    @FXML
    private void loadNamespaces() {
        try {
            // Load the JSON content from the file
            String jsonString = FileHandler.loadFile("JSON File", "*.json");
            if (jsonString == null) {
                CustomAlert.showError("File selection error", "No file selected.");
                return;
            }

            // Parse the JSON array directly
            JSONArray JSONContent = new JSONArray(jsonString);
            List<MyPair<String, String>> namespacesTemp = new ArrayList<>();

            for (int i = 0; i < JSONContent.length(); i++) {
                // Access each item as JSONObject
                JSONObject obj = JSONContent.getJSONObject(i);
                if (obj.has("name") && obj.has("uri"))
                    namespacesTemp.add(new MyPair<>(obj.getString("name"), obj.getString("uri")));
                else throw new JSONException("Missing name or uri in object: " + obj);
            }

            // Add the new namespaces to the existing list and update the ListView
            namespaces.addAll(namespacesTemp);
            namespaceListView.getItems().clear();  // Clear existing items
            namespaceListView.setItems(FXCollections.observableArrayList(namespaces));  // Set updated list

        } catch (JSONException e) {
            // Log error and provide feedback to the user
            CustomAlert.showError("Error while reading file.", "Error while reading file: " + e.getMessage());
            AppLogger.severe("Error while reading file: " + e.getMessage());
        }
    }

    @FXML
    private void saveNamespaces() {
        JSONArray JSONContent = new JSONArray();
        namespaces.forEach(namespace -> {
            JSONObject obj = new JSONObject();
            obj.put("name", namespace.getFirst());
            obj.put("uri", namespace.getSecond());
            JSONContent.put(obj);
        });

        FileHandler.saveFile("JSON File", "*.json", JSONContent);
    }
}
