package ru.marchenko.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.marchenko.NauJava.entity.Category;

/**
 * Репозиторий для управления сущностями TaskList.
 */
public interface CategoryRepository extends CrudRepository<Category,Long> {
}
