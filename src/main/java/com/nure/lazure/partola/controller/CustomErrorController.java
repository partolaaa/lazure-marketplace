package com.nure.lazure.partola.controller;

import com.nure.lazure.partola.service.CustomErrorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Ivan Partola
 */
@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {
    private final CustomErrorService customErrorService;
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        customErrorService.handleError(request, model);
        return "global/error";
    }
}
