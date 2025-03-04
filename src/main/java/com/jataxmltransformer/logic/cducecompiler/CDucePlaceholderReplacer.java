package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logs.AppLogger;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.util.Map;

/**
 * The {@code CDucePlaceholderReplacer} class processes a CDuce source file by replacing
 * placeholders with specified values and saves the modified content to an output file.
 * <p>
 * This class dynamically updates CDuce code, enabling transformation of CDuce scripts
 * based on given key-value pairs.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 *     Map<String, String> placeholders = Map.of("{PLACEHOLDER1}", "value1", "{PLACEHOLDER2}", "value2");
 *     CDucePlaceholderReplacer replacer = new CDucePlaceholderReplacer("inputFileName", "outputFileName");
 *     replacer.replacePlaceholders(placeholders);
 * </pre>
 */
public class CDucePlaceholderReplacer {

    private final String cduceCodeInputPath;
    private final String cduceCodeOutputPath;

    /**
     * Constructs a new {@code CDucePlaceholderReplacer} instance with the given input and output file names.
     * <p>
     * The paths to the input and output files are constructed by appending the file names to the directory path
     * stored in the environment variable {@code CDUCE_CODE_PATH}.
     * </p>
     *
     * @param cduceCodeInput  The name of the input file (without path).
     * @param cduceCodeOutput The name of the output file (without path).
     * @throws NullPointerException If the {@code CDUCE_CODE_PATH} environment variable is not set.
     */
    public CDucePlaceholderReplacer(String cduceCodeInput, String cduceCodeOutput) {
        Dotenv dotenv = Dotenv.load();
        String basePath = dotenv.get("CDUCE_CODE_PATH");
        if (basePath == null) {
            throw new NullPointerException("CDUCE_CODE_PATH environment variable is not set.");
        }
        this.cduceCodeInputPath = basePath + cduceCodeInput;
        this.cduceCodeOutputPath = basePath + cduceCodeOutput;
    }

    /**
     * Reads a CDuce input file, replaces placeholders with provided values, and writes the modified content
     * to an output file.
     * <p>
     * The base directory for the files is retrieved from environment variables.
     * </p>
     *
     * @param placeholders A map containing placeholder keys and their respective replacement values.
     *                     Each key should be a string matching the placeholder format used in the CDuce file.
     * @throws IOException If an I/O error occurs while reading or writing the file.
     * @throws FileNotFoundException If the input file does not exist.
     */
    public void replacePlaceholders(Map<String, String> placeholders) throws IOException {

        // Ensure paths are valid
        if (cduceCodeInputPath == null || cduceCodeOutputPath == null) {
            AppLogger.severe("CDUCE_CODE_PATH environment variable is not set.");
            throw new IOException("CDUCE_CODE_PATH environment variable is not set.");
        }

        // Process the file and replace placeholders
        try (BufferedReader reader = new BufferedReader(new FileReader(cduceCodeInputPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(cduceCodeOutputPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                    line = line.replace(placeholder.getKey(), placeholder.getValue());
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            AppLogger.severe("File not found: " + cduceCodeInputPath);
            throw new FileNotFoundException("File not found: " + cduceCodeInputPath);
        } catch (IOException e) {
            AppLogger.severe("Error processing the CDuce file.");
            throw new IOException("Error processing the CDuce file.", e);
        }
    }

    /**
     * Deletes the specified output file
     * located at the path defined by the environment variable {@code CDUCE_CODE_PATH}.
     * <p>
     * This method checks if the file exists at the given path and attempts to delete it.
     * If the file does not exist or deletion fails, appropriate messages are logged.
     * </p>
     *
     * @param fileName The name of the file to be deleted.
     *                 The full file path is constructed by appending the file name to the {@code CDUCE_CODE_PATH} environment variable.
     * @throws SecurityException If a security manager exists and denies the deletion of the file.
     */
    public void deleteOutputFile(String fileName) {
        Dotenv dotenv = Dotenv.load();
        String basePath = dotenv.get("CDUCE_CODE_PATH");
        if (basePath == null) {
            AppLogger.severe("CDUCE_CODE_PATH environment variable is not set.");
            return;
        }

        String filePath = basePath + fileName;
        File outputFile = new File(filePath);

        if (outputFile.exists()) {
            boolean isDeleted = outputFile.delete();
            if (!isDeleted) {
                AppLogger.warning("Failed to delete the output file: " + filePath);
            }
        } else {
            AppLogger.info("Output file does not exist: " + filePath);
        }
    }
}