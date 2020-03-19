package main;

import controller.login.ControllerLogin;
import controller.main.ControllerMain;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("resources/view/login/Login.fxml"));
        Parent root = fxmlLoader.load();

        ControllerLogin ctrl = fxmlLoader.getController();

        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/view/login/Login.fxml"));
        primaryStage.setTitle("3D PrintED");
        Scene scene = new Scene(root, 465, 270);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(event -> ctrl.getBtnLogin().fire());
    }

    public static void main(String[] args) {
        launch(args);
    }
}