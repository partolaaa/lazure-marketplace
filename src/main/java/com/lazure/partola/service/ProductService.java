package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.exception.ProductNotAddedException;
import com.lazure.partola.model.Product;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class ProductService {
    private final RestTemplate restTemplate;
    @Value("${products.api.url}")
    private String PRODUCTS_API_URL;
    private final String BEARER_PREFIX = "Bearer ";
    @Autowired
    public ProductService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    public void add(Product product, HttpSession session) {
        try {
            String jwtToken = session.getAttribute("jwtToken").toString();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, format("%s%s", BEARER_PREFIX, jwtToken));
            HttpEntity<Product> request = new HttpEntity<>(product, headers);

            restTemplate.exchange(
                    format("%s/product", PRODUCTS_API_URL),
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ProductNotAddedException("Error while adding a new product.");
        }
    }

    public List<Product> getAllProductsByWallet(String wallet) {
        try {
            String url = format("%s/wallet/%s", PRODUCTS_API_URL, wallet);
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }

    public List<Product> getProducts(int limit,
                                     Optional<String> title,
                                     Optional<List<Integer>> categoryId,
                                     Optional<Integer> offset) {
        try {
            StringBuilder urlBuilder = new StringBuilder(format("%s/get-products?", PRODUCTS_API_URL));

            urlBuilder.append("limit=").append(limit);

            title.ifPresent(titleTemp -> urlBuilder.append("&title=").append(titleTemp));

            categoryId.ifPresent(ids -> ids.forEach(id -> urlBuilder.append("&category_id=").append(id)));

            offset.ifPresent(offsetTemp -> urlBuilder.append("&offset=").append(offsetTemp));
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    urlBuilder.toString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }
}
