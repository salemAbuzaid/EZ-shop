package it.polito.ezshop.jUnitTests;

import it.polito.ezshop.data.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;


public class TestEZShop_AccountBook_WB {
    AccountBook accountbook;
    AccountBook accountbook2;
    DBClass db = new DBClass();


    @Test
    public void accountingTestStatic() {
        accountbook = new AccountBook();
        TreeMap<Integer, BalanceOperationClass> balances = new TreeMap<>();

        assertNotNull(accountbook);
        assert(accountbook.getCurrentBalance() == 0.0);
        accountbook.setCurrentBalance(0.0);
        assertNotNull(accountbook.getBalanceOperationList());
        assertNull(accountbook.getListBalanceOperationsByDate(LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        assert(accountbook.getNumBalanceOperations() == 0);
        accountbook.setNewId();
        assert(accountbook.getID() == 0);

        BalanceOperationClass b1 = new BalanceOperationClass();
        assert(b1.getBalanceId() == -1);
        assertNull(b1.getDate());
        assert(b1.getMoney() == 0.0);
        assert(b1.getType().equals(""));

        b1.setBalanceId(accountbook.getNewId());
        assert(b1.getBalanceId() == 1);
        b1.setDate(LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertNotNull(b1.getDate());
        b1.setMoney(10.1);
        assert(b1.getMoney() == 10.1);
        b1.setType("credit");
        assert(b1.getType().equals("credit"));

        accountbook.addBalanceOperation(accountbook.getID(), b1);
        assert(!accountbook.getBalanceOperationList().isEmpty());
        assert(accountbook.getCurrentBalance() != 0.0);
        assert(accountbook.getNumBalanceOperations() == 1);
        accountbook.setNewId();
        assert(accountbook.getID() == 1);

        BalanceOperationClass b2 = new BalanceOperationClass(1, LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")), 2.8, "credit");
        BalanceOperationClass b3 = new BalanceOperationClass(2, LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")), -1.0, "debit");
        balances.put(1,b1);
        balances.put(2,b2);

        accountbook2 = new AccountBook(0.0, balances);
        assertNotNull(accountbook2);
        accountbook2.updateCurrentBalance("credit", 2.8);
        assert(accountbook2.getCurrentBalance() == 2.8);
        accountbook2.updateCurrentBalance("debit", 1.8);
        assert(accountbook2.getCurrentBalance() != 2.8);
        assertNotNull(accountbook2.getListBalanceOperationsByDate(LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        assertNull(accountbook2.getListBalanceOperationsByDate(LocalDate.parse("02/02/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

    }

    @Before
    public void accountingReinitializeDB(){
        db.deleteAccounting();
    }

    @Test
    public void accountingTestDB(){
        db.deleteAccounting();

        AccountBook accountBook = db.importAccounting();

        BalanceOperationClass b2 = new BalanceOperationClass(1, LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")), 2.8, "credit");
        BalanceOperationClass b3 = new BalanceOperationClass(2, LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")), -1.0, "debit");

        db.addBalanceOperation(b2);
        assertNotNull(db.getBalanceOperation(1));
        assertNull(db.getBalanceOperation(3));

    }
}
