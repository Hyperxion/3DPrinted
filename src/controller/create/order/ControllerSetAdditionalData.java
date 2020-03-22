package controller.create.order;

import classes.*;
import controller.create.ControllerCreateOrder;
import controller.edit.ControllerEditOrder;
import controller.main.ControllerMain;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ControllerSetAdditionalData implements Initializable {

    private ControllerCreateOrder controllerCreateOrder;
    private ControllerEditOrder controllerEditOrder;
    private ControllerMain controllerMain;

    private OrderItem selectedItem;
    private Material selectedMaterial;

    private boolean isCreated = true;

    @FXML
    private ComboBox<Printer> comboBoxPrinter;

    //All text fields are locked for editing by default

    @FXML //these text fields are being unlocked. For more info, head to unlockField() method.
    private TextField txtFieldQuantity, txtFieldWeight, txtFieldSupportWeight, txtFieldHours, txtFieldMinutes, txtFieldPrice;

    @FXML //these text fields are never being unlocked.
    private TextField txtFieldCosts, txtFieldMaterial;

    @FXML
    private Button btnSelectMaterial, btnCancel, btnAssign;

    @FXML
    private Label labelEditedObject, labelInfo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        txtFieldQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                //when you first add object, quantity is set to 1. that's it's default value. but when you change it, and assign it
                //it will overwrite the object's properties and thus when you want to change some information next time, it will load
                //previously overwritten values and it will consider that new values are valid for one unit. If you change quantity from
                //1 to 3, and object weights 50 grams, new weight will be 150 grams. If you then change quantity from 3 to 6, result will be
                //3 * 150 = 450, not 3 * 50 = 150. We can get original weights and build times by dividing values by quantity. How do we know
                //that the quantity has been changed? Well, it is not equal to 1

                //there are values entered during editing object's properties. It can be changed multiple time back and forth
                int quantityNew = Integer.parseInt(newValue);

                labelInfo.setText("Fill the fields");
                labelInfo.setTextFill(Color.web("#00cd00"));

                //this is object's quantity at the moment of opening window
                int quantity = selectedItem.getObject().getSoldCount();

                if (quantityNew <= 0) {
                    labelInfo.setText("Quantity must be higher than zero!");
                    labelInfo.setTextFill(Color.web("#ff0000"));
                    return;
                }

                //let's get properties per one unit
                double weight = selectedItem.getObject().getWeight() / quantity;
                double supportWeight = selectedItem.getObject().getSupportWeight() / quantity;
                int buildTime = selectedItem.getObject().getBuildTime() / quantity;

                //now we can multiply them by new value
                double weightNew = weight * quantityNew;
                double supportWeightNew = supportWeight * quantityNew;
                int buildTimeNew = buildTime * quantityNew;

                //and we can set them up in fields
                txtFieldWeight.setText(weightNew + "");
                txtFieldSupportWeight.setText(supportWeightNew + "");
                txtFieldMinutes.setText((buildTimeNew % 60) + "");
                txtFieldHours.setText((buildTimeNew / 60) + "");

                //and finally, update costs
                calculateCosts();

            } catch (NumberFormatException e) {
                labelInfo.setText("Wrong number format! Please, check your values!");
                labelInfo.setTextFill(Color.web("#ff0000"));
            }
        });

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

                stage.getScene().setOnKeyPressed(event1 -> {
                    switch (event1.getCode()) {
                        case ENTER:
                            ctrl.getBtnSelect().fire();
                            break;
                        case ESCAPE:
                            PrintedAPI.closeWindow(ctrl.getBtnSelect());
                    }
                });

                ctrl.setControllerSetAdditionalData(this);
                ctrl.setFields(controllerMain.getListOfNotSpentMaterials());

            } catch (IOException e){
                e.printStackTrace();
            }
        });

        btnAssign.setOnAction(event -> {
            if (assignValues())PrintedAPI.closeWindow(btnAssign);

            if (isCreated) {
                controllerCreateOrder.getTvSelectedObjects().refresh();
                controllerCreateOrder.calculateStats();
            } else {
                controllerEditOrder.getTvSelectedObjects().refresh();
                controllerEditOrder.calculateStats();
            }

        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean assignValues(){
        try {

            Printer printer = comboBoxPrinter.getValue();

            boolean areComboBoxesEmpty = PrintedAPI.areComboBoxesEmpty(comboBoxPrinter);
            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldQuantity, txtFieldWeight, txtFieldSupportWeight, txtFieldHours, txtFieldMinutes, txtFieldPrice);

            if (areComboBoxesEmpty || areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            double price = PrintedAPI.round(Double.parseDouble(txtFieldPrice.getText()));
            double costs = PrintedAPI.round(Double.parseDouble(txtFieldCosts.getText()));
            double weight = PrintedAPI.round(Double.parseDouble(txtFieldWeight.getText()));
            double supportWeight = PrintedAPI.round(Double.parseDouble(txtFieldSupportWeight.getText()));

            int quantity = Integer.parseInt(txtFieldQuantity.getText());
            int hours = Integer.parseInt(txtFieldHours.getText());
            int minutes = Integer.parseInt(txtFieldMinutes.getText());
            int buildTime = hours * 60 + minutes;

            if (costs < 0 || price < 0 || weight <= 0 || supportWeight < 0 || quantity <= 0 || hours < 0 || buildTime <= 0){
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
            selectedItem.setMaterial(selectedMaterial);
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

        int id = selectedItem.getObject().getId();
        String name = selectedItem.getObject().getName();

        double price = PrintedAPI.round(selectedItem.getObject().getSoldPrice());
        double costs = PrintedAPI.round(selectedItem.getObject().getCosts());
        double weight = PrintedAPI.round(selectedItem.getObject().getWeight());
        double supportWeight = PrintedAPI.round(selectedItem.getObject().getSupportWeight());

        int quantity = selectedItem.getObject().getSoldCount();
        int buildTime = selectedItem.getObject().getBuildTime();

        int hours = buildTime / 60;
        int minutes = buildTime % 60;

        txtFieldQuantity.setText(quantity + "");
        txtFieldWeight.setText(weight + "");
        txtFieldSupportWeight.setText(supportWeight + "");
        txtFieldHours.setText(hours + "");
        txtFieldMinutes.setText(minutes + "");
        txtFieldPrice.setText(price + "");
        txtFieldCosts.setText(costs + "");
        txtFieldMaterial.setText(selectedMaterial.getId() + ";" + selectedMaterial.getType() + ";" + selectedMaterial.getColor());

        labelEditedObject.setText(String.format("%d; %s", id, name));

        ObservableList<Printer> printers = controllerMain.getListOfPrinters();
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

    //this one is used when calculating costs using selected item, i. e. when it is double clicked from new order dialog
    public void calculateCosts(OrderItem selectedItem){
        try {

            Material selectedMaterial = selectedItem.getMaterial();

            double matWeight = selectedMaterial.getWeight();
            double matPrice = selectedMaterial.getPrice() + selectedMaterial.getShipping();
            double pricePerGram = matPrice / matWeight;
            double weight = selectedItem.getObject().getWeight();
            double supportWeight = selectedItem.getObject().getSupportWeight();
            double objWeight = weight + supportWeight;

            double costs = pricePerGram * objWeight;
            txtFieldCosts.setText(PrintedAPI.round(costs) + "");

        } catch (NumberFormatException e){
            labelInfo.setText("Wrong number format!");
            labelInfo.setTextFill(Color.web("#ff0000"));
        }
    }

    //this one is used with values from text fields when changing material, weight or quantity information in setAdditionalData dialog
    public void calculateCosts(){
        try {

            double costs;
            double matWeight = selectedMaterial.getWeight();
            double matPrice = selectedMaterial.getPrice() + selectedMaterial.getShipping();
            double pricePerGram = matPrice / matWeight;
            double weight = Double.parseDouble(txtFieldWeight.getText());
            double supportWeight = Double.parseDouble(txtFieldSupportWeight.getText());
            double objWeight = weight + supportWeight;

            costs = pricePerGram * objWeight;
            txtFieldCosts.setText(PrintedAPI.round(costs) + "");

        } catch (NumberFormatException e){
            labelInfo.setText("Wrong number format!");
            labelInfo.setTextFill(Color.web("#ff0000"));
        }
    }

    //this method will unlock fields based on edited item. If it is 3D printing as service, amount will be always 1 and we will want to change
    //only weights, build time and price. There we will lock quantity field and unlock rest of the text fields.
    //In case that it is predefined object, we will want to change only quantity and price - all other information are predefined in Objects tab.
    //All in all, we must define 3D printing object in Objects tab with id = 1
    public void unlockFields(){
        int id = selectedItem.getObject().getId();

        switch (id){
            case 1:
                txtFieldQuantity.setText("1");

                txtFieldWeight.setEditable(true);
                txtFieldSupportWeight.setEditable(true);
                txtFieldMinutes.setEditable(true);
                txtFieldHours.setEditable(true);
                txtFieldPrice.setEditable(true);
                break;
            default:
                txtFieldQuantity.setEditable(true);
        }


    }

    public void setControllerCreateOrder(ControllerCreateOrder controllerCreateOrder) {
        this.controllerCreateOrder = controllerCreateOrder;
    }

    public void setMaterial(Material selectedMaterial) {
        this.selectedMaterial = selectedMaterial;
        this.selectedItem.setMaterial(selectedMaterial);
        txtFieldMaterial.setText(selectedMaterial.getId() + ";" + selectedMaterial.getType() + ";" + selectedMaterial.getColor());
    }

    public Button getBtnAssign() {
        return btnAssign;
    }

    public void setControllerEditOrder(ControllerEditOrder controllerEditOrder) {
        this.controllerEditOrder = controllerEditOrder;
    }

    public void setControllerMain(ControllerMain controllerMain) {
        this.controllerMain = controllerMain;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }
}
