package ru.marchenko.NauJava.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Сущность Reminder.
 * Представляет напоминание с полями для связанной задачи, времени напоминания,
 * заметки, статуса отправки, а также временными метками создания и обновления
 */
@Entity
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(nullable = false)
    private LocalDateTime remindAt;

    @Column
    private String note;

    @Column(nullable = false)
    private boolean sent = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Reminder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        if (this.task == task) return;
        if (this.task != null) {
            Task previous = this.task;
            this.task = null;
            if (previous.getReminders() != null && previous.getReminders().contains(this)) {
                previous.getReminders().remove(this);
            }
        }
        this.task = task;
        if (task != null && !task.getReminders().contains(this)) {
            task.getReminders().add(this);
        }
    }

    public LocalDateTime getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(LocalDateTime remindAt) {
        this.remindAt = remindAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
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
}
