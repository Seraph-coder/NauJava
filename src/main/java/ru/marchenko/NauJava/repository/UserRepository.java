package ru.marchenko.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.marchenko.NauJava.entity.User;

/**
 * Репозиторий для управления сущностями User.
 */
public interface UserRepository extends CrudRepository<User,Long> {
}
