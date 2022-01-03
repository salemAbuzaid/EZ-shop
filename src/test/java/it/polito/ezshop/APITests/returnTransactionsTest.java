package it.polito.ezshop.APITests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class returnTransactionsTest {
    EZShopInterface ezshop = new EZShop();

    @Before
    public void reinitialize() throws InvalidQuantityException, InvalidLocationException, InvalidPricePerUnitException, InvalidProductIdException, InvalidTransactionIdException, UnauthorizedException, InvalidPasswordException, InvalidProductDescriptionException, InvalidRoleException, InvalidPaymentException, InvalidUsernameException, InvalidProductCodeException {
        resetTests reset = new resetTests();
        reset.resetTest();
    }

    @Test
    public void test_ReturnTransactions() throws InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidTransactionIdException, UnauthorizedException, InvalidProductCodeException, InvalidPaymentException, InvalidCreditCardException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user.getUsername());

        AtomicInteger tID = new AtomicInteger(-1);
        tID.set(ezshop.startSaleTransaction());

        assert(tID.get() >= 0);

        String validBarcode = "0000000000017";

        int finalTID = tID.get();

        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.addProductToSale(finalTID, "42", 1);
        });
        assertThrows(InvalidQuantityException.class, () -> {
            ezshop.addProductToSale(finalTID, validBarcode, -1);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ezshop.addProductToSale(-1, validBarcode, -1);
        });

        assertTrue(ezshop.addProductToSale(tID.get(), validBarcode, 4));

        assertTrue(ezshop.endSaleTransaction(tID.get()));
            // the ST needs to be paid in order to successfully ask for a return
        assertEquals(498.8, ezshop.receiveCashPayment(tID.get(), 500), 0.0);

        assertTrue(((SaleTransactionClass) ezshop.getSaleTransaction(tID.get())).isPaid());

        assertThrows(InvalidTransactionIdException.class, () -> {
            ezshop.startReturnTransaction(-1);
        });

        assertEquals(-1, (int) ezshop.startReturnTransaction(420));
        Integer rID;
        assertTrue((rID = ezshop.startReturnTransaction(tID.get())) != -1);

        int finalRID = rID;
        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.returnProduct(finalRID, "8", 2);
        });
        // too many potatoes
        assertFalse(ezshop.returnProduct(rID, validBarcode, 800));
        // return doesn't exist
        assertFalse(ezshop.returnProduct(42, validBarcode, 2));

        assertTrue(ezshop.returnProduct(rID, validBarcode, 2));

        assertTrue(ezshop.endReturnTransaction(rID, true));

        assertTrue(ezshop.deleteReturnTransaction(rID));

        rID = ezshop.startReturnTransaction(tID.get());

        assertTrue(ezshop.returnProduct(rID, validBarcode, 2));

    /*    double prevBal = ezshop.computeBalance();

        ezshop.recordBalanceUpdate(-prevBal); */

        assertTrue(ezshop.endReturnTransaction(rID, true));

     /*   ezshop.recordBalanceUpdate(prevBal);

        //assertTrue(ezshop.endReturnTransaction(rID, true));

        assertEquals(997, (int) ezshop.getProductTypeByBarCode(validBarcode).getQuantity()); */

        assert(ezshop.returnCreditCardPayment(rID, "4485370086510891") != -1);

        assertFalse(ezshop.deleteReturnTransaction(rID));

       Integer id = ezshop.startSaleTransaction();
       ezshop.addProductToSale(id, "0000000000024", 2);
       ezshop.endSaleTransaction(id);
       ezshop.receiveCreditCardPayment(id, "5100293991053009");

      Integer id_r = ezshop.startReturnTransaction(id);
       ezshop.returnProduct(id_r, "0000000000024", 2);
       ezshop.endReturnTransaction(id_r, true);
       assert(ezshop.returnCashPayment(id_r) == 0.8);

    }

    @Test
    public void test_ReturnTransactions_invalid_user() throws InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Cashier", "ciao");
        ezshop.logout();
        assertNotNull(user.getUsername());

        assertThrows(UnauthorizedException.class, () -> {
            ezshop.startReturnTransaction(1);
        });
    }
}
