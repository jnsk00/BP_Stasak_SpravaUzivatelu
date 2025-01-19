module org.example.spravauzivatelu_grafika {


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires static lombok;
    requires spring.web;
    requires javafx.web;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;

    exports org.example.backend;
    exports org.example.shared;


    opens org.example.desktop.spravauzivatelu_grafika to javafx.fxml;
    opens org.example.backend to spring.core, spring.beans, spring.context;
    exports org.example.desktop.spravauzivatelu_grafika;
}