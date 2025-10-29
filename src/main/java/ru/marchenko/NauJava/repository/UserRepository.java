package ru.marchenko.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.marchenko.NauJava.entity.User;

/**
 * Репозиторий для управления сущностями User.
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User,Long> {
}
