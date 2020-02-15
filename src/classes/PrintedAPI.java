package classes;

import javafx.concurrent.Service;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;


public class PrintedAPI {

    //closes any window of specified window
    public static void closeWindow(Button btn){
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    //safely starts or restarts any service
    public static void serviceStart(Service service){
        if(service.getState() == Service.State.SUCCEEDED){
            service.reset();
            service.start();
        } else if (service.getState() == Service.State.READY){
            service.start();
        }
    }

    //this methods centers list of columns that belongs to some TableView. We ca get this list by invoking TableView.getColumns() method
    public static <T> void centerColumns(ObservableList<TableColumn<T, ?>> columns){
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setStyle("-fx-alignment: CENTER;");
        }
    }

    //converting minutes into minutes and hours -> input: 125; output: 2h 5m
    public static SimpleStringProperty formatTime (int time){
        SimpleStringProperty newTime;

        int hours = time / 60; //since both are ints, you get an int
        int minutes = time % 60;

        newTime = new SimpleStringProperty(String.format("%dh %02dm", hours, minutes));

        return newTime;
    }

    //method for rounding double numbers
    public static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
