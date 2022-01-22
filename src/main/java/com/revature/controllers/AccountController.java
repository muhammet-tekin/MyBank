package com.revature.controllers;

import com.revature.dao.ConnectionManager;
import com.revature.dao.DAO;
import com.revature.model.Account;
import io.javalin.Javalin;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.util.ArrayList;

public class AccountController {

    Javalin app;
    Connection conn;
    DAO dao;

    public AccountController(Javalin app) {
        this.app = app;
        this.conn = ConnectionManager.getConnection();
        dao = new DAO();

        app.get("/accounts/{customerId}", getCustomerAccounts) ;
        app.get("/accounts", getAllAccounts);
        app.put("/accounts/{accId}", updateAccount);
        app.post("/accounts", createAccount);
        app.delete("/accounts/{accId}", deleteAccount);

    }


    public Handler updateAccount = ctx -> {
       Account a = ctx.bodyAsClass(Account.class);
        if (dao.setAccount(a)==1)
            ctx.status(201); // Status code 201 means "created"
        else ctx.status(400);
    };

    public Handler getAllAccounts = ctx -> {
        ArrayList<Account> listAccounts = dao.getAllAccounts();
        if(listAccounts == null){
            ctx.result("No account yet!!");
        }
        else{
            ctx.json(listAccounts);
        }
        ctx.status(200);
    };

    public Handler getCustomerAccounts = ctx -> {
        int customerId = Integer.parseInt(ctx.pathParam("customerId"));
        ArrayList<Account> listAccounts = dao.getCustomerAccounts(customerId);
        if(listAccounts == null){
            ctx.result("Customer does not have any account yet");
        }
        else{
            ctx.json(listAccounts);
            ctx.status(200);
        }
    };

    public Handler createAccount = ctx -> {
        // This line deserializes a JSON object from the body and creates a Java object out of it
        Account a = ctx.bodyAsClass(Account.class);
        int id = dao.createAccount();
        if (id!=-1) {
            ctx.status(201); // Status code 201 means "created"
            ctx.result("The id of the created account is " + id);
        }
        else ctx.status(400);
    };

    public Handler deleteAccount = ctx -> {
        Account a = ctx.bodyAsClass(Account.class);
        if (dao.deleteAccount(a.getId())==1)
            ctx.status(201); // Status code 201 means "created"
        else ctx.status(400);
    };
}