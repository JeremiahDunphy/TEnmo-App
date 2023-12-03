package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/accountByUserId/{userId}")
    public Account getAccountByUserId(@PathVariable int userId) {
        try {
            return accountDao.getAccountbyUserId(userId);
        } catch (Exception e) {
            // Log error
            return null;
        }
        }
    }


