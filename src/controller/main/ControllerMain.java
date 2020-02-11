package controller.main;

import classes.*;
import classes.Object;
import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/*

Naming convention:
        1) Methods - <Which Tab><What it does> ### Example: costsRefresh(); ordersDownloadTable(), materialsUpdateColor(), materialsUpdatePrice();
        2) Variables - <Which Tab><Type of variable><Name of variable> ### Example: costsBtnCreate, printersLabelTotalPrice, ordersTableView...
        3) Services - service<What it does> ### Example: serviceDownloadCosts, serviceRefreshOrders

Content:
    GENERAL VARIABLES
    COSTS VARIABLES
    PRINTERS VARIABLES
    MATERIALS VARIABLES
    OBJECTS VARIABLES
    CUSTOMERS VARIABLES
    ORDERS VARIABLES
    ORDER ITEMS VARIABLES
    GENERAL METHODS
    COSTS METHODS
    PRINTERS METHODS
    MATERIALS METHODS
    OBJECTS METHODS
    CUSTOMERS METHODS
    ORDERS METHODS
    ORDER ITEMS METHODS
    INITIALIZE GENERAL
    INITIALIZE COSTS TAB
    INITIALIZE PRINTERS TAB
    INITIALIZE MATERIALS TAB
    INITIALIZE OBJECTS TAB
    INITIALIZE CUSTOMERS TAB
    INITIALIZE ORDERS TAB
    GETTERS & SETTERS

 */


public class ControllerMain implements Initializable {

    /*****************************          GENERAL VARIABLES       *****************************/
    private String userTitle = "Lord";

    private HikariDataSource ds;
    private Service serviceDownloadAllTables;

    private ObservableList<SimpleTableObject> commonCustomerProperties;
    private ObservableList<SimpleTableObject> commonMaterialProperties;
    private ObservableList<SimpleTableObject> commonCustomerPropertyTypes;
    private ObservableList<SimpleTableObject> commonMaterialPropertyTypes;
    private ObservableList<SimpleTableObject> commonOrderStatus;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label labelMainInfo;

