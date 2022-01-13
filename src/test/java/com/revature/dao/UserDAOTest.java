package com.revature.dao;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserDAOTest {

    UserDAO dao = new UserDAO();

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

}