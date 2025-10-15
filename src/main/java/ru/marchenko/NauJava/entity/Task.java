package ru.marchenko.NauJava.entity;

/**
 * Класс задачи с полями id, name и taskStatus.
 * taskStatus по умолчанию устанавливается в BACKLOG.
 * Содержит геттеры и сеттеры для всех полей.
 */
public class Task {
    private Long id;

    private  String name;

    private TaskStatus taskStatus = TaskStatus.BACKLOG;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
