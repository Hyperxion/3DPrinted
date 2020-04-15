package controller.edit;

import classes.Object;
import classes.*;
import com.zaxxer.hikari.HikariDataSource;
import controller.create.order.ControllerSelectCustomer;
import controller.create.order.ControllerSelectObject;
import controller.create.order.ControllerSetAdditionalData;
import controller.main.ControllerMain;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ControllerEditOrder implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Order editedOrder;
    private Customer customer;

    private ObservableList<Material> listOfNotSpentMaterials;
    private ObservableList<Printer> listOfPrinters;
    private ObservableList<OrderItem> originalOrderItems;
    private ObservableList<OrderItem> editedOrderItems = FXCollections.observableArrayList();

    @FXML
    private ToggleGroup toggleGroupStatus = new ToggleGroup();

    @FXML
    private Label labelTitle, labelInfo, labelId, labelWeight, labelSupportWeight, labelWeightSum, labelQuantity, labelBuildTime, labelPrice, labelCosts, labelProfit;

    @FXML
    private TextField txtFieldCustomer, txtFieldComment, txtFieldPricePerHour;

    @FXML
    private TableView<OrderItem> tvSelectedObjects;

    @FXML
    private TableColumn<OrderItem, Integer> colId, colPrinterId, colMaterialID, colQuantity;

    @FXML
    private TableColumn<OrderItem, Double> colWeight, colSupportWeight, colPrice, colCosts;

    @FXML
    private TableColumn<OrderItem, String> colName, colPrinter, colBuildTimeFormatted, colMaterialType, colMaterialColor;

    @FXML
    private Button btnCreate, btnCancel, btnSelectCust, btnAddObj, btnRemoveSelected, btnCalculatePrices;

    @FXML
    private RadioButton radioBtnSold, radioBtnNotSold;

    @FXML
    private DatePicker datePickerDateCreated, datePickerDueDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCols();

        btnSelectCust.setOnAction(event -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/order/ViewSelectCustomer.fxml"));
                Parent root1 = fxmlLoader.load();
                ControllerSelectCustomer ctrl = fxmlLoader.getController();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Select Customer");
                //stage.setMinHeight(440);
                //stage.setMinWidth(506);

                stage.setScene(new Scene(root1));
                stage.setResizable(false);
                stage.centerOnScreen();

                stage.getScene().setOnKeyPressed(event1 -> {
                    switch (event1.getCode()) {
                        case ENTER:
                            ctrl.getBtnSelect().fire();
                            break;
                        case ESCAPE:
                            PrintedAPI.closeWindow(ctrl.getBtnSelect());
                    }
                });

                ctrl.setControllerEditOrder(this);
                ctrl.setControllerMain(controllerMain);
                ctrl.setCreated(false);
                ctrl.loadCustomers();
                ctrl.setCreated(false);

                stage.show();
            }catch (IOException e){
                e.printStackTrace();
            }
        });

        btnCreate.setOnAction(event -> {
            if (editOrder()) {
                Order.insertUpdateOrder(editedOrder, ds);
                OrderItem.insertUpdateOrderItem(editedOrderItems, ds, PrintedAPI.getCurrentAutoIncrementValue(ds, "OrderItems"));
                PrintedAPI.closeWindow(btnCreate);
                controllerMain.getOrdersTv().refresh();
                controllerMain.calculateAllStatistics();
            }
        });

        btnAddObj.setOnAction(event -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/order/ViewSelectObject.fxml"));
                Parent root1 = fxmlLoader.load();
                ControllerSelectObject ctrl = fxmlLoader.getController();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Select Object");
                //stage.setMinHeight(440);
                //stage.setMinWidth(506);

                stage.setScene(new Scene(root1));
                stage.setResizable(false);
                stage.centerOnScreen();

                stage.getScene().setOnKeyPressed(event1 -> {
                    switch (event1.getCode()) {
                        case ENTER:
                            ctrl.getBtnSelect().fire();
                            break;
                        case ESCAPE:
                            PrintedAPI.closeWindow(ctrl.getBtnSelect());
                    }
                });

                ctrl.setControllerEditOrder(this);
                ctrl.setControllerMain(controllerMain);
                ctrl.setCreated(false);
                ctrl.loadObjects();
                ctrl.setCreated(false);

                stage.show();
            }catch (IOException e){
                e.printStackTrace();
            }
        });

        btnCalculatePrices.setOnAction(event -> setPrices());

        tvSelectedObjects.setRowFactory( tv -> {
            TableRow<OrderItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    try{
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/order/ViewSetAdditionalData.fxml"));
                        Parent root1 = fxmlLoader.load();
                        ControllerSetAdditionalData ctrl = fxmlLoader.getController();
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setTitle("Set Additional Data");
                        //stage.setMinHeight(440);
                        //stage.setMinWidth(506);

                        stage.setScene(new Scene(root1));
                        stage.setResizable(false);
                        stage.centerOnScreen();
                        stage.show();

                        stage.getScene().setOnKeyPressed(event1 -> {
                            switch (event1.getCode()) {
                                case ENTER:
                                    ctrl.getBtnAssign().fire();
                                    break;
                                case ESCAPE:
                                    PrintedAPI.closeWindow(ctrl.getBtnAssign());
                            }
                        });

                        ctrl.setControllerEditOrder(this);
                        ctrl.setControllerMain(controllerMain);
                        ctrl.setCreated(false);
                        ctrl.setFieldsValues(tvSelectedObjects.getSelectionModel().getSelectedItem());
                        ctrl.calculateCosts(tvSelectedObjects.getSelectionModel().getSelectedItem());
                        ctrl.unlockFields();

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

        btnRemoveSelected.setOnAction(event -> {
            tvSelectedObjects.getItems().removeAll(tvSelectedObjects.getSelectionModel().getSelectedItems());
            tvSelectedObjects.refresh();
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    private void initializeCols(){

        colId.setCellValueFactory(param -> param.getValue().idProperty().asObject());
        colName.setCellValueFactory((param) -> param.getValue().getObject().nameProperty());
        colQuantity.setCellValueFactory((param) -> param.getValue().getObject().soldCountProperty().asObject());
        colPrinterId.setCellValueFactory((param) -> param.getValue().getPrinter().idProperty().asObject());
        colPrinter.setCellValueFactory((param) -> param.getValue().getPrinter().nameProperty());
        colBuildTimeFormatted.setCellValueFactory((param) -> param.getValue().getObject().buildTimeFormattedProperty());
        colMaterialID.setCellValueFactory((param) -> param.getValue().getMaterial().idProperty().asObject());
        colMaterialType.setCellValueFactory((param) -> param.getValue().getMaterial().typeProperty());
        colMaterialColor.setCellValueFactory((param) -> param.getValue().getMaterial().colorProperty());
        colWeight.setCellValueFactory((param) -> param.getValue().getObject().weightProperty().asObject());
        colSupportWeight.setCellValueFactory((param) -> param.getValue().getObject().supportWeightProperty().asObject());
        colPrice.setCellValueFactory((param) -> param.getValue().getObject().soldPriceProperty().asObject());
        colCosts.setCellValueFactory((param) -> param.getValue().getObject().costsProperty().asObject());

        colId.setCellValueFactory((param) -> param.getValue().getObject().idProperty().asObject());

        //Centering content
        PrintedAPI.centerColumns(tvSelectedObjects.getColumns());
        tvSelectedObjects.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void calculateStats(){
        double price = 0, weight = 0, supportWeight = 0, costs = 0, pricePerMinute;
        int buildTime = 0, quantity = 0;

        for (OrderItem item : editedOrderItems) {
            price += item.getObject().getSoldPrice();
            costs += item.getObject().getCosts();
            weight += item.getObject().getWeight();
            supportWeight += item.getObject().getSupportWeight();
            buildTime += item.getObject().getBuildTime();
            quantity += item.getObject().getSoldCount();
        }

        pricePerMinute = price / buildTime;

        txtFieldPricePerHour.setText(PrintedAPI.round(pricePerMinute * 60) + "");
        labelWeight.setText(PrintedAPI.round(weight) + " g");
        labelSupportWeight.setText(PrintedAPI.round(supportWeight) + " g");
        labelWeightSum.setText(PrintedAPI.round((weight + supportWeight)) + " g");
        labelQuantity.setText(quantity + "");
        labelBuildTime.setText(PrintedAPI.formatTime(buildTime).get());
        labelPrice.setText(PrintedAPI.round(price) + " $");
        labelCosts.setText(PrintedAPI.round(costs) + " $");
        labelProfit.setText(PrintedAPI.round((price - costs)) + " $");
    }

    public boolean editOrder(){
        try {
            //table columns
            int buildTime = 0, quantity = 0;
            String status, comment, dateCreated, dueDate;
            double costs = 0, price = 0, weight = 0, supportWeight = 0;

            if (PrintedAPI.areTxtFieldsEmpty(txtFieldCustomer)){
                labelInfo.setText("Customer field cannot be empty. Please, click on Select button and choose one.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            if (editedOrderItems.isEmpty()){
                labelInfo.setText("Please add some object to the list.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            for (OrderItem item : editedOrderItems) {
                price += item.getObject().getSoldPrice();
                costs += item.getObject().getCosts();
                weight += item.getObject().getWeight();
                supportWeight += item.getObject().getSupportWeight();
                buildTime += item.getObject().getBuildTime();
                quantity += item.getObject().getSoldCount();
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            RadioButton soldStatus = (RadioButton)toggleGroupStatus.getSelectedToggle();
            status = soldStatus.getText();
            comment = txtFieldComment.getText();
            dateCreated = datePickerDateCreated.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dueDate = datePickerDueDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            editedOrder.setBuildTime(buildTime);
            editedOrder.setQuantity(quantity);
            editedOrder.setStatus(status);
            editedOrder.setComment(comment);
            editedOrder.setDateCreated(dateCreated);
            editedOrder.setDueDate(dueDate);
            editedOrder.setCosts(costs);
            editedOrder.setPrice(price);
            editedOrder.setWeight(weight);
            editedOrder.setSupportWeight(supportWeight);
            editedOrder.setOrderItems(editedOrderItems);

            //when editing order, user can add or delete selected objects. We need to compare original list of orderItems
            //with new list of orderItems and remove from database deleted object in addition to adding new objects

            //toBeDeleted contains deleted items, i. e. items that were in original list, but they are not there anymore
            ObservableList<OrderItem> toBeDeleted = FXCollections.observableArrayList();

            for (OrderItem oldItem : originalOrderItems) {
                int oldId = oldItem.getId();

                for (OrderItem newItem : editedOrderItems) {
                    int newId = newItem.getId();

                    if (newId != oldId)toBeDeleted.add(oldItem);
                }
            }

            if(toBeDeleted != null)OrderItem.deleteOrderItems(toBeDeleted, ds);

            return true;
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            labelInfo.setText("Wrong number format! Please, check your values!");
            labelInfo.setTextFill(Color.web("#ff0000"));
            return false;
        }
    }

    private void setPrices(){
        try {
            ObservableList<OrderItem> items = tvSelectedObjects.getItems();

            double pricePerHour = Double.parseDouble(txtFieldPricePerHour.getText());
            double pricePerMinute = pricePerHour/60;

            if (pricePerHour <= 0) {
                labelInfo.setText("Price per hour must be higher than zero!");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return;
            }

            for (OrderItem item : items) {
                item.getObject().setSoldPrice(PrintedAPI.round(pricePerMinute * item.getObject().getBuildTime()));
            }

            calculateStats();
            tvSelectedObjects.refresh();

        } catch (NumberFormatException e) {
            //e.printStackTrace();
            labelInfo.setText("Wrong number format for price per hour! Please, check your values!");
            labelInfo.setTextFill(Color.web("#ff0000"));
            return;
        }
    }

    public void setDs(HikariDataSource ds) {
        this.ds = ds;
    }

    public void setControllerMain(ControllerMain controllerMain) {
        this.controllerMain = controllerMain;
    }

    public void setFieldsValues(Order order) {

        editedOrder = order;
        customer = order.getCustomer();
        editedOrderItems = order.getOrderItems();
        originalOrderItems = FXCollections.observableArrayList(editedOrderItems);
        tvSelectedObjects.setItems(editedOrderItems);


        listOfNotSpentMaterials = controllerMain.getListOfNotSpentMaterials();
        listOfPrinters = controllerMain.getListOfPrinters();

        int id = order.getId();
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText(id + ";" + customer.getLastName() + " " + customer.getFirstName() + ";Created at " + order.getDateCreated());

        txtFieldCustomer.setText(customer.getId() + ";" + customer.getLastName() + " " + customer.getFirstName());

        datePickerDateCreated.setValue(LocalDate.parse(order.getDateCreated()));
        datePickerDueDate.setValue(LocalDate.parse(order.getDueDate()));
        txtFieldComment.setText(order.getComment());
        switch (order.getStatus()){
            case "Not Sold":
                radioBtnNotSold.setSelected(true);
                break;
            default:
                radioBtnSold.setSelected(true);
        }

        calculateStats();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        txtFieldCustomer.setText(customer.getId() + ";" + customer.getLastName() + ";" + customer.getFirstName());
    }

    public ObservableList<Customer> getListOfCustomers(){
        return controllerMain.getListOfCustomers();
    }

    public ObservableList<Object> getListOfObjects(){
        return controllerMain.getListOfObjects();
    }

    public ObservableList<Material> getListOfNotSpentMaterials(){
        return controllerMain.getListOfNotSpentMaterials();
    }

    public ObservableList<Printer> getListOfPrinters(){
        return controllerMain.getListOfPrinters();
    }

    public void setObjects(ObservableList<Object> selectedObjects) {

        for (Object obj : selectedObjects){

            SimpleIntegerProperty id, orderId;
            Material material;
            Printer printer;

            obj.setBuildTimeFormatted(PrintedAPI.formatTime(obj.getBuildTime()).get());
            obj.setSoldCount(1);
            obj.setSoldPrice(0);
            obj.setCosts(0);

            id = new SimpleIntegerProperty(0);
            orderId = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));

            material = listOfNotSpentMaterials.get(0);
            printer = listOfPrinters.get(0);

            OrderItem item = new OrderItem(id, orderId, obj, material, printer);
            editedOrderItems.add(item);

        }
        tvSelectedObjects.refresh();
    }

    public TableView<OrderItem> getTvSelectedObjects() {
        return tvSelectedObjects;
    }

    public Button getBtnCreate() {
        return btnCreate;
    }
}
