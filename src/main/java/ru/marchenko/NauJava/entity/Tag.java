package ru.marchenko.NauJava.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность Tag.
 * Представляет категорию с полями для связанного пользователя, названия,
 * описания, а также временными метками создания и обновления.
 */
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Task> tasks = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Tag() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        if (this.tasks != null) {
            for (Task t : new ArrayList<>(this.tasks)) {
                removeTask(t);
            }
        }
        if (tasks != null) {
            for (Task t : tasks) {
                addTask(t);
            }
        }
    }

    public void addTask(Task task) {
        if (task == null) return;
        if (!this.tasks.contains(task)) {
            this.tasks.add(task);
        }
        if (task.getTags() == null || !task.getTags().contains(this)) {
            task.getTags().add(this);
        }
    }

    public void removeTask(Task task) {
        if (task == null) return;
        if (this.tasks.remove(task)) {
            if (task.getTags() != null && task.getTags().contains(this)) {
                task.getTags().remove(this);
            }
        }
    }
}