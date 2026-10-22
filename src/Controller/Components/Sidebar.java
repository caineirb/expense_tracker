package Controller.Components;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class Sidebar implements Initializable {
    @FXML
    private Button dashboardButton;
    @FXML
    private Button transactionsButton;
    @FXML
    private Button analyticsButton;

    private static final String INACTIVE_BUTTON = "-fx-background-color: #00000000; -fx-text-fill: #000000";
    private static final String ACTIVE_BUTTON = "-fx-background-color: #567dffff; -fx-text-fill: #000000";
    private MainScreen mainScreen;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    public void setMainScreenController(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.switchToDashboard();
    }

    @FXML
    private void switchToDashboard() {
        dashboardButton.setStyle(ACTIVE_BUTTON);
        transactionsButton.setStyle(INACTIVE_BUTTON);
        analyticsButton.setStyle(INACTIVE_BUTTON);
        this.mainScreen.changeMainView("/View/Dashboard/MainView.fxml");
    }

    @FXML
    private void switchToTransactions() {
        dashboardButton.setStyle(INACTIVE_BUTTON);
        transactionsButton.setStyle(ACTIVE_BUTTON);
        analyticsButton.setStyle(INACTIVE_BUTTON);
        this.mainScreen.changeMainView("/View/Transactions/MainView.fxml");
    }

    @FXML
    private void switchToAnalytics() {
        dashboardButton.setStyle(INACTIVE_BUTTON);
        transactionsButton.setStyle(INACTIVE_BUTTON);
        analyticsButton.setStyle(ACTIVE_BUTTON);
    }
}
