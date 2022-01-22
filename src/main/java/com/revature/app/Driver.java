package com.revature.app;


import com.revature.controllers.AccountController;
import com.revature.controllers.CustomerController;
import com.revature.controllers.EmployeeController;
import com.revature.dao.ConnectionManager;
import io.javalin.Javalin;
import java.sql.Connection;



public class Driver {

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7070);
        Connection conn = ConnectionManager.getConnection();
        AccountController accountController = new AccountController(app);
        EmployeeController employeeController = new EmployeeController(app);
        CustomerController customerController = new CustomerController(app);

    }
}
