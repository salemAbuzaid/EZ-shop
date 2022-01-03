package it.polito.ezshop.jUnitTests;
import it.polito.ezshop.data.*;

import static org.junit.Assert.*;

import org.junit.*;

import java.util.TreeMap;

public class TestEZShop_Products_WB {

    private DBClass db = new DBClass();;
    private Products products;

    @Test
    public void productsTestStatic(){

        products = new Products();
        assertNotNull(products);
        assert (products.giveNewId() == 0);

        assertNotNull(products.getProductTypeList());

        assert(products.getNumProducts() == 0);
        assertNull(products.getProductTypeById(0));
        assertNotNull(products.getProducts());


        ProductTypeClass p1 = new ProductTypeClass(products.updateNewId(), "barcode1",
                "descr1", 1.1, "prod1");
        assertNotNull(p1);

        assert(p1.getId() == 1);
        assert(p1.getBarCode().equals("barcode1"));
        assert(p1.getProductDescription().equals("descr1"));
        assert(p1.getPricePerUnit() == 1.1);
        assert(p1.getNote().equals("prod1"));

        p1.setLocation("location1");
        p1.setDiscountRate(1.1);

        ProductTypeClass p2 = new ProductTypeClass(products.updateNewId(), "barcode2",
                "descr2", 2.2, "prod2");
        assertNotNull(p2);

        assert(p2.getId() == 2);
        assert(p2.getBarCode().equals("barcode2"));
        assert(p2.getProductDescription().equals("descr2"));
        assert(p2.getPricePerUnit() == 2.2);
        assert(p2.getNote().equals("prod2"));

        p2.setLocation("location2");
        p2.setDiscountRate(2.2);

        products.addProduct(p1);
        products.addProduct(p2);

        p1=products.getProductTypeById(1);

        p1.setId(products.giveNewId());
        p1.setBarCode("barcode11");
        p1.setProductDescription("descr11");
        p1.setPricePerUnit(11.11);
        p1.setNote("prod11");
        p1.setLocation("location11");
        p1.setDiscountRate(11.11);

        assert(p1.getBarCode().equals("barcode11"));
        assert(p1.getProductDescription().equals("descr11"));
        assert(p1.getPricePerUnit() == 11.11);
        assert(p1.getNote().equals("prod11"));
        assert(p1.getLocation().equals("location11"));
        assert(p1.getDiscountRate() == 11.11);

        assertEquals(p1, products.getProductsByLocation().get(p1.getLocation()));
        assertEquals(p2, products.getProductsByLocation().get(p2.getLocation()));

        //due elementi con la stessa location ---> prendo il secondo
        p1.setLocation("location2");
        assertEquals(p2, products.getProductsByLocation().get(p1.getLocation()));

        assertEquals(p1, products.getProductTypeByBarCode(p1.getBarCode()));
        assertTrue(products.removeProduct(p1));
        assert(products.giveNewId() == 1);

        assertFalse(products.removeProduct(p1));
        assertNull(products.getProductTypeById(p1.getId()));

    }

    @Before
    public void productsReinitializeDB(){
        db.deleteProducts();
    }

    @Test
    public void productsTestDB(){
        db.deleteProducts();

        Products prodotti = db.importProducts();
        products = new Products();

        ProductTypeClass p1 = new ProductTypeClass(products.updateNewId(), "barcode1",
                "descr1", 1.1, "prod1");

        assertNotNull(p1);

        prodotti.addProduct(p1);

        assert (p1 == prodotti.getProductTypeById(p1.getId()));
        db.addProductType(p1);

        ProductTypeClass p2 = new ProductTypeClass(products.updateNewId(), "barcode2",
                "descr2", 2.2, "prod2");
        assertNotNull(p2);

        prodotti.addProduct(p2);

        assert (p2 == prodotti.getProductTypeById(p2.getId()));
        db.addProductType(p2);


        ProductTypeClass p3 = new ProductTypeClass(products.updateNewId(), "barcode3",
                "descr3", 3.3, "prod3");
        assertNotNull(p3);

        prodotti.addProduct(p3);

        assert(prodotti.getProductsByDescription("descr").size() == 3);

        assert (p3 == prodotti.getProductTypeById(p3.getId()));
        db.addProductType(p3);


        Products testProdotti = new Products();
        assertNotNull( testProdotti);
        testProdotti = db.importProducts();
        assertNotNull( testProdotti);

        ProductTypeClass productNULL = prodotti.getProductTypeById(5);
        assertNull(productNULL);

        ProductTypeClass prodotto2 = prodotti.getProductTypeById(2);
        assertNotNull (prodotto2);

        assertTrue(db.deleteProductType(2));
        assertNull (db.getProductType(2));

        ProductTypeClass prodotto3Test = db.getProductType(3);

        assertNotNull (prodotto3Test );

        db.updateProductType(3, "newDescription", "newCode", 100.0, "NEW");

        prodotto3Test = db.getProductType(3);
        assertEquals(prodotto3Test.getNote(), "NEW");
        assert (prodotto3Test.getId() == 3);

        prodotto3Test.setQuantity(10);
        assert (prodotto3Test.getQuantity() == 10);
        prodotto3Test.increaseQuantity(5);
        assert (prodotto3Test.getQuantity() == 15);
        prodotto3Test.decreaseQuantity(14);
        assert (prodotto3Test.getQuantity() == 1);

        db.deleteProductType(1);
        db.deleteProductType(3);
    }
}
