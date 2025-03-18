import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.xml.XMLErrorReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link XMLErrorReporter} class.
 * This class verifies the correctness of XML parsing, XPath evaluation,
 * and error reporting functionalities.
 */
public class XMLErrorReporterTests {

    private XMLErrorReporter xmlErrorReporter;

    /**
     * Sample XML string used for testing.
     */
    private final String xmlString = """
            <root>
                <item id="1">Value1</item>
                <item id="2">Value2</item>
            </root>
            """;

    /**
     * Sets up a new {@link XMLErrorReporter} instance before each test.
     *
     * @throws Exception if XML parsing fails.
     */
    @BeforeEach
    void setUp() throws Exception {
        xmlErrorReporter = new XMLErrorReporter(xmlString);
    }

    /**
     * Tests finding a valid node using an XPath expression.
     *
     * @throws Exception if XPath evaluation fails.
     */
    @Test
    void testFindNodeByXPath_ValidXPath() throws Exception {
        Node node = xmlErrorReporter.findNodeByXPath("/root/item[@id='1']");
        assertNotNull(node, "Node should not be null");
        assertEquals("item", node.getNodeName(), "Node name should be 'item'");
    }

    /**
     * Tests finding a node using an invalid XPath expression.
     * Ensures that an invalid expression does not crash the system.
     *
     * @throws XPathExpressionException if XPath expression fails.
     */
    @Test
    void testFindNodeByXPath_InvalidXPath() throws XPathExpressionException {
        assertNull(xmlErrorReporter.findNodeByXPath("invalid_xpath"));
    }

    /**
     * Tests searching for a node that does not exist in the XML document.
     *
     * @throws Exception if XPath evaluation fails.
     */
    @Test
    void testFindNodeByXPath_NonexistentNode() throws Exception {
        Node node = xmlErrorReporter.findNodeByXPath("/root/item[@id='3']");
        assertNull(node, "Node should be null for nonexistent elements");
    }

    /**
     * Tests generating error information for valid elements found in the XML document.
     *
     * @throws Exception if XML processing fails.
     */
    @Test
    void testGenerateErrorInfo_ValidElements() throws Exception {
        List<EditedElement> editedElements = new ArrayList<>();
        EditedElement editedElement1 = new EditedElement();
        editedElement1.setxPath("/root/item[@id='1']");
        editedElements.add(editedElement1);

        EditedElement editedElement2 = new EditedElement();
        editedElement2.setxPath("/root/item[@id='2']");
        editedElements.add(editedElement2);

        List<ErrorInfo> errors = xmlErrorReporter.generateErrorInfo(xmlString, editedElements);
        assertEquals(2, errors.size(), "There should be 2 errors reported");

        assertTrue(errors.getFirst().errorMessage().contains("Error in element"),
                "Error message should be generated");
    }

    /**
     * Tests generating error information for an element that does not exist in the XML document.
     *
     * @throws Exception if XML processing fails.
     */
    @Test
    void testGenerateErrorInfo_NonexistentElement() throws Exception {
        List<EditedElement> editedElements = new ArrayList<>();
        EditedElement editedElement = new EditedElement();
        editedElement.setxPath("/root/nonexistent");
        editedElements.add(editedElement);

        List<ErrorInfo> errors = xmlErrorReporter.generateErrorInfo(xmlString, editedElements);
        assertEquals(0, errors.size(), "No errors should be reported for nonexistent elements");
    }

    /**
     * Tests handling of an empty XML string.
     * Ensures that the system fails gracefully when parsing an empty input.
     */
    @Test
    void testGenerateErrorInfo_EmptyXMLString() {
        Exception exception = assertThrows(Exception.class, () -> new XMLErrorReporter(""));
        assertTrue(exception.getMessage().contains("Premature end of file"), "Should fail due to empty XML");
    }

    /**
     * Tests handling of a malformed XML string.
     * Ensures that the system detects structural errors in the XML input.
     */
    @Test
    void testGenerateErrorInfo_MalformedXML() {
        String malformedXML = "<root><item>Value1</item"; // Missing closing bracket
        Exception exception = assertThrows(Exception.class, () -> new XMLErrorReporter(malformedXML));
        assertTrue(exception.getMessage().contains("XML document structures must start and end within the same entity"),
                "Should fail due to malformed XML");
    }
}