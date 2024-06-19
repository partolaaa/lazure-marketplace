package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.exception.ProductNotAddedException;
import com.lazure.partola.model.dto.ProductDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class ProductService {

    private WebClient webClient;
    private final String BEARER_PREFIX = "Bearer ";

    @Value("${products.api.url}")
    private String PRODUCTS_API_URL;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(PRODUCTS_API_URL)
                .build();
    }

    public void add(ProductDto productDto, HttpSession session) {
        try {
            String jwtToken = getJwtTokenFromSession(session).orElseThrow(() -> new IllegalArgumentException("JWT token is missing in session"));
            webClient.post()
                    .uri("/product")
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtToken)
                    .bodyValue(productDto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while adding a new product: {}", e.getMessage());
            throw new ProductNotAddedException("Error while adding a new product.");
        }
    }

    public List<ProductDto> getAllProductsByWallet(String wallet) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/wallet/{wallet}")
                            .build(wallet))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error while retrieving products: {}", e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }

    public ProductDto getProductById(Long productId, HttpSession session) {

        try {
            Optional<String> jwtTokenOpt = getJwtTokenFromSession(session);

            WebClient.RequestHeadersSpec<?> request = webClient.get()
                    .uri("/product/{productId}", productId);

            if (jwtTokenOpt.isPresent()) {
                request = request.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenOpt.get());
            }

            return request.retrieve()
                    .bodyToMono(ProductDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while retrieving product with id {}: ", productId, e);
            throw new DataNotRetrievedException(String.format("Error while retrieving product with id %d.", productId));
        }
    }

    public ProductDto getProductByIdWithoutAuth(Long productId) {

        try {
            return webClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .bodyToMono(ProductDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while retrieving product with id {}: {}", productId, e.getMessage());
            throw new DataNotRetrievedException(String.format("Error while retrieving product with id %d.", productId));
        }
    }

    public Double getMaxCost() {
        try {
            Map<String, Double> response = webClient.get()
                    .uri("/max-cost")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Double>>() {
                    })
                    .block();

            if (response == null || !response.containsKey("maxCost")) {
                throw new DataNotRetrievedException("No max cost data found.");
            }

            return response.get("maxCost");
        } catch (WebClientException e) {
            log.error("Error while retrieving max cost", e);
            throw new DataNotRetrievedException("Error while retrieving max cost.");
        }
    }


    private Optional<String> getJwtTokenFromSession(HttpSession session) {

        return Optional.ofNullable(session.getAttribute("jwtToken"))
                .map(Object::toString);
    }
}
