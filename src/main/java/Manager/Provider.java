package Manager;

public class Provider {
    private String providerID;
    private String providerName;
    private String providerAddress;

    public Provider(String providerID, String providerName, String providerAddress) {
        this.providerID = providerID;
        this.providerName = providerName;
        this.providerAddress = providerAddress;
    }



    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }
    // In the Manager.Provider class
    public String getProviderID() {
        return this.providerID;
    }
}