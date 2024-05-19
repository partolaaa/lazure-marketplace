package com.nure.lazure.partola.controllers;

import com.nure.lazure.partola.models.Category;
import com.nure.lazure.partola.models.Product;
import com.nure.lazure.partola.models.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @author Ivan Partola
 */
@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
@Slf4j
public class ProductsController {
    private final RestTemplate restTemplate;
    private final String PRODUCTS_API_URL = "https://productsapi-954ed826b909.herokuapp.com";
    private final String ACCOUNTS_API_URL = "https://accountsapi-3a5f92f4b3d5.herokuapp.com/users";
    @Autowired
    public ProductsController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    @PostMapping("/new")
    public ResponseEntity<?> add(@ModelAttribute Product product, HttpSession session) {
        try {
            String jwtToken = session.getAttribute("jwtToken").toString();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
            HttpEntity<Product> request = new HttpEntity<>(product, headers);

            restTemplate.exchange(
                    PRODUCTS_API_URL+"/product",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return ResponseEntity.ok("Product added successfully.");
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error while adding a new product.");
        }
    }

    @GetMapping("/wallet/{wallet}")
    public ResponseEntity<?> getAllProductsByWallet(@PathVariable String wallet) {
        try {
            String url = PRODUCTS_API_URL+"/wallet/" + wallet;
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error while retrieving products.");
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> getCategories() {
        try {
            String url = PRODUCTS_API_URL+"/category";
            ResponseEntity<List<Category>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error while getting categories.");
        }
    }

    @GetMapping("/get-products")
    public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "40") int limit,
                                         @RequestParam Optional<String> title,
                                         @RequestParam Optional<List<Integer>> categoryId,
                                         @RequestParam Optional<Integer> offset) {
        try {
            StringBuilder urlBuilder = new StringBuilder(PRODUCTS_API_URL+"/get-products?");

            urlBuilder.append("limit=").append(limit);

            title.ifPresent(titleTemp -> urlBuilder.append("&title=").append(titleTemp));

            categoryId.ifPresent(ids -> {
                ids.forEach(id -> urlBuilder.append("&categoryId=").append(id));
            });

            offset.ifPresent(offsetTemp -> urlBuilder.append("&offset=").append(offsetTemp));
            System.out.println(urlBuilder);
            ResponseEntity<List<Product>> response = restTemplate.exchange(
                    urlBuilder.toString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error while retrieving products.");
        }
    }


    @GetMapping("/get-product-owner-wallet-by-product-id/{productId}")
    public ResponseEntity<?> getProductOwnerWalletByProductId(@PathVariable int productId) {
        try {
            String url = ACCOUNTS_API_URL+"/get-product-owner-wallet-by-product-id/"+productId;
            ResponseEntity<User> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error while getting owner's wallet.");
        }
    }
}
