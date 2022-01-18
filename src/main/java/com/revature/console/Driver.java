package com.revature.console;

import com.revature.dao.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Scanner;

public class Driver {

    public static final Logger logger = LogManager.getLogger(Driver.class);

    public static void main(String[] args) {

        printLogo();
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

    private static void printLogo(){
        String logo = "                                          ___..--'  .`.\n" +
                "                                    ___...--'     -  .` `.`.\n" +
                "                           ___...--' _      -  _   .` -   `.`.\n" +
                "                  ___...--'  -       _   -       .`  `. - _ `.`.\n" +
                "           __..--'_______________ -         _  .`  _   `.   - `.`.\n" +
                "        .`    _ /\\    -        .`      _     .`__________`. _  -`.`.\n" +
                "      .` -   _ /  \\_     -   .`  _         .` |  MY BANK  |`.   - `.`.\n" +
                "    .`-    _  /   /\\   -   .`        _   .`   |___________|  `. _   `.`.\n" +
                "  .`________ /__ /_ \\____.`____________.`     ___       ___  - `._____`|\n" +
                "    |   -  __  -|    | - |  ____  |   | | _  |   |  _  |   |  _ |\n" +
                "    | _   |  |  | -  |   | |.--.| |___| |    |___|     |___|    |\n" +
                "    |     |--|  |    | _ | |'--'| |---| |   _|---|     |---|_   |\n" +
                "    |   - |__| _|  - |   | |.--.| |   | |    |   |_  _ |   |    |\n" +
                " ---``--._      |    |   |=|'--'|=|___|=|====|___|=====|___|====|\n" +
                "                                             (source: ASCII art archive)";
        System.out.println(logo);
    }
}
