package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.utilities.MyPair;

/**
 * Interface for parsing XML data and retrieving the start and end line numbers for a given XPath.
 */
public interface IXPathCustomParser {

    /**
     * Retrieves the start and end line numbers for the given XPath.
     *
     * @param xPath The XPath to search for.
     * @return A MyPair object containing the start and end line numbers, or (-1, -1) if not found.
     */
    MyPair<Integer, Integer> getInfoFromXPath(String xPath);
}