package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class JbdcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate; //created a constructed below instead of using the "new" keyword for dependency injection.

    public JbdcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
  public Account getAccountbyUserId(int user_Id) {
       Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, user_Id);
            if(result.next()) {
               account = mapToAccount(result);
            }

        }  catch(CannotGetJdbcConnectionException e) {
            System.out.println("could not sucessfully connect to the database.");
        }
        return account;
  }
    public Account getAccountbyId(int account_id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, account_id);
            if (result.next()) {
                account = mapToAccount(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            System.out.println("Could not successfully connect to the database: " + e.getMessage());
        }
        return account;
    }

public Account mapToAccount(SqlRowSet rs) {
Account account = new Account();
account.setAccount_id(rs.getInt("account_id"));
account.setBalance(rs.getBigDecimal("balance"));
account.setUser_id(rs.getInt("user_id"));

    return account;
    }
}
