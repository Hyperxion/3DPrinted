package classes;

import com.zaxxer.hikari.HikariDataSource;

import java.net.SocketImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.management.openmbean.OpenMBeanInfoSupport;


/*
The purpose of this special class is to unify all auxiliary tables into one.
Database version of  this object exists as table with PropertyID, PropertyTypeID, PropertyName
or PropertyTypeID and PropertyTypeName. In the latter case, missing parameter PropertyID is initialized
as null.

Example of use:

Let's say you have many similar tables which consists of only primary key and text. Such tables can be:

- countries - in this case property is country. Each country needs to have unique identifier (propertyId) and text of property (propertyName)
- types of 3D printers - in this case, property is type (FDM, SLA, DLP...)
- types of Materials - in this case property is Material. Material has it's unique materialID (PropertyID), material type (propertyTypeId)
  and this propertyId can be distributor, seller, color (depends on number they represent, for example each distributor has propertyTypeId
  equals to 3. Tables of this types is called MaterialPropertyTypes) and property text. This can be text of color, text of distributor, seller...
 */


public class SimpleTableObject {

    private SimpleIntegerProperty propertyId, propertyTypeId;
    private SimpleStringProperty propertyName;

    public static ObservableList<SimpleTableObject> downloadCommonMaterialProperties(HikariDataSource ds) {

        //Create list
        ObservableList<SimpleTableObject> properties = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM CommonMaterialProperties ORDER BY PropertyID ASC";

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

                SimpleIntegerProperty property_id, property_type_id;
                SimpleStringProperty property_type_name;

                property_id = new SimpleIntegerProperty(rs.getInt("PropertyID"));
                property_type_id = new SimpleIntegerProperty(rs.getInt("PropertyTypeID"));
                property_type_name = new SimpleStringProperty(rs.getString("PropertyName"));

                SimpleTableObject property = new SimpleTableObject(property_id, property_type_id, property_type_name);

                properties.add(property);

            }

            rs.close();
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

        return properties;
    }

    public static ObservableList<SimpleTableObject> downloadMaterialPropertyTypes(HikariDataSource ds) {

        //Create list
        ObservableList<SimpleTableObject> types = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM MaterialPropertyTypes";

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

                SimpleIntegerProperty property_type_id;
                SimpleStringProperty property_type_name;

                property_type_id = new SimpleIntegerProperty(rs.getInt("PropertyTypeID"));
                property_type_name = new SimpleStringProperty(rs.getString("PropertyTypeName"));

                SimpleTableObject property = new SimpleTableObject(null, property_type_id, property_type_name);

                types.add(property);

            }

            rs.close();
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
        return types;
    }

    public static ObservableList<SimpleTableObject> downloadCommonCustomerProperties(HikariDataSource ds) {

        //Create list
        ObservableList<SimpleTableObject> properties = FXCollections.observableArrayList();

        //Create query
        String query = "select * from CommonCustomerProperties ORDER BY PropertyID";

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

                SimpleIntegerProperty property_id, property_type_id;
                SimpleStringProperty property_type_name;

                property_id = new SimpleIntegerProperty(rs.getInt("PropertyID"));
                property_type_id = new SimpleIntegerProperty(rs.getInt("PropertyTypeID"));
                property_type_name = new SimpleStringProperty(rs.getString("PropertyName"));

                SimpleTableObject obj = new SimpleTableObject(property_id, property_type_id, property_type_name);

                properties.add(obj);

            }

            rs.close();
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

        return properties;
    }

    public static ObservableList<SimpleTableObject> downloadCustomerPropertyTypes(HikariDataSource ds) {

        //Create list
        ObservableList<SimpleTableObject> types = FXCollections.observableArrayList();

        //Create query
        String query = "select * from CustomerPropertyTypes ORDER BY PropertyTypeID";

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

                SimpleIntegerProperty property_id, property_type_id;
                SimpleStringProperty property_type_name;

                property_id = new SimpleIntegerProperty(rs.getInt("PropertyTypeID"));
                property_type_name = new SimpleStringProperty(rs.getString("PropertyTypeName"));

                SimpleTableObject obj = new SimpleTableObject(property_id, null, property_type_name);

                types.add(obj);

            }

            rs.close();
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

        return types;
    }

    public static ObservableList<SimpleTableObject> downloadOrderStatusTable(){

        ObservableList<SimpleTableObject> orderStatuses = FXCollections.observableArrayList();

        SimpleTableObject cancelled = new SimpleTableObject(new SimpleIntegerProperty(1), null, new SimpleStringProperty("Cancelled"));
        SimpleTableObject notSold = new SimpleTableObject(new SimpleIntegerProperty(2), null, new SimpleStringProperty("Not Sold"));
        SimpleTableObject sold = new SimpleTableObject(new SimpleIntegerProperty(3), null, new SimpleStringProperty("Sold"));

        orderStatuses.add(cancelled);
        orderStatuses.add(notSold);
        orderStatuses.add(sold);

        return orderStatuses;

    }

    //this one will scan list of properties provided in "properties" input variable and remove all entries that does not
    //match type.
    public static ObservableList<SimpleTableObject> getListOfPropertiesByType(ObservableList<SimpleTableObject> properties, int type){

        ObservableList<SimpleTableObject> filteredProperties = FXCollections.observableArrayList();

        for(SimpleTableObject property : properties) {

            if (property.getPropertyTypeId() == type)filteredProperties.add(property);
        }
        return filteredProperties;
    }

    public SimpleTableObject(SimpleIntegerProperty propertyId, SimpleIntegerProperty propertyTypeId, SimpleStringProperty propertyName) {
        this.propertyId = propertyId;
        this.propertyTypeId = propertyTypeId;
        this.propertyName = propertyName;
    }

    public static SimpleTableObject getSimpleTableObjectByPropId(ObservableList<SimpleTableObject> properties, int propId){

        for (SimpleTableObject property : properties){
            if(property.getPropertyId() == propId)return property;
        }

        return null;
    }

    public static String binarySearch(ObservableList<SimpleTableObject> listOfProperties, int propertyId){
        String country = "Unknown";

        int max = listOfProperties.size() - 1;
        int min = 0;
        int index = 0;

        while (min <= max) {
            int mid = (min + max) / 2;
            if (mid < propertyId) {
                min = mid + 1;
            } else if (mid > propertyId) {
                max = mid - 1;
            } else if (mid == propertyId) {
                index = mid;
                break;
            }
        }

        return listOfProperties.get(index).getPropertyName();
    }








    public int getPropertyId() {
        return propertyId.get();
    }

    public SimpleIntegerProperty propertyIdProperty() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId.set(propertyId);
    }

    public int getPropertyTypeId() {
        return propertyTypeId.get();
    }

    public SimpleIntegerProperty propertyTypeIdProperty() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(int propertyTypeId) {
        this.propertyTypeId.set(propertyTypeId);
    }

    public String getPropertyName() {
        return propertyName.get();
    }

    public SimpleStringProperty propertyNameProperty() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName.set(propertyName);
    }
}
