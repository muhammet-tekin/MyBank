package com.revature.model;

public class Credit {
    int id;
    Double amount;
    int debtorId;
    boolean isApproved;

    public Credit(int id, Double amount, int debtorId, boolean isApproved) {
        this.id = id;
        this.amount = amount;
        this.debtorId = debtorId;
        this.isApproved = isApproved;
    }
}
