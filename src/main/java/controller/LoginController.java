package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LoginController extends MainController {
    @FXML
    private TextField loginUser;
    @FXML
    private PasswordField loginPass;
    @FXML
    private Button loginBtn;

    private void handleLogin() {
        String username = loginUser.getText();
        String password = loginPass.getText();

        if (username.equals("exit")) {
            System.exit(0);
        }

        try {
            Scanner accountScanner = new Scanner(new File("accounts.txt"));
            Scanner customerScanner = new Scanner(new File("customers.txt"));
            Scanner providerScanner = new Scanner(new File("providers.txt"));

            while (accountScanner.hasNextLine()) {
                String line = accountScanner.nextLine();
                String[] parts = line.split(",");

                
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleEvent();
    }

    public void handleEvent() {
        loginBtn.setOnMouseClicked(event -> handleLogin());
    }
}