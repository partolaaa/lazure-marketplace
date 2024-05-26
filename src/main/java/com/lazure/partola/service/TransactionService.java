package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.exception.TransactionNotAddedException;
import com.lazure.partola.model.dto.TransactionDto;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
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
public class TransactionService {
    @Value("${accounts.api.url}")
    private String ACCOUNTS_API_URL;

    @Value("${accounts.api.url.path.transactions}")
    private String TRANSACTIONS_URL_PATH;

    private final RestTemplate restTemplate;

    @Autowired
    public TransactionService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<TransactionDto> getTransactionsByWalletId(String walletId) {
        try {
            URIBuilder uriBuilder = new URIBuilder(format("%s/%s", ACCOUNTS_API_URL, TRANSACTIONS_URL_PATH));
            uriBuilder.addParameter("walletId", walletId);
            ResponseEntity<List<TransactionDto>> response = restTemplate.exchange(
                    uriBuilder.build().toString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving transactions.");
        }
    }

    public void add(TransactionDto transactionDto, HttpSession session) {
        try {
            String jwtToken = session.getAttribute("jwtToken").toString();
            HttpHeaders headers = new HttpHeaders();
            String BEARER_PREFIX = "Bearer ";
            headers.set(HttpHeaders.AUTHORIZATION, format("%s%s", BEARER_PREFIX, jwtToken));
            HttpEntity<TransactionDto> request = new HttpEntity<>(transactionDto, headers);

            restTemplate.exchange(
                    format("%s/%s", ACCOUNTS_API_URL, TRANSACTIONS_URL_PATH),
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TransactionNotAddedException("Error while adding a new transaction.");
        }
    }
}
