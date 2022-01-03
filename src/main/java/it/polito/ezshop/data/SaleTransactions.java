package it.polito.ezshop.data;

import java.util.*;

public class SaleTransactions {
    private Integer newId;
    private TreeMap<Integer,SaleTransactionClass> saleTransactions;

   public SaleTransactionClass getTransaction(Integer id){
        return saleTransactions.get(id);
    }

    public SaleTransactions(TreeMap<Integer, SaleTransactionClass> saleTransactions) {
        this.saleTransactions = saleTransactions;
    }

    public SaleTransactions() {
        this.saleTransactions = new TreeMap<Integer,SaleTransactionClass>();
    }

    public TreeMap<Integer, SaleTransactionClass> getSaleTransactions() {
        return saleTransactions;
    }
/*
    public void setSaleTransactions(TreeMap<Integer, SaleTransactionClass> saleTransactions) {
        this.saleTransactions = saleTransactions;
    }*/

    public void addTransaction(SaleTransactionClass st){
        this.saleTransactions.put(st.getTicketNumber(),st);
    }

    public void removeTransaction(SaleTransactionClass st){
        this.saleTransactions.remove(st.getTicketNumber());
    }

    public Integer getNewId(){
        return ++newId;
    }

    public void setNewId(){
        if(saleTransactions.isEmpty()){
            newId = 0;
        }
        else {
            newId = saleTransactions.lastKey();
        }
    }

    public SaleTransactionClass getSaleTransactionById(Integer id){
        return saleTransactions.get(id);
    }

}
