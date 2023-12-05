package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JbdcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JbdcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Transactional
    @Override
    public TransferDto createTransfer(TransferDto transferDto) {

        //validate transfer conditions

        if(transferDto.getAmount().compareTo(BigDecimal.ZERO) <=0) {
            throw new IllegalArgumentException("Transfer Amount must be positive.");
        }
        BigDecimal senderBalance = checkBalance(transferDto.getAccount_from());
        if(senderBalance.compareTo(transferDto.getAmount()) <= 0) {
            throw new IllegalArgumentException("insufficient funds");
        }
        if(transferDto.getAccount_from() == transferDto.getAccount_to()) {
            throw new IllegalArgumentException("You cannot transfer to your own account.");
        }

          try {
            //record transaction
            recordTransaction(transferDto);
            //update balances
            removeBalance(transferDto.getAccount_from(), transferDto.getAmount());
            addBalance(transferDto.getAccount_to(), transferDto.getAmount());
        } catch(CannotGetJdbcConnectionException e) {
            System.out.println("cannot get database connection." + e);
        } catch (DataIntegrityViolationException e) {
            System.out.println("You violated integrity check your keys and references" + e);
        }
        return transferDto;
    }
    @Override
    public BigDecimal checkBalance(int userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }
    @Override
    public BigDecimal addBalance(int userId, BigDecimal amount) {
        // Update the balance
        String updateSql = "UPDATE account SET balance = balance + ? WHERE user_id = ?";
        jdbcTemplate.update(updateSql, amount, userId);
        //get the new updated balance and return it
        String selectSql = "SELECT balance FROM account WHERE user_id = ?";
            return jdbcTemplate.queryForObject(selectSql, BigDecimal.class, userId);
        }

    @Override
    public BigDecimal removeBalance(int userId, BigDecimal amount) {
        //update the balance
        String sql = "UPDATE account SET balance = balance - ? WHERE user_id = ?";
        jdbcTemplate.update(sql, amount, userId);
        //get the updated balance
        String sql1 = "SELECT balance FROM account WHERE user_id = ?;";
        return jdbcTemplate.queryForObject(sql1, BigDecimal.class, userId);

    }

    @Override
    public void recordTransaction(TransferDto transferDto) {
        String recordTransferSql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(recordTransferSql, transferDto.getTransfer_type_id(), transferDto.getTransfer_status_id(), transferDto.getAccount_from(), transferDto.getAccount_to(), transferDto.getAmount());
    }
    public TransferDto mapToTransferDto(SqlRowSet rs) {
        TransferDto transferDto = new TransferDto();
        transferDto.setTransfer_id(rs.getInt("transfer_id"));
        transferDto.setTransfer_type_id(rs.getInt("transfer_type_id"));
        transferDto.setTransfer_status_id(rs.getInt("transfer_status_id"));
        transferDto.setAccount_from(rs.getInt("account_from"));
        transferDto.setAccount_to(rs.getInt("account_to"));
        transferDto.setAmount(rs.getBigDecimal("amount"));

        return transferDto;
    }
    @Override
    public List<TransferDto> getPastTransfersByUserId(int userid) {
        List<TransferDto> transfers = new ArrayList<>();
        String sql = "SELECT t.* FROM transfer t " +
                "JOIN account a ON a.account_id = t.account_from " +
                "WHERE a.user_id = ? " +
                "UNION " +
                "SELECT t.* FROM transfer t " +
                "JOIN account a ON a.account_id = t.account_to " +
                "WHERE a.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userid, userid);
        while (results.next()) {
            transfers.add(mapToTransferDto(results));
        }
        return transfers;
    }

}
