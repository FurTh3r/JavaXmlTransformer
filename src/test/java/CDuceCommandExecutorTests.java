import com.jataxmltransformer.logic.cducecompiler.CDuceCodeLoader;
import com.jataxmltransformer.logic.cducecompiler.CDuceCommandExecutor;
import com.jataxmltransformer.logic.data.Ontology;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CDuceCommandExecutorTests {
    private static Ontology input;
    CDuceCommandExecutor commandExecutor = new CDuceCommandExecutor();

    @BeforeAll
    static void setUp() {
        // Initializing default structure for testing
        List<String> namespaces = List.of("namespace www = \"http://www.persone#\";;");
        List<String> structure = List.of("type Ontology = <rdf:RDF xml:base=String> [ Class* ]",
                "type Class = <owl:Class rdf:about=String> [ ClassAtt* ]",
                "type ClassAtt = SubClass | EqClass | Label | Note",
                "type SubClass = <rdfs:subClassOf rdf:resource=String> []",
                "type EqClass  = <owl:equivalentClass> [ EqAttr ]" + "| <owl:equivalentClass rdf:resource=String> []",
                "type EqAttr   = <owl:Restriction> [ AnyXml* ]",
                "type Label    = <rdfs:label xml:lang=String> String",
                "type Note     = <skos:scopeNote xml:lang=String> String");
        List<String> attributes = List.of("SubClass", "EqClass", "Label", "Note");
        List<String> classes = List.of("<owl:Class rdf:about=cls>");

        // Loading the structure with CDuceCodeLoader (Replaces the namespaces)
        CDuceCodeLoader.loadCheckStructure(namespaces, structure, attributes, classes);

        input = new Ontology();

        input.setOntologyName("test");
        input.setOntologyName(".xml");
    }

    @Test
    void verifyOntologyCorrect() throws Exception {
        // Verifying a correct ontology

        // Loading correct ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
                """);

        assertTrue(commandExecutor.verifyOntology(input));
    }

    @Test
    void verifyOntologySyntaxError() throws Exception {
        // Verifying an ontology with a syntax error

        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF
                """); // Missing > for closing

        assertFalse(commandExecutor.verifyOntology(input));
    }

    @Test
    void verifyOntologySemanticError1() throws Exception {
        // Verifying an ontology with a semantic error

        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                        <owl:Class rdf:about="http://www.persone#Individuo">
                            <rdfs:label xml:lang="it">Ind</rdfs:label>
                            <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                        </owl:Class>
                    </owl:Class>
                </rdf:RDF>"""); // Added a class in a class

        assertFalse(commandExecutor.verifyOntology(input));
    }

    @Test
    void verifyOntologySemanticError2() throws Exception {
        // Verifying an ontology with a semantic error

        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
                """); // Added a label outside a class

        assertFalse(commandExecutor.verifyOntology(input));
    }

    @Test
    void transformOntologyCorrect() throws Exception {
        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
                """); // correct ontology

        // Setting up the expected ontology
        Ontology expected = new Ontology();
        expected.setXmlData(input.getXmlData());

        Ontology output = commandExecutor.transformOntology(input);
        assertEquals(formatXMLFromString(output.getXmlData()), formatXMLFromString(expected.getXmlData()));
    }

    @Test
    void transformOntologyError1() throws Exception {
        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                        <owl:Class rdf:about="http://www.persone#Individuo">
                            <rdfs:label xml:lang="it">Ind</rdfs:label>
                            <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                        </owl:Class>
                    </owl:Class>
                </rdf:RDF>
                """); // Added a class in class

        // Setting up the expected ontology
        final Ontology expected = getExpected();

        Ontology output = commandExecutor.transformOntology(input);
        assertEquals(formatXMLFromString(output.getXmlData()), formatXMLFromString(expected.getXmlData()));
    }

    @Test
    void transformOntologyError2() throws Exception {
        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
                """); // Added a label outside a class

        // Setting up the expected ontology
        final Ontology expected = getExpected();

        Ontology output = commandExecutor.transformOntology(input);
        assertEquals(formatXMLFromString(output.getXmlData()), formatXMLFromString(expected.getXmlData()));
    }

    @Test
    void transformOntologyError3() throws Exception {
        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                        <owl:Class rdf:about="http://www.persone#Individuo">
                            <rdfs:label xml:lang="it">Ind</rdfs:label>
                            <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                        </owl:Class>
                    </owl:Class>
                </rdf:RDF>
                """); // Added a label outside a class and a class in the class

        // Setting up the expected ontology
        final Ontology expected = getExpected();

        Ontology output = commandExecutor.transformOntology(input);
        assertEquals(formatXMLFromString(output.getXmlData()), formatXMLFromString(expected.getXmlData()));
    }

    @Test
    void transformOntologyError4() throws Exception {
        // Loading ontology
        input.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <rdfs:label xml:lang="it">Ind</rdfs:label>
                    <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                        <owl:Class rdf:about="http://www.persone#Individuo">
                            <rdfs:label xml:lang="it">Ind</rdfs:label>
                            <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                            <owl:Class rdf:about="http://www.persone#Individuo">
                                <rdfs:label xml:lang="it">Ind</rdfs:label>
                                <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                            </owl:Class>
                        </owl:Class>
                    </owl:Class>
                </rdf:RDF>
                """); // many mistakes!

        // Setting up the expected ontology
        final Ontology expected = getExpected();

        Ontology output = commandExecutor.transformOntology(input);
        assertEquals(formatXMLFromString(output.getXmlData()), formatXMLFromString(expected.getXmlData()));
    }

    private static Ontology getExpected() {
        Ontology expected = new Ontology();
        expected.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
                """);
        return expected;
    }

    /**
     * Formats the XML data contained in a string and returns the formatted XML.
     *
     * @param xmlData The XML data as a string.
     * @return The formatted XML data as a string.
     * @throws Exception If an error occurs during XML formatting.
     */
    private String formatXMLFromString(String xmlData) throws Exception {
        if (xmlData == null || xmlData.isEmpty()) {
            throw new IllegalArgumentException("XML data is null or empty.");
        }

        // Parse the XML from the string
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true); // Ignore unnecessary whitespaces between elements
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xmlData)));
        document.normalizeDocument(); // Normalize the document structure

        // Prepare a transformer for formatted output
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // Write formatted XML to a string
        StringWriter writer = new StringWriter();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        // Normalize the result by removing excessive newlines and unwanted spaces
        return writer.toString().replaceAll("\n{2,}", "\n")
                .replaceAll(">\\s+<", "><");
    }
}
