package it.polito.ezshop.data;

public class ProductTypeClass implements ProductType{

    private Integer id;
    private String barCode;
    private String productDescription;
    private Double pricePerUnit;
    private Integer quantity;
    private Double discountRate;
    private String note;
    private String location;

    public ProductTypeClass(Integer id, String barCode, String productDescription, Double pricePerUnit, String note){
        this.id = id;
        this.barCode = barCode;
        this.productDescription = productDescription;
        this.pricePerUnit = pricePerUnit;
        this.quantity = -1;
        this.discountRate = 0.0;
        this.note = note;
        this.location = "";
    }

    public ProductTypeClass(){
        this.id = -1;
        this.barCode = "";
        this.productDescription="";
        this.pricePerUnit = 0.0;
        this.discountRate = 0.0;
        this.quantity = -1;
        this.note = "";
        this.location = "";
    }

    @Override
    public Integer getQuantity(){
        return this.quantity;
    }

    @Override
    public  void setQuantity(Integer quantity){
        this.quantity = quantity;
    }

    @Override
    public  String getLocation(){
        return this.location;
    }

    @Override
    public void setLocation(String location){
        this.location = location;
    }

    @Override
    public String getNote(){
        return this.note;
    }

    @Override
    public void setNote(String note){
        this.note = note;
    }

    @Override
    public String getProductDescription(){
        return this.productDescription;
    }

    @Override
    public void setProductDescription(String productDescription){
        this.productDescription = productDescription;
    }

    @Override
    public String getBarCode(){
        return this.barCode;
    }

    @Override
    public void setBarCode(String barCode){
        this.barCode = barCode;
    }

    @Override
    public Double getPricePerUnit(){
        return this.pricePerUnit;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit){
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public Integer getId(){
        return this.id;
    }

    @Override
    public void setId(Integer id){
        this.id = id;
    }

    public Double getDiscountRate(){
        return this.discountRate;
    }

    public void setDiscountRate(Double rate){
        this.discountRate = rate;
    }

    public void increaseQuantity(int qty){
        this.quantity += qty;
    }

    public void decreaseQuantity(int qty){
        this.quantity -= qty;
    }
}
