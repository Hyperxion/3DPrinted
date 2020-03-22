package classes;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;


public class OrderItem {

    //table fields
    private SimpleIntegerProperty id, orderId;


    //additional fields - included in object
    //SimpleIntegerProperty (object)id, buildTime, soldCount(quantity)
    //SimpleDoubleProperty supportWeight, weight, soldPrice, costs;
    //SimpleStringProperty buildTimeFormatted;
    private Object object;

    //additional fields - included in Material
    //SimpleIntegerProperty materialId;
    //private SimpleStringProperty material_type, material_color;
    private Material material;

    //additional fields - included in Printer
    //private SimpleStringProperty printer_name;
    //SimpleIntegerProperty printerId
    private Printer printer;

    public OrderItem(SimpleIntegerProperty id, SimpleIntegerProperty orderId, Object obj, Material mat, Printer printer) {
        this.id = id;
        this.orderId = orderId;
        this.object = obj;
        this.material = mat;
        this.printer = printer;
    }

    public static ObservableList<OrderItem> downloadOrderItemsTable(HikariDataSource ds){

        //Create list
        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM OrderItems ORDER BY OrderID DESC";

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

                //table fields
                SimpleIntegerProperty id, orderId, objectId, materialId, soldCount, printerId, buildTime;
                SimpleDoubleProperty supportWeight, weight, price, costs;

                id = new SimpleIntegerProperty(rs.getInt("OrderItemID"));
                orderId = new SimpleIntegerProperty(rs.getInt("OrderID"));
                objectId = new SimpleIntegerProperty(rs.getInt("ObjectID"));
                materialId = new SimpleIntegerProperty(rs.getInt("ItemMaterialID"));
                buildTime = new SimpleIntegerProperty(rs.getInt("ItemBuildTime"));
                soldCount = new SimpleIntegerProperty(rs.getInt("ItemQuantity"));
                printerId = new SimpleIntegerProperty(rs.getInt("PrinterID"));

                supportWeight = new SimpleDoubleProperty(rs.getDouble("ItemSupportWeight"));
                weight = new SimpleDoubleProperty(rs.getDouble("ItemWeight"));
                price = new SimpleDoubleProperty(rs.getDouble("ItemPrice"));
                costs = new SimpleDoubleProperty(rs.getDouble("ItemCosts"));

                Object obj1 = new Object(objectId, buildTime, weight, supportWeight, soldCount, price, costs);
                Material mat1 = new Material(materialId);
                Printer printer1 = new Printer(printerId);

                OrderItem orderItem = new OrderItem(id, orderId, obj1, mat1, printer1);

                orderItems.add(orderItem);

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

        return orderItems;
    }

    public static void insertUpdateOrderItem(ObservableList<OrderItem> orderItems, HikariDataSource ds, int autoincrementValue){

        //Create query
        String updateQuery;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            //STEP 2: Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            //STEP 3: Open a connection

            conn = ds.getConnection();
            //STEP 4: Execute a query

            for (OrderItem orderItem : orderItems) {
                updateQuery = "INSERT INTO OrderItems (OrderItemID, OrderID, ObjectID, ItemMaterialID, ItemWeight, ItemSupportWeight, ItemBuildTime, ItemPrice, ItemQuantity, PrinterID, ItemCosts) VALUES (?,?,?,?,?,?,?,?,?,?,?) "
                        + "ON DUPLICATE KEY UPDATE OrderItemID=?, OrderID=?, ObjectID=?, ItemMaterialID=?, ItemWeight=?, ItemSupportWeight=?, ItemBuildTime=?, ItemPrice=?, ItemQuantity=?, PrinterID=?, ItemCosts=?";

                stmt = conn.prepareStatement(updateQuery);

                int i = 0;

                if (orderItem.getId() != 0){
                    stmt.setInt(++i, orderItem.getId());
                } else {
                    stmt.setInt(++i, autoincrementValue++);
                }

                stmt.setInt(++i, orderItem.getOrderId());//orderitem id
                stmt.setInt(++i, orderItem.getObject().getId());//order id
                stmt.setInt(++i, orderItem.getMaterial().getId());
                stmt.setDouble(++i, orderItem.getObject().getWeight());
                stmt.setDouble(++i, orderItem.getObject().getSupportWeight());
                stmt.setInt(++i, orderItem.getObject().getBuildTime());
                stmt.setDouble(++i,orderItem.getObject().getSoldPrice());
                stmt.setInt(++i,orderItem.getObject().getSoldCount());
                stmt.setInt(++i,orderItem.getPrinter().getId());
                stmt.setDouble(++i, orderItem.getObject().getCosts());

                if (orderItem.getId() != 0){
                    stmt.setInt(++i, orderItem.getId());
                } else {
                    stmt.setInt(++i, autoincrementValue);
                }
                stmt.setInt(++i, orderItem.getOrderId());//orderitem id
                stmt.setInt(++i, orderItem.getObject().getId());
                stmt.setInt(++i, orderItem.getMaterial().getId());
                stmt.setDouble(++i, orderItem.getObject().getWeight());
                stmt.setDouble(++i, orderItem.getObject().getSupportWeight());
                stmt.setInt(++i, orderItem.getObject().getBuildTime());
                stmt.setDouble(++i,orderItem.getObject().getSoldPrice());
                stmt.setInt(++i,orderItem.getObject().getSoldCount());
                stmt.setInt(++i,orderItem.getPrinter().getId());
                stmt.setDouble(++i, orderItem.getObject().getCosts());

                stmt.executeUpdate();
            }

            stmt.close();
            conn.close();
        } catch (SQLNonTransientConnectionException se) {
            se.printStackTrace();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
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
    }

    public static void deleteOrderItems(ObservableList<OrderItem> orderItems, HikariDataSource ds){

        //Create query
        String updateQuery;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            //STEP 2: Register JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            //STEP 3: Open a connection

            conn = ds.getConnection();
            //STEP 4: Execute a query

            for (OrderItem orderItem : orderItems) {

                updateQuery = "DELETE FROM OrderItems WHERE OrderItemId = " + orderItem.getId();

                stmt = conn.prepareStatement(updateQuery);
                stmt.executeUpdate();
            }

            stmt.close();
            conn.close();
        } catch (SQLNonTransientConnectionException se) {
            se.printStackTrace();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
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

    public int getOrderId() {
        return orderId.get();
    }

    public SimpleIntegerProperty orderIdProperty() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId.set(orderId);
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }
}
