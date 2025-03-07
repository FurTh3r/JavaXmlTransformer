package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logs.AppLogger;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Object controlValue = comparison.getControlDetails().getValue();
        Object testValue = comparison.getTestDetails().getValue();

        String controlNodeValue = (controlValue == null) ? "null" : controlValue.toString().trim();
        String testNodeValue = (testValue == null) ? "null" : testValue.toString().trim();

        editedElement.setData(testNodeValue);

        // Add context to the message
        String context = "Class: " + getParentNodeName(comparison.getControlDetails().getTarget()) + ", Property: "
                + getNodeName(comparison.getControlDetails().getTarget());
        editedElement.setId("Control: " + controlNodeValue + " => Test: " + testNodeValue + " | Context: " + context);

        try {
            int lineNumber = determineLineNumber(inputXMLPath, controlNodeValue, testNodeValue);
            editedElement.setLine(lineNumber);
        } catch (IOException e) {
            AppLogger.severe("Error determining line number: " + e.getMessage());
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
            if (parent != null) {
                return parent.getNodeName();
            }
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
        if (node instanceof org.w3c.dom.Node) {
            return ((org.w3c.dom.Node) node).getNodeName();
        }
        return "Unknown";
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
    private static int determineLineNumber(String xmlFilePath, String controlValue, String testValue)
            throws IOException {
        int lineNumber = -1;
        int currentLine = 0;

        File file = new File(xmlFilePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (line.contains(controlValue) || line.contains(testValue)) {
                    lineNumber = currentLine;
                    break;
                }
            }
        }
        return lineNumber;
    }

    /**
     * Compares two XML files and identifies the differences.
     *
     * @param inputXMLPath  The path to the original XML file.
     * @param outputXMLPath The path to the modified XML file.
     * @return A list of {@link EditedElement} objects representing the differences found.
     */
    @Override
    public List<EditedElement> diff(String inputXMLPath, String outputXMLPath) {
        List<EditedElement> differences = new ArrayList<>();
        Set<String> seenDifferences = new HashSet<>(); // To avoid duplicates

        StreamSource input = new StreamSource(new File(inputXMLPath));
        StreamSource output = new StreamSource(new File(outputXMLPath));

        Diff diff = DiffBuilder
                .compare(input)
                .withTest(output)
                .normalizeWhitespace()
                .ignoreWhitespace()
                .ignoreComments()
                .ignoreElementContentWhitespace()
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText))
                .checkForSimilar()
                .build();

        for (Difference difference : diff.getDifferences()) {
            EditedElement editedElement = getEditedElement(inputXMLPath, difference);

            if (seenDifferences.add(editedElement.getId())) { // Avoid duplicates
                differences.add(editedElement);
                AppLogger.info("Found difference at " + difference.getComparison().getControlDetails().getXPath()
                        + " | Expected: " + difference.getComparison().getControlDetails().getValue()
                        + " | Found: " + difference.getComparison().getTestDetails().getValue()
                        + " | Control Node: " + difference.getComparison().getControlDetails().getTarget()
                        + " | Test Node: " + difference.getComparison().getTestDetails().getTarget());
            }
        }
        return differences;
    }
}