package ru.marchenko.NauJava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.marchenko.NauJava.entity.User;
import ru.marchenko.NauJava.repository.UserRepository;

/**
 * Контроллер для отображения списка пользователей (рендерит HTML через Thymeleaf).
 */
@Controller
@RequestMapping("/userlist")
public class UserListController {
    private final UserRepository userRepository;

    public UserListController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Отображает список всех пользователей в виде HTML-страницы.
     */
    @GetMapping
    public String userListView(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "userlist";
    }
}
