package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Charger le layout principal avec sidebar et navbar
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));

            // Configurer la scène et ajouter les styles CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

            // Configurer et afficher la fenêtre principale
            primaryStage.setTitle("DOC4U - Administration");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}