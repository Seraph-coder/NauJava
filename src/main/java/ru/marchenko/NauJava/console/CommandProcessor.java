package ru.marchenko.NauJava.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.marchenko.NauJava.entity.Task;
import ru.marchenko.NauJava.entity.TaskStatus;
import ru.marchenko.NauJava.service.TaskService;

import java.util.ArrayList;
import java.util.List;

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
        List<String> partsList = getStrings(command);

        if (partsList.isEmpty()) {
            System.out.println("Введите команду.");
            return;
        }
        String action = partsList.getFirst();
        String[] parts = partsList.toArray(new String[0]);

        switch (action.toLowerCase()) {
            case "create", "c":
                if (parts.length != 3) {
                    System.out.println("Используйте: create (c) <id> <\"name\">");
                    return;
                }
                try {
                    Long createId = Long.parseLong(parts[1]);

                    if (taskService.findById(createId) != null) {
                        System.out.println("Задача с таким ID уже существует.");
                        return;
                    }
                    String createName = parts[2];

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

                    Task task = taskService.findById(readId);
                    if (task != null) {
                        System.out.println("Задача найдена: " + task.getName()
                                +" " +task.getTaskStatus());
                    } else {
                        System.out.println("Задача не найдена.");
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("ID должен быть числом.");
                    return;
                }

            case "update", "u":
                if (parts.length < 3 || parts.length > 4) {
                    System.out.println("Используйте: update (u) <task id> <\"new name\"> " +
                            "<task status (опционально)>");
                    return;
                }
                try {
                    Long taskId = Long.parseLong(parts[1]);

                    if (taskService.findById(taskId) == null) {
                        System.out.println("Задача с таким ID не найдена.");
                        return;
                    }

                    String updateName = parts[2];

                    TaskStatus taskStatus = taskService.findById(taskId).getTaskStatus();

                    if (parts.length == 4) {
                        String statusStr = parts[3].toUpperCase();
                        try {
                            taskStatus = TaskStatus.valueOf(statusStr);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Некорректный статус задачи. " +
                                    "Используйте: BACKLOG, IN_PROGRESS, DONE.");
                            return;
                        }
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
                    if (taskService.findById(deleteId) == null) {
                        System.out.println("Задача с таким ID не найдена.");
                        return;
                    }

                    taskService.deleteById(deleteId);
                    System.out.println("Задача удалена.");
                    break;
                } catch (NumberFormatException e) {
                    System.err.println("ID должен быть числом.");
                    return;
                }

            default:
                System.out.println("""
                        Введена неизвестная команда...
                        Доступные команды:
                        create (c) <id> <"name"> - создать задачу
                        read (r) <task id> - прочитать задачу по ID
                        update (u) <task id> <"new name"> <task status (опционально)>
                        - обновить имя (и при желании статус) задачи по ID
                        delete (d) <task id> - удалить задачу по ID
                       \s""");
                break;
        }
    }

    /**
     * Разбивает команду на части, учитывая кавычки для имен с пробелами.
     *
     * @param command Команда, введенная пользователем.
     * @return Список частей команды.
     */
    private static List<String> getStrings(String command) {
        List<String> partsList = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (!currentPart.isEmpty()) {
                    partsList.add(currentPart.toString());
                    currentPart.setLength(0);
                }
            } else {
                currentPart.append(c);
            }
        }

        if (!currentPart.isEmpty()) {
            partsList.add(currentPart.toString());
        }
        return partsList;
    }
}
