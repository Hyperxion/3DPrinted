package controller.create.order;

import classes.*;
import classes.Object;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ControllerCreateOrder implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Order newOrder;
    private Customer customer;

    private ObservableList<Material> listOfNotSpentMaterials;
    private ObservableList<Printer> listOfPrinters;

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
    private Button btnCreate, btnCancel, btnSelectCust, btnAddObj, btnRemoveSelected;

    @FXML
    private RadioButton radioButtonSold;

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

                ctrl.setControllerCreateOrder(this);
                ctrl.loadCustomers();

                stage.show();
            }catch (IOException e){
                e.printStackTrace();
            }
        });

        btnCreate.setOnAction(event -> {
            if (createOrder()) {
                Order.insertUpdateOrder(newOrder, ds);
                PrintedAPI.closeWindow(btnCreate);
                PrintedAPI.serviceStart(controllerMain.getServiceDownloadAllTables());
            }
        });

        tvSelectedObjects.getItems().addListener((ListChangeListener.Change<? extends OrderItem> c) -> {
            calculateStats();
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

                ctrl.setControllerCreateOrder(this);
                ctrl.loadObjects();

                stage.show();
            }catch (IOException e){
                e.printStackTrace();
            }
        });

//        tvSelectedObjects.setRowFactory( tv -> {
//            TableRow<OrderItem> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
//                    try {
//                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/select/SelectPrinterMaterialPrice.fxml"));
//                        Parent root1 = fxmlLoader.load();
//                        SelectPrinterMaterialPriceController ctrl = fxmlLoader.getController();
//                        Stage stage = new Stage();
//                        stage.initModality(Modality.APPLICATION_MODAL);
//                        stage.setTitle("Assign Additional Information");
//
//                        stage.setScene(new Scene(root1));
//                        stage.setResizable(false);
//                        stage.centerOnScreen();
//
//                        stage.show();
//                        //stage.setAlwaysOnTop(true);
//                        ctrl.setDs(ds);
//                        ctrl.setMainController(mainController);
//                        ctrl.setNewOrderController(this);
//                        ctrl.setFields(tv_orderObjects.getSelectionModel().getSelectedItem());
//
//                    }catch (IOException e){
//
//                    }
//
//                }
//            });
//            return row;
//        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    private void initializeCols(){

        colId.setCellValueFactory(param -> param.getValue().idProperty().asObject());
        colName.setCellValueFactory((param) -> param.getValue().getObject().nameProperty());
        colQuantity.setCellValueFactory((param) -> param.getValue().getObject().soldCountProperty().asObject());
        colPrinterId.setCellValueFactory((param) -> param.getValue().getPrinter().idProperty().asObject());
        colBuildTimeFormatted.setCellValueFactory((param) -> param.getValue().getObject().buildTimeFormattedProperty());
        colMaterialID.setCellValueFactory((param) -> param.getValue().getMaterial().idProperty().asObject());
        colMaterialType.setCellValueFactory((param) -> param.getValue().getMaterial().typeProperty());
        colMaterialColor.setCellValueFactory((param) -> param.getValue().getMaterial().colorProperty());
        colWeight.setCellValueFactory((param) -> param.getValue().getObject().weightProperty().asObject());
        colSupportWeight.setCellValueFactory((param) -> param.getValue().getObject().supportWeightProperty().asObject());
        colPrice.setCellValueFactory((param) -> param.getValue().getObject().soldPriceProperty().asObject());
        colCosts.setCellValueFactory((param) -> param.getValue().getObject().costsProperty().asObject());

        colId.setCellValueFactory((param) -> param.getValue().idProperty().asObject());

        //Centering content
        PrintedAPI.centerColumns(tvSelectedObjects.getColumns());
        tvSelectedObjects.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void calculateStats(){
        double price = 0, weight = 0, supportWeight = 0, costs = 0;
        int buildTime = 0, quantity = 0;

        ObservableList<OrderItem> orderItems = tvSelectedObjects.getItems();

        for (OrderItem item : orderItems) {
            price += item.getObject().getSoldPrice();
            costs += item.getObject().getCosts();
            weight += item.getObject().getWeight();
            supportWeight += item.getObject().getSupportWeight();
            buildTime += item.getObject().getBuildTime();
            quantity += item.getObject().getSoldCount();
        }

        labelWeight.setText(weight + " g");
        labelSupportWeight.setText(supportWeight + " g");
        labelWeightSum.setText((weight + supportWeight) + " g");
        labelQuantity.setText(quantity + "");
        labelBuildTime.setText(PrintedAPI.formatTime(buildTime).get());
        labelPrice.setText(price + " $");
        labelCosts.setText(costs + " $");
        labelProfit.setText((price - costs) + " $");
    }

    public boolean createOrder(){
        try {
            //table columns
            SimpleIntegerProperty id, buildTime, quantity;
            SimpleStringProperty status, comment, dateCreated, dueDate;
            SimpleDoubleProperty costs, price, weight, supportWeight;

            ObservableList<OrderItem> orderItems = tvSelectedObjects.getItems();

            if (PrintedAPI.areTxtFieldsEmpty(txtFieldCustomer)){
                labelInfo.setText("Customer field cannot be empty. Please, click on Select button and choose one.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            if (orderItems.isEmpty()){
                labelInfo.setText("Please add some object to the list.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            double priceDouble = 0, weightDouble = 0, supportWeightDouble = 0, costsDouble = 0;
            int buildTimeInt = 0, quantityInt = 0;

            for (OrderItem item : orderItems) {
                priceDouble += item.getObject().getSoldPrice();
                costsDouble += item.getObject().getCosts();
                weightDouble += item.getObject().getWeight();
                supportWeightDouble += item.getObject().getSupportWeight();
                buildTimeInt += item.getObject().getBuildTime();
                quantityInt += item.getObject().getSoldCount();
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            id = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));

            buildTime = new SimpleIntegerProperty(buildTimeInt);
            quantity = new SimpleIntegerProperty(quantityInt);
            RadioButton soldStatus = (RadioButton)toggleGroupStatus.getSelectedToggle();
            status = new SimpleStringProperty(soldStatus.getText());
            comment = new SimpleStringProperty(txtFieldComment.getText());
            dateCreated = new SimpleStringProperty(datePickerDateCreated.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            dueDate = new SimpleStringProperty(datePickerDueDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            price = new SimpleDoubleProperty(priceDouble);
            costs = new SimpleDoubleProperty(costsDouble);
            weight = new SimpleDoubleProperty(weightDouble);
            supportWeight = new SimpleDoubleProperty(supportWeightDouble);

            newOrder = new Order(id, customer, buildTime, quantity, status, comment, dateCreated, dueDate, costs, price, weight, supportWeight, orderItems);

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

        int id = PrintedAPI.getCurrentAutoIncrementValue(ds, "Orders");
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText("New Order");

        datePickerDateCreated.setValue(LocalDate.now());
        datePickerDueDate.setValue(LocalDate.now());

        txtFieldPricePerHour.setText("3.5");

        listOfNotSpentMaterials = controllerMain.getListOfNotSpentMaterials();
        listOfPrinters = controllerMain.getListOfPrinters();
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

    public void setObjects(ObservableList<Object> selectedObjects) {

        ObservableList<OrderItem> items = FXCollections.observableArrayList();

        for (Object obj : selectedObjects){

            SimpleIntegerProperty id, orderId;
            Material material;
            Printer printer;

            obj.setBuildTimeFormatted(PrintedAPI.formatTime(obj.getBuildTime()).get());

            id = obj.idProperty();
            orderId = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));

            material = listOfNotSpentMaterials.get(0);
            printer = listOfPrinters.get(0);

            OrderItem item = new OrderItem(id, orderId, obj, material, printer);
            items.add(item);

        }

        tvSelectedObjects.setItems(items);
    }
}
