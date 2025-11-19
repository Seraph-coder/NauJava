package ru.marchenko.NauJava.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium тест для страницы входа: неуспешный логин.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0")
public class LoginFailedTest {
    private static WebDriver driver;

    @LocalServerPort
    private int port;

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
     * Завершение работы WebDriver.
     */
    @AfterAll
    public static void tearDownClass() {
        if (driver != null) driver.quit();
    }

    /**
     * Тест неуспешного входа с неверными учетными данными.
     */
    @Test
    public void failedLogin() {
        String loginUrl = "http://localhost:" + port + "/login";
        driver.get(loginUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement usernameEl = wait.until(
                ExpectedConditions.presenceOfElementLocated
                        (By.name("username")));
        WebElement passwordEl = driver.findElement(
                By.name("password"));
        usernameEl.sendKeys("wronguser");
        passwordEl.sendKeys("wrongpass");
        WebElement submit = driver.findElement(
                By.cssSelector("button[type=submit]"));
        submit.click();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl())
                .contains("/login"));
        assertTrue(
                Objects.requireNonNull(driver.getPageSource())
                        .contains("Invalid credentials") ||
                        driver.getPageSource().contains("Bad credentials"));
    }
}
