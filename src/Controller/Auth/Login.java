package Controller.Auth;

import Model.Database.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Login {
    @FXML
    private BorderPane loginScreen;
    @FXML
    private PasswordField dbPassword;
    @FXML
    private Button submitPassword;

    @FXML
    private void handleDatabaseEnter() {
        Stage loginStage = (Stage) loginScreen.getScene().getWindow();
        String databasePassword = dbPassword.getText();
        DatabaseConnector.initializeDB(databasePassword, loginStage);
        dbPassword.setText("");
    }
}
