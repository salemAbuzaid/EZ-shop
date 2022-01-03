package it.polito.ezshop.jUnitTests;
import it.polito.ezshop.data.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShop_ProductsRFID_WB {
    @Test
    public void test_productsRFID(){
        ProductRFID p1 = new ProductRFID();
        ProductsRFID products = new ProductsRFID();

        assertTrue(products.isRFIDfromValid("000000000003", 10));
        p1.setRFID("000000000003");
        p1.setBarCode("0000000000024");
        p1.setSold(false);

        assert(p1.getRFID().equals("000000000003"));
        assert(p1.getBarCode().equals("0000000000024"));
        assertFalse(p1.isSold());

        ProductRFID p2 = new ProductRFID("000000000004", "0000000000024");

        assert(p1.getRFID().equals("000000000003"));
        assert(p1.getBarCode().equals("0000000000024"));
        assertFalse(p1.isSold());

        assertTrue(products.addProductRFID(p1.getRFID(), p1));
        assertTrue(products.addProductRFID(p2.getRFID(), p2));
        assertFalse(products.isRFIDfromValid("000000000003", 10));
        assertTrue(products.removeProductRFID(p1.getRFID()));
        assert(products.getProductByRFID("000000000004").equals(p2));

        products.removeProductRFID(p2.getRFID());

    }
}
