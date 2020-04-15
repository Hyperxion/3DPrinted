package controller.edit;

import classes.Material;
import classes.PrintedAPI;
import classes.SimpleTableObject;
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

public class ControllerEditMaterial implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Material editedMaterial;

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
            if (editMaterial()) {
                Material.insertUpdateMaterial(editedMaterial, ds);
                PrintedAPI.closeWindow(btnCreate);
                controllerMain.getMatTv().refresh();
                controllerMain.calculateAllStatistics();
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean editMaterial(){
        try {
            //database table columns
            int manufacturerId, typeId, colorId, weightId, sellerId, diameterId;
            double price, shipping;
            String comment, purchaseDate;

            boolean areComboBoxesEmpty = PrintedAPI.areComboBoxesEmpty(comboBoxColor, comboBoxDiameter, comboBoxDistributor, comboBoxManufacturer, comboBoxType, comboBoxWeight);
            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldPrice, txtFieldShipping);

            if (areComboBoxesEmpty || areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            shipping = PrintedAPI.round(Double.parseDouble(txtFieldShipping.getText()));
            price = PrintedAPI.round(Double.parseDouble(txtFieldPrice.getText()));

            if (shipping <= 0 || price <= 0){
                labelInfo.setText("Numeric values must be greater than zero.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return  false;
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            manufacturerId = comboBoxManufacturer.getValue().getPropertyId();
            typeId = comboBoxType.getValue().getPropertyId();
            colorId = comboBoxColor.getValue().getPropertyId();
            weightId = comboBoxWeight.getValue().getPropertyId();
            sellerId = comboBoxDistributor.getValue().getPropertyId();
            diameterId = comboBoxDiameter.getValue().getPropertyId();

            purchaseDate = datePickerPurchaseDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            comment = txtFieldComment.getText();

            editedMaterial.setManufacturerId(manufacturerId);
            editedMaterial.setManufacturer(comboBoxManufacturer.getValue().getPropertyName());
            editedMaterial.setTypeId(typeId);
            editedMaterial.setType(comboBoxType.getValue().getPropertyName());
            editedMaterial.setColorId(colorId);
            editedMaterial.setColor(comboBoxColor.getValue().getPropertyName());
            editedMaterial.setWeightId(weightId);
            editedMaterial.setWeight(Double.parseDouble(comboBoxWeight.getValue().getPropertyName()));
            editedMaterial.setSellerId(sellerId);
            editedMaterial.setSeller(comboBoxDistributor.getValue().getPropertyName());
            editedMaterial.setDiameterId(diameterId);
            editedMaterial.setDiameter(Double.parseDouble(comboBoxDiameter.getValue().getPropertyName()));
            editedMaterial.setPrice(price);
            editedMaterial.setShipping(shipping);

            editedMaterial.setComment(comment);
            editedMaterial.setPurchaseDate(purchaseDate);

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

    public void setFieldsValues(Material material) {

        editedMaterial = material;

        int id = material.getId();
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText(id + ";" + material.getType() + ";" + material.getColor());

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

        for (SimpleTableObject type : types) {
            if (type.getPropertyId() == material.getTypeId()){
                comboBoxType.setValue(type);
                break;
            }
        }

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

        for (SimpleTableObject color : colors) {
            if (color.getPropertyId() == material.getColorId()){
                comboBoxColor.setValue(color);
                break;
            }
        }

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

        for (SimpleTableObject diameter : diameters) {
            if (diameter.getPropertyId() == material.getDiameterId()){
                comboBoxDiameter.setValue(diameter);
                break;
            }
        }

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

        for (SimpleTableObject distributor : distributors) {
            if (distributor.getPropertyId() == material.getSellerId()){
                comboBoxDistributor.setValue(distributor);
                break;
            }
        }

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

        for (SimpleTableObject manufacturer : manufacturers) {
            if (manufacturer.getPropertyId() == material.getManufacturerId()){
                comboBoxManufacturer.setValue(manufacturer);
                break;
            }
        }


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

        for (SimpleTableObject weight : weights) {
            if (weight.getPropertyId() == material.getWeightId()){
                comboBoxWeight.setValue(weight);
                break;
            }
        }

        txtFieldPrice.setText(material.getPrice() + "");
        txtFieldShipping.setText(material.getShipping() + "");
        datePickerPurchaseDate.setValue(LocalDate.parse(material.getPurchaseDate()));
        txtFieldComment.setText(material.getComment());

    }

    public Button getBtnCreate() {
        return btnCreate;
    }
}
