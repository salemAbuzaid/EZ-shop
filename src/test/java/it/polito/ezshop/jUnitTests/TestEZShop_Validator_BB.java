package it.polito.ezshop.jUnitTests;

import it.polito.ezshop.data.CustomerClass;
import it.polito.ezshop.data.EZShop;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestEZShop_Validator_BB {
    EZShop ezShop = new it.polito.ezshop.data.EZShop();


    /**
     * non tutti i caratteri del barcode sono numeri
     */
    @Test
    public void TestBCFalse(){

        String barcode = "122";
        assertFalse( ezShop.barcodeValidator(barcode) );

        assertFalse(ezShop.barcodeValidator(null));

        barcode = "123456789012345678";
        assertFalse( ezShop.barcodeValidator(barcode) );


        barcode = "1234a678v012d45678";
        assertFalse( ezShop.barcodeValidator(barcode) );

        barcode = "123abc";
        assertFalse( ezShop.barcodeValidator(barcode) );

        barcode = "+1234567890123";
        assertFalse( ezShop.barcodeValidator(barcode) );

        barcode = "1234567890123a";
        assertFalse( ezShop.barcodeValidator(barcode) );

        barcode = "-1234a678v012d45678";
        assertFalse( ezShop.barcodeValidator(barcode) );

        barcode = "-123abc";
        assertFalse( ezShop.barcodeValidator(barcode) );

        barcode = "-12345678901234";
        assertFalse( ezShop.barcodeValidator(barcode) );

    }

    @Test
    public void TestBCTrue(){
        //example prof
        String example ="6291041500213";

        //12 digit
        String barcode12t="723951367038";
        String barcode12f="123456789015";

        //13 digit
        String barcode13t="1234567890128";
        String barcode13f="1234567890122";

        //14 digit
        String barcode14t="94201294365724";
        String barcode14f="94201294365727";


        assertTrue((ezShop.barcodeValidator(example)));
        assertTrue(ezShop.barcodeValidator(barcode12t));
        assertTrue(ezShop.barcodeValidator(barcode13t));
        assertTrue(ezShop.barcodeValidator(barcode14t));

        assertFalse(ezShop.barcodeValidator(barcode12f));
        assertFalse(ezShop.barcodeValidator(barcode13f));
        assertFalse(ezShop.barcodeValidator(barcode14f));

    }

    /**
     * non tutti i caratteri sono numeri
     */
    @Test
    public void TestCCFalse(){
        String empty = "";
        assertFalse(ezShop.validCreditCard(""));
        assertFalse(ezShop.validCreditCard(null));
        String creditcard = "122";
        assertFalse(ezShop.validCreditCard(creditcard));
        creditcard="98765432101234567890";
        assertFalse(ezShop.validCreditCard(creditcard));
        creditcard="12123921ab21215";
        assertFalse(ezShop.validCreditCard(creditcard));
        creditcard="12123921ab212153";
        assertFalse(ezShop.validCreditCard(creditcard));
        creditcard="+1212392156212153";
        assertFalse(ezShop.validCreditCard(creditcard));
        creditcard="+121239215212153";
        assertFalse(ezShop.validCreditCard(creditcard));
    }

    @Test
    public void TestCCTrue(){

        String barcode12 = "987654321015";

        String barcode13t ="9876543210128";
        String barcode13f ="9876543210123";

        String barcode14t ="98765432101237";
        String barcode14f ="98765432101234";

        String barcode15t ="987654321012347";
        String barcode15f ="987654321012345";

        String barcode16t="4551188765219900";
        String barcode16f="4551188765219902";

        String barcode17t ="98765432105279";
        String barcode17f ="98765432105278";

        String barcode18t ="98765432101234569";
        String barcode18f ="98765432101234567";

        String barcode19t ="987654321012345671";
        String barcode19f ="987654321012345678";


        assertFalse((ezShop.validCreditCard((barcode12))));

        assertTrue((ezShop.validCreditCard(barcode13t)));
        assertFalse((ezShop.validCreditCard(barcode13f)));

        assertTrue(ezShop.validCreditCard(barcode14t));
        assertFalse(ezShop.validCreditCard(barcode14f));

        assertTrue(ezShop.validCreditCard(barcode15t));
        assertFalse(ezShop.validCreditCard(barcode15f));

        assertTrue((ezShop.validCreditCard(barcode16t)));
        assertFalse((ezShop.validCreditCard(barcode16f)));

        assertTrue(ezShop.validCreditCard(barcode17t));
        assertFalse(ezShop.validCreditCard(barcode17f));

        assertTrue(ezShop.validCreditCard(barcode18t));
        assertFalse(ezShop.validCreditCard(barcode18f));

        assertTrue(ezShop.validCreditCard(barcode19t));
        assertFalse(ezShop.validCreditCard(barcode19f));

    }

    @Test
    public void validLoyaltyCard(){
        String validCard = "0123456789";
        CustomerClass c = new CustomerClass(1,"mario bianchi", "", -1);

        assertTrue(c.attachLoyaltyCard(validCard));

        assert(c.getCustomerCard().equals(validCard));
        assert(c.getPoints()==0);
    }

    @Test
    public void invalidLoyaltyCard(){
        String invalidCard1 = "01234567";
        String invalidCard2 = "0123456789012";
        String invalidCard3 = "-.xèùaAb/_";
        CustomerClass c = new CustomerClass(1,"mario bianchi", "", -1);

        assertFalse(c.attachLoyaltyCard(invalidCard1)); //card code is too short
        assertFalse(c.attachLoyaltyCard(invalidCard2)); //card code is too short
        assertFalse(c.attachLoyaltyCard(invalidCard3)); //doesn't match regex (only capital letters and numbers)
    }

}
