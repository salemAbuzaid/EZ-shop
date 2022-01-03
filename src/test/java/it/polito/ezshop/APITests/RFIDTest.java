package it.polito.ezshop.APITests;
import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.*;
import org.sqlite.core.DB;

import static org.junit.Assert.*;

public class RFIDTest {
    private static EZShopInterface ezshop;
    private int adminId;
    private String adminBaseUsername = "ADMIN";
    private String adminBasePwd = "ADMIN_PWD";


    private String username1 = "TestUsr1";
    private String username2 = "TestUsr2";
    private String username3 = "TestUsr3";
    private String userPwd = "TestUsrPwd";
    private String cashier = "Cashier";
    private String shopManager = "ShopManager";
    private String admin = "Administrator";

    private String productDescr1 = "testProduct1";
    private String productDescr2 = "testProduct2";
    private String barCode = "000012354788";
    private String barCode2 = "000055555555";
    private String invalidBarCode = "12354780";
    private double pricePerUnit = 0.50;
    private double pricePerUnit2 = 1.50;
    private double orderPricePerUnit = 0.25;
    private double orderPricePerUnit2 = 1.00;

    private String note1 = "test description";
    private String note2 = "description product";
    private String note3 = "type type";
    private String emptyNote = "";

    private int quantity = 10;
    private int quantity2 = 1;

    private String location1 = "10-A-1";
    private String location2 = "1-Z-10";
    private String invalidLocation1 = "A-A-0";
    private String invalidLocation2 = "0-A-A";
    private String invalidLocation3 = "A-A-A";

    private String customerName1 = "testCustomerName 1";
    private String customerName2 = "testCustomerName 2";
    private String customerCard1 = "1000011110";
    private String customerCard2 = "1000011111";
    private String invalidCustomerCard = "100001111a";
    private int point1 = 0;
    private int point2 = 100;

    private double discountRate = 0.5;
    private double invalidDiscountRate = 1.01;

    private double cash = 5.00;
    private String creditCard150 = "4485370086510891";
    private String creditCard10 = "5100293991053009";
    private String creditCard0 = "4716258050958645";
    private String notRegisteredCreditCard = "4485232344883462";
    private String invalidCreditCard = "4485370086510898";

    private final String BALANCE_ORDER = "ORDER";
    private final String BALANCE_SALE = "SALE";
    private final String BALANCE_RETURN = "RETURN";
    private final String BALANCE_CREDIT = "CREDIT";
    private final String BALANCE_DEBIT = "DEBIT";

    private final String ORDER_ISSUED = "ISSUED";
    private final String ORDER_PAYED = "PAYED";
    private final String ORDER_COMPLETED = "COMPLETED";

    private final String RFID_tooshort = "0000000003";
    private final String RFID_toolong = "0000000000002";
    private final String RFID_alphanumeric = "00f000000002";

    private String getErrorMsg(String testName, String msg) {
        return "Error in test " + testName + ": " + msg;
    }

    @BeforeClass
    public static void setUpEzShop() {
        ezshop = new EZShop();
    }

    @AfterClass
    public static void clearEzShop(){
        ezshop.reset();
    }

