package ru.marchenko.NauJava.repository;

import org.springframework.stereotype.Component;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.exception.TaskNotFoundException;

import java.util.Iterator;
import java.util.List;

/**
 * Репозиторий для управления задачами (Task).
 * Реализует интерфейс CrudRepository для выполнения операций создания,
 * чтения, обновления и удаления задач.
 */
@Component
public class TaskRepository implements  CrudRepository<Task,Long> {
    private final List<Task> taskContainer;

    public TaskRepository(List<Task> taskContainer) {
        this.taskContainer = taskContainer;
    }

    /**
     * Создает новую задачу и добавляет ее в контейнер задач.
     *
     * @param entity Задача для создания.
     */
    @Override
    public void create(Task entity) {
        taskContainer.add(entity);
    }

    /**
     * Читает задачу по ее идентификатору.
     *
     * @param id Идентификатор задачи.
     * @return Задача с указанным идентификатором.
     * @throws TaskNotFoundException Если задача с указанным идентификатором не найдена.
     */
    @Override
    public Task read(Long id) throws TaskNotFoundException {
        for (Task task : taskContainer) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        throw new TaskNotFoundException(id);
    }

    /**
     * Обновляет существующую задачу в контейнере задач.
     *
     * @param entity Задача с обновленными данными.
     */
    @Override
    public void update(Task entity) {
        for (int i = 0; i < taskContainer.size(); i++) {
            if (taskContainer.get(i).getId().equals(entity.getId())) {
                taskContainer.set(i, entity);
                return;
            }
        }
    }

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param id Идентификатор задачи для удаления.
     */
    @Override
    public void delete(Long id) {
        Iterator<Task> iterator = taskContainer.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getId().equals(id)) {
                iterator.remove();
                return;
            }
        }
    }
}
