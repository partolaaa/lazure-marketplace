package com.nure.lazure.partola.controller;

import com.nure.lazure.partola.model.User;
import com.nure.lazure.partola.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/get-product-owner-wallet-by-product-id/{productId}")
    public User getProductOwnerWalletByProductId(@PathVariable int productId) {
        return userService.getProductOwnerWalletByProductId(productId);
    }
}
