package com.jataxmltransformer.logic.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the structure of a system by maintaining lists of attributes, classes,
 * namespaces, and overall structure definitions.
 */
public class CheckStructure {
    private List<String> attributes;
    private List<String> classes;
    private List<String> namespaces;
    private List<String> structure;

    /**
     * Constructs an empty CheckStructure instance with initialized lists.
     */
    public CheckStructure() {
        this.namespaces = new ArrayList<>();
        this.structure = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    /**
     * Returns a list of default namespaces commonly used in RDF and OWL structures.
     *
     * @return A list of predefined namespace declarations.
     */
    public List<String> getDefaultNamespaces() {
        return List.of(
                "namespace skos = \"http://www.w3.org/2004/02/skos/core#\"",
                "namespace owl = \"http://www.w3.org/2002/07/owl#\"",
                "namespace rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"",
                "namespace xml = \"http://www.w3.org/XML/1998/namespace\"",
                "namespace xsd = \"http://www.w3.org/2001/XMLSchema#\"",
                "namespace rdfs = \"http://www.w3.org/2000/01/rdf-schema#\""
        );
    }

    /**
     * Gets the list of attributes.
     *
     * @return The list of attributes.
     */
    public List<String> getAttributes() {
        return attributes;
    }

    /**
     * Sets the list of attributes.
     *
     * @param attributes The new list of attributes.
     */
    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Gets the list of classes.
     *
     * @return The list of classes.
     */
    public List<String> getClasses() {
        return classes;
    }

    /**
     * Sets the list of classes.
     *
     * @param classes The new list of classes.
     */
    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    /**
     * Gets the list of namespaces.
     *
     * @return The list of namespaces.
     */
    public List<String> getNamespaces() {
        return namespaces;
    }

    /**
     * Sets the list of namespaces.
     *
     * @param namespaces The new list of namespaces.
     */
    public void setNamespaces(List<String> namespaces) {
        this.namespaces = new ArrayList<>(getDefaultNamespaces());
        this.namespaces.addAll(namespaces);
    }

    /**
     * Gets the list representing the structure of the system.
     *
     * @return The list of structural elements.
     */
    public List<String> getStructure() {
        return structure;
    }

    /**
     * Sets the structure list.
     *
     * @param structure The new list representing the system structure.
     */
    public void setStructure(List<String> structure) {
        this.structure = structure;
    }
}