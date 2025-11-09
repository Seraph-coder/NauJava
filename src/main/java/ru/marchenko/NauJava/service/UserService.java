package ru.marchenko.NauJava.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marchenko.NauJava.entity.User;
import ru.marchenko.NauJava.repository.UserRepository;

import java.util.Optional;

/**
 * Сервис для управления пользователями.
 *
 * @author Seraph-coder
 * @since 09.11.2025
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Добавляет нового пользователя.
     *
     * @param user Пользователь для добавления.
     * @return Сохраненный пользователь.
     * @throws IllegalArgumentException если имя пользователя или пароль пусты,
     *                                  или пользователь с таким именем уже существует.
     */
    @Transactional
    public User addUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Иmя пользователя не должно быть пустым");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Пароль не должен быть пустым");
        }

        Optional<User> existing = userRepository.findUserByUsername(user.getUsername());

        if (existing.isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует: " + user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Ошибка сохранения: возможно, email уже используется", ex
            );
        }
    }
}
