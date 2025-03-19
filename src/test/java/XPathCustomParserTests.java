import com.jataxmltransformer.logic.utilities.MyPair;
import com.jataxmltransformer.logic.xml.XPathCustomParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for {@link XPathCustomParser}.
 * Ensures correct parsing and line number extraction from XML using XPath.
 */
class XPathCustomParserTests {

    private XPathCustomParser parser;

    /**
     * Sets up the test environment by initializing the parser with sample XML data.
     */
    @BeforeEach
    void setUp() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xml:base="http://www.persone/">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
                """;
        parser = new XPathCustomParser(xml);
    }

    /**
     * Tests if the root RDF element is correctly identified with the right line numbers.
     */
    @Test
    void testRootElement() {
        MyPair<Integer, Integer> info = parser.getInfoFromXPath("/RDF[1]");
        assertNotNull(info);
        assertEquals(2, info.getFirst(), "Start line for RDF should be 2");
        assertEquals(8, info.getSecond(), "End line for RDF should be last line of the document");
    }

    /**
     * Tests if a nested element (Class) is correctly identified with the right line numbers.
     */
    @Test
    void testNestedElements() {
        MyPair<Integer, Integer> classInfo = parser.getInfoFromXPath("/RDF[1]/Class[1]");
        assertNotNull(classInfo);
        assertEquals(4, classInfo.getFirst(), "Start line for Class should be 4");
        assertEquals(7, classInfo.getSecond(), "End line for Class should be 7");
    }

    /**
     * Tests if namespaces are correctly stripped from element names in XPath.
     */
    @Test
    void testNamespaceStripping() {
        MyPair<Integer, Integer> scopeNoteInfo = parser.getInfoFromXPath("/RDF[1]/Class[1]/scopeNote[1]");
        assertNotNull(scopeNoteInfo);
        assertEquals(6, scopeNoteInfo.getFirst(), "Start line for scopeNote should be 6");
        assertEquals(6, scopeNoteInfo.getSecond(), "End line for scopeNote should be 6");
    }

    /**
     * Tests if single-line elements are correctly identified.
     */
    @Test
    void testSingleLineElements() {
        MyPair<Integer, Integer> labelInfo = parser.getInfoFromXPath("/RDF[1]/label[1]");
        assertNotNull(labelInfo);
        assertEquals(3, labelInfo.getFirst(), "Start line for label should be 3");
        assertEquals(3, labelInfo.getSecond(), "End line for label should be 3");
    }

    /**
     * Tests behavior when querying for an XPath that does not exist in the XML.
     */
    @Test
    void testInvalidXPath() {
        MyPair<Integer, Integer> invalidInfo = parser.getInfoFromXPath("/RDF[1]/NonExistentTag[1]");
        assertNotNull(invalidInfo);
        assertEquals(-1, invalidInfo.getFirst(), "Start line should be -1 for non-existent tag");
        assertEquals(-1, invalidInfo.getSecond(), "End line should be -1 for non-existent tag");
    }
}