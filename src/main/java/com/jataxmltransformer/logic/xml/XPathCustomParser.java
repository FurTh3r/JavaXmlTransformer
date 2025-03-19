package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.utilities.MyPair;
import com.jataxmltransformer.logs.AppLogger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.*;

/**
 * Parses an XML string and provides a method to retrieve the start and end line numbers for a given XPath.
 */
public class XPathCustomParser implements IXPathCustomParser {

    private final Map<String, LineData> lines;

    /**
     * Constructs an XPathCustomParser with the provided XML data.
     *
     * @param xmlData The XML data as a string.
     */
    public XPathCustomParser(String xmlData) {
        this.lines = parse(xmlData);
    }

    /**
     * Parses the given XML data using SAX parser and returns a map of XPath to LineData.
     *
     * @param xmlData The XML data as a string.
     * @return A map where the key is the XPath, and the value is the corresponding LineData.
     */
    private static Map<String, LineData> parse(String xmlData) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SAXHandler handler = new SAXHandler();
            saxParser.parse(new InputSource(new StringReader(xmlData)), handler);
            return handler.getElements();
        } catch (Exception e) {
            AppLogger.severe(e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Retrieves the start and end line numbers for the given XPath.
     *
     * @param xPath The XPath to search for.
     * @return A MyPair object containing the start and end line numbers, or (-1, -1) if not found.
     */
    @Override
    public MyPair<Integer, Integer> getInfoFromXPath(String xPath) {
        LineData lineData = lines.get(xPath);
        if (lineData == null) {
            return new MyPair<>(-1, -1); // Default value when XPath is not found
        }
        return new MyPair<>(lineData.beginLineNumber, lineData.endLineNumber);
    }

    /**
     * Represents the line data for an XML element.
     */
    private record LineData(String tagName, int beginLineNumber, int endLineNumber) {
        /**
         * Constructs a LineData object.
         *
         * @param tagName         The tag name of the XML element.
         * @param beginLineNumber The line number where the element starts.
         * @param endLineNumber   The line number where the element ends.
         */
        private LineData {
        }

        @Override
        public String toString() {
            return String.format("%s, %d, %d", tagName, beginLineNumber, endLineNumber);
        }
    }

    /**
     * SAX handler that processes XML elements and records their XPath and line numbers.
     */
    private static class SAXHandler extends DefaultHandler {

        private final Map<String, LineData> elements = new HashMap<>();
        private final Deque<String> pathStack = new ArrayDeque<>();
        private final Map<String, Integer> elementCount = new HashMap<>();
        private final Map<String, Integer> startLineMap = new HashMap<>();
        private Locator locator;

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            String tagName = stripNamespace(qName);
            int lineNumber = locator.getLineNumber();

            // Track occurrence count for correct XPath indexing
            elementCount.put(tagName, elementCount.getOrDefault(tagName, 0) + 1);
            int index = elementCount.get(tagName);

            // Construct the XPath based on parent elements and current index
            String parentXPath = pathStack.isEmpty() ? "" : pathStack.peek();
            String xPath = String.format("%s/%s[%d]", parentXPath, tagName, index);

            pathStack.push(xPath);
            startLineMap.put(xPath, lineNumber);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            String tagName = stripNamespace(qName);
            String xPath = pathStack.pop();
            int startLine = startLineMap.getOrDefault(xPath, -1);
            int endLine = locator.getLineNumber();

            elements.put(xPath, new LineData(tagName, startLine, endLine));
        }

        /**
         * Strips the namespace from a qualified XML tag name.
         *
         * @param qName The qualified name (with namespace).
         * @return The local part of the tag name (without namespace).
         */
        private String stripNamespace(String qName) {
            return qName.contains(":") ? qName.substring(qName.indexOf(":") + 1) : qName;
        }

        /**
         * Retrieves the map of elements with their respective line numbers.
         *
         * @return A map where the key is the XPath and the value is the corresponding LineData.
         */
        public Map<String, LineData> getElements() {
            return elements;
        }
    }
}