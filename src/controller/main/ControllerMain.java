package controller.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMain implements Initializable {

    @FXML
    private Tab tab_costs;

    @FXML
    private Button cost_btn_newCost;

    @FXML
    private Label costs_label_selected;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cost_btn_newCost.setOnAction((event) -> {

        });
    };


}
