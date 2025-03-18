package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The {@code XMLErrorReporter} class processes XML data (as a String) and a list of EditedElement objects
 * to generate a list of ErrorInfo objects representing the errors encountered.
 */
public class XMLErrorReporter {

    private final Map<Node, Integer> startLineNumbers = new HashMap<>();
    private final Map<Node, Integer> endLineNumbers = new HashMap<>();
    private final Document document;

    public XMLErrorReporter(String xmlString) throws Exception {
        // Parse the XML document from the string input
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.document = builder.parse(inputStream);
    }

    /**
     * Given an XML string and a list of EditedElement objects, returns a list of ErrorInfo objects
     * with the start and end line numbers, error message, and element details for each element.
     *
     * @param xmlString       The XML string.
     * @param editedElements  A list of EditedElement objects containing the XPath and other details.
     * @return A list of ErrorInfo objects representing the errors found.
     * @throws Exception If an error occurs while processing the XML or parsing.
     */
    public List<ErrorInfo> generateErrorInfo(String xmlString, List<EditedElement> editedElements) throws Exception {
        List<ErrorInfo> errorInfos = new ArrayList<>();

        // Parse the XML string and extract line numbers
        parseXMLWithSAX(xmlString);

        // Iterate through the EditedElement objects
        for (EditedElement editedElement : editedElements) {
            try {
                String elementXPath = editedElement.getxPath();

                // Find nodes based on the XPath (you can implement the XPath logic here)
                Node node = findNodeByXPath(elementXPath);

                // If the node was found, use the stored line numbers
                if (node != null) {
                    int startLine = startLineNumbers.getOrDefault(node, 0);
                    int endLine = endLineNumbers.getOrDefault(node, 0);

                    String errorMessage = "Error in element: " + node.getNodeName();
                    String elementDetails = "XPath: " + elementXPath + ", Node Name: " + node.getNodeName();

                    // Add to the error list
                    ErrorInfo errorInfo = new ErrorInfo(startLine, endLine, errorMessage, elementDetails);
                    errorInfos.add(errorInfo);
                }
            } catch (Exception e) {
                throw new Exception("Error evaluating XPath: " + editedElement.getxPath(), e);
            }
        }

        return errorInfos;
    }

    /**
     * Parses the XML string using SAXParser and tracks line numbers for each element.
     *
     * @param xmlString The XML string to parse.
     * @throws Exception If an error occurs while parsing the XML.
     */
    private void parseXMLWithSAX(String xmlString) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        // Custom handler to track line numbers
        DefaultHandler handler = new DefaultHandler() {
            private Locator locator;

            @Override
            public void setDocumentLocator(Locator locator) {
                this.locator = locator;
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                // Find the corresponding DOM Node
                Node node;
                try {
                    node = findNodeByXPath("/" + qName);
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
                if (node != null) {
                    startLineNumbers.put(node, locator.getLineNumber());
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                // Find the corresponding DOM Node
                Node node;
                try {
                    node = findNodeByXPath("/" + qName);
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
                if (node != null) {
                    endLineNumbers.put(node, locator.getLineNumber());
                }
            }
        };

        parser.parse(inputStream, handler);
    }

    /**
     * Finds a Node by XPath expression in the XML document.
     *
     * @param xPath The XPath expression to evaluate.
     * @return The corresponding Node or null if not found.
     * @throws XPathExpressionException If the XPath expression cannot be evaluated.
     */
    public Node findNodeByXPath(String xPath) throws XPathExpressionException {
        // Create XPath object
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        // Compile the XPath expression
        XPathExpression expr = xpath.compile(xPath);

        // Evaluate the expression against the document and return the first matching node
        return (Node) expr.evaluate(document, XPathConstants.NODE);
    }
}