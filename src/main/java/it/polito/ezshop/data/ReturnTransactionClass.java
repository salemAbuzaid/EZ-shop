package it.polito.ezshop.data;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

public class ReturnTransactionClass {
    private Integer id;
    private double price;
    private SaleTransactionClass saleTransaction;
    private TreeMap<String , TicketEntryClass> entries = new TreeMap<>();

    public ReturnTransactionClass(Integer id, double price, SaleTransactionClass saleTransaction, TreeMap<String, TicketEntryClass> entries) {
        this.id = id;
        this.price = price;
        this.saleTransaction = saleTransaction;
        this.entries = entries;
    }

    public ReturnTransactionClass(Integer id, double price){
        this.id = id;
        this.price = price;
    }

    public ReturnTransactionClass(){
       id = 0;
       price = 0.0;
       saleTransaction =null;
       entries = new TreeMap<>();
    }

    public void addEntry(String barCode, TicketEntryClass t){
        if (!entries.containsKey(barCode)){
            entries.put(barCode, t);
        }else{
            TicketEntryClass t2 = entries.get(barCode);
            t.setAmount(t.getAmount()+t2.getAmount());
        }

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public SaleTransactionClass getSaleTransaction() {
        return saleTransaction;
    }

    public void setSaleTransaction(SaleTransactionClass saleTransaction) {
        this.saleTransaction = saleTransaction;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public TreeMap<String, TicketEntryClass> getEntries() {
        return entries;
    }

    public ArrayList<TicketEntry> getEntriesList(){
        return new ArrayList<>(entries.values());
    }


    public void updateSaleEntries(boolean returnTransaction){
        for (TicketEntry t : saleTransaction.getEntries()){
            if (returnTransaction){
                t.setAmount(t.getAmount() - entries.get(t.getBarCode()).getAmount());
            }else{
                t.setAmount(t.getAmount() + entries.get(t.getBarCode()).getAmount());
            }
        }
    }

    public void updateProductQuantities(Products products ,boolean returnTransaction){
        ProductTypeClass p;
        for (Map.Entry<String , TicketEntryClass> entry: entries.entrySet()){
            p = products.getProductTypeByBarCode(entry.getValue().getBarCode());
            if (returnTransaction) {
                p.setQuantity(p.getQuantity() + entry.getValue().getAmount());
            } else {
                p.setQuantity(p.getQuantity() - entry.getValue().getAmount());
            }

        }
    }

    public double calculateTotalReturnPrice(){
        double sum =0;
        for (Map.Entry<String , TicketEntryClass> entry: entries.entrySet()){
            sum += entry.getValue().getPricePerUnit()*entry.getValue().getAmount();
        }
        return sum;
    }

}
