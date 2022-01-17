package com.revature.console;

import com.revature.dao.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Scanner;

public class Driver {

    public static final Logger logger = LogManager.getLogger(Driver.class);

    public static void main(String[] args) {


        try {
            Scanner scanner = new Scanner(System.in);
            boolean stay = true;
            while (stay) {
                Menu menu = new Menu(scanner);
                stay = menu.mainMenu();
            }
        } finally {
            // When the program is stopped, this will trigger and close the connection
            // You have to use the stop button in your IDE. Similar to finalize in ConnectionManager
            try {
                ConnectionManager.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
