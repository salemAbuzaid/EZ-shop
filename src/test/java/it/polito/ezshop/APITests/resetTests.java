package it.polito.ezshop.APITests;

import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.*;
import org.junit.Test;

public class resetTests {
    EZShopInterface ezShop = new it.polito.ezshop.data.EZShop();

    @Test
    public void resetTest() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException, InvalidQuantityException, InvalidTransactionIdException, InvalidPaymentException {
        ezShop.reset();

        ezShop.createUser("Admin", "ciao","Administrator");
        ezShop.login("Admin", "ciao");

        assert(ezShop.getAllCustomers().isEmpty());
        assert(ezShop.getAllProductTypes().isEmpty());
        assert(ezShop.getAllOrders().isEmpty());

        /* Ripopolo db */
        /* Products */
        ezShop.createProductType("Apple","0000000000017", 0.3,"");
        ezShop.createProductType("Banana","0000000000024", 0.4,"");
        ezShop.createProductType("Cherry","0000000000031", 10,"");
        ezShop.createProductType("Mango","0000000000048", 27.1,"");

        ezShop.updatePosition(1, "1-A-1");
        ezShop.updatePosition(2, "2-B-2");
        ezShop.updatePosition(3, "3-C-3");
        ezShop.updatePosition(4, "4-D-4");

        ezShop.updateQuantity(1, 7);
        ezShop.updateQuantity(2, 16);
        ezShop.updateQuantity(3, 10);
        ezShop.updateQuantity(4, 100);

        /* Sale Transactions */
        Integer id = ezShop.startSaleTransaction();
        ezShop.addProductToSale(id, "0000000000031", 2);
        ezShop.endSaleTransaction(id);
        ezShop.receiveCashPayment(id, 20.00);

        Integer id2 = ezShop.startSaleTransaction();
        ezShop.addProductToSale(id2, "0000000000031", 1);
        ezShop.endSaleTransaction(id2);

        Integer id3 = ezShop.startSaleTransaction();
        ezShop.addProductToSale(id3, "0000000000031", 1);
        ezShop.endSaleTransaction(id3);
        ezShop.receiveCashPayment(id3, 10.00);

        ezShop.updateQuantity(3, 4);

        /* Users */
        ezShop.createUser("Admin", "ciao", "Administrator");
        ezShop.createUser("Cashier", "ciao", "Cashier");
        ezShop.createUser("Shop", "ciao", "ShopManager");
    }
}