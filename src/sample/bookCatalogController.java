package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.classes.Book;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class bookCatalogController implements Initializable {

    public String DB_URL = "Jdbc:mysql://cougarlibrary.cyt69uqxe34i.us-east-2.rds.amazonaws.com?useSSL=false";
    public String userName = "amar";
    public String password = "Cougar2020";

    public TableView<Book> bookTable;
    public TableColumn<Book, Integer> bookIDCol;
    public TableColumn<Book, String> titleCol;
    public TableColumn<Book, String> authorCol;
    public TableColumn<Book, String> statusCol;
    public TableColumn<Book, Date> dueDateCol;
    public ObservableList<Book> bookList = FXCollections.observableArrayList();
    public TextField searchBooksTxt;

    public Date currentDate = new Date();

    int cookieAccountID;

    public void passData(int cookieAccountID) {
        this.cookieAccountID = cookieAccountID;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        bookIDCol.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        bookTable.setItems(bookList);

        PreparedStatement stmt = null;
        String sqlBook = "select * from COUGARLIBRARY.BOOKS \n" +
                    "left join COUGARLIBRARY.ISSUE_STATUS_BOOKS\n" +
                    "on COUGARLIBRARY.ISSUE_STATUS_BOOKS.BOOK_ID=COUGARLIBRARY.BOOKS.BOOK_ID;";
        ResultSet rsBook = null;

            try {
                Connection conn = DriverManager.getConnection(DB_URL, userName, password);

                stmt = conn.prepareStatement(sqlBook);
                rsBook = stmt.executeQuery();
                while(rsBook.next()) {
                    bookList.add(new Book(rsBook.getInt("BOOK_ID"), rsBook.getString("TITLE"), rsBook.getString("AUTHOR"), rsBook.getString("ISSUE_STATUS"), rsBook.getDate("DUE_DATE")));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //Filtered list to filter through books
            FilteredList<Book> filteredBookList =  new FilteredList<>(bookList, b -> true);

            searchBooksTxt.textProperty().addListener((observable, oldValue, newValue) -> {
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
        }

    public void checkoutBook(ActionEvent actionEvent) throws IOException {

        int NumOfBooksCheckedOut = 0;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String available = "AVAILABLE";

        if (bookTable.getSelectionModel().getSelectedItem().getStatus().equals(available)) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, userName, password);

                // get info to calculate due date and also to check if user is above book limit
                String sql = "select * from COUGARLIBRARY.ACCOUNTS inner join COUGARLIBRARY.ACCOUNT_TYPES on COUGARLIBRARY.ACCOUNTS.ACCOUNT_TYPE=COUGARLIBRARY.ACCOUNT_TYPES.ACCOUNT_TYPE where ACCOUNT_ID = ?;";
                String sq = "select count(*) FROM COUGARLIBRARY.ISSUE_STATUS_BOOKS WHERE ISSUER_ACCOUNT_ID = ?;";

                int lendingPeriod = 0;
                int bookLimit = 0;
                int currentNumOfBooks = 0;
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, cookieAccountID);
                ResultSet getAccountInfo = stmt.executeQuery();
                while(getAccountInfo.next()) {
                    lendingPeriod = getAccountInfo.getInt("LENDING_PERIOD");
                    bookLimit = getAccountInfo.getInt("BOOK_LIMIT");
                }

                stmt = conn.prepareStatement(sq);
                stmt.setInt(1, cookieAccountID);
                getAccountInfo = stmt.executeQuery();
                while(getAccountInfo.next()) {
                    currentNumOfBooks=getAccountInfo.getInt(1);
                }
                int noOfDays = (7*lendingPeriod);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
                Date date = calendar.getTime();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                if (currentNumOfBooks == bookLimit ) {
                    JOptionPane.showMessageDialog(null, "You have reached your book limit.\n Please return a book before you checkout another book.", "Error", JOptionPane.ERROR_MESSAGE);

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("userViewTables.fxml"));
                    Parent GUI = loader.load();
                    Scene scene = new Scene(GUI);

                    userViewTablesController controller = loader.getController();
                    controller.passData(cookieAccountID);

                    Stage window = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
                    window.setScene(scene);
                    window.show();


                } else {
                    //INSERT IF UNDER BOOK LIMIT
                    String sql0 = "update COUGARLIBRARY.BOOKS SET ISSUE_STATUS = 'CHECKED-OUT' WHERE BOOK_ID = ?;";
                    String sql1 = "insert INTO COUGARLIBRARY.ISSUE_STATUS_BOOKS (BOOK_ID, ISSUER_ACCOUNT_ID, DUE_DATE)\n" +
                            "VALUES (?, ?, ?);";
                    String sql2 = "SELECT * FROM COUGARLIBRARY.USERS WHERE ACCOUNT_ID = ?";
                    String sql3 = "update COUGARLIBRARY.USERS set NumOfBooksCheckedOut = ? where ACCOUNT_ID = ?;";

                    stmt = conn.prepareStatement(sql0);
                    stmt.setInt(1, bookTable.getSelectionModel().getSelectedItem().getBookID());
                    stmt.executeUpdate();

                    stmt = conn.prepareStatement(sql1);
                    stmt.setInt(1, bookTable.getSelectionModel().getSelectedItem().getBookID());
                    stmt.setInt(2, cookieAccountID);
                    stmt.setDate(3, sqlDate);
                    stmt.executeUpdate();

                    stmt=conn.prepareStatement(sql2);
                    stmt.setInt(1, cookieAccountID);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        NumOfBooksCheckedOut = rs.getInt("NumOfBooksCheckedOut");
                    }

                    stmt=conn.prepareStatement(sql3);
                    stmt.setInt(1, (NumOfBooksCheckedOut+1));
                    stmt.setInt(2, cookieAccountID);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(null,"Thank you for stopping by!\n Your book is due on " + sqlDate, "Information Dialog", JOptionPane.INFORMATION_MESSAGE);

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (bookTable.getSelectionModel().getSelectedItem().getStatus().equals("CHECKED-OUT")) {
            JOptionPane.showMessageDialog(null,"This item is checked out. Please select another item", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "Nothing selected. Please select an item to check out.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println(cookieAccountID);

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
