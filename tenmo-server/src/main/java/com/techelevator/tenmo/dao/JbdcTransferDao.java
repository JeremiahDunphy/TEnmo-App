package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
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

        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer Amount must be positive.");
        }
        BigDecimal senderBalance = checkBalance(transferDto.getAccount_from());
        if (senderBalance.compareTo(transferDto.getAmount()) <= 0) {
            throw new IllegalArgumentException("insufficient funds");
        }
        if (transferDto.getAccount_from() == transferDto.getAccount_to()) {
            throw new IllegalArgumentException("You cannot transfer to your own account.");
        }
        if (transferDto.getTransfer_type_id() == 1) {
            try {
                requestTransfer(transferDto);
            } catch (DataIntegrityViolationException e) {
                throw new DataIntegrityViolationException("Failed to create the transfer request. Please check your transfer request and ensure accuracy." + e.getMessage());
            }
        }
        if (transferDto.getTransfer_type_id() == 2)

            try {
                //update balances
                removeBalance(transferDto.getAccount_from() + 1000, transferDto.getAmount());
                addBalance(transferDto.getAccount_to() + 1000, transferDto.getAmount());
                //record transaction
                recordTransaction(transferDto);
                return transferDto;
            } catch (CannotGetJdbcConnectionException e) {
                System.out.println("cannot get database connection." + e);
            } catch (DataIntegrityViolationException e) {
                System.out.println("You violated integrity check your keys and references" + e);
            }
        if(transferDto.getTransfer_type_id() == 3) {
            throw new IllegalArgumentException("Your transfer has been rejected");
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
        String updateSql = "UPDATE account SET balance = balance - ? WHERE user_id = ?";
        String selectSql = "SELECT balance FROM account WHERE user_id = ?";

        try {
            int rowsAffected = jdbcTemplate.update(updateSql, amount, userId);
            if (rowsAffected == 0) {
                // Handle the case where the user ID doesn't exist or no balance was removed
                System.out.println("No account balance updated for user_id: " + userId);
                return null; // or handle it accordingly
            }
            // If the update was successful, fetch and return the new balance
            return jdbcTemplate.queryForObject(selectSql, BigDecimal.class, userId);
        } catch (DataAccessException e) {
            System.out.println("Error updating balance for user_id: " + userId + ": " + e.getMessage());
            e.printStackTrace(); // Optional, for detailed exception information
            throw e; // Rethrow if you want to signal that an error occurred
        }
    }

    @Override
    public BigDecimal removeBalance(int userId, BigDecimal amount) {
        String updateSql = "UPDATE account SET balance = balance - ? WHERE user_id = ?";
        String selectSql = "SELECT balance FROM account WHERE user_id = ?";

        try {
            int rowsAffected = jdbcTemplate.update(updateSql, amount, userId);
            if (rowsAffected == 0) {
                // Handle the case where the user ID doesn't exist or no balance was removed
                System.out.println("No account balance updated for user_id: " + userId);
                return null; // or handle it accordingly
            }
            // If the update was successful, fetch and return the new balance
            return jdbcTemplate.queryForObject(selectSql, BigDecimal.class, userId);
        } catch (DataAccessException e) {
            System.out.println("Error updating balance for user_id: " + userId + ": " + e.getMessage());
            e.printStackTrace(); // Optional, for detailed exception information
            throw e; // Rethrow if you want to signal that an error occurred
        }

    }

    @Override
    public void recordTransaction(TransferDto transferDto) {
        String recordTransferSql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?)";

        try {
            jdbcTemplate.update(recordTransferSql, transferDto.getTransfer_type_id(), transferDto.getTransfer_status_id(), transferDto.getAccount_from(), transferDto.getAccount_to(), transferDto.getAmount());
            // Additional code here (if any) to handle the successful update
        } catch (DataAccessException e) {
            // Log the exception details here using your preferred logging framework
            // For example: log.error("Error recording transaction: ", e);
            // Then, you might want to rethrow the exception or handle it accordingly
            throw e; // Rethrow if you want to propagate the exception to the caller
        }
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
    @Override
    public TransferDto requestTransfer(TransferDto transferDto) {
        if (transferDto.getAccount_from() == transferDto.getAccount_to()) {
            throw new IllegalArgumentException("Cannot request money from yourself.");
        }
        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The requested amount must be positive.");
        }

        // Insert the transfer request using the status provided by the client
        String insertSql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";

        Integer newTransferId = jdbcTemplate.queryForObject(
                insertSql,
                new Object[]{
                        transferDto.getTransfer_type_id(),
                        transferDto.getTransfer_status_id(),
                        transferDto.getAccount_from(),
                        transferDto.getAccount_to(),
                        transferDto.getAmount()
                },
                Integer.class
        );

        if (newTransferId == null) {
            throw new DataAccessResourceFailureException("Failed to create the transfer request. The transfer ID could not be retrieved.");
        }

        transferDto.setTransfer_id(newTransferId);
        return transferDto;
    }

}


