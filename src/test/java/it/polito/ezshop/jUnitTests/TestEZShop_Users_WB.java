package it.polito.ezshop.jUnitTests;

import it.polito.ezshop.data.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.*;

public class TestEZShop_Users_WB {
    Users users;
    DBClass db = new DBClass();

    @Test
    public void usersTestStatic() {
        users = new Users();
        assertNotNull(users);
        assertNull(users.getUserById(0));
        assertNotNull(users.getUsers());
        assertNull(users.getUserByUsername("ciao"));
        assertFalse(users.checkCredentials("ciao", "ciao"));
        assertFalse(users.removeUser(0));


        UserClass u1 = new UserClass();
        assertNotNull(u1);
        assert(u1.getId() == -1);
        assert(u1.getUsername() == "");
        assert(u1.getPassword() == "");
        assert(u1.getRole() == "");
        users.setNewId();
        assert(users.getNewId() == 0);

        u1.setId(users.getNewUserID());
        assert(u1.getId() == 1);
        u1.setUsername("ciao");
        assert(u1.getUsername() == "ciao");
        u1.setPassword("ciao");
        assert(u1.getPassword() == "ciao");
        u1.setRole("cashier");
        assert(u1.getRole() == "cashier");

        assertTrue(users.addUser(u1));

        UserClass u2 = new UserClass(users.getNewUserID(), "ciao2", "ciao2", "administrator");
        assertNotNull(u2);
        assert(u2.getId() == 2);
        assert(u2.getUsername() == "ciao2");
        assert(u2.getPassword() == "ciao2");
        assert(u2.getRole() == "administrator");

        assertTrue(users.addUser(u2));
        assertTrue(users.removeUser(2));
        assertFalse(users.removeUser(3));
        assert(u1 == users.getUserById(1));
        assertNull(users.getUserById(3));
        assert(u1 == users.getUserByUsername("ciao"));
        assertNull(users.getUserByUsername("ciao3"));
        assertFalse(users.checkCredentials("ciao3", "ciao"));
        assertFalse(users.checkCredentials("ciao", "ciao2"));
        assertTrue(users.checkCredentials("ciao", "ciao"));
    }

    @Before
    public void usersReinitializeDB(){
        db.deleteUsers();
    }

    @Test
    public void usersTestDB(){
        db.deleteUsers();

        Users users = db.importUsers();

        UserClass u1 = new UserClass(1, "ciao", "ciao", "shopmanager");
        db.addUser(u1);
        users.addUser(u1);

        assertNull(db.getUser(2));
        assertTrue(db.updateUserRights(1, "administrator"));
        assertTrue(db.removeUser(3));

        users.setNewId();

        assert(users.getNewId() == 1);

        UserClass u2 = new UserClass(users.getNewUserID(), "ciao2", "ciao2", "cashier");
        assertNotNull(u2);
        assert(u2.getId() == 2);
        assert(u2.getUsername() == "ciao2");
        assert(u2.getPassword() == "ciao2");
        assert(u2.getRole() == "cashier");

        db.addUser(u2);
        assert(u2.getId() == db.getUser(2).getId());
        assert(u2.getUsername().equals(db.getUser(2).getUsername()));
        assert(u2.getPassword().equals(db.getUser(2).getPassword()));
        assert(u2.getRole().equals((db.getUser(2).getRole())));

        Users users2 = db.importUsers();
        assert(users2.getUsers().size() != 0);

        assertTrue(db.removeUser(2));
        assertTrue(db.removeUser(1));
    }
}
