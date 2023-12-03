package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {
    Account getAccountbyId(int account_id);
    Account getAccountbyUserId(int userId); // Declare this method
}