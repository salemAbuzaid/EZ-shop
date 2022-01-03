package it.polito.ezshop.APITests;
import static org.junit.Assert.*;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class productsTests {
    EZShopInterface ez = new it.polito.ezshop.data.EZShop();

    @Before
    public void reset() throws InvalidQuantityException, InvalidLocationException, InvalidPricePerUnitException, InvalidProductIdException, InvalidTransactionIdException, UnauthorizedException, InvalidPasswordException, InvalidProductDescriptionException, InvalidRoleException, InvalidPaymentException, InvalidUsernameException, InvalidProductCodeException {
        resetTests test_reset = new resetTests();
        test_reset.resetTest();
    }

    @Test
    public void testLoggedUserInCreateProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        //Test whether the user is logged in
        assertThrows(UnauthorizedException.class,()-> ez.createProductType("rice", "123456789", 5, "will be in sale next week"));

    }

    @Test
    public void testDescriptionCreateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        //Test user Description

        ez.login("Admin","ciao");
        assertThrows(InvalidProductDescriptionException.class,()-> ez.createProductType("", "67891011234522", 5, "will be in sale next week"));

    }

    @Test
    public void testBarCodeCreateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        //Test user BarCode
        ez.login("Admin","ciao");
        assertThrows (InvalidProductCodeException.class ,()-> ez.createProductType("rice", "", 5, "will be in sale next week"));
    }


    @Test
    public void testPpuCreateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        //Test Price per unit
        ez.login("Admin","ciao");
        assertThrows(InvalidPricePerUnitException.class ,()-> ez.createProductType("rice", "12345678901231", -5, "will be in sale next week"));
    }

    @Test
    public void testCreateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        //Test creation of new product

        ez.login("Admin","ciao");
        Integer pID = ez.createProductType("rice", "12345678901231", 5, "will be in sale next week");
        assert(pID != -1);
        ProductType p = ez.getProductTypeByBarCode("12345678901231");
        assertEquals(p.getId(),pID);
        ez.deleteProductType(pID);
    }

    // here starts the test for update product
    @Test
    public void testLoggedUserInUpdateProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductIdException, InvalidProductCodeException {
        assertThrows (UnauthorizedException.class ,()->ez.updateProduct(4,"meat","12345348901233",10,"expires next week"));
    }

    @Test
    public void testRoleInUpdateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductIdException, InvalidProductCodeException {
        ez.login("Cashier","ciao");
        assertThrows(UnauthorizedException.class,()-> ez.updateProduct(4,"meat","12345348901233",10,"expires next week"));
    }

    @Test
    public void testProductIdInUpdateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductIdException, InvalidProductCodeException {
        ez.login("Admin","ciao");
        assertThrows(InvalidProductIdException.class, ()-> ez.updateProduct(0,"meat","12345348901233",10,"expires next week"));
    }

    @Test
    public void testNewDescriptionInUpdateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductIdException, InvalidProductCodeException {
        ez.login("Admin","ciao");
        assertThrows(InvalidProductDescriptionException.class,()-> ez.updateProduct(1,"","12345348901233",10,"expires next week"));
    }

    @Test
    public void testNewCodeInUpdateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductIdException, InvalidProductCodeException {
        ez.login("Admin","ciao");
        assertThrows(InvalidProductCodeException.class,()-> ez.updateProduct(1,"meat",null,10,"expires next week"));
    }

    @Test
    public void testNewPricePerUnitInUpdateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductIdException, InvalidProductCodeException {
        ez.login("Admin","ciao");
        assertThrows(InvalidPricePerUnitException.class,()-> ez.updateProduct(1,"meat","12345348901233",0,"expires next week"));
    }

    @Test
    public void testUpdateProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        ez.login("Admin","ciao");
        int pID = ez.createProductType("rice", "723951367038", 5, "will be in sale next week");
        assertTrue(ez.updateProduct(pID,"meat","94201294365724",10,"expires next week"));
        assertFalse(ez.updateProduct(1000,"meat","6291041500213",10,"expires next week"));
        ez.deleteProductType(pID);
    }

    //here starts the test for delete product type
    @Test
    public void testLoggedUserInDeleteProduct(){
        assertThrows(UnauthorizedException.class,()-> ez.deleteProductType(1));
    }

    @Test
    public void testAuthorizationDeleteProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductIdException {
        ez.login("Cashier","ciao");
        assertThrows (UnauthorizedException.class,()-> ez.deleteProductType(1));
    }

    @Test
    public void testValidIdDeleteProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductIdException {
        ez.login("Admin","ciao");
        assertThrows(InvalidProductIdException.class,()-> ez.deleteProductType(0));
    }

    @Test
    public void testExistenceOfProductDeleteProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductIdException {
        ez.login("Admin","ciao");
        assertFalse(ez.deleteProductType(110));
    }

    @Test
    public void testDeleteProduct() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        ez.login("Admin","ciao");
        int id =  ez.createProductType("banana","723951367038",19.3,"");
        assertTrue(ez.deleteProductType(id));
    }

    //here starts the test for get all product type
    @Test
    public void testLoggedUserInGetAllProductType(){
        assertThrows(UnauthorizedException.class ,()-> ez.getAllProductTypes());
    }

    @Test
    public void testGetAllProductType() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        ez.login("Admin","ciao");
        int id1 = ez.createProductType("Avocado", "12345678901231",3.5,"");
        int id2 =  ez.createProductType("banana","14545348901235",19.3,"");
        List<ProductType> listPr = ez.getAllProductTypes();
        assertNotNull(listPr);
        assertTrue( listPr.contains(ez.getProductTypeByBarCode("12345678901231")));
        assertNotNull(listPr.get(2));
        assertTrue(ez.deleteProductType(id1));
        assertTrue(ez.deleteProductType(id2));
    }

    //here starts the test to get product by description
    @Test
    public void testLoggedUserInGetProductTypesByDescription(){
        assertThrows(UnauthorizedException.class , ()-> ez.getProductTypesByDescription("234443"));
    }

    @Test
    public void testRoleInGetProductTypesByDescription() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        ez.login("Cashier","ciao");
        assertThrows(UnauthorizedException.class,() -> ez.getProductTypesByDescription("Avocado"));
    }

    @Test
    public void testGetProductTypesByDescription() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException, InvalidProductIdException {
        ez.login("Admin","ciao");
        ez.createProductType("Avocado", "1234567890128",3.5,"");
        ez.createProductType("Avocado", "6291041500213",3.5,"");
        List<ProductType> listPr =ez.getProductTypesByDescription("Avocado");
        assertNotNull(listPr);
        ez.deleteProductType(listPr.get(0).getId());
        ez.deleteProductType(listPr.get(1).getId());
    }

    //in the following is the test to get product by barcode
    @Test
    public void testGetProductByBarcode() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        assertThrows(UnauthorizedException.class, ()-> ez.getProductTypeByBarCode("eaaaa"));
        ez.login("Cashier","ciao");
        assertThrows(UnauthorizedException.class, ()-> ez.getProductTypeByBarCode("eaaaa"));
        assertTrue(ez.logout());
        ez.login("Admin","ciao");
        assertThrows(InvalidProductCodeException.class ,()-> ez.getProductTypeByBarCode("eaaaa"));
        ez.createProductType("Avocado", "12345678901231",3.5,"");
        assertNotNull(ez.getProductTypeByBarCode("12345678901231"));
        ez.deleteProductType(ez.getProductTypeByBarCode("12345678901231").getId());
    }


    //in the following there is the test for updating product quantity
    @Test
    public void testUpdateProductQuantity()throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        //test whether loggedUser == null
        assertThrows(UnauthorizedException.class, ()-> ez.updateQuantity(1,3));
        //Test whether Role is valid
        ez.login("Cashier","ciao");
        assertThrows(UnauthorizedException.class, ()-> ez.updateQuantity(1,3));
        assertTrue(ez.logout());
        assertNotNull(ez.login("Admin","ciao"));
        ez.createProductType("Avocado", "6291041500213",3.5,"");
        ProductType p = ez.getProductTypeByBarCode("6291041500213");
        p.setLocation("2-c-3");       //the location should not be null during this test, we need to set it with a temporary value to commit the test
        int id = p.getId();
        assertTrue(ez.updateQuantity(id,2));
        ez.deleteProductType(id);
    }

    //In the following we test update position methode
    @Test
    public void testUpdatePosition()throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException,InvalidLocationException{
        //test whether loggedUser == null
        assertThrows(UnauthorizedException.class, ()-> ez.updatePosition(1,"xyz"));
        //Test whether Role is valid
        ez.login("Cashier","ciao");
        assertThrows(UnauthorizedException.class, ()-> ez.updatePosition(1,"xyz"));
        assertTrue(ez.logout());
        assertNotNull(ez.login("Admin","ciao"));
        ez.createProductType("Avocado", "6291041500213",3.5,"");
        ez.createProductType("juice", "723951367038",5.5,"");
        assertThrows(InvalidProductIdException.class , ()-> ez.updatePosition(0 , "123"));
        int id1 = ez.getProductTypeByBarCode("6291041500213").getId();
        int id2 = ez.getProductTypeByBarCode("723951367038").getId();
        assertThrows(InvalidLocationException.class , ()-> ez.updatePosition(id1 , "123"));
        assertTrue(ez.updatePosition(id1, "9-B-3"));
        assertFalse(ez.updatePosition(id2, "9-B-3"));
        ez.deleteProductType(id1);
        ez.deleteProductType(id2);
    }
}



