module tasks {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires log4j;

    opens tasks.model to javafx.base;
    exports tasks.model;
    opens tasks.view to javafx.fxml;
    exports tasks.view;
    opens tasks.controller to javafx.fxml;
    exports tasks.controller;
    exports tasks.services;
    opens tasks.services to javafx.base;
    exports tasks.utils;
    opens tasks.utils to javafx.base;
    exports tasks.repository;
    opens tasks.repository to javafx.base;
}
