package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.Ontology;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * The {@code XMLFormatter} class provides functionality to format XML data with proper indentation.
 * It can format XML from a file or a string,
 * and it also supports formatting XML data within an {@link Ontology} object.
 * This class ensures XML formatting by removing unnecessary whitespace,
 * handling namespaces, and ensuring consistent indentation.
 */
public class XMLFormatter {

    private static boolean formatNamespacesOnNewLine = false;

    /**
     * Formats an XML file by adding proper indentation and returns the formatted XML as a string.
     *
     * @param filePath                  The path of the XML file to format.
     * @param formatNamespacesOnNewLine A flag to indicate whether to break lines for namespaces.
     * @return A formatted XML string.
     * @throws Exception   If an error occurs during file processing.
     * @throws IOException If the file cannot be found.
     */
    public static String formatXML(String filePath, boolean formatNamespacesOnNewLine) throws Exception {
        XMLFormatter.formatNamespacesOnNewLine = formatNamespacesOnNewLine;
        return formatXMLInner(filePath);
    }

    /**
     * Formats an XML file by adding proper indentation and returns the formatted XML as a string.
     *
     * @param filePath The path of the XML file to format.
     * @return A formatted XML string.
     * @throws Exception   If an error occurs during file processing.
     * @throws IOException If the file cannot be found.
     */
    public static String formatXML(String filePath) throws Exception {
        return formatXMLInner(filePath);
    }

    /**
     * Formats an XML file by adding proper indentation and returns the formatted XML as a string.
     *
     * @param filePath The path of the XML file to format.
     * @return A formatted XML string.
     * @throws Exception   If an error occurs during file processing.
     * @throws IOException If the file cannot be found.
     */
    private static String formatXMLInner(String filePath) throws Exception {
        File xmlFile = new File(filePath);

        // Check if the file exists
        if (!xmlFile.exists())
            throw new IOException("XML file not found: " + filePath);

        return formatXMLFromFile(xmlFile);
    }

    /**
     * Formats an XML file and returns it as a properly indented string.
     *
     * @param xmlFile The XML file to format.
     * @return A formatted XML string.
     * @throws Exception         If an error occurs during file processing.
     * @throws SAXParseException If there are errors in the XML syntax.
     */
    private static String formatXMLFromFile(File xmlFile) throws Exception {
        // Parse the XML file with namespace awareness
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);  // Enable namespace awareness
        factory.setValidating(false);     // Disable validation
        factory.setIgnoringElementContentWhitespace(false);  // Preserve whitespace within elements

        DocumentBuilder builder = factory.newDocumentBuilder();
        try {
            org.w3c.dom.Document document = builder.parse(xmlFile);  // Parse the XML file
            document.getDocumentElement().normalize();

            return transformDocumentToString(document);
        } catch (SAXParseException e) {
            // Handle XML parsing errors and return details
            throw new Exception("XML syntax error: " + e.getMessage() + " at line " + e.getLineNumber() + ", column "
                    + e.getColumnNumber(), e);
        }
    }

    /**
     * Formats the XML data of the given {@link Ontology} object
     * and returns a new {@link Ontology} object with the formatted XML data.
     *
     * @param ontology                  The Ontology object whose XML data needs to be formatted.
     * @param formatNamespacesOnNewLine A flag to indicate whether to break lines for namespaces.
     * @return A new Ontology object with the reformatted XML data.
     * @throws Exception If an error occurs during XML formatting.
     */
    public static Ontology formatOntology(Ontology ontology, boolean formatNamespacesOnNewLine) throws Exception {
        XMLFormatter.formatNamespacesOnNewLine = formatNamespacesOnNewLine;
        return formatOntologyInner(ontology);
    }

    /**
     * Formats the XML data of the given {@link Ontology} object
     * and returns a new {@link Ontology} object with the formatted XML data.
     *
     * @param ontology The Ontology object whose XML data needs to be formatted.
     * @return A new Ontology object with the reformatted XML data.
     * @throws Exception If an error occurs during XML formatting.
     */
    public static Ontology formatOntology(Ontology ontology) throws Exception {
        return formatOntologyInner(ontology);
    }

    /**
     * Formats the XML data of the given {@link Ontology} object
     * and returns a new {@link Ontology} object with the formatted XML data.
     *
     * @param ontology The Ontology object whose XML data needs to be formatted.
     * @return A new Ontology object with the reformatted XML data.
     * @throws Exception If an error occurs during XML formatting.
     */
    private static Ontology formatOntologyInner(Ontology ontology) throws Exception {
        if (ontology == null || ontology.getXmlData() == null || ontology.getXmlData().isEmpty())
            throw new IllegalArgumentException("Ontology is either null or empty.");

        // Format the XML data of the ontology
        String formattedXml = formatXMLFromString(ontology.getXmlData());

        // Create a new Ontology object with the formatted XML data
        Ontology formattedOntology = new Ontology(ontology.getOntologyName(), ontology.getOntologyExtension());
        formattedOntology.setXmlData(formattedXml);

        return formattedOntology;
    }

    /**
     * Formats an XML string and returns a properly indented version.
     *
     * @param xmlData The XML data as a string.
     * @return The formatted XML string.
     * @throws Exception         If an error occurs during XML processing.
     * @throws SAXParseException If there are errors in the XML syntax.
     */
    public static String formatXMLFromString(String xmlData) throws Exception {
        if (xmlData == null || xmlData.isEmpty())
            throw new IllegalArgumentException("XML data is null or empty.");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setCoalescing(false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Format and return the indented XML string
            return transformDocumentToString(builder.parse(new InputSource(new StringReader(xmlData))));

        } catch (SAXParseException e) {
            // Handle XML parsing errors and return details
            throw new Exception("XML syntax error: " + e.getMessage() + " at line " + e.getLineNumber() + ", column "
                    + e.getColumnNumber(), e);
        } catch (Exception e) {
            // Catch any other general exceptions
            throw new Exception("An error occurred while formatting XML: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a Document object to a formatted XML string.
     *
     * @param document The XML document to transform.
     * @return A formatted XML string.
     * @throws Exception If an error occurs during transformation.
     */
    private static String transformDocumentToString(Document document) throws Exception {
        try {
            // Removing empty XML nodes
            removeEmptyTextNodes(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Clean formatting settings
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);

            return finalFormatting(writer.toString());
        } catch (Exception e) {
            throw new Exception("Error transforming XML to string: " + e.getMessage(), e);
        }
    }

    /**
     * Final formatting of the XML string, removing unnecessary attributes and normalizing line breaks.
     *
     * @param xml The XML string to format.
     * @return The formatted XML string.
     */
    private static String finalFormatting(String xml) {
        // RegEx to find namespaces
        String regex = "(xmlns:[a-zA-Z0-9\\-]+=\"[^\"]*\")";

        // xmlns namespaces in multiple lines
        if (formatNamespacesOnNewLine)
            xml = xml.replaceAll(regex, "$0\n");
        xml = xml.replace("\r\n", "\n"); // Normalize to LF

        return xml;
    }

    /**
     * Removes empty text nodes from an XML document.
     *
     * @param node The node from which empty text nodes need to be removed.
     */
    private static void removeEmptyTextNodes(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty())
                node.removeChild(child);
            else if (child.getNodeType() == Node.ELEMENT_NODE)
                removeEmptyTextNodes(child);
        }
    }
}