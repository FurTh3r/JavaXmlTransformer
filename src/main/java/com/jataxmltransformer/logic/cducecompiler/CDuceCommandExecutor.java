package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.shellinterface.ProcessExecutor;
import com.jataxmltransformer.logic.shellinterface.ProcessExecutorInterface;
import com.jataxmltransformer.logic.utilities.PathUtility;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

/**
 * CDuceCommandExecutor is responsible for executing CDuce commands to verify and transform ontologies.
 * This class interacts with the shell and uses the CDuce environment for ontology validation and transformation.
 */
public class CDuceCommandExecutor implements CDuceCommandExecutorInterface {

    private static final Dotenv dotenv = Dotenv.load(); // Load environment variables only once

    /**
     * Verifies the given ontology using CDuce verification command.
     *
     * @param ontology the ontology to be verified.
     * @return true if the ontology is valid, false if it is invalid.
     * @throws Exception if there is an error during the verification process.
     */
    @Override
    public boolean verifyOntology(Ontology ontology) throws Exception {
        // Save the ontology XML to the specified file path
        ontology.saveXmlToFile(dotenv.get("ONTOLOGY_INPUT"));

        // Load the verification code
        CDuceCodeLoader.loadVerifyMain(PathUtility.convertToWslPath(dotenv.get("ONTOLOGY_INPUT_ABSOLUTE")));

        // Prepare the shell commands for executing the verification
        List<String> commands = prepareCommands(dotenv.get("CDUCE_CODE_PATH_ABSOLUTE"), "verify_code.cd");

        // Execute the commands and capture the output
        String output = executeShellCommands(commands);

        // Check if the output contains valid or invalid ontology information
        return parseVerificationOutput(output);
    }

    /**
     * Transforms the given ontology using CDuce transformation command.
     *
     * @param ontology the ontology to be transformed.
     * @return the transformed ontology, or null if the transformation failed.
     * @throws Exception if there is an error during the transformation process.
     */
    @Override
    public Ontology transformOntology(Ontology ontology) throws Exception {
        // Save the ontology XML to the specified file path
        ontology.saveXmlToFile(dotenv.get("ONTOLOGY_INPUT"));

        // Load the transformation code
        CDuceCodeLoader.loadTransformMain(PathUtility.convertToWslPath(dotenv.get("ONTOLOGY_INPUT_ABSOLUTE")),
                PathUtility.convertToWslPath(dotenv.get("ONTOLOGY_OUTPUT_ABSOLUTE")));

        // Prepare the shell commands for executing the transformation
        List<String> commands = prepareCommands(dotenv.get("CDUCE_CODE_PATH_ABSOLUTE"), "transform_code.cd");

        // Execute the commands and capture the output
        String output = executeShellCommands(commands);

        // Analyze the result and return the transformed ontology if successful
        return parseTransformationOutput(output);
    }

    /**
     * Prepares the list of shell commands to be executed.
     *
     * @param cdPath the path to the CDuce code directory.
     * @param script the script to be executed.
     * @return a list of commands to be executed.
     */
    private List<String> prepareCommands(String cdPath, String script) {
        return List.of("cd " + PathUtility.convertToWslPath(cdPath), "cduce " + script);
    }

    /**
     * Executes the shell commands and returns the output.
     *
     * @param commands the commands to be executed.
     * @return the output from the shell execution.
     * @throws Exception if there is an error during execution.
     */
    private String executeShellCommands(List<String> commands) throws Exception {
        ProcessExecutorInterface processExecutor = new ProcessExecutor();
        String output = processExecutor.execute(commands);

        if (output == null) throw new Exception("Unexpected output: null");
        return output;
    }

    /**
     * Parses the output of the ontology verification and returns the result.
     *
     * @param output the output from the verification command.
     * @return true if the ontology is valid, false if it is invalid.
     * @throws Exception if the output is unexpected.
     */
    private boolean parseVerificationOutput(String output) throws Exception {
        if (output.contains("LOADED\nVALID ONTOLOGY\n")) return true; // Valid ontology
        else if (output.contains("LOADED\nINVALID ONTOLOGY\n") || output.contains("SYNTAX ERROR\nVALID ONTOLOGY\n"))
            return false; // Invalid ontology or syntax error
        else throw new Exception("Unexpected output from CDuce verification: " + output); // Handle unexpected output
    }

    /**
     * Parses the output of the ontology transformation and returns the result.
     *
     * @param output the output from the transformation command.
     * @return the transformed ontology if successful, or null if the transformation failed.
     * @throws Exception if the output is unexpected.
     */
    private Ontology parseTransformationOutput(String output) throws Exception {
        if (output.contains("LOADED") && output.contains("FILE SAVED")) {
            Ontology result = new Ontology();
            result.loadXmlFromFile(dotenv.get("ONTOLOGY_OUTPUT_ABSOLUTE"));
            return result;
        } else if (output.contains("LOADED") && output.contains("FAILED"))
            return null; // Invalid ontology or transformation failure
        else throw new Exception("Unexpected output from CDuce transformation: " + output);
    }
}