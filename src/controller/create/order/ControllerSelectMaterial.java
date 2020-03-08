package controller.create.order;

import classes.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerSelectMaterial implements Initializable {

    ControllerSetAdditionalData controllerSetAdditionalData;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Button btnSelect, btnCancel;

    @FXML
    private TableView<Material> tvMaterials;

    @FXML
    private TableColumn<Material, String> colColor, colManufacturer, colType, colDistributor, colPurchaseDate;

    @FXML
    private TableColumn<Material, Integer> colId;

    @FXML
    private TableColumn<Material, Double> colShipping,colPrice, colUsed, colDiameter, colWeight, colRemaining;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCols();

        btnSelect.setOnAction(event -> {
            controllerSetAdditionalData.setMaterial(tvMaterials.getSelectionModel().getSelectedItem());
            PrintedAPI.closeWindow(btnSelect);
        });

        tvMaterials.setRowFactory( tv -> {
            TableRow<Material> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    btnSelect.fire();
                }
            });
            return row;
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));
    }

    protected void setFields(ObservableList<Material> listOfNotSpentMaterials){
        tvMaterials.setItems(listOfNotSpentMaterials);
    }

    private void initializeCols(){
        colColor.setCellValueFactory((param) -> {return param.getValue().colorProperty();});
        colDistributor.setCellValueFactory((param) -> {return param.getValue().sellerProperty();});        
        colManufacturer.setCellValueFactory((param) -> {return param.getValue().manufacturerProperty();});
        colPurchaseDate.setCellValueFactory((param) -> {return param.getValue().purchaseDateProperty();});
        colType.setCellValueFactory((param) -> {return param.getValue().typeProperty();});

        colId.setCellValueFactory((param) -> {return param.getValue().idProperty().asObject();});
        colWeight.setCellValueFactory((param) -> {return param.getValue().weightProperty().asObject();});

        colRemaining.setCellValueFactory((param) -> {return param.getValue().remainingProperty().asObject();});
        colDiameter.setCellValueFactory((param) -> {return param.getValue().diameterProperty().asObject();});
        colPrice.setCellValueFactory((param) -> {return param.getValue().priceProperty().asObject();});
        colShipping.setCellValueFactory((param) -> {return param.getValue().shippingProperty().asObject();});
        colUsed.setCellValueFactory((param) -> {return param.getValue().usedProperty().asObject();});

        //Centering content
        PrintedAPI.centerColumns(tvMaterials.getColumns());
        tvMaterials.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    
    public void setControllerSetAdditionalData(ControllerSetAdditionalData controllerSetAdditionalData) {
        this.controllerSetAdditionalData = controllerSetAdditionalData;
    }
}
