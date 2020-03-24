package classes;

import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Material {

    //table columns
    SimpleIntegerProperty id, manufacturerId, typeId, colorId, weightId, sellerId, diameterId;
    SimpleDoubleProperty price, shipping;
    SimpleStringProperty comment, finished, purchaseDate;

    //additional columns
    SimpleStringProperty color, manufacturer, type, seller;
    SimpleDoubleProperty diameter, used, trash, soldFor, profit, remaining, weight;

    public static ObservableList<Material> downloadMaterialsTable(HikariDataSource ds) {

        //Create list
        ObservableList<Material> materials = FXCollections.observableArrayList();

        //Create query
        String query = "SELECT * FROM Materials ORDER BY MaterialID DESC";

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
                SimpleIntegerProperty material_id = new SimpleIntegerProperty(rs.getInt("MaterialID"));
                SimpleIntegerProperty material_id_manufacturer = new SimpleIntegerProperty(rs.getInt("ManufacturerID"));
                SimpleIntegerProperty material_id_materialType = new SimpleIntegerProperty(rs.getInt("MaterialTypeID"));
                SimpleIntegerProperty material_id_color = new SimpleIntegerProperty(rs.getInt("ColorID"));
                SimpleIntegerProperty material_id_weight = new SimpleIntegerProperty(rs.getInt("WeightID"));
                SimpleIntegerProperty material_id_diameter = new SimpleIntegerProperty(rs.getInt("DiameterID"));
                SimpleIntegerProperty material_id_seller = new SimpleIntegerProperty(rs.getInt("SellerID"));

                SimpleDoubleProperty material_price = new SimpleDoubleProperty(rs.getDouble("MaterialPrice"));
                SimpleDoubleProperty material_shipping = new SimpleDoubleProperty(rs.getDouble("MaterialShipping"));
                SimpleDoubleProperty materialTrash = new SimpleDoubleProperty(rs.getDouble("Trash"));


                SimpleStringProperty material_purchaseDate = new SimpleStringProperty(rs.getString("PurchaseDate"));
                SimpleStringProperty material_finished = new SimpleStringProperty(rs.getString("Finished"));
                SimpleStringProperty material_comment = new SimpleStringProperty(rs.getString("Comment"));

                Material material = new Material(material_id, material_id_manufacturer, material_id_materialType, material_id_color, material_id_weight, material_id_seller, material_id_diameter, material_price, material_shipping, materialTrash, material_comment, material_finished, material_purchaseDate);

                materials.add(material);

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

        return materials;
    }

    public static void insertUpdateMaterial(Material material, HikariDataSource ds){

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

            updateQuery = "INSERT INTO Materials (MaterialID,ManufacturerID,MaterialTypeID,ColorID,WeightID,MaterialPrice,MaterialShipping,PurchaseDate,SellerID,Finished,DiameterID,Comment) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE MaterialID=?,ManufacturerID=?,MaterialTypeID=?,ColorID=?,WeightID=?,MaterialPrice=?,MaterialShipping=?,PurchaseDate=?,SellerID=?,Finished=?,DiameterID=?,Comment=?";
            stmt = conn.prepareStatement(updateQuery);

            int i = 0;

            stmt.setInt(++i, material.getId()); //MaterialId
            stmt.setInt(++i, material.getManufacturerId()); //ManufacturerId
            stmt.setInt(++i, material.getTypeId()); //MaterialTypeId
            stmt.setInt(++i, material.getColorId()); //colorId
            stmt.setInt(++i, material.getWeightId());//weightId
            stmt.setDouble(++i, material.getPrice());//price
            stmt.setDouble(++i, material.getShipping());//shipping
            stmt.setString(++i, material.getPurchaseDate());//purchase date
            stmt.setInt(++i, material.getSellerId());//sellerid
            stmt.setString(++i, material.getFinished());//finished
            stmt.setInt(++i, material.getDiameterId());//diameterid
            stmt.setString(++i, material.getComment());//comment

            stmt.setInt(++i, material.getId()); //MaterialId
            stmt.setInt(++i, material.getManufacturerId()); //ManufacturerId
            stmt.setInt(++i, material.getTypeId()); //MaterialTypeId
            stmt.setInt(++i, material.getColorId()); //colorId
            stmt.setInt(++i, material.getWeightId());//weightId
            stmt.setDouble(++i, material.getPrice());//price
            stmt.setDouble(++i, material.getShipping());//shipping
            stmt.setString(++i, material.getPurchaseDate());//purchase date
            stmt.setInt(++i, material.getSellerId());//sellerid
            stmt.setString(++i, material.getFinished());//finished
            stmt.setInt(++i, material.getDiameterId());//diameterid
            stmt.setString(++i, material.getComment());//comment

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

    public static void deleteMaterials(ObservableList<Material> materials, HikariDataSource ds){
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

            for (Material material : materials){
                updateQuery = "DELETE FROM Materials WHERE MaterialID =  " + material.getId();
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

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", manufacturerId=" + manufacturerId +
                ", typeId=" + typeId +
                ", colorId=" + colorId +
                ", weightId=" + weightId +
                ", sellerId=" + sellerId +
                ", diameterId=" + diameterId +
                ", price=" + price +
                ", shipping=" + shipping +
                ", comment=" + comment +
                ", finished=" + finished +
                ", purchaseDate=" + purchaseDate +
                ", color=" + color +
                ", manufacturer=" + manufacturer +
                ", type=" + type +
                ", seller=" + seller +
                ", diameter=" + diameter +
                ", used=" + used +
                ", trash=" + trash +
                ", soldFor=" + soldFor +
                ", profit=" + profit +
                ", remaining=" + remaining +
                ", weight=" + weight +
                '}';
    }

    //simple constructor for database table
    public Material(SimpleIntegerProperty id, SimpleIntegerProperty manufacturerId, SimpleIntegerProperty typeId, SimpleIntegerProperty colorId, SimpleIntegerProperty weightId, SimpleIntegerProperty sellerId, SimpleIntegerProperty diameterId, SimpleDoubleProperty price, SimpleDoubleProperty shipping, SimpleDoubleProperty trash, SimpleStringProperty comment, SimpleStringProperty finished, SimpleStringProperty purchaseDate) {
        this.id = id;
        this.manufacturerId = manufacturerId;
        this.typeId = typeId;
        this.colorId = colorId;
        this.weightId = weightId;
        this.sellerId = sellerId;
        this.diameterId = diameterId;
        this.price = price;
        this.shipping = shipping;
        this.trash = trash;
        this.comment = comment;
        this.finished = finished;
        this.purchaseDate = purchaseDate;

        //we must initialize non-table columns, otherwise are those values null and we cant work with them - null pointer exception is thrown
        this.type = new SimpleStringProperty("Unknown");
        this.color = new SimpleStringProperty("Color");
        this.manufacturer = new SimpleStringProperty("Unknown");
        this.seller = new SimpleStringProperty("Unknown");

        this.diameter = new SimpleDoubleProperty(0);
        this.used = new SimpleDoubleProperty(0);
        this.trash = new SimpleDoubleProperty(0);
        this.soldFor = new SimpleDoubleProperty(0);
        this.profit = new SimpleDoubleProperty(0);
        this.remaining = new SimpleDoubleProperty(0);
        this.weight = new SimpleDoubleProperty(0);
    }

    //complex constructor for table view
    public Material(SimpleIntegerProperty id, SimpleIntegerProperty manufacturerId, SimpleIntegerProperty typeId, SimpleIntegerProperty colorId, SimpleIntegerProperty weightId, SimpleIntegerProperty sellerId, SimpleIntegerProperty diameterId, SimpleDoubleProperty price, SimpleDoubleProperty shipping, SimpleStringProperty comment, SimpleStringProperty finished, SimpleStringProperty purchaseDate, SimpleStringProperty color, SimpleStringProperty manufacturer, SimpleStringProperty type, SimpleStringProperty seller, SimpleDoubleProperty diameter, SimpleDoubleProperty used, SimpleDoubleProperty trash, SimpleDoubleProperty soldFor, SimpleDoubleProperty profit, SimpleDoubleProperty remaining, SimpleDoubleProperty weight) {
        this.id = id;
        this.manufacturerId = manufacturerId;
        this.typeId = typeId;
        this.colorId = colorId;
        this.weightId = weightId;
        this.sellerId = sellerId;
        this.diameterId = diameterId;
        this.price = price;
        this.shipping = shipping;
        this.comment = comment;
        this.finished = finished;
        this.purchaseDate = purchaseDate;
        this.color = color;
        this.manufacturer = manufacturer;
        this.type = type;
        this.seller = seller;
        this.diameter = diameter;
        this.used = used;
        this.trash = trash;
        this.soldFor = soldFor;
        this.profit = profit;
        this.remaining = remaining;
        this.weight = weight;
    }

    //constructor for creating material object from information stored in orderItem
    public Material(SimpleIntegerProperty matId) {
        this.id = matId;
//        this.manufacturerId = null;
//        this.typeId = null;
//        this.colorId = null;
//        this.weightId = null;
//        this.sellerId = null;
//        this.diameterId = null;
//        this.price = new SimpleDoubleProperty(0);
//        this.shipping = null;
//        this.comment = null;
//        this.finished = null;
//        this.purchaseDate = null;
//        this.color = null;
//        this.manufacturer = null;
        this.type = new SimpleStringProperty("Unknown Type");
//        this.seller = null;
//        this.diameter = null;
//        this.used = null;
//        this.trash = null;
//        this.soldFor = null;
//        this.profit = null;
//        this.remaining = null;
//        this.weight = null;
    }

    public static Material getMaterialById(ObservableList<Material> listOfMaterials, int id){

        for (Material mat : listOfMaterials){
            if (mat.getId() == id)return mat;
        }
        return null;
    }

    public boolean isSold(){
        if(this.getFinished() == "Sold")return true;

        return false;
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

    public int getManufacturerId() {
        return manufacturerId.get();
    }

    public SimpleIntegerProperty manufacturerIdProperty() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId.set(manufacturerId);
    }

    public int getTypeId() {
        return typeId.get();
    }

    public SimpleIntegerProperty typeIdProperty() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId.set(typeId);
    }

    public int getColorId() {
        return colorId.get();
    }

    public SimpleIntegerProperty colorIdProperty() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId.set(colorId);
    }

    public int getWeightId() {
        return weightId.get();
    }

    public SimpleIntegerProperty weightIdProperty() {
        return weightId;
    }

    public void setWeightId(int weightId) {
        this.weightId.set(weightId);
    }

    public int getSellerId() {
        return sellerId.get();
    }

    public SimpleIntegerProperty sellerIdProperty() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId.set(sellerId);
    }

    public int getDiameterId() {
        return diameterId.get();
    }

    public SimpleIntegerProperty diameterIdProperty() {
        return diameterId;
    }

    public void setDiameterId(int diameterId) {
        this.diameterId.set(diameterId);
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

    public double getShipping() {
        return shipping.get();
    }

    public SimpleDoubleProperty shippingProperty() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping.set(shipping);
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

    public String getFinished() {
        return finished.get();
    }

    public SimpleStringProperty finishedProperty() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished.set(finished);
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

    public String getColor() {
        return color.get();
    }

    public SimpleStringProperty colorProperty() {
        return color;
    }

    public void setColor(String color) {
        this.color.set(color);
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public SimpleStringProperty manufacturerProperty() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer.set(manufacturer);
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

    public String getSeller() {
        return seller.get();
    }

    public SimpleStringProperty sellerProperty() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller.set(seller);
    }

    public double getDiameter() {
        return diameter.get();
    }

    public SimpleDoubleProperty diameterProperty() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter.set(diameter);
    }

    public double getUsed() {
        return used.get();
    }

    public SimpleDoubleProperty usedProperty() {
        return used;
    }

    public void setUsed(double used) {
        this.used.set(used);
    }

    public double getTrash() {
        return trash.get();
    }

    public SimpleDoubleProperty trashProperty() {
        return trash;
    }

    public void setTrash(double trash) {
        this.trash.set(trash);
    }

    public double getSoldFor() {
        return soldFor.get();
    }

    public SimpleDoubleProperty soldForProperty() {
        return soldFor;
    }

    public void setSoldFor(double soldFor) {
        this.soldFor.set(soldFor);
    }

    public double getProfit() {
        return profit.get();
    }

    public SimpleDoubleProperty profitProperty() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit.set(profit);
    }

    public double getRemaining() {
        return remaining.get();
    }

    public SimpleDoubleProperty remainingProperty() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining.set(remaining);
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
}
