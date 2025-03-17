import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.xml.XMLDiffChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link XMLDiffChecker} class to verify the functionality of the diff method.
 */
public class XMLDiffCheckerTests {

    private XMLDiffChecker xmlDiffChecker;

    /**
     * Setup method to initialize the XMLDiffChecker before each test.
     */
    @BeforeEach
    public void setUp() {
        xmlDiffChecker = new XMLDiffChecker();
    }

    /**
     * Test for the {@link XMLDiffChecker#diffXmlFiles(String, String)} method when there are differences between two XML files.
     *
     * @throws IOException If an I/O error occurs while reading the files.
     */
    @Test
    public void testDiffWithDifferences() throws Exception {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output.xml";  // Path to test output XML file

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diffXmlFiles(inputXMLPath, outputXMLPath);

        // Assert that the differences are detected
        assertNotNull(differences, "Differences list should not be null.");
        assertFalse(differences.isEmpty(), "There should be differences between the two XML files.");
    }

    /**
     * Test for the {@link XMLDiffChecker#diffXmlFiles(String, String)} method when there are no differences between two XML files.
     *
     * @throws IOException If an I/O error occurs while reading the files.
     */
    @Test
    public void testDiffWithNoDifferences() throws Exception {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output_no_diff.xml";  // Path to an XML file with no differences

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diffXmlFiles(inputXMLPath, outputXMLPath);

        // Assert that no differences are detected
        assertNotNull(differences, "Differences list should not be null.");
        assertEquals(0, differences.size(), "There should be no differences between the two XML files.");
    }

    /**
     * Test for the {@link XMLDiffChecker#diffXmlFiles(String, String)} method when provided with invalid file paths.
     */
    @Test
    public void testDiffWithInvalidFilePaths() {
        String inputXMLPath = "src/main/resources/io/test/non_existent_input.xml";  // Invalid path
        String outputXMLPath = "src/main/resources/io/test/non_existent_output.xml";  // Invalid path

        // Try comparing the two XML files and assert that an empty list is returned or an exception is thrown
        assertThrows(Exception.class, () -> xmlDiffChecker.diffXmlFiles(inputXMLPath, outputXMLPath),
                "An exception should be thrown when trying to compare non-existent files.");
    }

    /**
     * Test for the {@link XMLDiffChecker#diffXmlFiles(String, String)} method when both files have identical content.
     *
     * @throws IOException If an I/O error occurs while reading the files.
     */
    @Test
    public void testDiffIdenticalFiles() throws Exception {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/input.xml";  // Identical file

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diffXmlFiles(inputXMLPath, outputXMLPath);

        // Assert that no differences are found
        assertNotNull(differences, "Differences list should not be null.");
        assertEquals(0, differences.size(), "There should be no differences between the two identical XML files.");
    }

    /**
     * Test for the {@link XMLDiffChecker#diffXmlFiles(String, String)} method when one XML file has more elements than the other.
     *
     * @throws IOException If an I/O error occurs while reading the files.
     */
    @Test
    public void testDiffWithMoreElementsInOneFile() throws Exception {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output_with_more_elements.xml";  // Path to output XML with more elements

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diffXmlFiles(inputXMLPath, outputXMLPath);

        // Assert that differences are found
        assertNotNull(differences, "Differences list should not be null.");
        assertFalse(differences.isEmpty(), "There should be differences when one file has more elements.");
    }

    /**
     * Test for the {@link XMLDiffChecker#diffXmlFiles(String, String)} method when one XML file has less content than the other.
     *
     * @throws IOException If an I/O error occurs while reading the files.
     */
    @Test
    public void testDiffWithLessContentInOneFile() throws Exception {
        String inputXMLPath = "src/main/resources/io/test/input_with_more_content.xml";  // Path to input XML with more content
        String outputXMLPath = "src/main/resources/io/test/output.xml";  // Path to output XML with less content

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diffXmlFiles(inputXMLPath, outputXMLPath);

        // Assert that differences are found
        assertNotNull(differences, "Differences list should not be null.");
        assertFalse(differences.isEmpty(), "There should be differences when one file has less content.");
    }

    /**
     * Test for the {@link XMLDiffChecker#diffXmlFiles(String, String)} method when one XML file is missing its root element.
     *
     * @throws IOException If an I/O error occurs while reading the files.
     */
    @Test
    public void testDiffWithMissingRootElement() throws Exception {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output_missing_root.xml";  // Path to output XML missing root element

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diffXmlFiles(inputXMLPath, outputXMLPath);

        // Assert that error was found
        assertNull(differences);
    }
}