package it.polito.ezshop;
import static org.junit.Assert.*;
import it.polito.ezshop.data.*;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import org.junit.Test;
import it.polito.ezshop.data.CreditCards;
import java.util.List;

public class TestEZShop_importCreditCards {
    EZShop ezShop = new EZShop();

    @Test
    public void importCCTest(){
        CreditCards creditCards;
        creditCards = ezShop.importCreditCards();
        assertNotNull(creditCards.getCreditCard("4485370086510891"));
    }
}
