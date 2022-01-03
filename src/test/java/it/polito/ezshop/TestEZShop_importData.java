package it.polito.ezshop;
import it.polito.ezshop.APITests.resetTests;
import it.polito.ezshop.data.*;
import static org.junit.Assert.*;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.jUnitTests.TestEZShop_Orders_WB;
import org.junit.Test;
import java.util.List;
import it.polito.ezshop.data.DBClass;

public class TestEZShop_importData {
    EZShop ezShop = new EZShop();

    @Test
    public void importDataTest() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidLocationException, InvalidTransactionIdException, InvalidProductDescriptionException, InvalidRoleException, InvalidPaymentException, InvalidPricePerUnitException, InvalidProductIdException, InvalidProductCodeException {
        resetTests test_reset = new resetTests();
        test_reset.resetTest();
        /* Inside the login there is the importData() */
        ezShop.login("Admin", "ciao");

        assertNotNull(ezShop.getAllCustomers());
        assertNotNull(ezShop.getAllOrders());
        assertNotNull(ezShop.getAllProductTypes());
        assertNotNull(ezShop.getAllUsers());
    }
}
