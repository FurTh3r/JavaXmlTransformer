package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logs.AppLogger;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The CDuceCodeLoader class is responsible for loading and replacing placeholders in CDuce code templates.
 * It supports loading code templates for verification and transformation processes, as well as loading structure and
 * filtering templates for namespace, structure, attributes, and classes.
 */
public class CDuceCodeLoader {

    /**
     * Loads and replaces placeholders in the CDuce code templates for checking ontology structure.
     *
     * @param namespaces list of namespaces to be replaced in the template.
     * @param structure  list of structure elements to be replaced in the template.
     * @param attributes list of attributes to be used in the template.
     * @param classes    list of classes to be used in the template.
     */
    public static void loadCheckStructure(List<String> namespaces, List<String> structure,
                                          List<String> attributes, List<String> classes) {
        try {
            // Convert lists to single strings for placeholders
            String namespaceString = String.join("\n", namespaces);
            String structureString = String.join("\n", structure);

            // Initialize the placeholder replacer
            CDucePlaceholderReplacer placeholderReplacer =
                    new CDucePlaceholderReplacer("base_code.cd", "init_code.cd");

            // Map the placeholders to the actual content
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{NAMESPACE_PLACEHOLDER}", namespaceString);
            placeholders.put("{STRUCTURE_PLACEHOLDER}", structureString);
            placeholders.put("{FILTER_ATTRIBUTES_PLACEHOLDER}",
                    loadTemplateAndSubstitute("FILTER_ATTRIBUTES", attributes));
            placeholders.put("{FILTER_CLASSES_PLACEHOLDER}",
                    loadTemplateAndSubstitute("FILTER_CLASSES", classes));

            // Perform placeholder replacement
            placeholderReplacer.replacePlaceholders(placeholders);
        } catch (Exception e) {
            AppLogger.severe("Error loading structure check template: " + e.getMessage());
        }
    }

    /**
     * Loads the CDuce verification code template and replaces the placeholders with the provided ontology input.
     *
     * @param ontologyInput the path to the ontology input file.
     */
    static void loadVerifyMain(String ontologyInput) {
        try {
            // Prepare placeholders for verification
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{INPUT_FILE_PLACEHOLDER}", ontologyInput);
            placeholders.put("{ENTRYPOINT_PLACEHOLDER}", loadTemplate("ENTRYPOINT_VERIFY"));

            // Replace placeholders in the template
            CDucePlaceholderReplacer placeholderReplacer =
                    new CDucePlaceholderReplacer("init_code.cd", "verify_code.cd");
            placeholderReplacer.replacePlaceholders(placeholders);
        } catch (Exception e) {
            AppLogger.severe("Error loading verification code template: " + e.getMessage());
        }
    }

    /**
     * Loads the CDuce transformation code template and replaces the placeholders with the provided input and output paths.
     *
     * @param ontologyInput  the path to the ontology input file.
     * @param ontologyOutput the path to the ontology output file.
     */
    static void loadTransformMain(String ontologyInput, String ontologyOutput) {
        try {
            // Prepare placeholders for transformation
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{INPUT_FILE_PLACEHOLDER}", ontologyInput);
            placeholders.put("{ENTRYPOINT_PLACEHOLDER}", loadTemplate("ENTRYPOINT_TRANSFORM"));
            placeholders.put("{OUTPUT_FILE_PLACEHOLDER}", ontologyOutput);

            // Replace placeholders in the transformation template
            CDucePlaceholderReplacer placeholderReplacer =
                    new CDucePlaceholderReplacer("init_code.cd", "transform_code.cd");
            placeholderReplacer.replacePlaceholders(placeholders);
        } catch (Exception e) {
            AppLogger.severe("Error loading transformation code template: " + e.getMessage());
        }
    }

    /**
     * Loads a template and substitutes placeholders for each element in the provided list.
     *
     * @param templateName the name of the template to be loaded.
     * @param elements     the elements to be substituted in the template.
     * @return the substituted template content as a string.
     */
    private static String loadTemplateAndSubstitute(String templateName, List<String> elements) {
        StringBuilder elementsBlock = new StringBuilder();

        for (String element : elements) {
            String template = loadTemplate(templateName);
            if (template == null) {
                return null; // Return null if the template couldn't be loaded
            }
            // Substitute the placeholder in the template for each element
            elementsBlock.append(template.replace("{PLACEHOLDER}", element)).append("\n");
        }
        return elementsBlock.toString();
    }

    /**
     * Loads the template content for a specific template name from the CDuce template JSON file.
     *
     * @param templateName the name of the template to be loaded.
     * @return the template content as a string.
     */
    private static String loadTemplate(String templateName) {
        // Read the template JSON file
        try (FileReader reader = new FileReader(Dotenv.load().get("CDUCE_TEMPLATE_PATH"))) {
            // Read the content of the file into a string
            StringBuilder jsonContent = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                jsonContent.append((char) i);
            }

            // Parse the JSON content to retrieve the template
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            String template = jsonObject.optString(templateName);

            if (template.isEmpty()) {
                AppLogger.severe("The template '" + templateName + "' is empty.");
                return null;
            }

            return template;
        } catch (IOException e) {
            AppLogger.severe("The template '" + templateName + "' doesn't exist or could not be loaded.");
            return null;
        }
    }
}
