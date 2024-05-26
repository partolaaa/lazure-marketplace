package com.lazure.partola.service;

import com.lazure.partola.model.dto.UserDto;
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
    @Value("${accounts.api.url.path.users}")
    private String USERS_URL_PATH;
    @Value("${accounts.api.password}")
    private String ACCOUNTS_API_PASSWORD;

    @Autowired
    public UserLoginService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void login(UserDto userDto, HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        String BEARER_PREFIX = "Bearer ";
        headers.set(HttpHeaders.AUTHORIZATION, format("%s%s", BEARER_PREFIX, ACCOUNTS_API_PASSWORD));
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                format("%s/%s/login", ACCOUNTS_API_URI, USERS_URL_PATH),
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
