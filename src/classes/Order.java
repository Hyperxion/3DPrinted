package classes;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Order {

    //table columns
    private SimpleIntegerProperty id, customerId, buildTime, quantity;
    private SimpleStringProperty status, comment, dateCreated, dueDate;
    private SimpleDoubleProperty costs, price, weight, supportWeight;

    //table view columns
    private SimpleStringProperty customerName, buildTimeFormatted;


    private ObservableList<OrderItem> orderItems;
    private Customer customer;

    //this constructor is used for loading of orders directly from database
    public Order(SimpleIntegerProperty id, SimpleIntegerProperty customerId, SimpleIntegerProperty buildTime, SimpleIntegerProperty quantity, SimpleStringProperty status, SimpleStringProperty comment, SimpleStringProperty dateCreated, SimpleStringProperty dueDate, SimpleDoubleProperty costs, SimpleDoubleProperty price, SimpleDoubleProperty weight, SimpleDoubleProperty supportWeight) {
        this.id = id;
        this.customerId = customerId;
        this.buildTime = buildTime;
        this.quantity = quantity;
        this.status = status;
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.dueDate = dueDate;
        this.costs = costs;
        this.price = price;
        this.weight = weight;
        this.supportWeight = supportWeight;
        this.customerName = new SimpleStringProperty("Dunno");
        this.buildTimeFormatted = PrintedAPI.formatTime(buildTime.get());
        this.orderItems = null;
        this.customer = null;
    }

    //this constructor is used only for purpose of inserting order into database table. Since we dont need customer name (we will use their id)
    public Order(SimpleIntegerProperty id, Customer customer, SimpleIntegerProperty buildTime, SimpleIntegerProperty quantity, SimpleStringProperty status, SimpleStringProperty comment, SimpleStringProperty dateCreated, SimpleStringProperty dueDate, SimpleDoubleProperty costs, SimpleDoubleProperty price, SimpleDoubleProperty weight, SimpleDoubleProperty supportWeight, ObservableList<OrderItem> orderItems) {
        this.id = id;
        this.customerId = customer.idProperty();
        this.buildTime = buildTime;
        this.quantity = quantity;
        this.status = status;
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.dueDate = dueDate;
        this.costs = costs;
        this.price = price;
        this.weight = weight;
        this.supportWeight = supportWeight;
        this.customerName = null;
        this.buildTimeFormatted = PrintedAPI.formatTime(buildTime.get());
        this.orderItems = orderItems;
        this.customer = null;
    }

    public static ObservableList<Order> downloadOrdersTable(HikariDataSource ds) {

        //Create list
        ObservableList<Order> orders = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM Orders ORDER BY OrderID DESC";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {

            //STEP 2: Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            //STEP 3: Open a connection
            conn = ds.getConnection();

            //STEP 4: Execute a query
            stmt = conn.createStatement();

            rs = stmt.executeQuery(query);
            //Query is executed, resultSet saved. Now we need to process the data
            //rs.next() loads row
            //in this loop we sequentialy add columns to list of Strings
            while(rs.next()){

                //table columns
                SimpleIntegerProperty orderId, customerId, buildTime, quantity;
                SimpleStringProperty status, comment, dateCreated, dueDate;
                SimpleDoubleProperty costs, price, weight, supportWeight;

                orderId = new SimpleIntegerProperty(rs.getInt("OrderID"));
                customerId = new SimpleIntegerProperty(rs.getInt("CustomerID"));
                buildTime = new SimpleIntegerProperty(rs.getInt("OrderBuildTime"));
                quantity = new SimpleIntegerProperty(rs.getInt("OrderQuantity"));

                dateCreated = new SimpleStringProperty(rs.getString("DateCreated"));
                status = new SimpleStringProperty(rs.getString("OrderStatus"));
                dueDate = new SimpleStringProperty(rs.getString("DueDate"));
                comment = new SimpleStringProperty(rs.getString("Comment"));

                costs = new SimpleDoubleProperty(rs.getDouble("OrderCosts"));
                price = new SimpleDoubleProperty(rs.getDouble("OrderPrice"));
                weight = new SimpleDoubleProperty(rs.getDouble("OrderWeight"));
                supportWeight = new SimpleDoubleProperty(rs.getDouble("OrderSupportWeight"));

                Order order = new Order(orderId, customerId, buildTime, quantity, status, comment, dateCreated, dueDate, costs, price, weight, supportWeight);

                orders.add(order);

            }

            rs.close();
        } catch (NullPointerException e){
            //signIn(event);
            e.printStackTrace();
        } catch (SQLNonTransientConnectionException se) {
            se.printStackTrace();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (ClassNotFoundException se) {
            //Handle errors for Class.forName

            se.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try

        return orders;
    }

    public static void insertUpdateOrder(Order newOrder, HikariDataSource ds){

    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getCustomerId() {
        return customerId.get();
    }

    public SimpleIntegerProperty customerIdProperty() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public int getBuildTime() {
        return buildTime.get();
    }

    public SimpleIntegerProperty buildTimeProperty() {
        return buildTime;
    }

    public void setBuildTime(int buildTime) {
        this.buildTime.set(buildTime);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getComment() {
        return comment.get();
    }

    public SimpleStringProperty commentProperty() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public String getDateCreated() {
        return dateCreated.get();
    }

    public SimpleStringProperty dateCreatedProperty() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated.set(dateCreated);
    }

    public String getDueDate() {
        return dueDate.get();
    }

    public SimpleStringProperty dueDateProperty() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate.set(dueDate);
    }

    public double getCosts() {
        return costs.get();
    }

    public SimpleDoubleProperty costsProperty() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs.set(costs);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public double getWeight() {
        return weight.get();
    }

    public SimpleDoubleProperty weightProperty() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    public double getSupportWeight() {
        return supportWeight.get();
    }

    public SimpleDoubleProperty supportWeightProperty() {
        return supportWeight;
    }

    public void setSupportWeight(double supportWeight) {
        this.supportWeight.set(supportWeight);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public String getBuildTimeFormatted() {
        return buildTimeFormatted.get();
    }

    public SimpleStringProperty buildTimeFormattedProperty() {
        return buildTimeFormatted;
    }

    public void setBuildTimeFormatted(String buildTimeFormatted) {
        this.buildTimeFormatted.set(buildTimeFormatted);
    }

    public ObservableList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ObservableList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
