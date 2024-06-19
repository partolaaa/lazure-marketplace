package com.lazure.partola.service;

import com.lazure.partola.exception.SolanaExchangeRatesException;
import com.lazure.partola.mapper.SolanaPriceMapper;
import com.lazure.partola.model.dto.CurrencyPriceResponseDto;
import com.lazure.partola.model.dto.SolanaPriceDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static com.lazure.partola.config.CacheConfiguration.CURRENT_CACHE_NAME;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SolanaExchangeService {
    private WebClient webClient;

    @Value("${solana.api.key}")
    private String SOLANA_API_KEY;

    @Value("${solana.api.url}")
    private String SOLANA_API_URL;

    @Value("${solana.api.key-name}")
    private String SOLANA_API_KEY_NAME;
    private final String TICKER_SOL = "SOL";
    private final String TICKER_USDC = "USDC";

    private final SolanaPriceMapper solanaPriceMapper;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(SOLANA_API_URL)
                .build();
    }

    @Cacheable(CURRENT_CACHE_NAME)
    public SolanaPriceDto getSolanaPrice() {
        try {
            log.info("Call external API to get solana price");
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("symbol", TICKER_SOL)
                            .queryParam("convert", TICKER_USDC)
                            .build())
                    .header(HttpHeaders.ACCEPT_ENCODING, "gzip", "deflate", "br")
                    .header(SOLANA_API_KEY_NAME, SOLANA_API_KEY)
                    .retrieve()
                    .bodyToMono(CurrencyPriceResponseDto.class)
                    .map(solanaPriceMapper::toSolanaPriceDto)
                    .block();
        } catch (Exception e) {
            log.error("Error during getting current solana price: {}", e.getMessage());
            throw new SolanaExchangeRatesException("Couldn't get current solana price: " + e);
        }
    }
}
