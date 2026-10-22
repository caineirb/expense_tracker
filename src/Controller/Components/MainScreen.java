package Controller.Components;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Utils.ErrorMessages;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;

public class MainScreen implements Initializable {
    @FXML
    private SplitPane mainScreen;
    @FXML
    private Sidebar mainSidebarController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (mainSidebarController != null)
            mainSidebarController.setMainScreenController(this);
    }

    public void changeMainView(String fxmlPath) {
        try {
            Parent newView = FXMLLoader.load(this.getClass().getResource(fxmlPath));
            if (mainScreen.getItems().size() > 1)
                mainScreen.getItems().set(1, newView);
            else
                mainScreen.getItems().add(newView);
        } catch (IOException io) {
            ErrorMessages.printError(this.getClass(), io, "IO Exception: ");
        }
    }
}
