package controller.main;

import classes.*;
import classes.Object;
import com.zaxxer.hikari.HikariDataSource;
import controller.create.*;
import controller.create.order.ControllerCreateOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.omg.CORBA.MARSHAL;

import java.io.IOException;
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

    /*****************************          COSTS VARIABLES       *****************************/
    private ObservableList<Cost> listOfCosts = FXCollections.observableArrayList();

    @FXML
    private Tab costsTab;

    @FXML
    private TableView<Cost> costsTv;

    @FXML
    private Button costsBtnCreate, costsBtnDelete, costsBtnDetails, costsBtnEdit, costsBtnRefresh;

    @FXML
    private Label costsLabelSelected, costsLabelTotal, costsLabelSelectedQuantity, costsLabelSelectedShippingPriceTotal, costsLabelTotalQuantity, costsLabelTotalShippingPriceTotal;

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
    private Button printersBtnCreate, printersBtnDelete, printersBtnDetails, printersBtnEdit, printersBtnRefresh;

    @FXML
    private Label printersLabelSelectedTotal, printersLabelTotal, printersLabelTotalItemsPrinted, printersLabelTotalIncomesProfit, printersLabelSelected, printersLabelSelectedPriceInclExpenses,
            printersLabelSelectedExpenses, printersLabelSelectedPriceOfPrinters, printersLabelSelectedDutyTaxTotal, printersLabelSelectedShippingPriceTotal, printersLabelTotalExpenses,
            printersLabelTotalPriceOfPrinters, printersLabelTotalDutyTaxTotal, printersLabelTotalShippingPriceTotal, printersLabelSelectedItemsPrinted, printersLabelSelectedIncomesProfit,
            printersLabelTotalPriceInclExpenses;

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
    private Button matBtnCreate, matBtnDelete, matBtnDetails, matBtnEdit, matBtnRefresh;

    @FXML
    private Label matLabelSelectedTotal, matLabelTotal, matLabelTotalColorsTypes, matLabelTotalTrash, matLabelTotalAvgRollBuySellPrice, matLabelTotalRemainingWeightRolls, matLabelSelected,
            matLabelSelectedRemainingWeightRolls, matLabelSelectedSoldWeightRolls, matLabelSelectedWeightRolls, matLabelSelectedRevenueProfit, matLabelSelectedShippingPriceTotal,
            matLabelTotalSoldWeightRolls, matLabelTotalWeightRolls, matLabelTotalRevenueProfit, matLabelTotalShippingPriceTotal, matLabelSelectedTrash, matLabelSelectedAvgRollBuySellPrice;

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
    private Button objBtnCreate, objBtnDelete, objBtnDetails, objBtnEdit, objBtnRefresh;

    @FXML
    private Label objLabelSelectedTotal, objLabelTotal, objLabelSelected, objLabelSelectedWeights, objLabelSelectedPriceCostsProfit, objLabelSelectedTimesPrinted, objLabelSelectedBuildTime, objLabelSelectedPerHour;

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
    private Button custBtnCreate, custBtnDelete, custBtnDetails, custBtnEdit, custBtnRefresh;

    @FXML
    private Label custLabelTotal, custLabelSelected, custLabelSelectedCosts, custLabelSelectedPrice, custLabelSelectedItemsPrinted, custLabelSelectedOrders, custLabelSelectedWeights,
            custLabelSelectedPerHour, custLabelSelectedBuildTime;

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
    private Button ordersBtnCreate, ordersBtnMarkNotSold, ordersBtnCancel, ordersBtnMarkSold, ordersBtnCreateFrom, ordersBtnRefresh, ordersBtnEdit, ordersBtnDelete;

    @FXML
    private Label ordersLabelSold, ordersLabelSoldPriceCostsProfit, ordersLabelNotSold,
            ordersLabelNotSoldPriceCostsProfit, ordersLabelTotal, ordersLabelTotalItemsPrinted, ordersLabelTotalBuildTime, ordersLabelTotalWeightSupports,
            ordersLabelTotalPerHour, ordersLabelTotalPriceCostsProfit, ordersLabelSelected, ordersLabelSelectedBuildTime, ordersLabelSelectedItemsPrinted,
            ordersLabelSelectedWeightSupports, ordersLabelSelectedPerHour, ordersLabelSelectedPriceCostsProfit;

    @FXML
    private TableColumn<Order, String> ordersColCustomer, ordersColStatus, ordersColComment, ordersColDateCreated, ordersColDueDate, ordersColTotalBuildTimeFormatted;

    @FXML
    private TableColumn<Order, Integer> ordersColCustomerID, ordersColOrderId, ordersColTotalQuantity;

    @FXML
    private TableColumn<Order, Double> ordersColTotalPrice, ordersColTotalCosts, ordersColTotalWeight, ordersColTotalSupportWeight;

    /*****************************          ORDER ITEMS VARIABLES       *****************************/
    private ObservableList<OrderItem> listOfOrderItems = FXCollections.observableArrayList();


    /*****************************          GENERAL METHODS       *****************************/

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
    //We can set here labels for total costs only, not for selected, because we are looping all entries.
    //And because we cannot change UI elements from other thread tha man javaFX thread, we will do it in Platform.runLater() method.
    private void completeListOfCosts(){

        int total = listOfCosts.size(), totalQuantity = 0;
        double shipping = 0, price = 0;

        for (Cost cost : listOfCosts) {
            cost.setPrinterName(Printer.getName(listOfPrinters, cost.getPrinterId()));

            totalQuantity += cost.getQuantity();
            shipping += cost.getShipping();
            price += cost.getPrice();

        }

        int finalTotalQuantity = totalQuantity;

        double finalPrice = PrintedAPI.round(price);
        double finalShipping = PrintedAPI.round(shipping);

        Platform.runLater(() -> {
            costsLabelTotal.setText("Total (" + total + ")");
            costsLabelTotalQuantity.setText("" + finalTotalQuantity);
            costsLabelTotalShippingPriceTotal.setText(String.format("%.2f $/%.2f $ (%.2f)", finalShipping, finalPrice, finalShipping + finalPrice));
        });
    }

    private void calculateSelectedCostsStatistics(ObservableList<Cost> selectedCosts){

        int totalQuantity = 0;
        double shipping = 0, price = 0;

        for (Cost cost : selectedCosts) {
            totalQuantity += cost.getQuantity();
            shipping += cost.getShipping();
            price += cost.getPrice();
        }

        int finalTotalQuantity = totalQuantity;

        double finalPrice = PrintedAPI.round(price);
        double finalShipping = PrintedAPI.round(shipping);

        Platform.runLater(() -> {
            costsLabelSelected.setText("Selected (" + selectedCosts.size() + ")");
            costsLabelSelectedQuantity.setText("" + finalTotalQuantity);
            costsLabelSelectedShippingPriceTotal.setText(String.format("%.2f $/%.2f $ (%.2f)", finalShipping, finalPrice, finalShipping + finalPrice));
            });
    }


    /*****************************          PRINTERS METHODS         *****************************/
    //use this to display any list of costs - for example filtered or result of search
    public void displayPrinters(ObservableList<Printer> printers) {
        printersTv.setItems(printers);
    }

    //display current list of costs saved in global variable.
    public void displayPrinters() {
        completeListOfPrinters();
        printersTv.setItems(listOfPrinters);
    }

    //the same as completeListOfCosts() except this one is for printers
    //fields to be completed: ItemsSold, Type, Incomes, Expenses, Overall Incomes
    private void completeListOfPrinters(){
        //We can get number of sold items and incomes by searching listOfOrderItems.
        //We can get expenses by searching listOfCosts
        //We can get overall Incomes by subtracting costs from incomes

        //declaration of variables used for labels
        int itemsPrinted = 0;

        double shipping = 0, price = 0, duty = 0, tax = 0, totalPrice = 0, totalExpenses = 0, priceInclExpenses = 0, printersIncome = 0;

        for (Printer printer : listOfPrinters) {

            int printerId = printer.getId();
            double expenses = 0, incomes = 0, overallIncome;

            //lets loop listOfOrderItems and add sold items and incomes to current printer based on printerId
            for (OrderItem orderItem : listOfOrderItems) {
                if (printerId == orderItem.getPrinter().getId()) {
                    printer.setItemsSold(printer.getItemsSold() + orderItem.getObject().getSoldCount());
                    incomes = printer.getIncomes() + orderItem.getObject().getSoldPrice();
                    printer.setIncomes(PrintedAPI.round(incomes));
                    itemsPrinted += orderItem.getObject().getSoldCount();
                }
            }

            //lets loop listOfCosts and add expenses to the current printer based on printerId
            for (Cost cost : listOfCosts) {
                if (printerId == cost.getPrinterId()) {
                    expenses = printer.getExpenses() + cost.getShipping() + cost.getPrice();
                    printer.setExpenses(PrintedAPI.round(expenses));
                }
            }

            overallIncome = incomes - expenses;
            printer.setOverallIncome(PrintedAPI.round(overallIncome));
            printer.setType(SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, printer.getTypeID()).getPropertyName());

            //calculation for "Total" labels
            shipping += printer.getShipping();
            price += printer.getPrice();
            duty += printer.getDuty();
            tax += printer.getTax();
            totalPrice += printer.getShipping() + printer.getPrice() + printer.getDuty() + printer.getTax();
            totalExpenses += printer.getExpenses();
            printersIncome += printer.getIncomes();
        }

        priceInclExpenses += totalPrice + totalExpenses;

        double finalShipping = PrintedAPI.round(shipping);
        double finalPrice = PrintedAPI.round(price);
        double finalDuty = PrintedAPI.round(duty);
        double finalTax = PrintedAPI.round(tax);
        double finalTotalPrice = PrintedAPI.round(totalPrice);
        double finalTotalExpenses = PrintedAPI.round(totalExpenses);
        double finalPriceInclExpenses = PrintedAPI.round(priceInclExpenses);
        double finalPrintersIncome = PrintedAPI.round(printersIncome);
        int finalItemsPrinted = itemsPrinted;

        Platform.runLater(() -> {
            printersLabelTotal.setText("Total (" + listOfPrinters.size() + ")");
            printersLabelTotalShippingPriceTotal.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalShipping, finalPrice, finalShipping + finalPrice));
            printersLabelTotalDutyTaxTotal.setText(String.format("%.2f $/%.2f $ ( %.2f $)", finalDuty, finalTax, finalDuty + finalTax));
            printersLabelTotalPriceOfPrinters.setText(finalTotalPrice + " $");
            printersLabelTotalExpenses.setText(finalTotalExpenses + " $");
            printersLabelTotalPriceInclExpenses.setText(finalPriceInclExpenses + " $");
            printersLabelTotalIncomesProfit.setText(String.format("%.2f $ (%.2f $)", finalPrintersIncome, finalPrintersIncome - finalPriceInclExpenses));
            printersLabelTotalItemsPrinted.setText(finalItemsPrinted + "");
        });
    }

    private void calculateSelectedPrintersStatistics(ObservableList<Printer> selectedPrinters) {
        //declaration of variables used for labels
        int itemsPrinted = 0;

        double shipping = 0, price = 0, duty = 0, tax = 0, totalPrice = 0, totalExpenses = 0, priceInclExpenses = 0, printersIncome = 0;

        for (Printer printer : selectedPrinters) {

            int printerId = printer.getId();
            double expenses = 0, incomes = 0, overallIncome;

            //lets loop listOfOrderItems and add sold items and incomes to current printer based on printerId
            for (OrderItem orderItem : listOfOrderItems) {
                if (printerId == orderItem.getPrinter().getId()) {
                    incomes = printer.getIncomes() + orderItem.getObject().getSoldPrice();
                    itemsPrinted += orderItem.getObject().getSoldCount();
                }
            }

            //lets loop listOfCosts and add expenses to the current printer based on printerId
            for (Cost cost : listOfCosts) {
                if (printerId == cost.getPrinterId()) {
                    expenses = printer.getExpenses() + cost.getShipping() + cost.getPrice();
                }
            }

            overallIncome = incomes - expenses;
            printer.setOverallIncome(PrintedAPI.round(overallIncome));
            printer.setType(SimpleTableObject.getSimpleTableObjectByPropId(commonMaterialProperties, printer.getTypeID()).getPropertyName());

            //calculation for "Total" labels
            shipping += printer.getShipping();
            price += printer.getPrice();
            duty += printer.getDuty();
            tax += printer.getTax();
            totalPrice += printer.getShipping() + printer.getPrice() + printer.getDuty() + printer.getTax();
            totalExpenses += printer.getExpenses();
            printersIncome += printer.getIncomes();
        }

        priceInclExpenses += totalPrice + totalExpenses;

        double finalShipping = PrintedAPI.round(shipping);
        double finalPrice = PrintedAPI.round(price);
        double finalDuty = PrintedAPI.round(duty);
        double finalTax = PrintedAPI.round(tax);
        double finalTotalPrice = PrintedAPI.round(totalPrice);
        double finalTotalExpenses = PrintedAPI.round(totalExpenses);
        double finalPriceInclExpenses = PrintedAPI.round(priceInclExpenses);
        double finalPrintersIncome = PrintedAPI.round(printersIncome);
        int finalItemsPrinted = itemsPrinted;

        Platform.runLater(() -> {
            printersLabelSelected.setText("Selected (" + selectedPrinters.size() + ")");
            printersLabelSelectedShippingPriceTotal.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalShipping, finalPrice, finalShipping + finalPrice));
            printersLabelSelectedDutyTaxTotal.setText(String.format("%.2f $/%.2f $ ( %.2f $)", finalDuty, finalTax, finalDuty + finalTax));
            printersLabelSelectedPriceOfPrinters.setText(finalTotalPrice + " $");
            printersLabelSelectedExpenses.setText(finalTotalExpenses + " $");
            printersLabelSelectedPriceInclExpenses.setText(finalPriceInclExpenses + " $");
            printersLabelSelectedIncomesProfit.setText(String.format("%.2f $ (%.2f $)", finalPrintersIncome, finalPrintersIncome - finalPriceInclExpenses));
            printersLabelSelectedItemsPrinted.setText(finalItemsPrinted + "");
        });
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

        //declaration of variables used for labels
        int colors = 0, types = 0, soldRolls = 0, remainingRolls = 0;

        double shipping = 0, price = 0, revenue = 0, totalWeight = 0, soldWeight = 0, remainingWeight = 0,
                trash = 0;

        for (Material material : listOfMaterials) {

            int matId = material.getId();

            //these can be found in commonMaterialProperties table
            String type, color, manufacturer, seller;
            double weight, diameter;

            //we have to calculate these using data in other OrderItems table
            double used = 0, remaining, soldFor = 0;

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

            material.setWeight(PrintedAPI.round(weight));
            material.setDiameter(PrintedAPI.round(diameter));

            //setting used, remaining, trash, soldFor and profit
            for (OrderItem orderItem : listOfOrderItems) {
                if(orderItem.getMaterial().getId() == matId){

                    orderItem.setMaterial(material);

                    used += orderItem.getObject().getWeight() + orderItem.getObject().getSupportWeight();
                    soldFor += orderItem.getObject().getSoldPrice();

                }
            }

            remaining = weight - used;

            material.setUsed(PrintedAPI.round(used/weight*100));
            material.setRemaining(PrintedAPI.round(remaining));
            material.setSoldFor(PrintedAPI.round(soldFor));
            material.setProfit(PrintedAPI.round(soldFor - material.getPrice()));

            //setting label values
            shipping += material.getShipping();
            price += material.getPrice();
            trash += material.getTrash();

            revenue += soldFor;
            totalWeight += material.getWeight();

            if (material.isSold())soldRolls++;
            if (!material.isSold())remainingRolls++;

            soldWeight += used;
            remainingWeight += remaining;
        }

        for (SimpleTableObject obj : commonMaterialProperties){
            int id = obj.getPropertyTypeId();

            switch (id) {
                case 1:
                    types++;
                    continue;
                case 2:
                    colors++;
                    continue;
                default:
                    continue;
            }
        }

        double finalShipping = PrintedAPI.round(shipping);
        double finalPrice = PrintedAPI.round(price);
        double finalRevenue = PrintedAPI.round(revenue);
        double finalProfit = PrintedAPI.round(revenue - (finalPrice + finalShipping));
        double finalRemainingWeight = PrintedAPI.round(remainingWeight);
        double finalTotalWeight = PrintedAPI.round(totalWeight);
        double finalSoldWeight = PrintedAPI.round(soldWeight);
        double finalTrash = PrintedAPI.round(trash);

        int finalTotal = listOfMaterials.size();
        int finalRemainingRolls = remainingRolls;
        int finalTotalRolls = finalTotal;
        int finalSoldRolls = soldRolls;
        int finalColors = colors, finalTypes = types;

        Platform.runLater(() -> {
           matLabelTotal.setText("Total(" + finalTotal + ")");
           matLabelTotalShippingPriceTotal.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalShipping, finalPrice, finalShipping + finalPrice));
           matLabelTotalRevenueProfit.setText(finalRevenue + " $/" + finalProfit + " $");
           matLabelTotalWeightRolls.setText(String.format("%.2f kg/%d rolls", finalTotalWeight/1000, finalTotalRolls));
           matLabelTotalSoldWeightRolls.setText(String.format("%.2f kg/%d rolls", finalSoldWeight/1000, finalSoldRolls));
           matLabelTotalRemainingWeightRolls.setText(String.format("%.2f kg/%d rolls", finalRemainingWeight/1000, finalRemainingRolls));
           matLabelTotalAvgRollBuySellPrice.setText(String.format("%.2f $/ %.2f $", finalPrice/finalTotal, finalRevenue/finalTotal));
           matLabelTotalTrash.setText(finalTrash/1000 + " kg");
           matLabelTotalColorsTypes.setText(finalColors + "/" + finalTypes);
        });
    }

    private void calculateSelectedMaterialsStatistics(ObservableList<Material> selectedMaterials){

        //declaration of variables used for labels
        int colors = 0, types = 0, soldRolls = 0, remainingRolls = 0;

        double shipping = 0, price = 0, revenue = 0, totalWeight = 0, soldWeight = 0, remainingWeight = 0, trash = 0;

        for (Material material : selectedMaterials) {

            int matId = material.getId();
            //we have to calculate these using data in other OrderItems table
            double used = 0, remaining, soldFor = 0;
            double weight = material.getWeight();

            //setting used, remaining, trash, soldFor and profit
            for (OrderItem orderItem : listOfOrderItems) {
                if(orderItem.getMaterial().getId() == matId){

                    used += orderItem.getObject().getWeight() + orderItem.getObject().getSupportWeight();
                    soldFor += orderItem.getObject().getSoldPrice();

                }
            }

            remaining = weight - used;

            //setting label values
            shipping += material.getShipping();
            price += material.getPrice();
            trash += material.getTrash();

            revenue += soldFor;
            totalWeight += material.getWeight();

            if (material.isSold())soldRolls++;
            if (!material.isSold())remainingRolls++;

            soldWeight += used;
            remainingWeight += remaining;
        }

        for (SimpleTableObject obj : commonMaterialProperties){
            int id = obj.getPropertyTypeId();

            switch (id) {
                case 1:
                    types++;
                    continue;
                case 2:
                    colors++;
                    continue;
                default:
                    continue;
            }
        }

        double finalShipping = PrintedAPI.round(shipping);
        double finalPrice = PrintedAPI.round(price);
        double finalRevenue = PrintedAPI.round(revenue);
        double finalProfit = PrintedAPI.round(revenue - (finalPrice + finalShipping));
        double finalRemainingWeight = PrintedAPI.round(remainingWeight);
        double finalTotalWeight = PrintedAPI.round(totalWeight);
        double finalSoldWeight = PrintedAPI.round(soldWeight);
        double finalTrash = PrintedAPI.round(trash);

        int finalTotal = selectedMaterials.size();
        int finalRemainingRolls = remainingRolls;
        int finalTotalRolls = finalTotal;
        int finalSoldRolls = soldRolls;
        int finalColors = colors, finalTypes = types;

        Platform.runLater(() -> {
            matLabelSelected.setText("Total(" + finalTotal + ")");
            matLabelSelectedShippingPriceTotal.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalShipping, finalPrice, finalShipping + finalPrice));
            matLabelSelectedRevenueProfit.setText(finalRevenue + " $/" + finalProfit + " $");
            matLabelSelectedWeightRolls.setText(String.format("%.2f kg/%d rolls", finalTotalWeight/1000, finalTotalRolls));
            matLabelSelectedSoldWeightRolls.setText(String.format("%.2f kg/%d rolls", finalSoldWeight/1000, finalSoldRolls));
            matLabelSelectedRemainingWeightRolls.setText(String.format("%.2f kg/%d rolls", finalRemainingWeight/1000, finalRemainingRolls));
            matLabelSelectedAvgRollBuySellPrice.setText(String.format("%.2f $/ %.2f $", finalPrice/finalTotal, finalRevenue/finalTotal));
            matLabelSelectedTrash.setText(finalTrash/1000 + " kg");
            //matLabelSelectedColorsTypes.setText(finalColors + "/" + finalTypes);
        });

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
    private void completeListOfObjects(){
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
            object.setSoldPrice(PrintedAPI.round(soldPrice));
            object.setCosts(PrintedAPI.round(costs));
        }

        Platform.runLater(() -> {
            objLabelTotal.setText(listOfObjects.size() + "");
        });
    }

    private void calculateSelectedObjectsStatistics(ObservableList<Object> selectedObjects){
        int soldCount  = 0, buildTime = 0;
        double soldPrice = 0, costs = 0, weight = 0, supportWeight = 0, pricePerHour;

        for (Object object : selectedObjects) {

            int objectId = object.getId();

            //We can find all required fields in listOfOrderItems.
            for (OrderItem item : listOfOrderItems) {
                Object obj = item.getObject();

                if (objectId == obj.getId()) {
                    soldCount += obj.getSoldCount();
                    soldPrice += obj.getSoldPrice();
                    costs += obj.getCosts();
                    buildTime += obj.getBuildTime();
                    weight += obj.getWeight();
                    supportWeight += obj.getSupportWeight();
                }
            }
        }

        int finalSoldCount = soldCount;
        int finalBuildTime = buildTime;

        double finalSoldPrice = PrintedAPI.round(soldPrice);
        double finalCosts = PrintedAPI.round(costs);
        double finalSupportWeight = PrintedAPI.round(supportWeight);
        double finalWeight = PrintedAPI.round(weight);
        double finalPricePerHour = PrintedAPI.round(finalSoldPrice/finalBuildTime*60);


        Platform.runLater(() -> {
            objLabelSelected.setText( "Selected(" + selectedObjects.size() + ")");
            objLabelSelectedTimesPrinted.setText(finalSoldCount + "");
            objLabelSelectedPriceCostsProfit.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalSoldPrice, finalCosts, finalSoldPrice - finalCosts));
            objLabelSelectedWeights.setText(String.format("%.2fg/%.2fg (%.2fg)", finalWeight, finalSupportWeight, finalWeight + finalSupportWeight));

            if (finalBuildTime >= 24*60){
                objLabelSelectedBuildTime.setText(String.format("%s (%s)", PrintedAPI.formatTime(finalBuildTime).get(), PrintedAPI.formatTimeToDays(finalBuildTime)));
            } else {
                objLabelSelectedBuildTime.setText(String.format("%s", PrintedAPI.formatTime(finalBuildTime).get()));
            }

            objLabelSelectedPerHour.setText(finalPricePerHour + "$/h");
        });
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
    private void completeListOfCustomers(){
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

        Platform.runLater(() -> {
            custLabelTotal.setText(listOfCustomers.size() + "");
        });
    }

    private void calculateSelectedCustomersStatistics(ObservableList<Customer> selectedCustomers){

        int ordersCount = 0, itemsPrinted = 0, buildTime = 0;
        double ordersPrice = 0, ordersCosts = 0, weight = 0, supportWeight = 0;

        for (Customer customer : selectedCustomers){

            int custId = customer.getId();

            for (Order order : listOfOrders) {
                if(custId == order.getCustomerId()) {
                    ordersCount++;
                    ordersPrice += order.getPrice();
                    itemsPrinted += order.getQuantity();
                    ordersCosts += order.getCosts();
                    weight += order.getWeight();
                    supportWeight += order.getSupportWeight();
                    buildTime += order.getBuildTime();
                }
            }
        }

        int finalItemsPrinted = itemsPrinted;
        double finalOrdersPrice = PrintedAPI.round(ordersPrice);
        int finalOrdersCount = ordersCount, finalBuildTime = buildTime;
        double finalOrdersCosts = PrintedAPI.round(ordersCosts);
        double finalSupportWeight = PrintedAPI.round(supportWeight);
        double finalWeight = PrintedAPI.round(weight);
        
        double finalPricePerHour = PrintedAPI.round(ordersPrice/finalBuildTime*60);

        Platform.runLater(() -> {
            custLabelSelected.setText("Selected(" + selectedCustomers.size() + ")");
            custLabelSelectedOrders.setText(finalOrdersCount + "");
            custLabelSelectedItemsPrinted.setText(finalItemsPrinted + "");
            custLabelSelectedPrice.setText(finalOrdersPrice + "$");
            custLabelSelectedCosts.setText(finalOrdersCosts + "$");
            custLabelSelectedWeights.setText(String.format("%.2f kg/%.2f kg", finalWeight, finalSupportWeight, finalWeight + finalSupportWeight));

            if (finalBuildTime >= 24*60){
                custLabelSelectedBuildTime.setText(String.format("%s (%s)", PrintedAPI.formatTime(finalBuildTime).get(), PrintedAPI.formatTimeToDays(finalBuildTime)));
            } else {
                custLabelSelectedBuildTime.setText(String.format("%s", PrintedAPI.formatTime(finalBuildTime).get()));
            }

            custLabelSelectedPerHour.setText(String.format("%.2f $/h", finalPricePerHour));

        });

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
    private void completeListOfOrders(){
        String custName = "Dunno Who";

        //declaration of variables used for labels
        double soldPrice = 0, soldCosts = 0,
                notSoldPrice = 0, notSoldCosts = 0,
                totalPrice = 0, totalCosts = 0,
                pricePerHour = 0, weight = 0, supportWeight = 0;

        int buildTime = 0, itemsPrinted = listOfOrderItems.size(), total = listOfOrders.size(), sold = 0, notSold = 0;

        for (Order order : listOfOrders) {

            int custId = order.getCustomerId();

            for(Customer cust : listOfCustomers) {
                if(cust.getId() == custId) {
                    custName = cust.getLastName() + " " + cust.getFirstName();
                    break;
                }
            }

            order.setCustomerName(custName);

            buildTime += order.getBuildTime();
            weight += order.getWeight();
            supportWeight += order.getSupportWeight();
            itemsPrinted += order.getQuantity();

            if (order.getStatus().equals("Sold")) {
                soldPrice += order.getPrice();
                soldCosts += order.getCosts();
                sold++;
            } else if (order.getStatus().equals("Not Sold")) {
                notSoldPrice += order.getPrice();
                notSoldCosts += order.getCosts();
                notSold++;
            }
        }

        pricePerHour = (soldPrice + notSoldPrice)/buildTime*60;

        double finalSoldCosts = soldCosts;
        double finalSoldPrice = soldPrice;
        double finalNotSoldPrice = notSoldPrice;
        double finalNotSoldCosts = notSoldCosts;
        double finalPricePerHour = pricePerHour;
        double finalSupportWeight = supportWeight;
        double finalWeight = weight;

        String finalBuildTime = PrintedAPI.formatTime(buildTime).get();

        int finalItemsPrinted = itemsPrinted;
        int finalSold = sold;
        int finalNotSold = notSold;

        Platform.runLater(() -> {
            ordersLabelNotSold.setText("Not Sold (" + finalNotSold + ")");
            ordersLabelSold.setText("Not Sold (" + finalSold + ")");
            ordersLabelTotal.setText("Total(" + total + ")");
            ordersLabelSoldPriceCostsProfit.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalSoldPrice, finalSoldCosts, finalSoldPrice - finalSoldCosts));
            ordersLabelNotSoldPriceCostsProfit.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalNotSoldPrice, finalNotSoldCosts, finalNotSoldPrice - finalNotSoldCosts));
            ordersLabelTotalPriceCostsProfit.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalSoldPrice + finalNotSoldPrice, finalSoldCosts + finalNotSoldCosts, (finalSoldPrice + finalNotSoldPrice) - (finalSoldCosts + finalNotSoldCosts)));
            ordersLabelTotalPerHour.setText(String.format("%.2f $/h", finalPricePerHour));
            ordersLabelTotalWeightSupports.setText(String.format("%.2f kg/%.2f kg", finalWeight/1000, finalSupportWeight/1000));
            ordersLabelTotalBuildTime.setText(finalBuildTime);
            ordersLabelTotalItemsPrinted.setText(finalItemsPrinted + "");
        });
    }

    private void calculateSelectedOrdersStatistics(ObservableList<Order> selectedOrders){

        //declaration of variables used for labels
        double price = 0, costs = 0, pricePerHour, weight = 0, supportWeight = 0;

        int buildTime = 0, itemsPrinted = 0, total = selectedOrders.size();

        for (Order order : selectedOrders) {

            buildTime += order.getBuildTime();
            weight += order.getWeight();
            supportWeight += order.getSupportWeight();
            itemsPrinted += order.getQuantity();
            price += order.getPrice();
            costs += order.getCosts();

        }

        pricePerHour = price/buildTime*60;

        double finalPricePerHour = pricePerHour;
        double finalSupportWeight = supportWeight;
        double finalWeight = weight;

        String finalBuildTime = PrintedAPI.formatTime(buildTime).get();

        int finalItemsPrinted = itemsPrinted;

        double finalPrice = price;
        double finalCosts = costs;

        Platform.runLater(() -> {
            ordersLabelSelected.setText("Selected(" + total + ")");
            ordersLabelSelectedPriceCostsProfit.setText(String.format("%.2f $/%.2f $ (%.2f $)", finalPrice, finalCosts, finalPrice - finalCosts));
            ordersLabelSelectedPerHour.setText(String.format("%.2f $/h", finalPricePerHour));
            ordersLabelSelectedWeightSupports.setText(String.format("%.2f kg/%.2f kg", finalWeight/1000, finalSupportWeight/1000));
            ordersLabelSelectedBuildTime.setText(finalBuildTime);
            ordersLabelSelectedItemsPrinted.setText(finalItemsPrinted + "");
        });


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
                    try {
                        int i = -1, max = 12;

                        progressBar.setVisible(true);

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

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                return null;
                }
            };
            }
        };
        progressBar.progressProperty().bind(serviceDownloadAllTables.progressProperty());
        labelMainInfo.textProperty().bind(serviceDownloadAllTables.messageProperty());


    /*****************************          INITIALIZE COSTS TAB          *****************************/
    costsTv.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Cost> c) -> {
        calculateSelectedCostsStatistics(costsTv.getSelectionModel().getSelectedItems());
    });

    costsBtnCreate.setOnAction((event) -> {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/ViewCreateCost.fxml"));
            Parent root1 = fxmlLoader.load();
            ControllerCreateCost ctrl = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Cost");
            stage.setMinHeight(440);
            stage.setMinWidth(400);

            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.centerOnScreen();


            //passing credentials to main controller
            ctrl.setDs(ds);
            ctrl.setControllerMain(this);
            ctrl.setFieldsValues();
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    });

    /*****************************          INITIALIZE PRINTERS TAB          *****************************/
    printersTv.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Printer> c) -> {
        calculateSelectedPrintersStatistics(printersTv.getSelectionModel().getSelectedItems());
    });

    printersBtnCreate.setOnAction(event -> {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/ViewCreatePrinter.fxml"));
            Parent root1 = fxmlLoader.load();
            ControllerCreatePrinter ctrl = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Printer");
            //stage.setMinHeight(440);
            //stage.setMinWidth(506);

            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.centerOnScreen();


            //passing credentials to main controller
            ctrl.setDs(ds);
            ctrl.setControllerMain(this);
            ctrl.setFieldsValues();
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    });


    /*****************************          INITIALIZE MATERIALS TAB          *****************************/
    matTv.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Material> c) -> {
        calculateSelectedMaterialsStatistics(matTv.getSelectionModel().getSelectedItems());
    });

    matBtnCreate.setOnAction((event) -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/ViewCreateMaterial.fxml"));
                Parent root1 = fxmlLoader.load();
                ControllerCreateMaterial ctrl = fxmlLoader.getController();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("New Material");
                stage.setMinHeight(440);
                stage.setMinWidth(400);

                stage.setScene(new Scene(root1));
                stage.setResizable(false);
                stage.centerOnScreen();


                //passing credentials to main controller
                ctrl.setDs(ds);
                ctrl.setControllerMain(this);
                ctrl.setFieldsValues();
                stage.show();

            }catch (IOException e){
                e.printStackTrace();
            }
        });

    /*****************************          INITIALIZE OBJECTS TAB          *****************************/
    objTv.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Object> c) -> {
        calculateSelectedObjectsStatistics(objTv.getSelectionModel().getSelectedItems());
    });

    objBtnCreate.setOnAction(event -> {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/ViewCreateObject.fxml"));
            Parent root1 = fxmlLoader.load();
            ControllerCreateObject ctrl = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Object");
            //stage.setMinHeight(440);
            //stage.setMinWidth(506);

            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.centerOnScreen();


            //passing credentials to main controller
            ctrl.setDs(ds);
            ctrl.setControllerMain(this);
            ctrl.setFieldsValues();
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    });
    /*****************************          INITIALIZE CUSTOMERS TAB          *****************************/
    custTv.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Customer> c) -> {
        calculateSelectedCustomersStatistics(custTv.getSelectionModel().getSelectedItems());
    });

    custBtnCreate.setOnAction(event -> {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/ViewCreateCustomer.fxml"));
            Parent root1 = fxmlLoader.load();
            ControllerCreateCustomer ctrl = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Customer");
            //stage.setMinHeight(440);
            //stage.setMinWidth(506);

            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.centerOnScreen();


            //passing credentials to main controller
            ctrl.setDs(ds);
            ctrl.setControllerMain(this);
            ctrl.setFieldsValues();
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    });

    /*****************************          INITIALIZE ORDERS TAB          *****************************/
    ordersTv.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Order> c) -> {
        calculateSelectedOrdersStatistics(ordersTv.getSelectionModel().getSelectedItems());
    });

    ordersBtnCreate.setOnAction(event -> {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/create/order/ViewCreateOrder.fxml"));
            Parent root1 = fxmlLoader.load();
            ControllerCreateOrder ctrl = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Object");
            //stage.setMinHeight(440);
            //stage.setMinWidth(506);

            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            //passing credentials to main controller
            ctrl.setDs(ds);
            ctrl.setControllerMain(this);
            ctrl.setFieldsValues();


        }catch (IOException e){
            e.printStackTrace();
        }
    });

    }//end of initialize();

    public Label getLabelMainInfo() {
        return labelMainInfo;
    }

    public ObservableList<SimpleTableObject> getCommonCustomerProperties() {
        return commonCustomerProperties;
    }

    public ObservableList<SimpleTableObject> getCommonMaterialProperties() {
        return commonMaterialProperties;
    }

    public ObservableList<SimpleTableObject> getCommonCustomerPropertyTypes() {
        return commonCustomerPropertyTypes;
    }

    public ObservableList<SimpleTableObject> getCommonMaterialPropertyTypes() {
        return commonMaterialPropertyTypes;
    }

    public ObservableList<SimpleTableObject> getCommonOrderStatus() {
        return commonOrderStatus;
    }

    /*************************          GETTERS & SETTERS          *************************/

    public ObservableList<Customer> getListOfCustomers() {
        return listOfCustomers;
    }

    public ObservableList<Object> getListOfObjects() {
        return listOfObjects;
    }

    public ObservableList<Material> getListOfNotSpentMaterials() {

        ObservableList<Material> notSpentMaterials = FXCollections.observableArrayList();

        for (Material mat : listOfMaterials){
            if (mat.getFinished().equals("No"))notSpentMaterials.add(mat);
        }

        return notSpentMaterials;
    }

    public ObservableList<Printer> getListOfPrinters() {
        return listOfPrinters;
    }

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

    public TableView<Cost> getCostsTv() {
        return costsTv;
    }

    public TableView<Printer> getPrintersTv() {
        return printersTv;
    }

    public TableView<Material> getMatTv() {
        return matTv;
    }

    public TableView<Object> getObjTv() {
        return objTv;
    }

    public TableView<Customer> getCustTv() {
        return custTv;
    }

    public TableView<Order> getOrdersTv() {
        return ordersTv;
    }
}
