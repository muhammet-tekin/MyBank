package com.revature.dao;

import com.revature.console.Driver;
import com.revature.model.Account;
import com.revature.model.Credit;
import com.revature.model.Customer;
import com.revature.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class UserDAO {



    public boolean execute(String[] args, Customer loggedIn){
        return true;
    }

    public boolean isUsernameTaken(String username) {

        try {
            // 1. Create a Statement
            Connection c = ConnectionManager.getConnection();
            //Statement statement = c.createStatement();

            PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM customers WHERE username = ?");
            preparedStatement.setString(1, username);

            ResultSet results = preparedStatement.executeQuery();


            if (results.next())
                return true;

            return false;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    public int signUp(String username, String password) {

        int customerId;
        PreparedStatement stmt=null;
        try {
            Connection c = ConnectionManager.getConnection();
            stmt = c.prepareStatement("INSERT INTO customers(username, password) VALUES (?,?) ", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, password);
            int rowsAffected = stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys();) {
                if (generatedKeys.next()) {
                    customerId = generatedKeys.getInt(1);
                    Driver.logger.trace("New customer signed-up id: " + customerId);
                    stmt.close();
                    return customerId;
                }
                else {
                    c.rollback();
                    System.out.println("No id returned");
                    throw new SQLException("Customer could not be created! No ID returned.");
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public Customer logIn(String username, String password){
        Customer customer = getCustomer(username, password);
        if(customer== null) return null;
        return customer;
    }

    public Customer getCustomer(String username, String password){

        try {
            Connection c = ConnectionManager.getConnection();

            String customerRecord = "SELECT * FROM customers WHERE username = ? and password = ?";

            PreparedStatement preparedStatement = c.prepareStatement(customerRecord);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                return null;
            }
            else {
                int id = results.getInt("id");
                return new Customer(username, password, id);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
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


    public Boolean isAccountCancelled(int accountId){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("SELECT isCancelled FROM accounts WHERE id = ?");
            preparedStatement.setInt(1, accountId);

            // 2. Run a query using the statement to find any records with the given username
            //ResultSet results = statement.executeQuery("SELECT * FROM users WHERE username = \'" + username + "\'");
            ResultSet results = preparedStatement.executeQuery();
            results.next();
            return results.getBoolean("isCancelled");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Boolean isAccountApproved(int accountId){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("SELECT isApproved FROM accounts WHERE id = ?");
            preparedStatement.setInt(1, accountId);

            // 2. Run a query using the statement to find any records with the given username
            //ResultSet results = statement.executeQuery("SELECT * FROM users WHERE username = \'" + username + "\'");
            ResultSet results = preparedStatement.executeQuery();
            results.next();
            return results.getBoolean("isApproved");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public int cancelAccount(int accountId, boolean isCancelled){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE accounts SET isCancelled = ? WHERE id = ?");
            preparedStatement.setBoolean(1, isCancelled);
            preparedStatement.setInt(2, accountId);

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 1) Driver.logger.trace("Account is cancelled id: " + accountId);
            return rowsAffected;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int approveAccount(int accountId, boolean isApproved){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE accounts SET isApproved = ? WHERE id = ?");

            preparedStatement.setBoolean(1, isApproved);
            preparedStatement.setInt(2, accountId);

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 1) Driver.logger.trace("Account is approved id: " + accountId);
            return rowsAffected;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }


    public void showAccountsToBeApproved(){

        try {
            Connection c = ConnectionManager.getConnection();

            String listOfAccountsToBeApproved = "SELECT a.id, c.username " +
                           "FROM accounts as a JOIN " +
                           "account_owners ao ON a.id = ao.accountId JOIN " +
                           "customers c ON ao.ownerId = c.id " +
                           "WHERE a.isApproved = FALSE; ";

            PreparedStatement preparedStatement = c.prepareStatement(listOfAccountsToBeApproved);


            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                System.out.println("No account to be approved!");
            }
            else {
                System.out.println("****** Accounts to be approved *******");
                do {
                    int id = results.getInt("id");
                    String username = results.getString("username");
                    System.out.println("Account_id :" + id + "     Customer : " +username);
                } while (results.next());
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public Employee isEmployeeOrAdmin(String username, String password){


        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM employee WHERE " +
                                                                            "username = ? and password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) return null ; //neither employee nor admin
            else{
                int id = results.getInt("id") ;
                boolean isAdmin = results.getBoolean("isAdmin");
                return new Employee(username, password, id, isAdmin );
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public void showCreditsToBeApproved(){

        try {
            Connection c = ConnectionManager.getConnection();

            String listOfCreditsToBeApproved = "SELECT cr.id as creditId,  cu.id as debtorId, cu.username, cr. amount FROM credits as cr " +
                                                "JOIN customers cu " +
                                                "ON cr.debtorId = cu.id" +
                                                " WHERE cr.isApproved = FALSE;";

            PreparedStatement preparedStatement = c.prepareStatement(listOfCreditsToBeApproved);


            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                System.out.println("No credits to be approved!");
            }
            else {
                System.out.println("****** Credits to be approved *******");
                do {
                    int creditId = results.getInt("creditId");
                    int debtorId = results.getInt("debtorId");
                    String username = results.getString("username");
                    double amount = results.getDouble("amount");
                    System.out.println("Credit_id :" + creditId + "     Customer : " +username
                                        + "(ID:" +debtorId +")" +"  Amount : $" + amount);
                } while (results.next());
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Credit createCredit(int debtorId, double amount) {

        int creditId;
        PreparedStatement stmt=null;
        try {
            Connection c = ConnectionManager.getConnection();
            stmt = c.prepareStatement("INSERT INTO credits(debtorId, amount) VALUES (?, ?) ", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, debtorId);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys();) {
                if (generatedKeys.next()) {
                    creditId = generatedKeys.getInt(1);
                    Driver.logger.trace("User (ID: " + debtorId + ") applied for a credit of amount $" + amount);
                    stmt.close();
                    return new Credit(creditId, amount, debtorId, false);
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
        return null;
    }


    public ArrayList<Account> getCustomerAccounts(int customerId){

        ArrayList<Account> listAccounts = new ArrayList<>();

        try {
            Connection c = ConnectionManager.getConnection();

            String customerAccounts = "SELECT ac.id as id, balance, isApproved, isCancelled " +
                    "FROM accounts as ac JOIN " +
                    "account_owners as ao ON ac.id = ao.accountId " +
                    "WHERE ao.ownerId = ?;";



            PreparedStatement preparedStatement = c.prepareStatement(customerAccounts);
            preparedStatement.setInt(1, customerId);
            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                System.out.println("Customer has no account yet!");
            }
            else {
                System.out.println("****** Accounts of the customer *******");
                do {
                    int accId = results.getInt("id");
                    double balance = results.getDouble("balance");
                    boolean isApproved = results.getBoolean("isApproved");
                    boolean isCancelled = results.getBoolean("isCancelled");
                    listAccounts.add(new Account(accId, balance, isApproved, isCancelled));

                } while (results.next());

                return listAccounts;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
