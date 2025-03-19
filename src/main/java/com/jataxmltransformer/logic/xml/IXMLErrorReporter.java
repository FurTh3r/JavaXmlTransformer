package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.ErrorInfo;

import java.util.List;

/**
 * Interface for reporting errors related to XML transformations.
 * Implementations of this interface should define how to generate error information
 * based on a list of edited elements.
 */
public interface IXMLErrorReporter {

    /**
     * Generates a list of error information based on a list of edited elements.
     *
     * @param editedElements The list of edited elements to analyze for errors.
     * @return A list of {@link ErrorInfo} objects, each containing details of an error found.
     */
    List<ErrorInfo> generateErrorInfo(List<EditedElement> editedElements);
}