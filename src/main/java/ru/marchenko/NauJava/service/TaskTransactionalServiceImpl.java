package ru.marchenko.NauJava.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.repository.TaskRepository;

/**
 * Реализация сервиса для выполнения транзакционных операций с задачами.
 */
@Service
public class TaskTransactionalServiceImpl implements TaskTransactionalService {
    private final TaskRepository taskRepository;

    public TaskTransactionalServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Удаляет задачу вместе со всеми её подзадачами, если таковые имеются,
     * а также корректно обновляет связи с родительской задачей,
     * иначе просто удаляет задачу.
     */
    @Override
    @Transactional
    public void deleteTaskWithSubTasks(Long taskId) {
        taskRepository.findById(taskId).ifPresent(task -> {
            if (task.getParentTask() != null) {
                Task parent = task.getParentTask();
                parent.removeSubTask(task);
                taskRepository.save(parent);
            }
            taskRepository.delete(task);
        });
    }
}
