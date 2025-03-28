package com.jataxmltransformer.middleware;

import com.jataxmltransformer.logic.cducecompiler.CDuceCodeLoader;
import com.jataxmltransformer.logic.cducecompiler.CDuceCommandExecutor;
import com.jataxmltransformer.logic.data.CheckStructure;
import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLDiffChecker;
import com.jataxmltransformer.logic.xml.XMLErrorReporter;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import com.jataxmltransformer.logs.AppLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Middleware class that acts as an intermediary for ontology processing.
 * It maintains a global state and facilitates interactions between various components.
 */
public class Middleware {
    private static Middleware instance;
    private static CheckStructure checkStructure;
    private static Ontology ontologyInput;
    private static Ontology ontologyOutput;

    /**
     * Private constructor to enforce a singleton pattern.
     */
    private Middleware() {
        checkStructure = new CheckStructure();
        ontologyInput = new Ontology();
        ontologyOutput = new Ontology();
    }

    /**
     * Returns the singleton instance of Middleware.
     * If the instance does not exist, it is created.
     *
     * @return Middleware instance
     */
    public static Middleware getInstance() {
        if (instance == null) {
            instance = new Middleware();
        }
        return instance;
    }

    /**
     * Resets the singleton instance, allowing a new instance to be created.
     */
    public static void resetInstance() {
        instance = null;
        checkStructure = new CheckStructure();
        ontologyInput = new Ontology();
        ontologyOutput = new Ontology();
        AppLogger.close();
    }

    /**
     * Retrieves the list of errors found when comparing the input and output ontologies.
     * The method compares the XML data of the input and output ontologies and checks for any differences.
     * If differences are found, it generates error information for the mismatched elements.
     * If no differences are detected or the ontologies are invalid, an empty list is returned.
     *
     * @return A list of {@link ErrorInfo} objects representing the differences between the input and output ontologies.
     * Returns an empty list if there are no differences or if either of the ontologies is invalid.
     * @throws Exception If an error occurs while processing the ontologies or generating the error information.
     */
    public static List<ErrorInfo> getErrors() throws Exception {
        // Validate ontology input and output before proceeding
        if (Middleware.ontologyInput == null || Middleware.ontologyOutput == null ||
                Middleware.ontologyInput.isEmpty() || Middleware.ontologyOutput.isEmpty()) {
            return Collections.emptyList();
        }

        // Parse ontology XML and get the differences
        Ontology ontologyInput = XMLFormatter.formatOntology(Middleware.ontologyInput);
        Ontology ontologyOutput = XMLFormatter.formatOntology(Middleware.ontologyOutput);

        XMLDiffChecker xmlDiffChecker = new XMLDiffChecker();
        List<EditedElement> differences = xmlDiffChecker.diffOntologies(ontologyInput, ontologyOutput);

        // If no differences, return an empty list
        if (differences.isEmpty())
            return Collections.emptyList();

        // Generate error info based on the differences
        XMLErrorReporter reporter = new XMLErrorReporter(Middleware.ontologyInput.getXmlData());
        return reporter.generateErrorInfo(differences);
    }

    public Ontology getOntologyOutput() {
        return ontologyOutput;
    }

    public void setOntologyOutput(Ontology ontologyOutput) {
        Middleware.ontologyOutput = ontologyOutput;
    }

    /**
     * Gets the current ontology.
     *
     * @return the ontology
     */
    public Ontology getOntologyInput() {
        return ontologyInput;
    }

    /**
     * Sets the ontology.
     *
     * @param ontology the ontology to set
     */
    public void setOntologyInput(Ontology ontology) {
        Middleware.ontologyInput = ontology;
    }

    /**
     * Gets the list of namespaces.
     *
     * @return list of namespaces
     */
    public List<String> getNamespaces() {
        return checkStructure.getNamespaces();
    }

    /**
     * Sets the list of namespaces after cleaning and formatting them.
     *
     * @param namespaces list of namespaces
     */
    public void setNamespaces(List<String> namespaces) {
        List<String> cleanedNamespaces = new ArrayList<>();

        List<String> defaultNamespaces = checkStructure.getDefaultNamespaces();

        for (String namespace : namespaces) {
            if (!namespace.isBlank() && !defaultNamespaces.contains(namespace.replace(";;", ""))) {
                if (!namespace.contains(";;"))
                    namespace = namespace.concat(";;");
                cleanedNamespaces.add(namespace);
            }
        }

        checkStructure.setNamespaces(cleanedNamespaces);
    }

