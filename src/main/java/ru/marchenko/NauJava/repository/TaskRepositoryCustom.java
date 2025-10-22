package ru.marchenko.NauJava.repository;

import ru.marchenko.NauJava.entity.Task;

import java.util.List;

/**
 * Пользовательский репозиторий для выполнения сложных запросов к сущности User.
 */
public interface TaskRepositoryCustom {
    /**
     * Находит задачи по названию и описанию.
     */
    List<Task> findByTitleAndDescription(String title, String description);

    /**
     * Выполняет поиск задач по ключевому слову в названии или описании.
     */
    List<Task> searchByKeyword(String keyword);
}
