import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Claim {
    private String id;
    private LocalDate claimDate;
    private Customer insuredPerson;
    private InsuranceCard cardNumber;
    private LocalDate examDate;
    private List<String> documents;
    private double claimAmount;
    private ClaimStatus status;
    private ReceiverBankingInfo receiverBankingInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public Customer getInsuredPerson() {
        return insuredPerson;
    }

    public void setInsuredPerson(Customer insuredPerson) {
        this.insuredPerson = insuredPerson;
    }

    public InsuranceCard getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(InsuranceCard cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public ReceiverBankingInfo getReceiverBankingInfo() {
        return receiverBankingInfo;
    }

    public void setReceiverBankingInfo(ReceiverBankingInfo receiverBankingInfo) {
        this.receiverBankingInfo = receiverBankingInfo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Claim ID: %-10s Claim Date: %-10s Insured Person: %-20s Card Number: %-10s Claim Amount: %-10.2f Status: %s\n", id, claimDate, insuredPerson.getFullName(), cardNumber.getCardNumber(), claimAmount, status));

        if (documents != null && !documents.isEmpty()) {
            sb.append("Documents:\n");
            for (String document : documents) {
                sb.append(String.format("\t%s\n", document));
            }
        } else {
            sb.append("No documents.\n");
        }

        if (receiverBankingInfo != null) {
            sb.append("Receiver Banking Info: ").append(receiverBankingInfo).append("\n");
        } else {
            sb.append("No receiver banking info.\n");
        }

        return sb.toString();
    }

    public void addDocument(String documentName) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        String formattedDocumentName = String.format("%s_%s_%s.pdf", this.id, this.cardNumber.getCardNumber(), documentName);
        this.documents.add(formattedDocumentName);
    }
}