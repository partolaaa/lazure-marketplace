package com.nure.lazure.partola.controllers;

import com.nure.lazure.partola.models.User;
import com.nure.lazure.partola.services.UserLoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserLoginController {
    private final UserLoginService userLoginService;

    @PostMapping("/login")
    public String login(@RequestBody User user, HttpSession session) {
        return userLoginService.login(user, session);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        userLoginService.logout(session);
        return ResponseEntity.ok("Wallet was successfully disconnected!");
    }
}
