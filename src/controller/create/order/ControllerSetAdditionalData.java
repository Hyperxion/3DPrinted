package controller.create.order;

import classes.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ControllerSetAdditionalData implements Initializable {

    private ControllerCreateOrder controllerCreateOrder;
    private OrderItem selectedItem;
    private Material selectedMaterial;

    @FXML
    private ComboBox<Printer> comboBoxPrinter;

    @FXML //these text fields are locked for editing by default
    private TextField txtFieldQuantity, txtFieldWeight, txtFieldSupportWeight, txtFieldHours, txtFieldMinutes, txtFieldPrice;

    @FXML //these text fields are not locked for editing by default
    private TextField txtFieldCosts, txtFieldMaterial;

    @FXML
    private Button btnSelectMaterial, btnCancel, btnAssign;

    @FXML
    private Label labelEditedObject, labelInfo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnSelectMaterial.setOnAction(event -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/order/ViewSelectMaterial.fxml"));
                Parent root1 = fxmlLoader.load();
                ControllerSelectMaterial ctrl = fxmlLoader.getController();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Select Material");
                //stage.setMinHeight(440);
                //stage.setMinWidth(506);

                stage.setScene(new Scene(root1));
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();

                ctrl.setControllerSetAdditionalData(this);
                ctrl.setFields(controllerCreateOrder.getListOfNotSpentMaterials());

            } catch (IOException e){
                e.printStackTrace();
            }
        });

        btnAssign.setOnAction(event -> {
            if (assignValues())PrintedAPI.closeWindow(btnAssign);
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean assignValues(){
        try {

            Printer printer = comboBoxPrinter.getValue();
            Material mat = this.selectedMaterial;

            boolean areComboBoxesEmpty = PrintedAPI.areComboBoxesEmpty(comboBoxPrinter);
            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldQuantity, txtFieldWeight, txtFieldSupportWeight, txtFieldHours, txtFieldMinutes, txtFieldPrice);

            if (areComboBoxesEmpty || areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            double price = PrintedAPI.round(Double.parseDouble(txtFieldPrice.getText()));
            double costs = PrintedAPI.round(Double.parseDouble(txtFieldCosts.getText()));
            double weight = PrintedAPI.round(Double.parseDouble(txtFieldCosts.getText()));
            double supportWeight = PrintedAPI.round(Double.parseDouble(txtFieldCosts.getText()));

            int quantity = Integer.parseInt(txtFieldQuantity.getText());
            int hours = Integer.parseInt(txtFieldHours.getText());
            int minutes = Integer.parseInt(txtFieldMinutes.getText());
            int buildTime = hours * 60 + minutes;

            if (costs <= 0 || price <= 0 || weight <= 0 || supportWeight < 0 || quantity <= 0 || hours < 0){
                labelInfo.setText("Numeric values must be greater than zero.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return  false;
            }

            if (minutes < 0 || minutes > 59){
                labelInfo.setText("Minutes value must be in range of 1 - 59 inclusive");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return  false;
            }

            selectedItem.getObject().setSoldCount(quantity);
            selectedItem.getObject().setWeight(weight);
            selectedItem.getObject().setSupportWeight(supportWeight);
            selectedItem.getObject().setBuildTime(buildTime);

            if (buildTime > 1440) {
                selectedItem.getObject().setBuildTimeFormatted(PrintedAPI.formatTimeToDays(buildTime));
            } else {
                selectedItem.getObject().setBuildTimeFormatted(PrintedAPI.formatTime(buildTime).get());
            }

            selectedItem.setPrinter(printer);
            selectedItem.setMaterial(mat);
            selectedItem.getObject().setSoldPrice(price);
            selectedItem.getObject().setCosts(costs);

            return true;
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            labelInfo.setText("Wrong number format! Please, check your values!");
            labelInfo.setTextFill(Color.web("#ff0000"));
            return false;
        }
    }

    public void setFieldsValues(OrderItem selectedItem) {

        this.selectedItem = selectedItem;
        this.selectedMaterial = selectedItem.getMaterial();

        int id = selectedItem.getId();
        String name = selectedItem.getObject().getName();

        double price = PrintedAPI.round(selectedItem.getObject().getSoldPrice());
        double costs = PrintedAPI.round(selectedItem.getObject().getCosts());
        double weight = PrintedAPI.round(selectedItem.getObject().getWeight());
        double supportWeight = PrintedAPI.round(selectedItem.getObject().getSupportWeight());

        int quantity = selectedItem.getObject().getSoldCount();
        int buildTime = selectedItem.getObject().getBuildTime();

        int hours = buildTime % 60;
        int minutes = buildTime - hours*60;

        txtFieldQuantity.setText(quantity + "");
        txtFieldWeight.setText(weight + "");
        txtFieldSupportWeight.setText(supportWeight + "");
        txtFieldHours.setText(hours + "");
        txtFieldMinutes.setText(minutes + "");
        txtFieldPrice.setText(price + "");
        txtFieldCosts.setText(costs + "");
        txtFieldMaterial.setText(selectedMaterial.getId() + ";" + selectedMaterial.getType() + ";" + selectedMaterial.getColor());

        labelEditedObject.setText(String.format("%d; %s", id, name));

        ObservableList<Printer> printers = controllerCreateOrder.getListOfPrinters();
        comboBoxPrinter.setItems(printers);
        comboBoxPrinter.setVisibleRowCount(7);
        comboBoxPrinter.setConverter(new StringConverter<Printer>() {
            @Override
            public String toString(Printer object) {
                return object.getId() + "; " + object.getName();
            }

            @Override
            public Printer fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        //if there is already some printer set, set this printer, otherwise set first printer in the list
        if (selectedItem.getPrinter().getId() == 0) {
            comboBoxPrinter.setValue(printers.get(0));
        } else {
            for (Printer printer : printers){
                if (printer.getId() == selectedItem.getPrinter().getId())comboBoxPrinter.setValue(printer);
            }
        }
    }

    public void setControllerCreateOrder(ControllerCreateOrder controllerCreateOrder) {
        this.controllerCreateOrder = controllerCreateOrder;
    }

    public void setMaterial(Material selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
        this.selectedItem.setMaterial(selectedMaterial);
    }
}
