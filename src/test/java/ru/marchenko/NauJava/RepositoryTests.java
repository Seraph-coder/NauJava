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
import ru.marchenko.NauJava.repository.TaskRepositoryCriteriaAPI;
import ru.marchenko.NauJava.repository.TaskRepositoryCriteriaAPIImpl;
import ru.marchenko.NauJava.repository.UserRepository;
import ru.marchenko.NauJava.testutil.TaskBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для репозиториев TaskRepository и TaskRepositoryCriteriaAPI.
 */
@DataJpaTest
@Import(TaskRepositoryCriteriaAPIImpl.class)
public class RepositoryTests {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskRepositoryCriteriaAPI taskRepositoryCriteriaAPI;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Вспомогательный метод для создания и сохранения нескольких задач в базе данных.
     */
    void createAndSaveTasks() {
        TaskBuilder b1 = new TaskBuilder()
                .title("Сделать уроки")
                .description("Математика, русский");
        User savedUser1 = userRepository.save(b1.getUser());
        Category category = b1.getCategory();
        category.setUser(savedUser1);
        Category savedCat1 = categoryRepository.save(category);
        b1.user(savedUser1).category(savedCat1);
        taskRepository.saveAndFlush(b1.build());

        TaskBuilder b2 = new TaskBuilder().title("Подготовиться к диктанту")
                .description("Русский");
        User savedUser2 = userRepository.save(b2.getUser());
        Category category2 = b2.getCategory();
        category2.setUser(savedUser2);
        Category savedCat2 = categoryRepository.save(category2);
        b2.user(savedUser2).category(savedCat2);
        taskRepository.saveAndFlush(b2.build());

        TaskBuilder b3 = new TaskBuilder().title("Погулять")
                .description("С Витей");
        User savedUser3 = userRepository.save(b3.getUser());
        Category category3 = b3.getCategory();
        category3.setUser(savedUser3);
        Category savedCat3 = categoryRepository.save(category3);
        b3.user(savedUser3).category(savedCat3);
        taskRepository.saveAndFlush(b3.build());
    }

    /**
     * Тест поиска задачи по названию и описанию.
     */
    @Test
    void foundTaskByTitleAndDescription() {
        createAndSaveTasks();

        List<Task> found = taskRepository.findByTitleAndDescription("Сделать уроки", "Математика, русский");
        assertEquals(1, found.size());
        assertEquals("Сделать уроки", found.get(0).getTitle());
        assertEquals("Математика, русский", found.get(0).getDescription());
    }

    /**
     * Тест отрицательного поиска задачи по названию и описанию.
     */
    @Test
    void shouldNotFindTaskByTitleAndDescription() {
        createAndSaveTasks();

        List<Task> found = taskRepository.findByTitleAndDescription("Поиграть в компьютер", "Deadlock");
        assertEquals(0, found.size());
    }

    /**
     * Тест поиска задач по ключевому слову.
     */
    @Test
    void searchTasksByKeyword() {
        createAndSaveTasks();
        List<Task> found = taskRepository.searchByKeyword("русский");

        assertEquals(2, found.size());
    }

    /**
     * Тест отрицательного поиска задач по ключевому слову.
     */
    @Test
    void shouldNotFindTasksByKeyword() {
        createAndSaveTasks();
        List<Task> found = taskRepository.searchByKeyword("cj,frf");

        assertEquals(0, found.size());
    }

    /**
     * Тест поиска задачи по названию и описанию с использованием Criteria API.
     */
    @Test
    void criteria_foundTaskByTitleAndDescription() {
        createAndSaveTasks();
        List<Task> found = taskRepositoryCriteriaAPI.findByTitleAndDescription("Сделать уроки", "Математика, русский");
        assertEquals(1, found.size());
        assertEquals("Сделать уроки", found.get(0).getTitle());
        assertEquals("Математика, русский", found.get(0).getDescription());
    }

    /**
     * Тест отрицательного поиска задачи по названию и описанию с использованием Criteria API.
     */
    @Test
    void criteria_shouldNotFindTaskByTitleAndDescription() {
        createAndSaveTasks();
        List<Task> found = taskRepositoryCriteriaAPI.findByTitleAndDescription("Поиграть в компьютер", "Deadlock");
        assertEquals(0, found.size());
    }

    /**
     * Тест поиска задач по ключевому слову с использованием Criteria API.
     */
    @Test
    void criteria_searchTasksByKeyword() {
        createAndSaveTasks();
        List<Task> found = taskRepositoryCriteriaAPI.searchByKeyword("русский");
        assertEquals(2, found.size());
    }

    /**
     * Тест отрицательного поиска задач по ключевому слову с использованием Criteria API.
     */
    @Test
    void criteria_shouldNotFindTasksByKeyword() {
        createAndSaveTasks();
        List<Task> found = taskRepositoryCriteriaAPI.searchByKeyword("собака");
        assertEquals(0, found.size());
    }
}