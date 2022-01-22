package com.revature.controllers;

import com.revature.dao.ConnectionManager;
import com.revature.dao.DAO;
import com.revature.model.Customer;
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
        app.delete("/customers/{username}", deleteCustomer);
    }


    public Handler getCustomerByUsername = ctx -> {
        Customer c = dao.findCustomerByUsername(ctx.pathParam("username"));
        if(c != null){
            ctx.json(c);
            ctx.status(200);
        } else ctx.status(204);

    };

    public Handler createNewCustomer = ctx -> {
        // This line deserializes a JSON object from the body and creates a Java object out of it
        try{
            Customer c = ctx.bodyAsClass(Customer.class);
            dao.createCustomer(ctx.pathParam("username"), ctx.pathParam("username"));
            ctx.status(200);
        } catch(Exception e){
            ctx.status(400);
            e.printStackTrace();
        }
    };



    public Handler deleteCustomer = ctx -> {
        try{
            Customer c = ctx.bodyAsClass(Customer.class);
            if (dao.deleteCustomer(c.getId())==1){
                ctx.status(204);
            }
            // Status code 400 means "Error occurred"
            else ctx.status(400);
        } catch (Exception e){
            e.printStackTrace();
        }
    };

}
