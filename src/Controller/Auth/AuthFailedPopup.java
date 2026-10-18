package Controller.Auth;

import java.io.IOException;

import Utils.ErrorMessages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AuthFailedPopup {
    @FXML
    private BorderPane authFailedPopup;
    @FXML
    private Button okayButton;

    public static void showPopup(Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AuthFailedPopup.class.getResource("/View/Auth/AuthFailedPopup.fxml"));

            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.setTitle("Incorrect Database Password");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(ownerStage);
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (IOException ie) {
            ErrorMessages.printError(AuthFailedPopup.class.getClass(), ie, "IO Exception: ");
        } catch (Exception e) {
            ErrorMessages.printError(AuthFailedPopup.class.getClass(), e);
        }
    }

    @FXML
    private void handleOkayButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
