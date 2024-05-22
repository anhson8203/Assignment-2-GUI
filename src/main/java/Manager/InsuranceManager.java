package Manager;

import java.util.ArrayList;
import java.util.List;

public class InsuranceManager extends Provider {
    private List<InsuranceSurveyor> surveyors;
    private List<Claim> proposedClaims;

    public List<InsuranceSurveyor> getSurveyors() {
        return surveyors;
    }

    public void setSurveyors(List<InsuranceSurveyor> surveyors) {
        this.surveyors = surveyors;
    }

    public List<Claim> getProposedClaims() {
        return proposedClaims;
    }

    public void setProposedClaims(List<Claim> proposedClaims) {
        this.proposedClaims = proposedClaims;
    }

    public InsuranceManager(String providerID, String providerName, String providerAddress) {
        super(providerID, providerName, providerAddress);
        this.surveyors = new ArrayList<>();
        this.proposedClaims = new ArrayList<>();
    }
    public void addSurveyor(InsuranceSurveyor surveyor) {
        this.surveyors.add(surveyor);
    }
    public void retrieveSurveyorsInfo() {
        System.out.println("\nSurveyors:");
        for (InsuranceSurveyor surveyor : surveyors) {
            System.out.println(surveyor);
        }
    }
}
