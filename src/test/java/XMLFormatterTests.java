import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class XMLFormatterTests {

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

    @BeforeEach
    void setUp() {
        ontology = new Ontology();
        ontology.setOntologyName("TestOntology");
        ontology.setOntologyExtension(".owl");
        ontology.setXmlData(TEST_XML_STRING);
    }

    @Test
    void testFormatOntology() throws Exception {
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertNotNull(formattedOntology);
        assertEquals(ontology.getOntologyName(), formattedOntology.getOntologyName());
        assertEquals(ontology.getOntologyExtension(), formattedOntology.getOntologyExtension());
        assertNotNull(formattedOntology.getXmlData());
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

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

    @Test
    void testFormatOntologyAlreadyFormatted() throws Exception {
        ontology.setXmlData(EXPECTED_FORMATTED_XML);
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertTrue(formattedOntology.getXmlData().contains("&amp;"));
    }

    @Test
    void testFormatOntologyWithInvalidXml() {
        ontology.setXmlData("<invalid><xml></invalid>");
        Exception exception = assertThrows(Exception.class, () -> {
            XMLFormatter.formatOntology(ontology);
        });
        assertNotNull(exception);
    }

    @Test
    void testFormatOntologyWithNullOntology() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            XMLFormatter.formatOntology(null);
        });
        assertEquals("Ontology is either null or empty.", exception.getMessage());
    }

    @Test
    void testFormatOntologyPreservesNamespaces() throws Exception {
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertTrue(formattedOntology.getXmlData().contains("xmlns:rdf="));
        assertTrue(formattedOntology.getXmlData().contains("xmlns:owl="));
        assertTrue(formattedOntology.getXmlData().contains("xmlns:rdfs="));
        assertTrue(formattedOntology.getXmlData().contains("xmlns:skos="));
    }

    @Test
    void testFormatOntologyPreservesXmlDeclaration() throws Exception {
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertTrue(formattedOntology.getXmlData().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"));
    }

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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertTrue(formattedOntology.getXmlData().contains("&amp;"));
        assertTrue(formattedOntology.getXmlData().contains("&lt;"));
        assertTrue(formattedOntology.getXmlData().contains("&gt;"));
    }

    // Case 1: Handling excessive whitespace around tags
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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

    // Case 2: Handling empty elements or self-closing tags
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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertEquals(expected.trim(), formattedOntology.getXmlData().trim());
    }

    // Case 3: Handling complex nested XML elements
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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertEquals(expected.trim(), formattedOntology.getXmlData().trim());
    }

    // Case 4: Handling multiple namespace declarations for the same prefix
    @Test
    void testFormatOntologyWithMultipleNamespaces() throws Exception {
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
        assertThrows(Exception.class, () -> XMLFormatter.formatOntology(ontology), "duplicated namespaces");
    }

    // Case 5: Special characters in attribute values
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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertTrue(formattedOntology.getXmlData().contains("&lt;"));
        assertTrue(formattedOntology.getXmlData().contains("&gt;"));
        assertTrue(formattedOntology.getXmlData().contains("&amp;"));
    }

    // Case 6: Mixed line endings (LF and CRLF)
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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        assertEquals(EXPECTED_FORMATTED_XML, formattedOntology.getXmlData().trim());
    }

    // Case 7: Performance test with large XML files (mocking large data)
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
        Ontology formattedOntology = XMLFormatter.formatOntology(ontology);
        long endTime = System.currentTimeMillis();
        System.out.println("Large XML formatting took: " + (endTime - startTime) + "ms");
        assertNotNull(formattedOntology);
    }
}