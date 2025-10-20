package ru.marchenko.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.TaskStatus;
import ru.marchenko.NauJava.exception.TaskNotFoundException;
import ru.marchenko.NauJava.repository.TaskRepository;

/**
 * Сервисный класс для управления задачами.
 * Реализует интерфейс TaskService и использует TaskRepository
 * для выполнения операций над задачами.
 */
@Service
public class TaskServiceImpl implements  TaskService {
    private final  TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Создает новую задачу с указанным идентификатором и именем.
     */
    @Override
    public void createTask(Long id, String name) {
        Task newTask = new Task();
        newTask.setId(id);
        newTask.setName(name);
        taskRepository.create(newTask);
    }

    /**
     * Находит задачу по ее идентификатору.
     */
    @Override
    public Task findById(Long id) {
        try {
            return taskRepository.read(id);
        } catch (TaskNotFoundException e) {
            throw new TaskNotFoundException(id);
        }

    }

    /**
     * Обновляет задачу с указанным идентификатором, именем и статусом.
     */
    @Override
    public void updateTask(Long id, String name, TaskStatus taskStatus) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setTaskStatus(taskStatus);
        taskRepository.update(task);
    }

    /**
     * Удаляет задачу по ее идентификатору.
     */
    @Override
    public void deleteById(Long id) {
        taskRepository.delete(id);
    }
}
