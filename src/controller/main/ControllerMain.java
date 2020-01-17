package controller.main;

import com.zaxxer.hikari.HikariDataSource;
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

/*

Content:
    - GENERAL - VARIABLES
    - GETTERS & SETTERS


 */


public class ControllerMain implements Initializable {

    /*****************************          GENERAL - VARIABLES       *****************************/
    private HikariDataSource ds;

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
    }

    /*************************          GETTERS & SETTERS          *************************/

    public HikariDataSource getDataSource() {
        return ds;
    }

    public void setDataSource(HikariDataSource ds) {
        this.ds = ds;
    }
}
