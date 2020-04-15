package controller.edit;

import classes.Object;
import classes.PrintedAPI;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerEditObject implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Object editedObject;

    @FXML
    private Label labelTitle, labelInfo, labelId;

    @FXML
    private TextField txtFieldName, txtFieldWeight, txtFieldSupportWeight, txtFieldComment, txtFieldStlLink, txtFieldTimeHours, txtFieldTimeMinutes;

    @FXML
    private Button btnCreate, btnCancel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCreate.setOnAction(event -> {
            if (editObject()){
                Object.insertUpdateObject(editedObject, ds);
                PrintedAPI.closeWindow(btnCreate);
                controllerMain.getObjTv().refresh();
                controllerMain.calculateAllStatistics();
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean editObject(){
        try {
            //table columns
            String name, stlLink, comment;
            int id, buildTime;
            double supportWeight, weight;

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

            buildTime = minutes + hours;

            name = txtFieldName.getText();
            stlLink = txtFieldStlLink.getText();
            comment = txtFieldComment.getText();

            weight = PrintedAPI.round(Double.parseDouble(txtFieldWeight.getText()));
            supportWeight = PrintedAPI.round(Double.parseDouble(txtFieldSupportWeight.getText()));

            editedObject.setName(name);
            editedObject.setStlLink(stlLink);
            editedObject.setComment(comment);
            editedObject.setBuildTime(buildTime);
            editedObject.setBuildTimeFormatted(PrintedAPI.formatTime(buildTime).get());
            editedObject.setSupportWeight(supportWeight);
            editedObject.setWeight(weight);

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

    public void setFieldsValues(Object object) {

        editedObject = object;

        int id = object.getId();
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText(id + ";" + object.getName());

        txtFieldName.setText(editedObject.getName());
        txtFieldWeight.setText(editedObject.getWeight() + "");
        txtFieldSupportWeight.setText(editedObject.getSupportWeight() + "");
        txtFieldTimeHours.setText(editedObject.getBuildTime()/60 + "");
        txtFieldTimeMinutes.setText(editedObject.getBuildTime()%60 + "");
        txtFieldStlLink.setText(editedObject.getStlLink());
        txtFieldComment.setText(editedObject.getComment());
    }

    public Button getBtnCreate() {
        return btnCreate;
    }
}
