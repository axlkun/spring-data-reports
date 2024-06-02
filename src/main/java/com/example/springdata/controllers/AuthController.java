package com.example.springdata.controllers;

import com.example.springdata.models.User;
import com.example.springdata.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    // register view
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    // login view
    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, BindingResult result, Model model) {

        logger.info("Attempting to register user with email: " + user.getEmail());

        if (result.hasErrors()) {
            logger.error("Registration form has errors: " + result.getAllErrors());
            return "auth/register";
        }
        userService.save(user);
        model.addAttribute("message", "Registro correcto! Ir al login");
        logger.info("User registered successfully with email: " + user.getEmail());
        return "auth/register";
    }
}
