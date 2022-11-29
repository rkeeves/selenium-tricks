package io.github.rkeeves.waitingfor;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WaitingForAngularTestabilityTest {

    /**
     * What's the goal?
     * Demonstrate the usage of Angular Testability.
     *
     * What does it do?
     * We visit a book search site.
     * Click on different options "By Title" and "By Topic".
     * We will count how many results were on the screen for each case.
     * Then we'll compare them.
     * Clicking around causes the app to "load".
     * It takes a long time to fetch data from the backend, and run all the 3rd party codes etc.
     * So the time it takes for the app to "settle down" can differ greatly across different test runs.
     * In this example we use Angular Testability checks to determine "readyness".
     */
    @RepeatedTest(2)
    void example() {
        final var driver = await.until(identity());

        driver.navigate().to("https://ieeexplore.ieee.org/browse/books/title");

        final var tab = driver.findElement(By.cssSelector(".stats-browse-pub-tab"));

        tab.findElement(By.xpath("//*[text()='By Title']")).click();
        await.until(WaitingForAngularTestabilityTest::angularTestabilitiesAreStable);
        final var countOne = driver.findElements(By.tagName("xpl-browse-results-item")).size();

        tab.findElement(By.xpath("//*[text()='By Topic']")).click();
        await.until(WaitingForAngularTestabilityTest::angularTestabilitiesAreStable);
        final var countTwo = driver.findElements(By.tagName("xpl-browse-results-item")).size();

        assertEquals(countOne, countTwo);
    }

    WebDriverWait await;

    private static Boolean angularTestabilitiesAreStable(WebDriver webDriver) {
        return (Boolean) ((JavascriptExecutor) webDriver).executeScript("return window.getAllAngularTestabilities().filter(x => !x.isStable()).length === 0");
    }

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        await = new WebDriverWait(new ChromeDriver(), Duration.ofSeconds(8L));
    }

    @AfterEach
    void afterEach() {
        if (await != null) await.until(identity()).quit();
    }

    @AfterAll
    static void afterAll() {

    }
}
