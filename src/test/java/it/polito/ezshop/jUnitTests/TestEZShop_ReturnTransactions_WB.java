package it.polito.ezshop.jUnitTests;

import it.polito.ezshop.data.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class TestEZShop_ReturnTransactions_WB {
    DBClass db = new DBClass();

    @Before
    public void cleanTable(){
        db.deleteReturnTransactions();
    }

    @Test
    public void testReturns (){
        db.deleteReturnTransactions();

        ReturnTransactionRepository rtp = db.importReturnTransactions();
        assertNotNull(rtp);
        ReturnTransactionClass rt = new ReturnTransactionClass();
        rtp.setNewId();
        int id = rtp.getNewId();
        assert(id == 1);
        rt.setId(id);
        assert(rt.getId() == 1);
        TicketEntryClass t = new TicketEntryClass();
        t.setBarCode("abc");
        t.setAmount(2);
        t.setPricePerUnit(5);
        rt.addEntry(t.getBarCode(),t);
        assertNotNull(rt);
        assertNotNull(t);
        assert(rt.getEntries().get("abc").getBarCode().equals("abc"));

        TicketEntryClass t2 = new TicketEntryClass();
        t2.setBarCode("abc");
        t2.setAmount(3);
        t2.setPricePerUnit(5);
        SaleTransactionClass st = new SaleTransactionClass();
        st.setTicketNumber(1);
        st.setPrice(15);
        st.addNewTicketEntry(t2);

        rt.setSaleTransaction(st);
        assertNotNull(rt.getSaleTransaction());

        assertNotNull(rt.getEntries());
        rt.setPrice(rt.calculateTotalReturnPrice());
        assert(rt.getPrice() == 10);
        double price = rt.getEntries().get("abc").getPricePerUnit();
        assert (price == 5);

        assertEquals(rt.getSaleTransaction().getEntryByBarCode("abc").getAmount() ,3);
        rt.updateSaleEntries(true);
        assert(st.getEntryByBarCode("abc").getAmount()== 1);

        Products ps = db.importProducts();
        ProductTypeClass p = new ProductTypeClass();
        p.setBarCode("abc");
        p.setQuantity(10);
        ps.addProduct(p);

        rt.updateProductQuantities(ps,true);

        assert( p.getQuantity()== 12);

        assert (rt.getId() ==1);

        rtp.addReturn(rt , rt.getId());

        ReturnTransactionClass  newRt = rtp.getReturnTransactionById(1);
        assertNotNull(newRt);

        assert(newRt.getId() == 1);

        assert(rtp.getNewId() == 2);

        rtp.removeReturnTransaction(1);

        assertNull(rtp.getReturnTransactionById(1));

        /* TEST SU COSTRUTTORE NON VUOTO */
        SaleTransactions sales = new SaleTransactions();
        sales.setNewId();
        TicketEntryClass ticket = new TicketEntryClass();
        ticket.setBarCode("6291041500213");
        List<TicketEntry> list2 = new ArrayList<>();
        list2.add(t);
        TreeMap<String, TicketEntryClass> list = new TreeMap<>();
        list.put(ticket.getBarCode(), ticket);
        SaleTransactionClass s1 = new SaleTransactionClass(sales.getNewId(), 20.8, "cash", 0.8, list2);
        ReturnTransactionClass ret = new ReturnTransactionClass(rtp.getNewId(), s1.getPrice(), s1, list);
        assertNotNull(ret);
        assert(ret.getSaleTransaction().equals(s1));
        //manca di testare per il data base
    }

}
