package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.Ontology;

import java.io.IOException;
import java.util.List;

/**
 * The {@code XMLDiffChecker} class compares two XML files using XMLUnit and identifies the differences.
 * <p>
 * It compares the XML files at the node level, providing a more structured and XML-aware diffing approach.
 * Differences are stored as a list of {@code EditedElement} objects.
 * </p>
 */
public interface DiffChecker {
    /**
     * Compares two XML files and returns a list of differences.
     *
     * @param inputXMLPath  The path to the input XML file (control file).
     * @param outputXMLPath The path to the output XML file (test file).
     * @return A list of {@code EditedElement} objects representing the differences.
     */
    List<EditedElement> diffXmlFiles(String inputXMLPath, String outputXMLPath) throws IOException;

    /**
     * Compares two XML files and returns a list of differences.
     *
     * @param inputOntology  The input ontology data (control Ontology).
     * @param outputOntology The output ontology data (test Ontology).
     * @return A list of {@code EditedElement} objects representing the differences.
     */
    List<EditedElement> diffOntologies(Ontology inputOntology, Ontology outputOntology) throws Exception;
}