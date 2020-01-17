package classes;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class Order {

    //table columns
    private SimpleIntegerProperty orderID, order_customerID;
    private SimpleStringProperty order_status, order_comment, order_dateCreated, order_dueDate;

    //table view columns
    private SimpleIntegerProperty order_quantity, order_buildTime;
    private SimpleDoubleProperty order_costs, order_price, order_weight, order_support_weight;
    private SimpleStringProperty order_customer,order_buildTime_formated;


    private ObservableList<OrderItem> orderItems;

    private Customer customer;

}
