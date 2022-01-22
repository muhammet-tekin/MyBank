package com.revature.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Employee extends Customer{
    public boolean isAdmin;

    public Employee(){
        super("","",-1);
        isAdmin = false;
    };

    public Employee(String username, String password, int id, boolean isAdmin) {
        super(username, password, id);
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
