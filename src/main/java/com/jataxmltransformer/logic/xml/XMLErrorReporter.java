package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;
import com.jataxmltransformer.logic.utilities.MyPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link IXMLErrorReporter} interface that generates error information
 * based on a list of edited XML elements.
 * It processes each edited element and extracts
 * the relevant line information from the associated XPath.
 */
public class XMLErrorReporter implements IXMLErrorReporter {

    private final IXPathCustomParser xPathCustomParser;

    /**
     * Constructor to initialize the error reporter with the provided XML string.
     *
     * @param xmlString The XML string to be parsed for generating error information.
     */
    public XMLErrorReporter(String xmlString) {
        xPathCustomParser = new XPathCustomParser(xmlString);
    }

    /**
     * Generates a list of error information based on the provided list of edited elements.
     * The method uses XPath parsing to obtain line numbers for errors and creates
     * {@link ErrorInfo} objects accordingly.
     *
     * <p>This method will ignore any elements with an XPath of "/RDF[1]" as they are considered fake errors.</p>
     *
     * @param editedElements The list of edited elements to be analyzed for errors.
     * @return A list of {@link ErrorInfo} objects containing error details, including line numbers,
     *         element data, and element IDs.
     */
    @Override
    public List<ErrorInfo> generateErrorInfo(List<EditedElement> editedElements) {
        List<ErrorInfo> errorInfos = new ArrayList<>();

        for (EditedElement editedElement : editedElements) {
            // Ignore /RDF[1] because it's a useless error (tells there are errors in the XML)
            if (editedElement.getXPath() == null || editedElement.getXPath().equals("/RDF[1]"))
                continue;

            MyPair<Integer, Integer> lines = xPathCustomParser.getInfoFromXPath(editedElement.getXPath());
            if (lines != null && lines.getFirst() != -1 && lines.getSecond() != -1) {
                ErrorInfo errorInfo = new ErrorInfo(
                        lines.getFirst(),
                        lines.getSecond(),
                        editedElement.getData(),
                        editedElement.getId());
                errorInfos.add(errorInfo);
            }
        }
        return errorInfos;
    }
}