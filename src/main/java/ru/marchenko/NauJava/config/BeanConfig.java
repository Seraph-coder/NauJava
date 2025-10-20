package ru.marchenko.NauJava.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.marchenko.NauJava.entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
    * Конфигурационный класс для создания и настройки бина taskContainer.
 */
@Configuration
public class BeanConfig {

    /**
     * Название приложения, загружаемое из файла настроек (application.properties).
     */
    @Value("${app.name}")
    private String appName;

    /**
     * Версия приложения, загружаемая из файла настроек (application.properties).
     */
    @Value("${app.version}")
    private String appVersion;

    /**
     * Создает и возвращает бин taskContainer, представляющий собой список задач.
     * Этот бин имеет синглтон область видимости, что означает, что будет создан
     * только один экземпляр этого списка на все приложение.
     * @return Список задач (List<Task>).
     */
    @Bean
    public List<Task> taskContainer() {
        return new ArrayList<>();
    }

    /**
     * Инициализационный метод, который выводит информацию о приложении
     * после создания бина BeanConfig.
     */
    @PostConstruct
    public void init() {
        System.out.println("Application Name: " + appName);
        System.out.println("Application Version: " + appVersion);
    }
}
