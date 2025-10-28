package ru.marchenko.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.marchenko.NauJava.entity.Reminder;

/**
 * Репозиторий для управления сущностями Reminder.
 */
public interface ReminderRepository extends CrudRepository<Reminder,Long> {
}
