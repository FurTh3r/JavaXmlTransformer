package com.jataxmltransformer.logic.data;

/**
 * Represents an edited element in an XML file, containing information about the difference found.
 */
public class EditedElement {
    private String id;   // Identifier for the edited element
    private String data; // Modified data value
    private int line;    // Line number where the modification occurred

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
     * Gets the line number where the modification occurred.
     *
     * @return The line number of the modification.
     */
    public int getLine() {
        return line;
    }

    /**
     * Sets the line number where the modification occurred.
     *
     * @param line The line number to set.
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Returns a string representation of the EditedElement.
     *
     * @return A string containing the id, data, and line number of the element.
     */
    @Override
    public String toString() {
        return "EditedElement{"
                + "id='"
                + id + '\''
                + ", data='"
                + data + '\''
                + ", line="
                + line
                + '}';
    }
}