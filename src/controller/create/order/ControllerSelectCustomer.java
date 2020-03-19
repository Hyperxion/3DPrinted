package controller.create.order;

import classes.Customer;
import classes.PrintedAPI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerSelectCustomer implements Initializable {

    ControllerCreateOrder controllerCreateOrder = null;
    ObservableList<Customer> listOfCustomers;

    @FXML
    private TableView<Customer> tvCustomers;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Button btnSelect, btnClose;

    @FXML
    private TableColumn<Customer, String> colLastName, colFirstName, colMail, colPhone, colCompany, colDateCreated;

    @FXML
    private TableColumn<Customer, Integer> colId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCols();

        btnSelect.setOnAction(event -> {
            controllerCreateOrder.setCustomer(tvCustomers.getSelectionModel().getSelectedItem());
            PrintedAPI.closeWindow(btnSelect);
        });

        btnClose.setOnAction(event -> PrintedAPI.closeWindow(btnClose));

        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> searchCustomers(newValue));

        tvCustomers.setRowFactory( tv -> {
            TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    btnSelect.fire();
                }
            });
            return row;
        });
    }

    public void loadCustomers() {
        listOfCustomers = controllerCreateOrder.getListOfCustomers();
        tvCustomers.setItems(listOfCustomers);
    }

    private void searchCustomers(String lastName) {
        ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();

        if (lastName.isEmpty()){
            tvCustomers.setItems(listOfCustomers);
        } else {
            for (Customer customer : listOfCustomers){
                String string = customer.getLastName();
                if (string.contains(lastName)) {
                    filteredCustomers.add(customer);
                }
            }

        tvCustomers.setItems(filteredCustomers);
        }
    }

    private void initializeCols(){

        colCompany.setCellValueFactory(param -> param.getValue().commentProperty());
        colDateCreated.setCellValueFactory((param) -> param.getValue().dateCreatedProperty());
        colFirstName.setCellValueFactory((param) -> param.getValue().firstNameProperty());
        colLastName.setCellValueFactory((param) -> param.getValue().lastNameProperty());
        colMail.setCellValueFactory((param) -> param.getValue().mailProperty());
        colPhone.setCellValueFactory((param) -> param.getValue().phoneProperty());

        colId.setCellValueFactory((param) -> param.getValue().idProperty().asObject());

        //Centering content
        PrintedAPI.centerColumns(tvCustomers.getColumns());
        tvCustomers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setControllerCreateOrder(ControllerCreateOrder controllerCreateOrder) {
        this.controllerCreateOrder = controllerCreateOrder;
    }

    public Button getBtnSelect() {
        return btnSelect;
    }
}
