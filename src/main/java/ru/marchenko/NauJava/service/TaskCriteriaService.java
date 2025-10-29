package ru.marchenko.NauJava.service;

import ru.marchenko.NauJava.entity.Task;

import java.util.List;

/**
 * Интерфейс сервиса для выполнения критерийных запросов к задачам.
 */
public interface TaskCriteriaService {
    /**
     * Получает список задач по названию и описанию.
     */
    List<Task> getByTitleAndDescription(String title, String description);

    /**
     * Получает список задач по ключевому слову в названии или описании.
     */
    List<Task> getByKeyword(String keyword);
}

