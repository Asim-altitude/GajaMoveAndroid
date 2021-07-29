package customer.gajamove.com.gajamove_customer.models;

public class BankObj {
    private String bank_id,name,acc_title,account_number;

    public BankObj() {
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getAcc_title() {
        return acc_title;
    }

    public void setAcc_title(String acc_title) {
        this.acc_title = acc_title;
    }
}
