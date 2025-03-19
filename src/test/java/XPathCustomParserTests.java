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
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                    <owl:ObjectProperty rdf:about="http://www.persone#hasName">
                        <rdfs:label xml:lang="it">Name</rdfs:label>
                        <rdfs:domain rdf:resource="http://www.persone#Individuo"/>
                        <rdfs:range rdf:resource="http://www.persone#Name"/>
                    </owl:ObjectProperty>
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
        assertEquals(17, info.getSecond(), "End line for RDF should be last line of the document");
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

    /**
     * Tests behavior when querying for an XPath that does not exist in the XML.
     */
    @Test
    void testInnerClassLabelXPath() {
        MyPair<Integer, Integer> labelInfo = parser.getInfoFromXPath("/RDF[1]/Class[1]/label[1]");
        assertNotNull(labelInfo);
        assertEquals(5, labelInfo.getFirst(), "Start line for label should be 5");
        assertEquals(5, labelInfo.getSecond(), "End line for label should be 5");
    }

    /**
     * Tests behavior when querying for a deeply nested element.
     */
    @Test
    void testDeeplyNestedElement() {
        MyPair<Integer, Integer> domainInfo = parser.getInfoFromXPath("/RDF[1]/ObjectProperty[1]/domain[1]");
        assertNotNull(domainInfo);
        assertEquals(14, domainInfo.getFirst(), "Start line for domain should be 14");
        assertEquals(14, domainInfo.getSecond(), "End line for domain should be 14");
    }

    /**
     * Tests behavior when querying for an element with multiple siblings.
     */
    @Test
    void testMultipleSiblings() {
        MyPair<Integer, Integer> class2Info = parser.getInfoFromXPath("/RDF[1]/Class[2]");
        assertNotNull(class2Info);
        assertEquals(8, class2Info.getFirst(), "Start line for second Class should be 8");
        assertEquals(11, class2Info.getSecond(), "End line for second Class should be 11");
    }

    /**
     * Tests behavior when querying for an element with attributes.
     */
    @Test
    void testElementWithAttributes() {
        MyPair<Integer, Integer> objectPropertyInfo = parser.getInfoFromXPath("/RDF[1]/ObjectProperty[1]");
        assertNotNull(objectPropertyInfo);
        assertEquals(12, objectPropertyInfo.getFirst(),
                "Start line for ObjectProperty should be 12");
        assertEquals(16, objectPropertyInfo.getSecond(),
                "End line for ObjectProperty should be 16");
    }

    /**
     * Tests behavior when querying for an element with a specific attribute.
     */
    @Test
    void testElementWithSpecificAttribute() {
        MyPair<Integer, Integer> labelInfo = parser.getInfoFromXPath("/RDF[1]/ObjectProperty[1]/label[1]");
        assertNotNull(labelInfo);
        assertEquals(13, labelInfo.getFirst(),
                "Start line for label in ObjectProperty should be 13");
        assertEquals(13, labelInfo.getSecond(),
                "End line for label in ObjectProperty should be 13");
    }

    /**
     * Tests behavior when querying for an element with multiple occurrences.
     */
    @Test
    void testMultipleOccurrences() {
        MyPair<Integer, Integer> scopeNoteInfo = parser.getInfoFromXPath("/RDF[1]/Class[2]/scopeNote[1]");
        assertNotNull(scopeNoteInfo);
        assertEquals(10, scopeNoteInfo.getFirst(),
                "Start line for scopeNote in second Class should be 10");
        assertEquals(10, scopeNoteInfo.getSecond(),
                "End line for scopeNote in second Class should be 10");
    }

    /**
     * Tests behavior when querying for an element with mixed content.
     */
    @Test
    void testMixedContentElement() {
        MyPair<Integer, Integer> labelInfo = parser.getInfoFromXPath("/RDF[1]/Class[1]/label[1]");
        assertNotNull(labelInfo);
        assertEquals(5, labelInfo.getFirst(), "Start line for label in first Class should be 5");
        assertEquals(5, labelInfo.getSecond(), "End line for label in first Class should be 5");
    }
}