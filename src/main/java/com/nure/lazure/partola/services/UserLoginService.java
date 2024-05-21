package com.nure.lazure.partola.services;

import com.nure.lazure.partola.models.User;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class UserLoginService {
    private final RestTemplate restTemplate;

    @Autowired
    public UserLoginService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String login(User user, HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + System.getenv("PASSWORD"));
        HttpEntity<User> request = new HttpEntity<>(user, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://accountsapi-3a5f92f4b3d5.herokuapp.com/users/login",
                HttpMethod.POST,
                request,
                String.class
        );

        HttpHeaders responseHeaders = response.getHeaders();
        String jwtToken = Objects.requireNonNull(responseHeaders.getFirst(HttpHeaders.AUTHORIZATION)).replace("Bearer ", "");
        session.setAttribute("jwtToken", jwtToken);

        return "Wallet was connected successfully.";
    }

    public void logout(HttpSession session) {
        session.removeAttribute("jwtToken");
    }
}
