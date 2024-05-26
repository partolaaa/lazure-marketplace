package com.lazure.partola.controller;

import com.lazure.partola.model.dto.UserDto;
import com.lazure.partola.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/${accounts.api.url.path.users}")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/get-product-owner-wallet-by-product-id/{productId}")
    public UserDto getProductOwnerWalletByProductId(@PathVariable Long productId) {
        return userService.getProductOwnerByProductId(productId);
    }

    @GetMapping("/wallet/{walletId}")
    public UserDto getUserByWalletId(@PathVariable String walletId) {
        return userService.getUserByWalletId(walletId);
    }
}
