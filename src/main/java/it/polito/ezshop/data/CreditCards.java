package it.polito.ezshop.data;

import java.util.TreeMap;

public class CreditCards {
    private TreeMap<String, CreditCard> creditCards = new TreeMap<>();

    public CreditCard getCreditCard(String code){
        return creditCards.get(code);
    }

    public boolean addCreditCard(CreditCard c){
        creditCards.put(c.getCode(), c);
        return true;
    }
}
