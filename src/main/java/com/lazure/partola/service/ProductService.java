package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.exception.ProductNotAddedException;
import com.lazure.partola.model.dto.ProductDto;
import com.lazure.partola.model.criteria.ProductCriteria;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


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
            String jwtToken = getJwtTokenFromSession(session);
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
                    .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Error while retrieving products: {}", e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }

    public List<ProductDto> getProducts(int limit, int offset, ProductCriteria productCriteria) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> {
                        URIBuilder builder;
                        try {
                            builder = new URIBuilder(PRODUCTS_API_URL + "/get-products");
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        builder.addParameter("limit", String.valueOf(limit))
                                .addParameter("offset", String.valueOf(offset));

                        URIBuilder finalBuilder = builder;
                        productCriteria.title().ifPresent(title -> finalBuilder.addParameter("title", title));
                        URIBuilder finalBuilder1 = builder;
                        productCriteria.categoryId().ifPresent(ids -> ids.forEach(id ->
                                finalBuilder1.addParameter("category_id", String.valueOf(id))));

                        try {
                            return URI.create(builder.build().toString());
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Error while retrieving products: {}", e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }

    public ProductDto getProductById(Long productId, HttpSession session) {
        try {
            String jwtToken = getJwtTokenFromSession(session);
            return webClient.get()
                    .uri("/product/{productId}", productId)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtToken)
                    .retrieve()
                    .bodyToMono(ProductDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while retrieving product with id {}: {}", productId, e.getMessage());
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

    private String getJwtTokenFromSession(HttpSession session) {
        Object token = session.getAttribute("jwtToken");
        if (token == null) {
            throw new IllegalArgumentException("JWT token is missing in session");
        }
        return token.toString();
    }
}

