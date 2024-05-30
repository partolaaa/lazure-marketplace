package com.lazure.partola.service;

import com.lazure.partola.exception.DataNotRetrievedException;
import com.lazure.partola.exception.TransactionNotAddedException;
import com.lazure.partola.mapper.TransactionMapper;
import com.lazure.partola.model.dto.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

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

    private final TransactionMapper transactionMapper;
    private final UserService userService;
    private final ProductService productService;
    private WebClient webClient;

    @Autowired
    public TransactionService(TransactionMapper transactionMapper, UserService userService, ProductService productService) {
        this.transactionMapper = transactionMapper;
        this.userService = userService;
        this.productService = productService;
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(ACCOUNTS_API_URL)
                .build();
    }

    public List<FullTransactionInfoDto> getTransactionsByWalletId(String walletId) {
        try {
            String url = String.format("%s/%s?walletId=%s", ACCOUNTS_API_URL, TRANSACTIONS_URL_PATH, walletId);
            List<TransactionDto> transactionList = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TransactionDto>>() {})
                    .block();

            return Objects.requireNonNull(transactionList).stream().map(transactionDto -> {
                UserDto buyer = userService.getUserById(transactionDto.getBuyerId());
                UserDto seller = userService.getUserById(transactionDto.getSellerId());
                ProductDto productDto = productService.getProductByIdWithoutAuth(transactionDto.getProductId());
                return transactionMapper.toFullTransactionInfoDto(transactionDto, buyer, seller, productDto);
            }).toList();
        } catch (Exception e) {
            log.error("Error while retrieving transactions: {}", e.getMessage());
            throw new DataNotRetrievedException("Error while retrieving transactions.");
        }
    }

    public void add(TransactionDto transactionDto, HttpSession session) {
        try {
            transactionDto.setCreatedTime(LocalDateTime.now(ZoneOffset.UTC));
            String jwtToken = Objects.requireNonNull(session.getAttribute("jwtToken")).toString();
            String BEARER_PREFIX = "Bearer ";
            String url = String.format("%s/%s", ACCOUNTS_API_URL, TRANSACTIONS_URL_PATH);

            webClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtToken)
                    .bodyValue(transactionDto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while adding a new transaction: {}", e.getMessage());
            throw new TransactionNotAddedException("Error while adding a new transaction.");
        }
    }
}

