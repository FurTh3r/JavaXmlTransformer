package com.jataxmltransformer.middleware;

import com.jataxmltransformer.logic.cducecompiler.CDuceCodeLoader;
import com.jataxmltransformer.logic.cducecompiler.CDuceCommandExecutor;
import com.jataxmltransformer.logic.data.EditedElement;
import com.jataxmltransformer.logic.data.Ontology;
import com.jataxmltransformer.logic.xml.XMLDiffChecker;

import java.io.IOException;
import java.util.List;

public class Middleware {
    public static void setup() {
        // Load structure

        List<String> namespaces = List.of(
                "namespace www = \"http://www.persone#\";;"
        );

        List<String> structure = List.of(
                "type Ontology = <rdf:RDF xml:base=String> [ Class* ]",
                "type Class = <owl:Class rdf:about=String> [ ClassAtt* ]",
                "type ClassAtt = SubClass | EqClass | Label | Note",
                "type SubClass = <rdfs:subClassOf rdf:resource=String> []",
                "type EqClass  = <owl:equivalentClass> [ EqAttr ] \n" +
                        "              | <owl:equivalentClass rdf:resource=String> []",
                "type EqAttr   = <owl:Restriction> [ AnyXml* ]",
                "type Label    = <rdfs:label xml:lang=String> String",
                "type Note     = <skos:scopeNote xml:lang=String> String"
        );

        List<String> attributes = List.of(
                "SubClass",
                "EqClass",
                "Label",
                "Note"
        );

        List<String> classes = List.of(
                "<owl:Class rdf:about=cls>"
        );

        CDuceCodeLoader.loadCheckStructure(namespaces, structure, attributes, classes);
    }

    public static void main(String[] args) throws Exception {
        setup();
        CDuceCommandExecutor executor = new CDuceCommandExecutor();
        Ontology ontology = new Ontology();
        ontology.setOntologyName("try");
        ontology.setOntologyExtension(".rdf"); // TODO Test syntax error?????????????????????????????????????????????????
        ontology.setXmlData("<?xml version=\"1.0\"?>\n" +
                "<rdf:RDF xmlns=\"http://www.persone/\"\n" +
                "         xml:base=\"http://www.persone/\"\n" +
                "         xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                "         xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "         xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "         xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\">\n" +
                "\n" +
                "  <!-- Classe Persona -->\n" +
                "  <owl:Class rdf:about=\"http://www.persone#Persona\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"http://www.persone#Individuo\"/>\n" +
                "    <rdfs:label xml:lang=\"en\">Person</rdfs:label>\n" +
                "    <skos:scopeNote xml:lang=\"en\">This class represents a person</skos:scopeNote>\n" +
                "    <!-- Classe Individuo -->\n" +
                "    <owl:Class rdf:about=\"http://www.persone#Individuo\">\n" +
                "      <rdfs:label xml:lang=\"it\">Individuo</rdfs:label>\n" +
                "      <skos:scopeNote xml:lang=\"it\">Questa classe rappresenta un individuo</skos:scopeNote>\n" +
                "    </owl:Class>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <!-- Classe Individuo -->\n" +
                "  <owl:Class rdf:about=\"http://www.persone#Individuo\">\n" +
                "    <rdfs:label xml:lang=\"it\">Individuo</rdfs:label>\n" +
                "    <skos:scopeNote xml:lang=\"it\">Questa classe rappresenta un individuo</skos:scopeNote>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "</rdf:RDF>");
        executor.verifyOntology(ontology);
        executor.transformOntology(ontology);



        // TEST DIFF XML

        // TODO edit middleware to adapt to the new form of xml
 /*
        XMLDiffChecker checker = new XMLDiffChecker();
        List<EditedElement> edit =
                checker.diff("C:\\Users\\loryp\\Documents\\University\\Lezioni\\2024-2025\\Tesi\\JavaXmlTransformer\\XmlTransformer\\src\\main\\resources\\io\\ontology.xml",
                        "C:\\Users\\loryp\\Documents\\University\\Lezioni\\2024-2025\\Tesi\\JavaXmlTransformer\\XmlTransformer\\src\\main\\resources\\io\\ontology_output.xml");

*/
    }
}
