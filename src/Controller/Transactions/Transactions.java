package Controller.Transactions;

import java.net.URL;
import java.util.ResourceBundle;

import Model.Transactions.TransactionTable;
import Model.Transactions.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class Transactions implements Initializable {
    @FXML private BorderPane transactionsScreen;
    @FXML private TableView<TransactionTable> transactionsTable;
    @FXML private TableColumn<TransactionTable, Integer> numberColumn;
    @FXML private TableColumn<TransactionTable, String> nameColumn;
    @FXML private TableColumn<TransactionTable, String> descriptionColumn;
    @FXML private TableColumn<TransactionTable, String> dateColumn;
    @FXML private TableColumn<TransactionTable, String> locationColumn;
    @FXML private ChoiceBox<String> columnSelector;
    @FXML private TextField transactionSearch;
    @FXML private Button addTransactionButton;
    
    private static final String[] columns = {"number", "name", "description", "date", "location"};
    private FilteredList<TransactionTable> filteredList;
    private SortedList<TransactionTable> sortedList;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.setCellsValueFactory();
        this.setColumnSelector();
        this.setupTable();
        this.setSearchingBehavior();
    }

    private void setCellsValueFactory() {
        this.numberColumn.setCellValueFactory(new PropertyValueFactory<>(columns[0]));
        this.nameColumn.setCellValueFactory(new PropertyValueFactory<>(columns[1]));
        this.descriptionColumn.setCellValueFactory(new PropertyValueFactory<>(columns[2]));
        this.dateColumn.setCellValueFactory(new PropertyValueFactory<>(columns[3]));
        this.locationColumn.setCellValueFactory(new PropertyValueFactory<>(columns[4]));
    }

    private void setColumnSelector() {
        this.columnSelector.setItems(FXCollections.observableArrayList(columns));
        this.columnSelector.setValue(columns[1]);
    }

    private void setupTable() {
        TransactionService transactions = new TransactionService();
        ObservableList<TransactionTable> observableList = FXCollections.observableArrayList(transactions.getTransactions());
        this.filteredList = new FilteredList<>(observableList, transaction -> true);
        this.sortedList = new SortedList<>(filteredList);
        this.sortedList.comparatorProperty().bind(this.transactionsTable.comparatorProperty());
        this.transactionsTable.setItems(sortedList);
    }

    private void setSearchingBehavior() {
        this.transactionSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(newValue, this.columnSelector.getValue());
        });

        this.columnSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(this.transactionSearch.getText(), newValue);
        });
    }

    private void updateFilter(String searchText, String selectedColumn) {
        this.filteredList.setPredicate(transaction -> {
            if (searchText == null || searchText.isEmpty()) return true;
            String lowerCaseFilter = searchText.toLowerCase();

            switch (selectedColumn) {
                case "number":
                    return String.valueOf(transaction.getNumber()).contains(lowerCaseFilter);
                case "name":
                    return transaction.getName() != null && transaction.getName().toLowerCase().contains(lowerCaseFilter);
                case "description":
                    return transaction.getDescription() != null && transaction.getDescription().toLowerCase().contains(lowerCaseFilter);
                case "date":
                    return transaction.getDate() != null && transaction.getDate().toLowerCase().contains(lowerCaseFilter);
                case "location":
                    return transaction.getLocation() != null && transaction.getLocation().toLowerCase().contains(lowerCaseFilter);
                default:
                    return false;
            }
        });   
    }
}
