package it.polito.ezshop.data;

import java.util.*;

public class SaleTransactionClass implements SaleTransaction{
    private Integer id;
    private Double price;
    private String paymentType;
    private Double discountRate;
    private boolean paid;
    private List<TicketEntry> entries;

    public SaleTransactionClass(Integer id, Double price, String paymentType, Double discountRate, List<TicketEntry> entries) {
        this.id = id;
        this.price = price;
        this.paymentType = paymentType;
        this.discountRate = discountRate;
        this.entries = entries;
    }

    public SaleTransactionClass(){
        this.id = -1;
        this.price = 0.0;
        this.paymentType = "";
        this.discountRate = 0.0;
        this.entries = new ArrayList<TicketEntry>();
        this.paid = false;
    }

    public Integer getId() {
        return id;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public Integer getTicketNumber(){
        return id;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber){
        this.id = ticketNumber;
    }

    @Override
    public List<TicketEntry> getEntries(){
        return this.entries;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entries = entries;
    }

    @Override
    public double getDiscountRate(){
        return this.discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate){
        this.discountRate = discountRate;
    }

    @Override
    public double getPrice(){
        return this.price;
    }

    @Override
    public void setPrice(double price){
        this.price = price;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void addNewTicketEntry(TicketEntryClass t){
        entries.add(t);
    }

    public TicketEntryClass getEntryByBarCode(String bareCode){
        for (TicketEntry t : entries){
            if( t.getBarCode().equals(bareCode))
                return (TicketEntryClass) t;
        }

        return null;
    }

    public double calculatePrice (){
        double sum = 0;
        for (TicketEntry t : entries){
            sum += t.getAmount() * t.getPricePerUnit();
        }
        sum = sum - (sum* discountRate);
        return sum;
    }

    public void updatePrice(){
        Double price = 0.0;
        for(TicketEntry t : entries){
            if(t.getDiscountRate() == 0.0)
                price += t.getPricePerUnit()*t.getAmount();
            else
                price += t.getAmount()*t.getPricePerUnit()*t.getDiscountRate();
        }
        setPrice(price);
    }



}
