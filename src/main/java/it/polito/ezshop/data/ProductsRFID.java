package it.polito.ezshop.data;

import java.util.HashMap;
import java.util.TreeMap;

public class ProductsRFID {
    private HashMap<String, ProductRFID> productsRFID = new HashMap<>();

    public boolean addProductRFID(String rfid, ProductRFID p){
        if(productsRFID.put(rfid, p) == null)
            return true;
        else return false;
    }


    public boolean removeProductRFID(String rfid){
        return productsRFID.remove(rfid) != null;
    }

    public HashMap<String, ProductRFID> getProductsRFID() {
        return productsRFID;
    }

    public ProductRFID getProductByRFID(String rfid){
        return productsRFID.get(rfid);
    }

    public boolean isRFIDfromValid(String rfid, int offset) {
        for (int i = 0; i < offset; i++) {
            if (getProductByRFID(rfid) != null) return false;
            int intRFID = Integer.parseInt(rfid);
            rfid = String.format("%012d", ++intRFID);
        }
        return true;
    }
}