    //setCellValueFactory for or columns in costs table view
    public void initializeCols(){
        //COSTS COLUMNS*********************************************************
        costsColComment.setCellValueFactory(param -> param.getValue().commentProperty());
        costsColName.setCellValueFactory((param) -> param.getValue().nameProperty());
        costsColPurchaseDate.setCellValueFactory((param) -> param.getValue().purchaseDateProperty());
        costsColPrinterName.setCellValueFactory((param) -> param.getValue().printerNameProperty());

        costsColId.setCellValueFactory((param) -> param.getValue().idProperty().asObject());
        costsColQuantity.setCellValueFactory((param) -> param.getValue().quantityProperty().asObject());
        costsColPrinterID.setCellValueFactory((param) -> param.getValue().printerIdProperty().asObject());

        costsColPrice.setCellValueFactory((param) -> param.getValue().priceProperty().asObject());
        costsColShipping.setCellValueFactory((param) -> param.getValue().shippingProperty().asObject());

        //Centering content
        PrintedAPI.centerColumns(costsTv.getColumns());
        costsTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //PRINTERS COLUMNS*********************************************************
        printersColName.setCellValueFactory((param) -> {return param.getValue().nameProperty();});
        printersColPurchaseDate.setCellValueFactory((param) -> {return param.getValue().purchaseDateProperty();});
        printersColType.setCellValueFactory((param) -> {return param.getValue().typeProperty();});
        printersColComment.setCellValueFactory((param) -> {return param.getValue().commentProperty();});

        printersColId.setCellValueFactory((param) -> {return param.getValue().idProperty().asObject();});
        printersColItemsSold.setCellValueFactory((param) -> {return param.getValue().itemsSoldProperty().asObject();});

        printersColPrice.setCellValueFactory((param) -> {return param.getValue().priceProperty().asObject();});
        printersColShipping.setCellValueFactory((param) -> {return param.getValue().shippingProperty().asObject();});
        printersColIncomes.setCellValueFactory((param) -> {return param.getValue().incomesProperty().asObject();});
        printersColExpenses.setCellValueFactory((param) -> {return param.getValue().expensesProperty().asObject();});
        printersColOverallIncome.setCellValueFactory((param) -> {return param.getValue().overallIncomeProperty().asObject();});
        printersColDuty.setCellValueFactory((param) -> {return param.getValue().dutyProperty().asObject();});
        printersColTax.setCellValueFactory((param) -> {return param.getValue().taxProperty().asObject();});

        //Centering content
        PrintedAPI.centerColumns(printersTv.getColumns());
        printersTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //MATERIALS COLUMNS*********************************************************
        matColColor.setCellValueFactory((param) -> {return param.getValue().colorProperty();});
        matColSeller.setCellValueFactory((param) -> {return param.getValue().sellerProperty();});
        matColFinished.setCellValueFactory((param) -> {return param.getValue().finishedProperty();});
        matColManufacturer.setCellValueFactory((param) -> {return param.getValue().manufacturerProperty();});
        matColPurchDate.setCellValueFactory((param) -> {return param.getValue().purchaseDateProperty();});
        matColType.setCellValueFactory((param) -> {return param.getValue().typeProperty();});
        matColComment.setCellValueFactory((param) -> {return param.getValue().commentProperty();});

        matColId.setCellValueFactory((param) -> {return param.getValue().idProperty().asObject();});
        matColWeight.setCellValueFactory((param) -> {return param.getValue().weightProperty().asObject();});

        matColRemaining.setCellValueFactory((param) -> {return param.getValue().remainingProperty().asObject();});
        matColDiameter.setCellValueFactory((param) -> {return param.getValue().diameterProperty().asObject();});
        matColPrice.setCellValueFactory((param) -> {return param.getValue().priceProperty().asObject();});
        matColShipping.setCellValueFactory((param) -> {return param.getValue().shippingProperty().asObject();});
        matColProfit.setCellValueFactory((param) -> {return param.getValue().profitProperty().asObject();});
        matColSoldFor.setCellValueFactory((param) -> {return param.getValue().soldForProperty().asObject();});
        matColTrash.setCellValueFactory((param) -> {return param.getValue().trashProperty().asObject();});
        matColUsed.setCellValueFactory((param) -> {return param.getValue().usedProperty().asObject();});

        //Centering content
        PrintedAPI.centerColumns(matTv.getColumns());
        matTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //OBJECTS COLUMNS*********************************************************
        objColName.setCellValueFactory((param) -> {return param.getValue().nameProperty();});
        objColStlLink.setCellValueFactory((param) -> {return param.getValue().stlLinkProperty();});
        objColBuildTimeFormatted.setCellValueFactory((param) -> {return param.getValue().buildTimeFormattedProperty();});
        objColComment.setCellValueFactory((param) -> {return param.getValue().commentProperty();});

        objColId.setCellValueFactory((param) -> {return param.getValue().idProperty().asObject();});
        objColCount.setCellValueFactory((param) -> {return param.getValue().soldCountProperty().asObject();});

        objColWeight.setCellValueFactory((param) -> {return param.getValue().weightProperty().asObject();});
        objColSupportWeight.setCellValueFactory((param) -> {return param.getValue().supportWeightProperty().asObject();});
        objColSoldPrice.setCellValueFactory((param) -> {return param.getValue().soldPriceProperty().asObject();});
        objColCosts.setCellValueFactory((param) -> {return param.getValue().costsProperty().asObject();});

        //Centering content
        PrintedAPI.centerColumns(objTv.getColumns());
        objTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //CUSTOMERS COLUMNS*********************************************************
        custColLastName.setCellValueFactory((param) -> {return param.getValue().lastNameProperty();});
        custColFirstName.setCellValueFactory((param) -> {return param.getValue().firstNameProperty();});
        custColDateCreated.setCellValueFactory((param) -> {return param.getValue().dateCreatedProperty();});
        custColMail.setCellValueFactory((param) -> {return param.getValue().mailProperty();});
        custColPhone.setCellValueFactory((param) -> {return param.getValue().phoneProperty();});
        custColAddress.setCellValueFactory((param) -> {return param.getValue().addressProperty();});
        custColCity.setCellValueFactory((param) -> {return param.getValue().cityProperty();});
        custColZipCode.setCellValueFactory((param) -> {return param.getValue().zipCodeProperty();});
        custColCountry.setCellValueFactory((param) -> {return param.getValue().countryProperty();});
        custColCompany.setCellValueFactory((param) -> {return param.getValue().companyProperty();});
        custColComment.setCellValueFactory((param) -> {return param.getValue().commentProperty();});

        custColId.setCellValueFactory((param) -> {return param.getValue().idProperty().asObject();});
        custColOrderCount.setCellValueFactory((param) -> {return param.getValue().orderCountProperty().asObject();});

        custColOrdersPrice.setCellValueFactory((param) -> {return param.getValue().ordersPriceProperty().asObject();});

        //Centering content
        PrintedAPI.centerColumns(custTv.getColumns());
        custTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //ORDERS COLUMNS*********************************************************
        ordersColOrderId.setCellValueFactory((param) -> {return param.getValue().idProperty().asObject();});
        ordersColCustomerID.setCellValueFactory((param) -> {return param.getValue().customerIdProperty().asObject();});
        ordersColCustomer.setCellValueFactory((param) -> {return param.getValue().customerNameProperty();});
        ordersColTotalQuantity.setCellValueFactory((param) -> {return param.getValue().quantityProperty().asObject();});
        ordersColTotalPrice.setCellValueFactory((param) -> {return param.getValue().priceProperty().asObject();});
        ordersColTotalCosts.setCellValueFactory((param) -> {return param.getValue().costsProperty().asObject();});
        ordersColTotalWeight.setCellValueFactory((param) -> {return param.getValue().weightProperty().asObject();});
        ordersColTotalSupportWeight.setCellValueFactory((param) -> {return param.getValue().supportWeightProperty().asObject();});
        ordersColTotalBuildTimeFormatted.setCellValueFactory((param) -> {return param.getValue().buildTimeFormattedProperty();});
        ordersColDateCreated.setCellValueFactory((param) -> {return param.getValue().dateCreatedProperty();});
        ordersColDueDate.setCellValueFactory((param) -> {return param.getValue().dueDateProperty();});
        ordersColStatus.setCellValueFactory((param) -> {return param.getValue().statusProperty();});
        ordersColComment.setCellValueFactory((param) -> {return param.getValue().commentProperty();});

        PrintedAPI.centerColumns(ordersTv.getColumns());
        ordersTv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }



