package com.revature.controllers;

import com.revature.dao.ConnectionManager;
import com.revature.dao.DAO;
import com.revature.dao.EmployeeDAO;
import com.revature.model.Account;
import com.revature.model.Employee;
import io.javalin.Javalin;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.util.ArrayList;

public class EmployeeController {

    Javalin app;
    Connection conn;

    public EmployeeController(Javalin app) {
        this.app = app;
        this.conn = ConnectionManager.getConnection();
        app.post("/employees/", createNewEmployee);
        app.get("/employees/{username}", getEmployeeByUsername);
        app.put("/employees/{username}", updateEmployee);
        app.delete("/employees/{username}", deleteEmployee);
    }

    // Create a new User from a given User object

    public Handler getEmployeeByUsername = ctx -> {
        Employee e = EmployeeDAO.findEmployeeByUsername(ctx.pathParam("username"));
        if(e != null){
            ctx.json(e);
            ctx.status(200);
        } else ctx.status(204);

    };

  public Handler createNewEmployee = ctx -> {
        // This line deserializes a JSON object from the body and creates a Java object out of it
        try{
            Employee employee = ctx.bodyAsClass(Employee.class);
            System.out.println(null == employee);
        } catch(Exception e){
            e.printStackTrace();
        }

    };



    public Handler updateEmployee = ctx -> {
        try{
            Employee employee = ctx.bodyAsClass(Employee.class);
            if (EmployeeDAO.setEmployee(employee)){
                // Status code 204 means "Successfully updated"
                ctx.json(employee);
                ctx.status(204);

            }

                // Status code 400 means "Error occurred"
            else ctx.status(400);
        } catch (Exception e){
            e.printStackTrace();
        }
    };


    public Handler deleteEmployee = ctx -> {
        try{
            Employee employee = ctx.bodyAsClass(Employee.class);
            if (EmployeeDAO.deleteEmployee(employee.getId())){
                // Status code 204 means "Successfully updated"
                ctx.status(204);
            }
            // Status code 400 means "Error occurred"
            else ctx.status(400);
        } catch (Exception e){
            e.printStackTrace();
        }
    };

}