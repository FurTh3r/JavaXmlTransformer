package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logs.AppLogger;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@code XMLDiffChecker} class compares two XML files or ontologies using XMLUnit
 * and identifies the differences between them. It provides methods to compare XML files
 * by their file paths or ontology objects and returns a list of {@link EditedElement}
 * objects representing the detected differences.
 */
public class XMLDiffChecker implements DiffChecker {

    /**
     * Compares two XML files and identifies the differences between them.
     *
     * @param inputXMLPath  The path to the original (control) XML file.
     * @param outputXMLPath The path to the modified (test) XML file.
     * @return A list of {@link EditedElement} objects representing the differences found.
     * @throws IOException If an error occurs while reading the XML files (e.g., file not found).
     */
    @Override
    public List<EditedElement> diffXmlFiles(String inputXMLPath, String outputXMLPath) throws IOException {
        File inputXML = new File(inputXMLPath);
        File outputXML = new File(outputXMLPath);

        if (!inputXML.exists() || !outputXML.exists())
            throw new IOException("One or both XML files do not exist.");

        try {
            return diff(new StreamSource(inputXML), new StreamSource(outputXML), inputXMLPath);
        } catch (Exception e) {
            AppLogger.severe("Error comparing XML files: " + e.getMessage());
            throw new IOException("Error comparing XML files", e);
        }
    }

    /**
     * Compares two ontology objects and identifies the differences between their XML data.
     *
     * @param inputOntology  The input ontology data (control ontology).
     * @param outputOntology The output ontology data (test ontology).
     * @return A list of {@link EditedElement} objects representing the differences found.
     * @throws Exception If an error occurs during the comparison (e.g., invalid XML data).
     */
    @Override
    public List<EditedElement> diffOntologies(Ontology inputOntology, Ontology outputOntology) throws Exception {
        if (inputOntology == null || outputOntology == null)
            throw new IllegalArgumentException("Input or output ontology cannot be null.");

        return diff(inputOntology.getXmlData(), outputOntology.getXmlData(), null);
    }

    /**
     * Compares two XML sources (files or strings) and identifies the differences.
     *
     * @param inputSource   The source of the original (control) XML data.
     * @param outputSource  The source of the modified (test) XML data.
     * @param inputXMLPath  The path to the original XML file (if applicable). Used for line number determination.
     * @return A list of {@link EditedElement} objects representing the differences found.
     * @throws Exception If an error occurs during the comparison (e.g., malformed XML).
     */
    private static List<EditedElement> diff(Object inputSource, Object outputSource, String inputXMLPath)
            throws Exception {
        List<EditedElement> differences = new ArrayList<>();
        Set<String> seenDifferences = new HashSet<>();

        Diff diff;
        try {
            diff = DiffBuilder.compare(inputSource)
                    .withTest(outputSource)
                    .normalizeWhitespace()
                    .ignoreWhitespace()
                    .ignoreComments()
                    .ignoreElementContentWhitespace()
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                    .checkForSimilar()
                    .build();
        } catch (XMLUnitException e) {
            if (e.getCause() instanceof org.xml.sax.SAXParseException) {
                AppLogger.severe("The XML file is not well-formed: " + e.getCause().getMessage());
                return null;
            } else {
                AppLogger.severe("XMLUnit error: " + e.getMessage());
                throw new Exception("Error during XML comparison", e);
            }
        }

        // Read the entire XML content into a String
        String xmlContent;
        try (Reader inputDataStream = (inputXMLPath == null)
                ? new StringReader((String) inputSource)
                : new FileReader(inputXMLPath)) {
            xmlContent = readAllLines(inputDataStream);
        } catch (IOException e) {
            AppLogger.severe("Error reading XML data: " + e.getMessage());
            throw new Exception("Error reading XML data", e);
        }

        // Process differences using the XML content
        for (Difference difference : diff.getDifferences()) {
            EditedElement editedElement = getEditedElement(xmlContent, difference);
            if (editedElement != null && seenDifferences.add(editedElement.getId()))
                differences.add(editedElement);
        }

        return differences;
    }