    /*****************************          COSTS VARIABLES       *****************************/
    private ObservableList<Cost> listOfCosts = FXCollections.observableArrayList();

    @FXML
    private Tab costsTab;

    @FXML
    private TableView<Cost> costsTv;

    @FXML
    private Button costsBtnNew;

    @FXML
    private Label costsLabelSelected;

    @FXML
    private TableColumn<Cost, Integer> costsColId, costsColQuantity, costsColPrinterID;

    @FXML
    private TableColumn<Cost, String> costsColName, costsColPurchaseDate, costsColComment, costsColPrinterName;

    @FXML
    private TableColumn<Cost, Double> costsColPrice, costsColShipping;

    /*****************************          PRINTERS VARIABLES       *****************************/
    private ObservableList<Printer> listOfPrinters = FXCollections.observableArrayList();

    @FXML
    private Tab printersTab;

    @FXML
    private TableView<Printer> printersTv;

    @FXML
    private Button printersBtnNew;

    @FXML
    private Label printersLabelSelectedTotal;

    @FXML
    private TableColumn<Printer, Integer> printersColId, printersColItemsSold;

    @FXML
    private TableColumn<Printer, String> printersColName, printersColPurchaseDate, printersColComment, printersColType;

    @FXML
    private TableColumn<Printer, Double> printersColPrice, printersColShipping, printersColDuty, printersColTax, printersColIncomes, printersColExpenses, printersColOverallIncome;

    /*****************************          MATERIALS VARIABLES       *****************************/
    private ObservableList<Material> listOfMaterials = FXCollections.observableArrayList();

    @FXML
    private Tab materialsTab;

