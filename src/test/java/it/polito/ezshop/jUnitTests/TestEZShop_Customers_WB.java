package it.polito.ezshop.jUnitTests;

import it.polito.ezshop.data.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShop_Customers_WB {
    Customers customers;
    DBClass db = new DBClass();

    @Test
    public void staticCustomerTest(){

        customers = new Customers();
        assertNotNull(customers);
        assertNull(customers.getCustomerById(0));
        assertNotNull(customers.getCustomerList());
        customers.setNewId(); //newId == 0
        assert(customers.checkNewId() == 0);
        Integer newId = customers.getNewId(); //returns ++newId
        assert(newId == 1);

        CustomerClass c1 = new CustomerClass(newId,"mario rossi", customers.getNewValidCardCode(), 0);
        assertNotNull(c1);
        assertTrue(customers.addCustomer(c1.getId(),c1));
        String newCard = c1.getCustomerCard();
        assertTrue(customers.checkIfCardExists(newCard)); //checkIfCardExists test, returns true if a card is already present (newCard should be already present)

        assertFalse(customers.checkIfCardExists(customers.getNewValidCardCode())); //getNewValidCardCode test through checkIfCardExists

        CustomerClass c2 = new CustomerClass(customers.getNewId(),"mario rossi", "", -1);
        assertFalse(customers.addCustomer(c2.getId(),c2)); //name "mario rossi" should be already present

        CustomerClass c3 = new CustomerClass(newId,"mario bianchi", "", -1);
        assertFalse(customers.addCustomer(c3.getId(),c3)); //newId used above, thus it's already present

        CustomerClass c4 = new CustomerClass(customers.getNewId(),"mario bianchi", "", -1);

        CustomerClass c5 = new CustomerClass();
        assertNotNull(c5);

        assert(c5.getId() == -1);
        assert(c5.getCustomerName().equals(""));
        assert(c5.getCustomerCard().equals(""));
        assert(c5.getPoints() == -1);

        customers.setNewId(); //increase newId

        c5.setId(customers.getNewId());
        c5.setCustomerName("ciao");
        assert(c5.getCustomerName().equals("ciao"));
        c5.setCustomerCard("0123456789");
        assert(c5.getCustomerCard().equals("0123456789"));
        c5.setPoints(14);
        assert(c5.getPoints() == 14);
        customers.addCustomer(c5.getId(),c5);

        assert(c5.equals(customers.getCustomerByCard("0123456789")));

        String newValidCard = customers.getNewValidCardCode();
        assertTrue(c4.attachLoyaltyCard(newValidCard));
        assert(c4.getCustomerCard().equals(newValidCard));
        assert(c4.getPoints() == 0);

        assertFalse(c4.updatePoints(-5)); // returns false because c4.points should never become negative
        assertTrue(c4.updatePoints(4));
        assert(c4.getPoints()==4);

        CustomerClass toBeRemoved = customers.getCustomerById(1);
        assertNotNull(toBeRemoved); //customer (id=1) found
        assertTrue(customers.removeCustomer(toBeRemoved)); //customer (id=1) removed succesfully
        assertFalse(customers.removeCustomer(c4));
        assertNull(customers.getCustomerById(1)); //customer (id=1) is not present anymore
    }

    @Before
    public void cleanTable(){
        db.deleteCustomers();
    }

    @Test
    public void databaseCustomerTest(){
        db.deleteCustomers();

        Customers customers = db.importCustomers();
        assertNotNull(customers);
        assert(customers.getCustomerList().isEmpty());

        //ADD CUSTOMER TEST

        CustomerClass c1 = new CustomerClass(1,"mario bianchi", "", -1);
        assertTrue(db.addCustomer(c1));
        String c1name = c1.getCustomerName();
        Integer c1id = c1.getId();
        assert(c1name.equals(db.getCustomer(c1id).getCustomerName()));

        //DELETE CUSTOMER TEST

        CustomerClass toBeDeleted = new CustomerClass(2,"mario rossi", "", -1);
        assertTrue(db.addCustomer(toBeDeleted));

        //deleting toBeDeleted
        assertTrue(db.deleteCustomer(toBeDeleted.getId()));

        //toBeDeleted should not be present anymore
        assertNull(db.getCustomer(toBeDeleted.getId()));

        //UPDATE CUSTOMER NAME TEST

        CustomerClass toBeModified =  new CustomerClass(3,"mario verdi", "", -1);
        db.addCustomer(toBeModified);
        //changing name from "mario verdi" to "giuseppe rossi"
        assertTrue(db.updateCustomerName(toBeModified.getId(),"giuseppe rossi"));

        toBeModified = db.getCustomer(toBeModified.getId());

        assert(toBeModified.getCustomerName().equals("giuseppe rossi"));

        //ATTACH CARD TEST
        //attached card for "giuseppe rossi" is "3216549870", points = 0
        assertTrue(db.attachCard(toBeModified.getId(),"3216549870"));

        toBeModified = db.getCustomer(toBeModified.getId());

        assert(toBeModified.getCustomerCard().equals("3216549870"));
        assert(toBeModified.getPoints() == 0);

        //UPDATE CUSTOMER CARD TEST
        //new card for "giuseppe rossi" is "9876543210"
        assertTrue(db.updateCustomerCard(toBeModified.getId(),"9876543210"));

        toBeModified = db.getCustomer(toBeModified.getId());

        assert(toBeModified.getCustomerCard().equals("9876543210"));

        //UPDATE POINTS TEST
        // +10 points for card "9876543210"
        assertTrue(db.updatePoints(toBeModified.getCustomerCard(),10));

        toBeModified = db.getCustomer(toBeModified.getId());
        System.out.println(toBeModified);
        assert( toBeModified.getPoints() == 10);

        db.deleteCustomer(1);
        db.deleteCustomer(2);
        db.deleteCustomer(3);
    }


}
