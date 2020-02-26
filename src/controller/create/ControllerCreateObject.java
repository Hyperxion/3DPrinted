package controller.create;

import classes.PrintedAPI;
import classes.Object;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCreateObject implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Object newObject;

    @FXML
    private Label labelTitle, labelInfo, labelId;

    @FXML
    private TextField txtFieldName, txtFieldWeight, txtFieldSupportWeight, txtFieldComment, txtFieldStlLink, txtFieldTimeHours, txtFieldTimeMinutes;

    @FXML
    private Button btnCreate, btnCancel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCreate.setOnAction(event -> {
            if (createObject()){
                Object.insertUpdateObject(newObject, ds);
                PrintedAPI.closeWindow(btnCreate);
                PrintedAPI.serviceStart(controllerMain.getServiceDownloadAllTables());
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean createObject(){
        try {
            //table columns
            SimpleStringProperty name, stlLink, comment;
            SimpleIntegerProperty id, buildTime;
            SimpleDoubleProperty supportWeight, weight;

            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldName, txtFieldWeight, txtFieldSupportWeight, txtFieldStlLink, txtFieldTimeHours, txtFieldTimeMinutes);

            int minutes = Integer.parseInt(txtFieldTimeMinutes.getText());
            int hours = Integer.parseInt(txtFieldTimeHours.getText())*60;

            if (areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            if (minutes > 59 || minutes < 0) {
                labelInfo.setText("Minutes must be between 0 and 59 inclusive.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            if (hours < 0) {
                labelInfo.setText("Hours must be greater than zero.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            id = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));
            buildTime = new SimpleIntegerProperty(minutes + hours);

            name = new SimpleStringProperty(txtFieldName.getText());
            stlLink = new SimpleStringProperty(txtFieldStlLink.getText());
            comment = new SimpleStringProperty(txtFieldComment.getText());

            weight = new SimpleDoubleProperty(PrintedAPI.round(Double.parseDouble(txtFieldWeight.getText())));
            supportWeight = new SimpleDoubleProperty(PrintedAPI.round(Double.parseDouble(txtFieldSupportWeight.getText())));

            newObject = new Object(name, stlLink, comment, id, buildTime, new SimpleIntegerProperty(0), supportWeight, weight);

            return true;
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            labelInfo.setText("Wrong number format! Please, check your values!");
            labelInfo.setTextFill(Color.web("#ff0000"));
            return false;
        }
    }

    public void setDs(HikariDataSource ds) {
        this.ds = ds;
    }

    public void setControllerMain(ControllerMain controllerMain) {
        this.controllerMain = controllerMain;
    }

    public void setFieldsValues() {

        int id = PrintedAPI.getCurrentAutoIncrementValue(ds, "Objects");
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText("New Object");
    }

}
