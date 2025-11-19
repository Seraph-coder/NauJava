package ru.marchenko.NauJava.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.marchenko.NauJava.entity.User;
import ru.marchenko.NauJava.repository.UserRepository;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium тест для создания отчёта.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0")
public class ReportCreateTest {
    private static WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Настройка WebDriver.
     */
    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    /**
     * Завершение работы WebDriver после всех тестов.
     */
    @AfterAll
    public static void tearDownClass() {
        if (driver != null) driver.quit();
    }

    /**
     * Создание пользователя admin перед каждым тестом.
     */
    @BeforeEach
    public void ensureAdminUser() {
        userRepository.deleteAll();
        User u = new User();
        u.setUsername("admin");
        u.setEmail("admin@example.com");
        u.setPassword(passwordEncoder.encode("admin"));
        u.setRole("ADMIN");
        try {
            u.getClass().getMethod("setEnabled", boolean.class).
                    invoke(u, true);
        } catch (Exception ignored) {
        }
        try {
            u.getClass().getMethod(
                            "setAccountNonLocked", boolean.class)
                    .invoke(u, true);
        } catch (Exception ignored) {
        }
        try {
            u.getClass().getMethod(
                            "setAccountNonExpired", boolean.class)
                    .invoke(u, true);
        } catch (Exception ignored) {
        }
        try {
            u.getClass().getMethod(
                            "setCredentialsNonExpired", boolean.class)
                    .invoke(u, true);
        } catch (Exception ignored) {
        }
        userRepository.save(u);
    }

    /**
     * Тест создания отчёта.
     */
    @Test
    public void createReport() {
        String loginUrl = "http://localhost:" + port + "/login";
        String createUrl = "http://localhost:" + port + "/reports/create";
        driver.get(loginUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement usernameEl = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.name("username")));
        WebElement passwordEl = driver.findElement(By.name("password"));
        usernameEl.sendKeys("admin");
        passwordEl.sendKeys("admin");
        WebElement submit = driver.findElement(
                By.cssSelector("button[type=submit]"));
        submit.click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login")));

        driver.get(createUrl);
        wait.until(ExpectedConditions.urlContains("/reports/"));
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).matches(
                "http://localhost:" + port + "/reports/\\d+"));
    }
}
