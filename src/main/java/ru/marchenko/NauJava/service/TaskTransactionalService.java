package ru.marchenko.NauJava.service;

public interface TaskTransactionalService {
    void deleteTaskWithSubTasks(Long taskId);
}
