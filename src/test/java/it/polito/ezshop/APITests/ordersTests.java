package it.polito.ezshop.APITests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ordersTests {
    EZShop ezshop = new EZShop();
    DBClass db = new DBClass();

    @Before
    public void initialize() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidRoleException, InvalidQuantityException, InvalidLocationException, InvalidTransactionIdException, InvalidPaymentException, InvalidProductIdException {
        resetTests test_reset = new resetTests();
        test_reset.resetTest();
        ezshop.login("Admin", "ciao");
        ezshop.recordBalanceUpdate(1000);
        ezshop.logout();
    }

    @Test
    public void TestScenario3_1_Admin_Valid() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user.getUsername());

        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);
        assertNotNull(p);

        int ordered_quantity = 5;
        double price = 0.5;

        double oldbalance = ezshop.computeBalance();
        int oldqty = p.getQuantity();
        List<Order> oldOrders = ezshop.getAllOrders();

        int order_id = ezshop.issueOrder(validBarcode, ordered_quantity, price);
        assert (order_id > 0);

        assert (oldbalance == ezshop.computeBalance());
        assert (ezshop.getProductTypeByBarCode(validBarcode).getQuantity() == oldqty);

        assertNotNull(ezshop.getAllOrders());

        assertNotEquals(oldOrders, ezshop.getAllOrders());

        for (Order o : ezshop.getAllOrders()) {
            if (o.getOrderId().equals(order_id)) {
                assert (o.getQuantity() == ordered_quantity);
                assert (o.getPricePerUnit() == price);
                assert (o.getStatus().equals("ISSUED"));
            }
        }


    }

    @Test
    public void TestScenario3_1_Shop_Valid() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user.getUsername());

        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);
        assertNotNull(p);

        int ordered_quantity = 5;
        double price = 0.5;

        double oldbalance = ezshop.computeBalance();
        int oldqty = p.getQuantity();
        List<Order> oldOrders = ezshop.getAllOrders();

        int order_id = ezshop.issueOrder(validBarcode, ordered_quantity, price);
        assert (order_id > 0);

        assert (oldbalance == ezshop.computeBalance());
        assert (ezshop.getProductTypeByBarCode(validBarcode).getQuantity() == oldqty);

        assertNotNull(ezshop.getAllOrders());

        assertNotEquals(oldOrders, ezshop.getAllOrders());

        for (Order o : ezshop.getAllOrders()) {
            if (o.getOrderId().equals(order_id)) {
                assert (o.getQuantity() == ordered_quantity);
                assert (o.getPricePerUnit() == price);
                assert (o.getStatus().equals("ISSUED"));
            }
        }
    }

    @Test
    public void TestScenario3_1_Casher_Invalid() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Cashier", "ciao");

        assertNotNull(user.getUsername());

        String validBarcode = "0000000000017";

        int ordered_quantity = 5;
        double price = 0.5;
        assertThrows(UnauthorizedException.class, () -> {
            ezshop.issueOrder(validBarcode, ordered_quantity, price);
        });
    }

    @Test
    public void TestScenario3_1_ValidBarcode_NotFound() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user.getUsername());


        String invalidBarcode = "00000000009997";
        ProductType p = ezshop.getProductTypeByBarCode(invalidBarcode);

        int ordered_quantity = 5;
        double price = 0.5;
        int order_id = ezshop.issueOrder(invalidBarcode, ordered_quantity, price);
        assert (order_id == -1);
    }

    @Test
    public void TestScenario3_1_InvalidBarcode() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String invalidBarcode = "0000000000018";

        int ordered_quantity = 5;
        double price = 0.5;

        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.issueOrder(invalidBarcode, ordered_quantity, price);
        });

    }

    @Test
    public void T_issueOrder_Invalid_user_Null() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException {
        ezshop.logout();

        String validBarcode = "0000000000017";

        int ordered_quantity = 5;
        double price = 0.5;

        assertThrows(UnauthorizedException.class, () -> {
            ezshop.issueOrder(validBarcode, ordered_quantity, price);
        });

    }

    @Test
    public void TestScenario3_1_InvalidBarcode_Empty() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String invalidBarcode = "";

        int ordered_quantity = 5;
        double price = 0.5;

        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.issueOrder(invalidBarcode, ordered_quantity, price);
        });

    }

    @Test
    public void TestScenario3_1_InvalidBarcode_Null() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");


        String validBarcode = null;

        int ordered_quantity = 5;
        double price = 0.5;

        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.issueOrder(validBarcode, ordered_quantity, price);
        });

    }

    @Test
    public void TestScenario3_1_Invalid_OrderedQuantity() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");


        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);


        int ordered_quantity = 0;
        double price = 0.5;
        assertThrows(InvalidQuantityException.class, () -> {
            ezshop.issueOrder(validBarcode, ordered_quantity, price);
        });
    }

    @Test
    public void TestScenario3_1_Invalid_ppu() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);

        int ordered_quantity = 5;
        double price = 0.0;
        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezshop.issueOrder(validBarcode, ordered_quantity, price);
        });
    }


    @Test
    public void T_payOrder_Valid1_1() throws UnauthorizedException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidProductDescriptionException, InvalidRoleException, InvalidPricePerUnitException, InvalidProductCodeException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user.getUsername());

        String barcode = "0000000000024";
        int ordered_quantity=4;
        double price = 0.9;
        int orderId = ezshop.issueOrder(barcode, ordered_quantity, price);
        assert (orderId > 0);

        double oldBalance = ezshop.computeBalance();

        ProductType oldP = null;
        int oldQty = 0;
        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                oldP = ezshop.getProductTypeByBarCode(o.getProductCode());
                oldQty = oldP.getQuantity();
                break;
            }
        }


        assertTrue (ezshop.payOrder(orderId));

        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                ProductType p = ezshop.getProductTypeByBarCode(o.getProductCode());
                int qty = p.getQuantity();
                assert (oldQty == qty);
                assert (oldBalance >= ezshop.computeBalance());
                assert (o.getStatus().equals("PAYED"));
                break;
            }
        }


    }

    @Test
    public void T_payOrder_Valid1_2() throws UnauthorizedException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException, InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user.getUsername());

        String barcode = "0000000000024";
        int ordered_quantity=4;
        double price = 0.9;
        int orderId = ezshop.issueOrder(barcode, ordered_quantity, price);
        assert (orderId > 0);

        double oldBalance = ezshop.computeBalance();

        ProductType oldP = null;
        int oldQty = 0;
        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                oldP = ezshop.getProductTypeByBarCode(o.getProductCode());
                oldQty = oldP.getQuantity();
                break;
            }
        }


        assertTrue (ezshop.payOrder(orderId));

        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                ProductType p = ezshop.getProductTypeByBarCode(o.getProductCode());
                int qty = p.getQuantity();
                assert (oldQty == qty);
                assert (oldBalance >= ezshop.computeBalance());
                assert (o.getStatus().equals("PAYED"));
                break;
            }
        }


    }

    @Test
    public void T_payOrder_Valid1_3() throws UnauthorizedException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException, InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user.getUsername());

        String barcode = "0000000000024";
        int ordered_quantity=4;
        double price = 0.9;
        int orderId = ezshop.issueOrder(barcode, ordered_quantity, price);
        assert (orderId > 0);

        double oldBalance = ezshop.computeBalance();

        ProductType oldP = null;
        int oldQty = 0;
        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                oldP = ezshop.getProductTypeByBarCode(o.getProductCode());
                oldQty = oldP.getQuantity();
                break;
            }
        }


        assertTrue (ezshop.payOrder(orderId));

        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                ProductType p = ezshop.getProductTypeByBarCode(o.getProductCode());
                int qty = p.getQuantity();
                assert (oldQty == qty);
                assert (oldBalance >= ezshop.computeBalance());
                assert (o.getStatus().equals("PAYED"));
                break;
            }
        }

    }

    @Test
    public void T_payOrder_Invalid1_1() throws UnauthorizedException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user.getUsername());

        assertThrows (InvalidOrderIdException.class, () -> {
            ezshop.payOrder(0);
        });
    }

    @Test
    public void T_payOrder_Invalid1_2() throws UnauthorizedException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException {

        ezshop.login("Admin", "ciao");

        assertThrows (InvalidOrderIdException.class, () -> {
            ezshop.payOrder(null);
        });
    }

    @Test
    public void T_payOrder_Invalid1_3() throws UnauthorizedException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException {

        ezshop.login("Cashier", "ciao");

        assertThrows (UnauthorizedException.class, () -> {
            ezshop.payOrder(2);
        });
    }


    @Test
    public void T_payOrderFor_Valid1_1() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user);

        String validBarcode = "0000000000031";

        int ordered_quantity=8;
        double price = 15;


        ProductType oldP = ezshop.getProductTypeByBarCode(validBarcode);
        int oldQty = oldP.getQuantity();
        double oldBalance = ezshop.computeBalance();


        int order_id = ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        assert (order_id > 0);

        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == order_id){
                ProductType prod = ezshop.getProductTypeByBarCode(o.getProductCode());
                int qty = prod.getQuantity();
                assert (oldQty == qty);
                double newbalance = ezshop.computeBalance();
                assert (oldBalance >= newbalance);
                assert (o.getStatus().equals("PAYED"));
                break;
            }
        }
    }

    @Test
    public void T_payOrderFor_Valid1_2() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user);

        String validBarcode = "0000000000048";


        int ordered_quantity=3;
        double price = 30;


        ProductType oldP = ezshop.getProductTypeByBarCode(validBarcode);
        int oldQty = oldP.getQuantity();
        double oldBalance = ezshop.computeBalance();


        int order_id = ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        assert (order_id > 0);

        for( Order o : ezshop.getAllOrders()){
            if(o.getOrderId() == order_id){
                ProductType prod = ezshop.getProductTypeByBarCode(o.getProductCode());
                int qty = prod.getQuantity();
                assert (oldQty == qty);
                double newbalance = ezshop.computeBalance();
                assert (oldBalance >= newbalance);
                assert (o.getStatus().equals("PAYED"));
                break;
            }
        }
    }




    @Test
    public void T_payOrderFor_Valid3() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user);

        String validBarcode = "00000000009997";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);

        int ordered_quantity=5;
        double price = 0.5;
        double oldBalance = ezshop.computeBalance();
        int order_id = ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        double newBalance = ezshop.computeBalance();
        assert (order_id == -1);
        assert (oldBalance == newBalance);

        for(Order o: ezshop.getAllOrders()){
            if(o.getOrderId() == order_id){
                assert(o.getStatus().equals("ISSUED"));
            }
        }
    }

    @Test
    public void T_payOrderFor_Valid_NoBalance() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user);

        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);

        int ordered_quantity=5;
        double price = 10000;
        double oldBalance = ezshop.computeBalance();
        int order_id = ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        double newBalance = ezshop.computeBalance();
        assert (order_id == -1);
        assert (oldBalance == newBalance);

        for(Order o: ezshop.getAllOrders()){
            if(o.getOrderId() == order_id){
                assert(o.getStatus().equals("ISSUED"));
            }
        }
        ezshop.recordBalanceUpdate(1000);
    }


    @Test
    public void T_payOrderFor_Invalid_user1() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);

        int ordered_quantity=5;
        double price = 25;

        double oldBalance = ezshop.computeBalance();

        ezshop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        });

    }

    @Test
    public void T_payOrderFor_Invalid_user2() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Shop", "ciao");
        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);
        p.setQuantity(100);

        int ordered_quantity=5;
        double price = 25;

        ezshop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        });

    }

    @Test
    public void T_payOrderFor_Invalid_barcode1() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String invalidBarcode = "0000000000018";

        int ordered_quantity=5;
        double price = 0.5;

        double oldBalance = ezshop.computeBalance();

        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.payOrderFor(invalidBarcode, ordered_quantity, price);
        });

        double newBalance = ezshop.computeBalance();

        assert (oldBalance == newBalance);
    }

    @Test
    public void T_payOrderFor_Invalid_barcode2() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String invalidBarcode = "";

        int ordered_quantity=5;
        double price = 0.5;

        double oldBalance = ezshop.computeBalance();

        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.payOrderFor(invalidBarcode, ordered_quantity, price);
        });

        double newBalance = ezshop.computeBalance();

        assert (oldBalance == newBalance);
    }

    @Test
    public void T_payOrderFor_Invalid_barcode3() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");


        int ordered_quantity=5;
        double price = 0.5;

        double oldBalance = ezshop.computeBalance();

        assertThrows(InvalidProductCodeException.class, () -> {
            ezshop.payOrderFor(null, ordered_quantity, price);
        });

        double newBalance = ezshop.computeBalance();

        assert (oldBalance == newBalance);

    }

    @Test
    public void T_payOrderFor_Invalid_quantity() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);
        p.setQuantity(100);

        int ordered_quantity=0;
        double price = 0.5;

        double oldBalance = ezshop.computeBalance();

        assertThrows(InvalidQuantityException.class, () -> {
            ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        });

        double newBalance = ezshop.computeBalance();

        assert (oldBalance == newBalance);

    }

    @Test
    public void T_payOrderFor_Invalid_ppu() throws InvalidQuantityException, UnauthorizedException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        ezshop.login("Admin", "ciao");

        String validBarcode = "0000000000017";
        ProductType p = ezshop.getProductTypeByBarCode(validBarcode);
        p.setQuantity(100);

        int ordered_quantity=5;
        double price = 0.0;
        double oldBalance = ezshop.computeBalance();

        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezshop.payOrderFor(validBarcode, ordered_quantity, price);
        });

        double newBalance = ezshop.computeBalance();

        assert (oldBalance == newBalance);
    }


    @Test
    public void Test_RecorderArrival_Valid_admin() throws InvalidPasswordException, InvalidUsernameException, InvalidLocationException, UnauthorizedException, InvalidOrderIdException, InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user.getUsername());

        String barcode = "0000000000031";
        int ordered_quantity=7;
        double price = 16.1;
        int order_id3 = ezshop.payOrderFor(barcode, ordered_quantity, price);
        assert (order_id3 > 0);

        int orderId = order_id3;
        int oldQty = 0;
        for(Order o : this.ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                oldQty = ezshop.getProductTypeByBarCode( o.getProductCode() ).getQuantity();
            }
        }

        assertTrue( ezshop.recordOrderArrival(orderId) );

        for(Order o : this.ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                int newQty = ezshop.getProductTypeByBarCode( o.getProductCode() ).getQuantity();
                assert (oldQty < newQty);
                assert (o.getStatus().equals("COMPLETED"));
            }
        }

    }

    @Test
    public void Test_RecorderArrival_Valid_shop_doNothing() throws InvalidPasswordException, InvalidUsernameException, InvalidLocationException, UnauthorizedException, InvalidOrderIdException, InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user.getUsername());

        String barcode = "0000000000048";
        int ordered_quantity=20;
        double price = 30.0;
        int order_id4 = ezshop.payOrderFor(barcode, ordered_quantity, price);
        assert (order_id4 > 0);


        int orderId = order_id4;
        int oldQty = 0;
        for(Order o : this.ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                oldQty = ezshop.getProductTypeByBarCode( o.getProductCode() ).getQuantity();
            }
        }

        assertTrue( ezshop.recordOrderArrival(orderId) );

        for(Order o : this.ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                int newQty = ezshop.getProductTypeByBarCode( o.getProductCode() ).getQuantity();
                assert (oldQty < newQty);
                assert (o.getStatus().equals("COMPLETED"));
            }
        }

        //do nothing
        assertTrue( ezshop.recordOrderArrival(orderId) );
        for(Order o : this.ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                int newQty = ezshop.getProductTypeByBarCode( o.getProductCode() ).getQuantity();
                assert (oldQty < newQty);
                assert (o.getStatus().equals("COMPLETED"));
            }
        }

    }

    @Test
    public void Test_RecorderArrival_InValid_Cashier() throws InvalidPasswordException, InvalidUsernameException, InvalidLocationException, UnauthorizedException, InvalidOrderIdException, InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException {
        User user = ezshop.login("Cashier", "ciao");
        assertNotNull(user.getUsername());



        int orderId = 1;
        assertThrows( UnauthorizedException.class, () -> {
            ezshop.recordOrderArrival(orderId);
        });

    }


    @Test
    public void Test_RecorderArrival_InValid_NoOrder() throws InvalidPasswordException, InvalidUsernameException, InvalidLocationException, UnauthorizedException, InvalidOrderIdException, InvalidProductCodeException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user.getUsername());

        int orderId = 10000;
        int oldQty = 0;
        for(Order o : this.ezshop.getAllOrders()){
            if(o.getOrderId() == orderId){
                oldQty = ezshop.getProductTypeByBarCode( o.getProductCode() ).getQuantity();
            }
        }

        assertFalse(ezshop.recordOrderArrival(orderId));

    }

    @Test
    public void Test_RecorderArrival_InValid_InvalidId() throws InvalidPasswordException, InvalidUsernameException, InvalidLocationException, UnauthorizedException, InvalidOrderIdException, InvalidProductCodeException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user.getUsername());

        int orderId1 = -0;
        Integer orderId2 = null;
        int oldQty = 0;

        assertThrows( InvalidOrderIdException.class, () -> {
            ezshop.recordOrderArrival(orderId1);
        });

        assertThrows( InvalidOrderIdException.class, () -> {
            ezshop.recordOrderArrival(orderId2);
        });

    }


    @Test
    public void Test_RecorderArrival_InValid_Location() throws InvalidPasswordException, InvalidUsernameException, InvalidLocationException, UnauthorizedException, InvalidOrderIdException, InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductIdException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user.getUsername());



        String barcode = "0000000000031";
        ProductType p =ezshop.getProductTypeByBarCode(barcode);

        assertThrows( InvalidLocationException.class, () -> {
            ezshop.updatePosition(p.getId(), "3-3-3");
        });
    }


    @Test
    public void Test_RecorderArrival_Valid_Admin() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Admin", "ciao");
        assertNotNull(user);
        assertNotNull( ezshop.getAllOrders() );
    }

    @Test
    public void Test_Valid_Cashier() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Shop", "ciao");
        assertNotNull(user);
        assertNotNull( ezshop.getAllOrders() );
    }

    @Test
    public void Test_RecorderArrival_Valid_Shop() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        User user = ezshop.login("Cashier", "ciao");
        assertNotNull(user);
        assertThrows(UnauthorizedException.class, () -> {
            ezshop.getAllOrders();
        });
    }

    @After
    public void reset() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidRoleException {
        ezshop.logout();
        ezshop.login("Admin", "ciao");
        ezshop.recordBalanceUpdate(-1000);
        ezshop.logout();
    }


}
