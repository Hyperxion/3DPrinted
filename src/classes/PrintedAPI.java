package classes;

import com.zaxxer.hikari.HikariDataSource;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

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

    //converting minutes into days, hours and minutes -> input: 2600; output: 1d 19h 20m
    public static String formatTimeToDays (int time){
//        String newTime;
//
//        int days = time % 1440;
//        int hours = (time - days*1440) / 60; //since both are ints, you get an int
//        int minutes = time - days*1440 -  % 60;
//
//        newTime = String.format("%dh %02dm", hours, minutes);
        return time/24/60 + "d " + time/60%24 + "h " + time%60 + "m";
        //return newTime;
    }

    //method for rounding double numbers
    public static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //method for rounding double numbers with places parameter
    public static double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);

        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean areComboBoxesEmpty(ComboBox... comboboxes) {

        boolean isEmpty = false;

        for (int i = 0; i < comboboxes.length; i++) {
            ComboBox combobox = comboboxes[i];
            if(combobox.getSelectionModel().isEmpty())isEmpty = true;
        }

        return isEmpty;
    }

    public static boolean areTxtFieldsEmpty(TextField... textfields) {

        boolean isEmpty = false;

        for (int i = 0; i < textfields.length; i++) {
            TextField textfield = textfields[i];
            if(textfield.lengthProperty().get() == 0)isEmpty = true;
        }

        return isEmpty;
    }

    //use this to test values of prices, shippings... to test if they are greater than 0
    public static boolean isGreaterThanZero(double... doubles){
        for (double number : doubles) {
            if (number <= 0)return false;
        }

        return true;
    }

    //use this to test values of build times, quantities... to test if they are greater than 0
    public static boolean isGreaterThanZero(int... integers){
        for (double number : integers) {
            if (number <= 0)return false;
        }
        return true;
    }

    //checks text fields for apostrophes. String cannot contain apostrophes, otherwise UPDATE or INSERT
    //query will fail. It must be escaped by another apostrophe.
    public static void checkApostrophe(TextField... textfields) {

        for (int i = 0; i < textfields.length; i++) {
            TextField textfield = textfields[i];
            String text = textfield.getText();
            if(text.contains("'"))textfield.setText(text.replace("'", "''"));
        }

    }

    public static int getCurrentAutoIncrementValue(HikariDataSource ds, String tableName) {
        int currentAutoIncrementValue = 0;

        //Create query
        String query = "SELECT AUTO_INCREMENT FROM  INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA ='" + ds.getUsername() + "' AND   TABLE_NAME   ='" + tableName + "'";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            //STEP 2: Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            //STEP 3: Open a connection

            conn = ds.getConnection();
            //STEP 4: Execute a query
            stmt = conn.createStatement();

            rs = stmt.executeQuery(query);
            //Query is executed, resultSet saved. Now we need to process the data
            //rs.next() loads row
            //in this loop we sequentialy add columns to list of Strings
            while(rs.next()){

                currentAutoIncrementValue = rs.getInt("AUTO_INCREMENT");

            }
            rs.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    return currentAutoIncrementValue;
    }

    //with this method we can perform simple one-time queries - update or delete
    public static void performSimpleQuery(String query, Label info, HikariDataSource ds){
        //Create query
        String updateQuery = query;

        Connection conn = null;
        Statement stmt = null;

        try {

            //STEP 2: Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            //STEP 3: Open a connection

            conn = ds.getConnection();
            if(conn.isValid(15) == false) {
                System.out.print("Connection Lost");
            }
            //STEP 4: Execute a query
            stmt = conn.createStatement();

            stmt.executeUpdate(updateQuery);


        } catch (SQLIntegrityConstraintViolationException e){

            String[] message = e.getMessage().split("`");
            String table = message[3];
            table = table.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");
            info.setText("Remove related " + table + " first.");
            info.setTextFill(Color.web("#ff0000"));

        } catch (SQLNonTransientConnectionException se) {
//            MngApi obj = new MngApi();
//            obj.alertConnectionLost();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }

}
