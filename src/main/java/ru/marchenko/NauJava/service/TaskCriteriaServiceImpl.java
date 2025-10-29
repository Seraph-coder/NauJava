package ru.marchenko.NauJava.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.exception.TaskNotFoundException;
import ru.marchenko.NauJava.repository.TaskRepositoryCustom;
import ru.marchenko.NauJava.repository.TaskRepository;

import java.util.List;
/**
 * Реализация сервиса для выполнения критерийных запросов к задачам.
 */
@Service
public class TaskCriteriaServiceImpl implements TaskCriteriaService {
    private final TaskRepositoryCustom taskRepositoryImpl;

    public TaskCriteriaServiceImpl(TaskRepositoryCustom taskRepositoryImpl) {
        this.taskRepositoryImpl = taskRepositoryImpl;
    }

    /**
     * Получает список задач по названию и описанию.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> getByTitleAndDescription(String title, String description) {
        List<Task> tasks = taskRepositoryImpl.findByTitleAndDescription(title, description);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException(title, description);
        }
        return tasks;
    }

    /**
     * Получает список задач по ключевому слову в названии или описании.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> getByKeyword(String keyword) {
        List<Task> tasks = taskRepositoryImpl.searchByKeyword(keyword);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException(keyword);
        }
        return tasks;
    }
}
