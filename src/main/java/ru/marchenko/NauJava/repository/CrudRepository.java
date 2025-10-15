package ru.marchenko.NauJava.repository;

/**
 * Универсальный интерфейс для CRUD операций.
 *
 * @param <T>  Тип сущности.
 * @param <ID> Тип идентификатора сущности.
 */
public interface CrudRepository <T, ID> {

    void create(T entity);

    T read(ID id);

    void update(T entity);

    void delete(ID id);
}