    @FXML
    private TableView<Material> matTv;

    @FXML
    private Button matBtnNew;

    @FXML
    private Label matLabelSelectedTotal;

    @FXML
    private TableColumn<Material, String> matColColor, matColManufacturer, matColType, matColFinished, matColSeller, matColPurchDate, matColComment;

    @FXML
    private TableColumn<Material, Integer> matColId;

    @FXML
    private TableColumn<Material, Double> matColRemaining, matColWeight, matColPrice, matColShipping, matColUsed, matColTrash, matColSoldFor, matColProfit, matColDiameter;

    /*****************************          OBJECTS VARIABLES       *****************************/
    private ObservableList<Object> listOfObjects = FXCollections.observableArrayList();

    @FXML
    private Tab objTab;

    @FXML
    private TableView<Object> objTv;

    @FXML
    private Button objBtnNew;

    @FXML
    private Label objLabelSelectedTotal;

    @FXML
    private TableColumn<Object, String> objColName, objColStlLink, objColBuildTimeFormatted, objColComment;

    @FXML
    private TableColumn<Object, Integer> objColId, objColCount;

    @FXML
    private TableColumn<classes.Object, Double> objColWeight, objColSupportWeight, objColSoldPrice, objColCosts;

    /*****************************          CUSTOMERS VARIABLES       *****************************/
    private ObservableList<Customer> listOfCustomers = FXCollections.observableArrayList();

    @FXML
    private Tab custTab;

    @FXML
    private TableView<Customer> custTv;

    @FXML
    private Button custBtnNew;

    @FXML
    private Label labelCustSelectedTotal;

    @FXML
    private TableColumn<Customer, String> custColLastName, custColFirstName, custColDateCreated, custColMail, custColPhone, custColAddress, custColCity, custColZipCode, custColCountry, custColCompany, custColComment;

    @FXML
    private TableColumn<Customer, Integer> custColId, custColOrderCount;

    @FXML
    private TableColumn<Customer, Double> custColOrdersPrice;

    /*****************************          ORDERS VARIABLES       *****************************/
    private ObservableList<Order> listOfOrders = FXCollections.observableArrayList();

    @FXML
    private Tab ordersTab;

    @FXML
    private TableView<Order> ordersTv;

    @FXML
    private Button ordersBtnNew;

    @FXML
    private Label ordersLabelSelected, ordersLabelSoldOrders, ordersLabelSoldOrdersProfit, ordersLabelSoldOrdersCosts;

    @FXML
    private TableColumn<Order, String> ordersColCustomer, ordersColStatus, ordersColComment, ordersColDateCreated, ordersColDueDate, ordersColTotalBuildTimeFormatted;

    @FXML
    private TableColumn<Order, Integer> ordersColCustomerID, ordersColOrderId, ordersColTotalQuantity;

    @FXML
    private TableColumn<Order, Double> ordersColTotalPrice, ordersColTotalCosts, ordersColTotalWeight, ordersColTotalSupportWeight;

    /*****************************          ORDER ITEMS VARIABLES       *****************************/
    private ObservableList<OrderItem> listOfOrderItems = FXCollections.observableArrayList();


    /*****************************          GENERAL METHODS       *****************************/


    /*****************************          COSTS METHODS         *****************************/
    //use this to display any list of costs - for example filtered or result of search
    public void displayCosts(ObservableList<Cost> costs) {
        costsTv.setItems(costs);
    }

    //display current list of costs saved in global variable.
    public void displayCosts() {
        completeListOfCosts();
        costsTv.setItems(listOfCosts);
    }

    //Since data in database table are not the same as in table views, we have to complete these data.
    //Regarding costs  table view it is only printer name.
    //This method uses table of printers and id of printer stored in listOfCosts to get and set printer name for particular Cost object
    private void completeListOfCosts(){
        for (Cost cost : listOfCosts) {
            cost.setPrinterName(Printer.getName(listOfPrinters, cost.getPrinterId()));
        }
    }


    /*****************************          PRINTERS METHODS         *****************************/
    //use this to display any list of costs - for example filtered or result of search
    public void displayPrinters(ObservableList<Printer> printers) {
        completeListOfPrinters();
        printersTv.setItems(printers);
    }

