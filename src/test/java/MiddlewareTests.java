import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import com.jataxmltransformer.middleware.Middleware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Middleware class.
 * Verifies the functionality of the Middleware's ontology handling and transformation methods.
 */
class MiddlewareTests {

    // XML data used for ontology testing
    private static final String xmlDataCheck = """
            <?xml version="1.0" encoding="UTF-8"?>
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
     * Creates an Ontology to use as input built on model xmlDataCheck.
     *
     * @return the ontology to be used in tests
     */
    private static Ontology getOntologyInput() {
        Ontology inputOntology = new Ontology();
        inputOntology.setOntologyName("TestOntology");
        inputOntology.setOntologyExtension(".xml");
        inputOntology.setXmlData(xmlDataCheck);
        return inputOntology;
    }

    /**
     * Resets the Middleware instance before each test.
     */
    @BeforeEach
    void setUp() {
        Middleware.resetInstance();
    }

    /**
     * Tests that the Middleware class correctly follows the Singleton pattern.
     * Verifies that only one instance of the Middleware class exists throughout the lifecycle of the application.
     */
    @Test
    void testSingletonInstance() {
        Middleware instance1 = Middleware.getInstance();
        Middleware instance2 = Middleware.getInstance();
        assertSame(instance1, instance2, "Middleware should be a singleton instance");
    }

    /**
     * Tests setting and getting the ontology input in the Middleware.
     * Verifies that the ontology input can be set and retrieved correctly.
     */
    @Test
    void testSetAndGetOntologyInput() {
        Middleware middleware = Middleware.getInstance();
        Ontology inputOntology = new Ontology();
        inputOntology.setOntologyName("TestOntology");
        inputOntology.setOntologyExtension(".xml");
        inputOntology.setXmlData("<Ontology></Ontology>");

        middleware.setOntologyInput(inputOntology);
        assertEquals("TestOntology", middleware.getOntologyInput().getOntologyName());
        assertEquals(".xml", middleware.getOntologyInput().getOntologyExtension());
        assertEquals("<Ontology></Ontology>", middleware.getOntologyInput().getXmlData());
    }

    /**
     * Tests loading the ontology structure in the Middleware.
     * Verifies that the structure is loaded successfully when valid data is provided.
     */
    @Test
    void testLoadStructure() {
        Middleware middleware = Middleware.getInstance();
        middleware.setNamespaces(List.of("namespace www = \"http://www.persone#\""));
        middleware.setStructure(List.of("type Ontology = <rdf:RDF xml:base=String> [ Class* ]",
                "type Class = <owl:Class rdf:about=String> [ ClassAtt* ]",
                "type ClassAtt = SubClass | EqClass | Label | Note",
                "type SubClass = <rdfs:subClassOf rdf:resource=String> []",
                "type EqClass  = <owl:equivalentClass> [ EqAttr ]" + "| <owl:equivalentClass rdf:resource=String> []",
                "type EqAttr   = <owl:Restriction> [ AnyXml* ]",
                "type Label    = <rdfs:label xml:lang=String> String",
                "type Note     = <skos:scopeNote xml:lang=String> String"));
        middleware.setAttributes(List.of("SubClass", "EqClass", "Label", "Note"));
        middleware.setClasses(List.of("<owl:Class rdf:about=cls>"));

        boolean result = middleware.loadStructure();
        assertTrue(result, "Structure should load successfully");
    }

    /**
     * Tests that loading the structure fails when attributes and classes are empty.
     * Verifies that the structure loading fails if the required attributes and classes are not provided.
     */
    @Test
    void testLoadStructureFailsWhenEmpty() {
        Middleware middleware = Middleware.getInstance();
        boolean result = middleware.loadStructure();
        assertFalse(result, "Structure loading should fail when attributes and classes are empty");
    }

    /**
     * Tests verifying an empty ontology in the Middleware.
     * Verifies that the verification of an empty ontology fails as expected.
     */
    @Test
    void testVerifyOntologyFailsWhenOntologyIsEmpty() throws Exception {
        Middleware middleware = Middleware.getInstance();
        Ontology emptyOntology = new Ontology();
        middleware.setOntologyInput(emptyOntology);

        boolean result = middleware.verifyOntology();
        assertFalse(result, "Verification should fail for an empty ontology");
    }

    /**
     * Tests transforming an ontology and checking the output.
     * Verifies that the transformation of the ontology occurs correctly and outputs the expected result.
     */
    @Test
    void testTransformOntology1() throws Exception {
        Middleware middleware = Middleware.getInstance();
        Ontology inputOntology = new Ontology();
        inputOntology.setOntologyName("TestOntology");
        inputOntology.setOntologyExtension(".xml");
        inputOntology.setXmlData(xmlDataCheck);

        middleware.setOntologyInput(inputOntology);
        middleware.transformOntology();

        assertNotNull(middleware.getOntologyOutput(),
                "Ontology output should not be null after transformation");
        assertEquals(XMLFormatter.formatOntology(middleware.getOntologyOutput()).getXmlData(),
                XMLFormatter.formatOntology(inputOntology).getXmlData());
    }

    /**
     * Tests transforming another version of the ontology and checking the output.
     * Verifies that the transformation of another version of the ontology is handled correctly.
     */
    @Test
    void testTransformOntology2() throws Exception {
        Middleware middleware = Middleware.getInstance();
        final Ontology inputOntology = getOntologyInput();

        middleware.setOntologyInput(inputOntology);
        middleware.transformOntology();

        assertNotNull(middleware.getOntologyOutput(),
                "Ontology output should not be null after transformation");
        assertEquals(XMLFormatter.formatOntology(middleware.getOntologyOutput()).getXmlData(),
                XMLFormatter.formatXMLFromString(xmlDataCheck));
    }

    /**
     * Tests transforming an ontology with nested elements.
     * Verifies that the transformation correctly handles an ontology with nested elements.
     */
    @Test
    void testTransformOntology3() throws Exception {
        Middleware middleware = Middleware.getInstance();
        Ontology inputOntology = new Ontology();
        inputOntology.setOntologyName("TestOntology");
        inputOntology.setOntologyExtension(".xml");
        inputOntology.setXmlData("""
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
                """);

        middleware.setOntologyInput(inputOntology);
        middleware.transformOntology();

        assertNotNull(middleware.getOntologyOutput(),
                "Ontology output should not be null after transformation");
        assertEquals(XMLFormatter.formatOntology(middleware.getOntologyOutput()).getXmlData(),
                XMLFormatter.formatXMLFromString(xmlDataCheck));
    }

    /**
     * Tests transforming an ontology with multiple nested classes.
     * Verifies that the transformation correctly handles multiple nested classes within the ontology.
     */
    @Test
    void testTransformOntology4() throws Exception {
        Middleware middleware = Middleware.getInstance();
        Ontology inputOntology = new Ontology();
        inputOntology.setOntologyName("TestOntology");
        inputOntology.setOntologyExtension(".xml");
        inputOntology.setXmlData("""
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
                            <owl:Class rdf:about="http://www.persone#Individuo">
                                <rdfs:label xml:lang="it">Ind</rdfs:label>
                                <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                            </owl:Class>
                        </owl:Class>
                    </owl:Class>
                </rdf:RDF>
                """);

        middleware.setOntologyInput(inputOntology);
        middleware.transformOntology();

        assertNotNull(middleware.getOntologyOutput(),
                "Ontology output should not be null after transformation");
        assertEquals(XMLFormatter.formatOntology(middleware.getOntologyOutput()).getXmlData(),
                XMLFormatter.formatXMLFromString(xmlDataCheck));
    }

    /**
     * Tests the `getErrors()` method when both the input and output ontologies are null.
     * Verifies that an empty list is returned when no errors are present.
     */
    @Test
    void testGetErrors_NullInputOutput() throws Exception {
        assertEquals(Collections.emptyList(), Middleware.getErrors());
    }

    /**
     * Tests the `getErrors()` method when both the input and output ontologies are empty.
     * Verifies that an empty list is returned when no errors are present.
     */
    @Test
    void testGetErrors_EmptyInputOutput() throws Exception {
        Middleware middleware = Middleware.getInstance();

        middleware.setOntologyInput(new Ontology());
        middleware.setOntologyOutput(new Ontology());
        assertEquals(Collections.emptyList(), Middleware.getErrors());
    }

    /**
     * Tests the `getErrors()` method when there are no differences between the input and output ontologies.
     * Verifies that an empty list is returned when there are no errors.
     */
    @Test
    void testGetErrors_NoDifferences() throws Exception {
        Middleware middleware = Middleware.getInstance();

        Ontology inputOntology = new Ontology();
        inputOntology.setOntologyName("TestOntology");
        inputOntology.setOntologyExtension(".xml");
        inputOntology.setXmlData("<ontology></ontology>");
        Ontology outputOntology = new Ontology();
        outputOntology.setOntologyName("TestOntology");
        outputOntology.setOntologyExtension(".xml");
        outputOntology.setXmlData("<ontology></ontology>");

        middleware.setOntologyInput(inputOntology);
        middleware.setOntologyOutput(outputOntology);

        assertEquals(Collections.emptyList(), Middleware.getErrors());
    }

    /**
     * Tests the `getErrors()` method when an element is missing in the output ontology.
     * Verifies that the correct error is returned when the output ontology is missing a required element.
     */
    @Test
    void testGetErrors_WithMissingElement1() throws Exception {
        Middleware middleware = Middleware.getInstance();

        final Ontology inputOntology = getOntologyInput();
        Ontology outputOntology = new Ontology();
        outputOntology.setOntologyName("TestOntology");
        outputOntology.setOntologyExtension(".xml");
        outputOntology.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"\s
                    xml:base="http://www.persone/">
                </rdf:RDF>
                """); // Changed it ti us in language of first label

        middleware.setOntologyInput(inputOntology);
        middleware.setOntologyOutput(outputOntology);

        List<ErrorInfo> errors = Middleware.getErrors();
        assertEquals(1, errors.size());

        // Verify the error message and line numbers
        ErrorInfo error = errors.getFirst();
        assertEquals("", error.errorMessage());
        assertEquals(7, error.startLine()); // Checking the start line
        assertEquals(10, error.endLine());   // Checking the end line
    }

    /**
     * Tests the `getErrors()` method when multiple elements are missing in the output ontology.
     * Verifies that the correct errors are returned when multiple elements are missing in the output ontology.
     */
    @Test
    void testGetErrors_WithMissingElement2() throws Exception {
        Middleware middleware = Middleware.getInstance();

        // Create input ontology with multi-line XML data
        Ontology inputOntology = getOntologyInput();

        // Create output ontology with missing element
        Ontology outputOntology = new Ontology();
        outputOntology.setOntologyName("TestOntology");
        outputOntology.setOntologyExtension(".xml");
        outputOntology.setXmlData("""
                <?xml version="1.0" encoding="UTF-8"?>
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                    xmlns:owl="http://www.w3.org/2002/07/owl#"
                    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                    xml:base="http://www.persone/">
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
                """ // Missing label
        );

        // Set the ontologies in the middleware
        middleware.setOntologyInput(inputOntology);
        middleware.setOntologyOutput(outputOntology);

        // Retrieve the errors
        List<ErrorInfo> errors = Middleware.getErrors();

        // Assert that there is 1 error due to missing element in class (label and outer class)
        // 2 if removing the IGNORE_OUTER_NODES set to false
        assertEquals(1, errors.size());

        // Verify the error message and line numbers
        ErrorInfo error = errors.getFirst();
        assertEquals("", error.errorMessage());
        assertEquals(8, error.startLine()); // Checking the start line
        assertEquals(8, error.endLine());   // Checking the end line
    }
}