package it.polito.ezshop.data;

import java.util.*;

public class Orders {
    private Integer newId;
    private int numOrders;
    private TreeMap<Integer, OrderClass> orders = new TreeMap<>();

    public Orders() {
        this.numOrders = 0;
        this.newId = 0;
    }

    public ArrayList<Order> getOrdersList(){
        return new ArrayList<Order>(orders.values());
    }


    public void addOrder(OrderClass order){
        orders.put(order.getOrderId(), order);
        numOrders = numOrders+1;
    }

    public int getNumOrders(){
        return numOrders;
    }

    public TreeMap<Integer, OrderClass> getOrders(){
        return orders;
    }

    public OrderClass getOrderById(Integer id){
        return orders.get(id);
    }

    public Integer getNewId(){
        return ++newId;
    }

    public void setNewId(){
        if(orders.isEmpty()){
            newId= 0;
        }
        else {
            newId = orders.lastKey();
        }
    }

    public Integer giveId(){
        return newId;
    }
}
