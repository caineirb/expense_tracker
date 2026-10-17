package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;

public class Login {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerLink;

    @FXML
    private void handleUsernameFocus() {
        username.requestFocus();
    }

    @FXML
    private void handlePasswordFocus() {
        password.requestFocus();
    }

    @FXML
    private void handleLogin() {
        System.out.println("Login is pressed!");
    }

    @FXML
    private void handleRegister() {
        System.out.println("Registration Redirect");
    }
}
