package ru.marchenko.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.marchenko.NauJava.entity.SubTask;

/**
 * Репозиторий для управления сущностями SubTask.
 */
public interface SubTaskRepository extends CrudRepository<SubTask, Long> {
}
