package classes;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Object {

    //table columns
    private SimpleStringProperty name, stlLink, comment;
    private SimpleIntegerProperty id, buildTime, projectId;
    private SimpleDoubleProperty supportWeight, weight;

    //additional columns
    private SimpleStringProperty buildTimeFormatted;
    private SimpleIntegerProperty soldCount;
    private SimpleDoubleProperty soldPrice, costs;//field "costs" is used only for purpose of orderitem costs calculation

    //general constructor  for objects from database
    public Object(SimpleStringProperty name, SimpleStringProperty stlLink, SimpleStringProperty comment, SimpleIntegerProperty id, SimpleIntegerProperty buildTime, SimpleIntegerProperty projectId, SimpleDoubleProperty supportWeight, SimpleDoubleProperty weight) {
        this.name = name;
        this.stlLink = stlLink;
        this.comment = comment;
        this.id = id;
        this.buildTime = buildTime;
        this.projectId = projectId;
        this.supportWeight = supportWeight;
        this.weight = weight;
        this.buildTimeFormatted = PrintedAPI.formatTime(buildTime.get());
        this.soldCount = new SimpleIntegerProperty(0);
        this.soldPrice = new SimpleDoubleProperty(0);
        this.costs = new SimpleDoubleProperty(0);
    }

    //constructor for creating printed object from information stored in orderItem
    public Object(SimpleIntegerProperty objectId, SimpleIntegerProperty buildTime, SimpleDoubleProperty weight, SimpleDoubleProperty supportWeight, SimpleIntegerProperty soldCount, SimpleDoubleProperty price, SimpleDoubleProperty costs) {
        this.name = new SimpleStringProperty("null");
        this.stlLink = new SimpleStringProperty("null");
        this.comment = new SimpleStringProperty("null");
        this.id = objectId;
        this.buildTime = buildTime;
        this.projectId = new SimpleIntegerProperty(0);
        this.supportWeight = supportWeight;
        this.weight = weight;
        this.buildTimeFormatted = PrintedAPI.formatTime(buildTime.get());
        this.soldCount = soldCount;
        this.soldPrice = price;
        this.costs = costs;
    }

    public static ObservableList<Object> downloadObjectsTable(HikariDataSource ds){

        //Create list
        ObservableList<Object> objectList = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM Objects ORDER BY ObjectID DESC";

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
                SimpleStringProperty name, stlLink, comment;
                SimpleIntegerProperty id, buildTime, projectId;
                SimpleDoubleProperty supportWeight, weight;


                name = new SimpleStringProperty(rs.getString("ObjectName"));
                stlLink = new SimpleStringProperty(rs.getString("StlLink"));
                comment = new SimpleStringProperty(rs.getString("Comment"));

                id = new SimpleIntegerProperty(rs.getInt("ObjectID"));
                buildTime = new SimpleIntegerProperty(rs.getInt("BuildTime"));
                //buildTimeFormatted = PrintedAPI.formatTime(buildTime.get());
                projectId = new SimpleIntegerProperty(rs.getInt("ProjectID"));

                supportWeight = new SimpleDoubleProperty(rs.getDouble("SupportWeight"));
                weight = new SimpleDoubleProperty(rs.getDouble("ObjectWeight"));

                Object object = new Object(name, stlLink, comment, id, buildTime, projectId, supportWeight, weight);

                objectList.add(object);
            }

            rs.close();
        } catch (NullPointerException e){
            //signIn(event);
            e.printStackTrace();
        } catch (SQLNonTransientConnectionException se) {
            se.printStackTrace();
        } catch (SQLException se) {
            //Handle errors for JDBC
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

        return objectList;
    }

    public static void insertUpdateObject(Object newObject, HikariDataSource ds) {
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


            updateQuery = "INSERT INTO Objects (ObjectID,ObjectName,ObjectWeight,SupportWeight,BuildTime,StlLink,Comment, ProjectID) VALUES (?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE ObjectID=?,ObjectName=?,ObjectWeight=?,SupportWeight=?,BuildTime=?,StlLink=?,Comment=?,ProjectID=?";
            stmt = conn.prepareStatement(updateQuery);

            int i = 0;
            
            stmt.setInt(++i, newObject.getId());//id
            stmt.setString(++i, newObject.getName());//name
            stmt.setDouble(++i, newObject.getWeight());//weight
            stmt.setDouble(++i, newObject.getSupportWeight());//supports
            stmt.setInt(++i, newObject.getBuildTime());//buildtime
            stmt.setString(++i, newObject.getStlLink());//stllink
            stmt.setString(++i, newObject.getComment());//comment
            stmt.setInt(++i, 1);//projectId

            stmt.setInt(++i, newObject.getId());//id
            stmt.setString(++i, newObject.getName());//name
            stmt.setDouble(++i, newObject.getWeight());//weight
            stmt.setDouble(++i, newObject.getSupportWeight());//supports
            stmt.setInt(++i, newObject.getBuildTime());//buildtime
            stmt.setString(++i, newObject.getStlLink());//stllink
            stmt.setString(++i, newObject.getComment());//comment
            stmt.setInt(++i, 1);//projectId

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        } //Handle errors for JDBC
        catch (Exception se) {
            se.printStackTrace();
        }//Handle errors for Class.forName
        finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException ignored) {
            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }

    public static void deleteObjects(ObservableList<Object> objects, HikariDataSource ds){
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

            for (Object object : objects){
                updateQuery = "DELETE FROM Objects WHERE ObjectID =  " + object.getId();
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

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getStlLink() {
        return stlLink.get();
    }

    public SimpleStringProperty stlLinkProperty() {
        return stlLink;
    }

    public void setStlLink(String stlLink) {
        this.stlLink.set(stlLink);
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

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
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

    public int getProjectId() {
        return projectId.get();
    }

    public SimpleIntegerProperty projectIdProperty() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId.set(projectId);
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

    public double getWeight() {
        return weight.get();
    }

    public SimpleDoubleProperty weightProperty() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
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

    public int getSoldCount() {
        return soldCount.get();
    }

    public SimpleIntegerProperty soldCountProperty() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount.set(soldCount);
    }

    public double getSoldPrice() {
        return soldPrice.get();
    }

    public SimpleDoubleProperty soldPriceProperty() {
        return soldPrice;
    }

    public void setSoldPrice(double soldPrice) {
        this.soldPrice.set(soldPrice);
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
}
