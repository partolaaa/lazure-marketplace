package com.lazure.partola.controller;

import com.lazure.partola.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Ivan Partola
 */
@Controller
@RequiredArgsConstructor
public class UserPageController {
    @GetMapping
    public String mainPage() {
        return "global/marketplace";
    }

    @GetMapping("/profile/{wallet}")
    public String profile(@PathVariable String wallet) {
        System.out.println(wallet);
        return "user/profile";
    }

    @GetMapping("/my-listings")
    public String listings(Model model) {
        model.addAttribute("product", new Product());
        return "user/listings";
    }
}
