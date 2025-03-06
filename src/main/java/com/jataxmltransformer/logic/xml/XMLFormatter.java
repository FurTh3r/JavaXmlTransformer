package com.jataxmltransformer.logic.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * The {@code XMLFormatter} class provides functionality to format an XML file with proper indentation.
 * <p>
 * It reads an XML file, formats it, and returns the formatted XML as a string.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 *     String formattedXml = XMLFormatter.formatXML("path/to/xmlfile.xml");
 *     System.out.println(formattedXml);
 * </pre>
 */
public class XMLFormatter {

    /**
     * Formats an XML file by adding proper indentation and returns the formatted XML as a string.
     *
     * @param filePath The path of the XML file to format.
     * @return A formatted XML string.
     * @throws Exception If an error occurs during file processing.
     */
    public static String formatXML(String filePath) throws Exception {
        File xmlFile = new File(filePath);

        if (!xmlFile.exists())
            throw new IOException("XML file not found: " + filePath);

        // Parse the XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true); // Remove unnecessary whitespace
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(xmlFile);
        document.normalizeDocument(); // Normalize the document structure

        // Prepare a transformer for formatted output
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // Set indentation size

        // Write formatted XML to a string
        StringWriter writer = new StringWriter();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        return writer.toString();
    }
}