    @Before
    public void setup() {
        ezshop.reset();
        try {
            adminId = ezshop.createUser(adminBaseUsername,adminBasePwd,admin);
            ezshop.login(adminBaseUsername,adminBasePwd);
        } catch (InvalidUsernameException | InvalidPasswordException | InvalidRoleException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void recordOrderArrivalTest() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException, InvalidQuantityException, InvalidPasswordException, InvalidUsernameException, InvalidRFIDException, InvalidOrderIdException {
        DBClass fake_db = new DBClass();
        ProductsRFID fake_products = new ProductsRFID();
        int prodId = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
        ezshop.recordBalanceUpdate(1000);
        String RFID1 = "000000000001";


        int id = ezshop.payOrderFor(barCode, quantity, 0.1);

        ezshop.logout();
        assertThrows(UnauthorizedException.class, ()->{ezshop.recordOrderArrivalRFID(id, RFID1);});

        ezshop.login(adminBaseUsername,adminBasePwd);

        assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrivalRFID(null, RFID1);});
        assertThrows(InvalidOrderIdException.class, ()->{ezshop.recordOrderArrivalRFID(-1, RFID1);});
        assertThrows(InvalidLocationException.class, ()->{ezshop.recordOrderArrivalRFID(id, RFID1);});
        ezshop.updatePosition(prodId,location1);
        assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id, null);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id, "");});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id, RFID_tooshort);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id, RFID_toolong);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id, RFID_alphanumeric);});

        assertTrue(ezshop.recordOrderArrivalRFID(id, RFID1));
        fake_products = fake_db.importProductsRFID();

        int id2 = ezshop.payOrderFor(barCode, quantity, 0.1);
        assertThrows(InvalidRFIDException.class, ()->{ezshop.recordOrderArrivalRFID(id2, "000000000002");});

    }

    @Test
    public void saleTransactionsTest() throws UnauthorizedException, InvalidRFIDException, InvalidTransactionIdException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException, InvalidQuantityException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException {
        DBClass fake_db = new DBClass();
        ProductsRFID fake_products = new ProductsRFID();
        SaleTransactions fake_transactions = new SaleTransactions();
        ezshop.recordBalanceUpdate(1000);

        /* TEST ADDPRODUCT TO SALE */
        int prodId = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
        ezshop.updatePosition(prodId,location1);
        int orderId = ezshop.payOrderFor(barCode, quantity, 0.1);
        String RFID1 = "000000000001";
        ezshop.recordOrderArrivalRFID(orderId, RFID1);
        int close_transaction = ezshop.startSaleTransaction();
        ezshop.endSaleTransaction(close_transaction);
        int quantity = ezshop.getProductTypeByBarCode(barCode).getQuantity();

        int transactionId = ezshop.startSaleTransaction();

        // TEST ECCEZIONI
        ezshop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezshop.addProductToSaleRFID(100, barCode);
        });

        ezshop.login(adminBaseUsername,adminBasePwd);

        assertThrows(InvalidTransactionIdException.class, () -> {ezshop.addProductToSaleRFID(null, RFID1);});
        assertThrows(InvalidTransactionIdException.class, () -> {ezshop.addProductToSaleRFID(0, RFID1);});
        assertFalse(ezshop.addProductToSaleRFID(100, RFID1));
        assertFalse(ezshop.addProductToSaleRFID(close_transaction, RFID1));
        assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(transactionId, null);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(transactionId, "");});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(transactionId, RFID_tooshort);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(transactionId, RFID_toolong);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.addProductToSaleRFID(transactionId, RFID_alphanumeric);});

        /* TEST A BUON FINE */
        assertTrue(ezshop.addProductToSaleRFID(transactionId, RFID1));
        assert(ezshop.getProductTypeByBarCode(barCode).getQuantity().equals(quantity-1));
        fake_products = fake_db.importProductsRFID();
        assertNotNull(fake_products.getProductByRFID(RFID1));
        assertTrue(fake_products.getProductByRFID(RFID1).isSold());
  /*      fake_transactions = fake_db.importTransactions();
        assertTrue(fake_transactions.getSaleTransactionById(transactionId).getEntryByBarCode(barCode).getProductsRFID().contains(RFID1));
  */
        /* TEST DELETEPRODUCT */
        // TEST ECCEZIONI
        ezshop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezshop.deleteProductFromSaleRFID(transactionId, barCode);
        });

        ezshop.login(adminBaseUsername,adminBasePwd);
        assertThrows(InvalidTransactionIdException.class, () -> {ezshop.deleteProductFromSaleRFID(null, RFID1);});
        assertThrows(InvalidTransactionIdException.class, () -> {ezshop.deleteProductFromSaleRFID(0, RFID1);});
        assertFalse(ezshop.deleteProductFromSaleRFID(100, RFID1));
        assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(transactionId, null);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(transactionId, "");});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(transactionId, RFID_tooshort);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(transactionId, RFID_toolong);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.deleteProductFromSaleRFID(transactionId, RFID_alphanumeric);});
        assertFalse(ezshop.deleteProductFromSaleRFID(transactionId, "000000000200"));

        // TEST A BUON FINE
        assertTrue(ezshop.deleteProductFromSaleRFID(transactionId, RFID1));
        fake_products = fake_db.importProductsRFID();
        assertFalse(fake_products.getProductByRFID(RFID1).isSold());
    }

    @Test
    public void returnTest() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidProductIdException, InvalidQuantityException, InvalidRFIDException, InvalidOrderIdException, InvalidTransactionIdException, InvalidPaymentException, InvalidPasswordException, InvalidUsernameException {
        DBClass fake_db = new DBClass();
        ProductsRFID fake_products = new ProductsRFID();
        SaleTransactions fake_transactions = new SaleTransactions();
        ezshop.recordBalanceUpdate(1000);

        int prodId = ezshop.createProductType(productDescr1,barCode,pricePerUnit, note1);
        ezshop.updatePosition(prodId,location1);

        int orderId = ezshop.payOrderFor(barCode, quantity, 0.1);
        String RFID1 = "000000000001";
        ezshop.recordOrderArrivalRFID(orderId, RFID1);

        int transactionId = ezshop.startSaleTransaction();
        assertTrue(ezshop.addProductToSaleRFID(transactionId, RFID1));
        ezshop.endSaleTransaction(transactionId);
        ezshop.receiveCashPayment(transactionId, 5.0);

        int returnId = ezshop.startReturnTransaction(transactionId);

        /* TEST ECCEZIONI */
        ezshop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezshop.addProductToSaleRFID(100, barCode);
        });

        ezshop.login(adminBaseUsername,adminBasePwd);
        assertThrows(InvalidTransactionIdException.class, () -> {ezshop.returnProductRFID(null, RFID1);});
        assertThrows(InvalidTransactionIdException.class, () -> {ezshop.returnProductRFID(0, RFID1);});
        assertFalse(ezshop.returnProductRFID(100, RFID1));
        assertThrows(InvalidRFIDException.class, ()->{ezshop.returnProductRFID(returnId, null);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.returnProductRFID(returnId, "");});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.returnProductRFID(returnId, RFID_tooshort);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.returnProductRFID(returnId, RFID_toolong);});
        assertThrows(InvalidRFIDException.class, ()->{ezshop.returnProductRFID(returnId, RFID_alphanumeric);});

        fake_products = fake_db.importProductsRFID();
        assertTrue(fake_products.getProductByRFID(RFID1).isSold());

        assertTrue(ezshop.returnProductRFID(returnId, RFID1));
        fake_products = fake_db.importProductsRFID();
        assertFalse(fake_products.getProductByRFID(RFID1).isSold());
    }
}
