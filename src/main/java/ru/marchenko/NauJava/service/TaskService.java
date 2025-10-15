package ru.marchenko.NauJava.service;

import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.TaskStatus;

public interface TaskService {
    void createTask(Long id, String name);

    Task findById(Long id);

    void updateTask(Long id, String name, TaskStatus taskStatus);

    void deleteById(Long id);
}
