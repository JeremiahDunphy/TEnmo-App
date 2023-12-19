package com.techelevator.tenmo.model;


import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;

public class Transfer {
    int transfer_id;

        int transfer_type_id;

        int transfer_status_id;
        int account_from;
        int account_to;
        BigDecimal amount;
        @NotNull
        String transfer_type_desc;
        @NotNull
        String transfer_status_desc;

        public int getTransfer_id() {
            return transfer_id;
        }

        public void setTransfer_id(int transfer_id) {
            this.transfer_id = transfer_id;
        }

        public int getTransfer_type_id() {
            return transfer_type_id;
        }

        public void setTransfer_type_id(int transfer_type_id) {
            this.transfer_type_id = transfer_type_id;
        }

        public int getTransfer_status_id() {
            return transfer_status_id;
        }

        public void setTransfer_status_id(int transfer_status_id) {
            this.transfer_status_id = transfer_status_id;
        }

        public int getAccount_from() {
            return account_from;
        }

        public void setAccount_from(int account_from) {
            this.account_from = account_from;
        }

        public int getAccount_to() {
            return account_to;
        }

        public void setAccount_to(int account_to) {
            this.account_to = account_to;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

    }

