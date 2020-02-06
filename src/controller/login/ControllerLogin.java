package controller.login;


import classes.Cost;
import classes.PrintedAPI;
import com.zaxxer.hikari.HikariDataSource;
import controller.main.ControllerMain;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable {

    @FXML
    private TextField ipAddress, uName;
    @FXML
    private PasswordField uPasswd;
    @FXML
    private Button btnLogin;

    private final HikariDataSource ds = new HikariDataSource();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnLogin.setOnAction(event -> {
            try {

                PrintedAPI.closeWindow(btnLogin);

                ds.setJdbcUrl("jdbc:mariadb://" + ipAddress.getText() + ":3306/" + uName.getText());
                ds.setUsername(uName.getText());
                ds.setPassword(uPasswd.getText());
                ds.addDataSourceProperty("cachePrepStmts", "true");
                ds.addDataSourceProperty("prepStmtCacheSize", "250");
                ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                ds.setMaxLifetime(1800000);
                ds.setMaximumPoolSize(5);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("resources/view/main/ViewMain.fxml"));
                Parent root = fxmlLoader.load();

                ControllerMain ctrlMain = fxmlLoader.getController();
                ctrlMain.setDataSource(ds);

                Stage stage = new Stage();
                stage.setTitle("3D PrintED");
                stage.setScene(new Scene(root, 1139, 680));
                stage.setMaximized(true);
                stage.show();

                PrintedAPI.serviceStart(ctrlMain.getServiceDownloadAllTables());
                //ctrlMain.displayAllTables();

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

    }
}