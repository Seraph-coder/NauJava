package ru.marchenko.NauJava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.marchenko.NauJava.console.CommandProcessor;

import java.util.Scanner;

/**
 * Конфигурационный класс для настройки консольного ввода-вывода.
 * Он создает бин CommandLineRunner, который запускается при старте приложения
 * и обрабатывает команды, введенные пользователем в консоль.
 */
@Configuration
public class ConsoleIOConfig {
    /**
     * Компонент для обработки команд, введенных пользователем.
     */
    @Autowired
    private CommandProcessor commandProcessor;

    /**
     * Создает и возвращает бин CommandLineRunner, который запускается при старте приложения.
     * Этот бин обрабатывает команды, введенные пользователем в консоль.
     *
     * @return Командный раннер для обработки консольных команд.
     */
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("""
                    Вы можете использовать команды: create (с), read (r), update (u), delete (d).
                    Для получения памятки по команде введите ее название.
                    Для выхода введите: exit.""");

            System.out.println("Консольный ввод-вывод запущен. Введите команду:");

            try (Scanner scanner = new java.util.Scanner(System.in)) {
                while (true) {
                    System.out.print("> ");

                    String command = scanner.nextLine();
                    if ("exit".equalsIgnoreCase(command.trim())) {
                        System.out.println("Завершение работы.");
                        break;
                    }
                    commandProcessor.processCommand(command);
                }
            }
        };
    }
}
