package Manager;

public class ReceiverBankingInfo {
    private String bank;
    private String name;
    private String number;

    public ReceiverBankingInfo() {
        this.bank = null;
        this.name = null;
        this.number = null;
    }

    public ReceiverBankingInfo(String bank, String name, String number) {
        this.bank = bank;
        this.name = name;
        this.number = number;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Bank: " + bank + ", Name: " + name + ", Number: " + number;
    }

    public String toSaveFormat() {
        return bank + "_" + name + "_" + number;
    }
}