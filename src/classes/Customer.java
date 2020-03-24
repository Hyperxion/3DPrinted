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

public class Customer {

    //Database table columns
    private SimpleStringProperty lastName, firstName, dateCreated, mail, phone, address, city, zipCode, comment;
    private SimpleIntegerProperty id, companyId, countryId;

    //Additional fields
    private SimpleStringProperty country, company;
    private SimpleIntegerProperty orderCount;
    private SimpleDoubleProperty ordersPrice;

    public static ObservableList<Customer> downloadCustomersTable(HikariDataSource ds){

        //Create list
        ObservableList<Customer> customersList = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM Customers ORDER BY CustomerID DESC";

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

                //Database table columns
                SimpleStringProperty lastName, firstName, dateCreated, mail, phone, address, city, zipCode, comment;
                SimpleIntegerProperty id, companyId, countryId;

                lastName = new SimpleStringProperty(rs.getString("LastName"));
                firstName = new SimpleStringProperty(rs.getString("FirstName"));
                dateCreated = new SimpleStringProperty(rs.getString("DateCreated"));
                comment = new SimpleStringProperty(rs.getString("Comment"));
                mail = new SimpleStringProperty(rs.getString("Mail"));
                phone = new SimpleStringProperty(rs.getString("Phone"));
                address = new SimpleStringProperty(rs.getString("Address"));
                city = new SimpleStringProperty(rs.getString("City"));
                zipCode = new SimpleStringProperty(rs.getString("ZipCode"));

                id = new SimpleIntegerProperty(rs.getInt("CustomerID"));
                companyId = new SimpleIntegerProperty(rs.getInt("CompanyID"));
                countryId = new SimpleIntegerProperty(rs.getInt("CountryID"));

                Customer customer = new Customer(lastName, firstName, dateCreated, mail, phone, address, city, zipCode, comment, id, companyId, countryId);

                customersList.add(customer);
            }

            rs.close();
        } catch (NullPointerException | SQLNonTransientConnectionException | ClassNotFoundException e){
            //signIn(event);
            e.printStackTrace();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } //Handle errors for Class.forName
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


        return customersList;
    }

    public static void insertUpdateCust(Customer customer, HikariDataSource ds){

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

            updateQuery = "INSERT INTO Customers (CustomerID,FirstName,LastName,DateCreated,Comment,Phone,Address,City,Mail,ZipCode,CountryID,CompanyID)" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)" +
                    "ON DUPLICATE KEY UPDATE CustomerID=?,FirstName=?,LastName=?,DateCreated=?,Comment=?,Phone=?,Address=?,City=?,Mail=?,ZipCode=?,CountryID=?,CompanyID=?";
            stmt = conn.prepareStatement(updateQuery);

            int i = 0;

            stmt.setInt(++i, customer.getId());
            stmt.setString(++i, customer.getFirstName());
            stmt.setString(++i, customer.getLastName());
            stmt.setString(++i, customer.getDateCreated());
            stmt.setString(++i, customer.getComment());
            stmt.setString(++i, customer.getPhone());
            stmt.setString(++i, customer.getAddress());
            stmt.setString(++i, customer.getCity());
            stmt.setString(++i, customer.getMail());
            stmt.setString(++i, customer.getZipCode());
            stmt.setInt(++i, customer.getCountryId());
            stmt.setInt(++i, customer.getCompanyId());

            stmt.setInt(++i, customer.getId());
            stmt.setString(++i, customer.getFirstName());
            stmt.setString(++i, customer.getLastName());
            stmt.setString(++i, customer.getDateCreated());
            stmt.setString(++i, customer.getComment());
            stmt.setString(++i, customer.getPhone());
            stmt.setString(++i, customer.getAddress());
            stmt.setString(++i, customer.getCity());
            stmt.setString(++i, customer.getMail());
            stmt.setString(++i, customer.getZipCode());
            stmt.setInt(++i, customer.getCountryId());
            stmt.setInt(++i, customer.getCompanyId());

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

    public static void deleteCustomers(ObservableList<Customer> customers, HikariDataSource ds){
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

            for (Customer customer : customers){
                updateQuery = "DELETE FROM Customers WHERE CustomerID =  " + customer.getId();
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

    public Customer(SimpleStringProperty lastName, SimpleStringProperty firstName, SimpleStringProperty dateCreated, SimpleStringProperty mail, SimpleStringProperty phone, SimpleStringProperty address, SimpleStringProperty city, SimpleStringProperty zipCode, SimpleStringProperty comment, SimpleIntegerProperty id, SimpleIntegerProperty companyId, SimpleIntegerProperty countryId) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.dateCreated = dateCreated;
        this.mail = mail;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.comment = comment;
        this.id = id;
        this.companyId = companyId;
        this.countryId = countryId;
        this.country = new SimpleStringProperty("null");
        this.company = new SimpleStringProperty("null");
        this.orderCount = new SimpleIntegerProperty(0);
        this.ordersPrice = new SimpleDoubleProperty(0);
    }

    public Customer(SimpleIntegerProperty customerId) {
        this.lastName = new SimpleStringProperty();
        this.firstName = new SimpleStringProperty();
        this.dateCreated = new SimpleStringProperty();
        this.mail = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.address = new SimpleStringProperty();
        this.city = new SimpleStringProperty();
        this.zipCode = new SimpleStringProperty();
        this.comment = new SimpleStringProperty();
        this.id = customerId;
        this.companyId = new SimpleIntegerProperty();
        this.countryId = new SimpleIntegerProperty();
        this.country = new SimpleStringProperty("null");
        this.company = new SimpleStringProperty("null");
        this.orderCount = new SimpleIntegerProperty(0);
        this.ordersPrice = new SimpleDoubleProperty(0);
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
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

    public String getMail() {
        return mail.get();
    }

    public SimpleStringProperty mailProperty() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail.set(mail);
    }

    public String getPhone() {
        return phone.get();
    }

    public SimpleStringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getCity() {
        return city.get();
    }

    public SimpleStringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public String getZipCode() {
        return zipCode.get();
    }

    public SimpleStringProperty zipCodeProperty() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode.set(zipCode);
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

    public int getCompanyId() {
        return companyId.get();
    }

    public SimpleIntegerProperty companyIdProperty() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId.set(companyId);
    }

    public int getCountryId() {
        return countryId.get();
    }

    public SimpleIntegerProperty countryIdProperty() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId.set(countryId);
    }

    public String getCountry() {
        return country.get();
    }

    public SimpleStringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    public String getCompany() {
        return company.get();
    }

    public SimpleStringProperty companyProperty() {
        return company;
    }

    public void setCompany(String company) {
        this.company.set(company);
    }

    public int getOrderCount() {
        return orderCount.get();
    }

    public SimpleIntegerProperty orderCountProperty() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount.set(orderCount);
    }

    public double getOrdersPrice() {
        return ordersPrice.get();
    }

    public SimpleDoubleProperty ordersPriceProperty() {
        return ordersPrice;
    }

    public void setOrdersPrice(double ordersPrice) {
        this.ordersPrice.set(ordersPrice);
    }


}
