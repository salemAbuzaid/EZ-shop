package it.polito.ezshop.jUnitTests;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestEZShop_All {

    @Test
    public void MainTest(){

        TestEZShop_Orders_WB test_orders = new TestEZShop_Orders_WB();
        TestEZShop_Products_WB test_products = new TestEZShop_Products_WB();
        TestEZShop_Validator_BB test_validator = new TestEZShop_Validator_BB();
        TestEZShop_AccountBook_WB test_accounting= new TestEZShop_AccountBook_WB();
        TestEZShop_Users_WB test_users = new TestEZShop_Users_WB();
        TestEZShop_SaleTransactions_WB test_saletransactions = new TestEZShop_SaleTransactions_WB();
        TestEZShop_ReturnTransactions_WB test_returntransactions = new TestEZShop_ReturnTransactions_WB();
        TestEZShop_Customers_WB test_customers = new TestEZShop_Customers_WB();


        test_orders.ordersTestStatic();
        test_orders.ordersReinitializeDB();
        test_orders.ordersTestDB();

        test_products.productsTestStatic();
        test_products.productsReinitializeDB();
        test_products.productsTestDB();

        test_validator.TestBCFalse();
        test_validator.TestBCTrue();
        test_validator.TestCCFalse();
        test_validator.TestCCTrue();
        test_validator.validLoyaltyCard();
        test_validator.invalidLoyaltyCard();

        test_accounting.accountingTestStatic();
        test_accounting.accountingTestDB();

        test_users.usersTestStatic();
        test_users.usersTestDB();

        test_saletransactions.testSales();

        test_returntransactions.testReturns();

        test_customers.staticCustomerTest();
        test_customers.databaseCustomerTest();


    }
}
