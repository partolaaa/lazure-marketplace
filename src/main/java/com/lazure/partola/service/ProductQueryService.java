package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.model.criteria.ProductCriteria;
import com.lazure.partola.model.dto.ProductDto;
import com.lazure.partola.model.dto.ProductOwnerDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductQueryService {
    private WebClient webClient;
    private final String BEARER_PREFIX = "Bearer ";

    @Value("${products.api.url}")
    private String PRODUCTS_API_URL;

    private final UserService userService;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(PRODUCTS_API_URL)
                .build();
    }
    public List<ProductDto> getProducts(int limit, int offset, ProductCriteria productCriteria) {
        try {
            List<ProductDto> productDtos = Objects.requireNonNull(webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/get-products")
                                .queryParam("limit", limit)
                                .queryParam("offset", offset);

                        productCriteria.title().ifPresent(title -> uriBuilder.queryParam("title", title));
                        productCriteria.categoryId().ifPresent(ids ->
                                ids.forEach(id -> uriBuilder.queryParam("category_id", id)));

                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ProductDto>>() {})
                    .block());

            List<ProductOwnerDto> productOwnerDtos = userService.getMultipleProductOwners(productDtos);
            productDtos.forEach(productDto -> productOwnerDtos.stream()
                    .filter(productOwnerDto -> productOwnerDto.getProductId().equals(productDto.getProductId()))
                    .findFirst()
                    .ifPresent(productOwnerDto -> productDto.setWalletId(productOwnerDto.getWalletId())));

            return productDtos;
        } catch (Exception e) {
            log.error("Error while retrieving products: {}", e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }
}
