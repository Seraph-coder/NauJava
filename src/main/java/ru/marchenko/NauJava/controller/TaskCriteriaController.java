package ru.marchenko.NauJava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.service.TaskCriteriaService;

import java.util.List;
/**
 * Контроллер для выполнения критерийных запросов к задачам.
 */
@RestController
@RequestMapping("/api/task-criteria")
public class TaskCriteriaController {
    private final TaskCriteriaService taskCriteriaService;
    public TaskCriteriaController(TaskCriteriaService taskCriteriaService) {
        this.taskCriteriaService = taskCriteriaService;
    }

    /**
     * Получает список задач по названию и описанию.
     */
    @GetMapping("/by-title-description")
    public ResponseEntity<List<Task>> getByTitleAndDescription(@RequestParam String title, @RequestParam String description) {
        return ResponseEntity.ok(taskCriteriaService.getByTitleAndDescription(title, description));
    }

    /**
     * Получает список задач по ключевому слову в названии или описании.
     */
    @GetMapping("/by-keyword")
    public ResponseEntity<List<Task>> getByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(taskCriteriaService.getByKeyword(keyword));
    }
}
