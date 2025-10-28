package ru.marchenko.NauJava.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность Task.
 * Представляет задачу с возможностью иерархии подзадач,
 * связью с пользователем, категорией, напоминаниями и тегами.
 */
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> subTasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tasks_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityEnum priority = PriorityEnum.MEDIUM;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Task() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        if (this.parentTask == parentTask) {
            return;
        }
        if (this.parentTask != null) {
            Task oldParent = this.parentTask;
            this.parentTask = null;
            if (oldParent.getSubTasks() != null && oldParent.getSubTasks().contains(this)) {
                oldParent.getSubTasks().remove(this);
            }
        }
        this.parentTask = parentTask;
        if (parentTask != null && !parentTask.getSubTasks().contains(this)) {
            parentTask.getSubTasks().add(this);
        }
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public void addSubTask(Task child) {
        if (child == null) return;
        if (!this.subTasks.contains(child)) {
            this.subTasks.add(child);
        }
        if (child.getParentTask() != this) {
            child.setParentTask(this);
        }
    }

    public void removeSubTask(Task child) {
        if (child == null) return;
        if (this.subTasks.remove(child)) {
            if (child.getParentTask() == this) {
                child.setParentTask(null);
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        if (this.reminders != null) {
            for (Reminder r : new ArrayList<>(this.reminders)) {
                removeReminder(r);
            }
        }
        if (reminders != null) {
            for (Reminder r : reminders) {
                addReminder(r);
            }
        }
    }

    public void addReminder(Reminder reminder) {
        if (reminder == null) return;
        if (!this.reminders.contains(reminder)) {
            this.reminders.add(reminder);
        }
        if (reminder.getTask() != this) {
            reminder.setTask(this);
        }
    }

    public void removeReminder(Reminder reminder) {
        if (reminder == null) return;
        if (this.reminders.remove(reminder)) {
            if (reminder.getTask() == this) {
                reminder.setTask(null);
            }
        }
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        if (this.tags != null) {
            for (Tag t : new ArrayList<>(this.tags)) {
                removeTag(t);
            }
        }
        if (tags != null) {
            for (Tag t : tags) {
                addTag(t);
            }
        }
    }

    public void addTag(Tag tag) {
        if (tag == null) return;
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
        if (tag.getTasks() == null || !tag.getTasks().contains(this)) {
            tag.getTasks().add(this);
        }
    }

    public void removeTag(Tag tag) {
        if (tag == null) return;
        if (this.tags.remove(tag)) {
            if (tag.getTasks() != null && tag.getTasks().contains(this)) {
                tag.getTasks().remove(this);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority;
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