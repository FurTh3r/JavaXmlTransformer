module XmlTransformer {
    requires io.github.cdimascio.dotenv.java;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.json;
    requires org.xmlunit;
    requires java.xml;

    opens com.jataxmltransformer.GUI;
}