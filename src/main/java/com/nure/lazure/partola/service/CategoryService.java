package com.nure.lazure.partola.service;

import com.nure.lazure.partola.exception.DataNotRetrievedException;
import com.nure.lazure.partola.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.lang.String.format;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class CategoryService {
    private final RestTemplate restTemplate;
    @Value("${products.api.url}")
    private String PRODUCTS_API_URL;

    @Autowired
    public CategoryService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Category> getCategories() {
        try {
            String url = format("%s/category", PRODUCTS_API_URL);
            ResponseEntity<List<Category>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving categories.");
        }
    }
}
