package ru.marchenko.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.marchenko.NauJava.entity.Report;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями Report.
 *
 * @author Seraph-coder
 * @since 16.11.2025
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    /**
     * Получает содержимое отчета по его идентификатору.
     */
    Optional<String> getContentById(long id);

    /**
     * Находит отчет по его идентификатору.
     */
    Optional<Report> findById(long id);
}
