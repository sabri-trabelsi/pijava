module tn.esprit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens tn.esprit.tests to javafx.fxml;
    opens tn.esprit.Controllers to javafx.fxml;
    opens tn.esprit.models to javafx.base;

    exports tn.esprit.tests;
    exports tn.esprit.Controllers;
    exports tn.esprit.models;
    exports tn.esprit;
}