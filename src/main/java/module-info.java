module tn.esprit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires jbcrypt;
    requires mysql.connector.j;
    requires com.google.protobuf;
    requires java.mail;


    opens tn.esprit.Controllers.Doctor to javafx.fxml;
    opens tn.esprit.Controllers.Patient to javafx.fxml;
    opens tn.esprit.tests to javafx.fxml;
    opens tn.esprit.Controllers to javafx.fxml;
    opens tn.esprit.models to javafx.base;


    exports tn.esprit.Controllers.Doctor;
    exports tn.esprit.Controllers.Patient;
    exports tn.esprit.tests;
    exports tn.esprit.Controllers;
    exports tn.esprit.models;
    exports tn.esprit;

}