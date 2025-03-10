package com.jataxmltransformer.logic.utilities;

import com.jataxmltransformer.logs.AppLogger;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class for handling file operations such as loading and saving files.
 */
public class FileHandler {

    /**
     * Opens a file selection dialog and loads the content of the selected file as a string.
     *
     * @param extensionName The name of the file extension filter (e.g., "XML Files").
     * @param extension     The allowed file extension (e.g., "*.xml").
     * @return The content of the selected file as a string, or {@code null} if an error occurs or no file is selected.
     */
    public static String loadFile(String extensionName, String extension) {
        FileChooser fileChooser = new FileChooser();
        if (extensionName != null && extension != null)
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionName, extension));

        File file = fileChooser.showOpenDialog(null);
        if (file == null)
            return null;

        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            AppLogger.severe("Error while reading file: " + file.getAbsolutePath());
            return null;
        }
    }

    /**
     * Opens a file save dialog and writes the given content to the selected file.
     *
     * @param extensionName The name of the file extension filter (e.g., "Text Files").
     * @param extension     The allowed file extension (e.g., "*.txt").
     * @param content       The content to be written to the file.
     */
    public static void saveFile(String extensionName, String extension, Object content) {
        FileChooser fileChooser = new FileChooser();
        if (extensionName != null && extension != null)
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionName, extension));

        File file = fileChooser.showSaveDialog(null);
        if (file == null)
            return;

        try {
            Files.write(file.toPath(), content.toString().getBytes());
        } catch (IOException e) {
            AppLogger.severe("Error while saving file: " + file.getAbsolutePath());
        }
    }
}