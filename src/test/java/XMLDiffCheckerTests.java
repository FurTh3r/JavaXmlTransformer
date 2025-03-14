import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.xml.XMLDiffChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XMLDiffCheckerTests {

    private XMLDiffChecker xmlDiffChecker;

    @BeforeEach
    public void setUp() {
        xmlDiffChecker = new XMLDiffChecker();
    }

    // Test diff() when there are differences between two XML files
    @Test
    public void testDiffWithDifferences() throws IOException {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output.xml";  // Path to test output XML file

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diff(inputXMLPath, outputXMLPath);

        // Assert that the differences are detected
        assertNotNull(differences, "Differences list should not be null.");
        assertFalse(differences.isEmpty(), "There should be differences between the two XML files.");
    }

    // Test diff() when there are no differences between two XML files
    @Test
    public void testDiffWithNoDifferences() throws IOException {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output_no_diff.xml";  // Path to an XML file with no differences

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diff(inputXMLPath, outputXMLPath);

        // Assert that no differences are detected
        assertNotNull(differences, "Differences list should not be null.");
        assertEquals(0, differences.size(), "There should be no differences between the two XML files.");
    }

    // Test diff() for invalid file paths
    @Test
    public void testDiffWithInvalidFilePaths() {
        String inputXMLPath = "src/main/resources/io/test/non_existent_input.xml";  // Invalid path
        String outputXMLPath = "src/main/resources/io/test/non_existent_output.xml";  // Invalid path

        // Try comparing the two XML files and assert that an empty list is returned or an exception is thrown
        assertThrows(IOException.class, () -> xmlDiffChecker.diff(inputXMLPath, outputXMLPath),
                "An exception should be thrown when trying to compare non-existent files.");
    }

    // Test diff() for files with identical content
    @Test
    public void testDiffIdenticalFiles() throws IOException {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/input.xml";  // Identical file

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diff(inputXMLPath, outputXMLPath);

        // Assert that no differences are found
        assertNotNull(differences, "Differences list should not be null.");
        assertEquals(0, differences.size(), "There should be no differences between the two identical XML files.");
    }

    // Test diff() when one file has more elements than the other
    @Test
    public void testDiffWithMoreElementsInOneFile() throws IOException {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output_with_more_elements.xml";  // Path to output XML with more elements

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diff(inputXMLPath, outputXMLPath);

        // Assert that differences are found
        assertNotNull(differences, "Differences list should not be null.");
        assertFalse(differences.isEmpty(), "There should be differences when one file has more elements.");
    }

    // Test diff() when one file has less content than the other
    @Test
    public void testDiffWithLessContentInOneFile() throws IOException {
        String inputXMLPath = "src/main/resources/io/test/input_with_more_content.xml";  // Path to input XML with more content
        String outputXMLPath = "src/main/resources/io/test/output.xml";  // Path to output XML with less content

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diff(inputXMLPath, outputXMLPath);

        // Assert that differences are found
        assertNotNull(differences, "Differences list should not be null.");
        assertFalse(differences.isEmpty(), "There should be differences when one file has less content.");
    }

    // Test diff() for files with a missing root element
    @Test
    public void testDiffWithMissingRootElement() throws IOException {
        String inputXMLPath = "src/main/resources/io/test/input.xml";  // Path to test input XML file
        String outputXMLPath = "src/main/resources/io/test/output_missing_root.xml";  // Path to output XML missing root element

        // Compare the two XML files
        List<EditedElement> differences = xmlDiffChecker.diff(inputXMLPath, outputXMLPath);

        // Assert that error was found
        assertNull(differences);
    }
}