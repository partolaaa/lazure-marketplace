package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.exception.ProductNotAddedException;
import com.lazure.partola.model.dto.ProductDto;
import com.lazure.partola.model.criteria.ProductCriteria;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
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

    public void add(ProductDto productDto, HttpSession session) {
        try {
            String jwtToken = session.getAttribute("jwtToken").toString();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, format("%s%s", BEARER_PREFIX, jwtToken));
            HttpEntity<ProductDto> request = new HttpEntity<>(productDto, headers);

            restTemplate.exchange(
                    format("%s/productDto", PRODUCTS_API_URL),
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ProductNotAddedException("Error while adding a new productDto.");
        }
    }

    public List<ProductDto> getAllProductsByWallet(String wallet) {
        try {
            String url = format("%s/wallet/%s", PRODUCTS_API_URL, wallet);
            ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }

    public List<ProductDto> getProducts(int limit,
                                        int offset,
                                        ProductCriteria productCriteria) {

        try {
            final String getProductsApiUri = format("%s/get-products", PRODUCTS_API_URL);
            URIBuilder uriBuilder = new URIBuilder(getProductsApiUri);
            uriBuilder
                    .addParameter("limit", String.valueOf(limit))
                    .addParameter("offset", String.valueOf(offset));

            productCriteria.title().ifPresent(titleTemp -> uriBuilder.addParameter("title", titleTemp));

            productCriteria.categoryId().ifPresent((categoryId -> uriBuilder.addParameters(categoryId.stream()
                    .map(id -> (NameValuePair) new BasicNameValuePair("category_id", String.valueOf(id)))
                    .toList())));
            ResponseEntity<List<ProductDto>> response = restTemplate.exchange(
                    uriBuilder.build().toString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving products.");
        }
    }
}
