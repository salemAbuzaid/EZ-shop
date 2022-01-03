package it.polito.ezshop.data;

public class CreditCard {
    private String code;
    private Double credit;


    public CreditCard() {}

    public CreditCard(String code, Double credit) {
        this.code = code;
        this.credit = credit;
    }

    public Double getCredit() {
        return credit;
    }

    public String getCode() {
        return code;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
