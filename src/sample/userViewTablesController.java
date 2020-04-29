package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.classes.Book;
import sample.classes.Media;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class userViewTablesController implements Initializable {

    public String DB_URL = "Jdbc:mysql://cougarlibrary.cyt69uqxe34i.us-east-2.rds.amazonaws.com?useSSL=false";
    public String userName = "amar";
    public String password = "Cougar2020";

    public TableView<Book> bookTable;
    public TableColumn<Book, Integer> bookIDCol;
    public TableColumn<Book, Integer> BissueIDCol;
    public TableColumn<Book, String> bookTitleCol;
    public TableColumn<Book, String> bookAuthorCol;
    public TableColumn<Book, Date> bookCdateCol;
    public TableColumn<Book, Date> bookDdateCol;
    public ObservableList<Book> bookList = FXCollections.observableArrayList();


    public TableView<Media> mediaTable;
    public TableColumn<Media, Integer> mediaIDCol;
    public TableColumn<Media, Integer> MissueIDCol;
    public TableColumn<Media, String> mediaTitleCol;
    public TableColumn<Media, String> mediaDirectorCol;
    public TableColumn<Media, Date> mediaCdateCol;
    public TableColumn<Media, Date> mediaDdateCol;
    public ObservableList<Media> mediaList = FXCollections.observableArrayList();

    int cookieAccountID;

    public void passData(int cookieAccountID) {
        this.cookieAccountID = cookieAccountID;

        bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        BissueIDCol.setCellValueFactory(new PropertyValueFactory<>("issueID"));
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookCdateCol.setCellValueFactory(new PropertyValueFactory<>("checkoutDate"));
        bookDdateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        bookTable.setItems(bookList);

        mediaIDCol.setCellValueFactory(new PropertyValueFactory<>("mediaID"));
        MissueIDCol.setCellValueFactory(new PropertyValueFactory<>("issueID"));
        mediaTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        mediaDirectorCol.setCellValueFactory(new PropertyValueFactory<>("director"));
        mediaCdateCol.setCellValueFactory(new PropertyValueFactory<>("checkoutDate"));
        mediaDdateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        mediaTable.setItems(mediaList);

        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        ResultSet rsBook = null;
        ResultSet rsMedia = null;
        String sqlBook = "select * from COUGARLIBRARY.ISSUE_STATUS_BOOKS left join COUGARLIBRARY.BOOKS on COUGARLIBRARY.ISSUE_STATUS_BOOKS.BOOK_ID=COUGARLIBRARY.BOOKS.BOOK_ID where COUGARLIBRARY.ISSUE_STATUS_BOOKS.ISSUER_ACCOUNT_ID = ?;";
        String sqlMedia = "select * from COUGARLIBRARY.ISSUE_STATUS_MEDIA left join COUGARLIBRARY.MEDIA on COUGARLIBRARY.ISSUE_STATUS_MEDIA.MEDIA_ID=COUGARLIBRARY.MEDIA.MEDIA_ID where COUGARLIBRARY.ISSUE_STATUS_MEDIA.ISSUER_ACCOUNT_ID=?;";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, userName, password);

            stmt = conn.prepareStatement(sqlBook);
            stmt.setInt(1, cookieAccountID);
            rsBook = stmt.executeQuery();
            while (rsBook.next()) {
                bookList.add(new Book(rsBook.getInt("BOOK_ID"), rsBook.getInt("ISSUE_ID"), rsBook.getString("TITLE"), rsBook.getString("AUTHOR"), rsBook.getDate("ISSUE_DATE"), rsBook.getDate("DUE_DATE")));
            }

            stmt1 = conn.prepareStatement(sqlMedia);
            stmt1.setInt(1, cookieAccountID);
            rsMedia = stmt1.executeQuery();
            while (rsMedia.next()) {
                mediaList.add(new Media(rsMedia.getInt("MEDIA_ID"), rsMedia.getInt("ISSUE_ID"), rsMedia.getString("TITLE"), rsMedia.getString("DIRECTOR"), rsMedia.getDate("ISSUE_DATE"), rsMedia.getDate("DUE_DATE")));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(cookieAccountID);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void searchBooks(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("bookCatalog.fxml"));
        Parent GUI = loader.load();
        Scene scene = new Scene(GUI);

        bookCatalogController controller = loader.getController();
        controller.passData(cookieAccountID);

        Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void returnBook(ActionEvent actionEvent) {

        ResultSet rs = null;
        PreparedStatement stmt = null;
        String sql0 = "DELETE FROM COUGARLIBRARY.ISSUE_STATUS_BOOKS where issue_id=?;";
        String sql1 = "update COUGARLIBRARY.BOOKS SET ISSUE_STATUS = 'AVAILABLE' WHERE BOOK_ID = ?;";
        String sql2 = "SELECT * FROM COUGARLIBRARY.USERS WHERE ACCOUNT_ID = ?";
        String sql3 = "update COUGARLIBRARY.USERS set NumOfBooksCheckedOut = ? where ACCOUNT_ID = ?;";

        if (bookTable.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Nothing selected.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, userName, password);

                stmt = conn.prepareStatement(sql0);
                stmt.setInt(1, bookTable.getSelectionModel().getSelectedItem().getIssueID());
                stmt.executeUpdate();

                stmt = conn.prepareStatement(sql1);
                stmt.setInt(1, bookTable.getSelectionModel().getSelectedItem().getBookID());
                stmt.executeUpdate();

                int numOfBooksCheckedOut = 0;
                stmt = conn.prepareStatement(sql2);
                stmt.setInt(1, cookieAccountID);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    numOfBooksCheckedOut = rs.getInt("NumOfBooksCheckedOut");
                }

                stmt = conn.prepareStatement(sql3);
                stmt.setInt(1, (numOfBooksCheckedOut - 1));
                stmt.setInt(2, cookieAccountID);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Thank you for returning our book!", "Information Dialog", JOptionPane.INFORMATION_MESSAGE);

                bookList.remove(bookTable.getSelectionModel().getSelectedItem());
                bookTable.refresh();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void searchMedia(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("mediaCatalog.fxml"));
        Parent GUI = loader.load();
        Scene scene = new Scene(GUI);

        mediaCatalogController controller = loader.getController();
        controller.passData(cookieAccountID);

        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void returnMedia(ActionEvent actionEvent) {

        ResultSet rs = null;
        PreparedStatement stmt = null;
        String sql0 = "DELETE FROM COUGARLIBRARY.ISSUE_STATUS_MEDIA where issue_id=?;";
        String sql1 = "update COUGARLIBRARY.MEDIA SET ISSUE_STATUS = 'AVAILABLE' WHERE MEDIA_ID = ?;";
        String sql2 = "SELECT * FROM COUGARLIBRARY.USERS WHERE ACCOUNT_ID = ?";
        String sql3 = "update COUGARLIBRARY.USERS set NumOfMediaCheckedOut = ? where ACCOUNT_ID = ?;";

        if (mediaTable.getSelectionModel().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Nothing selected.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, userName, password);

                stmt = conn.prepareStatement(sql0);
                stmt.setInt(1, mediaTable.getSelectionModel().getSelectedItem().getIssueID());
                stmt.executeUpdate();

                stmt = conn.prepareStatement(sql1);
                stmt.setInt(1, mediaTable.getSelectionModel().getSelectedItem().getMediaID());
                stmt.executeUpdate();

                int numOfMediaCheckedOut = 0;
                stmt = conn.prepareStatement(sql2);
                stmt.setInt(1, cookieAccountID);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    numOfMediaCheckedOut = rs.getInt("NumOfMediaCheckedOut");
                }

                stmt = conn.prepareStatement(sql3);
                stmt.setInt(1, (numOfMediaCheckedOut - 1));
                stmt.setInt(2, cookieAccountID);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Thank you for returning our item!", "Information Dialog", JOptionPane.INFORMATION_MESSAGE);

                mediaList.remove(mediaTable.getSelectionModel().getSelectedItem());
                mediaTable.refresh();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


    public void back2Home(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("userHomePage.fxml"));
        Parent GUI = loader.load();
        Scene scene = new Scene(GUI);
        userHomePageController controller = loader.getController();
        controller.passData(cookieAccountID);
        Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
