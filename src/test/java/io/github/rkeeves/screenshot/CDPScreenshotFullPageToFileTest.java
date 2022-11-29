package io.github.rkeeves.screenshot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v105.dom.model.Rect;
import org.openqa.selenium.devtools.v105.page.Page;
import org.openqa.selenium.devtools.v105.page.model.Viewport;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import static com.google.common.base.Functions.identity;

public class CDPScreenshotFullPageToFileTest {

    /**
     * What's the goal?
     * Demonstrate the usage of screenshot.
     *
     * What does it do?
     * We just simply take a screenshot of a page (full) and write it to a file.
     */
    @Test
    void example() throws IOException {
        final var driver = (ChromeDriver) await.until(identity());

        driver.navigate().to("https://github.com/");
        final var devTools = driver.getDevTools();
        devTools.createSessionIfThereIsNotOne();
        Page.GetLayoutMetricsResponse metrics = devTools.send(Page.getLayoutMetrics());
        Rect contentSize = metrics.getContentSize();
        final var pngButInBase64BecauseItHadToBeWrappedIntoJSON = devTools.send(Page.captureScreenshot(
                Optional.empty(), // defaults to png
                Optional.empty(),
                Optional.of(new Viewport(0, 0, contentSize.getWidth(), contentSize.getHeight(), 1)),
                Optional.empty(),
                Optional.of(true)
        ));

        final var pngDecodedInRawBytes = Base64.getDecoder().decode(pngButInBase64BecauseItHadToBeWrappedIntoJSON);
        Files.createDirectories(Paths.get("artifact"));
        Path destination = Paths.get("artifact","full_page_screenshot.png");
        Files.write(destination, pngDecodedInRawBytes);
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
