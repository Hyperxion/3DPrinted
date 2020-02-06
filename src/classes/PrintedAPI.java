package classes;

import com.zaxxer.hikari.HikariDataSource;
import javafx.concurrent.Service;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.stage.Modality;


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
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
