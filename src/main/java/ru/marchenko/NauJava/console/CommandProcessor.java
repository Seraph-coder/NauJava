package ru.marchenko.NauJava.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.TaskStatus;
import ru.marchenko.NauJava.exception.TaskNotFoundException;
import ru.marchenko.NauJava.service.TaskService;
import java.util.Arrays;

/**
    * Класс CommandProcessor отвечает за обработку команд, введенных пользователем
    * в консольном приложении. Он взаимодействует с сервисом TaskService для выполнения
    * операций CRUD (создание, чтение, обновление, удаление) над задачами.
 */
@Configuration
public class CommandProcessor {
    private final TaskService taskService;

    /**
     * Конструктор класса CommandProcessor, который принимает TaskService
     * для выполнения операций над задачами.
     *
     * @param taskService Сервис для управления задачами.
     */
    @Autowired
    public CommandProcessor(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Обрабатывает введенную пользователем команду.
     * Поддерживаемые команды: create (c), read (r), update (u), delete (d).
     *
     * @param command Команда, введенная пользователем.
     */
    public void processCommand(String command) {
        String[] parts = command.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isEmpty()) {
            System.out.println("Введите команду.");
            return;
        }

        String action = parts[0];

        switch (action.toLowerCase()) {
            case "create", "c":
                if (parts.length < 3) {
                    System.out.println("Используйте: create (c) <id> <name>");
                    return;
                }
                try {
                    Long createId = Long.parseLong(parts[1]);

                    try {
                        taskService.findById(createId);
                        System.out.println("Задача с таким ID уже существует.");
                        return;
                    } catch (TaskNotFoundException e) {
                        // Задача не существует, можно создать
                    }
                    String createName = String.join(" ",
                            Arrays.copyOfRange(parts, 2, parts.length));

                    taskService.createTask(createId, createName);
                    System.out.println("Задача создана.");
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("ID должен быть целым числом.");
                    return;
                }

            case "read", "r":
                if (parts.length != 2) {
                    System.out.println("Используйте: read (r) <task id>");
                    return;
                }
                try {
                    Long readId = Long.parseLong(parts[1]);

                    try {
                        Task task = taskService.findById(readId);
                        System.out.println("Задача найдена: " + task.getName()
                                +" " +task.getTaskStatus());
                    } catch (TaskNotFoundException e) {
                        System.out.println("Задача не найдена.");
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("ID должен быть числом.");
                    return;
                }

            case "update", "u":
                if (parts.length < 3) {
                    System.out.println("Используйте: update (u) <task id> <new name> " +
                            "<task status (опционально)>");
                    return;
                }
                try {
                    Long taskId = Long.parseLong(parts[1]);

                    try {
                        taskService.findById(taskId);
                    } catch (TaskNotFoundException e) {
                        System.out.println("Задача не найдена.");
                        return;
                    }

                    String updateName;

                    TaskStatus taskStatus = taskService.findById(taskId).getTaskStatus();

                    String lastPart = parts[parts.length - 1].toUpperCase();
                        try {
                            taskStatus = TaskStatus.valueOf(lastPart);
                            updateName = String.join(" ",
                                    Arrays.copyOfRange(parts, 2, parts.length - 1));

                        } catch (IllegalArgumentException e) {
                            updateName = String.join(" ",
                                    Arrays.copyOfRange(parts, 2, parts.length));
                        }
                    taskService.updateTask(taskId, updateName, taskStatus);
                    System.out.println("Задача обновлена.");
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("ID должен быть числом.");
                    return;
                }

            case "delete", "d":
                if (parts.length != 2) {
                    System.out.println("Используйте: delete (d) <task id>");
                    return;
                }
                try {
                    Long deleteId = Long.parseLong(parts[1]);
                    taskService.findById(deleteId);

                    taskService.deleteById(deleteId);
                    System.out.println("Задача удалена.");
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("ID должен быть числом.");
                    return;
                } catch (TaskNotFoundException e) {
                    System.out.println("Задача с таким ID не найдена.");
                    return;
                }

            default:
                System.out.println("""
                        Введена неизвестная команда...
                        Доступные команды:
                        create (c) <id> <name> - создать задачу
                        read (r) <task id> - прочитать задачу по ID
                        update (u) <task id> <new name> <task status (опционально)>
                        - обновить имя (и при желании статус) задачи по ID
                        delete (d) <task id> - удалить задачу по ID
                       \s""");
                break;
        }
    }
}
