package com.revature.model;

import java.io.Serializable;

public class Account implements Serializable {
    int id;
    Double balance;
    boolean isApproved;
    boolean isCancelled;

    public Account(int id, Double balance, boolean isApproved, boolean isCancelled) {
        this.id = id;
        this.balance = balance;
        this.isApproved = isApproved;
        this.isCancelled = isCancelled;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                ", isApproved=" + isApproved +
                ", isCancelled=" + isCancelled +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