    /**
     * Reads all lines from a {@link Reader} and returns them as a single {@link String}.
     *
     * @param reader The {@link Reader} to read from.
     * @return The content of the {@link Reader} as a {@link String}.
     * @throws IOException If an error occurs while reading.
     */
    private static String readAllLines(Reader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                content.append(line).append(System.lineSeparator());
        }
        return content.toString();
    }

    /**
     * Extracts an {@link EditedElement} representing a detected difference in an XML file.
     *
     * @param xmlContent The content of the XML file as a {@link String}.
     * @param difference The difference detected by XMLUnit.
     * @return An {@link EditedElement} containing information about the detected difference.
     */
    private static EditedElement getEditedElement(String xmlContent, Difference difference) {
        if (difference == null) {
            AppLogger.severe("Difference is null.");
            return null;
        }

        EditedElement editedElement = new EditedElement();
        Comparison comparison = difference.getComparison();

        if (comparison == null || comparison.getControlDetails() == null || comparison.getTestDetails() == null) {
            AppLogger.severe("Comparison or control/test details are null.");
            return editedElement;
        }

        Object controlValue = comparison.getControlDetails().getValue();
        Object testValue = comparison.getTestDetails().getValue();

        String controlNodeValue = (controlValue == null) ? "null" : controlValue.toString().trim();
        String testNodeValue = (testValue == null) ? "null" : testValue.toString().trim();

        editedElement.setData(testNodeValue);

        String context = "Class: " + getParentNodeName(comparison.getControlDetails().getTarget()) +
                ", Property: " + getNodeName(comparison.getControlDetails().getTarget());
        editedElement.setId("Control: " + controlNodeValue + " => Test: " + testNodeValue + " | Context: " + context);

        try {
            int startLine = determineLineNumber(xmlContent, controlNodeValue, testNodeValue);
            int endLine = (controlNodeValue.contains("\n") || testNodeValue.contains("\n")) ?
                    findEndLine(xmlContent, startLine, controlNodeValue, testNodeValue) : startLine;
            editedElement.setStartLine(startLine);
            editedElement.setEndLine(endLine);
        } catch (IOException e) {
            AppLogger.severe("Error determining line numbers: " + e.getMessage());
        }

        return editedElement;
    }

    /**
     * Retrieves the name of the parent node of a given XML node.
     *
     * @param node The XML node whose parent name is to be determined.
     * @return The name of the parent node, or "Unknown" if not found.
     */
    private static String getParentNodeName(Object node) {
        if (node instanceof org.w3c.dom.Node) {
            org.w3c.dom.Node parent = ((org.w3c.dom.Node) node).getParentNode();
            return (parent != null) ? parent.getNodeName() : "Unknown";
        }
        return "Unknown";
    }

    /**
     * Retrieves the name of a given XML node.
     *
     * @param node The XML node whose name is to be determined.
     * @return The name of the node, or "Unknown" if not found.
     */
    private static String getNodeName(Object node) {
        return (node instanceof org.w3c.dom.Node) ? ((org.w3c.dom.Node) node).getNodeName() : "Unknown";
    }

    /**
     * Determines the line number of a given value within an XML file.
     *
     * @param xmlContent  The content of the XML file as a {@link String}.
     * @param controlValue The expected value.
     * @param testValue    The actual value found in the test XML.
     * @return The line number where the difference occurs, or -1 if not found.
     * @throws IOException If an error occurs while reading the file.
     */
    private static int determineLineNumber(String xmlContent, String controlValue, String testValue)
            throws IOException {
        if (xmlContent == null)
            throw new IllegalArgumentException("XML content cannot be null.");

        int lineNumber = -1;
        int currentLine = 0;
        try (BufferedReader reader = new BufferedReader(new StringReader(xmlContent))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (line.contains(controlValue) || line.contains(testValue))
                    return currentLine;
            }
        }
        return lineNumber;
    }

    /**
     * Finds the ending line number of a multi-line difference in the XML file.
     *
     * @param xmlContent  The content of the XML file as a {@link String}.
     * @param startLine   The starting line of the difference.
     * @param controlValue The expected value.
     * @param testValue    The actual value found in the test XML.
     * @return The ending line number of the difference.
     * @throws IOException If an error occurs while reading the file.
     */
    private static int findEndLine(String xmlContent, int startLine, String controlValue, String testValue)
            throws IOException {
        if (xmlContent == null)
            throw new IllegalArgumentException("XML content cannot be null.");

        int endLine = startLine;
        try (BufferedReader reader = new BufferedReader(new StringReader(xmlContent))) {
            int currentLine = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (currentLine >= startLine && (line.contains(controlValue) || line.contains(testValue)))
                    endLine = currentLine;
            }
        }
        return endLine;
    }
}