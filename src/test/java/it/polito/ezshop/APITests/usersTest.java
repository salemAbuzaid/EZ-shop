package it.polito.ezshop.APITests;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class usersTest {
    EZShopInterface ezShop = new it.polito.ezshop.data.EZShop();

    @Before
    public void reset() throws InvalidQuantityException, InvalidLocationException, InvalidPricePerUnitException, InvalidProductIdException, InvalidTransactionIdException, UnauthorizedException, InvalidPasswordException, InvalidProductDescriptionException, InvalidRoleException, InvalidPaymentException, InvalidUsernameException, InvalidProductCodeException {
        resetTests test_reset = new resetTests();
        test_reset.resetTest();
    }

    @Test
    public void createUserTest() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException, InvalidUserIdException, UnauthorizedException {
        Integer id = 0;

        /* Invalid username (us == null) */
        assertThrows(InvalidUsernameException.class, () -> {ezShop.createUser(null, "ciao", "Cashier");});

        /* Invalid username (us == empty) */
        assertThrows(InvalidUsernameException.class, ()->{ezShop.createUser("", "ciao", "Cashier");});

        /* Invalid password (psw == null) */
        assertThrows(InvalidPasswordException.class, () -> {ezShop.createUser("Cashier", null, "Cashier");});

        /* Invalid password (psw == empty) */
        assertThrows(InvalidPasswordException.class, () -> {ezShop.createUser("Cashier", "", "Cashier");});

        /* Invalid role (role == null) */
        assertThrows(InvalidRoleException.class, () -> {ezShop.createUser("Admin", "ciao", null);});

        /* Invalid role (role == empty) */
        assertThrows(InvalidRoleException.class, () -> {ezShop.createUser("Admin", "ciao", "");});

        /* Invalid role (role == 'inventoryManager') */
        assertThrows(InvalidRoleException.class, () -> {ezShop.createUser("Inv", "ciao", "InventoryManager");});

        /* Valid */
        assert((id = ezShop.createUser("Anna", "ciao", "Cashier")) > 0);
        ezShop.login("Admin", "ciao");
        assertNotNull(ezShop.getUser(id));

        /* Invalid -> the username already exists */
        assert(ezShop.createUser("Cashier", "ciao2", "Administrator") == -1);
        ezShop.deleteUser(4);
        ezShop.logout();
    }

    @Test
    public void deleteUserTest() throws InvalidPasswordException, InvalidUsernameException, InvalidUserIdException, UnauthorizedException, InvalidRoleException {
       Integer id = 0;

        /* Logged = null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.deleteUser(6);});

        /* Logged != null */
        ezShop.login("Cashier", "ciao");
        /* Invalid role */
        assertThrows(UnauthorizedException.class, ()->{ezShop.deleteUser(6);});
        /* Valid role */
        ezShop.logout();
        ezShop.login("Admin", "ciao");
        /* Id = null */
        assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(null);});
        /* Id <= 0 */
        assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(0);});
        assertThrows(InvalidUserIdException.class, ()->{ezShop.deleteUser(-2);});

        /* User doesn't exist */
        assertFalse(ezShop.deleteUser(500));

        /* All valid */
        assert((id = ezShop.createUser("Anna", "ciao", "Cashier")) > 0);
        assertTrue(ezShop.deleteUser(4));
        assertNull(ezShop.getUser(id));
        ezShop.logout();
    }

    @Test
    public void getAllUsersTest() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        /* Logged = null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.getAllUsers();});

        /* Logged != null */
        ezShop.login("Cashier", "ciao");
        /* Invalid role */
        assertThrows(UnauthorizedException.class, ()->{ezShop.getAllUsers();});
        /* Valid role */
        ezShop.logout();
        ezShop.login("Admin", "ciao");

        /* All valid */
        assertNotNull(ezShop.getAllUsers());
    }

    @Test
    public void getUserTest() throws InvalidPasswordException, InvalidUsernameException, InvalidUserIdException, UnauthorizedException {
        /* Logged = null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.getUser(1);});

        /* Logged != null */
        ezShop.login("Cashier", "ciao");
        /* Invalid role */
        assertThrows(UnauthorizedException.class, ()->{ezShop.getUser(1);});
        /* Valid role */
        ezShop.logout();
        ezShop.login("Admin", "ciao");
        /* Id = null */
        assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(null);});
        /* Id <= 0 */
        assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(0);});
        assertThrows(InvalidUserIdException.class, ()->{ezShop.getUser(-2);});

        /* User doesn't exist */
        assertNull(ezShop.getUser(500));

        /* All valid */
        assertNotNull(ezShop.getUser(1));

    }

    @Test
    public void updateUserRightsTest() throws InvalidPasswordException, InvalidUsernameException, InvalidUserIdException, UnauthorizedException, InvalidRoleException {
        /* Logged = null */
        assertThrows(UnauthorizedException.class, ()->{ezShop.updateUserRights(1, "ShopManager");});

        /* Logged != null */
        ezShop.login("Cashier", "ciao");
        /* Invalid role */
        assertThrows(UnauthorizedException.class, ()->{ezShop.updateUserRights(1, "ShopManager");});
        /* Valid role */
        ezShop.logout();
        ezShop.login("Admin", "ciao");
        /* Id = null */
        assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(null, "ShopManager");});
        /* Id <= 0 */
        assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(0, "ShopManager");});
        assertThrows(InvalidUserIdException.class, ()->{ezShop.updateUserRights(-2, "ShopManager");});
        /* Invalid role */
        assertThrows(InvalidRoleException.class, ()->{ezShop.updateUserRights(1, "InventoryManager");});

        /* User doesn't exist */
        assertFalse(ezShop.updateUserRights(7, "ShopManager"));

        /* User exists */
        ezShop.createUser("Change", "ciao", "Cashier");
        assertTrue(ezShop.updateUserRights(4, "ShopManager"));
        ezShop.deleteUser(4);
    }

    @Test
    public void loginTest() throws InvalidPasswordException, InvalidUsernameException {
        /* Invalid username (us == null) */
        assertThrows(InvalidUsernameException.class, () -> {ezShop.login(null, "ciao");});

        /* Invalid username (us == empty) */
        assertThrows(InvalidUsernameException.class, () -> {ezShop.login("", "ciao");});

        /* Invalid password (psw == null) */
        assertThrows(InvalidPasswordException.class, () -> {ezShop.login("Cashier", null);});

        /* Invalid password (psw == empty) */
        assertThrows(InvalidPasswordException.class, () -> {ezShop.login("Cashier", "");});

        /* Invalid credentials */
        assertNull(ezShop.login("Cashier", "ciao2"));
        assertNull(ezShop.login("Cash", "ciao"));

        /* Valid log */
        assertNotNull(ezShop.login("Cashier", "ciao"));

        /* A user is already logged */
        assertNull(ezShop.login("Admin", "ciao"));
    }

    @Test
    public void logoutTest() throws InvalidPasswordException, InvalidUsernameException {
        /* No user logged */
        assertFalse(ezShop.logout());

        /* User logged in */
        assertNotNull(ezShop.login("Cashier", "ciao"));
        assertTrue(ezShop.logout());
    }
}