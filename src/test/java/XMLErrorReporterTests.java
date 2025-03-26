import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.xml.IXMLErrorReporter;
import com.jataxmltransformer.logic.xml.XMLErrorReporter;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XMLErrorReporterTests {

    /**
     * Sample XML string used for testing.
     */
    private final String xmlString1 = """
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
                    <owl:Class rdf:about="http://www.persone#Individuo">
                        <rdfs:label xml:lang="it">Ind</rdfs:label>
                        <skos:scopeNote xml:lang="it">Class</skos:scopeNote>
                    </owl:Class>
                </rdf:RDF>
            """;
    private IXMLErrorReporter xmlErrorReporter;

    @Before
    public void setUp() {
        // Initialize XMLErrorReporter with the custom parser
        xmlErrorReporter = new XMLErrorReporter(xmlString1);
    }

    @Test
    public void testGenerateErrorInfo_withValidData() {
        // Prepare test data (EditedElement)
        EditedElement editedElement = new EditedElement("elementId", "Modified Data",
                "/RDF[1]/Class[1]");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that the error info matches the expected result
        assertEquals(1, errorInfos.size()); // We expect one error info
        ErrorInfo errorInfo = errorInfos.getFirst();
        assertEquals(4, errorInfo.startLine());
        assertEquals(7, errorInfo.endLine());
        assertEquals("elementId", errorInfo.errorMessage()); // Based on the EditedElement id
        assertEquals("Modified Data", errorInfo.elementDetails()); // Based on the EditedElement data
    }

    @Test
    public void testGenerateErrorInfo_withFakeError() {
        // Arrange: EditedElement with a fake error XPath "/RDF[1]"
        EditedElement editedElement = new EditedElement("elementId", "Modified Data", "/RDF[1]");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information (this should skip the "/RDF[1]" element)
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that no error info is generated for the fake error
        assertEquals(0, errorInfos.size()); // No errors should be reported for the fake XPath
    }

    @Test
    public void testGenerateErrorInfo_withNoMatchingXPath() {
        EditedElement editedElement = new EditedElement("elementId", "Modified Data",
                "/RDF[1]/unknownElement[1]");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that no error info is generated for the non-existent XPath
        assertEquals(0, errorInfos.size()); // No errors should be reported for invalid XPath
    }

    @Test
    public void testGenerateErrorInfo_withMultipleEditedElements() {
        // Arrange: Multiple EditedElements
        EditedElement editedElement1 = new EditedElement("elementId1", "Modified Data 1",
                "/RDF[1]/Class[1]");
        EditedElement editedElement2 = new EditedElement("elementId2", "Modified Data 2",
                "/RDF[1]/Class[2]");
        List<EditedElement> editedElements = List.of(editedElement1, editedElement2);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that both edited elements are processed
        assertEquals(2, errorInfos.size());
        assertEquals("elementId1", errorInfos.getFirst().errorMessage());
        assertEquals(4, errorInfos.get(0).startLine());
        assertEquals(7, errorInfos.get(0).endLine());
        assertEquals("elementId2", errorInfos.get(1).errorMessage());
        assertEquals(8, errorInfos.get(1).startLine());
        assertEquals(11, errorInfos.get(1).endLine());
    }

    @Test
    public void testGenerateErrorInfo_withNullDataInEditedElement() {
        // Arrange: EditedElement with null data
        EditedElement editedElement = new EditedElement("elementId", null, "/RDF[1]/Class[1]");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that null data is handled
        assertEquals(1, errorInfos.size());
        assertEquals("elementId", errorInfos.getFirst().errorMessage());
        assertNull(errorInfos.getFirst().elementDetails()); // Expecting "null" for empty data
    }

    @Test
    public void testGenerateErrorInfo_withNestedElements() {
        // Arrange: EditedElement with a nested element XPath
        EditedElement editedElement = new EditedElement("elementId", "Modified Data",
                "/RDF[1]/Class[1]/label[1]");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that the nested element's line numbers and data are processed
        assertEquals(1, errorInfos.size());
        assertEquals("elementId", errorInfos.getFirst().errorMessage());
        assertEquals("Modified Data", errorInfos.getFirst().elementDetails());
        assertEquals(5, errorInfos.getFirst().startLine()); // Should correspond to the rdfs:label element
        assertEquals(5, errorInfos.getFirst().endLine());
    }

    @Test
    public void testGenerateErrorInfo_withEmptyEditedElementsList() {
        // Arrange: Empty list of EditedElements
        List<EditedElement> editedElements = Collections.emptyList();

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that no error info is generated
        assertEquals(0, errorInfos.size()); // No errors should be reported for an empty list
    }

    @Test
    public void testGenerateErrorInfo_withInvalidXPathFormat() {
        // Arrange: EditedElement with an invalid XPath format
        EditedElement editedElement = new EditedElement("elementId", "Modified Data", "invalidXPath");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that no error info is generated for invalid XPath format
        assertEquals(0, errorInfos.size()); // No errors should be reported for invalid XPath format
    }

    @Test
    public void testGenerateErrorInfo_withDeeplyNestedElement() {
        // Arrange: EditedElement with a deeply nested element XPath
        EditedElement editedElement = new EditedElement("elementId", "Modified Data",
                "/RDF[1]/Class[1]/scopeNote[1]");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that the deeply nested element's line numbers and data are processed
        assertEquals(1, errorInfos.size());
        assertEquals("elementId", errorInfos.getFirst().errorMessage());
        assertEquals("Modified Data", errorInfos.getFirst().elementDetails());
        assertEquals(6, errorInfos.getFirst().startLine()); // Should correspond to the skos:scopeNote element
        assertEquals(6, errorInfos.getFirst().endLine());
    }

    @Test
    public void testGenerateErrorInfo_withMultipleNestedElements() {
        // Arrange: Multiple EditedElements with nested XPaths
        EditedElement editedElement1 = new EditedElement("elementId1", "Modified Data 1",
                "/RDF[1]/Class[1]/label[1]");
        EditedElement editedElement2 = new EditedElement("elementId2", "Modified Data 2",
                "/RDF[1]/Class[2]/scopeNote[1]");
        List<EditedElement> editedElements = List.of(editedElement1, editedElement2);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that both nested elements are processed
        assertEquals(2, errorInfos.size());
        assertEquals("elementId1", errorInfos.getFirst().errorMessage());
        assertEquals(5, errorInfos.get(0).startLine());
        assertEquals(5, errorInfos.get(0).endLine());
        assertEquals("elementId2", errorInfos.get(1).errorMessage());
        assertEquals(10, errorInfos.get(1).startLine());
        assertEquals(10, errorInfos.get(1).endLine());
    }

    @Test
    public void testGenerateErrorInfo_withNullXPath() {
        // Arrange: EditedElement with null XPath
        EditedElement editedElement = new EditedElement("elementId", "Modified Data", null);
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that no error info is generated for null XPath
        assertEquals(0, errorInfos.size()); // No errors should be reported for null XPath
    }

    @Test
    public void testGenerateErrorInfo_withEmptyXPath() {
        // Arrange: EditedElement with empty XPath
        EditedElement editedElement = new EditedElement("elementId", "Modified Data", "");
        List<EditedElement> editedElements = List.of(editedElement);

        // Act: Generate the error information
        List<ErrorInfo> errorInfos = xmlErrorReporter.generateErrorInfo(editedElements);

        // Assert: Verify that no error info is generated for empty XPath
        assertEquals(0, errorInfos.size()); // No errors should be reported for empty XPath
    }
}