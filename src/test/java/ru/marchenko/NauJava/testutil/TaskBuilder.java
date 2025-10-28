package ru.marchenko.NauJava.testutil;

import ru.marchenko.NauJava.entity.Category;
import ru.marchenko.NauJava.entity.PriorityEnum;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Билдер для создания объектов Task с различными конфигурациями для тестирования.
 */
public class TaskBuilder {
    private String title = "default title";
    private String description = "default description";
    private User creator;
    private Category category;
    private PriorityEnum priority = PriorityEnum.LOW;
    private List<Task> subTasks = new ArrayList<>();

    /**
     * Инициализирует билдер с уникальным пользователем и категорией.
     */
    public TaskBuilder() {
        String uniq = UUID.randomUUID().toString().substring(0, 8);
        this.creator = createSimpleUser(uniq);
        this.category = createSimpleCategory(uniq, this.creator);
    }

    /**
     * Создает простого пользователя с уникальными данными.
     * @param uniq уникальная строка для генерации данных пользователя
     */
    private User createSimpleUser(String uniq) {
        User u = new User();
        u.setUserName("testuser_" + uniq);
        u.setEmail("testuser_" + uniq + "@example.com");
        u.setPasswordHash("pass" + uniq);
        return u;
    }

    /**
     * Создает простую категорию с уникальным именем и владельцем.
     * @param uniq уникальная строка для генерации имени категории
     * @param owner владелец категории
     */
    private Category createSimpleCategory(String uniq, User owner) {
        Category c = new Category();
        c.setName("default-category-" + uniq);
        c.setUser(owner);
        return c;
    }

    /**
     * Устанавливает заголовок задачи.
     */
    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Устанавливает описание задачи.
     */
    public TaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Устанавливает приоритет задачи.
     */
    public TaskBuilder priority(PriorityEnum priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Устанавливает пользователя-создателя задачи.
     */
    public TaskBuilder user(User user) {
        this.creator = user;
        if (this.category != null && this.category.getUser() == null) {
            this.category.setUser(user);
        }
        return this;
    }

    /**
     * Устанавливает категорию задачи.
     */
    public TaskBuilder category(Category category) {
        this.category = category;
        if (this.category.getUser() == null) {
            this.category.setUser(this.creator);
        }
        return this;
    }

    /**
     * Добавляет подзадачу к текущей задаче.
     */
    public TaskBuilder subTask(Task child) {
        if (child == null) return this;
        this.subTasks.add(child);
        return this;
    }

    /**
     * Добавляет подзадачу, созданную с помощью другого билдера.
     */
    public TaskBuilder subTask(TaskBuilder childBuilder) {
        if (childBuilder == null) return this;
        childBuilder.user(this.creator);
        childBuilder.category(this.category);
        this.subTasks.add(childBuilder.build());
        return this;
    }

    public User getUser() {
        return creator;
    }

    public Category getCategory() {
        return category;
    }

    /**
     * Строит и возвращает объект Task с заданными параметрами.
     */
    public Task build() {
        Task t = new Task();
        t.setTitle(title);
        t.setDescription(description);
        t.setUser(creator);
        t.setCategory(category);
        t.setPriority(priority);
        if (subTasks != null) {
            for (Task child : subTasks) {
                if (child.getUser() == null) child.setUser(creator);
                if (child.getCategory() == null) child.setCategory(category);
                t.addSubTask(child);
            }
        }
        return t;
    }
}
