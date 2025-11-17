package ru.marchenko.NauJava.entity;

import jakarta.persistence.*;

/**
 * Сущность отчета.
 *
 * @author Seraph-coder
 * @since 16.11.2025
 */
@Entity
@Table(name = "reports")
public class Report {
    /**
     * Идентификатор отчета.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Статус отчета.
     */
    private StatusEnum status = StatusEnum.CREATED;

    /**
     * Содержимое отчета.
     */
    @Column(columnDefinition = "text")
    private String content;

    /**
     * Вспомогательные метрики юзеров, задач и времени для отчёта
     */
    private Long usersCount = 0L;
    private Integer tasksCount = 0;
    private Long usersTimeMs = 0L;
    private Long tasksTimeMs = 0L;
    private Long totalTimeMs = 0L;

    public Report() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(Long usersCount) {
        this.usersCount = usersCount;
    }

    public Integer getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(Integer tasksCount) {
        this.tasksCount = tasksCount;
    }

    public Long getUsersTimeMs() {
        return usersTimeMs;
    }

    public void setUsersTimeMs(Long usersTimeMs) {
        this.usersTimeMs = usersTimeMs;
    }

    public Long getTasksTimeMs() {
        return tasksTimeMs;
    }

    public void setTasksTimeMs(Long tasksTimeMs) {
        this.tasksTimeMs = tasksTimeMs;
    }

    public Long getTotalTimeMs() {
        return totalTimeMs;
    }

    public void setTotalTimeMs(Long totalTimeMs) {
        this.totalTimeMs = totalTimeMs;
    }
}
