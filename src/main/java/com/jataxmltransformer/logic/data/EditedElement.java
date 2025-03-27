package com.jataxmltransformer.logic.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an edited element in an XML file, containing information about the difference found.
 */
public class EditedElement {
    private String data;
    private String id;
    private String xPath;

    /**
     * Constructor for the EditedElement class
     *
     * @param data  of the edited element
     * @param id    of the edited element
     * @param xPath of the edited element
     */
    public EditedElement(String data, String id, String xPath) {
        this.data = data;
        this.id = id;
        this.xPath = xPath;
    }
    /**
     * Constructor for the EditedElement class
     */
    public EditedElement() {
    }

    /**
     * Gets the identifier of the edited element.
     *
     * @return The identifier of the edited element.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier of the edited element.
     *
     * @param id The identifier to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the modified data value.
     *
     * @return The modified data value.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the modified data value.
     *
     * @param data The modified data value to set.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the XPath expression of the edited element.
     *
     * @return The XPath expression of the edited element.
     */
    public String getXPath() {
        return xPath;
    }

    /**
     * Sets the XPath expression of the edited element.
     *
     * @param xPath The XPath expression to set.
     */
    public void setXPath(String xPath) {
        this.xPath = xPath;
    }

    /**
     * Extracts the test XPath value from the identifier string if it matches the predefined pattern.
     * The identifier should contain a segment in the format "Test XPath: value" where "value" is the desired text to extract.
     *
     * @return The extracted test XPath value if found, or null if the pattern does not match.
     */
    public String getTestPath() {
        if (id == null)
            return "";

        Pattern pattern = Pattern.compile("Test XPath: (.*?)(?:\\s*\\|\\s*|$)");
        Matcher matcher = pattern.matcher(id);

        if (matcher.find())
            return matcher.group(1);
        else
            return "";
    }

    /**
     * Returns a string representation of the EditedElement.
     *
     * @return A string containing the id, data, and XPath of the element.
     */
    @Override
    public String toString() {
        return "EditedElement{" +
                "id='" + id + '\'' +
                ", data='" + data + '\'' +
                ", xPath='" + xPath + '\'' +
                '}';
    }
}