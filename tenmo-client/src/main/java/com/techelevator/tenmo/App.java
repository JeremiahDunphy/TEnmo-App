package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.util.ArrayList;
import java.util.List;

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
        Account account = accountService.getAccountByIdWithToken(userId);
        int accountId = account.getAccount_id();
        transferService.setAuthToken(currentUser.getToken());
        List<Transfer> pendingRequests = transferService.getTransfersByUserId(userId);
        for (Transfer request : pendingRequests) {
            System.out.println(requestTracker + ". " + request);
            requestTracker++;
        }
        if (requestTracker == 1) {
            System.out.println("No pending transfer requests.");

        }
    }

        private void sendBucks () {
            // TODO Auto-generated method stub

        }

        private void requestBucks () {
            // TODO Auto-generated method stub

        }

    }
