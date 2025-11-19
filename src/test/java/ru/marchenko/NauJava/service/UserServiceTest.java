package ru.marchenko.NauJava.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.marchenko.NauJava.entity.User;
import ru.marchenko.NauJava.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link UserService}.
 *
 * @author Seraph-coder
 * @since 19.11.2025
 */
public class UserServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    /**
     * Проверяет успешное добавление пользователя: пароль кодируется и
     * объект сохраняется.
     */
    @Test
    public void addUser_success() {
        User input = new User();
        input.setUsername("john");
        input.setPassword("plain");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain"))
                .thenReturn("encoded");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        User saved = userService.addUser(input);

        assertEquals("encoded", saved.getPassword());
        assertEquals("john", saved.getUsername());
        verify(passwordEncoder, times(1))
                .encode("plain");
        verify(userRepository, times(1))
                .saveAndFlush(any(User.class));
    }

    /**
     * Проверка на бросание ошибки при пустом имени пользователя
     * -> IllegalArgumentException (имя пользователя не должно быть пустым)
     */
    @Test
    public void addUser_emptyUsername_throws() {
        User input = new User();
        input.setUsername("  ");
        input.setPassword("p");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () ->
                        userService.addUser(input));
        assertEquals(
                "Иmя пользователя не должно быть пустым",
                ex.getMessage());
    }

    /**
     * Проверка на бросание ошибки при пустом пароле
     * -> IllegalArgumentException (пароль не должен быть пустым)
     */
    @Test
    public void addUser_emptyPassword_throws() {
        User input = new User();
        input.setUsername("john");
        input.setPassword("");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () ->
                        userService.addUser(input));
        assertEquals(
                "Пароль не должен быть пустым",
                ex.getMessage());
    }

    /**
     * Проверка на бросание ошибки при попытке добавить уже существующего пользователя
     * -> IllegalArgumentException (имя пользователя уже занято)
     */
    @Test
    public void addUser_existingUser_throws() {
        User input = new User();
        input.setUsername("john");
        input.setPassword("p");

        User existing = new User();
        existing.setUsername("john");

        when(userRepository
                .findUserByUsername("john"))
                .thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () ->
                        userService.addUser(input));
        assertEquals(
                "Пользователь с таким именем уже существует: john",
                ex.getMessage());
    }

    /**
     * Проверка на бросание ошибки при нарушении целостности данных в БД
     * -> IllegalArgumentException (возможно, email уже используется)
     */
    @Test
    public void addUser_saveThrowsDataIntegrityViolation_throwsIllegalArgument() {
        User input = new User();
        input.setUsername("john");
        input.setPassword("plain");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain"))
                .thenReturn("encoded");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "db constraint"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () ->
                        userService.addUser(input));
        assertEquals(
                "Ошибка сохранения: возможно, email уже используется",
                ex.getMessage());
    }
}
