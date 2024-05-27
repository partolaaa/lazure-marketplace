package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.model.dto.UserDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class UserService {
    private WebClient webClient;

    @Value("${accounts.api.url}")
    private String ACCOUNTS_API_URL;

    @Value("${accounts.api.url.path.users}")
    private String USERS_URL_PATH;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(ACCOUNTS_API_URL)
                .build();
    }

    public UserDto getProductOwnerByProductId(Long productId) {
        try {
            String url = String.format("%s/%s/get-product-owner-wallet-by-product-id/%d", ACCOUNTS_API_URL, USERS_URL_PATH, productId);
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while getting owner's wallet: {}", e.getMessage());
            throw new DataNotRetrievedException("Error while getting owner's wallet.");
        }
    }

    public UserDto getUserById(Long userId) {
        try {
            String url = String.format("%s/user/%d", ACCOUNTS_API_URL, userId);
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while getting user by id {}: {}", userId, e.getMessage());
            throw new DataNotRetrievedException(String.format("Error while getting user by id %d.", userId));
        }
    }

    public UserDto getUserByWalletId(String walletId) {
        try {
            String url = String.format("%s/wallet/%s", ACCOUNTS_API_URL, walletId);
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while getting user by walletId {}: {}", walletId, e.getMessage());
            throw new DataNotRetrievedException(String.format("Error while getting user by walletId %s.", walletId));
        }
    }
}

