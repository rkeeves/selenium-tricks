package io.github.rkeeves.network;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.google.common.base.Functions.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoNotLoadEyecandyTest {

    /**
     * What's the goal?
     * Demonstrate usage of Route abort.
     *
     * What does it do?
     * We visit a book store app.
     * It fetches a lot of data, but it also fetches images.
     * In this simple example we just abort the loading of the logo image.
     * It can be beneficial to not load eyecandy during functional tests.
     */
    @Test
    void test() {
        final var driver = (ChromeDriver) await.until(identity());
        final var interceptor = new NetworkInterceptor(
                driver,
                Route.matching(req -> HttpMethod.GET.equals(req.getMethod()) && req.getUri().matches(TOTALLY_LEGIT_REGEX))
                        .to(() -> req -> new HttpResponse()
                                .setStatus(404)));
        driver.navigate().to("https://demoqa.com/books");
        final var headerImg = driver.findElement(By.cssSelector("header img"));
        final var naturalWidth = driver.executeScript("return arguments[0].naturalWidth", headerImg);
        final var naturalHeight = driver.executeScript("return arguments[0].naturalHeight", headerImg);
        assertEquals(0L, naturalWidth);
        assertEquals(0L, naturalHeight);
    }

    static final String TOTALLY_LEGIT_REGEX = ".*Toolsqa\\.(png|jpg|jpeg)";

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
