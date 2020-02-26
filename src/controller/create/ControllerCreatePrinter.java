package controller.create;

import classes.PrintedAPI;
import classes.Printer;
import classes.SimpleTableObject;
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

public class ControllerCreatePrinter implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Printer newPrinter;

    @FXML
    private Label labelTitle, labelInfo, labelId;

    @FXML
    private TextField txtFieldName, txtFieldPrice, txtFieldShipping, txtFieldComment, txtFieldDuty, txtFieldTax;

    @FXML
    private DatePicker datePickerPurchaseDate;

    @FXML
    private Button btnCreate, btnCancel;

    @FXML
    private ComboBox<SimpleTableObject> comboBoxType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCreate.setOnAction(event -> {
            if (createPrinter()) {
                Printer.insertUpdatePrinter(newPrinter, ds);
                PrintedAPI.closeWindow(btnCreate);
                PrintedAPI.serviceStart(controllerMain.getServiceDownloadAllTables());
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean createPrinter(){
        try {
            //database table columns
            SimpleIntegerProperty id, typeID;
            SimpleStringProperty name, purchaseDate, comment;
            SimpleDoubleProperty shipping, price, duty, tax;

            boolean areComboBoxesEmpty = PrintedAPI.areComboBoxesEmpty(comboBoxType);
            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldDuty, txtFieldName, txtFieldPrice, txtFieldShipping, txtFieldTax);

            if (areComboBoxesEmpty || areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            double shippingDouble = PrintedAPI.round(Double.parseDouble(txtFieldShipping.getText()));
            double priceDouble = PrintedAPI.round(Double.parseDouble(txtFieldPrice.getText()));
            double dutyDouble = PrintedAPI.round(Double.parseDouble(txtFieldDuty.getText()));
            double taxDouble = PrintedAPI.round(Double.parseDouble(txtFieldTax.getText()));

            if (shippingDouble <= 0 || priceDouble <= 0 || dutyDouble <= 0 || taxDouble <= 0) {
                labelInfo.setText("Numeric values must be greater than zero.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            id = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));
            typeID = new SimpleIntegerProperty(comboBoxType.getValue().getPropertyId());

            name = new SimpleStringProperty(txtFieldName.getText());
            purchaseDate = new SimpleStringProperty(datePickerPurchaseDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            comment = new SimpleStringProperty(txtFieldComment.getText());

            shipping = new SimpleDoubleProperty(shippingDouble);
            price = new SimpleDoubleProperty(priceDouble);
            duty = new SimpleDoubleProperty(dutyDouble);
            tax = new SimpleDoubleProperty(taxDouble);

            newPrinter = new Printer(id, typeID, name, purchaseDate, comment, shipping, price, duty, tax);
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

        int id = PrintedAPI.getCurrentAutoIncrementValue(ds, "Printers");
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText("New Material");

        datePickerPurchaseDate.setValue(LocalDate.now());

        ObservableList<SimpleTableObject> printerTypes = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonMaterialProperties(), 7);

        comboBoxType.setItems(printerTypes);
        comboBoxType.setVisibleRowCount(7);
        comboBoxType.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxType.setValue(printerTypes.get(0));

    }
}
