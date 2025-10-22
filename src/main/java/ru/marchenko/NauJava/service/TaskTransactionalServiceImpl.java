package ru.marchenko.NauJava.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.marchenko.NauJava.repository.TaskRepository;

@Service
public class TaskTransactionalServiceImpl implements TaskTransactionalService {
    private final TaskRepository taskRepository;

    public TaskTransactionalServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void deleteTaskWithSubTasks(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
