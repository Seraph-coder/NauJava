package ru.marchenko.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.marchenko.NauJava.entity.User;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями User.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * Выполняет поиск пользователя по имени пользователя.
     */
    Optional<User> findUserByUsername(String userName);
}
