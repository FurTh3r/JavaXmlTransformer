package com.jataxmltransformer.middleware;

import com.jataxmltransformer.logic.cducecompiler.CDuceCodeLoader;
import com.jataxmltransformer.logic.cducecompiler.CDuceCommandExecutor;
import com.jataxmltransformer.logic.data.Ontology;
import java.util.ArrayList;
import java.util.List;

/**
 * Middleware class that acts as an intermediary for ontology processing.
 * It maintains a global state and facilitates interactions between various components.
 */
public class Middleware {
    private static Middleware instance;
    private static List<String> namespaces;
    private static List<String> structure;
    private static List<String> classes;
    private static List<String> attributes;
    private static Ontology ontology;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private Middleware() {
        namespaces = new ArrayList<>();
        structure = new ArrayList<>();
        classes = new ArrayList<>();
        attributes = new ArrayList<>();
        ontology = new Ontology();
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

    /**
     * Gets the current ontology.
     *
     * @return the ontology
     */
    public Ontology getOntology() {
        return ontology;
    }

    /**
     * Sets the ontology.
     *
     * @param ontology the ontology to set
     */
    public void setOntology(Ontology ontology) {
        Middleware.ontology = ontology;
    }

    /**
     * Gets the list of namespaces.
     *
     * @return list of namespaces
     */
    public List<String> getNamespaces() {
        return namespaces;
    }

    /**
     * Sets the list of namespaces.
     *
     * @param namespaces list of namespaces
     */
    public void setNamespaces(List<String> namespaces) {
        Middleware.namespaces = namespaces;
    }

    /**
     * Gets the structure list.
     *
     * @return structure list
     */
    public List<String> getStructure() {
        return structure;
    }

    /**
     * Sets the structure list.
     *
     * @param structure structure list to set
     */
    public void setStructure(List<String> structure) {
        Middleware.structure = structure;
    }

    /**
     * Gets the list of classes.
     *
     * @return list of classes
     */
    public List<String> getClasses() {
        return classes;
    }

    /**
     * Sets the list of classes.
     *
     * @param classes list of classes
     */
    public void setClasses(List<String> classes) {
        Middleware.classes = classes;
    }

    /**
     * Gets the list of attributes.
     *
     * @return list of attributes
     */
    public List<String> getAttributes() {
        return attributes;
    }

    /**
     * Sets the list of attributes.
     *
     * @param attributes list of attributes
     */
    public void setAttributes(List<String> attributes) {
        Middleware.attributes = attributes;
    }

    /**
     * Sets namespaces, structure, classes, and attributes simultaneously.
     *
     * @param namespaces list of namespaces
     * @param structure list of structure elements
     * @param classes list of classes
     * @param attributes list of attributes
     */
    public void setNamespacesAndStructure(List<String> namespaces, List<String> structure,
                                          List<String> classes, List<String> attributes) {
        Middleware.namespaces = namespaces;
        Middleware.structure = structure;
        Middleware.classes = classes;
        Middleware.attributes = attributes;
    }

    /**
     * Loads the ontology structure into the system using CDuce.
     *
     * @return true if the structure is successfully loaded, false otherwise
     */
    public boolean loadStructure() {
        if (structure.isEmpty() || attributes.isEmpty() || classes.isEmpty()) return false;
        CDuceCodeLoader.loadCheckStructure(namespaces, structure, attributes, classes);
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
        if (ontology.isEmpty()) return false;
        executor.verifyOntology(ontology);
        return true;
    }

    /**
     * Transforms the ontology. (Implementation pending)
     *
     * @return false (until implemented)
     */
    public boolean transformOntology() {
        // Implement transformation logic here TODO
        return false;
    }
}