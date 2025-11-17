package ru.marchenko.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.marchenko.NauJava.entity.Task;

import java.util.List;

/**
 * Репозиторий для управления сущностями Task.
 */
@RepositoryRestResource
public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * Выполняет поиск задач по названию и описанию.
     */
    List<Task> findByTitleAndDescription(String title, String description);

    /**
     * Выполняет поиск задач по ключевому слову в названии или описании (регистронезависимо).
     */
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.category LEFT JOIN FETCH t.user WHERE LOWER(t.title) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(t.description) LIKE CONCAT('%', LOWER(:keyword), '%')")
    List<Task> searchByKeyword(String keyword);

    /**
     * Находит все задачи, отсортированные по имени пользователя в порядке возрастания.
     */
    List<Task> findAllByOrderByUserAsc();
}
