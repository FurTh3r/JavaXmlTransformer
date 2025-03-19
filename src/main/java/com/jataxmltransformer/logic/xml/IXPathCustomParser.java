package com.jataxmltransformer.logic.xml;

import com.jataxmltransformer.logic.utilities.MyPair;

import java.util.Map;

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

    /**
     * Retrieves the start and end line numbers for all XPath elements.
     * <p>
     * This method returns a map where the key is the XPath of an XML element and the value is a
     * {@link MyPair} containing the start and end line numbers for that element.
     *
     * @return A map with XPath as the key and a {@link MyPair} of integers representing the start
     *         and end line numbers of the element.
     *         If no data is available, an empty map is returned.
     */
    Map<String, MyPair<Integer, Integer>> getAllInfo();
}