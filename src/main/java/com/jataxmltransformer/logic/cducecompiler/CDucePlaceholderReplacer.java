package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logs.AppLogger;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.util.Map;

/**
 * The {@code CDucePlaceholderReplacer} class is responsible for processing a CDuce source file
 * by replacing placeholders with actual values and saving the modified content to an output file.
 * <p>
 * The input and output file paths are loaded from environment variables using {@code Dotenv}.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     CDucePlaceholderReplacer replacer = new CDucePlaceholderReplacer();
 *     Map&lt;String, String&gt; placeholders = Map.of("{PLACEHOLDER1}", "value1", "{PLACEHOLDER2}", "value2");
 *     replacer.replacePlaceholders(placeholders);
 * </pre>
 * </p>
 */
public class CDucePlaceholderReplacer {
    private final String cduceCodeInputPath;
    private final String cduceCodeOutputPath;

    /**
     * Initializes the CDuce placeholder replacer, loading input and output file paths from environment variables.
     */
    public CDucePlaceholderReplacer() {
        Dotenv dotenv = Dotenv.load();
        cduceCodeInputPath = dotenv.get("TRANSFORMATION_INPUT_PATH");
        cduceCodeOutputPath = dotenv.get("TRANSFORMATION_OUTPUT_PATH");
    }

    /**
     * Reads the CDuce input file, replaces placeholders with provided values, and writes the modified content
     * to the output file.
     * <p>
     * Each occurrence of a placeholder in the file is replaced with its corresponding value from the provided map.
     * </p>
     *
     * @param placeholders A map containing placeholder keys and their respective replacement values.
     * @throws Exception If the input file is not found or an I/O error occurs during processing.
     */
    private void replacer(Map<String, String> placeholders) throws Exception {
        try (
                BufferedReader reader = new BufferedReader(new FileReader(cduceCodeInputPath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(cduceCodeOutputPath))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                    line = line.replace(placeholder.getKey(), placeholder.getValue());
                }

                // Writing the edited file to the new path
                writer.write(line);
                writer.newLine();
            }

        } catch (FileNotFoundException e) {
            AppLogger.severe("File not found: " + cduceCodeInputPath);
            throw new Exception("File not found: " + cduceCodeInputPath);
        }
    }
}
