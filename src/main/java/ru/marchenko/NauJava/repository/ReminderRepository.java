package ru.marchenko.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.marchenko.NauJava.entity.Reminder;

/**
 * Репозиторий для управления сущностями Reminder.
 */
@RepositoryRestResource
public interface ReminderRepository extends JpaRepository<Reminder,Long> {
}
