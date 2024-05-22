public class Dependent extends Customer {
    private PolicyHolder policyHolder;

    public Dependent(String id, String fullName, InsuranceCard insuranceCard, PolicyHolder policyHolder) {
        super(id, fullName, insuranceCard);
        this.policyHolder = policyHolder;
    }

    public PolicyHolder getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(PolicyHolder policyHolder) {
        this.policyHolder = policyHolder;
    }

    @Override
    public String toString() {
        return "Dependent ID: " + getId() + "\n" +
                "Full Name: " + getFullName() + "\n" +
                "Insurance Card: " + (getInsuranceCard() != null ? getInsuranceCard().getCardNumber() : "No card") + "\n" +
                "Phone Number: " + getPhoneNumber() + "\n" +
                "Address: " + getAddress() + "\n" +
                "Email: " + getEmail() + "\n" +
                "Policy Holder: " + (policyHolder != null ? policyHolder.getFullName() : "No policy holder");
    }
}