    /**
     * Gets the structure list.
     *
     * @return structure list
     */
    public List<String> getStructure() {
        return checkStructure.getStructure();
    }

    /**
     * Sets the structure list.
     *
     * @param structure structure list to set
     */
    public void setStructure(List<String> structure) {
        checkStructure.setStructure(structure);
    }

    /**
     * Gets the list of classes.
     *
     * @return list of classes
     */
    public List<String> getClasses() {
        return checkStructure.getClasses();
    }

    /**
     * Sets the list of classes.
     *
     * @param classes list of classes
     */
    public void setClasses(List<String> classes) {
        checkStructure.setClasses(classes);
    }

    /**
     * Gets the list of attributes.
     *
     * @return list of attributes
     */
    public List<String> getAttributes() {
        return checkStructure.getAttributes();
    }

    /**
     * Sets the list of attributes.
     *
     * @param attributes list of attributes
     */
    public void setAttributes(List<String> attributes) {
        checkStructure.setAttributes(attributes);
    }

    /**
     * Sets namespaces, structure, classes, and attributes simultaneously.
     *
     * @param namespaces list of namespaces
     * @param structure  list of structure elements
     * @param classes    list of classes
     * @param attributes list of attributes
     */
    public void setNamespacesAndStructure(List<String> namespaces, List<String> structure, List<String> classes,
                                          List<String> attributes) {
        checkStructure.setNamespaces(namespaces);
        checkStructure.setStructure(structure);
        checkStructure.setClasses(classes);
        checkStructure.setAttributes(attributes);
    }

    /**
     * Loads the ontology structure into the system using CDuce.
     *
     * @return true if the structure is successfully loaded, false otherwise
     */
    public boolean loadStructure() {
        if (checkStructure.getStructure().isEmpty() || checkStructure.getAttributes().isEmpty()
                || checkStructure.getClasses().isEmpty()) {
            AppLogger.severe("Middleware: loadStructure: structure or classes or attributes is empty");
            return false;
        }
        CDuceCodeLoader.loadCheckStructure(
                checkStructure.getNamespaces(),
                checkStructure.getStructure(),
                checkStructure.getAttributes(),
                checkStructure.getClasses());
        return true;
    }

    /**
     * Verifies the ontology using the CDuce command executor.
     *
     * @return true if verification is successful, false otherwise
     * @throws Exception if an error occurs during verification
     */
    public boolean verifyOntology() throws Exception {
        CDuceCommandExecutor executor = new CDuceCommandExecutor();
        if (ontologyInput.isEmpty()) {
            AppLogger.severe("Middleware: verifyOntology: ontology is empty");
            return false;
        }
        return executor.verifyOntology(ontologyInput);
    }

    /**
     * Transforms the ontology using the CDuce command executor.
     * <p>
     * This method attempts to transform the input ontology using a CDuceCommandExecutor.
     * If the transformation is successful,
     * the method updates the output ontology with the transformed XML data,
     * ontology name, and ontology extension.
     * </p>
     *
     * @return {@code true} if the ontology was successfully transformed and the output ontology is updated;
     * {@code false} if the transformation failed or if the resulting content is {@code null}.
     * @throws Exception if an error occurs during the transformation process.
     */
    public boolean transformOntology() throws Exception {
        CDuceCommandExecutor executor = new CDuceCommandExecutor();
        Ontology content = executor.transformOntology(ontologyInput);
        if (content == null)
            return false;

        ontologyOutput.setXmlData(content.getXmlData());
        ontologyOutput.setOntologyName(ontologyInput.getOntologyName());
        ontologyOutput.setOntologyExtension(ontologyInput.getOntologyExtension());
        return true;
    }

    /**
     * Returns a list of default namespaces commonly used in RDF and OWL structures.
     *
     * @return A list of predefined namespace declarations.
     */
    public List<String> getDefaultNamespaces() {
        return checkStructure.getDefaultNamespaces();
    }
}