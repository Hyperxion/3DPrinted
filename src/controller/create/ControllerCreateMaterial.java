package controller.create;

import classes.Material;
import classes.PrintedAPI;
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

public class ControllerCreateMaterial implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Material newMaterial;

    @FXML
    private Label labelTitle, labelInfo, labelId;

    @FXML
    private TextField txtFieldPrice, txtFieldShipping, txtFieldComment;

    @FXML
    private DatePicker datePickerPurchaseDate;

    @FXML
    private Button btnCreate, btnCancel;

    @FXML
    private ComboBox<SimpleTableObject> comboBoxType, comboBoxColor, comboBoxDiameter, comboBoxWeight, comboBoxManufacturer, comboBoxDistributor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCreate.setOnAction(event -> {
            if (createMaterial()) {
                Material.insertUpdateMaterial(newMaterial, ds);
                PrintedAPI.closeWindow(btnCreate);
                controllerMain.getListOfMaterials().add(0, newMaterial);
                controllerMain.getMatTv().refresh();
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean createMaterial(){
        try {
            //database table columns
            SimpleIntegerProperty id, manufacturerId, typeId, colorId, weightId, sellerId, diameterId;
            SimpleDoubleProperty price, shipping;
            SimpleStringProperty comment, purchaseDate;

            boolean areComboBoxesEmpty = PrintedAPI.areComboBoxesEmpty(comboBoxColor, comboBoxDiameter, comboBoxDistributor, comboBoxManufacturer, comboBoxType, comboBoxWeight);
            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldPrice, txtFieldShipping);

            if (areComboBoxesEmpty || areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            double shippingDouble = PrintedAPI.round(Double.parseDouble(txtFieldShipping.getText()));
            double priceDouble = PrintedAPI.round(Double.parseDouble(txtFieldPrice.getText()));

            if (shippingDouble <= 0 || priceDouble <= 0){
                labelInfo.setText("Numeric values must be greater than zero.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return  false;
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            id = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));
            manufacturerId = new SimpleIntegerProperty(comboBoxManufacturer.getValue().getPropertyId());
            typeId = new SimpleIntegerProperty(comboBoxType.getValue().getPropertyId());
            colorId = new SimpleIntegerProperty(comboBoxColor.getValue().getPropertyId());
            weightId = new SimpleIntegerProperty(comboBoxWeight.getValue().getPropertyId());
            sellerId = new SimpleIntegerProperty(comboBoxDistributor.getValue().getPropertyId());
            diameterId = new SimpleIntegerProperty(comboBoxDiameter.getValue().getPropertyId());

            purchaseDate = new SimpleStringProperty(datePickerPurchaseDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            comment = new SimpleStringProperty(txtFieldComment.getText());

            shipping = new SimpleDoubleProperty(shippingDouble);
            price = new SimpleDoubleProperty(priceDouble);

            newMaterial = new Material(id, manufacturerId, typeId, colorId, weightId, sellerId, diameterId, price, shipping, new SimpleDoubleProperty(0), comment, new SimpleStringProperty("No"), purchaseDate);
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

        int id = PrintedAPI.getCurrentAutoIncrementValue(ds, "Materials");
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText("New Material");
        datePickerPurchaseDate.setValue(LocalDate.now());

        ObservableList<SimpleTableObject> types = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonMaterialProperties(), 1);
        ObservableList<SimpleTableObject> colors = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonMaterialProperties(), 2);
        ObservableList<SimpleTableObject> manufacturers = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonMaterialProperties(), 3);
        ObservableList<SimpleTableObject> distributors = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonMaterialProperties(), 4);
        ObservableList<SimpleTableObject> diameters = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonMaterialProperties(), 5);
        ObservableList<SimpleTableObject> weights = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonMaterialProperties(), 6);

        comboBoxType.setItems(types);
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
        comboBoxType.setValue(types.get(0));

        comboBoxColor.setItems(colors);
        comboBoxColor.setVisibleRowCount(7);
        comboBoxColor.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxColor.setValue(colors.get(0));

        comboBoxDiameter.setItems(diameters);
        comboBoxDiameter.setVisibleRowCount(7);
        comboBoxDiameter.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxDiameter.setValue(diameters.get(0));

        comboBoxDistributor.setItems(distributors);
        comboBoxDistributor.setVisibleRowCount(7);
        comboBoxDistributor.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxDistributor.setValue(distributors.get(0));

        comboBoxManufacturer.setItems(manufacturers);
        comboBoxManufacturer.setVisibleRowCount(7);
        comboBoxManufacturer.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxManufacturer.setValue(manufacturers.get(0));

        comboBoxWeight.setItems(weights);
        comboBoxWeight.setVisibleRowCount(7);
        comboBoxWeight.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxWeight.setValue(weights.get(0));

    }

    public Button getBtnCreate() {
        return btnCreate;
    }
}
