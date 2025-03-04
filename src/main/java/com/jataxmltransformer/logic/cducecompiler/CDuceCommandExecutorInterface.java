package com.jataxmltransformer.logic.cducecompiler;

import com.jataxmltransformer.logic.data.Ontology;

import java.io.IOException;

public interface CDuceCommandExecutorInterface {
    boolean verifyOntology(Ontology ontology) throws Exception;

    boolean transformOntology(Ontology ontology) throws Exception;
}
