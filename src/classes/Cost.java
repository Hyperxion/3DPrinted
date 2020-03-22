package classes;

import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Cost {

    //database table columns
    private SimpleIntegerProperty id, quantity, printerId;
    private SimpleStringProperty name, purchaseDate, comment;
    private SimpleDoubleProperty shipping, price;

    //additional fields
    private SimpleStringProperty printerName;

    public static ObservableList<Cost> downloadCostsTable(HikariDataSource ds) {

        //Create list
        ObservableList<Cost> costs = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM Costs ORDER BY CostID DESC";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs;
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

                SimpleIntegerProperty id = new SimpleIntegerProperty(rs.getInt("CostID"));
                SimpleIntegerProperty quantity = new SimpleIntegerProperty(rs.getInt("CostQuantity"));
                SimpleIntegerProperty printerId = new SimpleIntegerProperty(rs.getInt("PrinterID"));

                SimpleDoubleProperty shipping = new SimpleDoubleProperty(rs.getDouble("CostShipping"));
                SimpleDoubleProperty price = new SimpleDoubleProperty(rs.getDouble("CostPrice"));

                SimpleStringProperty name = new SimpleStringProperty(rs.getString("CostName"));
                SimpleStringProperty purchaseDate = new SimpleStringProperty(rs.getString("PurchaseDate"));
                SimpleStringProperty comment = new SimpleStringProperty(rs.getString("Comment"));

                Cost cost = new Cost(id, quantity, printerId, name, purchaseDate, comment, shipping, price, null);

                costs.add(cost);

            }
            rs.close();

        } catch (NullPointerException | SQLException | ClassNotFoundException e){
            e.printStackTrace();
        } //Handle errors for Class.forName
        finally {
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

        return costs;
    }

    public Cost(SimpleIntegerProperty id, SimpleIntegerProperty quantity, SimpleIntegerProperty printerId, SimpleStringProperty name, SimpleStringProperty purchaseDate, SimpleStringProperty comment, SimpleDoubleProperty shipping, SimpleDoubleProperty price, SimpleStringProperty printerName) {
        this.id = id;
        this.quantity = quantity;
        this.printerId = printerId;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.comment = comment;
        this.shipping = shipping;
        this.price = price;
        if (printerName == null) {
            this.printerName = new SimpleStringProperty();
        } else {
            this.printerName = printerName;
        }

    }

    public static void insertUpdateCost(Cost cost, HikariDataSource ds) {
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


            updateQuery = "INSERT INTO Costs (CostID,CostName,CostQuantity,CostShipping, PurchaseDate, Comment,CostPrice,PrinterID) VALUES (?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE CostID=?,CostName=?,CostQuantity=?,CostShipping=?, PurchaseDate=?, Comment=?,CostPrice=?,PrinterID=?";
            stmt = conn.prepareStatement(updateQuery);

            int i = 0;

            stmt.setInt(++i, cost.getId());//Cost id
            stmt.setString(++i, cost.getName());//Cost name
            stmt.setInt(++i, cost.getQuantity());//quantity
            stmt.setDouble(++i,cost.getShipping());//shipping
            stmt.setString(++i, cost.getPurchaseDate());//purchaseDate
            stmt.setString(++i, cost.getComment());//comment
            stmt.setDouble(++i, cost.getPrice());//price
            stmt.setInt(++i, cost.getPrinterId());//printerId

            stmt.setInt(++i, cost.getId());//Cost id
            stmt.setString(++i, cost.getName());//Cost name
            stmt.setInt(++i, cost.getQuantity());//quantity
            stmt.setDouble(++i,cost.getShipping());//shipping
            stmt.setString(++i, cost.getPurchaseDate());//purchaseDate
            stmt.setString(++i, cost.getComment());//comment
            stmt.setDouble(++i, cost.getPrice());//price
            stmt.setInt(++i, cost.getPrinterId());//printerId

            stmt.executeUpdate();

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

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public int getPrinterId() {
        return printerId.get();
    }

    public SimpleIntegerProperty printerIdProperty() {
        return printerId;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getPurchaseDate() {
        return purchaseDate.get();
    }

    public SimpleStringProperty purchaseDateProperty() {
        return purchaseDate;
    }

    public String getComment() {
        return comment.get();
    }

    public SimpleStringProperty commentProperty() {
        return comment;
    }

    public double getShipping() {
        return shipping.get();
    }

    public SimpleDoubleProperty shippingProperty() {
        return shipping;
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public String getPrinterName() {
        return printerName.get();
    }

    public SimpleStringProperty printerNameProperty() {
        return printerName;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public void setPrinterId(int printerId) {
        this.printerId.set(printerId);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate.set(purchaseDate);
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public void setShipping(double shipping) {
        this.shipping.set(shipping);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public void setPrinterName(String printerName) {
        this.printerName.set(printerName);
    }
}
