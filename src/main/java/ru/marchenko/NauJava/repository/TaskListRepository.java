package ru.marchenko.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.marchenko.NauJava.entity.TaskList;

/**
 * Репозиторий для управления сущностями TaskList.
 */
public interface TaskListRepository extends CrudRepository<TaskList,Long> {
}
