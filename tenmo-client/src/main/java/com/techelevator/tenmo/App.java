package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private AccountService accountService = new AccountService();

    private TransferService transferService = new TransferService();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        int userId = currentUser.getUser().getId();
        accountService.setAuthToken(currentUser.getToken());

        Account account = accountService.getAccountByIdWithToken(userId);
        System.out.println("Your current Balance is: $ " + account.getBalance());

    }

    private void viewTransferHistory() {
        int userId = currentUser.getUser().getId();
        transferService.setAuthToken(currentUser.getToken());

        try {
            List<Transfer> transferHistory = transferService.getTransfersByUserId(userId);

            // Check if the transferHistory is not null before iterating
            if (transferHistory != null && !transferHistory.isEmpty()) {
                for (Transfer transfer : transferHistory) {
                    System.out.println("Transfer ID: " + transfer.getTransfer_id() + " Amount: $" + transfer.getAmount());
                }
            } else {
                System.out.println("No transfer history found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while trying to fetch the transfer history: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void viewPendingRequests() {
        int requestTracker = 1;
        int userId = currentUser.getUser().getId();
        transferService.setAuthToken(currentUser.getToken());
        accountService.setAuthToken(currentUser.getToken());

        Account account = accountService.getAccountByIdWithToken(userId);
        if (account == null) {
            System.out.println("Error retrieving account information.");
            return;
        }

        int accountId = account.getAccount_id();
        List<Transfer> pendingRequests = transferService.getTransfersByUserId(userId);
        if (pendingRequests == null || pendingRequests.isEmpty()) {
            System.out.println("No pending transfer requests.");
            return;
        }
        for (Transfer request : pendingRequests) {
            System.out.println(requestTracker + ". " + request);
            requestTracker++;
        }
    }


    private void sendBucks() {
        transferService.setAuthToken(currentUser.getToken());
        accountService.setAuthToken(currentUser.getToken());
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the user ID of the user you would like to send funds to:");
        int recipientUserId = scanner.nextInt();
        System.out.println("Please enter the amount you would like to transfer to user ID: " + recipientUserId);
        BigDecimal transferAmount = scanner.nextBigDecimal();

        TransferDto transferDto = new TransferDto();
        transferDto.setAccount_from(currentUser.getUser().getId());
        transferDto.setAccount_to(recipientUserId);
        transferDto.setAmount(transferAmount);
        transferDto.setTransfer_status_id(2); // Replace 1 with the appropriate status ID for a completed transfer
        transferDto.setTransfer_type_id(2); // Replace 2 with the appropriate type ID for a send transfer

        try {
            TransferDto result = transferService.createTransfer(transferDto);
            if (result != null) {
                System.out.println("Transfer was successful");
            } else {
                System.out.println("Transfer failed.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


    private void requestBucks() {
        Transfer transfer = new Transfer();
        int fromUserID = currentUser.getUser().getId();
        Scanner scanner = new Scanner(System.in);
           System.out.println("Please enter the userID of who would like to request TE bucks from: ");
        int toUserID = scanner.nextInt();
        if(toUserID == fromUserID) {
        System.out.println("You cannot request TE bucks from yourself.");
        return;
        }

        System.out.println("Please enter the amount of TE bucks you would like to send: ");
            BigDecimal amountTo = scanner.nextBigDecimal();
            try {
                transfer.setTransfer_type_id(1); // pending
                transfer.setAccount_from(fromUserID);
                transfer.setAccount_to(toUserID);
                transfer.setAmount(amountTo);
                transfer.setTransfer_status_id(1); //pending

            } catch(IllegalArgumentException e) {
                throw new IllegalArgumentException("You had a illegal argument: " + e);
            }
            try {
                Transfer result = transferService.requestTransfer(transfer);
                if (result != null) {
                    System.out.println("Transfer request sent successfully.");
                } else {
                    System.out.println("Failed to send transfer request.");
                }
            } catch (Exception e) {
        System.out.println("Error during transfer request: " + e.getMessage());
    }

    }

    }
