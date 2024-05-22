package Manager;

import java.util.List;

public class InsuranceSurveyor extends Provider {


    public InsuranceSurveyor(String providerID, String providerName, String providerAddress) {
        super(providerID, providerName, providerAddress);
    }
    // In the Manager.InsuranceSurveyor class
    public InsuranceManager getManager(List<Provider> providers) {
        for (Provider provider : providers) {
            if (provider instanceof InsuranceManager) {
                InsuranceManager manager = (InsuranceManager) provider;
                if (manager.getSurveyors().contains(this)) {
                    return manager;
                }
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return "Surveyor ID: " + getProviderID() + "\n" +
                "Name: " + getProviderName() + "\n" +
                "Address: " + getProviderAddress();
    }

}