package it.polito.ezshop.APITests;

import it.polito.ezshop.APITests.resetTests;
import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class accountingTests {
    EZShopInterface ezshop = new EZShop() ;

    @Before
    public void reset() throws InvalidQuantityException, InvalidLocationException, InvalidPricePerUnitException, InvalidProductIdException, InvalidTransactionIdException, UnauthorizedException, InvalidPasswordException, InvalidProductDescriptionException, InvalidRoleException, InvalidPaymentException, InvalidUsernameException, InvalidProductCodeException {
        resetTests test_reset = new resetTests();
        test_reset.resetTest();
        ezshop.login("Admin","ciao");
    }

    @Test
    public void unauthorizedGetCreditsAndDebits() throws InvalidPasswordException, InvalidUsernameException {
        ezshop.logout();
        assertThrows(UnauthorizedException.class,()->ezshop.getCreditsAndDebits(null,null));
        ezshop.login("Admin", "ciao");
    }

    @Test
    public void getCreditsAndDebitsTest_firstDate_secondDate() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        LocalDate from = LocalDate.of(2021,5,3);
        LocalDate to = LocalDate.of(2021,5,8);
        List<BalanceOperation> myBoList = ezshop.getCreditsAndDebits(from,to);
        assertNotNull(myBoList);
        myBoList.forEach( c ->{
            assert((c.getDate().isAfter(from) || c.getDate().isEqual(from)) && (c.getDate().isBefore(to) ||c.getDate().isEqual(to)));
        });
    }

    @Test
    public void getCreditsAndDebitsTest_firstDate_secondDate_inverted() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        LocalDate to = LocalDate.of(2021,5,3);
        LocalDate from = LocalDate.of(2021,5,8);
        ezshop.getCreditsAndDebits(from,to);
        List<BalanceOperation> myBoList = ezshop.getCreditsAndDebits(from,to);
        assertNotNull(myBoList);
        myBoList.forEach( c ->{ //inverted check
            assert((c.getDate().isAfter(to) || c.getDate().isEqual(to)) && (c.getDate().isBefore(from) ||c.getDate().isEqual(from)));
        });
    }

    @Test
    public void getCreditsAndDebitsTest_firstDate_secondNull() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        LocalDate from = LocalDate.of(2021,5,3);
        List<BalanceOperation> myBoList = ezshop.getCreditsAndDebits(from,null);
        assertNotNull(myBoList);
        myBoList.forEach( c ->{
            assert((c.getDate().isAfter(from) || c.getDate().isEqual(from)) && (c.getDate().isBefore(LocalDate.now()) ||c.getDate().isEqual(LocalDate.now())));
        });
    }

    @Test
    public void getCreditsAndDebitsTest_firstNull_secondDate() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        LocalDate to = LocalDate.of(2021,5,8);
        List<BalanceOperation> myBoList = ezshop.getCreditsAndDebits(null,to);
        assertNotNull(myBoList);
        myBoList.forEach( c ->{
            assert((c.getDate().isAfter(LocalDate.MIN) || c.getDate().isEqual(LocalDate.MIN)) && (c.getDate().isBefore(to) ||c.getDate().isEqual(to)));
        });
    }

    @Test
    public void getCreditsAndDebitsTest_firstNull_secondNull() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        List<BalanceOperation> myBoList = ezshop.getCreditsAndDebits(null,null);
        assertNotNull(myBoList);
        myBoList.forEach( c ->{
            assert((c.getDate().isAfter(LocalDate.MIN) || c.getDate().isEqual(LocalDate.MIN)) && (c.getDate().isBefore(LocalDate.now()) ||c.getDate().isEqual(LocalDate.now())));
        });
    }

    @Test
    public void unauthorizedComputeBalance() throws InvalidPasswordException, InvalidUsernameException {
        ezshop.logout();
        assertThrows(UnauthorizedException.class,()->ezshop.computeBalance());
        ezshop.login("Admin", "ciao");
    }

    @Test
    public void computeBalanceTest() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        ezshop.recordBalanceUpdate(100);
        double before = ezshop.computeBalance();
        assert(before >= 100);
        ezshop.recordBalanceUpdate(-20);
        double after = ezshop.computeBalance();
        assert(after < before);
    }
}
