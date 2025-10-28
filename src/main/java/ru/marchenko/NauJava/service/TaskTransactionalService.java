package ru.marchenko.NauJava.service;

/**
 * Сервис для выполнения транзакционных операций с задачами.
 */
public interface TaskTransactionalService {
    /**
     * Удаляет задачу вместе со всеми её подзадачами.
     */
    void deleteTaskWithSubTasks(Long taskId);
}
