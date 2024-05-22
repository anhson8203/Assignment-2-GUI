package Manager;

import java.util.ArrayList;
import java.util.List;

public class PolicyHolder extends Customer {
    private List<Dependent> dependents;

    public PolicyHolder(String id, String fullName, InsuranceCard insuranceCard) {
        super(id, fullName, insuranceCard);
        this.dependents = new ArrayList<>();
    }

    public PolicyHolder() {}

    public List<Dependent> getDependents() {
        return dependents;
    }

    public void setDependents(List<Dependent> dependents) {
        this.dependents = dependents;
    }

    @Override
    public String toString() {
        return "Policy Holder ID: " + getId() + "\n" +
            "Full Name: " + getFullName() + "\n" +
            "Insurance Card: " + (getInsuranceCard() != null ? getInsuranceCard().getCardNumber() : "No card") + "\n" +
            "Phone Number: " + getPhoneNumber() + "\n" +
            "Address: " + getAddress() + "\n" +
            "Email: " + getEmail();
    }

    public void addDependent(Dependent dependent) {
        this.dependents.add(dependent);
    }
}