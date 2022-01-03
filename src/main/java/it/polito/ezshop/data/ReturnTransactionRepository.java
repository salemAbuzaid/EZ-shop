package it.polito.ezshop.data;

import java.util.TreeMap;

public class ReturnTransactionRepository {
    private Integer newId;
    private TreeMap<Integer , ReturnTransactionClass> returnTransactions;

    public ReturnTransactionRepository() {
        this.returnTransactions = new TreeMap<>();
    }

    public void addReturn(ReturnTransactionClass rt, int id){
        returnTransactions.put(id , rt);
    }

    public void removeReturnTransaction(Integer id){
        returnTransactions.remove(id);
    }

    public ReturnTransactionClass getReturnTransactionById(Integer id){
        return returnTransactions.get(id);
    }

    public void setNewId(){
     if(returnTransactions.isEmpty()){
                newId = 0;
            }
            else {
                newId = returnTransactions.lastKey();
            }
    }

    public Integer getNewId(){
        newId++;
        return newId;
    }


}
