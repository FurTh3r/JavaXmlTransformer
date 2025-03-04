package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logs.AppLogger;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CDuceCodeLoader {
    public static boolean loadCheckStructure(List<String> namespaces, List<String> structure, List<String> attributes, List<String> classes) {
        try {
            // Convert namespaces list to a single string
            String namespaceString = String.join("\n", namespaces);

            // Convert structure list to a single string
            String structureString = String.join("\n", structure);

            // Convert attributes list to a single string
            String filterAttributesString = String.join("\n", attributes);

            // Convert classes list to a single string
            String filterClassesString = String.join("\n", classes);

            // Initialize CDucePlaceholderReplacer with the template files
            CDucePlaceholderReplacer cDucePlaceholderReplacer = new CDucePlaceholderReplacer("base_code.cd", "init_code.cd");

            // Create a mapping of placeholders to actual values
            Map<String, String> toReplace = new HashMap<>();
            toReplace.put("{NAMESPACE_PLACEHOLDER}", namespaceString);
            toReplace.put("{STRUCTURE_PLACEHOLDER}", structureString);
            toReplace.put("{FILTER_ATTRIBUTES_PLACEHOLDER}", loadTemplateAndSubstitute("FILTER_ATTRIBUTES", attributes));
            toReplace.put("{FILTER_CLASSES_PLACEHOLDER}", loadTemplateAndSubstitute("FILTER_CLASSES", classes));

            // Perform the placeholder replacement
            cDucePlaceholderReplacer.replacePlaceholders(toReplace);

            return true; // Success
        } catch (Exception e) {
            AppLogger.severe(e.getMessage()); // Log the error for debugging
            return false; // Failure
        }
    }

    public static boolean loadVerifyMain(String ontologyInput) {
        try {
            // Substitute string for the path to ontology input
            Map<String, String> toReplace = new HashMap<>();
            toReplace.put("{INPUT_FILE_PLACEHOLDER}", ontologyInput);
            toReplace.put("{ENTRYPOINT_PLACEHOLDER}", loadTemplate("ENTRYPOINT_VERIFY"));

            CDucePlaceholderReplacer cDucePlaceholderReplacer = new CDucePlaceholderReplacer("init_code.cd", "verify_code.cd");
            cDucePlaceholderReplacer.replacePlaceholders(toReplace);
            return true;
        } catch (Exception e) {
            AppLogger.severe(e.getMessage()); // Log the error for debugging
            return false; // Failure
        }
    }

    public static boolean loadTransformMain(String ontologyInput, String ontologyOutput) {
        try {
            // Substitute string for the path to ontology input
            Map<String, String> toReplace = new HashMap<>();
            toReplace.put("{INPUT_FILE_PLACEHOLDER}", ontologyInput);
            toReplace.put("{ENTRYPOINT_PLACEHOLDER}", loadTemplate("ENTRYPOINT_TRANSFORM"));
            toReplace.put("{OUTPUT_FILE_PLACEHOLDER}", ontologyOutput);

            CDucePlaceholderReplacer cDucePlaceholderReplacer = new CDucePlaceholderReplacer("init_code.cd", "transform_code.cd");
            cDucePlaceholderReplacer.replacePlaceholders(toReplace);
            return true;
        } catch (Exception e) {
            AppLogger.severe(e.getMessage()); // Log the error for debugging
            return false; // Failure
        }
    }

    private static String loadTemplateAndSubstitute(String templateName, List<String> elements) {
        StringBuilder elementsBlock = new StringBuilder();
        for (String el : elements) {
            String element = loadTemplate(templateName);
            if (element == null)
                return null;
            elementsBlock.append(element.replace("{PLACEHOLDER}", el));
            elementsBlock.append("\n");
        }
        return elementsBlock.toString();
    }

    private static String loadTemplate(String templateName) {
        // Create a FileReader to read the JSON file
        try (FileReader reader = new FileReader(Dotenv.load().get("CDUCE_TEMPLATE_PATH"))) {
            // Parse the JSON content into a JSONObject
            StringBuilder jsonContent = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                jsonContent.append((char) i);
            }

            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            // Return the template code for the specified templateName
            String template = jsonObject.optString(templateName);

            if (template.isEmpty()) {
                AppLogger.severe("The template " + templateName + " is empty");
                return null;
            }

            return template;
        } catch (IOException e) {
            AppLogger.severe("The template " + templateName + " doesn't exist!");
            return null;
        }
    }
}
