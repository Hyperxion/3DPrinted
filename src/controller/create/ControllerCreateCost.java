package controller.create;

import classes.Cost;
import classes.PrintedAPI;
import classes.Printer;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ControllerCreateCost implements Initializable {

    HikariDataSource ds;
    private ControllerMain controllerMain;

    private Cost newCost;

    @FXML
    private Label labelTitle, labelInfo, labelId;

    @FXML
    private TextField txtFieldName, txtFieldQuantity, txtFieldPrice, txtFieldShipping, txtFieldComment;

    @FXML
    private DatePicker datePickerPurchaseDate;

    @FXML
    private Button btnCreate, btnCancel;

    @FXML
    private ComboBox<Printer> comboBoxPrinter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCreate.setOnAction(event -> {
            if (createCost()) {
                Cost.insertUpdateCost(newCost, ds);
                PrintedAPI.closeWindow(btnCreate);
                PrintedAPI.serviceStart(controllerMain.getServiceDownloadAllTables());
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean createCost(){
        try {
            //database table columns
            SimpleIntegerProperty id, quantity, printerId;
            SimpleStringProperty name, purchaseDate, comment;
            SimpleDoubleProperty shipping, price;

            boolean areComboBoxesEmpty = PrintedAPI.areComboBoxesEmpty(comboBoxPrinter);
            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldName, txtFieldPrice, txtFieldQuantity, txtFieldShipping);

            if (areComboBoxesEmpty || areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            double shippingDouble = PrintedAPI.round(Double.parseDouble(txtFieldShipping.getText()));
            double priceDouble = PrintedAPI.round(Double.parseDouble(txtFieldPrice.getText()));
            int quantityInt = Integer.parseInt(txtFieldQuantity.getText());

            if (shippingDouble <= 0 || priceDouble <= 0 || quantityInt <= 0){
                labelInfo.setText("Numeric values must be greater than zero.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return  false;
            }


            PrintedAPI.checkApostrophe(txtFieldName, txtFieldComment);

            id = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));
            quantity = new SimpleIntegerProperty(quantityInt);
            printerId = new SimpleIntegerProperty(comboBoxPrinter.getSelectionModel().getSelectedItem().getId());

            name = new SimpleStringProperty(txtFieldName.getText());
            purchaseDate = new SimpleStringProperty(datePickerPurchaseDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            comment = new SimpleStringProperty(txtFieldComment.getText());

            shipping = new SimpleDoubleProperty(shippingDouble);
            price = new SimpleDoubleProperty(priceDouble);

            newCost = new Cost(id, quantity, printerId, name, purchaseDate, comment, shipping, price);
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

        int id = PrintedAPI.getCurrentAutoIncrementValue(ds, "Costs");
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText("New Cost");

        ObservableList<Printer> printers = controllerMain.getPrintersTv().getItems();
        comboBoxPrinter.setItems(printers);
        comboBoxPrinter.setVisibleRowCount(7);
        comboBoxPrinter.setConverter(new StringConverter<Printer>() {
            @Override
            public String toString(Printer object) {
                return object.getName();
            }

            @Override
            public Printer fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxPrinter.setValue(printers.get(0));

        datePickerPurchaseDate.setValue(LocalDate.now());
    }

}
