package com.jataxmltransformer.logic.utilities;

/**
 * The {@code PathUtility} class provides a utility method to convert Windows file paths
 * to their corresponding WSL (Windows Subsystem for Linux) format.
 * <p>
 * This conversion is useful when working with tools or scripts that run in a WSL environment
 * but need to access files stored on a Windows filesystem.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 *     String windowsPath = "C:\\Users\\John\\Documents\\file.txt";
 *     String wslPath = PathUtility.convertToWslPath(windowsPath);
 *     System.out.println(wslPath); // Output: /mnt/c/Users/John/Documents/file.txt
 * </pre>
 */
public class PathUtility {

    /**
     * Converts a Windows file path to a WSL-compatible path.
     * <p>
     * This method checks if the given path follows the Windows format (e.g., "C:\Users\...").
     * If so, it transforms it into the WSL format (e.g., "/mnt/c/Users/...").
     * If the input path is already in WSL or Unix format, it is returned unchanged.
     * </p>
     *
     * @param windowsPath The file path in Windows format (e.g., "C:\\Users\\John\\Documents\\file.txt").
     * @return The equivalent file path in WSL format (e.g., "/mnt/c/Users/John/Documents/file.txt"),
     * or the original path if no conversion is needed.
     */
    public static String convertToWslPath(String windowsPath) {
        if (windowsPath.contains(":\\")) {
            // Extract drive letter and replace backslashes
            String drive = windowsPath.substring(0, 1).toLowerCase(); // Extract "C" and convert to lowercase
            String path = windowsPath.substring(2).replace("\\", "/"); // Remove "C:" and replace backslashes
            return "/mnt/" + drive + path;
        }
        return windowsPath; // Return as is if already in the correct format
    }
}