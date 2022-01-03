package it.polito.ezshop.APITests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class saleTransactionsTest {
    EZShopInterface ezShop = new it.polito.ezshop.data.EZShop();

    @Before
    public void reset() throws InvalidQuantityException, InvalidLocationException, InvalidPricePerUnitException, InvalidProductIdException, InvalidTransactionIdException, UnauthorizedException, InvalidPasswordException, InvalidProductDescriptionException, InvalidRoleException, InvalidPaymentException, InvalidUsernameException, InvalidProductCodeException {
        resetTests test_reset = new resetTests();
        test_reset.resetTest();
    }

    @Test
    public void startSaleTransactionTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidTransactionIdException {
        Integer id = 0;

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.startSaleTransaction();});

        /* Logged != null */
        assertNotNull(ezShop.login("Cashier", "ciao"));
        assert((id = ezShop.startSaleTransaction()) > 0);
        ezShop.logout();
    }

    @Test
    public void addProductToSaleTest() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException, InvalidProductIdException {
        ezShop.login("Admin", "ciao");
        Integer id = ezShop.startSaleTransaction();
        ezShop.logout();

        /* Logged == null */
       assertThrows(UnauthorizedException.class, ()->{ezShop.addProductToSale(id, "0000000000017", 2);});

       /* Logged != null */
        ezShop.login("Admin", "ciao");
        /* Transaction id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.addProductToSale(null,"0000000000017",2);});

        /* Transaction id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.addProductToSale(-2,"0000000000017",2);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.addProductToSale(0,"0000000000017",2);});

        /* Product code = null */
        assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(id,null,2);});

        /* Product code = empty */
        assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(id,"",2);});

        /* Product code = invalid */
        assertThrows(InvalidProductCodeException.class, ()->{ezShop.addProductToSale(id,"123567",2);});

        /* Amount < 0 */
        assertThrows(InvalidQuantityException.class, ()->{ezShop.addProductToSale(id,"0000000000017",-5);});

        /* All valid */
        assertTrue(ezShop.addProductToSale(id, "0000000000017", 2));
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 6));

        ezShop.updateQuantity(1, 2);
        ezShop.updateQuantity(3,6);

        ezShop.logout();
    }

    @Test
    public void deleteProductFromSale() throws InvalidQuantityException, InvalidTransactionIdException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidProductCodeException, InvalidProductIdException {
        ezShop.login("Admin", "ciao");
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000017", 2));
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 6));
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.deleteProductFromSale(id,"0000000000031", 6 );});

        /* Logged != null */
        ezShop.login("Shop","ciao");

        /* Amount < 0 */
        assertThrows(InvalidQuantityException.class, ()->{ezShop.deleteProductFromSale(id,"0000000000031",-5);});

        /* Product code = null */
        assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(id,null,2);});

        /* Product code = empty */
        assertThrows(InvalidProductCodeException.class, ()->{ezShop.deleteProductFromSale(id,"",2);});

        /* Transaction id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteProductFromSale(null,"0000000000031",2);});

        /* Transaction id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteProductFromSale(-2,"0000000000031",2);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteProductFromSale(0,"0000000000031",2);});

        /* All valid */
        assertTrue(ezShop.deleteProductFromSale(id,"0000000000031",6));
        assertTrue(ezShop.deleteProductFromSale(id,"0000000000017",2));


    }

    @Test
    public void applyDiscountRateToProductTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductCodeException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductIdException, InvalidDiscountRateException {
        ezShop.login("Admin", "ciao");

        /* All valid but no open transaction */
        assertFalse(ezShop.applyDiscountRateToProduct(1, "0000000000031", 0.8));

        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000017", 2));
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.applyDiscountRateToProduct(id,"0000000000031", 0.8 );});

        /* Logged != null */
        ezShop.login("Cashier","ciao");

        /* Product code = null */
        assertThrows(InvalidProductCodeException.class, ()->{ezShop.applyDiscountRateToProduct(id,null,0.8);});

        /* Product code = empty */
        assertThrows(InvalidProductCodeException.class, ()->{ezShop.applyDiscountRateToProduct(id,"",0.8);});

        /* Transaction id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToProduct(null,"0000000000031",0.8);});

        /* Transaction id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToProduct(-2,"0000000000031",0.8);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToProduct(0,"0000000000031",0.8);});

        /* Discount Rate < 0 */
        assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToProduct(id,"0000000000031",-1);});

        /* Discount Rate > 1 */
        assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToProduct(id,"0000000000031",1.5);});

        /* All Valid */
        assertTrue(ezShop.applyDiscountRateToProduct(id, "0000000000017", 0.8));
        assertTrue(ezShop.applyDiscountRateToProduct(id, "0000000000017", 0));
        assertTrue(ezShop.deleteProductFromSale(id, "0000000000017", 2));
    }

    @Test
    public void applyDiscountRateToSale() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, InvalidPaymentException, InvalidProductIdException {
        ezShop.login("Admin", "ciao");
        ezShop.updateQuantity(3, 8);
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 6));
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.applyDiscountRateToSale(id,0.2);});

        /* Logged != null */
        ezShop.login("Shop", "ciao");

        /* Discount Rate < 0 */
        assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToSale(id,-1);});

        /* Discount Rate > 1 */
        assertThrows(InvalidDiscountRateException.class, ()->{ezShop.applyDiscountRateToSale(id,1.5);});

        /* Transaction id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToSale(null,0.8);});

        /* Transaction id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToSale(-2,0.8);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.applyDiscountRateToSale(0,0.8);});

        /* All valid -> transaction opened */
        assertTrue(ezShop.applyDiscountRateToSale(id, 0.2));

        ezShop.endSaleTransaction(id);
        assertTrue(ezShop.applyDiscountRateToSale(id, 0.1));

        ezShop.updateQuantity(3, 6);
        ezShop.deleteSaleTransaction(id);
    }

    @Test
    public void computePointsToSaleTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductIdException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException {
        ezShop.login("Admin", "ciao");
        ezShop.updateQuantity(3, 8);
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 2));
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.computePointsForSale(id);});

        /* Logged != null */
        ezShop.login("Shop", "ciao");

        /* Transaction id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.computePointsForSale(null);});

        /* Transaction id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.computePointsForSale(-2);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.computePointsForSale(0);});

        /* All valid */
        ezShop.endSaleTransaction(id);
        ezShop.applyDiscountRateToSale(id, 0.1);
        assert(ezShop.computePointsForSale(id) == 1);

        ezShop.updateQuantity(3, 2);
        ezShop.deleteSaleTransaction(id);

    }

    @Test
    public void endSaleTransactionTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductIdException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException {
        ezShop.login("Admin", "ciao");
        ezShop.updateQuantity(3, 8);
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 2));
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.endSaleTransaction(id);});

        /* Logged != null */
        ezShop.login("Shop", "ciao");

        /* Transaction id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.endSaleTransaction(null);});

        /* Transaction id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.endSaleTransaction(-2);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.endSaleTransaction(0);});

        assertTrue(ezShop.endSaleTransaction(id));
        ezShop.updateQuantity(3,2);
        ezShop.deleteSaleTransaction(id);

    }

    @Test
    public void deleteSaleTransaction() throws InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, UnauthorizedException {
        ezShop.login("Cashier", "ciao");
        Integer id = ezShop.startSaleTransaction();
        ezShop.endSaleTransaction(id);
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.deleteSaleTransaction(id);});

        /* Logged != null */
        ezShop.login("Cashier", "ciao");

        /* Sale id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteSaleTransaction(null);});

        /* Sale id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteSaleTransaction(-2);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.deleteSaleTransaction(0);});

        assertTrue(ezShop.deleteSaleTransaction(id));
        assertFalse(ezShop.deleteSaleTransaction(id));

    }

    @Test
    public void getSaleTransactionTest() throws InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, UnauthorizedException {
        ezShop.login("Cashier", "ciao");
        Integer id = ezShop.startSaleTransaction();
        ezShop.endSaleTransaction(id);
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.deleteSaleTransaction(id);});

        /* Logged != null */
        ezShop.login("Cashier", "ciao");

        /* Transaction id = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.getSaleTransaction(null);});

        /* Transaction id <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.getSaleTransaction(-2);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.getSaleTransaction(0);});

        assertNotNull(ezShop.getSaleTransaction(id));
        assertNull(ezShop.getSaleTransaction(200));

        ezShop.deleteSaleTransaction(id);
    }

    @Test
    public void receiveCashPaymentInvalidTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductIdException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException {
        ezShop.login("Admin", "ciao");
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 2));
        ezShop.endSaleTransaction(id);
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.receiveCashPayment(id, 5.0);});

        /* Logged != null */
        ezShop.login("Shop", "ciao");

        /* Cash <= 0 */
        assertThrows(InvalidPaymentException.class, ()->{ezShop.receiveCashPayment(id, 0.0);});
        assertThrows(InvalidPaymentException.class, ()->{ezShop.receiveCashPayment(id, -2);});

        /* Ticket number = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.receiveCashPayment(null, 5.0);});

        /* Ticket number <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.receiveCashPayment(-2, 5.0);});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.receiveCashPayment(0, 5.0);});

        /* Ticket number = invalid */
        assert(ezShop.receiveCashPayment(500, 5.0) == -1);

        /* Cash not enough */
        assert(ezShop.receiveCashPayment(id, 5.0) == -1);

        /* Sale already payed */
        assert(ezShop.receiveCashPayment(id, 20.00) == 0);
        ezShop.updateQuantity(3,2);
        assert(ezShop.receiveCashPayment(id, 20.00) == -1);
    }

    @Test
    public void receiveCashValidPaymentTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidCreditCardException, InvalidProductIdException {
        ezShop.login("Admin", "ciao");
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 2));
        ezShop.endSaleTransaction(id);
        assertTrue(ezShop.receiveCreditCardPayment(id, "4485370086510891"));

        /* To other tests */
        ezShop.updateQuantity(3,2);
    }

    @Test
    public void receiveCreditCardValidPaymentTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidCreditCardException, InvalidProductIdException {
        ezShop.login("Admin", "ciao");
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 2));
        ezShop.endSaleTransaction(id);
        assertTrue(ezShop.receiveCreditCardPayment(id, "4485370086510891"));
        ezShop.updateQuantity(3,2);
        ezShop.logout();
    }

    @Test
    public void receiveInvalidCreditCardPaymentTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidCreditCardException, InvalidProductIdException {
        ezShop.login("Admin", "ciao");
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 2));
        ezShop.endSaleTransaction(id);
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.receiveCreditCardPayment(id, "987654321015");});

        /* Logged != null */
        ezShop.login("Shop", "ciao");

        /* Credit card = null */
        assertThrows(InvalidCreditCardException.class, ()->{ezShop.receiveCreditCardPayment(id, null);});

        /* Credit card = empty */
        assertThrows(InvalidCreditCardException.class, ()->{ezShop.receiveCreditCardPayment(id, "null");});

        /* Ticket number = null */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.receiveCreditCardPayment(null, "4485370086510891");});

        /* Ticket number <= 0 */
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.receiveCreditCardPayment(-2, "4485370086510891");});
        assertThrows(InvalidTransactionIdException.class, ()->{ezShop.receiveCreditCardPayment(0, "4485370086510891");});

        /* Ticket number = invalid */
        assertFalse(ezShop.receiveCreditCardPayment(500, "4485370086510891"));

        /* Credit card = invalid */
        assertThrows(InvalidCreditCardException.class, ()->{ezShop.receiveCreditCardPayment(id, "122345");});

        /* Credit card valid but not registered */
        assertFalse(ezShop.receiveCreditCardPayment(id, "9876543210128"));

        /* To other tests */
        ezShop.updateQuantity(3,2);
    }

    @Test
    public void receiveCreditCardInvalidPaymentTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidCreditCardException, InvalidProductIdException {
        ezShop.login("Admin", "ciao");
        Integer id = ezShop.startSaleTransaction();
        assertTrue(ezShop.addProductToSale(id, "0000000000031", 2));
        ezShop.endSaleTransaction(id);
        ezShop.logout();

        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.receiveCreditCardPayment(id, "987654321015");});

        /* Logged != null */
        ezShop.login("Shop", "ciao");

        /* Credit card valid, registered, but not enough money */
        assertFalse(ezShop.receiveCreditCardPayment(id, "4716258050958645"));

        /* To other tests */
        ezShop.updateQuantity(3,2);
    }

    @Test
    public void recordBalanceUpdateTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        /* Logged == null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.recordBalanceUpdate(5.5);});

        /* Logged != null */
        ezShop.login("Admin", "ciao");

        Double balance = ezShop.computeBalance();

        /* Record credit operation */
        assertTrue(ezShop.recordBalanceUpdate(5.5));
        assert(ezShop.computeBalance() == balance+5.5);

        /* Record debit operation */
        assertTrue(ezShop.recordBalanceUpdate(-5.5));
        assert(ezShop.computeBalance() == balance);

    }

}
