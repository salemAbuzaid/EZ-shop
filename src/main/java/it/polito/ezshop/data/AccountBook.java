package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.*;

public class AccountBook {
    private Integer newId;
    Double currentBalance;
    TreeMap<Integer, BalanceOperationClass> balanceOperationList;

    public AccountBook(Double currentBalance, TreeMap<Integer, BalanceOperationClass> balanceOperationList) {
        this.currentBalance = currentBalance;
        this.balanceOperationList = balanceOperationList;
    }

    public AccountBook(){
        this.currentBalance=0.0;
        this.balanceOperationList=new TreeMap<>();
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<BalanceOperationClass> getBalanceOperationList() {
        return new ArrayList<>(balanceOperationList.values());
    }

 /*   public void setBalanceOperationList(TreeMap<Integer, BalanceOperationClass> balanceOperationList) {
        this.balanceOperationList = balanceOperationList;
    }*/

    public void addBalanceOperation(Integer id, BalanceOperationClass bo){
        balanceOperationList.put(id, bo);
        if(bo.getMoney()<0)
            updateCurrentBalance(bo.getType(),-bo.getMoney());
        else
            updateCurrentBalance(bo.getType(),bo.getMoney());
    }

    public void updateCurrentBalance(String type, Double amount){
        if(type.equals("credit"))
            currentBalance += amount;
        else
            currentBalance -= amount;
    }

    public List<BalanceOperationClass> getListBalanceOperationsByDate(LocalDate d){
        List<BalanceOperationClass> boList = new ArrayList<>();
        ArrayList<BalanceOperationClass> balOpList = new ArrayList<>(balanceOperationList.values());

        for(BalanceOperationClass bo:balOpList){
            if(bo.getDate().equals(d))
                boList.add(bo);
        }
        if(boList.isEmpty()){
            return null;
        }
        else
            return boList;
    }

    public int getNumBalanceOperations(){
        return balanceOperationList.size();
    }

    public Integer getNewId(){
        return ++newId;
    }

    public void setNewId(){
        if(balanceOperationList.isEmpty()){
            newId= 0;
        }
        else {
            newId = balanceOperationList.lastKey();
        }
    }

    public Integer getID(){ return newId; }
}
