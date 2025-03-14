package com.jataxmltransformer.logic.data;

/**
 * Represents error information that includes the start and end line of the error,
 * a message describing the error, and additional details about the affected element.
 * This class is used to capture and represent errors encountered during XML processing or transformation.
 *
 * <p>Example usage:</p>
 * <pre>
 * ErrorInfo error = new ErrorInfo(10, 12, "Invalid XML structure", "Element 'rdf:RDF' missing closing tag");
 * System.out.println(error);
 * </pre>
 */
public record ErrorInfo(
        int startLine,
        int endLine,
        String errorMessage,
        String elementDetails) {

    /**
     * Returns a string representation of the error information, including the start and end line,
     * the error message, and the details of the element affected by the error.
     *
     * @return A string describing the error information.
     */
    @Override
    public String toString() {
        return "ErrorInfo{" +
                "startLine=" + startLine +
                ", endLine=" + endLine +
                ", errorMessage='" + errorMessage + '\'' +
                ", elementDetails='" + elementDetails + '\'' +
                '}';
    }
}
