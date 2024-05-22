package controller;

import Manager.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DeleteCustomer extends MainController {
    @FXML
    private Button backBtn;
    @FXML
    private Button removeBtn;
    @FXML
    private TextField id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleEvent();
    }

    public void handleEvent() {
        backBtn.setOnMouseClicked(event -> handleTransfer(backBtn, "/fxml/admin.fxml"));
        removeBtn.setOnMouseClicked(event -> {
            try {
                handleRemove();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleRemove() throws FileNotFoundException {
        DataLoader dataLoader = new DataLoader();
        String customerId = id.getText();

        List<Customer> customers;
        customers = dataLoader.loadCustomers();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);

        if (customer == null) {
            showAlert("Error", "Customer not found!", Alert.AlertType.ERROR);
        } else {
            if (customer instanceof Dependent dependent) {
                PolicyHolder policyHolder = dependent.getPolicyHolder();
                policyHolder.getDependents().remove(dependent);
            }
            customers.remove(customer);
            try {
                dataLoader.saveCustomers(customers);
                showAlert("Success", "Customer has been removed!", Alert.AlertType.INFORMATION);
            } catch (FileNotFoundException e) {
                showAlert("Error", "An error has occurred!", Alert.AlertType.ERROR);
            }
        }
    }
}