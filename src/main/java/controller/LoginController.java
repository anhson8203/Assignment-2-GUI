package controller;

import Manager.Customer;
import Manager.DataLoader;
import Manager.Provider;
import javafx.fxml.FXML;
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
            Scanner fileScanner = new Scanner(new File("src/main/resources/database/accounts.txt"));
            DataLoader dataLoader = new DataLoader();
            List<Customer> customers = dataLoader.loadCustomers();
            List<Provider> providers = dataLoader.loadProviders();

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length == 3 && parts[0].equals(username) && parts[1].equals(password)) {
                    if (parts[2].equals("admin")) {
                        handleTransferRole("admin");
                        return;
                    }
                    for (Customer customer : customers) {
                        if (customer.getId().equals(username)) {
                            System.out.println(parts[2]);
                            handleTransferRole(parts[2].trim());
                            return;
                        }
                    }
                    // Check for Manager.InsuranceSurveyor
                    for (Provider provider : providers) {
                        if (provider.getProviderID().equals(username)) {
                            handleTransferRole(parts[2].trim());
                            return;
                        }
                    }
                    // No matching customer found, check for policy owner
                    Scanner insuranceCardScanner = new Scanner(new File("insuranceCards.txt"));
                    while (insuranceCardScanner.hasNextLine()) {
                        String insuranceCardLine = insuranceCardScanner.nextLine();
                        String[] insuranceCardParts = insuranceCardLine.split(",");
                        if (insuranceCardParts[2].equals(username)) {
                            handleTransferRole("policy owner");
                            return;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleEvent();
    }

    public void handleEvent() {
        loginBtn.setOnMouseClicked(event -> handleLogin());
    }

    public void handleTransferRole(String role) {
        String path = switch (role) {
            case "admin" -> "/fxml/admin.fxml";
            case "policy holder" -> "/fxml/policyholder.fxml";
            case "dependent" -> "/fxml/dependent.fxml";
            case "policy owner" -> "/fxml/policyowner.fxml";
            case "insurance manager" -> "/fxml/insurancemanager.fxml";
            default -> null;
        };
        handleTransfer(loginBtn, path);
    }
}