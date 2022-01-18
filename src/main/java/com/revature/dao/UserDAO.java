package com.revature.dao;

import com.revature.console.Driver;
import com.revature.model.Account;
import com.revature.model.Credit;
import com.revature.model.Customer;
import com.revature.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UserDAO {



    public boolean execute(String[] args, Customer loggedIn, String role){

        Double amount;
        int accountId;
        int secondAccountId;
        try {
            switch (args[0]) {
                case "deposit":
                    amount = Double.valueOf(args[1]);
                    accountId = Integer.parseInt(args[2]);
                    if(role.equals("Customer")){
                        if(!checkOwnership(loggedIn.getId(), accountId)) return false;
                    }

                    if (deposit(accountId, amount) == 1 && isAccountEligible(accountId)) {
                        Driver.logger.trace(loggedIn.getUsername() + " deposited  $" + amount + " in account id =" + accountId + ".");
                        System.out.println("You deposited  $" + amount + " in account " + accountId + ".");
                    }
                    break;

                case "withdraw":
                    amount = Double.valueOf(args[1]);
                    accountId = Integer.parseInt(args[2]);
                    if(role.equals("Customer")){
                        if(!checkOwnership(loggedIn.getId(), accountId)) return false;
                    }
                    if (withdraw(accountId, amount) == 1 && isAccountEligible(accountId)) {
                        Driver.logger.trace(loggedIn.getUsername() + " withdrew  $" + amount + " from account id =" + accountId + ".");
                        System.out.println("You withdrew $" + amount + " from account id =" + accountId + ".");
                    }
                    break;
                case "transfer":
                    amount = Double.valueOf(args[1]);
                    accountId = Integer.parseInt(args[2]);
                    if(role.equals("Customer")){
                        if(!checkOwnership(loggedIn.getId(), accountId)) return false;
                    }
                    secondAccountId = Integer.parseInt(args[3]);

                    if (isAccountEligible(accountId) && isAccountEligible(secondAccountId) && withdraw(accountId, amount) == 1) {
                        if (deposit(secondAccountId, amount) == 1) {
                            Driver.logger.trace(loggedIn.getUsername() + " transferred  $" + amount + " from account id =" +
                                    accountId + " to the account " + secondAccountId + ".");
                            System.out.println("You transferred  $" + amount + " from account " +
                                    accountId + " to the account " + secondAccountId + ".");
                        } else {
                            //could not deposit to the receiver, so give money back to the sender
                            deposit(accountId, amount);
                        }
                    }
                    break;

                case "cancel":
                    accountId = Integer.parseInt(args[1]);
                    if (cancelAccount(accountId, true) == 1) {
                        Driver.logger.trace(loggedIn.getUsername() + " cancelled account with id :" + accountId + ".");
                        System.out.println("You cancelled account (id: " + accountId + ").");
                    }
                    ;
                    break;

                case "no-cancel":
                    accountId = Integer.parseInt(args[1]);
                    if (cancelAccount(accountId, false) == 1) {
                        Driver.logger.trace(loggedIn.getUsername() + " un-cancelled account with id :" + accountId + ".");
                        System.out.println("You UN-cancelled account (id: " + accountId + ").");
                    }
                    ;
                    break;

                case "approve":
                    accountId = Integer.parseInt(args[1]);
                    if (approveAccount(accountId, true) == 1) {
                        Driver.logger.trace(loggedIn.getUsername() + " approved account with id :" + accountId + ".");
                        System.out.println("You approved account (id: " + accountId + ").");
                    } else {
                        System.out.println("No such an account");
                    }
                    ;
                    break;

                case "reject":
                    accountId = Integer.parseInt(args[1]);
                    if (approveAccount(accountId, false) == 1) {
                        Driver.logger.trace(loggedIn.getUsername() + " rejected the application for the account id :" + accountId + ".");
                        System.out.println("You rejected the application for the account (id: " + accountId + ").");
                    } else {
                        System.out.println("No such an account");
                    }
                    break;

                case "apply":
                    int createdAccountId = createAccount();
                    if (createdAccountId != -1) {
                        assignOwner(createdAccountId, loggedIn.getId());
                        Driver.logger.trace(loggedIn.getUsername() + " applied for an account, the id of created account is " +
                                createdAccountId);
                        System.out.println("You applied for the account id " + createdAccountId +
                                ". You will be able to use it once it is approved");
                    }
                    break;

                case "joint":

                    int jointAccountId = createAccount();
                    int otherOwnerId = Integer.parseInt(args[1]);
                    if(doesCustomerExist(otherOwnerId)){
                        if (jointAccountId != -1) {
                            assignOwner(jointAccountId, loggedIn.getId());
                            assignOwner(jointAccountId, otherOwnerId);
                            Driver.logger.trace(loggedIn.getUsername() + " applied for a joint account with the customer with id " +
                                    otherOwnerId + ", the id of created account is " +
                                    jointAccountId);
                            System.out.println("You applied for a joint account with the customer with id " +
                                    otherOwnerId + ", the id of created account is " +
                                    jointAccountId);
                        }
                    }
                    break;

                case "accounts":
                    if (role.equals("Customer"))
                        getCustomerAccounts(loggedIn.getId(), true);
                    else {
                        getAllAccounts();
                    }
                    break;

                case "applications":
                    showAccountsToBeApproved();
                    break;

                case "customers":
                    getAllCustomers(true);
                    break;

            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
            System.out.println("No valid input");
        }
        return true;
    }


    public boolean checkOwnership(int customerId, int accountId){

            HashSet<Integer> ids = new HashSet<>();
            ArrayList<Account> customerAccounts = getCustomerAccounts(customerId, false);
            if(customerAccounts == null) return false;
            for(Account a : customerAccounts){
                ids.add(a.getId());
            }
            if(ids.contains(accountId)) {
                return true;
            } else{
                System.out.println("You do not have such an account!!".toUpperCase(Locale.ROOT));
                return false;
            }
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

        if(amount <= 0){
            System.out.println("!!!Amount should be positive!!!".toUpperCase(Locale.ROOT));
            return -1;
        }

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2,accountId);


            // 2. Run a query using the statement to find any records with the given username
            //ResultSet results = statement.executeQuery("SELECT * FROM users WHERE username = \'" + username + "\'");

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected != 1) System.out.println("!!!!ACCOUNT DOES NOT EXIST!!!");
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

            ResultSet results = preparedStatement.executeQuery();
            if(results.next()){
                boolean isApproved = results.getBoolean("isApproved");
                if(!isApproved) System.out.println("Account: " + accountId +" is not yet approved".toUpperCase(Locale.ROOT));
                return isApproved;
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    public Boolean isAccountEligible(int accountId){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("SELECT isApproved, isCancelled FROM accounts WHERE id = ?");
            preparedStatement.setInt(1, accountId);

            ResultSet results = preparedStatement.executeQuery();
            if(results.next()){
                boolean isApproved = results.getBoolean("isApproved");
                boolean isCancelled = results.getBoolean("isCancelled");
                if(!isApproved) System.out.println("Account: " + accountId +" is not yet approved".toUpperCase(Locale.ROOT));
                if(isCancelled) System.out.println("Account: " + accountId +" is cancelled".toUpperCase(Locale.ROOT));
                return isApproved && (!isCancelled);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    public int cancelAccount(int accountId, boolean isCancelled){

        try {
            Connection c = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE accounts SET isCancelled = ? WHERE id = ?");
            preparedStatement.setBoolean(1, isCancelled);
            preparedStatement.setInt(2, accountId);

            int rowsAffected = preparedStatement.executeUpdate();
            //if(rowsAffected == 1) Driver.logger.trace("Account is cancelled id: " + accountId);
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


    public ArrayList<Account> showAccountsToBeApproved(){
        ArrayList<Account> listAccounts = new ArrayList<>();

        try {
            Connection c = ConnectionManager.getConnection();

            String customerAccounts = "SELECT ac.id as id, balance, isApproved, isCancelled, cu.username as username " +
                    "FROM accounts as ac JOIN " +
                    "account_owners as ao ON ac.id = ao.accountId " +
                    "JOIN customers as cu ON ao.ownerId = cu.id " +
                    "WHERE ac.isApproved = FALSE " +
                    "ORDER BY ac.id";



            PreparedStatement preparedStatement = c.prepareStatement(customerAccounts);
            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                System.out.println("No account waiting to be approved!");
            }
            else {
                System.out.println("Account Id\t  Balance\tApproved?\tCancelled?\tOwner");
                do {
                    int accId = results.getInt("id");
                    double balance = results.getDouble("balance");
                    boolean isApproved = results.getBoolean("isApproved");
                    boolean isCancelled = results.getBoolean("isCancelled");
                    Account a = new Account(accId, balance, isApproved, isCancelled);


                    System.out.printf(" %9d %10.1f        %s          %s    %s%n",
                            a.getId(), a.getBalance(),
                            isApproved ? "\u001B[32m+\u001B[0m" : "\u001B[31m-\u001B[0m" ,
                            isCancelled? "\u001B[31mYES\u001B[0m" : "\u001B[32mNO \u001B[0m",
                            "\u001B[34m" + results.getString("username") + "\u001B[0m");

                    listAccounts.add(a);

                } while (results.next());

                return listAccounts;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
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


    public ArrayList<Account> getCustomerAccounts(int customerId, boolean print){

        ArrayList<Account> listAccounts = new ArrayList<>();

        try {
            Connection c = ConnectionManager.getConnection();

            String customerAccounts = "SELECT ac.id as id, balance, isApproved, isCancelled, cu.username as username " +
                    "FROM accounts as ac JOIN " +
                    "account_owners as ao ON ac.id = ao.accountId " +
                    "JOIN customers as cu ON ao.ownerId = cu.id " +
                    "WHERE cu.id = ? ORDER BY ac.id";



            PreparedStatement preparedStatement = c.prepareStatement(customerAccounts);
            preparedStatement.setInt(1, customerId);
            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                if(print) System.out.println("No accounts in database!");
            }
            else {
                if(print) System.out.println("Account Id\t  Balance\tApproved?\tCancelled?\tOwner");
                do {
                    int accId = results.getInt("id");
                    double balance = results.getDouble("balance");
                    boolean isApproved = results.getBoolean("isApproved");
                    boolean isCancelled = results.getBoolean("isCancelled");
                    Account a = new Account(accId, balance, isApproved, isCancelled);

                    if(print)
                    System.out.printf(" %9d %10.1f        %s          %s%n",
                            a.getId(), a.getBalance(),
                            isApproved ? "\u001B[32m+\u001B[0m" : "\u001B[31m-\u001B[0m" ,
                            isCancelled? "\u001B[31mYES\u001B[0m" : "\u001B[32mNO \u001B[0m",
                            "\u001B[34m" );

                    listAccounts.add(a);

                } while (results.next());

                return listAccounts;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Account> getAllAccounts(){

        ArrayList<Account> listAccounts = new ArrayList<>();

        try {
            Connection c = ConnectionManager.getConnection();

            String customerAccounts = "SELECT ac.id as id, balance, isApproved, isCancelled, cu.username as username " +
                    "FROM accounts as ac JOIN " +
                    "account_owners as ao ON ac.id = ao.accountId " +
                    "JOIN customers as cu ON ao.ownerId = cu.id ORDER BY ac.id";



            PreparedStatement preparedStatement = c.prepareStatement(customerAccounts);
            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                System.out.println("No accounts in database!");
            }
            else {
                System.out.println("Account Id\t  Balance\tApproved?\tCancelled?\tOwner");
                do {
                    int accId = results.getInt("id");
                    double balance = results.getDouble("balance");
                    boolean isApproved = results.getBoolean("isApproved");
                    boolean isCancelled = results.getBoolean("isCancelled");
                    Account a = new Account(accId, balance, isApproved, isCancelled);


                    System.out.printf(" %9d %10.1f        %s          %s      %s%n",
                            a.getId(), a.getBalance(),
                            isApproved ? "\u001B[32m+\u001B[0m" : "\u001B[31m-\u001B[0m" ,
                            isCancelled? "\u001B[31mYES\u001B[0m" : "\u001B[32mNO \u001B[0m",
                            "\u001B[34m" + results.getString("username") + "\u001B[0m");

                    listAccounts.add(a);

                } while (results.next());

                return listAccounts;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }



    public int assignOwner(int accountId, int ownerId){

//        if(checkOwnership(ownerId, accountId)){
//            System.out.println("Customer already owns this account");
//            return 1;
//        }



        PreparedStatement stmt=null;
        try {
            Connection c = ConnectionManager.getConnection();
            stmt = c.prepareStatement("INSERT INTO account_owners(accountId, ownerId) VALUES (?, ?)");
            stmt.setInt(1, accountId);
            stmt.setInt(2, ownerId);

            int rowsAffected = stmt.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public boolean doesCustomerExist(int customerId){

        HashSet<Integer> ids = new HashSet<>();
        ArrayList<Customer> customerList = getAllCustomers(false);
        if(customerList == null) return false;
        for(Customer a : customerList){
            ids.add(a.getId());
        }
        if(ids.contains(customerId)) {
            return true;
        } else{
            System.out.println("There is not such a customer in the bank!!".toUpperCase(Locale.ROOT));
            return false;
        }
    }


    public ArrayList<Customer> getAllCustomers(boolean print){

        ArrayList<Customer> listCustomers = new ArrayList<>();

        try {
            Connection c = ConnectionManager.getConnection();

            String customerAccounts = "SELECT * FROM customers ORDER BY id";

            PreparedStatement preparedStatement = c.prepareStatement(customerAccounts);
            ResultSet results = preparedStatement.executeQuery();


            if (!results.next()) {                            //if rs.next() returns false
                //then there are no rows.
                System.out.println("No customer in database!");
            }
            else {
                if(print) System.out.println("Customer Id\t  Username\t");
                do {
                    int customerId = results.getInt("id");
                    String username = results.getString("username");

                    Customer a = new Customer(username, "***", customerId);
                    if(print)
                        System.out.printf("  %9d     %s%n",a.getId(), "\u001B[34m" + results.getString("username") + "\u001B[0m");
                    listCustomers.add(a);

                } while (results.next());

                return listCustomers;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
