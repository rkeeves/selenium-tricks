package io.github.rkeeves.screenshot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static com.google.common.base.Functions.identity;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ScreenshotSingleElementToFileTest {


    /**
     * What's the goal?
     * Demonstrate the usage of screenshot for elements.
     *
     * What does it do?
     * We just simply take a screenshot of an element and write it to a file.
     */
    @Test
    void example() throws IOException {
        final var driver = await.until(identity());
        driver.navigate().to("https://www.saucedemo.com/");
        driver.findElement(By.cssSelector("*[data-test='login-button']")).click();
        final var screenShotTempFile = driver.findElement(By.cssSelector(".error-message-container")).getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get("artifact","element_screenshot.png");
        Files.createDirectories(Paths.get("artifact"));
        Files.move(screenShotTempFile.toPath(), destination, REPLACE_EXISTING);
    }

    WebDriverWait await;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        await = new WebDriverWait(new ChromeDriver(), Duration.ofSeconds(4L));
    }

    @AfterEach
    void afterEach() {
        if (await != null) await.until(identity()).quit();
    }

    @AfterAll
    static void afterAll() {

    }
}
