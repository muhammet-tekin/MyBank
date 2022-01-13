package com.revature.dao;

import com.revature.console.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public boolean isUsernameTaken(String username) {

        try {
            // 1. Create a Statement
            Connection c = ConnectionManager.getConnection();
            //Statement statement = c.createStatement();

            PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM customers WHERE username = ?");
            preparedStatement.setString(1, username);

            // 2. Run a query using the statement to find any records with the given username
            //ResultSet results = statement.executeQuery("SELECT * FROM users WHERE username = \'" + username + "\'");
            ResultSet results = preparedStatement.executeQuery();

            // 3. If there is more than 0 records, we'll return false. Otherwise true.
			/*int size = results.getFetchSize();
			if (size > 0)
				// The username IS taken = true
				return true;*/
            if (results.next())
                return true;

            return false;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    public void signUp(String username, String password) {

        try {
            Connection c = ConnectionManager.getConnection();
            Statement statement = c.createStatement();
            int rowsAffected = statement.executeUpdate("INSERT INTO customers(username, password) VALUES (\'" + username
                    + "\', \'" + password + "\')");


            Driver.logger.trace("New user signed up: " + username);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


   public int deposit(int accountId, double amount){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2,accountId);


            // 2. Run a query using the statement to find any records with the given username
            //ResultSet results = statement.executeQuery("SELECT * FROM users WHERE username = \'" + username + "\'");

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 1) Driver.logger.trace("Deposit into account " + accountId + " amount: $" + amount);
            return rowsAffected;



        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }

    public int withdraw(int accountId, double amount){
        double balance = getBalance(accountId);
        if( balance < amount){
            System.out.println("Insufficient Balance!!" + "You have only $" + balance);
            return -1;
        }

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2,accountId);


            // 2. Run a query using the statement to find any records with the given username
            //ResultSet results = statement.executeQuery("SELECT * FROM users WHERE username = \'" + username + "\'");

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 1) Driver.logger.trace("Withdrawn from account " + accountId + " amount: $" + amount);
            return rowsAffected;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }


    public int transfer(int fromAccountId, int toAccountId, double amount){
        if(withdraw(fromAccountId, amount) == -1){
            return -1;
        }
        deposit(toAccountId, amount);
        return 1;
    }




    public Double getBalance(int accountId){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
            preparedStatement.setInt(1, accountId);

            // 2. Run a query using the statement to find any records with the given username
            //ResultSet results = statement.executeQuery("SELECT * FROM users WHERE username = \'" + username + "\'");
            ResultSet results = preparedStatement.executeQuery();
            results.next();
            return results.getDouble("balance");



        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public int createAccount() {

        int accountId;
        PreparedStatement stmt=null;
        try {
            Connection c = ConnectionManager.getConnection();
            stmt = c.prepareStatement("INSERT INTO accounts(balance) VALUES (DEFAULT) ", Statement.RETURN_GENERATED_KEYS);
            int rowsAffected = stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys();) {
                if (generatedKeys.next()) {
                    accountId = generatedKeys.getInt(1);
                    Driver.logger.trace("New account created id: " + accountId);
                    stmt.close();
                    return accountId;
                }
                else {
                    c.rollback();
                    System.out.println("No id returned");
                    throw new SQLException("Account could not be created! No ID returned.");
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }


    public int createCustomer(String username, String password) {

        int customerId;
        PreparedStatement stmt=null;
        try {
            Connection c = ConnectionManager.getConnection();
            stmt = c.prepareStatement("INSERT INTO customers(username, password) VALUES (?,?) ", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1,username);
            stmt.setString(2, password);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customerId = generatedKeys.getInt(1);
                    Driver.logger.trace("New customer added id: " + customerId);
                    stmt.close();
                    return customerId;
                }
                else {
                    c.rollback();
                    System.out.println("No id returned");
                    throw new SQLException("Account could not be created! No ID returned.");
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int deleteCustomer(int id) {

        try {
            Connection c = ConnectionManager.getConnection();
            Statement statement = c.createStatement();
            int rowsAffected = statement.executeUpdate("DELETE FROM customers WHERE id = " + id + ";");
            Driver.logger.trace("User with id: " + id + " is deleted.");
            return rowsAffected;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int deleteAccount(int id) {

        try {
            Connection c = ConnectionManager.getConnection();
            Statement statement = c.createStatement();
            int rowsAffected = statement.executeUpdate("DELETE FROM accounts WHERE id = " + id + ";");
            Driver.logger.trace("Account with id: " + id + " is deleted.");
            return rowsAffected;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }


    public void showAccountInfo(){

    }



}
