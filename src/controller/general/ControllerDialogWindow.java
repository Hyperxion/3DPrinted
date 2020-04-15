package controller.general;

import classes.*;
import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.lang.Object;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerDialogWindow implements Initializable {

    /*
    this is flag which determines what message to display and what procedure to perform
    we dont want to create multiple controllers just because we have to delete different items from
    different tables. We can do it via flag instead.

    0 = Cost (default) - we, however, dont need this because cost is related to one table only
    1 = Printer
    2 = Material
    3 = Object
    4 = Customer
    5 = Order
     */
    private int flag = 0;

    private ObservableList<Printer> printers;
    private ObservableList<Material> material;
    private ObservableList<Object> objects;
    private ObservableList<Customer> customers;
    private ObservableList<Order> orders;
    private ObservableList<OrderItem> orderItems;

    private HikariDataSource ds;

    @FXML
    private Label labelTitle, labelText;

    @FXML
    private Button btnConfirm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnConfirm.setOnAction(event -> {
            PrintedAPI.closeWindow(btnConfirm);
        });

    }

    private void safeDelete(int index) {
        switch (flag) {
            //Printer
            case 1:
                Printer.safeDeletePrinters(printers, ds);
                break;
            //Material
            case 2:
                break;
            //Object
            case 3:
                break;
            //Customer
            case 4:
                break;
            //Order
            case 5:
                break;
            //Cost
            default:
        }
    }

    public Label getLabelTitle() {
        return labelTitle;
    }

    public void setLabelTitle(Label labelTitle) {
        this.labelTitle = labelTitle;
    }

    public Label getLabelText() {
        return labelText;
    }

    public void setLabelText(int index) {
        switch (flag) {
            //Printer
            case 1:
                labelText.setText("You are trying to delete printer " + printers.get(index).getName() +  "with ID:" + printers.get(index).getId() + ". This will remove all it's occurrences in other tables. Do you wish to proceed?");
                break;
            //Material
            case 2:
                break;
            //Object
            case 3:
                break;
            //Customer
            case 4:
                break;
            //Order
            case 5:
                break;
            //Cost
            default:

        }
    }

    public Button getBtnConfirm() {
        return btnConfirm;
    }

    public void setPrinters(ObservableList<Printer> printers) {
        this.printers = printers;
    }

    public void setMaterial(ObservableList<Material> material) {
        this.material = material;
    }

    public void setObjects(ObservableList<Object> objects) {
        this.objects = objects;
    }

    public void setCustomers(ObservableList<Customer> customers) {
        this.customers = customers;
    }

    public void setOrders(ObservableList<Order> orders) {
        this.orders = orders;
    }
}
