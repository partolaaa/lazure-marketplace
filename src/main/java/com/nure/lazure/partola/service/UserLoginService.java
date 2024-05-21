package com.nure.lazure.partola.service;

import com.nure.lazure.partola.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static java.lang.String.format;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class UserLoginService {
    private final RestTemplate restTemplate;
    @Value("${accounts.api.url}")
    private String ACCOUNTS_API_URI;
    @Value("${accounts.api.password}")
    private String ACCOUNTS_API_PASSWORD;
    private final String BEARER_PREFIX = "Bearer ";

    @Autowired
    public UserLoginService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void login(User user, HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, format("%s%s", BEARER_PREFIX, ACCOUNTS_API_PASSWORD));
        HttpEntity<User> request = new HttpEntity<>(user, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                format("%s/login", ACCOUNTS_API_URI),
                HttpMethod.POST,
                request,
                String.class
        );

        HttpHeaders responseHeaders = response.getHeaders();
        String jwtToken = Objects.requireNonNull(responseHeaders.getFirst(HttpHeaders.AUTHORIZATION)).replace(BEARER_PREFIX, "");
        session.setAttribute("jwtToken", jwtToken);
    }

    public void logout(HttpSession session) {
        session.removeAttribute("jwtToken");
    }
}
