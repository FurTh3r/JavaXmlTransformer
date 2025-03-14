package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logs.AppLogger;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

/**
 * The {@code XMLDiffChecker} class compares two XML files using XMLUnit and identifies the differences.
 */
public class XMLDiffChecker implements DiffChecker {

    /**
     * Extracts an {@link EditedElement} representing a detected difference in an XML file.
     *
     * @param inputXMLPath The path to the original XML file.
     * @param difference   The difference detected by XMLUnit.
     * @return An {@link EditedElement} containing information about the detected difference.
     */
    private static EditedElement getEditedElement(String inputXMLPath, Difference difference) {
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
            int startLine = determineLineNumber(inputXMLPath, controlNodeValue, testNodeValue);
            int endLine = (controlNodeValue.contains("\n") || testNodeValue.contains("\n")) ?
                    findEndLine(inputXMLPath, startLine, controlNodeValue, testNodeValue) : startLine;
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
     * @param xmlFilePath  The path to the XML file.
     * @param controlValue The expected value.
     * @param testValue    The actual value found in the test XML.
     * @return The line number where the difference occurs, or -1 if not found.
     * @throws IOException If an error occurs while reading the file.
     */
    private static int determineLineNumber(String xmlFilePath, String controlValue, String testValue) throws IOException {
        int lineNumber = -1;
        int currentLine = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(xmlFilePath))) {
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
     * @param xmlFilePath  The path to the XML file.
     * @param startLine    The starting line of the difference.
     * @param controlValue The expected value.
     * @param testValue    The actual value found in the test XML.
     * @return The ending line number of the difference.
     * @throws IOException If an error occurs while reading the file.
     */
    private static int findEndLine(String xmlFilePath, int startLine, String controlValue, String testValue) throws IOException {
        int endLine = startLine;
        try (BufferedReader reader = new BufferedReader(new FileReader(xmlFilePath))) {
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

    /**
     * Compares two XML files and identifies the differences.
     *
     * @param inputXMLPath  The path to the original XML file.
     * @param outputXMLPath The path to the modified XML file.
     * @return A list of {@link EditedElement} objects representing the differences found.
     * @throws IOException If an error occurs while accessing the XML files.
     */
    @Override
    public List<EditedElement> diff(String inputXMLPath, String outputXMLPath) throws IOException {
        List<EditedElement> differences = new ArrayList<>();
        Set<String> seenDifferences = new HashSet<>();

        File inputXML = new File(inputXMLPath);
        File outputXML = new File(outputXMLPath);

        if (!inputXML.exists() || !outputXML.exists())
            throw new IOException("One or both XML files do not exist.");

        Diff diff;
        try {
            diff = DiffBuilder.compare(new StreamSource(inputXML))
                    .withTest(new StreamSource(outputXML))
                    .normalizeWhitespace()
                    .ignoreWhitespace()
                    .ignoreComments()
                    .ignoreElementContentWhitespace()
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                    .checkForSimilar()
                    .build();
        } catch (XMLUnitException e) {
            AppLogger.severe(e.getMessage());
            return null;
        }

        for (Difference difference : diff.getDifferences()) {
            EditedElement editedElement = getEditedElement(inputXMLPath, difference);
            if (seenDifferences.add(editedElement.getId()))
                differences.add(editedElement);
        }
        return differences;
    }
}