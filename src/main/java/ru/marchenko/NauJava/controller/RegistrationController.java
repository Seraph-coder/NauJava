package ru.marchenko.NauJava.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.marchenko.NauJava.entity.User;
import ru.marchenko.NauJava.service.UserService;

/**
 * Контроллер для регистрации новых пользователей.
 *
 * @author Seraph-coder
 * @since 09.11.2025
 */
@Controller
public class RegistrationController {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает страницу регистрации.
     */
    @GetMapping("/registration")
    public String registrationView() {
        return "registration";
    }

    /**
     * Обрабатывает регистрацию нового пользователя.
     */
    @PostMapping("/registration")
    public String registerUser(User user, Model model) {
        try {
            userService.addUser(user);
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            log.error("Illegal argument: {}", ex.getMessage());
            model.addAttribute("error", ex.getMessage());
            return "registration";
        }
    }
    }
