package ru.marchenko.NauJava;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.marchenko.NauJava.entity.Category;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.User;
import ru.marchenko.NauJava.repository.CategoryRepository;
import ru.marchenko.NauJava.repository.TaskRepository;
import ru.marchenko.NauJava.repository.UserRepository;
import ru.marchenko.NauJava.service.TaskTransactionalService;
import ru.marchenko.NauJava.service.TaskTransactionalServiceImpl;
import ru.marchenko.NauJava.testutil.TaskBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для сервиса TaskTransactionalService.
 */
@DataJpaTest
@Import(TaskTransactionalServiceImpl.class)
public class TaskTransactionalServiceTests {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskTransactionalService taskTransactionalService;

    /**
     * Тест удаления задачи вместе с её подзадачами.
     * Проверяет, что после удаления родительской задачи,
     * все её подзадачи также удаляются из репозитория.
     */
    @Test
    void deleteTaskWithSubTasks_deletesParentAndChildren() {
        TaskBuilder parentBuilder = new TaskBuilder()
                .title("Parent").description("parent")
                .subTask(new TaskBuilder().title("Child1").description("c1"))
                .subTask(new TaskBuilder().title("Child2").description("c2"));

        User savedUser = userRepository.save(parentBuilder.getUser());
        Category category = parentBuilder.getCategory();
        category.setUser(savedUser);
        Category savedCat = categoryRepository.save(category);

        Task parent = parentBuilder.build();
        parent.setUser(savedUser);
        parent.setCategory(savedCat);

        Task savedParent = taskRepository.save(parent);
        Long pid = savedParent.getId();
        assertTrue(taskRepository.findById(pid).isPresent());

        assertEquals(2, savedParent.getSubTasks().size());

        taskTransactionalService.deleteTaskWithSubTasks(pid);

        assertFalse(taskRepository.findById(pid).isPresent());
        assertEquals(0, taskRepository.findAll().size());
    }

    /**
     * Тест удаления задачи без подзадач.
     * Проверяет, что при удалении задачи без подзадач,
     * остальные задачи в репозитории остаются нетронутыми.
     */
    @Test
    void deleteTaskWithoutChildren_onlyDeletesThatTask() {
        TaskBuilder b1 = new TaskBuilder().title("Solo1").description("one");
        TaskBuilder b2 = new TaskBuilder().title("Solo2").description("two");

        User savedUser = userRepository.save(b1.getUser());
        Category category = b1.getCategory();
        category.setUser(savedUser);
        Category savedCat = categoryRepository.save(category);

        Task t1 = b1.build();
        t1.setUser(savedUser);
        t1.setCategory(savedCat);

        b2.user(savedUser).category(savedCat);
        Task t2 = b2.build();
        t2.setUser(savedUser);
        t2.setCategory(savedCat);

        Task saved1 = taskRepository.save(t1);
        Task saved2 = taskRepository.save(t2);

        Long id1 = saved1.getId();
        Long id2 = saved2.getId();

        assertTrue(taskRepository.findById(id1).isPresent());
        assertTrue(taskRepository.findById(id2).isPresent());

        taskTransactionalService.deleteTaskWithSubTasks(id1);

        assertFalse(taskRepository.findById(id1).isPresent());
        assertTrue(taskRepository.findById(id2).isPresent());
    }
}
