package Manager;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer implements ClaimProcessManager {
    private String id;
    private String fullName;
    private InsuranceCard insuranceCard;
    private String phoneNumber;
    private String address;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
    private List<Claim> claims;

    public Customer(String id, String fullName, InsuranceCard insuranceCard) {
        this.id = id;
        this.fullName = fullName;
        this.insuranceCard = insuranceCard;
        this.claims = new ArrayList<>();
    }

    public Customer() {}

    public InsuranceCard getInsuranceCard() {
        return insuranceCard;
    }

    public void setInsuranceCard(InsuranceCard insuranceCard) {
        this.insuranceCard = insuranceCard;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Customer ID: " + id + "\n" +
            "Full Name: " + fullName + "\n" +
            "Insurance Card: " + (insuranceCard != null ? insuranceCard.getCardNumber() : "No card") + "\n" +
            "Phone Number: " + phoneNumber + "\n" +
            "Address: " + address + "\n" +
            "Email: " + email;
    }

    @Override
    public void add(Claim claim) {
        this.claims.add(claim);
    }

    @Override
    public void update(Claim claim, ClaimStatus status) {
        int index = this.claims.indexOf(claim);
        if (index != -1) {
            claim.setStatus(status);
            this.claims.set(index, claim);
        }
    }

    @Override
    public void delete(Claim claim) {
        this.claims.remove(claim);
    }

    @Override
    public Claim getOne(String id) {
        for (Claim claim : this.claims) {
            if (claim.getId().equals(id)) {
                return claim;
            }
        }
        return null;
    }

    @Override
    public List<Claim> getAll(String customerId) {
        if (this.id.equals(customerId)) {
            return this.claims;
        }
        return null;
    }
}