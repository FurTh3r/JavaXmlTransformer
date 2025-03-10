package com.jataxmltransformer.GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility class for displaying custom alert dialogs in a JavaFX application.
 */
public class CustomAlert {

    /**
     * Displays an error alert with the specified title and message.
     *
     * @param title   The title of the alert window.
     * @param message The message to display in the alert.
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a warning alert with the specified title and message.
     *
     * @param title   The title of the alert window.
     * @param message The message to display in the alert.
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an informational alert with the specified title and message.
     *
     * @param title   The title of the alert window.
     * @param message The message to display in the alert.
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation alert with the specified title and message.
     *
     * @param title   The title of the alert window.
     * @param message The message to display in the alert.
     * @return {@code true} if the user selects OK, {@code false} otherwise.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}