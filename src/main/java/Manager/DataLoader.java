package Manager;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class DataLoader {
    public List<Customer> loadCustomers() throws FileNotFoundException {
        List<Customer> customers = new ArrayList<>();
        Map<String, PolicyHolder> policyHolders = new HashMap<>();
        Scanner scanner = new Scanner(new File("src/main/resources/database/customers.txt"));
        while (scanner.hasNextLine()) {
            String[] data = scanner.nextLine().split(",");
            if (data[2].equals("PolicyHolder")) {
                PolicyHolder policyHolder = new PolicyHolder(data[0], data[1], null); // InsuranceCard will be set later
                policyHolder.setPhoneNumber(data[3]);
                policyHolder.setAddress(data[4]);
                policyHolder.setEmail(data[5]);
                customers.add(policyHolder);
                policyHolders.put(data[0], policyHolder);
            } else if (data[2].equals("Dependent")) {
                PolicyHolder policyHolder = policyHolders.get(data[3]); // Get the policyholder for this dependent
                if (policyHolder != null) {
                    Dependent dependent = new Dependent(data[0], data[1], null, policyHolder); // InsuranceCard will be set later
                    dependent.setPhoneNumber(data[4]);
                    dependent.setAddress(data[5]);
                    dependent.setEmail(data[6]);
                    customers.add(dependent);
                    policyHolder.addDependent(dependent); // Add the dependent to the policyholder
                }
            }
        }
        scanner.close();
        return customers;
    }

    public List<InsuranceCard> loadInsuranceCards(List<Customer> customers) throws FileNotFoundException {
        List<InsuranceCard> insuranceCards = new ArrayList<>();
        Scanner scanner = new Scanner(new File("src/main/resources/database/insuranceCards.txt"));
        while (scanner.hasNextLine()) {
            String[] data = scanner.nextLine().split(",");
            Customer cardHolder = customers.stream().filter(c -> c.getId().equals(data[1])).findFirst().orElse(null);
            if (cardHolder != null) {
                InsuranceCard insuranceCard = new InsuranceCard(data[0], cardHolder, data[2], data[3]);
                insuranceCards.add(insuranceCard);
                cardHolder.setInsuranceCard(insuranceCard);
            }
        }
        scanner.close();
        return insuranceCards;
    }

    public List<Claim> loadClaims(List<Customer> customers, List<InsuranceCard> insuranceCards) throws FileNotFoundException {
        List<Claim> claims = new ArrayList<>();
        Scanner scanner = new Scanner(new File("src/main/resources/database/claims.txt"));
        while (scanner.hasNextLine()) {
            String[] data = scanner.nextLine().split(",");
            Customer insuredPerson = customers.stream().filter(c -> c.getId().equals(data[2])).findFirst().orElse(null);
            InsuranceCard card = insuranceCards.stream().filter(c -> c.getCardNumber().equals(data[3])).findFirst().orElse(null);
            if (insuredPerson != null && card != null) {
                try {
                    Claim claim = new Claim();
                    claim.setId(data[0]);
                    claim.setClaimDate(LocalDate.parse(data[1]));
                    claim.setInsuredPerson(insuredPerson);
                    claim.setCardNumber(card);
                    claim.setExamDate(LocalDate.parse(data[4]));
                    claim.setClaimAmount(Double.parseDouble(data[5]));
                    claim.setStatus(ClaimStatus.valueOf(data[6]));

                    // Add documents to the claim
                    String[] documents = data[7].split("\\|");
                    for (String document : documents) {
                        claim.addDocument(document);
                    }

                    // Add receiverBankingInfo to the claim
                    String[] receiverBankingInfoData = data[8].split("_");
                    ReceiverBankingInfo receiverBankingInfo = new ReceiverBankingInfo(receiverBankingInfoData[0], receiverBankingInfoData[1], receiverBankingInfoData[2]);
                    claim.setReceiverBankingInfo(receiverBankingInfo);

                    claims.add(claim);
                    insuredPerson.add(claim);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format in claims data. Skipping claim with ID: " + data[0]);
                }
            }
        }
        scanner.close();
        return claims;
    }

    public void saveCustomers(List<Customer> customers) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("src/main/resources/database/customers.txt");
        for (Customer customer : customers) {
            if (customer instanceof PolicyHolder policyHolder) {
                writer.println(customer.getId() + "," + customer.getFullName() + ",PolicyHolder," + policyHolder.getPhoneNumber() + "," + policyHolder.getAddress() + "," + policyHolder.getEmail());
                for (Dependent dependent : policyHolder.getDependents()) {
                    writer.println(dependent.getId() + "," + dependent.getFullName() + ",Dependent," + policyHolder.getId() + "," + dependent.getPhoneNumber() + "," + dependent.getAddress() + "," + dependent.getEmail());
                }
            }
        }
        writer.close();
    }

    public void saveInsuranceCards(List<InsuranceCard> insuranceCards) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("src/main/resources/database/insuranceCards.txt");
        for (InsuranceCard insuranceCard : insuranceCards) {
            writer.println(insuranceCard.getCardNumber() + "," + insuranceCard.getCardHolder().getId() + "," + insuranceCard.getPolicyOwner() + "," + insuranceCard.getExpirationDate());
        }
        writer.close();
    }

    public void saveClaims(List<Claim> claims) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("src/main/resources/database/claims.txt");
        for (Claim claim : claims) {
            List<String> documentNames = new ArrayList<>();
            for (String document : claim.getDocuments()) {
                // Extract the document name from the formatted document name
                String[] parts = document.split("_");
                String documentName = parts[parts.length - 1].replace(".pdf", "");
                documentNames.add(documentName);
            }
            String documents = String.join("|", documentNames);
            String receiverBankingInfo = claim.getReceiverBankingInfo().toSaveFormat();
            writer.println(claim.getId() + "," + claim.getClaimDate() + "," + claim.getInsuredPerson().getId() + "," + claim.getCardNumber().getCardNumber() + "," + claim.getExamDate() + "," + claim.getClaimAmount() + "," + claim.getStatus() + "," + documents + "," + receiverBankingInfo);
        }
        writer.close();
    }

    public void saveAccounts(List<Login> logins) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("src/main/resources/database/accounts.txt");
        for (Login login : logins) {
            writer.println(login.getUsername() + "," + login.getPassword() + "," + login.getRole());
        }
        writer.close();
    }

    public List<Login> loadLogins() throws FileNotFoundException {
        List<Login> logins = new ArrayList<>();
        Scanner scanner = new Scanner(new File("src/main/resources/database/accounts.txt"));
        while (scanner.hasNextLine()) {
            String[] data = scanner.nextLine().split(",");
            Login login = new Login(data[0], data[1], data[2]);
            logins.add(login);
        }
        scanner.close();
        return logins;
    }

    public void saveProviders(List<Provider> providers) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("src/main/resources/database/providers.txt");
        for (Provider provider : providers) {
            if (provider instanceof InsuranceManager manager) {
                String proposedClaims = manager.getProposedClaims().stream().map(Claim::getId).collect(Collectors.joining("|"));
                writer.println(manager.getProviderID() + "," + manager.getProviderName() + "," + manager.getProviderAddress() + ",InsuranceManager," + proposedClaims);
            } else if (provider instanceof InsuranceSurveyor surveyor) {
                InsuranceManager manager = surveyor.getManager(providers);
                if (manager != null) {
                    writer.println(surveyor.getProviderID() + "," + surveyor.getProviderName() + "," + surveyor.getProviderAddress() + ",InsuranceSurveyor," + manager.getProviderID());
                } else {
                    writer.println(surveyor.getProviderID() + "," + surveyor.getProviderName() + "," + surveyor.getProviderAddress() + ",InsuranceSurveyor");
                }
            }
        }
        writer.close();
    }

    public List<Provider> loadProviders() throws FileNotFoundException {
        List<Provider> providers = new ArrayList<>();
        Map<String, InsuranceManager> managers = new HashMap<>();
        Scanner scanner = new Scanner(new File("src/main/resources/database/providers.txt"));
        while (scanner.hasNextLine()) {
            String[] data = scanner.nextLine().split(",");
            if (data[3].equals("InsuranceManager")) {
                InsuranceManager manager = new InsuranceManager(data[0], data[1], data[2]);
                String[] claimIds = data[4].split("\\|");
                for (String claimId : claimIds) {
                    Claim claim = new Claim();
                    claim.setId(claimId);
                    manager.getProposedClaims().add(claim);
                }
                providers.add(manager);
                managers.put(manager.getProviderID(), manager);
            } else if (data[3].equals("InsuranceSurveyor")) {
                InsuranceSurveyor surveyor = new InsuranceSurveyor(data[0], data[1], data[2]);
                providers.add(surveyor);
                // Associate the surveyor with its manager
                if (data.length > 4) {
                    InsuranceManager manager = managers.get(data[4]);
                    if (manager != null) {
                        manager.addSurveyor(surveyor);
                    }
                }
            }
        }
        scanner.close();
        return providers;
    }

    public void appendPolicyHolderToFile(Customer customer) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter("src/main/resources/database/customers.txt", true))) {
            String customerRole = customer instanceof PolicyHolder ? "PolicyHolder" : "Dependent";
            String policyHolderId = customer instanceof Dependent ? ((Dependent) customer).getPolicyHolder().getId() : "";
            out.println(customer.getId() + "," + customer.getFullName() + "," + customerRole + "," + policyHolderId + customer.getPhoneNumber() + "," + customer.getAddress() + "," + customer.getEmail());
        }
    }

    public void appendDependentToFile(Customer customer) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter("src/main/resources/database/customers.txt", true))) {
            String customerRole = customer instanceof PolicyHolder ? "PolicyHolder" : "Dependent";
            String policyHolderId = customer instanceof Dependent ? ((Dependent) customer).getPolicyHolder().getId() : "";
            out.println(customer.getId() + "," + customer.getFullName() + "," + customerRole + "," + policyHolderId + "," + customer.getPhoneNumber() + "," + customer.getAddress() + "," + customer.getEmail());
        }
    }

    public void appendAccountToFile(String username, String role) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter("src/main/resources/database/accounts.txt", true))) {
            out.println(username + ",1," + role);
        }
    }
}