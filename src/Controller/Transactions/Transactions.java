package Controller.Transactions;

import java.net.URL;
import java.util.ResourceBundle;

import Model.Transactions.Transaction;
import Model.Transactions.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class Transactions implements Initializable {
    @FXML
    private BorderPane transactionsScreen;
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, Integer> numberColumn;
    @FXML
    private TableColumn<Transaction, String> nameColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> locationColumn;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        TransactionService transactions = new TransactionService();
        ObservableList<Transaction> observableList = FXCollections.observableArrayList(transactions.getTransactions());
        transactionsTable.setItems(observableList);
    }

}
