package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class viewTablesPageController implements Initializable {
    public Tab itemsPage;
    public Tab mediaPage;
    public Tab booksPage;
    public TableView mediaTable;
    public TableColumn mediaIDCol;
    public TableColumn mediaTtleCol;
    public TableColumn directorCol;
    public TableColumn mediaDoeCol;
    public TableColumn mediaStatusCol;
    public TableView bookTable;
    public TableColumn bookIDCol;
    public TableColumn bookTitleCol;
    public TableColumn authorCol;
    public TableColumn bookDoeCol;
    public TableColumn bookStatusCol;
    public TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void itemsPage(Event event) {
    }

    public void addBook(ActionEvent actionEvent) {
    }

    public void addMedia(ActionEvent actionEvent) {
    }

    public void mediaPage(Event event) {
    }

    public void booksPage(Event event) {
    }

    public void back2Home(ActionEvent actionEvent) {
    }
}
