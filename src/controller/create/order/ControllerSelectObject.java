package controller.create.order;

import classes.Material;
import classes.Object;
import classes.PrintedAPI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerSelectObject implements Initializable {

    private ControllerCreateOrder controllerCreateOrder;
    private ObservableList<Object> listOfObjects;
    private ObservableList<Material> notSpentMaterials;

    @FXML
    private TableView<Object> tvObjects;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Button btnSelect, btnClose;

    @FXML
    private TableColumn<Object, String> colName, colComment, colBuildTimeFormatted;

    @FXML
    private TableColumn<Object, Integer> colId, colSoldCount;

    @FXML
    private TableColumn<Object, Double> colWeight, colSupportWeight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCols();

        btnSelect.setOnAction(event -> {
            controllerCreateOrder.setObjects(tvObjects.getSelectionModel().getSelectedItems());
            controllerCreateOrder.calculateStats();
            PrintedAPI.closeWindow(btnSelect);

        });

        btnClose.setOnAction(event -> PrintedAPI.closeWindow(btnClose));

        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> searchObjects(newValue));

        tvObjects.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    btnSelect.fire();
                }
            });
            return row;
        });
    }

    public void loadObjects() {
        listOfObjects = controllerCreateOrder.getListOfObjects();
        tvObjects.setItems(listOfObjects);
    }

    private void searchObjects(String objName) {
        ObservableList<Object> filteredObjects = FXCollections.observableArrayList();

        if (objName.isEmpty()){
            tvObjects.setItems(listOfObjects);
            return;
        } else {
            for (Object obj : listOfObjects){
                String string = obj.getName();
                if (string.contains(objName)) {
                    filteredObjects.add(obj);
                }
            }

            tvObjects.setItems(filteredObjects);
        }
    }

    private void initializeCols(){

        colId.setCellValueFactory(param -> param.getValue().idProperty().asObject());
        colSoldCount.setCellValueFactory((param) -> param.getValue().soldCountProperty().asObject());

        colName.setCellValueFactory((param) -> param.getValue().nameProperty());
        colBuildTimeFormatted.setCellValueFactory((param) -> param.getValue().buildTimeFormattedProperty());
        colComment.setCellValueFactory((param) -> param.getValue().commentProperty());

        colWeight.setCellValueFactory((param) -> param.getValue().weightProperty().asObject());
        colSupportWeight.setCellValueFactory((param) -> param.getValue().supportWeightProperty().asObject());

        //Centering content
        PrintedAPI.centerColumns(tvObjects.getColumns());
        tvObjects.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setControllerCreateOrder(ControllerCreateOrder controllerCreateOrder) {
        this.controllerCreateOrder = controllerCreateOrder;
    }

    public Button getBtnSelect() {
        return btnSelect;
    }
}
