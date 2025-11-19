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
 * Selenium UI тест для сценария входа и выхода из приложения.
 * Тест запускает приложение в контексте Spring на случайном порту и использует headless Chrome.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0")
public class LoginSeleniumTest {
    private static WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Настройка WebDriver перед выполнением тестов.
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
     * Обеспечение наличия администратора в базе перед каждым тестом.
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
            u.getClass().getMethod(
                            "setEnabled", boolean.class)
                    .invoke(u, true);
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
     * Тест успешного входа и выхода из защищённой страницы.
     */
    @Test
    public void loginAndLogout_successful() {
        String protectedUrl = "http://localhost:" + port + "/userlist";
        driver.get(protectedUrl);
        WebDriverWait wait = new WebDriverWait(
                driver, Duration.ofSeconds(20));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.name("username")));
        } catch (Exception e) {
            System.out.println(
                    "Не найдена форма логина. URL: " + driver.getCurrentUrl());
            System.out.println("Страница: " + driver.getPageSource());
            throw e;
        }
        assertTrue
                (Objects.requireNonNull(driver.getCurrentUrl())
                                .contains("/login"),
                        "Ожидался редирект на /login, но был: " +
                                driver.getCurrentUrl());

        WebElement usernameEl = driver.findElement(By.name("username"));
        WebElement passwordEl = driver.findElement(By.name("password"));
        usernameEl.sendKeys("admin");
        passwordEl.sendKeys("admin");
        WebElement submit = driver.findElement(
                By.cssSelector("button[type=submit]"));
        submit.click();

        try {
            wait.until(ExpectedConditions.not(
                    ExpectedConditions.urlContains("/login")));
        } catch (Exception e) {
            System.out.println("Не удалось выйти с /login. URL: " +
                    driver.getCurrentUrl());
            System.out.println("Страница: " + driver.getPageSource());
            throw e;
        }

        driver.get(protectedUrl);
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.tagName("h1"), "Список пользователей"));
        } catch (Exception e) {
            System.out.println(
                    "Не найдена страница userlist. URL: " +
                            driver.getCurrentUrl());
            System.out.println("Страница: " + driver.getPageSource());
            throw e;
        }
        String usersPage = driver.getPageSource();
        assertTrue(
                (usersPage != null) &&
                        usersPage.contains("Список пользователей"),
                "Нет заголовка 'Список пользователей'");

        driver.manage().deleteAllCookies();
        driver.get(protectedUrl);
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.name("username")));
        } catch (Exception e) {
            System.out.println(
                    "После логаута не найдена форма логина. URL: " +
                            driver.getCurrentUrl());
            System.out.println("Страница: " + driver.getPageSource());
            throw e;
        }
        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "После логаута ожидался редирект на /login, но был: " +
                        driver.getCurrentUrl());
    }
}
