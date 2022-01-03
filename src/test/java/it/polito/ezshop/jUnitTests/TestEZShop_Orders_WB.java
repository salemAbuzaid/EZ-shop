package it.polito.ezshop.jUnitTests;

import it.polito.ezshop.data.DBClass;
import it.polito.ezshop.data.OrderClass;
import it.polito.ezshop.data.Orders;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.*;

public class TestEZShop_Orders_WB {
    Orders orders;
    DBClass db = new DBClass();

    @Test
    public void ordersTestStatic(){
        //no orders
        orders = new Orders();
        assertNotNull(orders);
        assert(orders.getNumOrders() == 0);
        assertNull(orders.getOrderById(0));
        assertNotNull(orders.getOrders());

        OrderClass o1 = new OrderClass(orders.getNewId(), 1.1, 1, "issued", -1, "pr1");
        assertNotNull(o1);

        assert( o1.getOrderId() == 1);
        assert( o1.getPricePerUnit() == 1.1);
        assert( o1.getQuantity() == 1 );
        assert(o1.getStatus().equals("issued"));
        assert( o1.getBalanceId() == -1 );
        assert(o1.getProductCode().equals("pr1"));

        OrderClass o2 = new OrderClass(orders.getNewId(), 2.2, 2, "issued", -1, "pr2");
        assertNotNull(o2);

        assert( o2.getOrderId() == 2);
        assert( o2.getPricePerUnit() == 2.2);
        assert( o2.getQuantity() == 2 );
        assert(o2.getStatus().equals("issued"));
        assert( o2.getBalanceId() == -1 );
        assert(o2.getProductCode().equals("pr2"));

        orders.addOrder(o1);
        orders.addOrder(o2);

        o1.setPricePerUnit(11.11);
        o1.setQuantity(11);
        o1.setStatus("payed");
        o1.setBalanceId(-2);
        o1.setProductCode("pr11");
        o1.setOrderId(11);

        assert( o1.getPricePerUnit() == 11.11);
        assert( o1.getQuantity() == 11 );
        assert( o1.getStatus().equals("payed"));
        assert( o1.getBalanceId() == -2 );
        assert( o1.getProductCode().equals("pr11"));
        assert( o1.getOrderId() == 11);

        o1 = orders.getOrderById(1);
        assert( o1.getPricePerUnit() == 11.11);

    }

    @Before
    public void ordersReinitializeDB(){
        db.deleteOrders();
    }

    @Test
    public void ordersTestDB(){
        db.deleteOrders();

        Orders ordini = db.importOrders();

        OrderClass o1 = new OrderClass(ordini.getNewId(), 1.0, 1, "issued", -1, "1");
        ordini.addOrder(o1);
        assert (o1 == ordini.getOrderById(o1.getOrderId()));
        db.addOrder(o1);

        OrderClass o2 = new OrderClass(ordini.getNewId(), 2.0, 2, "issued", -1, "2");
        ordini.addOrder(o2);
        assert (o2 == ordini.getOrderById(o2.getOrderId()));
        db.addOrder(o2);

        OrderClass o3 = new OrderClass(ordini.getNewId(), 3.0, 3, "issued", -1, "3");
        ordini.addOrder(o3);
        assert (o3 == ordini.getOrderById(o3.getOrderId()));
        db.addOrder(o3);

        Orders testOrdini = new Orders();
        assertNotNull( testOrdini);
        testOrdini = db.importOrders();
        assertNotNull( testOrdini);

        OrderClass ordineNULL = ordini.getOrderById(5);
        assertNull(ordineNULL);

        OrderClass ordine2 = ordini.getOrderById(2);
        assertNotNull (ordine2 );

        assertTrue(db.removeOrder(2));
        assertNull (db.getOrder(2));

        OrderClass ordine3Test = db.getOrder(3);

        assertNotNull (ordine3Test );

        assertTrue(db.orderUpdatePricePerUnit(3, 33.0));
        assertTrue(db.orderUpdateQuantity(3,33));
        assertTrue(db.orderUpdateBalanceID(3, 33));
        assertTrue(db.orderUpdateStatus(3, "payed"));

        assert(!ordini.getOrdersList().isEmpty());

        assert (ordine3Test.getOrderId().equals(db.getOrder(3).getOrderId()));

        db.removeOrder(1);
        db.removeOrder(3);

    }
}
