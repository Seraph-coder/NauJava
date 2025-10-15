package ru.marchenko.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.TaskStatus;
import ru.marchenko.NauJava.repository.TaskRepository;

@Service
public class TaskServiceImpl implements  TaskService {
    private final  TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void createTask(Long id, String name) {
        Task newTask = new Task();
        newTask.setId(id);
        newTask.setName(name);
        taskRepository.create(newTask);
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.read(id);
    }

    @Override
    public void updateTask(Long id, String name, TaskStatus taskStatus) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setTaskStatus(taskStatus);
        taskRepository.update(task);
    }

    @Override
    public void deleteById(Long id) {
        taskRepository.delete(id);
    }
}
