package com.nure.lazure.partola.services;

import com.nure.lazure.partola.exceptions.DataNotRetrievedException;
import com.nure.lazure.partola.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class UsersService {
    private final RestTemplate restTemplate;
    @Autowired
    public UsersService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    public User getProductOwnerWalletByProductId(int productId) {
        try {
            String ACCOUNTS_API_URL = "https://accountsapi-3a5f92f4b3d5.herokuapp.com/users";
            String url = ACCOUNTS_API_URL +"/get-product-owner-wallet-by-product-id/"+productId;
            ResponseEntity<User> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataNotRetrievedException("Error while getting owner's wallet.");
        }
    }
}
