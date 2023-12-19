package com.techelevator.tenmo.dao;
import com.techelevator.tenmo.model.TransferDto;
import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    TransferDto createTransfer(TransferDto transferDto);
    BigDecimal checkBalance(int userId);
    BigDecimal addBalance(int userId, BigDecimal amount);
    BigDecimal removeBalance(int userId, BigDecimal amount);
    void recordTransaction(TransferDto transferDto);
    List<TransferDto> getPastTransfersByUserId(int userid);

    TransferDto requestTransfer(TransferDto transferDto);


}


