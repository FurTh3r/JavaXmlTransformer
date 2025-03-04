package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logic.data.Ontology;

/**
 * Interface for executing CDuce commands to verify and transform ontologies.
 * This interface defines methods for verifying the validity of an ontology and transforming it using CDuce code.
 */
public interface CDuceCommandExecutorInterface {

    /**
     * Verifies the validity of the given ontology using CDuce verification.
     *
     * @param ontology the ontology to be verified.
     * @return true if the ontology is valid, false if it is invalid.
     * @throws Exception if an error occurs during the verification process.
     */
    boolean verifyOntology(Ontology ontology) throws Exception;

    /**
     * Transforms the given ontology using CDuce transformation code.
     *
     * @param ontology the ontology to be transformed.
     * @return the transformed ontology, or null if the transformation failed.
     * @throws Exception if an error occurs during the transformation process.
     */
    Ontology transformOntology(Ontology ontology) throws Exception;
}