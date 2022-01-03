package it.polito.ezshop.jUnitTests;

import it.polito.ezshop.data.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestEZShop_SaleTransactions_WB {
    DBClass db = new DBClass();

    @Before
    public void cleanTable(){
        db.deleteSaleTransactions();
    }


    @Test
    public void testSales(){
        db.deleteSaleTransactions();


        SaleTransactions sales = new SaleTransactions();
        assertNotNull(db);
        assertNotNull(sales);
        assertNotNull (sales.getSaleTransactions());

        sales = db.importTransactions();
        assert(sales.getSaleTransactions().isEmpty());

        SaleTransactionClass st = new SaleTransactionClass();
        SaleTransactionClass st2 = new SaleTransactionClass();
        assertNotNull(st);

        sales.setNewId();
        int id = sales.getNewId();
        assert(id == 1);

        st.setTicketNumber(id);

        sales.addTransaction(st);

        assert(sales.getSaleTransactionById(1) == st);
        assert(sales.getTransaction(1) == st);

        st2.setTicketNumber(sales.getNewId());
        sales.addTransaction(st2);

        assert (sales.getSaleTransactions().lastKey() == 2);

        assert(sales.getSaleTransactionById(id) == st);
        assertNull (sales.getSaleTransactionById(0));

       sales.removeTransaction(st2);

        assert (sales.getSaleTransactions().lastKey()==1);

        TreeMap<Integer , SaleTransactionClass> s = new TreeMap<>();
        SaleTransactions sales2 = new SaleTransactions(s);

        assertNotNull(sales2);

        sales2.addTransaction(st);
        assert (sales2.getSaleTransactions().lastKey()==1);

        assertFalse (st.isPaid());
        st.setPaid(true);
        assertTrue (st.isPaid());

        TicketEntryClass t1 = new TicketEntryClass();
        TicketEntryClass t2 = new TicketEntryClass();

        List<TicketEntry> ls = new ArrayList<>();
        List<TicketEntry> ls2;
        ls.add(t1);

       st.setEntries(ls);
       st.addNewTicketEntry(t2);

       ls2 = st.getEntries();
       assertNotNull(ls2);

        assert (st.getDiscountRate()== 0.0);
        st.setDiscountRate(0);
        assert (st.getDiscountRate() == 0);

        assert (st.getPrice() == 0);
        st.setPrice(12);
        assert (st.getPrice() > 0);

        assert (st.getPaymentType().equals(""));
        st .setPaymentType("cash");
        assertEquals(st.getPaymentType() , "cash");

        t1.setBarCode("p1");
        t2.setBarCode("p2");
        assertNull(st.getEntryByBarCode("Null"));
        assertNotNull(st.getEntryByBarCode("p1"));
        assertEquals(t1.getBarCode() , "p1");

        st.getEntries().get(0).setPricePerUnit(3);
        st.getEntries().get(0).setAmount(1);
        st.getEntries().get(1).setAmount(2);
        st.getEntries().get(1).setPricePerUnit(3);
        assert (st.calculatePrice() == 9);

        //data base tests

        assertTrue (db.deleteSaleTransactions());
        assertTrue(db.addSaleTransaction(st));
        assertTrue(db.addSaleEntries(st.getEntries(), st.getTicketNumber()));
        db.deleteSaleTransaction(st);

        TicketEntryClass t = new TicketEntryClass("6291041500213", "descr", 5, 5.0, 0.8);
        t.setProductDescription("new_descr");
        t.setDiscountRate(0.5);
        assert(t.getProductDescription().equals("new_descr"));
        assert(t.getDiscountRate() == 0.5);
        List<TicketEntry> list = new ArrayList<>();
        list.add(t);
        SaleTransactionClass s1 = new SaleTransactionClass(sales.getNewId(), 20.8, "cash", 0.8, list);
        assertNotNull(s1);

    }

}
