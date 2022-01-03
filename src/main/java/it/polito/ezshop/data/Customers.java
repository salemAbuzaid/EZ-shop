package it.polito.ezshop.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class Customers {
    private Integer newId;
    private TreeMap<Integer, CustomerClass> customerList;

 /*   public Customers(TreeMap<Integer,CustomerClass> customerList) {
        this.customerList = customerList;
    }*/

    public Customers() {
        customerList = new TreeMap<>();
    }

    public List<Customer> getCustomerList() {
        return new ArrayList<>(customerList.values());
    }

    /*public void setCustomerList(TreeMap<Integer, CustomerClass> customerList) {
        this.customerList = customerList;
    }*/

    public boolean addCustomer(Integer id, CustomerClass c){
        if(!checkIfNameExists(c.getCustomerName()) && !checkIfCardExists(c.getCustomerCard()) && !customerList.containsKey(id)) {
            customerList.put(id, c);
            return true;
        }
        else return false;
    }

    public boolean removeCustomer(CustomerClass c){
        //manage exceptions
        if(customerList.remove(c.getId()) != null)
            return true;
        else
            return false;
    }

    public CustomerClass getCustomerById(Integer id){
        return customerList.get(id);
    }

    public CustomerClass getCustomerByCard(String card){
        for(CustomerClass c:customerList.values())
            if(c.getCustomerCard().equals(card))
                return c;
        return null; // MODIFICATO
    }

    public Integer getNewId(){
        return ++newId;
    }

    public Integer checkNewId(){
        return newId;
    }

    public void setNewId(){
        if(customerList.isEmpty()){
            newId= 0;
        }
        else {
            newId = customerList.lastKey();
        }
    }

    public boolean checkIfCardExists(String card_code){
        if(!card_code.isEmpty())
            for(CustomerClass c:customerList.values())
                if(c.getCustomerCard().equals(card_code))
                    return true;
        return false;
    }

    public boolean checkIfNameExists(String name){
        for(CustomerClass c:customerList.values())
            if(c.getCustomerName().equals(name))
                return true;
        return false;
    }

    private String generateRandomCardCode() {
        String chars = "0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public String getNewValidCardCode(){
        while (true) {
            String new_code = generateRandomCardCode();
            if (!checkIfCardExists(new_code) && new_code.matches("[0-9]*"))
                return new_code;
        }
    }
}
