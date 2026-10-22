package Utils;

import java.util.Optional;

import Types.DTO.AlertDisplay;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class AlertCreator {
    public static boolean showConfirmationAlert(AlertDisplay confirmationContents) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(confirmationContents.titleText());
        alert.setHeaderText(confirmationContents.headerText());
        alert.setContentText(confirmationContents.contentText());
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void showErrorAlert(AlertDisplay errorContents) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(errorContents.titleText());
        alert.setHeaderText(errorContents.headerText());
        alert.setContentText(errorContents.contentText());
        alert.showAndWait();
    }
}
