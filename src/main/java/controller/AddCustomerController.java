package controller;

import Manager.Customer;
import Manager.CustomerService;
import Manager.Dependent;
import Manager.PolicyHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class AddCustomerController extends MainController {
    @FXML
    private Button backBtn;
    @FXML
    private Button doneBtn;
    @FXML
    private TextField name;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField address;
    @FXML
    private TextField email;
    @FXML
    private RadioButton policyholderRadioBtn;
    @FXML
    private RadioButton dependentRadioBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleEvent();
    }

    public void handleEvent() {
        backBtn.setOnMouseClicked(event -> handleTransfer(backBtn, "/fxml/admin.fxml"));
        doneBtn.setOnMouseClicked(event -> handleDone());
    }

    public void handleDone() {
        CustomerService customerService = new CustomerService();
        PolicyHolder policyHolder = new PolicyHolder();
        Dependent dependent = new Dependent();
        Random random = new Random();
        StringBuilder customerIdBuilder = new StringBuilder("c");
        for (int i = 0; i < 7; i++) {
            customerIdBuilder.append(random.nextInt(7));
        }
        if(policyholderRadioBtn.isSelected()){
            policyHolder.setId(customerIdBuilder.toString());
            policyHolder.setEmail(email.getText().trim());
            policyHolder.setFullName(name.getText().trim());
            policyHolder.setAddress(address.getText());
            policyHolder.setPhoneNumber(phoneNumber.getText());
            customerService.addCustomer(policyHolder);
        }
        if(dependentRadioBtn.isSelected()){
            dependent.setId(customerIdBuilder.toString());
            dependent.setEmail(email.getText().trim());
            dependent.setFullName(name.getText().trim());
            dependent.setAddress(address.getText());
            dependent.setPhoneNumber(phoneNumber.getText());
            customerService.addCustomer(dependent);
        }

        showAlert("Done", "Add new customer successfully", Alert.AlertType.INFORMATION);
        name.setText("");
        phoneNumber.setText("");
        address.setText("");
        email.setText("");
        policyholderRadioBtn.setSelected(false);
        dependentRadioBtn.setSelected(false);
    }
}