package ru.marchenko.NauJava.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.marchenko.NauJava.entity.Report;
import ru.marchenko.NauJava.entity.StatusEnum;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.User;
import ru.marchenko.NauJava.repository.ReportRepository;
import ru.marchenko.NauJava.repository.TaskRepository;
import ru.marchenko.NauJava.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для {@link ReportService}.
 *
 * @author Seraph-coder
 * @since 19.11.2025
 */
public class ReportServiceTest {
    private ReportRepository reportRepository;
    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private ReportService reportService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        reportRepository = Mockito.mock(ReportRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        taskRepository = Mockito.mock(TaskRepository.class);
        reportService = new ReportService(
                reportRepository, userRepository, taskRepository);
    }

    /**
     * Тест создания отчёта и поиска по-существующему и несуществующему id:
     * проверяет, что возвращается правильный отчёт или empty.
     */
    @Test
    public void createAndFindById_existingAndNonExistent() {
        Report savedReport = new Report();
        savedReport.setId(42L);

        when(reportRepository.save(any(Report.class)))
                .thenReturn(savedReport);
        when(reportRepository.findById(42L))
                .thenReturn(Optional.of(savedReport));
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        Long id = reportService.create();
        assertEquals(42L, id.longValue());

        Optional<Report> found = reportService.findById(42L);
        assertTrue(found.isPresent());
        assertEquals(42L, found.get().getId());

        Optional<Report> notFound = reportService.findById(99L);
        assertTrue(notFound.isEmpty());
        verify(reportRepository, times(1))
                .save(any(Report.class));
    }

    /**
     * Тест создания отчёта и получения содержимого по-существующему и
     * несуществующему id: проверяет правильное содержимое или empty.
     */
    @Test
    public void createAndGetContentById_existingAndNonExistent() {
        Report savedReport = new Report();
        savedReport.setId(42L);

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);
        when(reportRepository.getContentById(42L))
                .thenReturn(Optional.of(
                        "{\"usersCount\":5,\"tasks\":[]}"));
        when(reportRepository.getContentById(99L))
                .thenReturn(Optional.empty());

        Long id = reportService.create();
        assertEquals(42L, id.longValue());

        Optional<String> content = reportService.getContentById(42L);
        assertTrue(content.isPresent());
        assertEquals(
                "{\"usersCount\":5,\"tasks\":[]}", content.get());

        Optional<String> noContent = reportService.getContentById(99L);
        assertTrue(noContent.isEmpty());
        verify(reportRepository, times(1))
                .save(any(Report.class));
    }

    /**
     * Детальный тест успешного формирования отчёта: вызывает метод генерации,
     * проверяет, что статус = COMPLETED, usersCount и tasksCount
     * совпадают с ожиданием, а content точно равен ожидаемому JSON.
     */
    @Test
    public void generateReportWithThreads_success_setsContentAndStatusCompleted()
            throws Exception {
        when(userRepository.count()).thenReturn(2L);

        User u = new User();
        u.setUsername("alice");

        Task t1 = new Task();
        t1.setId(10L);
        t1.setTitle("Task A");
        t1.setUser(u);
        t1.setCompleted(true);

        Task t2 = new Task();
        t2.setId(11L);
        t2.setTitle("Task B");
        t2.setUser(null);
        t2.setCompleted(false);

        List<Task> tasks = List.of(t1, t2);
        when(taskRepository.findAllByOrderByUserAsc()).thenReturn(tasks);

        when(reportRepository.save(any(Report.class)))
                .thenAnswer(invocation -> {
                    Report r = invocation.getArgument(0);
                    if (r.getId() == null) {
                        r.setId(100L);
                    }
                    return r;
                });

        Long id = 100L;
        when(reportRepository.findById(id))
                .thenReturn(Optional.of(new Report()));

        Report result = reportService.generateReportWithThreads(id);

        assertNotNull(result);
        assertEquals(StatusEnum.COMPLETED, result.getStatus());
        assertEquals(2L, result.getUsersCount());
        assertEquals(2, result.getTasksCount());

        List<Map<String, Object>> tasksSummary = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 10L);
        map1.put("title", "Task A");
        map1.put("username", "alice");
        map1.put("completed", true);
        tasksSummary.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", 11L);
        map2.put("title", "Task B");
        map2.put("username", null);
        map2.put("completed", false);
        tasksSummary.add(map2);

        Map<String, Object> expected = new HashMap<>();
        expected.put("usersCount", 2L);
        expected.put("tasks", tasksSummary);

        String expectedJson = objectMapper.writeValueAsString(expected);

        assertEquals(expectedJson, result.getContent());
        verify(reportRepository, times(1))
                .save(any(Report.class));
    }

    /**
     * Тест проверки ветки ошибки при формировании отчёта: имитируем падение
     * при получении задач. Ожидаем, что статус отчёта станет FAILED и
     * содержимое будет соответствовать шаблону с
     * сообщением ошибки и временем.
     */
    @Test
    public void generateReportWithThreads_failure_setsStatusFailedAndContent() {
        when(userRepository.count()).thenReturn(1L);
        when(taskRepository.findAllByOrderByUserAsc())
                .thenThrow(new RuntimeException("boom"));

        when(reportRepository.save(any(Report.class)))
                .thenAnswer(invocation -> {
                    Report r = invocation.getArgument(0);
                    if (r.getId() == null) {
                        r.setId(200L);
                    }
                    return r;
                });

        Long id = 200L;
        when(reportRepository.findById(id))
                .thenReturn(Optional.of(new Report()));

        Report result = reportService.generateReportWithThreads(id);

        assertNotNull(result);
        assertEquals(StatusEnum.FAILED, result.getStatus());

        String content = result.getContent();
        assertNotNull(content);

        assertTrue(content.matches(
                "^Ошибка формирования отчёта: .* \\(время до ошибки: \\d+ ms\\)$"));
        verify(reportRepository, times(1))
                .save(any(Report.class));
    }
}

