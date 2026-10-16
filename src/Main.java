import java.sql.Connection;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import Model.DatabaseConnector;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Connection conn = DatabaseConnector.connect();
            System.out.println("Connection okay." + conn.toString());
        } catch (Error s) {
            throw new Error(s.toString());
        }
        Parent root = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Expense Tracker");
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
