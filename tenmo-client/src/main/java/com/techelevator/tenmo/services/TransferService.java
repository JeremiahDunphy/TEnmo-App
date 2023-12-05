package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

public class TransferService {
    public String API_Base_URL = "http://localhost:8080/transfer";

    private final RestTemplate restTemplate = new RestTemplate();
    public String authToken = null;

    public void setAuthToken(String authToken) {
    this.authToken = authToken;
    }
    public Transfer transferAccountWithToken(int accountId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(
                    API_Base_URL + "/transfer/" + accountId, // Adjusted URL
                    HttpMethod.POST, // or PUT, depending on your requirement
                    entity,
                    Transfer.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            System.out.println("Error with transfer: " + e.getMessage());
            e.printStackTrace(); // For more detailed debugging information
            return null;
        }
    }

    public List<Transfer> getTransfersByUserId(int userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<List<Transfer>> response = restTemplate.exchange(
                    API_Base_URL + "/transfers/" + userId, // Adjusted URL
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Transfer>>() {}
            );

            // Check the response status code before returning the body
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Transfer> transfers = response.getBody();
                // If the body is null (which can happen if there are no content), return an empty list
                return transfers != null ? transfers : Collections.emptyList();
            } else {
                // If the response status code is not 2xx (successful), then log and return an empty list
                System.out.println("Request failed with status code: " + response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            // Log the full stack trace for debugging purposes
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
