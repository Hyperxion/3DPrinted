package controller.create;

import classes.Customer;
import classes.PrintedAPI;
import classes.SimpleTableObject;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ControllerCreateCustomer implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Customer newCust;

    @FXML
    private Label labelTitle, labelInfo, labelId;

    @FXML
    private TextField txtFieldFirstName, txtFieldComment, txtFieldLastName, txtFieldPhone, txtFieldMail, txtFieldAddress, txtFieldCity, txtFieldZipCode;

    @FXML
    private Button btnCreate, btnCancel;

    @FXML
    private ComboBox<SimpleTableObject> comboBoxCountry, comboBoxCompany;

    @FXML
    private DatePicker datePickerDateCreated;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnCreate.setOnAction(event -> {
            if (createCustomer()){
                Customer.insertUpdateCust(newCust, ds);
                PrintedAPI.closeWindow(btnCreate);
                controllerMain.getListOfCustomers().add(0, newCust);
                controllerMain.getCustTv().refresh();
                controllerMain.calculateAllStatistics();
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean createCustomer(){
        try {
            //Database table columns
            SimpleStringProperty lastName, firstName, dateCreated, mail, phone, address, city, zipCode, comment;
            SimpleIntegerProperty id, companyId, countryId;

            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldFirstName, txtFieldLastName, txtFieldPhone, txtFieldMail, txtFieldAddress, txtFieldCity, txtFieldZipCode);

            if (areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            id = new SimpleIntegerProperty(Integer.parseInt(labelId.getText()));
            companyId = new SimpleIntegerProperty(comboBoxCompany.getValue().getPropertyId());
            countryId = new SimpleIntegerProperty(comboBoxCountry.getValue().getPropertyId());

            firstName = new SimpleStringProperty(txtFieldFirstName.getText());
            lastName = new SimpleStringProperty(txtFieldLastName.getText());
            dateCreated = new SimpleStringProperty(datePickerDateCreated.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            mail = new SimpleStringProperty(txtFieldMail.getText());
            phone = new SimpleStringProperty(txtFieldPhone.getText());
            address = new SimpleStringProperty(txtFieldAddress.getText());
            city = new SimpleStringProperty(txtFieldCity.getText());
            zipCode = new SimpleStringProperty(txtFieldZipCode.getText());
            comment = new SimpleStringProperty(txtFieldComment.getText());

            newCust = new Customer(lastName, firstName, dateCreated, mail, phone, address, city, zipCode, comment, id, companyId, countryId);
            return true;

        } catch (NumberFormatException e) {
            //e.printStackTrace();
            labelInfo.setText("Wrong number format! Please, check your values!");
            labelInfo.setTextFill(Color.web("#ff0000"));
            return false;
        }
    }

    public void setDs(HikariDataSource ds) {
        this.ds = ds;
    }

    public void setFieldsValues() {

        int id = PrintedAPI.getCurrentAutoIncrementValue(ds, "Customers");
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText("New Customer");
        datePickerDateCreated.setValue(LocalDate.now());

        ObservableList<SimpleTableObject> companies = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonCustomerProperties(), 1);
        ObservableList<SimpleTableObject> countries = SimpleTableObject.getListOfPropertiesByType(controllerMain.getCommonCustomerProperties(), 2);

        comboBoxCompany.setItems(companies);
        comboBoxCompany.setVisibleRowCount(7);
        comboBoxCompany.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxCompany.setValue(companies.get(0));

        comboBoxCountry.setItems(countries);
        comboBoxCountry.setVisibleRowCount(7);
        comboBoxCountry.setConverter(new StringConverter<SimpleTableObject>() {
            @Override
            public String toString(SimpleTableObject object) {
                return object.getPropertyName();
            }

            @Override
            public SimpleTableObject fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        comboBoxCountry.setValue(countries.get(0));

    }

    public void setControllerMain(ControllerMain controllerMain) {
        this.controllerMain = controllerMain;
    }

    public Button getBtnCreate() {
        return btnCreate;
    }
}
