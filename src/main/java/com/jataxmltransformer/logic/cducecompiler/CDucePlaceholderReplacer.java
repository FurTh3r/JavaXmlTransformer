package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logs.AppLogger;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.util.Map;

/**
 * The {@code CDucePlaceholderReplacer} class processes a CDuce source file by replacing
 * placeholders with specified values and saving the modified content to an output file.
 * <p>
 * This class dynamically updates CDuce code, enabling transformation of CDuce scripts
 * based on given key-value pairs.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 *     CDucePlaceholderReplacer replacer = new CDucePlaceholderReplacer();
 *     Map&lt;String, String&gt; placeholders = Map.of("{PLACEHOLDER1}", "value1", "{PLACEHOLDER2}", "value2");
 *     replacer.replacePlaceholders("example", placeholders);
 * </pre>
 */
public class CDucePlaceholderReplacer {
    /**
     * Reads a CDuce input file, replaces placeholders with provided values, and writes the modified content
     * to an output file.
     * <p>
     * The input file is expected to be named {@code cduceCodeName.cd}, and the output file is named
     * {@code cduceCodeName_exe.cd}. The base directory for the files is retrieved from an environment variable.
     * </p>
     *
     * @param cduceCodeName The name of the CDuce code file (without extension).
     * @param placeholders  A map containing placeholder keys and their respective replacement values.
     *                      Each key should be a string matching the placeholder format used in the CDuce file.
     * @throws IOException If an I/O error occurs while reading or writing the file.
     */
    public static void replacePlaceholders(String cduceCodeName, Map<String, String> placeholders) throws IOException {
        String basePath = Dotenv.load().get("CDUCE_CODE");
        if (basePath == null) {
            AppLogger.severe("CDUCE_CODE environment variable not found");
            throw new IOException("Environment variable CDUCE_CODE is not set.");
        }

        String cduceCodeInputPath = basePath + File.separator + cduceCodeName + ".cd";
        String cduceCodeOutputPath = basePath + File.separator + cduceCodeName + "_exe.cd";

        try (
                BufferedReader reader = new BufferedReader(new FileReader(cduceCodeInputPath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(cduceCodeOutputPath))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                    line = line.replace(placeholder.getKey(), placeholder.getValue());
                }

                // Writing the modified content to the output file
                writer.write(line);
                writer.newLine();
            }

        } catch (FileNotFoundException e) {
            AppLogger.severe("File not found: " + cduceCodeInputPath);
            throw new FileNotFoundException("File not found: " + cduceCodeInputPath);
        }
    }
}