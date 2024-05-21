package com.nure.lazure.partola.service;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author Ivan Partola
 */
@Service
@Slf4j
public class CustomErrorService {
    public void handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorType", 404);
                model.addAttribute("errorMessage", "This page could not be found");
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorType", 500);
                model.addAttribute("errorMessage", "Something went wrong...");
            }
        }
    }
}
