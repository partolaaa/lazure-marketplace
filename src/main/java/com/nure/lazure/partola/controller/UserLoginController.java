package com.nure.lazure.partola.controller;

import com.nure.lazure.partola.model.User;
import com.nure.lazure.partola.service.UserLoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    public void login(@RequestBody User user, HttpSession session) {
        userLoginService.login(user, session);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        userLoginService.logout(session);
    }
}
