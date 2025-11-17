package ru.marchenko.NauJava.exception;

/**
 * Исключение, выбрасываемое при отсутствии задачи с заданными параметрами.
 */
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String title, String description) {
        super("Task with title " + title +
                " and description " + description + " not found");
    }
    public  TaskNotFoundException(String keyword) {
        super("Task with keyword " + keyword + " not found");
    }
}