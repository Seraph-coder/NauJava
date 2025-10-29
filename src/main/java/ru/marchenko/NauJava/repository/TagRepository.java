package ru.marchenko.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.marchenko.NauJava.entity.Tag;

/**
 * Репозиторий для управления сущностями Tag.
 */
@RepositoryRestResource
public interface TagRepository extends JpaRepository<Tag, Long> {
}
