package com.jataxmltransformer.middleware;

import com.jataxmltransformer.logic.cducecompiler.CDuceCodeLoader;
import com.jataxmltransformer.logic.cducecompiler.CDuceCommandExecutor;
import com.jataxmltransformer.logic.data.CheckStructure;
import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLDiffChecker;
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
     * Private constructor to enforce singleton pattern.
     */
    private Middleware() {
        checkStructure = new CheckStructure();
        ontologyInput = new Ontology();
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
     * Sets the list of namespaces.
     *
     * @param namespaces list of namespaces
     */
    public void setNamespaces(List<String> namespaces) {
        checkStructure.setNamespaces(namespaces);
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
    public void setNamespacesAndStructure(List<String> namespaces, List<String> structure, List<String> classes, List<String> attributes) {
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
        if (checkStructure.getStructure().isEmpty() || checkStructure.getAttributes().isEmpty() || checkStructure.getClasses().isEmpty()) {
            AppLogger.severe("Middleware: loadStructure: structure or classes or attributes is empty");
            return false;
        }
        CDuceCodeLoader.loadCheckStructure(checkStructure.getNamespaces(), checkStructure.getStructure(), checkStructure.getAttributes(), checkStructure.getClasses());
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
     * Transforms the ontology.
     */
    public void transformOntology() throws Exception {
        CDuceCommandExecutor executor = new CDuceCommandExecutor();
        ontologyOutput = executor.transformOntology(ontologyInput);
    }

    public List<ErrorInfo> getErrors() throws Exception {
        if (ontologyOutput == null || ontologyInput == null || ontologyOutput.isEmpty() || ontologyInput.isEmpty())
            return Collections.emptyList();

        XMLDiffChecker xmlDiffChecker = new XMLDiffChecker();
        List<EditedElement> differences = xmlDiffChecker.diff(ontologyInput.getXmlData(), ontologyOutput.getXmlData());

        List<ErrorInfo> errorList = new ArrayList<>();

        for (EditedElement diff : differences) {
            int startLine = diff.getStartLine();
            int endLine = diff.getEndLine();
            String errorMessage = "Difference between the two files.";
            String elementDetails = diff.getId();

            if (startLine != -1 && endLine != -1) {
                errorList.add(new ErrorInfo(startLine, endLine, errorMessage, elementDetails));
            }
        }

        return errorList;
    }
}