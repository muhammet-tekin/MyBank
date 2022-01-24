package com.revature.controllers;

import com.revature.dao.ConnectionManager;
import com.revature.dao.DAO;
import com.revature.dao.EmployeeDAO;
import com.revature.model.Customer;
import com.revature.model.Employee;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import java.sql.Connection;

public class CustomerController {


    Javalin app;
    Connection conn;
    DAO dao;

    public CustomerController(Javalin app) {
        this.app = app;
        this.conn = ConnectionManager.getConnection();
        this.dao = new DAO();

        app.post("/customers/", createNewCustomer);
        app.get("/customers/{username}", getCustomerByUsername);
        app.put("/customers/{username}", updateCustomer);
        app.delete("/customers/{username}", deleteCustomer);
    }

    public Handler updateCustomer = ctx -> {
        try{
            Customer customer = ctx.bodyAsClass(Customer.class);
            if (dao.setCustomer(customer)){
                // Status code 204 means "Successfully updated"
                ctx.result("Customer record is updated!");
                ctx.status(200);

            }
            // Status code 400 means "Error occurred"
            else ctx.status(400);
        } catch (Exception e){
            e.printStackTrace();
        }
    };


    public Handler getCustomerByUsername = ctx -> {
        Customer c = dao.findCustomerByUsername(ctx.pathParam("username"));
        if(c != null){
            ctx.json(c);
            ctx.status(200);
        } else ctx.status(400);

    };

    public Handler createNewCustomer = ctx -> {
        // This line deserializes a JSON object from the body and creates a Java object out of it
        try{
            Customer c = ctx.bodyAsClass(Customer.class);
            if(dao.isUsernameTaken(c.getUsername())) {
                ctx.result("Customer already exists!");
                return;
            }
            dao.createCustomer(c.getUsername(), c.getPassword());
            ctx.result("Customer added!!");
            ctx.status(200);
        } catch(Exception e){
            ctx.status(400);
            e.printStackTrace();
        }
    };



    public Handler deleteCustomer = ctx -> {
        try{
            if(!dao.isUsernameTaken(ctx.pathParam("username"))){
                ctx.result("No such a customer!");
                ctx.status(400);
                return;
            }

            if (dao.deleteCustomer(dao.findCustomerByUsername(ctx.pathParam("username")).getId()) == 1){
                ctx.result("Employee deleted!!"); // Status code 204 means "Successfully updated"
                ctx.status(200);
            }
            // Status code 400 means "Error occurred"
            else {
                ctx.result("Customer not found!!");
                ctx.status(400);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    };

}
