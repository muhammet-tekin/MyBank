package com.revature.dao;

import com.revature.model.Account;
import com.revature.model.Credit;
import com.revature.model.Customer;
import com.revature.model.Employee;
import org.junit.Test;

import java.util.ArrayList;


import static org.junit.Assert.*;

public class DAOTest {

    DAO dao = new DAO();

    @Test
    public void depositTest() {
        assertEquals(1, dao.deposit(1, 100.0));
    }


    @Test
    public void getBalanceTest() {
        Double balance = dao.getBalance(1);
        assertTrue(dao.getBalance(1) instanceof Double);
        //System.out.println(balance);
    }

    @Test
    public void withdrawTest() {
        double balance = dao.getBalance(1);
        int returnedValue = dao.withdraw(1, 100.0);
        if( balance >= 100.0){
            assertEquals(1, returnedValue);
        } else {
            assertEquals(-1, returnedValue);
        }
    }

    @Test
    public void createAndDeleteAccountTest(){
        int id = dao.createAccount();
        assertEquals(1, dao.deleteAccount(id));
    }

    @Test
    public void createAndDeleteCustomerTest(){
        int id = dao.createCustomer("TestUser", "pass");
        assertEquals(1, dao.deleteCustomer(id));
    }

    @Test
    public void transferTest(){
        int accOneId = dao.createAccount();
        int accTwoId = dao.createAccount();
        dao.deposit(accOneId, 100.0);
        dao.transfer(accOneId,accTwoId, dao.getBalance(accOneId));
        assertEquals(dao.getBalance(accTwoId), 100, .001);
        dao.deleteAccount(accOneId);
        dao.deleteAccount(accTwoId);
    }

    @Test
    public void isAccountCancelledTest() {
        dao.cancelAccount(1,false);
        assertFalse(dao.isAccountCancelled(1));
    }

    @Test
    public void isAccountApprovedTest() {
        dao.approveAccount(1, true);
        assertTrue(dao.isAccountApproved(1));
    }

    @Test
    public void cancelAccountTest() {
        dao.cancelAccount(1, true);
        assertTrue(dao.isAccountCancelled(1));
    }

    @Test
    public void approveAccountTest() {
        dao.approveAccount(1, true);
        assertTrue(dao.isAccountApproved(1));
    }

    @Test
    public void showAccountsToBeApproved() {
        dao.showAccountsToBeApproved();
    }

    @Test
    public void isEmployeeOrAdmin() {
        assertTrue(dao.isEmployeeOrAdmin("employee", "123") instanceof Employee);
        assertTrue(dao.isEmployeeOrAdmin("admin", "123").isAdmin());
        assertNull(dao.isEmployeeOrAdmin("anyone else", "123"));
    }


    @Test
    public void getCustomerAccounts() {
        ArrayList<Account> listAccounts = dao.getCustomerAccounts(1, false);
        for(Account a:listAccounts)
            System.out.println(a.toString());
    }


    @Test
    public void getCustomerTest() {
        assertTrue(dao.getCustomer("muhammet", "123") instanceof Customer);
    }

    @Test
    public void executeTest() {
        dao.execute(new String[]{"cancel", "1"}, new Customer("admin", "123", 1), "Admin");
        assertTrue(dao.isAccountCancelled(1));
        dao.cancelAccount(1, false);
    }

    @Test
    public void getAllAccountsTest() {
        dao.getAllAccounts();
    }

    @Test
    public void setAccountTest() {
        int id =  dao.createAccount();
        Account account = new Account(id, 100., true, false);
        if(dao.setAccount(account) == 1){
            assertEquals(100.0, dao.getBalance(id), .001);
            dao.deleteAccount(id);
        }
    }
}