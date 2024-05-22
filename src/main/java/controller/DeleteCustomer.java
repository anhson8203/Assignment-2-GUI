package controller;

import Manager.*;
import javafx.fxml.FXML;
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
        String customerId = id.getText();
        DataLoader dataLoader = new DataLoader();
        List<Customer> customers;
        customers = dataLoader.loadCustomers();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        if (customer != null) {
            if (customer instanceof Dependent dependent) {
                PolicyHolder policyHolder = dependent.getPolicyHolder();
                policyHolder.getDependents().remove(dependent);
            }
            customers.remove(customer);
            try {
                dataLoader.saveCustomers(customers);
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while saving customers.");
            }
        } else {
            System.out.println("Customer not found!");
        }
    }
}