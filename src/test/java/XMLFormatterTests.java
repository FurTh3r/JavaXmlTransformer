import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link XMLFormatter} class. This class tests the various functionalities
 * of formatting an {@link Ontology} object and validating XML data.
 *
 * <p>Each test case verifies the proper handling of XML data, including formatting, special characters,
 * invalid XML, whitespace issues, and other edge cases.</p>
 */
class XMLFormatterTests {

    /**
     * Sample XML string used for testing.
     */
    private static final String TEST_XML_STRING = """
            <?xml version="1.0" encoding="UTF-8" standalone="no"?>
            <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
             xmlns:owl="http://www.w3.org/2002/07/owl#"
             xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
             xmlns:skos="http://www.w3.org/2004/02/skos/core#"
             xml:base="http://www.persone/">
              <owl:Class rdf:about="http://www.persone#Individuo">
                <rdfs:label xml:lang="it">Ind</rdfs:label>
                <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
              </owl:Class>
            </rdf:RDF>
            """;

    /**
     * Expected formatted XML string after calling the {@link XMLFormatter#formatOntology(Ontology)} method.
     */
    private static final String EXPECTED_FORMATTED_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="no"?>
            <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
             xmlns:owl="http://www.w3.org/2002/07/owl#"
             xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
             xmlns:skos="http://www.w3.org/2004/02/skos/core#"
             xml:base="http://www.persone/">
              <owl:Class rdf:about="http://www.persone#Individuo">
                <rdfs:label xml:lang="it">Ind</rdfs:label>
                <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
              </owl:Class>
            </rdf:RDF>
            """.trim();

    private Ontology ontology;

    /**
     * Sets up the test environment by initializing the {@link Ontology} object.
     */
    @BeforeEach
    void setUp() {
        ontology = new Ontology();
        ontology.setOntologyName("TestOntology");
        ontology.setOntologyExtension(".owl");
        ontology.setXmlData(TEST_XML_STRING);
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure the ontology is formatted correctly.
     *
     * @throws Exception If any error occurs during the formatting.
     */
    @Test
    void testFormatOntology() throws Exception {
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertNotNull(formattedOntology);
        assertEquals(ontology.getOntologyName(), formattedOntology.getOntologyName());
        assertEquals(ontology.getOntologyExtension(), formattedOntology.getOntologyExtension());
        assertNotNull(formattedOntology.getXmlData());
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method with an empty ontology object.
     *
     * @throws IllegalArgumentException if the ontology is empty.
     */
    @Test
    void testFormatOntologyWithEmptyData() {
        Ontology emptyOntology = new Ontology();
        emptyOntology.setOntologyName("EmptyOntology");
        emptyOntology.setOntologyExtension(".owl");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            XMLFormatter.formatOntology(emptyOntology);
        });

        assertEquals("Ontology is either null or empty.", exception.getMessage());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method when the ontology XML data is already formatted.
     *
     * @throws Exception If any error occurs during the formatting.
     */
    @Test
    void testFormatOntologyAlreadyFormatted() throws Exception {
        ontology.setXmlData(EXPECTED_FORMATTED_XML);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure the XML is formatted correctly, even with
     * incorrect indentation in the input XML.
     *
     * @throws Exception If any error occurs during the formatting.
     */
    @Test
    void testFormatOntologyWithIncorrectIndentation() throws Exception {
        String unformattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" +
                "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"" +
                "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"" +
                "    xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" " +
                "    xml:base=\"http://www.persone/\">" +
                "    <owl:Class rdf:about=\"http://www.persone#Individuo\">" +
                "        <rdfs:label xml:lang=\"it\">Ind</rdfs:label>" +
                "        <skos:scopeNote xml:lang=\"it\">Class</skos:scopeNote>" +
                "    </owl:Class>" +
                "</rdf:RDF>";
        ontology.setXmlData(unformattedXml);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure special characters in the XML are preserved
     * correctly.
     *
     * @throws Exception If any error occurs during the formatting.
     */
    @Test
    void testFormatOntologyWithSpecialCharacters() throws Exception {
        String xmlWithSpecialChars = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                  <owl:Class rdf:about="http://www.persone#Individuo">
                    <rdfs:label xml:lang="it">&amp;</rdfs:label>
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                  </owl:Class>
                </rdf:RDF>
                """;
        ontology.setXmlData(xmlWithSpecialChars);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertTrue(formattedOntology.getXmlData().contains("&amp;"));
    }

    /**
     * Tests that the {@link XMLFormatter#formatOntology(Ontology)} method throws an exception for invalid XML input.
     */
    @Test
    void testFormatOntologyWithInvalidXml() {
        ontology.setXmlData("<invalid><xml></invalid>");
        Exception exception = assertThrows(Exception.class, () -> {
            XMLFormatter.formatOntology(ontology, true);
        });
        assertNotNull(exception);
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method with a null ontology.
     * This test verifies that an IllegalArgumentException is thrown when the input ontology is null.
     */
    @Test
    void testFormatOntologyWithNullOntology() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            XMLFormatter.formatOntology(null);
        });
        assertEquals("Ontology is either null or empty.", exception.getMessage());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure it preserves
     * the important namespaces (rdf, owl, rdfs, skos) in the formatted ontology.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyPreservesNamespaces() throws Exception {
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertTrue(formattedOntology.getXmlData().contains("xmlns:rdf="));
        assertTrue(formattedOntology.getXmlData().contains("xmlns:owl="));
        assertTrue(formattedOntology.getXmlData().contains("xmlns:rdfs="));
        assertTrue(formattedOntology.getXmlData().contains("xmlns:skos="));
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure the XML declaration is preserved.
     * The formatted XML should start with the XML declaration:
     * `<?xml version="1.0" encoding="UTF-8" standalone="no"?>`
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyPreservesXmlDeclaration() throws Exception {
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertTrue(formattedOntology.getXmlData()
                .startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"));
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure that special characters like
     * `&`, `<`, and `>` are correctly encoded as `&amp;`, `&lt;`, and `&gt;` respectively.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithSpecialCharacters2() throws Exception {
        String xmlWithSpecialChars = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                  <owl:Class rdf:about="http://www.persone#Individuo">
                    <rdfs:label xml:lang="it">Special &amp; &lt; &gt; Characters</rdfs:label>
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                  </owl:Class>
                </rdf:RDF>
                """;
        ontology.setXmlData(xmlWithSpecialChars);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertTrue(formattedOntology.getXmlData().contains("&amp;"));
        assertTrue(formattedOntology.getXmlData().contains("&lt;"));
        assertTrue(formattedOntology.getXmlData().contains("&gt;"));
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure that excessive whitespace around
     * tags is properly handled and removed from the formatted XML.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithExcessiveWhitespace() throws Exception {
        String xmlWithWhitespaces = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"                    \s
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"                    \s
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">                    \s
                  <owl:Class rdf:about="http://www.persone#Individuo">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>                     \s
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                  </owl:Class>                    \t\t\t
                </rdf:RDF> \t\t\t
                """;
        ontology.setXmlData(xmlWithWhitespaces);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure that empty elements or
     * self-closing tags are handled correctly.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithEmptyElement() throws Exception {
        String xmlWithEmptyElement = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                        xmlns:owl="http://www.w3.org/2002/07/owl#"
                        xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                         xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                  <owl:Class rdf:about="http://www.persone#Individuo"               />
                        </rdf:RDF>
                """;

        String expected = """
                <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                  <owl:Class rdf:about="http://www.persone#Individuo"/>
                </rdf:RDF>
                """;
        ontology.setXmlData(xmlWithEmptyElement);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertEquals(expected.trim(), formattedOntology.getXmlData().trim());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure that complex nested XML elements
     * are formatted correctly without any loss of data.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithComplexNestedElements() throws Exception {
        String complexXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                        xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                       xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                                                <owl:Class rdf:about="http://www.persone#Individuo">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    <rdf:Description>
                      <rdfs:label xml:lang="en">Individual</rdfs:label>
                                <skos:scopeNote xml:lang="en">Description</skos:scopeNote>
                               </rdf:Description>
                            </owl:Class>
                </rdf:RDF>
                """;

        String expected = """
                <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                  <owl:Class rdf:about="http://www.persone#Individuo">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    <rdf:Description>
                      <rdfs:label xml:lang="en">Individual</rdfs:label>
                      <skos:scopeNote xml:lang="en">Description</skos:scopeNote>
                    </rdf:Description>
                  </owl:Class>
                </rdf:RDF>
                """;
        ontology.setXmlData(complexXml);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertEquals(expected.trim(), formattedOntology.getXmlData().trim());
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method with multiple namespace declarations
     * for the same prefix.
     * This test verifies that an exception is thrown when the XML has duplicate namespace
     * declarations for the same prefix.
     */
    @Test
    void testFormatOntologyWithMultipleNamespaces() {
        String xmlWithMultipleNamespaces = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:rdf="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                  <owl:Class rdf:about="http://www.persone#Individuo">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                  </owl:Class>
                </rdf:RDF>
                """;
        ontology.setXmlData(xmlWithMultipleNamespaces);
        assertThrows(Exception.class, () -> XMLFormatter.formatOntology(ontology, true),
                "duplicated namespaces");
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure that special characters in attribute values
     * (e.g., "<", ">", "&") are properly encoded as `&lt;`,
     * `&gt;`, and `&amp;` respectively in the formatted ontology.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithSpecialCharactersInAttributes() throws Exception {
        String xmlWithSpecialCharsInAttributes = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                 xml:base="http://www.persone/">
                  <owl:Class rdf:about="http://www.persone#Individuo" someAttribute="&lt;&gt;&amp;">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                  </owl:Class>
                </rdf:RDF>
                """;
        ontology.setXmlData(xmlWithSpecialCharsInAttributes);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertTrue(formattedOntology.getXmlData().contains("&lt;"));
        assertTrue(formattedOntology.getXmlData().contains("&gt;"));
        assertTrue(formattedOntology.getXmlData().contains("&amp;"));
    }

    /**
     * Tests the {@link XMLFormatter#formatOntology(Ontology)} method to ensure that XML with mixed line endings
     * (LF and CRLF) is correctly formatted with consistent line endings.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithMixedLineEndings() throws Exception {
        String xmlWithMixedLineEndings = """
                <?xml version="1.0" encoding="UTF-8"?>\r
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"\r
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#" \r
                 xml:base="http://www.persone/">\r
                 <owl:Class rdf:about="http://www.persone#Individuo">
                   <rdfs:label xml:lang="it">Ind</rdfs:label>\r
                   <skos:scopeNote xml:lang="it">Class</skos:scopeNote>\r
                 </owl:Class>
                </rdf:RDF>\r
                """;
        ontology.setXmlData(xmlWithMixedLineEndings);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

    /**
     * Performance test for the {@link XMLFormatter#formatOntology(Ontology)} method with large XML files.
     * This test verifies that the method can handle large XML datasets efficiently.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithLargeFile() throws Exception {
        String largeXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 xmlns:owl="http://www.w3.org/2002/07/owl#"
                 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                 xmlns:skos="http://www.w3.org/2004/02/skos/core#" xml:base="http://www.persone/">
                """ + ("""
                  <owl:Class rdf:about="http://www.persone#Individuo">
                    \
                <rdfs:label xml:lang="it">Ind</rdfs:label>
                  </owl:Class>
                """).repeat(1000) +
                "</rdf:RDF>";

        ontology.setXmlData(largeXml);
        long startTime = System.currentTimeMillis();
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology, true);
        long endTime = System.currentTimeMillis();
        System.out.println("Large XML formatting took: " + (endTime - startTime) + "ms");
        assertNotNull(formattedOntology);
    }

    /**
     * Performance test for the {@link XMLFormatter#formatOntology(Ontology)} method with large XML files.
     * This test verifies that the method can handle large XML datasets efficiently.
     *
     * @throws Exception if an error occurs during the formatting process
     */
    @Test
    void testFormatOntologyWithLargeFileSingleLineNamespaces() throws Exception {
        String largeXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xml:base="http://www.persone/">
                """ + ("""
                  <owl:Class rdf:about="http://www.persone#Individuo">
                    \
                <rdfs:label xml:lang="it">Ind</rdfs:label>
                  </owl:Class>
                """).repeat(1000) +
                "</rdf:RDF>";

        ontology.setXmlData(largeXml);
        long startTime = System.currentTimeMillis();
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        long endTime = System.currentTimeMillis();
        System.out.println("Large XML formatting took: " + (endTime - startTime) + "ms");
        assertNotNull(formattedOntology);
    }
}