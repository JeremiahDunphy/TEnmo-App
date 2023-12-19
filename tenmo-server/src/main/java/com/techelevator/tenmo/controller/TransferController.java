package com.techelevator.tenmo.controller;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController {
    private final TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TransferDto> createTransfer(@RequestBody TransferDto transferDto) {
        try {
            TransferDto createdTransfer = transferDao.createTransfer(transferDto);
            return new ResponseEntity<>(createdTransfer, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            throw new DaoException("Database access issue" + e.getMessage());

        }
    }

    @RequestMapping (path = "/update", method = RequestMethod.PUT)
    public ResponseEntity<TransferDto> updateTransfer(TransferDto transferDto) {
    try {
        TransferDto updatedTransfer  = transferDao.createTransfer(transferDto);
        return new ResponseEntity<>(updatedTransfer, HttpStatus.OK);
    } catch(DataAccessException e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }

    @RequestMapping(path = "/transfers/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<TransferDto>> getTransfersByUserId(@PathVariable int userId) {
        try {
            List<TransferDto> transfers = transferDao.getPastTransfersByUserId(userId);
            if (transfers.isEmpty()) {
                return new ResponseEntity<>(transfers, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(transfers, HttpStatus.OK);
        } catch (DataAccessException e) {
            System.out.print("Error fetching transfer history: " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}