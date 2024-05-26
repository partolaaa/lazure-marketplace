package com.lazure.partola.controller;

import com.lazure.partola.model.dto.UserDto;
import com.lazure.partola.service.UserLoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/${accounts.api.url.path.users}")
@RequiredArgsConstructor
public class UserLoginController {
    private final UserLoginService userLoginService;

    @PostMapping("/login")
    public void login(@RequestBody UserDto userDto, HttpSession session) {
        userLoginService.login(userDto, session);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        userLoginService.logout(session);
    }
}
