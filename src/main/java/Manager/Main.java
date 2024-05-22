package Manager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CustomerService customerService = new CustomerService();
        SystemService systemService = new SystemService();
        Scanner scanner = new Scanner(System.in);
        Login login = Login.login();

        while (login == null) {
            System.out.println("\nManager.Login failed! Please try again.\n");
            login = Login.login();
        }

        customerService.setLogin(login);

        while (true) {
            switch (login.getRole()) {
                case "policy holder" -> {
                    System.out.println("\n1. File a claim");
                    System.out.println("2. Update a claim");
                    System.out.println("3. Retrieve your claims");
                    System.out.println("4. Get my information");
                    System.out.println("5. Update my information");
                    System.out.println("6. Update dependent's information");
                    System.out.println("7. Get dependents' information");
                    System.out.println("0. Exit");
                    System.out.print("\nEnter your choice: ");

                    try {
                        int choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                customerService.fileClaim();
                                break;
                            case 2:
                                customerService.updateClaim();
                                break;
                            case 3:
                                customerService.retrieveClaims();
                                break;
                            case 4:
                                customerService.getInformation();
                                break;
                            case 5:
                                customerService.updateInformation();
                                break;
                            case 6:
                                customerService.updateDependantInformation();
                                break;
                            case 7:
                                customerService.getDependentsInformation();
                                break;
                            case 0:
                                System.out.println("\nExiting...");
                                System.exit(0);
                            default:
                                System.out.println("Invalid choice. Please enter a number between 0 and 6.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("\nInvalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
                case "dependent" -> {
                    System.out.println("\n1. Retrieve your claims");
                    System.out.println("2. Show my information");
                    System.out.println("0. Exit");
                    System.out.print("\nEnter your choice: ");

                    try {
                        int choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                customerService.retrieveClaims();
                                break;
                            case 2:
                                customerService.getInformation();
                                break;
                            case 0:
                                System.out.println("\nExiting...");
                                System.exit(0);
                            default:
                                System.out.println("Invalid choice. Please enter a number between 0 and 2.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("\nInvalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
                case "insurance surveyor" -> {
                    System.out.println("\n1. Require more information from a claim");
                    System.out.println("2. Propose a claim to manager");
                    System.out.println("0. Exit");
                    System.out.print("\nEnter your choice: ");

                    try {
                        int choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                // Code to require more information from a claim
                                break;
                            case 2:
                                // Code to propose a claim to manager

                                break;
                            case 0:
                                System.exit(0);
                                break;
                            default:
                                System.out.println("\nInvalid choice. Please try again.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("\nInvalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
                case "insurance manager" -> {
                    System.out.println("\n1. Approve a proposed claim");
                    System.out.println("2. Reject a proposed claim");
                    System.out.println("3. Retrieve all the claims");
                    System.out.println("4. Retrieve all the customers");
                    System.out.println("5. Retrieve information of insurance surveyors");
                    System.out.println("0. Exit");
                    System.out.print("\nEnter your choice: ");

                    try {
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                        switch (choice) {
                            case 1:
                                // Code to approve a proposed claim
                                customerService.approveProposedClaim();
                                break;
                            case 2:
                                // Code to reject a proposed claim
                                customerService.rejectProposedClaim();
                                break;
                            case 3:
                                // Code to retrieve all the claims
                                System.out.print("Enter status filter (APPROVED, REJECTED, or leave blank for all): ");
                                String statusFilterStr = scanner.nextLine();
                                ClaimStatus statusFilter = statusFilterStr.isEmpty() ? null : ClaimStatus.valueOf(statusFilterStr);
                                System.out.print("Enter customer ID filter (or leave blank for all): ");
                                String customerIdFilter = scanner.nextLine();
                                customerIdFilter = customerIdFilter.isEmpty() ? null : customerIdFilter;
                                customerService.retrieveAllClaimsFiltered(statusFilter, customerIdFilter);
                                break;
                            case 4:
                                // Code to retrieve all the customers
                                System.out.print("Enter role filter (Manager.PolicyHolder, Manager.Dependent, or leave blank for all): ");
                                String roleFilter = scanner.nextLine();
                                roleFilter = roleFilter.isEmpty() ? null : roleFilter;
                                System.out.print("Enter name filter (or leave blank for all): ");
                                String nameFilter = scanner.nextLine();
                                nameFilter = nameFilter.isEmpty() ? null : nameFilter;
                                customerService.retrieveAllCustomersFiltered(roleFilter, nameFilter);
                                break;
                            case 5:
                                // Code to retrieve information of insurance surveyors
                                customerService.retrieveInsuranceSurveyors();
                                break;
                            case 0:
                                System.exit(0);
                                break;
                            default:
                                System.out.println("\nInvalid choice. Please try again.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("\nInvalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
                case "admin" -> {
                    System.out.println("\n1. Display all claims");
                    System.out.println("2. Display all customers");
                    System.out.println("3. Add a customer");
                    System.out.println("4. Update a customer's information");
                    System.out.println("5. Delete a customer");
                    System.out.println("6. Sum up the successfully claimed amount");
                    System.out.println("0. Exit");
                    System.out.print("\nEnter your choice: ");

                    try {
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                        switch (choice) {
                            case 1:
                                systemService.retrieveAllClaims();
                                break;
                            case 2:
                                systemService.retrieveAllCustomers();
                                break;
                            case 3:
                                systemService.addCustomer();
                                break;
                            case 4:
                                systemService.updateCustomer();
                                break;
                            case 5:
                                systemService.deleteCustomer();
                                break;
                            case 6:
                                systemService.totalClaimedAmount();
                                break;
                            case 0:
                                System.out.println("\nExiting...");
                                System.exit(0);
                            default:
                                System.out.println("Invalid choice. Please enter a number between 0 and 6.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("\nInvalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
                case "policy owner" -> {
                    System.out.println("\n1. File a claim for a beneficiary");
                    System.out.println("2. Update a claim for a beneficiary");
                    System.out.println("3. Delete a claim for a beneficiary");
                    System.out.println("4. Retrieve claims for a beneficiary");
                    System.out.println("5. Add a beneficiary");
                    System.out.println("6. Update a beneficiary's information");
                    System.out.println("7. Delete a beneficiary");
                    System.out.println("8. Get all beneficiaries");
                    System.out.println("0. Exit");
                    System.out.print("\nEnter your choice: ");

                    try {
                        int choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                customerService.fileBeneficiaryClaim();
                                break;
                            case 2:
                                customerService.updateBeneficiaryClaim();
                                break;
                            case 3:
                                customerService.deleteBeneficiaryClaim();
                                break;
                            case 4:
                                customerService.retrieveBeneficiaryClaims();
                                break;
                            case 5:
                                customerService.addBeneficiary();
                                break;
                            case 6:
                                customerService.updateBeneficiary();
                                break;
                            case 7:
                                customerService.removeBeneficiary();
                                break;
                            case 8:
                                customerService.getAllBeneficiaries();
                                break;
                            case 0:
                                System.out.println("\nExiting...");
                                System.exit(0);
                            default:
                                System.out.println("Invalid choice. Please enter a number between 0 and 4.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("\nInvalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }
            }
        }
    }
}