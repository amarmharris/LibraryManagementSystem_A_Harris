package sample;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.classes.Book;
import sample.classes.Media;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;

public class viewTablesPageController implements Initializable {

    public String DB_URL = "Jdbc:mysql://cougarlibrary.cyt69uqxe34i.us-east-2.rds.amazonaws.com?useSSL=false";
    public String userName = "amar";
    public String password = "Cougar2020";

    //public TabPane tabPane;
    public Tab mediaPage;
    public TableView<Media> mediaTable;
    public TableColumn<Media, Integer> mediaIDCol;
    public TableColumn<Media, String> mediaTitleCol;
    public TableColumn<Media, String> directorCol;
    public TableColumn<Media, Date> mediaDoeCol;
    public TableColumn<Media, String> mediaStatusCol;
    public ObservableList<Media> mediaList = FXCollections.observableArrayList();
    public JFXTextField mediaSearchTxt;

    public Tab booksPage;
    public TableView<Book> bookTable;
    public TableColumn<Book, Integer> bookIDCol;
    public TableColumn<Book, String> bookTitleCol;
    public TableColumn<Book, String> authorCol;
    public TableColumn<Book, Date> bookDoeCol;
    public TableColumn<Book, String> bookStatusCol;
    public ObservableList<Book> bookList = FXCollections.observableArrayList();
    public JFXTextField bookSearchTxt;
    public ImageView mediaImageView;
    public ImageView bookImageView;

    int cookieAccountID;

    public void passData(int cookieAccountID) {
        this.cookieAccountID = cookieAccountID;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mediaIDCol.setCellValueFactory(new PropertyValueFactory<>("mediaID"));
        mediaTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        directorCol.setCellValueFactory(new PropertyValueFactory<>("director"));
        mediaDoeCol.setCellValueFactory(new PropertyValueFactory<>("doe"));
        mediaStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        mediaTable.setItems(mediaList);

        bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookDoeCol.setCellValueFactory(new PropertyValueFactory<>("doe"));
        bookStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookTable.setItems(bookList);

        PreparedStatement stmt = null;
        String sqlMedia = "select * from COUGARLIBRARY.MEDIA;";
        ResultSet rsMedia = null;
        String sqlBook = "select * from COUGARLIBRARY.BOOKS;";
        ResultSet rsBook = null;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, userName, password);

            stmt = conn.prepareStatement(sqlMedia);
            rsMedia = stmt.executeQuery();
            while(rsMedia.next()) {
                mediaList.add(new Media(rsMedia.getInt("MEDIA_ID"), rsMedia.getString("TITLE"), rsMedia.getString("DIRECTOR"), rsMedia.getDate("DATE_OF_ENTRY"), rsMedia.getString("ISSUE_STATUS")));
            }

            stmt = conn.prepareStatement(sqlBook);
            rsBook = stmt.executeQuery();
            while(rsBook.next()) {
                bookList.add(new Book(rsBook.getInt("BOOK_ID"), rsBook.getString("TITLE"), rsBook.getString("AUTHOR"), rsBook.getDate("DATE_OF_ENTRY"), rsBook.getString("ISSUE_STATUS")));
            }
            

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        //Filtered list to filter through media
        FilteredList<Media> filteredMediaList =  new FilteredList<>(mediaList, b -> true);

            mediaSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredMediaList.setPredicate(Media -> {

                    if (newValue == null||newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();
                    if (Media.getTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else if (Media.getDirector().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else
                        return false;
                });
            });
            SortedList<Media> sortedMedia = new SortedList<>(filteredMediaList);
            sortedMedia.comparatorProperty().bind(mediaTable.comparatorProperty());
            mediaTable.setItems(sortedMedia);

            //Filtered list to filter through books
            FilteredList<Book> filteredBookList =  new FilteredList<>(bookList, b -> true);

            bookSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredBookList.setPredicate(Book -> {

                    if (newValue == null||newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();
                    if (Book.getTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else if (Book.getAuthor().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                        return true;
                    } else
                        return false;
                });
            });
            SortedList<Book> sortedBooks = new SortedList<>(filteredBookList);
            sortedBooks.comparatorProperty().bind(bookTable.comparatorProperty());
            bookTable.setItems(sortedBooks);

            System.out.println(cookieAccountID);
    }


    public void addBook(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("enterBookForm.fxml"));
        Parent GUI = loader.load();
        Scene scene = new Scene(GUI);
        enterBookController controller = loader.getController();
        controller.passData(cookieAccountID);
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
        controller.passData(cookieAccountID);
        Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
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

    public void mediaPage(Event event) {
    }

    public void booksPage(Event event) {
    }

}
