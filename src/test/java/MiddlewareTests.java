import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLFormatter;
import com.jataxmltransformer.middleware.Middleware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Middleware class.
 * Verifies the functionality of the Middleware's ontology handling and transformation methods.
 */
class MiddlewareTests {

    // XML data used for ontology testing
    private final String xmlDataCheck = """
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
            """;

    /**
     * Resets the Middleware instance before each test.
     */
    @BeforeEach
    void setUp() {
        Middleware.resetInstance();
    }

    /**
     * Tests that the Middleware class correctly follows the Singleton pattern.
     */
    @Test
    void testSingletonInstance() {
        Middleware instance1 = Middleware.getInstance();
        Middleware instance2 = Middleware.getInstance();
        assertSame(instance1, instance2, "Middleware should be a singleton instance");
    }

    /**
     * Tests setting and getting the ontology input in the Middleware.
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
     */
    @Test
    void testLoadStructureFailsWhenEmpty() {
        Middleware middleware = Middleware.getInstance();
        boolean result = middleware.loadStructure();
        assertFalse(result, "Structure loading should fail when attributes and classes are empty");
    }

    /**
     * Tests verifying an empty ontology in the Middleware.
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
     */
    @Test
    void testTransformOntology2() throws Exception {
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
     * Tests transforming an ontology with nested elements.
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
}