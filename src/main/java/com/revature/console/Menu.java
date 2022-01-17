package com.revature.console;

import java.sql.SQLOutput;
import java.util.*;

import com.revature.dao.UserDAO;
import com.revature.model.Account;
import com.revature.model.Customer;
import com.revature.model.Employee;

public class Menu {
    Scanner scanner;
    UserDAO dao = new UserDAO();

    public Menu(Scanner scanner) {

        this.scanner = scanner;
    }

    public boolean mainMenu() {

        System.out.println("Welcome to MyBanK! Please choose an option below: ");
        System.out.println("   1 - Log in");
        System.out.println("   2 - Sign up");
        System.out.println("   3 - Exit");

        try {

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    logIn();
                    break;
                case "2":
                    signUp();
                    break;
                case "3":
                    return false;
                default:
                    System.out.println("Invalid input: " + input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void signUp() {
        System.out.println("To sign up, we'll need a username and password.");
        System.out.print("Username: ");

        // Because we are consuming the whole line, the scanner does not need to be flushed
        String username = scanner.nextLine();

        if (dao.isUsernameTaken(username)) {
            System.out.println("Username taken! Returning to main menu...");
            return;
        }

        System.out.print("Password (cover your screen):");

        String password = scanner.nextLine();
        int customer_id = dao.signUp(username, password);
        showCommandPanel(new Customer(username, password, customer_id));
    }


    public void logIn() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password (cover your screen):");
        String password = scanner.nextLine().trim();
        Employee loggedInEmployee = dao.isEmployeeOrAdmin(username, password);

        if(loggedInEmployee != null){
            showCommandPanel(loggedInEmployee);
        }
        else {
            Customer loggedIn = dao.logIn(username, password);
            if(loggedIn == null) System.out.println("User not found!");
            else{
                showCommandPanel(loggedIn);
            }
        }
    }

    public void showCommandPanel(Customer loggedIn){
        String role;
        if(loggedIn instanceof Employee){
            if(((Employee) loggedIn).isAdmin()){
                role = "Admin";
            } else { role = "Employee"; }
        }
        else {
            role = "Customer";
        }
        System.out.println("*** Welcome " + loggedIn.getUsername().toUpperCase() + " (You logged in as " + role +") ***");
        String[] args = getCommand(role);
        loop:
        while(true){
            if(args[0].equals("exit")) break loop;
            dao.execute(args, loggedIn);
        }

    }





    public String[] getCommand(String role) {


        HashMap<String, String> menus = new HashMap<>();

        menus.put("Customer",
                        "\nTo withdraw money from your account: withdraw [amount]  [accountId] " +
                        "\nTo transfer money                  : transfer [amount] [senderAccountId] [receiverAccountId] " +
                        "\nTo deposit in your account         : deposit [amount] [accountId]" +
                        "\nTo apply for a personal account    : apply " +
                        "\nTo apply for a joint account       : apply [otherOwnerId]" +
                        "\nTo exit                            : exit");

        menus.put("Employee",
                        "\nTo view customer information       : customers " +
                        "\nTo view account information        : accounts" +
                        "\nTo view account applications       : applications" +
                        "\nTo approve/reject an account       : [approve | reject] [account_id]" +
                        "\nTo exit                            : exit");


        menus.put("Admin",
                        "\nTo view customer information       : customers " +
                        "\nTo view account information        : accounts " +
                        "\nTo view account applications       : applications" +
                        "\nTo approve/reject an account       : [approve | reject] [account_id]" +
                        "\nTo withdraw money from an account  : withdraw [amount]  [accountId]" +
                        "\nTo transfer                        : transfer [amount] [senderAccountId] [receiverAccountId] " +
                        "\nTo deposit in an account           : deposit [amount] [accountId]" +
                        "\nTo cancel an account               : cancel [accountId] " +
                        "\nTo exit                            : exit");


        String[] args;
        do {
            System.out.println(menus.get(role));
            String command = scanner.nextLine();
            args = command.split("\\s");
        } while(!isValid(args, role));
        return args;
    }


    public boolean isValid(String[] args, String role){

        HashSet<String> adminCommands = new HashSet<>(Arrays.asList(
                "transfer", "deposit", "withdraw", "exit", "cancel", "reject",
                "approve", "customers", "accounts"));

        HashSet<String> employeeCommands = new HashSet<>(Arrays.asList(
                "exit", "reject", "approve", "customers", "accounts"));

        HashSet<String> customerCommands = new HashSet<>(Arrays.asList(
                "transfer", "deposit", "withdraw", "exit", "apply"));


        HashMap<String, HashSet> commandSets = new HashMap<>();
        commandSets.put("Customer", customerCommands);
        commandSets.put("Employee", employeeCommands);
        commandSets.put("Admin", adminCommands);

        if(args.length>4 || !commandSets.get(role).contains(args[0])) {
            System.out.println("!!!!  Command could not be recognized !!!!!!\n");
            return false;
        }

        return true;
    }
}