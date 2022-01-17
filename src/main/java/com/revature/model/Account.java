package com.revature.model;

public class Account {
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
}
