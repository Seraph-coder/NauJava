package ru.marchenko.NauJava.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.marchenko.NauJava.entity.Report;
import ru.marchenko.NauJava.repository.TaskRepository;
import ru.marchenko.NauJava.service.ReportService;

import java.util.*;

/**
 * Контроллер для управления отчетами.
 *
 * @author Seraph-coder
 * @since 17.11.2025
 */
@Controller
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;
    private final TaskRepository taskRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportController(ReportService reportService, TaskRepository taskRepository) {
        this.reportService = reportService;
        this.taskRepository = taskRepository;
    }

    /**
     * Создает новый отчет и перенаправляет на страницу просмотра отчёта.
     */
    @PostMapping("/create")
    public String createReport() {
        Long id = reportService.create();
        return "redirect:/reports/" + id;
    }

    /**
     * GET: удобный маршрут для ручного перехода в браузере по /reports/create
     */
    @GetMapping("/create")
    public String createReportGet() {
        Long id = reportService.create();
        return "redirect:/reports/" + id;
    }

    /**
     * Отображает страницу отчёта (Thymeleaf). Ограничиваем id только цифрами, чтобы избежать конфликтов с путями типа /create.
     */
    @GetMapping("/{id:\\d+}")
    public String getReport(@PathVariable Long id, Model model) {
        Optional<Report> opt = reportService.findById(id);
        if (opt.isEmpty()) {
            model.addAttribute("report", null);
            model.addAttribute("tasks", List.of());
            return "report";
        }
        Report r = opt.get();
        model.addAttribute("report", r);

        List<Map<String, Object>> tasksList = new ArrayList<>();
        String content = r.getContent();
        if (content != null && content.trim().startsWith("{")) {
            try {
                Map<String, Object> parsed = objectMapper.readValue(content, new TypeReference<>() {
                });
                Object tasksObj = parsed.get("tasks");
                if (tasksObj instanceof List) {
                    tasksList = (List<Map<String, Object>>) tasksObj;
                }
            } catch (Exception e) {
                tasksList = mapTasks(taskRepository.findAllByOrderByUserAsc());
            }
        } else {
            tasksList = mapTasks(taskRepository.findAllByOrderByUserAsc());
        }

        model.addAttribute("tasks", tasksList);
        return "report";
    }

    /**
     * Преобразует список задач в список отображаемых карт.
     */
    private List<Map<String, Object>> mapTasks(List<?> tasks) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object o : tasks) {
            try {
                if (o instanceof Map) {
                    out.add((Map<String, Object>) o);
                    continue;
                }
                // Use reflection-lite: assume Task has getId,getTitle,getUser(),isCompleted
                java.lang.reflect.Method getId = o.getClass().getMethod("getId");
                java.lang.reflect.Method getTitle = o.getClass().getMethod("getTitle");
                java.lang.reflect.Method isCompleted = null;
                try {
                    isCompleted = o.getClass().getMethod("isCompleted");
                } catch (NoSuchMethodException ignored) {
                }
                java.lang.reflect.Method getUser = null;
                try {
                    getUser = o.getClass().getMethod("getUser");
                } catch (NoSuchMethodException ignored) {
                }

                Object id = getId.invoke(o);
                Object title = getTitle.invoke(o);
                Object userObj = getUser != null ? getUser.invoke(o) : null;
                Object username = null;
                if (userObj != null) {
                    try {
                        java.lang.reflect.Method getUsername = userObj.getClass().getMethod("getUsername");
                        username = getUsername.invoke(userObj);
                    } catch (Exception ignored) {
                    }
                }
                Object completed = false;
                if (isCompleted != null) completed = isCompleted.invoke(o);

                Map<String, Object> m = new HashMap<>();
                m.put("id", id);
                m.put("title", title);
                m.put("username", username);
                m.put("completed", completed);
                out.add(m);
            } catch (Exception e) {
                // ignore individual mapping errors
            }
        }
        return out;
    }

    /**
     * Возвращает JSON с текущим статусом отчёта. Используется для опроса с клиента.
     */
    @GetMapping("/{id:\\d+}/status")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable Long id) {
        Optional<Report> opt = reportService.findById(id);
        return opt.map(report -> ResponseEntity.ok(Map.of("status", report.getStatus().name()))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "NOT_FOUND")));
    }

    /**
     * Возвращает JSON с содержимым отчёта. Используется для получения содержимого отчёта в формате JSON.
     */
    @GetMapping("/{id:\\d+}/content")
    @ResponseBody
    public ResponseEntity<Report> getReportContent(@PathVariable Long id) {
        Optional<Report> opt = reportService.findById(id);
        return opt.map(report -> ResponseEntity.ok(report)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
