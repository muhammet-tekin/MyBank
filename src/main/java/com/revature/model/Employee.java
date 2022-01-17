package com.revature.model;

public class Employee extends Customer{
    private boolean isAdmin;

    public Employee(String username, String password, int id, boolean isAdmin) {
        super(username, password, id);
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
