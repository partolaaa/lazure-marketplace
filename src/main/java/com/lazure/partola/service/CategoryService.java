package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.model.dto.CategoryDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class CategoryService {
    private WebClient webClient;

    @Value("${products.api.url}")
    private String PRODUCTS_API_URL;

    @Value("${products.api.url.path.categories}")
    private String CATEGORY_URL_PATH;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(PRODUCTS_API_URL)
                .build();
    }

    public List<CategoryDto> getCategories() {
        try {
            String url = String.format("%s/%s", PRODUCTS_API_URL, CATEGORY_URL_PATH);
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<CategoryDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Error while retrieving categories: {}", e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving categories.");
        }
    }
}

