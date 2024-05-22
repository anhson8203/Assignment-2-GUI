package Manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CustomerService {
    DataLoader dataLoader = new DataLoader();
    Random random = new Random();
    Login login;

    List<Customer> customers;
    List<Provider> providers;
    List<InsuranceCard> insuranceCards;
    List<Claim> claims;

    Scanner scanner = new Scanner(System.in);

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public CustomerService() {
        try {
            customers = dataLoader.loadCustomers();
            providers = dataLoader.loadProviders();
            insuranceCards = dataLoader.loadInsuranceCards(customers);
            claims = dataLoader.loadClaims(customers, insuranceCards);
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public void fileClaim() {
        System.out.println("\nPlease enter the claim details (or type 'exit' at any time to return to the main menu)\n");

        StringBuilder id = new StringBuilder("f");
        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }

        Customer insuredPerson = null;
        while (insuredPerson == null) {
            System.out.print("Enter insured person ID: ");
            String insuredPersonId = scanner.next();
            if ("exit".equalsIgnoreCase(insuredPersonId)) {
                break;
            }

            if (!insuredPersonId.matches("^c\\d{7}$")) {
                System.out.println("\nInvalid customer ID format. The format should be 'c' followed by 7 digits.\n");
                continue;
            }

            insuredPerson = customers.stream().filter(c -> c.getId().equals(insuredPersonId)).findFirst().orElse(null);
            if (insuredPerson == null) {
                System.out.println("\nInsured person not found. Please try again.\n");
            }
        }

        if (insuredPerson == null) {
            return;
        }

        InsuranceCard card = null;
        while (card == null) {
            System.out.print("\nEnter card number: ");
            String cardNumber = scanner.next();
            if ("exit".equalsIgnoreCase(cardNumber)) {
                break;
            }

            card = insuranceCards.stream().filter(c -> c.getCardNumber().equals(cardNumber)).findFirst().orElse(null);
            if (card == null) {
                System.out.println("\nCard number not found. Please try again.\n");
            } else if (!card.getCardHolder().equals(insuredPerson)) {
                System.out.println("\nThis card does not belong to the entered customer. Please try again.\n");
                card = null;
            }
        }

        if (card == null) {
            return;
        }

        Double claimAmount = null;
        while (claimAmount == null) {
            System.out.print("\nEnter claim amount: $");
            String input = scanner.next();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            try {
                claimAmount = Double.parseDouble(input);
                if (claimAmount <= 0) {
                    System.out.println("\nManager.Claim amount must be greater than zero. Please try again.\n");
                    claimAmount = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid claim amount. Please enter a number.\n");
            }
        }

        if (claimAmount == null) {
            return;
        }

        List<String> documentNames = new ArrayList<>();
        while (true) {
            System.out.print("\nEnter documents' names (type 'done' to finish adding documents): ");
            String input = scanner.next();

            if ("exit".equalsIgnoreCase(input)) {
                documentNames = null;
                break;
            } else if ("done".equalsIgnoreCase(input)) {
                break;
            } else if (!input.matches("^[a-zA-Z0-9_]+$")) {
                System.out.println("\nInvalid document name. Please enter a name containing only alphanumeric characters and underscores.\n");
            } else {
                documentNames.add(input);
            }
        }

        if (documentNames == null) {
            return;
        }

        ReceiverBankingInfo receiverBankingInfo = null;
        while (receiverBankingInfo == null) {
            System.out.print("\nEnter receiver banking info (Bank, Name, Banking Number separated by underscore, no spaces allowed): ");
            String receiverBankingInfoInput = scanner.next();
            scanner.nextLine();
            if ("exit".equalsIgnoreCase(receiverBankingInfoInput)) {
                break;
            }

            String[] receiverBankingInfoData = receiverBankingInfoInput.split("_");
            if (receiverBankingInfoData.length != 3) {
                System.out.println("\nInvalid receiver banking info format. Please enter Bank, Name, BankingNumber separated by underscore. Remember, no spaces are allowed.\n");
            } else {
                receiverBankingInfo = new ReceiverBankingInfo(receiverBankingInfoData[0], receiverBankingInfoData[1], receiverBankingInfoData[2]);
            }
        }

        if (receiverBankingInfo == null) {
            return;
        }

        Claim newClaim = new Claim();
        newClaim.setId(id.toString());
        newClaim.setClaimDate(LocalDate.now());
        newClaim.setInsuredPerson(insuredPerson);
        newClaim.setCardNumber(card);
        newClaim.setExamDate(LocalDate.now().plusDays(1));
        newClaim.setClaimAmount(claimAmount);
        newClaim.setStatus(ClaimStatus.NEW);

        for (String documentName : documentNames) {
            newClaim.addDocument(documentName);
        }

        newClaim.setReceiverBankingInfo(receiverBankingInfo);
        claims.add(newClaim);
        insuredPerson.add(newClaim);

        try {
            dataLoader.saveClaims(claims);
            System.out.println("Manager.Claim added successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public void updateClaim() {
        String customerId = login.getUsername();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);

        System.out.print("Enter the claim ID you want to update: ");
        String claimId = scanner.next();
        Claim claim = claims.stream().filter(c -> c.getId().equals(claimId)).findFirst().orElse(null);

        if (claim == null || (!claim.getInsuredPerson().getId().equals(customerId) && !(customer instanceof PolicyHolder && ((PolicyHolder) customer).getDependents().contains(claim.getInsuredPerson())))) {
            System.out.println("Manager.Claim not found or you do not have permission to update this claim!");
            return;
        }

        System.out.println("\nPlease enter the claim details (or type 'exit' at any time to return to the main menu)\n");

        Customer insuredPerson = null;
        while (insuredPerson == null) {
            System.out.print("Enter insured person ID: ");
            String insuredPersonId = scanner.next();
            if ("exit".equalsIgnoreCase(insuredPersonId)) {
                break;
            }

            if (!insuredPersonId.matches("^c\\d{7}$")) {
                System.out.println("\nInvalid customer ID format. The format should be 'c' followed by 7 digits.\n");
                continue;
            }

            insuredPerson = customers.stream().filter(c -> c.getId().equals(insuredPersonId)).findFirst().orElse(null);
            if (insuredPerson == null) {
                System.out.println("\nInsured person not found. Please try again.\n");
            }
        }

        if (insuredPerson == null) {
            return;
        }

        InsuranceCard card = null;
        while (card == null) {
            System.out.print("\nEnter card number: ");
            String cardNumber = scanner.next();
            if ("exit".equalsIgnoreCase(cardNumber)) {
                break;
            }

            card = insuranceCards.stream().filter(c -> c.getCardNumber().equals(cardNumber)).findFirst().orElse(null);
            if (card == null) {
                System.out.println("\nCard number not found. Please try again.\n");
            } else if (!card.getCardHolder().equals(insuredPerson)) {
                System.out.println("\nThis card does not belong to the entered customer. Please try again.\n");
                card = null;
            }
        }

        if (card == null) {
            return;
        }

        Double claimAmount = null;
        while (claimAmount == null) {
            System.out.print("\nEnter claim amount: $");
            String input = scanner.next();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            try {
                claimAmount = Double.parseDouble(input);
                if (claimAmount <= 0) {
                    System.out.println("\nManager.Claim amount must be greater than zero. Please try again.\n");
                    claimAmount = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid claim amount. Please enter a number.\n");
            }
        }

        if (claimAmount == null) {
            return;
        }

        List<String> documentNames = new ArrayList<>();
        while (true) {
            System.out.print("\nEnter documents' names (type 'done' to finish adding documents): ");
            String input = scanner.next();

            if ("exit".equalsIgnoreCase(input)) {
                documentNames = null;
                break;
            } else if ("done".equalsIgnoreCase(input)) {
                break;
            } else if (!input.matches("^[a-zA-Z0-9_]+$")) {
                System.out.println("\nInvalid document name. Please enter a name containing only alphanumeric characters and underscores.\n");
            } else {
                documentNames.add(input);
            }
        }

        if (documentNames == null) {
            return;
        }

        ReceiverBankingInfo receiverBankingInfo = null;
        while (receiverBankingInfo == null) {
            System.out.print("\nEnter receiver banking info (Bank, Name, Banking Number separated by underscore, no spaces allowed): ");
            String receiverBankingInfoInput = scanner.next();
            scanner.nextLine();
            if ("exit".equalsIgnoreCase(receiverBankingInfoInput)) {
                break;
            }

            String[] receiverBankingInfoData = receiverBankingInfoInput.split("_");
            if (receiverBankingInfoData.length != 3) {
                System.out.println("\nInvalid receiver banking info format. Please enter Bank, Name, BankingNumber separated by underscore. Remember, no spaces are allowed.\n");
            } else {
                receiverBankingInfo = new ReceiverBankingInfo(receiverBankingInfoData[0], receiverBankingInfoData[1], receiverBankingInfoData[2]);
            }
        }

        if (receiverBankingInfo == null) {
            return;
        }

        claim.setClaimDate(LocalDate.now());
        claim.setInsuredPerson(insuredPerson);
        claim.setCardNumber(card);
        claim.setExamDate(LocalDate.now().plusDays(1));
        claim.setClaimAmount(claimAmount);
        claim.setStatus(ClaimStatus.NEW);
        claim.getDocuments().clear();
        for (String documentName : documentNames) {
            claim.addDocument(documentName);
        }
        claim.setReceiverBankingInfo(receiverBankingInfo);

        try {
            dataLoader.saveClaims(claims);
            System.out.println("Manager.Claim updated successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error saving claims: " + e.getMessage());
        }
    }

    public void retrieveClaims() {
        try {
            customers = dataLoader.loadCustomers();
            insuranceCards = dataLoader.loadInsuranceCards(customers);
            claims = dataLoader.loadClaims(customers, insuranceCards);
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }

        String customerId = login.getUsername();

        if (!customerId.matches("^c\\d{7}$")) {
            System.out.println("\nInvalid username. The format should be 'c' followed by 7 digits.");
            return;
        }

        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        if (customer != null) {
            List<Claim> customerClaims = customer.getAll(customerId);
            if (customerClaims != null && !customerClaims.isEmpty()) {
                System.out.println("\nClaims for customer " + customerId + ": ");
                for (Claim claim : customerClaims) {
                    System.out.println(claim);
                }
            } else {
                System.out.println("No claims found for this customer.");
            }
        } else {
            System.out.println("Manager.Customer not found!");
        }
    }

    public void getInformation() {
        String customerId = login.getUsername();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);

        if (customer != null) {
            System.out.println("\n" + customer);
        } else {
            System.out.println("Manager.Customer not found!");
        }
    }

    public void updateInformation() {
        String customerId = login.getUsername();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);

        if (customer != null) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter new phone number: ");
            String newPhoneNumber = scanner.nextLine();
            System.out.print("Enter new address: ");
            String newAddress = scanner.nextLine();
            System.out.print("Enter new email: ");
            String newEmail = scanner.nextLine();
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();

            customer.setPhoneNumber(newPhoneNumber);
            customer.setAddress(newAddress);
            customer.setEmail(newEmail);
            login.setPassword(newPassword); // Update password in the login object

            try {
                dataLoader.saveCustomers(customers);
                List<Login> logins = dataLoader.loadLogins(); // Call loadLogins from the Manager.DataLoader instance
                logins.stream().filter(l -> l.getUsername().equals(login.getUsername())).findFirst().ifPresent(currentLogin -> currentLogin.setPassword(newPassword));

                dataLoader.saveAccounts(logins);
                System.out.println("\nInformation updated successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("Error saving customers: " + e.getMessage());
            }
        } else {
            System.out.println("Manager.Customer not found!");
        }
    }

    public void updateDependantInformation() {
        String customerId = login.getUsername();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);

        if (customer instanceof PolicyHolder policyHolder) {

            System.out.print("Enter the dependent's ID you want to update: ");
            String dependentId = scanner.next();
            scanner.nextLine();
            Dependent dependent = policyHolder.getDependents().stream().filter(d -> d.getId().equals(dependentId)).findFirst().orElse(null);

            if (dependent == null) {
                System.out.println("Manager.Dependent not found or you do not have permission to update this dependent!");
                return;
            }

            System.out.print("Enter new phone number: ");
            String newPhoneNumber = scanner.nextLine();
            System.out.print("Enter new address: ");
            String newAddress = scanner.nextLine();
            System.out.print("Enter new email: ");
            String newEmail = scanner.nextLine();

            dependent.setPhoneNumber(newPhoneNumber);
            dependent.setAddress(newAddress);
            dependent.setEmail(newEmail);

            try {
                dataLoader.saveCustomers(customers);
                System.out.println("\nManager.Dependent's information updated successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("Error saving customers: " + e.getMessage());
            }
        } else {
            System.out.println("Manager.Customer not found or not a policy holder.");
        }
    }

    public void getDependentsInformation() {
        String customerId = login.getUsername();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        if (customer instanceof PolicyHolder policyHolder) {
            List<Dependent> dependents = policyHolder.getDependents();
            if (dependents.isEmpty()) {
                System.out.println("No dependents found for this policy holder.");
            } else {
                System.out.println("\nDependents of user " + customerId);
                for (Dependent dependent : dependents) {
                    System.out.println(dependent);
                }
            }
        } else {
            System.out.println("Manager.Customer not found or not a policy holder.");
        }
    }

    public void updateDependentInformation() {
        String customerId = login.getUsername();
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);

        if (!(customer instanceof PolicyHolder policyHolder)) {
            System.out.println("Logged in user is not a policy holder.");
            return;
        }

        System.out.print("Enter dependent's ID: ");
        String dependentId = scanner.nextLine();
        Dependent dependent = policyHolder.getDependents().stream().filter(d -> d.getId().equals(dependentId)).findFirst().orElse(null);

        if (dependent == null) {
            System.out.println("Manager.Dependent not found!");
            return;
        }

        System.out.print("Enter new phone number: ");
        String newPhoneNumber = scanner.nextLine();
        System.out.print("Enter new address: ");
        String newAddress = scanner.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();

        dependent.setPhoneNumber(newPhoneNumber);
        dependent.setAddress(newAddress);
        dependent.setEmail(newEmail);

        try {
            dataLoader.saveCustomers(customers);
            System.out.println("\nManager.Dependent's information updated successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }

    public void fileBeneficiaryClaim() {
        if (!(login.getRole().equals("policy owner"))) {
            System.out.println("Logged in user is not a policy owner.");
            return;
        }

        System.out.println("\nPlease enter the claim details for the beneficiary (or type 'exit' at any time to return to the main menu)\n");

        StringBuilder id = new StringBuilder("f");
        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }

        System.out.print("Enter beneficiary ID: ");
        String beneficiaryId = scanner.next();
        if ("exit".equalsIgnoreCase(beneficiaryId)) {
            return;
        }

        Customer beneficiary = customers.stream().filter(c -> c.getId().equals(beneficiaryId)).findFirst().orElse(null);
        if (beneficiary == null) {
            System.out.println("\nBeneficiary not found. Please try again.\n");
            return;
        }

        // Use beneficiary as the insured person
        Customer insuredPerson = beneficiary;

        // Enter card number
        System.out.print("\nEnter card number: ");
        String cardNumber = scanner.next();
        if ("exit".equalsIgnoreCase(cardNumber)) {
            return;
        }


        InsuranceCard card = insuranceCards.stream().filter(c -> c.getCardNumber().equals(cardNumber) && c.getCardHolder().getId().equals(beneficiaryId)).findFirst().orElse(null);
        if (card == null) {
            System.out.println("\nCard number not found or does not belong to the beneficiary. Please try again.\n");
            return;
        }

        // Enter claim amount
        Double claimAmount = null;
        while (claimAmount == null) {
            System.out.print("\nEnter claim amount: $");
            String input = scanner.next();
            if ("exit".equalsIgnoreCase(input)) {
                return;
            }

            try {
                claimAmount = Double.parseDouble(input);
                if (claimAmount <= 0) {
                    System.out.println("\nManager.Claim amount must be greater than zero. Please try again.\n");
                    claimAmount = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid claim amount. Please enter a number.\n");
            }
        }

        // Enter documents' names
        List<String> documentNames = new ArrayList<>();
        while (true) {
            System.out.print("\nEnter documents' names (type 'done' to finish adding documents): ");
            String input = scanner.next();

            if ("exit".equalsIgnoreCase(input)) {
                documentNames = null;
                break;
            } else if ("done".equalsIgnoreCase(input)) {
                break;
            } else if (!input.matches("^[a-zA-Z0-9_]+$")) {
                System.out.println("\nInvalid document name. Please enter a name containing only alphanumeric characters and underscores.\n");
            } else {
                documentNames.add(input);
            }
        }

        if (documentNames == null) {
            return;
        }

        // Enter receiver banking info
        ReceiverBankingInfo receiverBankingInfo = null;
        while (receiverBankingInfo == null) {
            System.out.print("\nEnter receiver banking info (Bank, Name, Banking Number separated by underscore, no spaces allowed): ");
            String receiverBankingInfoInput = scanner.next();
            scanner.nextLine();
            if ("exit".equalsIgnoreCase(receiverBankingInfoInput)) {
                break;
            }

            String[] receiverBankingInfoData = receiverBankingInfoInput.split("_");
            if (receiverBankingInfoData.length != 3) {
                System.out.println("\nInvalid receiver banking info format. Please enter Bank, Name, BankingNumber separated by underscore. Remember, no spaces are allowed.\n");
            } else {
                receiverBankingInfo = new ReceiverBankingInfo(receiverBankingInfoData[0], receiverBankingInfoData[1], receiverBankingInfoData[2]);
            }
        }

        if (receiverBankingInfo == null) {
            return;
        }

        // Create new claim
        Claim newClaim = new Claim();
        newClaim.setId(id.toString());
        newClaim.setClaimDate(LocalDate.now());
        newClaim.setInsuredPerson(insuredPerson);
        newClaim.setCardNumber(card);
        newClaim.setExamDate(LocalDate.now().plusDays(1));
        newClaim.setClaimAmount(claimAmount);
        newClaim.setStatus(ClaimStatus.NEW);

        for (String documentName : documentNames) {
            newClaim.addDocument(documentName);
        }

        newClaim.setReceiverBankingInfo(receiverBankingInfo);
        claims.add(newClaim);
        insuredPerson.add(newClaim);

        try {
            dataLoader.saveClaims(claims);
            System.out.println("Manager.Claim added successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public void updateBeneficiaryClaim() {
        if (!(login.getRole().equals("policy owner"))) {
            System.out.println("Logged in user is not a policy owner.");
            return;
        }

        System.out.print("Enter beneficiary ID: ");
        String beneficiaryId = scanner.next();
        Customer beneficiary = customers.stream().filter(c -> c.getId().equals(beneficiaryId)).findFirst().orElse(null);
        if (beneficiary == null) {
            System.out.println("\nBeneficiary not found. Please try again.\n");
            return;
        }

        System.out.print("Enter claim ID to update: ");
        String claimId = scanner.next();
        Claim claimToUpdate = claims.stream().filter(c -> c.getId().equals(claimId) && c.getInsuredPerson().getId().equals(beneficiaryId)).findFirst().orElse(null);
        if (claimToUpdate == null) {
            System.out.println("\nManager.Claim not found. Please try again.\n");
            return;
        }

        System.out.print("Enter new claim amount: ");
        double newClaimAmount = scanner.nextDouble();
        claimToUpdate.setClaimAmount(newClaimAmount);

        try {
            dataLoader.saveClaims(claims);  // Save claims to file
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while saving the claims.");
        }

        System.out.println("Manager.Claim updated successfully.");
    }

    public void deleteBeneficiaryClaim() {
        if (!(login.getRole().equals("policy owner"))) {
            System.out.println("Logged in user is not a policy owner.");
            return;
        }

        System.out.print("Enter beneficiary ID: ");
        String beneficiaryId = scanner.next();
        Customer beneficiary = customers.stream().filter(c -> c.getId().equals(beneficiaryId)).findFirst().orElse(null);
        if (beneficiary == null) {
            System.out.println("\nBeneficiary not found. Please try again.\n");
            return;
        }

        System.out.print("Enter claim ID to delete: ");
        String claimId = scanner.next();
        Claim claimToDelete = claims.stream().filter(c -> c.getId().equals(claimId) && c.getInsuredPerson().getId().equals(beneficiaryId)).findFirst().orElse(null);
        if (claimToDelete == null) {
            System.out.println("\nManager.Claim not found. Please try again.\n");
            return;
        }

        claims.remove(claimToDelete);
        beneficiary.delete(claimToDelete);

        try {
            dataLoader.saveClaims(claims);
            System.out.println("Manager.Claim deleted successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public void retrieveBeneficiaryClaims() {
        if (!(login.getRole().equals("policy owner"))) {
            System.out.println("Logged in user is not a policy owner.");
            return;
        }

        System.out.print("Enter beneficiary ID: ");
        String beneficiaryId = scanner.next();
        Customer beneficiary = customers.stream().filter(c -> c.getId().equals(beneficiaryId)).findFirst().orElse(null);
        if (beneficiary == null) {
            System.out.println("\nBeneficiary not found. Please try again.\n");
            return;
        }

        List<Claim> beneficiaryClaims = claims.stream().filter(c -> c.getInsuredPerson().getId().equals(beneficiaryId)).toList();
        if (beneficiaryClaims.isEmpty()) {
            System.out.println("No claims found for this beneficiary.");
        } else {
            System.out.println("Claims for beneficiary " + beneficiaryId + ": ");
            for (Claim claim : beneficiaryClaims) {
                System.out.println(claim);
            }
        }
    }

    public void addBeneficiary() {
        StringBuilder idBuilder = new StringBuilder("c");
        for (int i = 0; i < 7; i++) {
            idBuilder.append(random.nextInt(7));
        }
        String beneficiaryId = idBuilder.toString();
        String role;

        System.out.print("Enter beneficiary full name: ");
        String beneficiaryFullName = scanner.nextLine();

        System.out.print("Enter beneficiary phone number: ");
        String beneficiaryPhoneNumber = scanner.nextLine();

        System.out.print("Enter beneficiary address: ");
        String beneficiaryAddress = scanner.nextLine();

        System.out.print("Enter beneficiary email: ");
        String beneficiaryEmail = scanner.nextLine();

        // Ask for the type of beneficiary
        System.out.print("Enter type of beneficiary (1 for Manager.PolicyHolder, 2 for Manager.Dependent): ");
        int beneficiaryType = scanner.nextInt();
        scanner.nextLine(); // consume the newline

        Customer beneficiary;

        if (beneficiaryType == 1) {
            beneficiary = new PolicyHolder(beneficiaryId, beneficiaryFullName, null);
            role = "policy holder";
        } else if (beneficiaryType == 2) {
            System.out.print("Enter policy holder ID: ");
            String policyHolderId = scanner.nextLine();
            PolicyHolder policyHolder = (PolicyHolder) customers.stream()
                    .filter(c -> c.getId().equals(policyHolderId) && c instanceof PolicyHolder)
                    .findFirst()
                    .orElse(null);
            if (policyHolder == null) {
                System.out.println("Policy holder not found. Please enter a valid policy holder ID.");
                return;
            }
            beneficiary = new Dependent(beneficiaryId, beneficiaryFullName, null, policyHolder);
            role = "dependent";
        } else {
            System.out.println("Invalid customer role. Please enter 1 for Manager.PolicyHolder or 2 for Manager.Dependent.");
            return;
        }

        beneficiary.setPhoneNumber(beneficiaryPhoneNumber);
        beneficiary.setAddress(beneficiaryAddress);
        beneficiary.setEmail(beneficiaryEmail);

        // Add the new beneficiary to the policy owner's list of beneficiaries
        String policyOwnerId = login.getUsername();
        Customer policyOwner = customers.stream().filter(c -> c.getId().equals(policyOwnerId)).findFirst().orElse(null);
        if (policyOwner instanceof PolicyHolder) {
            assert beneficiary instanceof Dependent;
            ((PolicyHolder) policyOwner).addDependent((Dependent) beneficiary);
        }

        customers.add(beneficiary);

        try {
            dataLoader.appendCustomerToFile(beneficiary);
            dataLoader.appendAccountToFile(beneficiaryId, role);
            System.out.println("\nManager.Customer added successfully!");
            System.out.println("An account is also created for the customer, with username is " + beneficiaryId + " and password is '1'.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving customers.");
        }
    }

    public void updateBeneficiary() {
        System.out.print("Enter beneficiary ID: ");
        String beneficiaryId = scanner.nextLine();
        Customer beneficiary = customers.stream().filter(c -> c.getId().equals(beneficiaryId)).findFirst().orElse(null);

        if (beneficiary != null) {
            System.out.print("Enter new beneficiary full name: ");
            String newBeneficiaryFullName = scanner.nextLine();
            System.out.print("Enter new beneficiary phone number: ");
            String newBeneficiaryPhoneNumber = scanner.nextLine();
            System.out.print("Enter new beneficiary address: ");
            String newBeneficiaryAddress = scanner.nextLine();
            System.out.print("Enter new beneficiary email: ");
            String newBeneficiaryEmail = scanner.nextLine();

            beneficiary.setFullName(newBeneficiaryFullName);
            beneficiary.setPhoneNumber(newBeneficiaryPhoneNumber);
            beneficiary.setAddress(newBeneficiaryAddress);
            beneficiary.setEmail(newBeneficiaryEmail);

            try {
                dataLoader.saveCustomers(customers);
                System.out.println("\nBeneficiary's information updated successfully!");
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            }
        } else {
            System.out.println("Beneficiary not found!");
        }
    }

    public void removeBeneficiary() {
        System.out.print("Enter beneficiary ID: ");
        String beneficiaryId = scanner.nextLine();
        Customer beneficiary = customers.stream().filter(c -> c.getId().equals(beneficiaryId)).findFirst().orElse(null);

        if (beneficiary != null) {
            customers.remove(beneficiary);
            try {
                dataLoader.saveCustomers(customers);
                System.out.println("\nBeneficiary removed successfully!");
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            }
        } else {
            System.out.println("Beneficiary not found!");
        }
    }

    public void getAllBeneficiaries() {
        PolicyHolder policyOwner = (PolicyHolder) customers.stream()
                .filter(c -> c.getInsuranceCard().getPolicyOwner().equals(login.getUsername()))
                .findFirst()
                .orElse(null);

        // If the policy owner is found
        if (policyOwner != null) {
            List<Dependent> dependents = policyOwner.getDependents();

            if (dependents.isEmpty()) {
                System.out.println("No beneficiaries found.");
            } else {
                System.out.println("Beneficiaries:\n");
                for (Dependent dependent : dependents) {
                    System.out.println(dependent + "\n");
                }
            }
        } else {
            System.out.println("You are not a policy owner.");
        }
    }

    public void createDependent() {
        System.out.print("Enter dependent ID: ");
        String dependentId = scanner.nextLine();
        System.out.print("Enter dependent full name: ");
        String dependentFullName = scanner.nextLine();
        System.out.print("Enter dependent phone number: ");
        String dependentPhoneNumber = scanner.nextLine();
        System.out.print("Enter dependent address: ");
        String dependentAddress = scanner.nextLine();
        System.out.print("Enter dependent email: ");
        String dependentEmail = scanner.nextLine();

        String policyOwnerId = login.getUsername();
        PolicyHolder policyHolder = (PolicyHolder) customers.stream().filter(c -> c.getId().equals(policyOwnerId)).findFirst().orElse(null);
        Dependent dependent = new Dependent(dependentId, dependentFullName, null, policyHolder);
        dependent.setPhoneNumber(dependentPhoneNumber);
        dependent.setAddress(dependentAddress);
        dependent.setEmail(dependentEmail);

        customers.add(dependent);

        try {
            dataLoader.saveCustomers(customers);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while saving customers.");
        }
    }

    public void updateDependent() {
        System.out.print("Enter dependent ID: ");
        String dependentId = scanner.nextLine();

        Dependent dependent = (Dependent) customers.stream().filter(c -> c.getId().equals(dependentId)).findFirst().orElse(null);
        if (dependent != null) {
            System.out.print("Enter new dependent full name: ");
            String newDependentFullName = scanner.nextLine();
            System.out.print("Enter new dependent phone number: ");
            String newDependentPhoneNumber = scanner.nextLine();
            System.out.print("Enter new dependent address: ");
            String newDependentAddress = scanner.nextLine();
            System.out.print("Enter new dependent email: ");
            String newDependentEmail = scanner.nextLine();

            dependent.setFullName(newDependentFullName);
            dependent.setPhoneNumber(newDependentPhoneNumber);
            dependent.setAddress(newDependentAddress);
            dependent.setEmail(newDependentEmail);

            try {
                dataLoader.saveCustomers(customers);
                System.out.println("\nManager.Dependent's information updated successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("Error saving customers: " + e.getMessage());
            }
        } else {
            System.out.println("Manager.Dependent not found.");
        }
    }

    public void readDependent() {
        System.out.print("Enter dependent ID: ");
        String dependentId = scanner.nextLine();

        Dependent dependent = (Dependent) customers.stream().filter(c -> c.getId().equals(dependentId)).findFirst().orElse(null);
        if (dependent != null) {
            System.out.println(dependent);
        } else {
            System.out.println("Manager.Dependent not found.");
        }
    }

    public void deleteDependent() {
        System.out.print("Enter dependent ID: ");
        String dependentId = scanner.nextLine();

        Dependent dependent = (Dependent) customers.stream().filter(c -> c.getId().equals(dependentId)).findFirst().orElse(null);
        if (dependent != null) {
            customers.remove(dependent);

            try {
                dataLoader.saveCustomers(customers);
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while saving customers.");
            }
        } else {
            System.out.println("Manager.Dependent not found.");
        }
    }
    public void retrieveAllClaims() {
        for (Customer customer : customers) {
            List<Claim> customerClaims = customer.getClaims();
            if (customerClaims.isEmpty()) {
                System.out.println("No claims found for customer " + customer.getId());
            } else {
                System.out.println("Claims for customer " + customer.getId() + ": ");
                for (Claim claim : customerClaims) {
                    System.out.println(claim);
                }
            }
        }
    }
    public void retrieveAllCustomers() {
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }public void addCustomer(Customer customer) {
        customers.add(customer);
        try {
            dataLoader.saveCustomers(customers);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while saving customers.");
        }
    }
    public void deleteCustomer() {
        // Check if the logged-in user is an admin
        if (!login.getRole().equals("admin")) {
            System.out.println("Only admins can delete customers.");
            return;
        }

        // Ask for the customer's ID
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();

        // Find the customer in the list of customers
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        if (customer != null) {
            // Remove the customer from the list of customers
            customers.remove(customer);

            // Save the updated list of customers
            try {
                dataLoader.saveCustomers(customers);
                System.out.println("Manager.Customer deleted successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while saving customers.");
            }
        } else {
            System.out.println("Manager.Customer not found.");
        }
    }

    public void updateCustomer() {
        // 1. Check if the logged-in user is an admin
        if (!login.getRole().equals("admin")) {
            System.out.println("Only admins can update customers.");
            return;
        }

        // 2. Ask for the customer's ID
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();

        // 3. Find the customer in the list of customers
        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        if (customer != null) {
            // 4. If the customer is found, ask for the new details and update the customer
            System.out.print("Enter new customer full name: ");
            String newCustomerFullName = scanner.nextLine();
            System.out.print("Enter new customer phone number: ");
            String newCustomerPhoneNumber = scanner.nextLine();
            System.out.print("Enter new customer address: ");
            String newCustomerAddress = scanner.nextLine();
            System.out.print("Enter new customer email: ");
            String newCustomerEmail = scanner.nextLine();

            customer.setFullName(newCustomerFullName);
            customer.setPhoneNumber(newCustomerPhoneNumber);
            customer.setAddress(newCustomerAddress);
            customer.setEmail(newCustomerEmail);

            // 5. Save the updated list of customers
            try {
                dataLoader.saveCustomers(customers);
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while saving customers.");
            }
        } else {
            System.out.println("Manager.Customer not found.");
        }
    }

    public double sumClaimedAmount(String customerId, LocalDate startDate, LocalDate endDate) {
        double totalClaimedAmount = 0;

        // Iterate over all customers
        for (Customer customer : customers) {
            // If the customer ID matches or the customer ID is null (sum up for all customers)
            if (customerId == null || customer.getId().equals(customerId)) {
                // Iterate over all claims of the customer
                for (Claim claim : customer.getClaims()) {
                    // If the claim status is successful
                    if (claim.getStatus() == ClaimStatus.APPROVED) {
                        // If the claim date is within the specified period or the dates are null (sum up for all dates)
                        if ((startDate == null && endDate == null) ||
                                (claim.getClaimDate().isAfter(startDate) && claim.getClaimDate().isBefore(endDate))) {
                            totalClaimedAmount += claim.getClaimAmount();
                        }
                    }
                }
            }
        }

        return totalClaimedAmount;
    }

    public void approveProposedClaim() {
        String managerId = login.getUsername();
        Provider provider = providers.stream().filter(p -> p.getProviderID().equals(managerId)).findFirst().orElse(null);
        if (provider instanceof InsuranceManager manager) {
            System.out.print("Enter claim ID to approve: ");
            String claimId = scanner.nextLine();
            Claim claim = manager.getProposedClaims().stream().filter(c -> c.getId().equals(claimId)).findFirst().orElse(null);
            if (claim != null) {
                claim.setStatus(ClaimStatus.APPROVED);
                System.out.println("Manager.Claim " + claimId + " has been approved.");
            } else {
                System.out.println("This claim is not proposed by you, so you can't approve it.");
            }
        } else {
            System.out.println("Manager.Provider not found or not an insurance manager.");
        }
    }

    public void rejectProposedClaim() {
        String managerId = login.getUsername();
        Provider provider = providers.stream().filter(p -> p.getProviderID().equals(managerId)).findFirst().orElse(null);
        if (provider instanceof InsuranceManager manager) {
            System.out.print("Enter claim ID to reject: ");
            String claimId = scanner.nextLine();
            Claim claim = manager.getProposedClaims().stream().filter(c -> c.getId().equals(claimId)).findFirst().orElse(null);
            if (claim != null) {
                claim.setStatus(ClaimStatus.REJECTED);
                System.out.println("Manager.Claim " + claimId + " has been rejected.");
            } else {
                System.out.println("This claim is not proposed by you, so you can't reject it.");
            }
        } else {
            System.out.println("Manager.Provider not found or not an insurance manager.");
        }
    }
    public void retrieveAllClaimsFiltered(ClaimStatus statusFilter, String customerIdFilter) {
        for (Claim claim : claims) {
            if ((statusFilter == null || claim.getStatus().equals(statusFilter)) &&
                    (customerIdFilter == null || claim.getInsuredPerson().getId().equals(customerIdFilter))) {
                System.out.println(claim);
            }
        }
    }

    public void retrieveAllCustomersFiltered(String roleFilter, String nameFilter) {
        for (Customer customer : customers) {
            if ((roleFilter == null || customer.getClass().getSimpleName().equals(roleFilter)) &&
                    (nameFilter == null || customer.getFullName().contains(nameFilter))) {
                System.out.println(customer);
            }
        }
    }

    public void retrieveInsuranceSurveyors() {
        String managerId = login.getUsername();
        Provider provider = providers.stream().filter(p -> p.getProviderID().equals(managerId)).findFirst().orElse(null);
        if (provider instanceof InsuranceManager manager) {
            manager.retrieveSurveyorsInfo();
        } else {
            System.out.println("Manager.Provider not found or not an insurance manager.");
        }
    }
}