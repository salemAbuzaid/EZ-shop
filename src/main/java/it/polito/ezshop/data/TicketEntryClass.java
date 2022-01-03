package it.polito.ezshop.data;

import java.util.*;

public class TicketEntryClass implements TicketEntry{
   private String barCode ;
   private String productDescription;
   private int amount;
   private double pricePerUnit;
   private double discountRate;
   private List<String> productsRFID;

    public TicketEntryClass(String barCode, String productDescription, int amount, double pricePerUnit, double discountRate) {
        this.barCode = barCode;
        this.productDescription = productDescription;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.discountRate = discountRate;
        this.productsRFID = new ArrayList<>();
    }

    public TicketEntryClass() {
        this.barCode = null;
        this.productDescription = null;
        this.amount = 0;
        this.pricePerUnit = 0;
        this.discountRate = 0;
        this.productsRFID = new ArrayList<>();
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProductDescription(){
        return productDescription;
    }

    public void setProductDescription(String productDescription){
        this.productDescription = productDescription;
    }

    public int getAmount(){
        return amount;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public double getPricePerUnit(){
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit){
        this.pricePerUnit = pricePerUnit;
    }

    public double getDiscountRate(){
        return discountRate;
    }

    public void setDiscountRate(double discountRate){
        this.discountRate = discountRate;
    }

    public List<String> getProductsRFID() {
        return productsRFID;
    }

    public boolean addRFID(String RFID){ return productsRFID.add(RFID);}

    public boolean removeRFID(String RFID){
        return productsRFID.remove(RFID);
    }
}
