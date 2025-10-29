package ru.marchenko.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.marchenko.NauJava.entity.Category;

/**
 * Репозиторий для управления сущностями Category.
 */
@RepositoryRestResource
public interface CategoryRepository extends JpaRepository<Category,Long> {
}
