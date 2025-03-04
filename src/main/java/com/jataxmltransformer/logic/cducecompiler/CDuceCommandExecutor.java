package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.shellinterface.ProcessExecutor;
import com.jataxmltransformer.logic.shellinterface.ProcessExecutorInterface;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.List;

public class CDuceCommandExecutor implements CDuceCommandExecutorInterface {
    @Override
    public boolean verifyOntology(Ontology ontology) throws Exception {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Adding the missing parts (placeholders) to code
        ontology.saveXmlToFile(dotenv.get("ONTOLOGY_INPUT"));
        CDuceCodeLoader.loadVerifyMain(dotenv.get("ONTOLOGY_INPUT_ABSOLUTE"));

        // Build the full path to the CDuce code directory (Retrieve the CDuce code directory path from environment variables)
        String fullCducePath = "/mnt/" + dotenv.get("CDUCE_CODE_PATH_ABSOLUTE");

        // Prepare the commands to be executed in the shell
        List<String> commands = List.of("cd " + fullCducePath, "cduce verify_code.cd");

        // Initialize the process executor and execute the commands
        ProcessExecutorInterface processExecutor = new ProcessExecutor();
        String output = processExecutor.execute(commands);

        // Check if the output is valid and handle different cases
        if (output == null)
            throw new Exception("Unexpected output: null");

        // Return true or false based on the specific output from the CDuce verification
        return switch (output) {
            case "LOADED\nVALID ONTOLOGY\n" -> true; // Valid ontology
            case "LOADED\nINVALID ONTOLOGY\n", "SYNTAX ERROR\nINVALID ONTOLOGY\n" ->
                    false; // Invalid ontology or syntax error
            default ->
                    throw new Exception("Unexpected output from CDuce verification: " + output); // Handle unexpected output
        };
    }

    @Override
    public boolean transformOntology(Ontology ontology) throws Exception {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Adding the missing parts (placeholders) to code
        ontology.saveXmlToFile(dotenv.get("ONTOLOGY_INPUT"));
        CDuceCodeLoader.loadTransformMain(dotenv.get("ONTOLOGY_INPUT_ABSOLUTE"), dotenv.get("ONTOLOGY_OUTPUT_ABSOLUTE"));

        // Build the full path to the CDuce code directory (Retrieve the CDuce code directory path from environment variables)
        String fullCducePath = "/mnt/" + dotenv.get("CDUCE_CODE_PATH_ABSOLUTE");

        // Prepare the commands to be executed in the shell
        List<String> commands = List.of("cd " + fullCducePath, "cduce transform_code.cd");

        // Initialize the process executor and execute the commands
        ProcessExecutorInterface processExecutor = new ProcessExecutor();
        String output = processExecutor.execute(commands);

        // Check if the output is valid and handle different cases
        if (output == null)
            throw new Exception("Unexpected output: null");

        // Return true or false based on the specific output from the CDuce verification
        return switch (output) { // TODO
            case "LOADED\nFILE SAVED\n" -> true; // Valid ontology
            case "LOADED\nFAILED\n" ->
                    false; // Invalid ontology or syntax error
            default ->
                    throw new Exception("Unexpected output from CDuce verification: " + output); // Handle unexpected output
        };
    }
}