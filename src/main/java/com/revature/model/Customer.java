package com.revature.model;

public class Customer {

    private String username, password;

    public Customer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
}
