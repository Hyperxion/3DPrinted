package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/view/login/Login.fxml"));
        primaryStage.setTitle("3D PrintED");
        primaryStage.setScene(new Scene(root, 465, 270));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}