package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
    private int account_id;
    private int user_id;
    private BigDecimal balance;

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public BigDecimal getBalance() {
        return this.balance;
    }

    public int getAccount_id() {
        return account_id;
    }

    public int getUser_id() {
        return user_id;
    }
}
