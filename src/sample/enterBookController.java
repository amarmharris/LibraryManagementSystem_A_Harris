package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class enterBookController implements Initializable {

    public String DB_URL = "Jdbc:mysql://cougarlibrary.cyt69uqxe34i.us-east-2.rds.amazonaws.com?useSSL=false";
    public String userName = "amar";
    public String password = "Cougar2020";

    public TextField bookTitleTxt;
    public TextField bookAuthorTxt;
    public ImageView imageView;

    //components to gather image
    public FileInputStream dbImage;
    public FileChooser fileChooser;
    public File file;
    public Image image;

    int cookieAccountID;

    public void passData(int cookieAccountID) {
        this.cookieAccountID = cookieAccountID;
    }

        @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void enterBook(ActionEvent actionEvent) {

        String bookName = bookTitleTxt.getText();
        String bookAuthor = bookAuthorTxt.getText();
        int numOfBooksEntered = 0;

        ResultSet rs = null;
        PreparedStatement stmt = null;

        if (validateText()) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, userName, password);

                String sql0 = "SELECT NumOfBooksEntered FROM COUGARLIBRARY.ADMIN WHERE ACCOUNT_ID = ?";
                String sql1 = "insert into COUGARLIBRARY.BOOKS (title, author, entrant_id) values (?, ?, ?);";
                String sql2 = "update COUGARLIBRARY.ADMIN set NumOfBooksEntered = ? where ACCOUNT_ID = ?;";

                stmt = conn.prepareStatement(sql0);
                stmt.setInt(1, cookieAccountID);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    numOfBooksEntered = rs.getInt("NumOfBooksEntered");
                }
                rs.close();

                stmt = conn.prepareStatement(sql1);
                stmt.setString(1, bookName);
                stmt.setString(2, bookAuthor);
                stmt.setInt(3, cookieAccountID);
                stmt.executeUpdate();

                stmt = conn.prepareStatement(sql2);
                stmt.setInt(1, (numOfBooksEntered + 1));
                stmt.setInt(2, cookieAccountID);
                stmt.executeUpdate();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("adminHome.fxml"));
                Parent GUI = loader.load();
                Scene scene = new Scene(GUI);
                adminHomeController controller = loader.getController();
                controller.passData(cookieAccountID);
                Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean validateText() {
        if (bookTitleTxt.getText().isEmpty() || bookAuthorTxt.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validate Fields");
            alert.setHeaderText(null);
            alert.setContentText("Please fill out all the fields");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public void back2Home(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("adminHome.fxml"));
        Parent GUI = loader.load();
        Scene scene = new Scene(GUI);
        adminHomeController controller = loader.getController();
        controller.passData(cookieAccountID);
        Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
