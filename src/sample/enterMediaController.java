package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class enterMediaController implements Initializable {

    public String DB_URL = "Jdbc:mysql://cougarlibrary.cyt69uqxe34i.us-east-2.rds.amazonaws.com?useSSL=false";
    public String userName = "amar";
    public String password = "Cougar2020";

    public TextField mediaTitleTxt;
    public TextField mediaDirectorTxt;

    int cookieAccountID;

    public void passData(int cookieAccountID) {
        this.cookieAccountID = cookieAccountID;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void enterMedia(ActionEvent actionEvent) {

        String mediaTitle = mediaTitleTxt.getText();
        String director = mediaDirectorTxt.getText();
        int numOfMediaEntered = 0;

        ResultSet rs = null;
        PreparedStatement stmt = null;

        try {

            Connection conn = DriverManager.getConnection(DB_URL, userName, password);

            String sql0 = "SELECT NumOfMediaEntered FROM COUGARLIBRARY.ADMIN WHERE ACCOUNT_ID = ?";
            String sql1 = "insert into COUGARLIBRARY.MEDIA (TITLE, DIRECTOR, entrant_id) values (?, ?, ?);";
            String sql2 = "update COUGARLIBRARY.ADMIN set NumOfMediaEntered = ? where ACCOUNT_ID = ?;";

            stmt = conn.prepareStatement(sql0);
            stmt.setInt(1,cookieAccountID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                numOfMediaEntered = rs.getInt("NumOfMediaEntered");
            }
            rs.close();

            stmt = conn.prepareStatement(sql1);
            stmt.setString(1, mediaTitle);
            stmt.setString(2, director);
            stmt.setInt(3, cookieAccountID);
            stmt.executeUpdate();

            stmt=conn.prepareStatement(sql2);
            stmt.setInt(1,(numOfMediaEntered+1));
            stmt.setInt(2, cookieAccountID);
            stmt.executeUpdate();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("adminHome.fxml"));
            Parent GUI = loader.load();
            Scene scene = new Scene(GUI);
            adminHomeController controller = loader.getController();
            controller.passData(cookieAccountID);
            Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

    }
}