    //display current list of costs saved in global variable.
    public void displayPrinters() {
        printersTv.setItems(listOfPrinters);
    }

    //the same as completeListOfCosts() except this one is for printers
    //fields to be completed: ItemsSold, Type, Incomes, Expenses, Overall Incomes
    private void completeListOfPrinters(){
        //We can get number of sold items and incomes by searching listOfOrderItems.
        //We can get expenses by searching listOfCosts
        //We can get overall Incomes by subtracting costs from incomes
        for (Printer printer : listOfPrinters) {

            int printerId = printer.getId();
            double expenses = 0, incomes = 0, overallIncome;

            //lets loop listOfOrderItems and add sold items and incomes to current printer based on printerId
            for (OrderItem orderItem : listOfOrderItems) {
                if (printerId == orderItem.getPrinter().getId()) {
                    printer.setItemsSold(printer.getItemsSold() + orderItem.getObject().getSoldCount());
                    incomes = printer.getIncomes() + orderItem.getObject().getSoldPrice();
                    printer.setIncomes(PrintedAPI.round(incomes, 2));
                }
            }

            //lets loop listOfCosts and add expenses to the current printer based on printerId
            for (Cost cost : listOfCosts) {
                if (printerId == cost.getPrinterId()) {
                    expenses = printer.getExpenses() + cost.getShipping() + cost.getPrice();
                    printer.setExpenses(PrintedAPI.round(expenses, 2));
                }

            }

            overallIncome = incomes - expenses;
            printer.setOverallIncome(PrintedAPI.round(overallIncome, 2));

        }
    }


    /*****************************          MATERIALS METHODS         *****************************/
    //display current list of costs saved in global variable.
    public void displayMaterials() {
        completeListOfMaterials();
        matTv.setItems(listOfMaterials);
    }

    //use this to display any list of costs - for example filtered or result of search
    public void displayMaterials(ObservableList<Material> materials) {
        matTv.setItems(materials);
    }


