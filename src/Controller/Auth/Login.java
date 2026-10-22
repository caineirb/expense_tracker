package Controller.Auth;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Model.Database.DatabaseConnector;
import Utils.ErrorMessages;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/// Login Auth Controller
/// This handles the frame for `/View/Auth/Login.fxml`. It checks for initial
/// database creation and password set prompt if first time. If not the first
/// time, then it asks for the database password and popups
/// the `AuthFailedPopup.fxml`.
public class Login implements Initializable {
    @FXML
    private BorderPane loginScreen;
    @FXML
    private Text screenText;
    @FXML
    private PasswordField dbPassword;
    @FXML
    private Button submitPassword;

    /**
     * Initializes the DB, and handles the submit button for database password
     * checking, then redirect to the next screen.
     */
    @FXML
    private void handleDatabaseEnter() {
        Stage loginStage = (Stage) loginScreen.getScene().getWindow();
        String databasePassword = dbPassword.getText();
        boolean isSuccess = DatabaseConnector.initializeDB(databasePassword, loginStage);
        dbPassword.setText("");

        if (isSuccess)
            try {
                changeScreen(loginStage);
            } catch (IOException ie) {
                ErrorMessages.printError(getClass(), ie, "IO Exception: ");
            }
    }

    /**
     * Changes the screen for successful database connection.
     * 
     * @param stage
     * @throws IOException
     */
    private void changeScreen(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Components/MainScreen.fxml"));
        Parent transactionsView = loader.load();
        Scene scene = new Scene(transactionsView);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (!DatabaseConnector.isDBFileExist())
            screenText.setText("Set Initial Password");
    }
}
