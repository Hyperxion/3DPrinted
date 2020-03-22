package controller.edit;

import classes.Customer;
import classes.PrintedAPI;
import classes.SimpleTableObject;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
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

public class ControllerEditCustomer implements Initializable {

    private HikariDataSource ds;
    private ControllerMain controllerMain;

    private Customer editedCust;

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
            if (editCustomer()){
                Customer.insertUpdateCust(editedCust, ds);
                PrintedAPI.closeWindow(btnCreate);
                controllerMain.getCustTv().refresh();
            }
        });

        btnCancel.setOnAction(event -> PrintedAPI.closeWindow(btnCancel));

    }

    public boolean editCustomer(){
        try {
            //Database table columns
            String lastName, firstName, dateCreated, mail, phone, address, city, zipCode, comment;
            int id, companyId, countryId;

            boolean areTxtFieldsEmpty = PrintedAPI.areTxtFieldsEmpty(txtFieldFirstName, txtFieldLastName, txtFieldPhone, txtFieldMail, txtFieldAddress, txtFieldCity, txtFieldZipCode);

            if (areTxtFieldsEmpty){
                labelInfo.setText("Fields cannot be empty.");
                labelInfo.setTextFill(Color.web("#ff0000"));
                return false;
            }

            PrintedAPI.checkApostrophe(txtFieldComment);

            id = Integer.parseInt(labelId.getText());
            companyId = comboBoxCompany.getValue().getPropertyId();
            countryId = comboBoxCountry.getValue().getPropertyId();

            firstName = txtFieldFirstName.getText();
            lastName = txtFieldLastName.getText();
            dateCreated = datePickerDateCreated.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            mail = txtFieldMail.getText();
            phone = txtFieldPhone.getText();
            address = txtFieldAddress.getText();
            city = txtFieldCity.getText();
            zipCode = txtFieldZipCode.getText();
            comment = txtFieldComment.getText();

            editedCust.setLastName(lastName);
            editedCust.setFirstName(firstName);
            editedCust.setDateCreated(dateCreated);
            editedCust.setMail(mail);
            editedCust.setPhone(phone);
            editedCust.setAddress(address);
            editedCust.setCity(city);
            editedCust.setZipCode(zipCode);
            editedCust.setComment(comment);
            editedCust.setCompany(comboBoxCompany.getValue().getPropertyName());
            editedCust.setCompanyId(companyId);
            editedCust.setCountry(comboBoxCountry.getValue().getPropertyName());
            editedCust.setCountryId(countryId);

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

    public void setFieldsValues(Customer cust) {
        editedCust = cust;

        int id = cust.getId();
        labelId.setText(String.valueOf(id));
        labelInfo.setTextFill(Color.web("#139e26"));
        labelTitle.setText(cust.getId() + ";" + cust.getLastName() + " " + cust.getFirstName());
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

        for (SimpleTableObject company : companies) {
            if (company.getPropertyId() == cust.getCompanyId()){
                comboBoxCompany.setValue(company);
                break;
            }
        }

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

        for (SimpleTableObject country : countries) {
            if (country.getPropertyId() == cust.getCountryId()){
                comboBoxCountry.setValue(country);
                break;
            }
        }

        txtFieldFirstName.setText(editedCust.getFirstName());
        txtFieldLastName.setText(editedCust.getLastName());
        txtFieldPhone.setText(editedCust.getPhone());
        txtFieldMail.setText(editedCust.getMail());
        txtFieldAddress.setText(editedCust.getAddress());
        txtFieldCity.setText(editedCust.getCity());
        txtFieldZipCode.setText(editedCust.getZipCode());
        datePickerDateCreated.setValue(LocalDate.parse(editedCust.getDateCreated()));
        txtFieldComment.setText(editedCust.getComment());

    }

    public void setControllerMain(ControllerMain controllerMain) {
        this.controllerMain = controllerMain;
    }

    public Button getBtnCreate() {
        return btnCreate;
    }
}
