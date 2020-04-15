package controller.edit;

import classes.Cost;
import classes.PrintedAPI;
import classes.Printer;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
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

public class ControllerEditCost implements Initializable {

    HikariDataSource ds;
    private ControllerMain controllerMain;

    private Cost editedCost;

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
            if (editCost()) {
                Cost.insertUpdateCost(editedCost, ds);
                PrintedAPI.closeWindow(btnCreate);
                //instead of loading all tables again, which consumes resources, we can just add new cost into existing list
                //and insert new cost into database.
                //PrintedAPI.serviceStart(controllerMain.getServiceDownloadAllTables());
                //controllerMain.getListOfCosts().add(0, editedCost);
                controllerMain.getCostsTv().refresh();
                controllerMain.calculateAllStatistics();
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean editCost(){
        try {
            //database table columns
            int quantity, printerId;
            String name, purchaseDate, comment, printerName;
            double shipping, price;

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

            if (shippingDouble < 0 || priceDouble <= 0 || quantityInt <= 0){
                labelInfo.setText("Numeric values must be greater than zero.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return  false;
            }


            PrintedAPI.checkApostrophe(txtFieldName, txtFieldComment);

            //It is existing cost being edited, therefore ID is known -> loaded from cost being edited
            //id = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));
            quantity = quantityInt;
            printerId = comboBoxPrinter.getSelectionModel().getSelectedItem().getId();

            name = txtFieldName.getText();
            purchaseDate = datePickerPurchaseDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            printerName = comboBoxPrinter.getValue().getName();
            comment = txtFieldComment.getText();

            shipping = shippingDouble;
            price = priceDouble;

            editedCost.setQuantity(quantity);
            editedCost.setPrinterId(printerId);
            editedCost.setName(name);
            editedCost.setPurchaseDate(purchaseDate);
            editedCost.setComment(comment);
            editedCost.setShipping(shipping);
            editedCost.setPrice(price);
            editedCost.setPrinterName(printerName);

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

    public void setFieldsValues(Cost cost) {

        editedCost = cost;

        int id = cost.getId();
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        //labelInfo.setText("Edit Fields");
        labelTitle.setText(id + ";" + cost.getName());

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

        for (Printer printer : printers) {
            if (printer.getId() == cost.getPrinterId()){
                comboBoxPrinter.setValue(printer);
                break;
            }
        }

        txtFieldName.setText(cost.getName());
        txtFieldQuantity.setText(cost.getQuantity() + "");
        txtFieldPrice.setText(cost.getPrice() + "");
        txtFieldShipping.setText(cost.getShipping() + "");
        datePickerPurchaseDate.setValue(LocalDate.parse(cost.getPurchaseDate()));
        txtFieldComment.setText(cost.getComment());
    }

    public Button getBtnCreate() {
        return btnCreate;
    }
}
