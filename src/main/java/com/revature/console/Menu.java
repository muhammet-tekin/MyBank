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

        System.out.println("------------------------ WELCOME TO MY BANK!---------------------------");
        System.out.println("--------------------- Please type an option below----------------------");
        System.out.println("---------------------- login , signup , or exit------------------------");
        System.out.print(" >>> " );

        try {

            String input = scanner.nextLine().trim();
            switch (input) {
                case "login":
                    System.out.println("User interface you will see depends on the role you have: Customer, Employee or Admin!");
                    System.out.println("To login as employee (username: employee pass: 123)" +
                                         " or as admin (username: admin pass: 123)" +
                                            " or as a customer (username: muhammet pass: 123)");
                    System.out.println("You can also signup from the main menu as a customer. Unsuccessful login attempt will lead you back in the main menu.");

                    logIn();
                    break;
                case "signup":
                    signUp();
                    break;
                case "exit":
                    System.out.println("                                                ,,,\n" +
                                       "                  Good bye...                  (o o)\n" +
                                       "-------------------------------------------oOO--( )--OOo----");
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
            dao.execute(args, loggedIn, role);
            args = getCommand(role);
        }

    }





    public String[] getCommand(String role) {


        HashMap<String, String> menus = new HashMap<>();

        menus.put("Customer",
                        "\nTo view your accounts              : accounts " +
                        "\nTo withdraw money from your account: withdraw \033[3mamount\033[0m \033[3maccount_id\033[0m " +
                        "\nTo transfer money                  : transfer \033[3mamount\033[0m \033[3msender_account_id\033[0m \033[3mreceiver_account_id\033[0m " +
                        "\nTo deposit in your account         : deposit \033[3mamount\033[0m \033[3maccount_id\033[0m" +
                        "\nTo apply for a personal account    : apply " +
                        "\nTo apply for a joint account       : joint \033[3mother_owner_id\033[0m" +
                        "\nTo exit                            : exit");

        menus.put("Employee",
                        "\nTo view customer information       : customers " +
                        "\nTo view account information        : accounts" +
                        "\nTo view account applications       : applications" +
                        "\nTo approve/reject an account       : (approve | reject) \033[3maccount_id\033[0m" +
                        "\nTo exit                            : exit");


        menus.put("Admin",
                        "\nTo view customer information       : customers " +
                        "\nTo view account information        : accounts " +
                        "\nTo view account applications       : applications" +
                        "\nTo approve/reject an account       : (approve | reject) \033[3maccount_id\033[0m" +
                        "\nTo withdraw money from an account  : withdraw \033[3mamount\033[0m \033[3maccount_id\033[0m" +
                        "\nTo transfer                        : transfer \033[3mamount\033[0m \033[3msender_account_id\033[0m \033[3mreceiver_account_id\033[0m " +
                        "\nTo deposit in an account           : deposit \033[3mamount\033[0m \033[3maccount_id\033[0m" +
                        "\nTo cancel an account               : (cancel | no-cancel) \033[3maccount_id\033[0m" +
                        "\nTo exit                            : exit");


        String[] args;
        do {
            System.out.print("\n(enter command, type help if needed) >>>  ");
            String command = scanner.nextLine();
            args = command.split("\\s");
            if(args[0].equals("help")){
                System.out.println(menus.get(role));
            }
        } while(!isValid(args, role));
        return args;
    }


    public boolean isValid(String[] args, String role){
        if(args[0].equals("help")) return false;

        HashSet<String> adminCommands = new HashSet<>(Arrays.asList(
                "transfer", "deposit", "withdraw", "exit", "cancel", "no-cancel","reject",
                "approve", "customers", "accounts", "applications"));

        HashSet<String> employeeCommands = new HashSet<>(Arrays.asList(
                "exit", "reject", "approve", "customers", "accounts", "applications"));

        HashSet<String> customerCommands = new HashSet<>(Arrays.asList(
                "accounts", "transfer", "deposit", "withdraw", "exit", "apply", "joint"));


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