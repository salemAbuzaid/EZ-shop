package it.polito.ezshop.data;

import java.util.*;

public class OrderClass implements Order{
    private Integer id;
    private Double pricePerUnit;
    private Integer quantity;
    private String status;
    private Integer balanceId;
    private String productCode;

    public OrderClass() {
        this.id = 0;
        this.pricePerUnit = 0.0;
        this.quantity = 0;
        this.status = null;
        this.balanceId = 0;
        this.productCode = null;
    }

    public OrderClass(Integer id, Double pricePerUnit, Integer quantity, String status, Integer balanceId, String productCode) {
        this.id = id;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.status = status;
        this.balanceId = balanceId;
        this.productCode = productCode;
    }


    @Override
    public Integer getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public String getProductCode() {
        return this.productCode;
    }

    @Override
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Integer getOrderId() {
        return this.id;
    }

    @Override
    public void setOrderId(Integer orderId) {
        this.id=orderId;
    }
}
