package Manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SystemService {
    DataLoader dataLoader = new DataLoader();
    Random random = new Random();

    List<Customer> customers;
    List<InsuranceCard> insuranceCards;
    List<Claim> claims;

    Scanner scanner = new Scanner(System.in);

    public SystemService() {
        try {
            customers = dataLoader.loadCustomers();
            insuranceCards = dataLoader.loadInsuranceCards(customers);
            claims = dataLoader.loadClaims(customers, insuranceCards);
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
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
            System.out.println("\n" + customer);
        }
    }

    public void addCustomer() {
        StringBuilder customerIdBuilder = new StringBuilder("c");
        for (int i = 0; i < 7; i++) {
            customerIdBuilder.append(random.nextInt(7));
        }
        String customerId = customerIdBuilder.toString();
        String role;

        System.out.print("Enter customer full name: ");
        String customerFullName = scanner.nextLine();

        System.out.print("Enter customer phone number: ");
        String customerPhoneNumber = scanner.nextLine();

        System.out.print("Enter customer address: ");
        String customerAddress = scanner.nextLine();

        System.out.print("Enter customer email: ");
        String customerEmail = scanner.nextLine();

        System.out.print("Enter customer role (1 for Manager.PolicyHolder, 2 for Manager.Dependent): ");
        int customerRole = scanner.nextInt();
        scanner.nextLine();

        Customer newCustomer;

        if (customerRole == 1) {
            newCustomer = new PolicyHolder(customerId, customerFullName, null);
            role = "policy holder";
        } else if (customerRole == 2) {
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
            newCustomer = new Dependent(customerId, customerFullName, null, policyHolder);
            role = "dependent";
        } else {
            System.out.println("Invalid customer role. Please enter 1 for Manager.PolicyHolder or 2 for Manager.Dependent.");
            return;
        }

        newCustomer.setPhoneNumber(customerPhoneNumber);
        newCustomer.setAddress(customerAddress);
        newCustomer.setEmail(customerEmail);

        customers.add(newCustomer);

        try {
            dataLoader.appendCustomerToFile(newCustomer);
            dataLoader.appendAccountToFile(customerId, role);
            System.out.println("\nManager.Customer added successfully!");
            System.out.println("An account is also created for the customer, with username is " + customerId + " and password is '1'.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving customers.");
        }
    }

    public void updateCustomer() {
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();

        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        if (customer != null) {
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

            try {
                dataLoader.saveCustomers(customers);
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while saving customers.");
            }
        } else {
            System.out.println("Manager.Customer not found!");
        }
    }

    public void deleteCustomer() {
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();

        Customer customer = customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().orElse(null);
        if (customer != null) {
            customers.remove(customer);

            try {
                dataLoader.saveCustomers(customers);
                System.out.println("Manager.Customer deleted successfully.");
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred while saving customers.");
            }
        } else {
            System.out.println("Manager.Customer not found!");
        }
    }

    private double sumClaimedAmount(String customerId, LocalDate startDate, LocalDate endDate) {
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

    public void totalClaimedAmount() {
        System.out.print("Enter customer ID (leave blank for all customers): ");
        String sumCustomerId = scanner.nextLine();
        sumCustomerId = sumCustomerId.isEmpty() ? null : sumCustomerId;

        System.out.print("Enter start date (YYYY-MM-DD, leave blank for all dates): ");
        String startDateStr = scanner.nextLine();
        LocalDate startDate = startDateStr.isEmpty() ? null : LocalDate.parse(startDateStr);

        System.out.print("Enter end date (YYYY-MM-DD, leave blank for all dates): ");
        String endDateStr = scanner.nextLine();
        LocalDate endDate = endDateStr.isEmpty() ? null : LocalDate.parse(endDateStr);

        double totalClaimedAmount = sumClaimedAmount(sumCustomerId, startDate, endDate);
        System.out.println("Total successfully claimed amount: $" + totalClaimedAmount);
    }
}