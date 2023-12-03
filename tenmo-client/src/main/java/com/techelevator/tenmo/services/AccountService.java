package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;


public class AccountService {
    public String API_BASE_URL2 = "http://localhost:8080/account/accountByUserId/";

    private final RestTemplate restTemplate = new RestTemplate();
    public String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Account getAccountByIdWithToken(int userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity entity = new HttpEntity(headers);

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL2 + userId, HttpMethod.GET, entity, Account.class);
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error retrieving account: " + e.getMessage());
            return null;
        }
    }

}
