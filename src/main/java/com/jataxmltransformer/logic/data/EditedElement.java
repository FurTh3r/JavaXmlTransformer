package com.jataxmltransformer.logic.data;

/**
 * Represents an edited element in an XML file, containing information about the difference found.
 */
public class EditedElement {
    private String data;
    private String id;
    private int startLine;
    private int endLine;

    /**
     * Gets the starting line number of the edited element.
     *
     * @return The starting line number.
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Sets the starting line number of the edited element.
     *
     * @param startLine The starting line number to set.
     */
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    /**
     * Gets the ending line number of the edited element.
     *
     * @return The ending line number.
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Sets the ending line number of the edited element.
     *
     * @param endLine The ending line number to set.
     */
    public void setEndLine(int endLine) {
        this.endLine = endLine;
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
     * Returns a string representation of the EditedElement.
     *
     * @return A string containing the id, data, and line numbers of the element.
     */
    @Override
    public String toString() {
        return "EditedElement{" + "id='" + id + '\''
                + ", data='" + data + '\''
                + ", lineBegin=" + startLine + "\'"
                + ", lineEnd=" + endLine + '}';
    }
}