    //the same as completeListOfCosts() except this one is for materials
    //fields to be completed: manufacturer, Type, color, weight, diameter, usedPercentage, remaining, trash, soldFor, profit, seller
    private void completeListOfMaterials(){
        for (Material material : listOfMaterials) {

            int matId = material.getId();

            //these can be found in commonMaterialProperties table
            String type, color, manufacturer, seller;
            double weight, diameter;

            //we have to calculate these using data in other OrderItems table
            double used = 0, soldFor = 0;

            type = SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, material.getTypeId()).getPropertyName();
            color = SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, material.getColorId()).getPropertyName();
            manufacturer = SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, material.getManufacturerId()).getPropertyName();
            seller = SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, material.getSellerId()).getPropertyName();

            weight = Double.parseDouble(SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, material.getWeightId()).getPropertyName());
            diameter = Double.parseDouble(SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, material.getDiameterId()).getPropertyName());

            material.setType(type);
            material.setColor(color);
            material.setManufacturer(manufacturer);
            material.setSeller(seller);

            material.setWeight(PrintedAPI.round(weight, 2));
            material.setDiameter(PrintedAPI.round(diameter, 2));

            //setting used, remaining, trash, soldFor and profit
            for (OrderItem orderItem : listOfOrderItems) {
                if(orderItem.getMaterial().getId() == matId){

                    orderItem.setMaterial(material);

                    used += orderItem.getObject().getWeight() + orderItem.getObject().getSupportWeight();
                    soldFor += orderItem.getObject().getSoldPrice();

                }
            }

            material.setUsed(PrintedAPI.round(used/weight*100, 2));
            material.setRemaining(PrintedAPI.round(weight-used, 2));
            material.setSoldFor(PrintedAPI.round(soldFor, 2));
            material.setProfit(PrintedAPI.round(soldFor - material.getPrice(), 2));
        }
    }

    /*****************************          OBJECTS METHODS         *****************************/
    //use this to display any list of costs - for example filtered or result of search
    public void displayObjects(ObservableList<Object> objects)  {
        objTv.setItems(objects);
    }

    //display current list of costs saved in global variable.
    public void displayObjects() {
        completeListOfObjects();
        objTv.setItems(listOfObjects);
    }
    //the same as completeListOfCosts() except this one is for materials
    //fields to be completed:  soldCount, soldPrice, costs;
    public void completeListOfObjects(){
        for (Object object : listOfObjects) {

            int objectId = object.getId();
            int soldCount  = 0;
            double soldPrice = 0, costs = 0;

            //We can find all required fields in listOfOrderItems.
            for (OrderItem item : listOfOrderItems) {
                Object obj = item.getObject();

                if (objectId == obj.getId()) {
                    soldCount += obj.getSoldCount();
                    soldPrice += obj.getSoldPrice();
                    costs += obj.getCosts();
                }
            }

            object.setSoldCount(soldCount);
            object.setSoldPrice(soldPrice);
            object.setCosts(costs);
        }
    }

    /*****************************          CUSTOMERS METHODS         *****************************/
    //use this to display any list of costs - for example filtered or result of search
    public void displayCustomers(ObservableList<Customer> customers) {
        custTv.setItems(customers);
    }

    //display current list of costs saved in global variable.
    public void displayCustomers() {
        completeListOfCustomers();
        custTv.setItems(listOfCustomers);
    }

    //the same as completeListOfCosts() except this one is for customers
    //fields to be completed: country, company, orderCount, ordersPrice
    public void completeListOfCustomers(){
        for (Customer customer : listOfCustomers){

            int custId = customer.getId(), countryId = customer.getCountryId(), companyId = customer.getCompanyId();

            //We will find country and company in commonCustomersProperties. It should match countryId.
            //for orderCount and orderPrice we must count all orders with particular custId
            //for ordersPrice we must sum prices of all orders with particular custId
            String country = SimpleTableObject.getSimpleTableObjectByPropId(commonCustomerProperties, countryId).getPropertyName();
            String company = SimpleTableObject.getSimpleTableObjectByPropId(commonCustomerProperties, companyId).getPropertyName();
            int ordersCount = 0;
            double ordersPrice = 0;

            for (Order order : listOfOrders) {
                if(custId == order.getCustomerId()) {
                    ordersCount++;
                    ordersPrice += order.getPrice();
                }
            }

            customer.setCountry(country);
            customer.setCompany(company);
            customer.setOrderCount(ordersCount);
            customer.setOrdersPrice(ordersPrice);
        }
    }


    /*****************************          ORDERS METHODS         *****************************/
    //use this to display any list of costs - for example filtered or result of search
    public void displayOrders(ObservableList<Order> orders) {

        ordersTv.setItems(orders);
    }

    //display current list of costs saved in global variable.
    public void displayOrders() {
        completeListOfOrders();
        ordersTv.setItems(listOfOrders);
    }

    //the same as completeListOfCosts() except this one is for customers
    //fields to be completed: customer Name
    public void completeListOfOrders(){
        String custName = "Dunno Who";

        for (Order order : listOfOrders) {
            int custId = order.getCustomerId();

            for(Customer cust : listOfCustomers) {
                if(cust.getId() == custId) {
                    custName = cust.getLastName() + " " + cust.getFirstName();
                    break;
                }
            }
            order.setCustomerName(custName);
        }
    }

    /*****************************          ORDER ITEMS METHODS         *****************************/



    @Override
    public void initialize(URL url, ResourceBundle rb) {
    /*****************************          INITIALIZE GENERAL          *****************************/
        initializeCols();

        serviceDownloadAllTables = new Service() {
            @Override
            protected Task<ObservableList<Cost>> createTask() {
                return new Task<ObservableList<Cost>>() {
                    @Override
                    protected ObservableList<Cost> call() throws Exception {

                        int i = -1, max = 12;

                        updateProgress(i, 6);
                        updateMessage("Downloading has just started, my " + userTitle);

                        updateMessage("Downloading Costs table, my " + userTitle);
                        listOfCosts = Cost.downloadCostsTable(ds);
                        updateProgress(i+2, max);
                        updateMessage("Done downloading Costs table, my " + userTitle);

                        updateMessage("Downloading Printers table, my " + userTitle);
                        listOfPrinters = Printer.downloadPrintersTable(ds);
                        updateProgress(i++, max);
                        updateMessage("Done downloading Printers table, my " + userTitle);

                        updateMessage("Downloading Materials table, my " + userTitle);
                        listOfMaterials = Material.downloadMaterialsTable(ds);
                        updateProgress(i++, max);
                        updateMessage("Done downloading Materials table, my " + userTitle);

                        updateMessage("Downloading Objects table, my " + userTitle);
                        listOfObjects = Object.downloadObjectsTable(ds);
                        updateProgress(i++, max);
                        updateMessage("Done downloading Objects table, my " + userTitle);

                        updateMessage("Downloading Customers table, my " + userTitle);
                        listOfCustomers = Customer.downloadCustomersTable(ds);
                        updateProgress(i++, max);
                        updateMessage("Done downloading Customers table, my " + userTitle);

                        updateMessage("Downloading Orders table, my " + userTitle);
                        listOfOrders = Order.downloadOrdersTable(ds);
                        updateProgress(i++, max);
                        updateMessage("Done downloading Orders table, my " + userTitle);

                        updateMessage("Downloading Order Items table, my " + userTitle);
                        listOfOrderItems = OrderItem.downloadOrderItemsTable(ds);
                        updateProgress(i++, max);
                        updateMessage("Done downloading Order Items table, my " + userTitle);

                        updateMessage("Downloading additional tables, my " + userTitle);
                        commonCustomerProperties = SimpleTableObject.downloadCommonCustomerProperties(ds);
                        updateProgress(i++, max);
                        commonMaterialProperties = SimpleTableObject.downloadCommonMaterialProperties(ds);
                        updateProgress(i++, max);
                        commonCustomerPropertyTypes = SimpleTableObject.downloadCustomerPropertyTypes(ds);
                        updateProgress(i++, max);
                        commonMaterialPropertyTypes = SimpleTableObject.downloadMaterialPropertyTypes(ds);
                        updateProgress(i++, max);
                        commonOrderStatus = SimpleTableObject.downloadOrderStatusTable();
                        updateProgress(i++, max);
                        updateMessage("Done downloading additional tables, my " + userTitle);


                        updateMessage("All tables were successfully downloaded, my " + userTitle);
                        updateMessage("Now calculating content, my " + userTitle);

                        displayCosts();
                        displayPrinters();
                        displayMaterials();
                        displayObjects();
                        displayCustomers();
                        displayOrders();
                        updateMessage("All done.");
                        updateProgress(max, max);

                        progressBar.setVisible(false);
                        //PrintedAPI.serviceStart(serviceDisplayAllTables);

                        return null;
                    }
                };
            }
        };
        progressBar.progressProperty().bind(serviceDownloadAllTables.progressProperty());
        labelMainInfo.textProperty().bind(serviceDownloadAllTables.messageProperty());


    /*****************************          INITIALIZE COSTS TAB          *****************************/
    costsBtnNew.setOnAction((event) -> {
        displayCosts();
//        System.out.println("Costs: " + listOfCosts.size());
//        System.out.println("Printers: " + listOfPrinters.size());
//        System.out.println("Materials: " + listOfMaterials.size());
//        System.out.println("Objects: " + listOfObjects.size());
//        System.out.println("Customers: " + listOfCustomers.size());
//        System.out.println("Orders: " + listOfOrders.size());
//        System.out.println("OrderItems: " + listOfOrderItems.size());

    });
    /*****************************          INITIALIZE PRINTERS TAB          *****************************/

    /*****************************          INITIALIZE MATERIALS TAB          *****************************/

    /*****************************          INITIALIZE OBJECTS TAB          *****************************/


    /*****************************          INITIALIZE CUSTOMERS TAB          *****************************/


    /*****************************          INITIALIZE ORDERS TAB          *****************************/


    }//end of initialize();
    /*************************          GETTERS & SETTERS          *************************/

    public void setDataSource(HikariDataSource ds) {
        this.ds = ds;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public Service getServiceDownloadAllTables() {
        return serviceDownloadAllTables;
    }
}
