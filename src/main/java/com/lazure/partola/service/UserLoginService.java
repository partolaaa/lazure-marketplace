package com.lazure.partola.service;

import com.lazure.partola.exception.LoginFailedException;
import com.lazure.partola.model.dto.UserDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserLoginService {
    private WebClient webClient;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${accounts.api.url}")
    private String ACCOUNTS_API_URI;

    @Value("${accounts.api.url.path.users}")
    private String USERS_URL_PATH;

    @Value("${accounts.api.password}")
    private String ACCOUNTS_API_PASSWORD;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(ACCOUNTS_API_URI)
                .build();
    }

    public void login(UserDto userDto, HttpSession session) {
        try {
            String BEARER_PREFIX = "Bearer ";
            String url = String.format("%s/%s/login", ACCOUNTS_API_URI, USERS_URL_PATH);

            String jwtToken = Objects.requireNonNull(webClient.post()
                            .uri(url)
                            .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + ACCOUNTS_API_PASSWORD)
                            .bodyValue(userDto)
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(response -> {
                                HttpHeaders responseHeaders = response.getHeaders();
                                return Mono.justOrEmpty(responseHeaders.getFirst(HttpHeaders.AUTHORIZATION));
                            })
                            .block())
                    .replace(BEARER_PREFIX, "");
            session.setAttribute("jwtToken", jwtToken);
            messagingTemplate.convertAndSend("/topic/login", "");
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            throw new LoginFailedException("Login failed with error: " + e);
        }
    }

    public void logout(HttpSession session) {
        session.removeAttribute("jwtToken");
        messagingTemplate.convertAndSend("/topic/logout", "");
    }
}

