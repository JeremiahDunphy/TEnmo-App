package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
                    new ParameterizedTypeReference<List<Transfer>>() {
                    }
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

    public TransferDto createTransfer(TransferDto transferDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        HttpEntity<TransferDto> entity = new HttpEntity<>(transferDto, headers);

        if (transferDto.getTransfer_type_id() == 2) {
            try {
                transferDto.setTransfer_type_id(2);
                transferDto.setTransfer_status_id(2);
                ResponseEntity<TransferDto> response = restTemplate.exchange(API_Base_URL + "/update", HttpMethod.PUT, entity, TransferDto.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                } else {
                    System.out.println("Transfer creation failed with status code: " + response.getStatusCode());
                    return null;
                }
            } catch (RestClientException e) {
                System.out.println("Error creating transfer: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        if (transferDto.getTransfer_type_id() == 1) {

            try {
                ResponseEntity<TransferDto> response = restTemplate.exchange(
                        API_Base_URL, // Base URL for transfer
                        HttpMethod.POST,
                        entity,
                        TransferDto.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                } else {
                    System.out.println("Transfer creation failed with status code: " + response.getStatusCode());
                    return null;
                }
            } catch (RestClientException e) {
                System.out.println("Error creating transfer: " + e.getMessage());
                e.printStackTrace();
               // return null;
            }
        }
        return transferDto;
    }

        public Transfer requestTransfer (Transfer transfer){
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
            try {
                ResponseEntity<Transfer> response = restTemplate.exchange(API_Base_URL, HttpMethod.POST, entity, Transfer.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                } else {
                    System.out.println("Transfer creation failed with status code: " + response.getStatusCode());
                    return null;
                }
            } catch (RestClientException e) {
                System.out.println("Error creating transfer: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }




