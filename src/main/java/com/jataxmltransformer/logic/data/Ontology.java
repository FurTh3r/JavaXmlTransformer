package com.jataxmltransformer.logic.data;

import java.io.*;

/**
 * The {@code Ontology} class represents an ontology containing XML data, the name of the ontology,
 * and its extension. It provides methods to save the XML content to a file and load it from a file.
 * <p>
 * This class supports basic file operations such as reading from and writing to files.
 * It allows you to store the XML content of the ontology, manage the name and extension of the ontology,
 * and provide file persistence operations.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>
 *     Ontology ontology = new Ontology("ExampleOntology", "example.xml");
 *     ontology.setXmlData("<xml>...</xml>");
 *     ontology.saveXmlToFile("path/to/save/file.xml");
 * </pre>
 */
public class Ontology {
    private String xmlData; // XML content of the ontology
    private String ontologyName; // Name of the ontology
    private String ontologyExtension; // Extension of the ontology (e.g., ".xml")

    /**
     * Default constructor for the {@code Ontology} class.
     */
    public Ontology() {
    }

    /**
     * Constructs an {@code Ontology} object with the specified name and extension.
     *
     * @param ontologyName The name of the ontology.
     * @param ontologyExtension The extension of the ontology (e.g., ".xml").
     */
    public Ontology(String ontologyName, String ontologyExtension) {
        this.ontologyName = ontologyName;
        this.ontologyExtension = ontologyExtension;
    }

    /**
     * Gets the XML data of the ontology.
     *
     * @return The XML data as a string.
     */
    public String getXmlData() {
        return xmlData;
    }

    /**
     * Sets the XML data of the ontology.
     *
     * @param xmlData The XML content to be set for the ontology.
     */
    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    /**
     * Gets the name of the ontology.
     *
     * @return The name of the ontology.
     */
    public String getOntologyName() {
        return ontologyName;
    }

    /**
     * Sets the name of the ontology.
     *
     * @param ontologyName The name of the ontology.
     */
    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }

    /**
     * Gets the extension of the ontology.
     *
     * @return The extension of the ontology (e.g., ".xml").
     */
    public String getOntologyExtension() {
        return ontologyExtension;
    }

    /**
     * Sets the extension of the ontology.
     *
     * @param ontologyExtension The extension of the ontology (e.g., ".xml").
     */
    public void setOntologyExtension(String ontologyExtension) {
        this.ontologyExtension = ontologyExtension;
    }

    /**
     * Saves the XML data to a file at the specified file path.
     * If the file cannot be written to, an exception is thrown.
     *
     * @param filePath The path where the XML content should be saved.
     * @throws IOException If an error occurs during file writing.
     */
    public void saveXmlToFile(String filePath) throws IOException {
        if (xmlData == null || xmlData.isEmpty()) {
            throw new IOException("XML data is empty or not set.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(xmlData);
        } catch (IOException e) {
            throw new IOException("Error saving XML data to file: " + filePath, e);
        }
    }

    /**
     * Loads the XML data from a file at the specified file path.
     * If the file cannot be read, an exception is thrown.
     *
     * @param filePath The path of the file from which the XML content should be loaded.
     * @throws IOException If an error occurs during file reading.
     */
    public void loadXmlFromFile(String filePath) throws IOException {
        StringBuilder xmlBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                xmlBuilder.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new IOException("File not found: " + filePath, e);
        } catch (IOException e) {
            throw new IOException("Error reading XML data from file: " + filePath, e);
        }

        this.xmlData = xmlBuilder.toString();
    }
}