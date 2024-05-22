package controller;

import Manager.Customer;
import Manager.CustomerService;
import Manager.DataLoader;
import Manager.PolicyHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateCustomer extends MainController {
    @FXML
    private Button backBtn;
    @FXML
    private Button doneBtn;
    @FXML
    private TextField id;
    @FXML
    private TextField name;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField address;
    @FXML
    private TextField email;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleEvent();
    }

    public void handleEvent() {
        backBtn.setOnMouseClicked(event -> handleTransfer(backBtn, "/fxml/admin.fxml"));
        doneBtn.setOnMouseClicked(event -> {
            try {
                handleDone();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleDone() throws FileNotFoundException {
        CustomerService customerService = new CustomerService();
        DataLoader dataLoader = new DataLoader();
        List<Customer> customers;
        customers = dataLoader.loadCustomers();
        System.out.println(customers.toString());
        String customerId = id.getText();
        for (Customer c : customers) {
            if (c.getId().equals(customerId)) {
                String newCustomerFullName = name.getText();
                String newCustomerPhoneNumber = phoneNumber.getText();
                String newCustomerAddress = address.getText();
                String newCustomerEmail = email.getText();

                c.setFullName(newCustomerFullName);
                c.setPhoneNumber(newCustomerPhoneNumber);
                c.setAddress(newCustomerAddress);
                c.setEmail(newCustomerEmail);
                break;
            }
        }
        dataLoader.saveCustomers(customers);
    }
}
