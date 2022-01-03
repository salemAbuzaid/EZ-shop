package it.polito.ezshop.data;

import java.time.LocalDate;

public class BalanceOperationClass implements BalanceOperation{

    private Integer id;
    private LocalDate date;
    private Double money;
    private String type;

    public BalanceOperationClass(Integer id, LocalDate date, Double money, String type) {
        this.id = id;
        this.date = date;
        this.money = money;
        this.type = type;
    }

    public BalanceOperationClass() {
        this.id = -1;
        this.date = null;
        this.money = 0.0;
        this.type = "";
    }

    public int getBalanceId() {
        return id;
    }

    @Override
    public void setBalanceId(int balanceId) {
        this.id = balanceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getMoney() {
        return money;
    }

    @Override
    public void setMoney(double money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
