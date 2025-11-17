package ru.marchenko.NauJava.entity;

/**
 * Перечисление статусов отчетов.
 *
 * @author Seraph-coder
 * @since 16.11.2025
 */
public enum StatusEnum {
    /**
     * Отчет создан.
     */
    CREATED,
    /**
     * Отчет сформирован.
     */
    COMPLETED,
    /**
     * Ошибка при создании отчета.
     */
    FAILED
}
