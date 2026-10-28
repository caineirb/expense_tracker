package Controller.Transactions;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.List;

import Model.Transactions.TransactionTable;
import Utils.FuzzySearch;
import Utils.StringCase;
import Model.Transactions.TransactionService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

import static javafx.util.Duration.millis;

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
    private List<TransactionTable> masterList;
    private ObservableList<TransactionTable> observableList;
    private FilteredList<TransactionTable> filteredList;
    private SortedList<TransactionTable> sortedList;
    private final Timeline debounceTimeline = new Timeline();
    private Task<List<TransactionTable>> filterTask;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        this.setCellsValueFactory();
        this.setColumnSelector();
        this.setupTable();
        this.setSearchingBehavior();
        this.setupDebounceFilter();
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
        this.columnSelector.setConverter(new StringConverter<String>() {

            @Override
            public String fromString(String arg0) {
                return arg0;
            }

            @Override
            public String toString(String arg0) {
                return (arg0 == null) ? "" : StringCase.convertToTitleCase(arg0);
            }
            
        });
    }

    private void setupTable() {
        TransactionService transactions = new TransactionService();
        this.masterList = transactions.getTransactions();
        this.observableList = FXCollections.observableArrayList(this.masterList);
        this.filteredList = new FilteredList<>(this.observableList, transaction -> true);
        this.sortedList = new SortedList<>(filteredList);
        this.sortedList.comparatorProperty().bind(this.transactionsTable.comparatorProperty());
        this.transactionsTable.setItems(sortedList);
    }

    private void setupDebounceFilter() {
        this.debounceTimeline.setCycleCount(1);
    }

    private void setSearchingBehavior() {
        this.transactionSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            debounceTimeline.stop();
            debounceTimeline.getKeyFrames().setAll(
                new KeyFrame(millis(300), event -> {
                    this.updateFilterBackground(newValue, this.columnSelector.getValue());
                })
            );
            debounceTimeline.play();
        });

        this.columnSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            debounceTimeline.stop();
            this.updateFilterBackground(this.transactionSearch.getText(), newValue);
        });
    }

    private void updateFilterBackground(String searchText, String selectedColumn) {
        if (filterTask != null && filterTask.isRunning()) filterTask.cancel();

        filterTask = new Task<List<TransactionTable>>() {
            @Override
            protected List<TransactionTable> call() throws Exception {
                if (searchText == null || searchText.isBlank()) return List.copyOf(masterList);
                String filter = searchText.trim();
                List<TransactionTable> exact = masterList.parallelStream()
                    .filter(exactFilter(filter, selectedColumn))
                    .collect(Collectors.toList());
                if (!exact.isEmpty()) return exact;
                return masterList.parallelStream()
                    .filter(updateFilter(filter, selectedColumn))
                    .collect(Collectors.toList());
            }
        };

        filterTask.setOnSucceeded(workerStateEvent -> {
            this.observableList.setAll(filterTask.getValue());
        });

        Thread thread = new Thread(filterTask);
        thread.setDaemon(true);
        thread.start();
    }

    private Predicate<TransactionTable> exactFilter(String filter, String selectedColumn) {
        return transaction -> {
            switch (selectedColumn) {
                case "number":
                    return FuzzySearch.isExact(String.valueOf(transaction.getNumber()), filter);
                case "name":
                    return FuzzySearch.isExactNoCase(transaction.getName(), filter);
                case "description":
                    return FuzzySearch.isExactNoCase(transaction.getDescription(), filter);
                case "date":
                    return FuzzySearch.isExactNoCase(transaction.getDate(), filter);
                case "location":
                    return FuzzySearch.isExactNoCase(transaction.getLocation(), filter);
                default:
                    return false;
            }
        };
    }

    private Predicate<TransactionTable> updateFilter(String searchText, String selectedColumn) {
        return transaction -> {
            String filter = searchText.trim();

            switch (selectedColumn) {
                case "number":
                    return FuzzySearch.contains(String.valueOf(transaction.getNumber()), filter);
                case "name":
                    return FuzzySearch.containsNoCase(transaction.getName(), filter);
                case "description":
                    return FuzzySearch.containsNoCase(transaction.getDescription(), filter);
                case "date":
                    return FuzzySearch.containsNoCase(transaction.getDate(), filter);
                case "location":
                    return FuzzySearch.containsNoCase(transaction.getLocation(), filter);
                default:
                    return false;
            }
        };
    }
}
