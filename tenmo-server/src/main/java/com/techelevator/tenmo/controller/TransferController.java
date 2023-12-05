package com.techelevator.tenmo.controller;
import com.techelevator.tenmo.dao.TransferDao;
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
    public TransferDto createTransfer(@RequestBody TransferDto transferDto) {
        try {
            return transferDao.createTransfer(transferDto);
        } catch (DataAccessException e) {
            System.out.print("There was an error processing transfer: " + e);
            return null;
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