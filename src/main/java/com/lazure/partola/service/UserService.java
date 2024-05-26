package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.model.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class UserService {
    private final RestTemplate restTemplate;
    @Value("${accounts.api.url}")
    private String ACCOUNTS_API_URL;

    @Value("${accounts.api.url.path.users}")
    private String USERS_URL_PATH;
    @Autowired
    public UserService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    public UserDto getProductOwnerWalletByProductId(int productId) {
        try {
            String url = format("%s/%s/get-product-owner-wallet-by-product-id/%s", ACCOUNTS_API_URL, USERS_URL_PATH, productId);
            ResponseEntity<UserDto> response = restTemplate.exchange(
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
