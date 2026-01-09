module com.example.nutrimap {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires java.sql;
    requires java.net.http;
    requires java.desktop;
    requires com.github.librepdf.openpdf;
    opens com.example.nutrimap to javafx.fxml;
    exports com.example.nutrimap;
    exports com.example.nutrimap.controller;
    opens com.example.nutrimap.controller to javafx.fxml;
    exports com.example.nutrimap.model;
    opens com.example.nutrimap.model to com.google.gson;
    exports com.example.nutrimap.dao;
    exports com.example.nutrimap.service;
    opens com.example.nutrimap.service to com.google.gson;
}
