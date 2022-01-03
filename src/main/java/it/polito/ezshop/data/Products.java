package it.polito.ezshop.data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Products {
    //map<ID, prodotto>
    private Integer newId = 0;
    private TreeMap<Integer, ProductTypeClass> products = new TreeMap<>();

    public ProductTypeClass getProductTypeById(Integer id){
        return products.get(id);
    }

    public ArrayList<ProductType> getProductTypeList(){
        return new ArrayList<ProductType>(products.values());
    }

    public Integer getNumProducts(){
        return products.values().size();
    }

    public Integer giveNewId(){
        if(products.isEmpty()){
            newId = 0;
        }
        else {
            newId = products.lastKey();
        }
        return newId;
    }

    public boolean addProduct(ProductTypeClass prod){

        products.put(prod.getId(), prod);
        return true;
    }

    public boolean removeProduct(ProductTypeClass prod){

        if( products.remove(prod.getId()) == null)
            return false;
        else {
            return true;
        }
    }

    public ProductTypeClass getProductTypeByBarCode(String barcode){
        for(ProductTypeClass p: products.values()){
            if(p.getBarCode().equals(barcode))
                return p;
        }
        return null;
    }

    public TreeMap<Integer, ProductTypeClass> getProducts(){
        return products;
    }


    public TreeMap<String, ProductTypeClass> getProductsByLocation(){
        return products.values().stream()
                .collect(
                        Collectors.toMap(
                                ProductTypeClass::getLocation,
                                Function.identity(),
                                (oldProd, newProd) -> newProd,
                                TreeMap::new
                        )
                );
    }


    public ArrayList<ProductType> getProductsByDescription(String description){
        ArrayList<ProductType> listProd = new ArrayList<>(products.values());
        ArrayList<ProductType> listbydescr = new ArrayList<>();
        for (ProductType p : listProd){
            if(p.getProductDescription().contains(description))
                listbydescr.add(p);
        }
            return listbydescr;
    }

    public Integer updateNewId(){
        return ++newId;
    }

}
