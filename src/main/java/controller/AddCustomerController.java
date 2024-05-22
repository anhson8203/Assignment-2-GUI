package controller;

import Manager.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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

    DataLoader dataLoader = new DataLoader();
    Random random = new Random();

    List<Customer> customers;
    List<InsuranceCard> insuranceCards;
    List<Claim> claims;

    public boolean checkCustomerRole() {
        if (policyholderRadioBtn.isSelected() && dependentRadioBtn.isSelected()) {
            showAlert("Error", "Please select only one role", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public AddCustomerController() {
        try {
            customers = dataLoader.loadCustomers();
            insuranceCards = dataLoader.loadInsuranceCards(customers);
            claims = dataLoader.loadClaims(customers, insuranceCards);
        } catch (FileNotFoundException e) {
            showAlert("Error", "File not found!", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleEvent();
    }

    public void handleEvent() {
        backBtn.setOnMouseClicked(event -> handleTransfer(backBtn, "/fxml/admin.fxml"));
        doneBtn.setOnMouseClicked(event -> handleDone());
    }

    public void handleDone() {
        PolicyHolder policyHolder = new PolicyHolder();
        Dependent dependent = new Dependent();

        if (!checkCustomerRole()) {
            return;
        }

        StringBuilder customerIdBuilder = new StringBuilder("c");
        for (int i = 0; i < 7; i++) {
            customerIdBuilder.append(random.nextInt(7));
        }
        String customerID = customerIdBuilder.toString();

        if (policyholderRadioBtn.isSelected()) {
            policyHolder.setId(customerID);
            policyHolder.setEmail(email.getText().trim());
            policyHolder.setFullName(name.getText().trim());
            policyHolder.setAddress(address.getText());
            policyHolder.setPhoneNumber(phoneNumber.getText());

            customers.add(policyHolder);

            try {
                dataLoader.appendPolicyHolderToFile(policyHolder);
                dataLoader.appendAccountToFile(customerID, "policy holder");
            } catch (IOException e) {
                showAlert("Error", "An error has occurred!", Alert.AlertType.ERROR);
            }
        }

        if (dependentRadioBtn.isSelected()) {
            dependent.setId(customerID);
            dependent.setEmail(email.getText().trim());
            dependent.setFullName(name.getText().trim());
            dependent.setAddress(address.getText());
            dependent.setPhoneNumber(phoneNumber.getText());

            PolicyHolder defaultPolicyHolder = new PolicyHolder();
            dependent.setPolicyHolder(defaultPolicyHolder);

            customers.add(dependent);

            try {
                dataLoader.appendDependentToFile(dependent);
                dataLoader.appendAccountToFile(customerID, "dependent");
            } catch (IOException e) {
                showAlert("Error", "An error has occurred!", Alert.AlertType.ERROR);
            }
        }

        showAlert("Done", "Add new customer successfully!\nAn account is also created for the customer, with username is " + customerID + " and password is '1'", Alert.AlertType.INFORMATION);

        name.setText("");
        phoneNumber.setText("");
        address.setText("");
        email.setText("");
        policyholderRadioBtn.setSelected(false);
        dependentRadioBtn.setSelected(false);
    }
}