package it.polito.ezshop.APITests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class customersTests{
    EZShopInterface ezshop = new EZShop();
    String username = "Admin";
    String password = "ciao";

    @Before
    public void reset() throws InvalidQuantityException, InvalidLocationException, InvalidPricePerUnitException, InvalidProductIdException, InvalidTransactionIdException, UnauthorizedException, InvalidPasswordException, InvalidProductDescriptionException, InvalidRoleException, InvalidPaymentException, InvalidUsernameException, InvalidProductCodeException {
        resetTests test_reset = new resetTests();
        test_reset.resetTest();
    }

    @Test
    public void defineCustomerTest() throws InvalidPasswordException, InvalidUsernameException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerIdException {
        String emptyCustomer = "";
        String nullCustomer = null;
        String validCustomer = "Gino Giorgino";

        assertThrows(UnauthorizedException.class,()->ezshop.defineCustomer(validCustomer));

        User logged_user = ezshop.login(username,password);
        assertNotNull(logged_user);

        assertThrows(InvalidCustomerNameException.class,() ->ezshop.defineCustomer(emptyCustomer));
        assertThrows(InvalidCustomerNameException.class,() ->ezshop.defineCustomer(nullCustomer));


        Integer id = ezshop.defineCustomer(validCustomer);
        assert(id != -1);
        assert(ezshop.getAllCustomers().size() > 0);
        assert(ezshop.getCustomer(id).getCustomerName().equals(validCustomer));
        //insert breakpoint below here to see the table changes
        ezshop.deleteCustomer(id); //inserted tuple deleted to make the test runnable several times
    }

    @Test
    public void modifyCustomerTest() throws UnauthorizedException, InvalidCustomerNameException, InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException, InvalidCustomerCardException {
        String emptyName = "", nullName = null, validName = "Mario Bianchi";
        String emptyCard = "", nullCard = null, validCard, invalidCard1 = "ABC4567890", invalidCard2 = "12345678901";

        assertThrows(UnauthorizedException.class,() -> ezshop.createCard());

        assertThrows(UnauthorizedException.class,()->ezshop.modifyCustomer(1, "ciao",""));
        ezshop.login(username,password);
        List<Customer> checkList = ezshop.getAllCustomers();
        validCard = ezshop.createCard();
        assert(validCard.length()==10 && validCard.matches("[0-9]*"));

        Integer id = ezshop.defineCustomer("Mario Giallo");
        assert(ezshop.getCustomer(id).getCustomerName().equals("Mario Giallo"));
        ezshop.modifyCustomer(id,validName,validCard);

        assertThrows(InvalidCustomerNameException.class,()->ezshop.modifyCustomer(id,emptyName,validCard));
        assertThrows(InvalidCustomerNameException.class,()->ezshop.modifyCustomer(id,nullName,validCard));
        assertThrows(InvalidCustomerCardException.class,()->ezshop.modifyCustomer(id,validName,emptyCard));
        assert(ezshop.getCustomer(id).getCustomerCard().equals(""));
        assertThrows(InvalidCustomerCardException.class,()->ezshop.modifyCustomer(id,validName,nullCard));
        assertThrows(InvalidCustomerCardException.class,()->ezshop.modifyCustomer(id,validName,invalidCard1));
        assertThrows(InvalidCustomerCardException.class,()->ezshop.modifyCustomer(id,validName,invalidCard2));

        ezshop.modifyCustomer(id,validName,validCard);
        assert(ezshop.getCustomer(id).getCustomerName().equals(validName));
        assert(ezshop.getCustomer(id).getCustomerCard().equals(validCard));
        //insert breakpoint below here to see the table changes
        ezshop.deleteCustomer(id);//inserted tuple deleted to make the test runnable several times
    }

    @Test
    public void deleteCustomer() throws InvalidCustomerNameException, UnauthorizedException, InvalidCustomerIdException, InvalidPasswordException, InvalidUsernameException {
        Integer nullId = null, invalidId1 = 0, invalidId2 = -3, validId;
        // no logged user -> UnauthorizedException
        assertThrows(UnauthorizedException.class,()->ezshop.deleteCustomer(1));

        User logged_user = ezshop.login(username,password);
        assertNotNull(logged_user);

        validId = ezshop.defineCustomer("Mimmo Modem");
        assert(ezshop.getCustomer(validId).getCustomerName().equals("Mimmo Modem"));

        //invalid ids -> InvalidCustomerIdException
        assertThrows(InvalidCustomerIdException.class, () -> ezshop.deleteCustomer(nullId));
        assertThrows(InvalidCustomerIdException.class, () -> ezshop.deleteCustomer(invalidId1));
        assertThrows(InvalidCustomerIdException.class, () -> ezshop.deleteCustomer(invalidId2));

        ezshop.deleteCustomer(validId);
        assertNull(ezshop.getCustomer(validId));
    }

    @Test
    public void getCustomerTest() throws InvalidCustomerNameException, UnauthorizedException, InvalidCustomerIdException, InvalidPasswordException, InvalidUsernameException {
        Integer validId, nullId = null, zeroId = 0, negativeId = -3, notFoundId = 500;

        assertThrows(UnauthorizedException.class, () -> ezshop.defineCustomer("Mario Bianchi"));

        ezshop.login(username,password);

        validId = ezshop.defineCustomer("Mario Biondi");
        assert(ezshop.getCustomer(validId).getCustomerName().equals("Mario Biondi"));

        assertThrows(InvalidCustomerIdException.class, () -> ezshop.getCustomer(nullId));
        assertThrows(InvalidCustomerIdException.class, () -> ezshop.getCustomer(zeroId));
        assertThrows(InvalidCustomerIdException.class, () -> ezshop.getCustomer(negativeId));
        assertNull(ezshop.getCustomer(notFoundId));

        ezshop.deleteCustomer(validId);
    }

    @Test
    public void getAllCustomersTest() throws InvalidPasswordException, InvalidUsernameException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerIdException {
        Integer c1,c2,c3;

        assertThrows(UnauthorizedException.class, () -> ezshop.getAllCustomers());

        ezshop.login(username,password);

        c1 = ezshop.defineCustomer("Carlo1");
        c2 = ezshop.defineCustomer("Carlo2");
        c3 = ezshop.defineCustomer("Carlo3");

        assert(ezshop.getAllCustomers().size() > 0);

        assertTrue(ezshop.getAllCustomers().stream().anyMatch(c -> (c.getId().equals(c1))));
        assertTrue(ezshop.getAllCustomers().stream().anyMatch(c -> (c.getId().equals(c2))));
        assertTrue(ezshop.getAllCustomers().stream().anyMatch(c -> (c.getId().equals(c3))));
        //insert breakpoint below here to see the table changes
        ezshop.deleteCustomer(c1);
        ezshop.deleteCustomer(c2);
        ezshop.deleteCustomer(c3);
    }

    @Test
    public void createCardTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        assertThrows(UnauthorizedException.class, () -> ezshop.getAllCustomers());

        ezshop.login(username,password);

        String card = ezshop.createCard();

        assert(card.length() == 10 && card.matches("[0-9]*"));
    }

    @Test
    public void attachCardToCustomerTest() throws InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException, UnauthorizedException, InvalidCustomerCardException, InvalidCustomerNameException {
        Integer invalidId1 = 0, invalidId2 = -3, nullId = null, validId;
        String emptyCard = "", nullCard = null, invalidCard1 = "ABC4567890", invalidCard2 = "12345678901";

        assertThrows(UnauthorizedException.class, () -> ezshop.attachCardToCustomer("0987654321",1));

        ezshop.login(username,password);

        validId = ezshop.defineCustomer("gino farfallino");
        String validCard = ezshop.createCard();

        assertThrows(InvalidCustomerIdException.class, () -> ezshop.attachCardToCustomer(validCard,invalidId1));
        assertThrows(InvalidCustomerIdException.class, () -> ezshop.attachCardToCustomer(validCard,invalidId2));
        assertThrows(InvalidCustomerIdException.class, () -> ezshop.attachCardToCustomer(validCard,nullId));
        assertThrows(InvalidCustomerCardException.class, () -> ezshop.attachCardToCustomer(emptyCard,validId));
        assertThrows(InvalidCustomerCardException.class, () -> ezshop.attachCardToCustomer(nullCard,validId));
        assertThrows(InvalidCustomerCardException.class, () -> ezshop.attachCardToCustomer(invalidCard1,validId));
        assertThrows(InvalidCustomerCardException.class, () -> ezshop.attachCardToCustomer(invalidCard2,validId));

        assertTrue(ezshop.attachCardToCustomer(validCard,validId));
        assertFalse(ezshop.attachCardToCustomer(validCard,validId)); //card "cust" already present
        //insert breakpoints to see table changes
        ezshop.deleteCustomer(validId);
    }

    @Test
    public void modifyPointsOnCard() throws InvalidPasswordException, InvalidUsernameException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerIdException, InvalidCustomerCardException {
        Integer negativePts = -6, positivePts = 5;
        String emptyCard = "", nullCard = null, invalidCard1 = "ABC4567890", invalidCard2 = "12345678901", nonExistingCard= "3216549870";
        assertThrows(UnauthorizedException.class, () -> ezshop.modifyPointsOnCard("1234567890",positivePts));

        ezshop.login(username,password);

        Integer id = ezshop.defineCustomer("Gino Giorgino");
        String card = ezshop.createCard();
        assertTrue(ezshop.attachCardToCustomer(card,id));

        assertThrows(InvalidCustomerCardException.class,() -> ezshop.modifyPointsOnCard(emptyCard,positivePts));
        assertThrows(InvalidCustomerCardException.class,() -> ezshop.modifyPointsOnCard(nullCard,positivePts));
        assertThrows(InvalidCustomerCardException.class,() -> ezshop.modifyPointsOnCard(invalidCard1,positivePts));
        assertThrows(InvalidCustomerCardException.class,() -> ezshop.modifyPointsOnCard(invalidCard2,positivePts));

        assertTrue(ezshop.modifyPointsOnCard(card,positivePts));
        assertEquals(ezshop.getCustomer(id).getCustomerCard(), card);
        assert(ezshop.getCustomer(id).getPoints() == 5);
        assertFalse(ezshop.modifyPointsOnCard(card,negativePts));
        assertFalse(ezshop.modifyPointsOnCard(nonExistingCard,positivePts));

        ezshop.deleteCustomer(id);
    }
}
