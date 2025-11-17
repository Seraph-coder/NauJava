package ru.marchenko.NauJava.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.marchenko.NauJava.entity.Report;
import ru.marchenko.NauJava.entity.StatusEnum;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.repository.ReportRepository;
import ru.marchenko.NauJava.repository.TaskRepository;
import ru.marchenko.NauJava.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Сервис для управления отчетами.
 *
 * @author Seraph-coder
 * @since 16.11.2025
 */
@Service
public class ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final Executor taskExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportService(ReportRepository reportRepository, UserRepository userRepository,
                         TaskRepository taskRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Находит отчет по его идентификатору, если он существует,
     * иначе возвращает пустой Optional.
     */
    public Optional<Report> findById(long id) {
        return reportRepository.findById(id);
    }

    /**
     * Создает новый пустой отчет, запускает формирование
     * отчета и возвращает его идентификатор.
     */
    public Long create() {
        Report report = new Report();
        Report saved = reportRepository.save(report);
        CompletableFuture.runAsync(() -> generateReportWithThreads(saved.getId()), taskExecutor);
        return saved.getId();
    }

    /**
     * Получает содержимое отчета по его идентификатору, если он существует,
     * иначе возвращает пустой Optional.
     */
    public Optional<String> getContentById(long id) {
        return reportRepository.getContentById(id);
    }

    /**
     * Вспомогательный класс для хранения результата с временем выполнения.
     */
    private record TimedResult<T>(T result, long elapsedMs) {
    }

    /**
     * Генерирует отчёт: собирает метрики и список задач.
     * В базе сохраняются только метрики; HTML для отображения формируется в шаблоне Thymeleaf.
     */
    private Report generateReportWithThreads(Long reportId) {
        long totalStart = System.currentTimeMillis();
        Report report = reportRepository
                .findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Report with id: %s not found",
                                reportId)));

        try {
            CompletableFuture<TimedResult<Long>> usersFuture = CompletableFuture.supplyAsync(() -> {
                long s = System.currentTimeMillis();
                Long count = userRepository.count();
                return new TimedResult<>(count, System.currentTimeMillis() - s);
            }, taskExecutor);

            CompletableFuture<TimedResult<List<Task>>> tasksFuture = CompletableFuture.supplyAsync(() -> {
                long s = System.currentTimeMillis();
                List<Task> tasks = taskRepository.findAllByOrderByUserAsc();
                return new TimedResult<>(tasks, System.currentTimeMillis() - s);
            }, taskExecutor);

            CompletableFuture.allOf(usersFuture, tasksFuture).join();

            TimedResult<Long> usersResult = usersFuture.join();
            TimedResult<List<Task>> tasksResult = tasksFuture.join();

            long totalElapsed = System.currentTimeMillis() - totalStart;
            log.info("Report {} generated in {} ms", reportId, totalElapsed);

            report.setUsersCount(usersResult.result);
            report.setUsersTimeMs(usersResult.elapsedMs);
            report.setTasksCount(tasksResult.result.size());
            report.setTasksTimeMs(tasksResult.elapsedMs);
            report.setTotalTimeMs(totalElapsed);

            List<Map<String, Object>> tasksSummary = tasksResult.result.stream().map(t -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", t.getId());
                m.put("title", t.getTitle());
                String username = null;
                try {
                    if (t.getUser() != null) username = t.getUser().getUsername();
                } catch (Exception ignored) {
                }
                m.put("username", username);
                m.put("completed", t.isCompleted());
                return m;
            }).collect(Collectors.toList());

            Map<String, Object> contentObj = new HashMap<>();
            contentObj.put("usersCount", usersResult.result);
            contentObj.put("tasks", tasksSummary);

            String json = objectMapper.writeValueAsString(contentObj);
            report.setContent(json);
            report.setStatus(StatusEnum.COMPLETED);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - totalStart;

            report.setContent("Ошибка формирования отчёта: " + escapeHtml(e.getMessage()) +
                    " (время до ошибки: " + elapsed + " ms)");
            report.setStatus(StatusEnum.FAILED);
            log.error("Error generating report {}: {}", reportId, e.getMessage(), e);
        }
        return reportRepository.save(report);
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
