package classes;

import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Printer {

    //table columns
    private SimpleIntegerProperty id, typeID;
    private SimpleStringProperty name, purchaseDate, comment;
    private SimpleDoubleProperty shipping, price, duty, tax;

    //additional columns
    private SimpleIntegerProperty itemsSold;
    private SimpleStringProperty type;
    private SimpleDoubleProperty overallIncome, expenses, incomes;

    public static ObservableList<Printer> downloadPrintersTable(HikariDataSource ds) {
        //Create list
        ObservableList<Printer> printers = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM Printers ORDER BY PrinterID";

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

            while(rs.next()){

                //table columns
                SimpleIntegerProperty id, typeID;
                SimpleStringProperty name, purchaseDate, comment;
                SimpleDoubleProperty shipping, price, duty, tax;

                id = new SimpleIntegerProperty(rs.getInt("PrinterID"));
                typeID = new SimpleIntegerProperty(rs.getInt("PrinterTypeID"));

                name = new SimpleStringProperty(rs.getString("PrinterName"));
                purchaseDate = new SimpleStringProperty(rs.getString("PurchaseDate"));
                comment = new SimpleStringProperty(rs.getString("Comment"));

                shipping = new SimpleDoubleProperty(rs.getDouble("PrinterShipping"));
                price = new SimpleDoubleProperty(rs.getDouble("PrinterPrice"));
                duty = new SimpleDoubleProperty(rs.getDouble("Duty"));
                tax = new SimpleDoubleProperty(rs.getDouble("Tax"));

                Printer printer = new Printer(id, typeID, name, purchaseDate, comment, shipping, price, duty, tax);

                printers.add(printer);

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

        return printers;
    }

    public static void insertUpdatePrinter(Printer printer, HikariDataSource ds){
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


            updateQuery = "INSERT INTO Printers (PrinterID,PrinterName,PrinterPrice,Comment,PrinterShipping,PrinterTypeID,PurchaseDate,Duty,Tax) VALUES (?,?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE PrinterID=?,PrinterName=?,PrinterPrice=?,Comment=?,PrinterShipping=?,PrinterTypeID=?,PurchaseDate=?,Duty=?,Tax=?";
            stmt = conn.prepareStatement(updateQuery);

            int i = 0;

            stmt.setInt(++i, printer.getId());
            stmt.setString(++i, printer.getName());
            stmt.setDouble(++i, printer.getPrice());
            stmt.setString(++i, printer.getComment());
            stmt.setDouble(++i, printer.getShipping());
            stmt.setInt(++i, printer.getTypeID());
            stmt.setString(++i, printer.getPurchaseDate());
            stmt.setDouble(++i, printer.getDuty());
            stmt.setDouble(++i, printer.getTax());

            stmt.setInt(++i, printer.getId());
            stmt.setString(++i, printer.getName());
            stmt.setDouble(++i, printer.getPrice());
            stmt.setString(++i, printer.getComment());
            stmt.setDouble(++i, printer.getShipping());
            stmt.setInt(++i, printer.getTypeID());
            stmt.setString(++i, printer.getPurchaseDate());
            stmt.setDouble(++i, printer.getDuty());
            stmt.setDouble(++i, printer.getTax());

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

    //constructor for creating Printer from information stored in database table "Printers"
    public Printer(SimpleIntegerProperty id, SimpleIntegerProperty typeID, SimpleStringProperty name, SimpleStringProperty purchaseDate, SimpleStringProperty comment, SimpleDoubleProperty shipping, SimpleDoubleProperty price, SimpleDoubleProperty duty, SimpleDoubleProperty tax) {
        this.id = id;
        this.typeID = typeID;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.comment = comment;
        this.shipping = shipping;
        this.price = price;
        this.duty = duty;
        this.tax = tax;
        this.itemsSold = new SimpleIntegerProperty(0);
        this.type = new SimpleStringProperty("Unknown type");
        this.overallIncome = new SimpleDoubleProperty(0);
        this.expenses = new SimpleDoubleProperty(0);
        this.incomes = new SimpleDoubleProperty(0);
    }

    //constructor for creating printer from information stored in orderItem
    public Printer(SimpleIntegerProperty id) {
        this.id = id;
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

    public int getTypeID() {
        return typeID.get();
    }

    public SimpleIntegerProperty typeIDProperty() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID.set(typeID);
    }

    public String getName() {
        return name.get();
    }

    //returns name of printer from the list which matches with printerId provided
    public static String getName(ObservableList<Printer> printers, int printerId) {
        for (Printer printer: printers){
            if (printer.getId() == printerId)return printer.getName();//return printer.nameProperty();
        }
        //return new SimpleStringProperty("Unknown printer");
        return  "Unknown printer";
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPurchaseDate() {
        return purchaseDate.get();
    }

    public SimpleStringProperty purchaseDateProperty() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate.set(purchaseDate);
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

    public double getShipping() {
        return shipping.get();
    }

    public SimpleDoubleProperty shippingProperty() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping.set(shipping);
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

    public double getDuty() {
        return duty.get();
    }

    public SimpleDoubleProperty dutyProperty() {
        return duty;
    }

    public void setDuty(double duty) {
        this.duty.set(duty);
    }

    public double getTax() {
        return tax.get();
    }

    public SimpleDoubleProperty taxProperty() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax.set(tax);
    }

    public int getItemsSold() {
        return itemsSold.get();
    }

    public SimpleIntegerProperty itemsSoldProperty() {
        return itemsSold;
    }

    public void setItemsSold(int itemsSold) {
        this.itemsSold.set(itemsSold);
    }

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public double getOverallIncome() {
        return overallIncome.get();
    }

    public SimpleDoubleProperty overallIncomeProperty() {
        return overallIncome;
    }

    public void setOverallIncome(double overallIncome) {
        this.overallIncome.set(overallIncome);
    }

    public double getExpenses() {
        return expenses.get();
    }

    public SimpleDoubleProperty expensesProperty() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses.set(expenses);
    }

    public double getIncomes() {
        return incomes.get();
    }

    public SimpleDoubleProperty incomesProperty() {
        return incomes;
    }

    public void setIncomes(double incomes) {
        this.incomes.set(incomes);
    }
}
