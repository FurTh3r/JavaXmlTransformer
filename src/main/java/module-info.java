module XmlTransformer {
    requires io.github.cdimascio.dotenv.java;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.json;
    requires org.xmlunit;
    requires java.xml;

    exports com.jataxmltransformer.middleware;
    exports com.jataxmltransformer.logic.cducecompiler;
    exports com.jataxmltransformer.logic.data;
    exports com.jataxmltransformer.logic.xml;
    exports com.jataxmltransformer.logic.utilities;
    exports com.jataxmltransformer.logs;
    opens com.jataxmltransformer.logic.cducecompiler;
    opens com.jataxmltransformer.GUI;
}
