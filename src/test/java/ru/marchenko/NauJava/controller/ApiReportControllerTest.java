package ru.marchenko.NauJava.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.marchenko.NauJava.entity.Report;
import ru.marchenko.NauJava.entity.StatusEnum;
import ru.marchenko.NauJava.repository.TaskRepository;
import ru.marchenko.NauJava.service.ReportService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API тесты для {@link ReportController}, используют MockMVC.
 */
public class ApiReportControllerTest {
    private ReportService reportService;
    private TaskRepository taskRepository;
    private MockMvc mockMvc;

    /**
     * Создаёт MockMvc с тестовым контроллером.
     */
    @BeforeEach
    public void setUp() {
        reportService = Mockito.mock(ReportService.class);
        taskRepository = Mockito.mock(TaskRepository.class);
        ReportController controller = new ReportController(
                reportService, taskRepository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    /**
     * Тест получения статуса отчёта по его идентификатору.
     * Проверяет успешный ответ.
     */
    @Test
    public void getStatus_existingReport_returnsOk() throws Exception {
        Report r = new Report();
        r.setId(10L);
        r.setStatus(ru.marchenko.NauJava.entity.StatusEnum.COMPLETED);

        when(reportService.findById(10L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/reports/10/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .value("COMPLETED"));
    }

    /**
     * Тест получения статуса несуществующего отчёта.
     * Проверяет ответ 404 Not Found.
     */
    @Test
    public void getStatus_nonExistingReport_returns404() throws Exception {
        when(reportService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reports/99/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .value("NOT_FOUND"));
    }

    /**
     * Тест получения содержимого отчёта со статусом COMPLETED.
     */
    @Test
    public void getReportContent_completedReport_returnsContent()
            throws Exception {
        Report r = new Report();
        r.setId(20L);
        r.setStatus(ru.marchenko.NauJava.entity.StatusEnum.COMPLETED);
        r.setContent("Report content");
        when(reportService.findById(20L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/reports/20/content")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content")
                        .value("Report content"))
                .andExpect(jsonPath("$.status")
                        .value("COMPLETED"));
    }

    /**
     * Тест получения содержимого отчёта со статусом FAILED.
     */
    @Test
    public void getReportContent_errorReport_returnsFailed()
            throws Exception {
        Report r = new Report();
        r.setId(21L);
        r.setStatus(StatusEnum.FAILED);
        r.setContent("Ошибка при формировании отчёта");
        when(reportService.findById(21L)).thenReturn(Optional.of(r));

        mockMvc.perform(get("/reports/21/content")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content")
                        .value("Ошибка при формировании отчёта"))
                .andExpect(jsonPath("$.status")
                        .value("FAILED"));
    }

    /**
     * Тест получения содержимого несуществующего отчёта.
     */
    @Test
    public void getReportContent_nonExistingReport_returns404()
            throws Exception {
        when(reportService.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reports/123/content")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
