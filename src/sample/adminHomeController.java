package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class adminHomeController implements Initializable {

    public String DB_URL = "Jdbc:mysql://cougarlibrary.cyt69uqxe34i.us-east-2.rds.amazonaws.com?useSSL=false";
    public String userName = "amar";
    public String password = "Cougar2020";

    public Label numBooksLabel;
    public Label numMediaLabel;
    public Label numItemsLabel;
    public Label nameLabel;
    public Label cookie;


    int cookieAccountID;

    public void passData(int cookieAccountID) {
        this.cookieAccountID = cookieAccountID;
        cookie.setText(String.valueOf(cookieAccountID));

        try {
            Connection conn = DriverManager.getConnection(DB_URL, userName, password);
            ResultSet rs = null;
            PreparedStatement stmt = null;

            stmt = conn.prepareStatement("select * from COUGARLIBRARY.ADMIN \n" +
                    "inner join COUGARLIBRARY.ACCOUNTS \n" +
                    "on ACCOUNTS.account_id=ADMIN.account_id\n" +
                    "where ADMIN.ACCOUNT_ID=?;");
            stmt.setInt(1, cookieAccountID);
            rs=stmt.executeQuery();

            while (rs.next()) {
                nameLabel.setText(rs.getString("USER_NAME"));
                numMediaLabel.setText(String.valueOf(rs.getInt("NumOfMediaEntered")));
                numBooksLabel.setText(String.valueOf(rs.getInt("NumOfBooksEntered")));
                int numItems = rs.getInt("NumOfMediaEntered") + rs.getInt("NumOfBooksEntered");
                numItemsLabel.setText(String.valueOf(numItems));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void viewBooks(ActionEvent actionEvent) {
    }

    public void viewMedia(ActionEvent actionEvent) {
    }


    public void addBook(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("enterBookForm.fxml"));
        Parent GUI = loader.load();
        Scene scene = new Scene(GUI);
        enterBookController controller = loader.getController();
        controller.passData(Integer.parseInt(cookie.getText()));
        Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void addMedia(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("enterMediaForm.fxml"));
        Parent GUI = loader.load();
        Scene scene = new Scene(GUI);
        enterMediaController controller = loader.getController();
        controller.passData(Integer.parseInt(cookie.getText()));
        Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
