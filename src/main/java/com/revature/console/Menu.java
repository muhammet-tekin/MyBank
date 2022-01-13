package com.revature.console;

import java.util.Scanner;

import com.revature.dao.UserDAO;

public class Menu {

    public boolean mainMenu(Scanner scanner) {
        System.out.println("Welcome to MyBanK! Please choose an option below: ");
        System.out.println("   1 - Log in");
        System.out.println("   2 - Sign up");
        System.out.println("   3 - Exit");

        try {
            // If we do scanner.nextInt, nextDouble, or some other primitive,
            // nextLine() is needed to flush the scanner of extra characters
            String input = scanner.nextLine().trim();


            switch (input) {
                case "1":
                    // Do log in stuff
                    break;
                case "2":
                    signUp(scanner);
                    break;
                case "3":
                    return false;
                default:
                    System.out.println("Invalid input: " + input);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void signUp(Scanner scanner) {
        System.out.println("To sign up, we'll need a username and password.");
        System.out.print("Username: ");

        // Because we are consuming the whole line, the scanner does not need to be flushed
        String username = scanner.nextLine();

        UserDAO userDao = new UserDAO();
        if (userDao.isUsernameTaken(username)) {
            System.out.println("Username taken! Returning to main menu...");
            return;
        }

        System.out.print("Password (cover your screen):");

        String password = scanner.nextLine();

        userDao.signUp(username, password);
    }
}