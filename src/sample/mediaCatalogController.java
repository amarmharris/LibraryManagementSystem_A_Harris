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
import sample.classes.Media;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class mediaCatalogController implements Initializable {

    public String DB_URL = "Jdbc:mysql://cougarlibrary.cyt69uqxe34i.us-east-2.rds.amazonaws.com?useSSL=false";
    public String userName = "amar";
    public String password = "Cougar2020";

    public TableView<Media> mediaTable;
    public TableColumn<Media, Integer> mediaIDCol;
    public TableColumn<Media, String> titleCol;
    public TableColumn<Media, String> directorCol;
    public TableColumn<Media, String> statusCol;
    public TableColumn<Media, Date> dueDateCol;
    public ObservableList<Media> mediaList = FXCollections.observableArrayList();
    public TextField searchMediaTxt;

    public Date currentDate = new Date();

    int cookieAccountID;

    public void passData(int cookieAccountID) {
        this.cookieAccountID = cookieAccountID;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mediaIDCol.setCellValueFactory(new PropertyValueFactory<>("mediaID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        directorCol.setCellValueFactory(new PropertyValueFactory<>("director"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        mediaTable.setItems(mediaList);

        PreparedStatement stmt = null;
        String sqlMedia = "select * from COUGARLIBRARY.MEDIA \n" +
                "left join COUGARLIBRARY.ISSUE_STATUS_MEDIA\n" +
                "on COUGARLIBRARY.ISSUE_STATUS_MEDIA.MEDIA_ID=COUGARLIBRARY.MEDIA.MEDIA_ID;";
        ResultSet rsMedia = null;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, userName, password);

            stmt = conn.prepareStatement(sqlMedia);
            rsMedia = stmt.executeQuery();
            while(rsMedia.next()) {
                mediaList.add(new Media(rsMedia.getInt("MEDIA_ID"), rsMedia.getString("TITLE"), rsMedia.getString("DIRECTOR"), rsMedia.getString("ISSUE_STATUS"), rsMedia.getDate("DUE_DATE")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Filtered list to filter through Media
        FilteredList<Media> filteredMediaList =  new FilteredList<>(mediaList, b -> true);

        searchMediaTxt.textProperty().addListener((observable, oldValue, newValue) -> {
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
    }

    public void checkoutMedia(ActionEvent actionEvent) {

        int NumOfMediaCheckedOut = 0;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String available = "AVAILABLE";

        if (mediaTable.getSelectionModel().getSelectedItem().getStatus().equals(available)) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, userName, password);

                // get info to calculate due date and also to check if user is above media limit
                String sql = "select * from COUGARLIBRARY.ACCOUNTS inner join COUGARLIBRARY.ACCOUNT_TYPES on COUGARLIBRARY.ACCOUNTS.ACCOUNT_TYPE=COUGARLIBRARY.ACCOUNT_TYPES.ACCOUNT_TYPE where ACCOUNT_ID = ?;";
                String sq = "select count(*) FROM COUGARLIBRARY.ISSUE_STATUS_MEDIA WHERE ISSUER_ACCOUNT_ID = ?;";

                int lendingPeriod = 0;
                int mediaLimit = 0;
                String accountType = null;
                int currentNumOfMedia = 0;
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, cookieAccountID);
                ResultSet getAccountInfo = stmt.executeQuery();
                while(getAccountInfo.next()) {
                    lendingPeriod = getAccountInfo.getInt("LENDING_PERIOD");
                    mediaLimit = getAccountInfo.getInt("MEDIA_LIMIT");
                    accountType = getAccountInfo.getString("ACCOUNT_TYPE");
                }

                stmt = conn.prepareStatement(sq);
                stmt.setInt(1, cookieAccountID);
                getAccountInfo = stmt.executeQuery();
                while(getAccountInfo.next()) {
                    currentNumOfMedia=getAccountInfo.getInt(1);
                }
                int noOfDays = (7*lendingPeriod);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
                Date date = calendar.getTime();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                if (currentNumOfMedia == mediaLimit ) {
                    JOptionPane.showMessageDialog(null, "As a " + accountType + " you have reached your limit of " + mediaLimit + " media items.\n Please return a media item before you checkout another one.", "Error", JOptionPane.ERROR_MESSAGE);

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
                    //INSERT IF UNDER MEDIA LIMIT
                    String sql0 = "update COUGARLIBRARY.MEDIA SET ISSUE_STATUS = 'CHECKED-OUT' WHERE MEDIA_ID = ?;";
                    String sql1 = "insert INTO COUGARLIBRARY.ISSUE_STATUS_MEDIA (MEDIA_ID, ISSUER_ACCOUNT_ID, DUE_DATE) VALUES (?, ?, ?);";
                    String sql2 = "SELECT * FROM COUGARLIBRARY.USERS WHERE ACCOUNT_ID = ?";
                    String sql3 = "update COUGARLIBRARY.USERS set NumOfMediaCheckedOut = ? where ACCOUNT_ID = ?;";

                    stmt = conn.prepareStatement(sql0);
                    stmt.setInt(1, mediaTable.getSelectionModel().getSelectedItem().getMediaID());
                    stmt.executeUpdate();

                    stmt = conn.prepareStatement(sql1);
                    stmt.setInt(1, mediaTable.getSelectionModel().getSelectedItem().getMediaID());
                    stmt.setInt(2, cookieAccountID);
                    stmt.setDate(3, sqlDate);
                    stmt.executeUpdate();

                    stmt=conn.prepareStatement(sql2);
                    stmt.setInt(1, cookieAccountID);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        NumOfMediaCheckedOut = rs.getInt("NumOfMediaCheckedOut");
                    }

                    stmt=conn.prepareStatement(sql3);
                    stmt.setInt(1, (NumOfMediaCheckedOut+1));
                    stmt.setInt(2, cookieAccountID);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(null,"Thank you for stopping by!\n Your item is due on " + sqlDate, "Information Dialog", JOptionPane.INFORMATION_MESSAGE);

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
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        } else if (mediaTable.getSelectionModel().getSelectedItem().getStatus().equals("CHECKED-OUT")) {
            JOptionPane.showMessageDialog(null,"This item is checked out. Please select another item", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
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
