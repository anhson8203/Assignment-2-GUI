package Manager;

import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Login {
    private String username;
    private String password;
    private String role;

    public Login(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static Login login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type 'exit' to exit the program.");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        if (username.equals("exit")) {
            System.out.println("\nExiting the program.");
            System.exit(0);
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

    try {
        Scanner fileScanner = new Scanner(new File("accounts.txt"));
        DataLoader dataLoader = new DataLoader();
        List<Customer> customers = dataLoader.loadCustomers();
        List<Provider> providers = dataLoader.loadProviders();

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] parts = line.split(",");
            if (parts.length == 3 && parts[0].equals(username) && parts[1].equals(password)) {
                if (parts[2].equals("admin")) {
                    return new Login(parts[0], parts[1], "admin");
                }
                for (Customer customer : customers) {
                    if (customer.getId().equals(username)) {
                        return new Login(parts[0], parts[1], parts[2]);
                    }
                }
                // Check for Manager.InsuranceSurveyor
                for (Provider provider : providers) {
                    if (provider.getProviderID().equals(username)) {
                        return new Login(parts[0], parts[1], parts[2]);
                    }
                }
                // No matching customer found, check for policy owner
                Scanner insuranceCardScanner = new Scanner(new File("insuranceCards.txt"));
                while (insuranceCardScanner.hasNextLine()) {
                    String insuranceCardLine = insuranceCardScanner.nextLine();
                    String[] insuranceCardParts = insuranceCardLine.split(",");
                    if (insuranceCardParts[2].equals(username)) {
                        return new Login(parts[0], parts[1], "policy owner");
                    }
                }
            }
        }
    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
    }
    return null;
    }
}