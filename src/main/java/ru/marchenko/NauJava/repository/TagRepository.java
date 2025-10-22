package ru.marchenko.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.marchenko.NauJava.entity.Tag;

/**
 * Репозиторий для управления сущностями Tag.
 */
public interface TagRepository extends CrudRepository<Tag, Long> {
}
